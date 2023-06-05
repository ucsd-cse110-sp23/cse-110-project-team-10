const express = require('express');
const { MongoClient, ServerApiVersion } = require('mongodb');
const { Configuration, OpenAIApi } = require('openai')
const bcrypt = require('bcrypt');
const crypto = require('crypto');
const fs = require('fs');
const { get } = require('https');
const { resolve } = require('path');

//api key for openAI
const apiKey = "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"
const configuration = new Configuration({
    apiKey: apiKey
});
const openai = new OpenAIApi(configuration);

//et up connection options for DB
const uri = "mongodb+srv://sgherzivincent:zemdi5-xixNax-wumvyp@cluster0.mrtvmgg.mongodb.net/?retryWrites=true&w=majority";

const client = new MongoClient(uri, {
    serverApi: {
        version: ServerApiVersion.v1,
        strict: true,
        deprecationErrors: true,
    }
});

//set up server endpoints
const app = express();
app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ limit: '50mb', extended: true }));


app.use((error, req, res, next) => {
    console.error(error);
    res.status(500).send('express json parsing failed');
});

app.post('/createAccount', async (req, res) => {


    if (!req.body) {
        res.status(400).send("Bad Request No Body")
        return
    }

    //break down user and pass
    let username
    let password
    let json

    try {
        json = JSON.parse(JSON.stringify(req.body))
    } catch (error) {
        console.log("error parsing json" + error)
        res.status(400).send("Bad Request cannot parse body")
        return
    }

    try {
        username = json.username

        if (username == undefined) {
            res.status(400).send("Bad Request No Username")
            return
        }
    } catch (error) {
        console.log("error extracting username")
        res.status(400).send("Bad Request No Username")
        return
    }

    try {
        password = json.password

        if (password == undefined) {
            res.status(400).send("Bad Request No password")
            return
        }
    } catch (error) {
        console.log("error extracting password")
        res.status(400).send("Bad Request No Password")
        return
    }


    try {

        const saltRounds = 10;
        const hashedPassword = await bcrypt.hash(password, saltRounds);

        // Generate a random token
        const token = crypto.randomBytes(32).toString('hex');
        const tokenExpiration = new Date();
        tokenExpiration.setDate(tokenExpiration.getDate() + 1);


        await client.connect();
        const db = client.db('Cluster0');
        const collection = db.collection('testUsers');


        const user = {
            username: username,
            password: hashedPassword,
            token: token,
            tokenExpiration: tokenExpiration,
            history: []
        };

        const result = await collection.insertOne(user);

        console.log('User inserted with _id:', result.insertedId);

        res.status(200).send(token);
    }

    catch (error) {
        console.error('Error inserting user:', error);
        res.status(500).send('Internal Server Error');
    }

    finally {
        await client.close();
    }
});



app.post('/login', async (req, res) => {
    if (!req.body) {
        res.status(400).send("Bad Request: No Body");
        return;
    }

    let username;
    let password;
    let json;

    try {
        json = JSON.parse(JSON.stringify(req.body));
    } catch (error) {
        console.log("Error parsing JSON: " + error);
        res.status(400).send("Bad Request: Cannot parse body");
        return;
    }

    try {
        username = json.username;

        if (username === undefined) {
            res.status(400).send("Bad Request: No Username");
            return;
        }
    } catch (error) {
        console.log("Error extracting username");
        res.status(400).send("Bad Request: No Username");
        return;
    }

    try {
        password = json.password;

        if (password === undefined) {
            res.status(400).send("Bad Request: No Password");
            return;
        }
    } catch (error) {
        console.log("Error extracting password");
        res.status(400).send("Bad Request: No Password");
        return;
    }

    try {
        await client.connect();
        const db = client.db('Cluster0');
        const collection = db.collection('testUsers');

        const user = await collection.findOne({ username: username });
        if (!user) {
            res.status(401).send("Unauthorized: User not found");
            return;
        }

        const passwordMatch = await bcrypt.compare(password, user.password);
        if (!passwordMatch) {
            res.status(401).send("Unauthorized: Invalid password");
            return;
        }

        // Generate a new token
        const token = crypto.randomBytes(32).toString('hex');
        const tokenExpiration = new Date();
        tokenExpiration.setDate(tokenExpiration.getDate() + 1);

        const updateResult = await collection.updateOne(
            { username: username },
            {
                $set: {
                    token: token,
                    tokenExpiration: tokenExpiration
                }
            }
        );

        if (updateResult.modifiedCount === 0) {
            res.status(500).send("Internal Server Error: Token update failed");
            return;
        }

        res.status(200).send(token);
    } catch (error) {
        console.error("Error during login:", error);
        res.status(500).send("Internal Server Error");
    } finally {
        await client.close();
    }
});




app.post('/transcribe', async (req, res) => {

    if (!req.body) {
        res.status(400).send("Bad Request No Body")
        return
    }

    // Parse request body
    const { token, audio } = req.body;

    if (!token) {
        res.status(400).send("Bad Request No Token")
        return
    }

    if (!audio) {
        res.status(400).send("Bad Request No Audio")
        return
    }

    try {
        await client.connect();
        const db = client.db('Cluster0');
        const collection = db.collection('testUsers');

        const user = await collection.findOne({ token: token });
        if (!user) {
            res.status(401).send("Unauthorized: Invalid token");
            return;
        }

        // Decode the Base64 audio data to a buffer
        const buffer = Buffer.from(audio, 'base64');

        // Write the buffer to a .wav file
        const filePath = "audio.wav";
        fs.writeFileSync(filePath, buffer);

        // Transcribe the audio file
        const transcript = await openai.createTranscription(
            fs.createReadStream(filePath),
            "whisper-1"
        );


        let response = transcript.data.text
        console.log(response)

        const categorize = await openai.createCompletion({
            model: "text-davinci-003",
            prompt: "From the following text does it seem like the user wants to send email, delete this occurence, delete all of something, or ask a general question:" + response + " respond with either 'send email' 'delete this' 'delete all' or 'question'",
            max_tokens: 16,
            temperature: 0,
        });

        let option = categorize.data.choices[0].text.toLowerCase().trim()

        if (option == "send email") {

            const getMessage = await openai.createCompletion({
                model: "text-davinci-003",
                prompt: "what is the message they want to send in the email, say only that message exactly and nothing else: " + response,
                max_tokens: 16,
                temperature: 0.4,
            });

            let message = getMessage.data.choices[0].text.toLocaleLowerCase().trim();

            const getEmail = await openai.createCompletion({
                model: "text-davinci-003",
                prompt: "what is the email in:" + response,
                max_tokens: 16,
                temperature: 0.4,
            });

            let email = getEmail.data.choices[0].text.toLocaleLowerCase().trim();

            let result = {
                command: "email",
                question: response,
                answer: "emailing " + message + " to " + email + " from " + user.username
            }


            res.status(200).send(JSON.stringify(result))
            return
        }

        else if (option == "delete this") {

        }

        else if (option == "delete all") {
            
            user.history = []
            const updateResult = await collection.updateOne(
                { username: user.username },
                {
                    $set: {
                        history: user.history
                    }
                }
            );

            if (updateResult.modifiedCount === 0) {
                res.status(500).send("Internal Server Error: User update failed");
                return;
            }


            let result = {
                command: "Delete All",
                question: "",
                answer: "",
            }
            //update the user on the DB

            res.status(200).send(JSON.stringify(result))
            return

        }

        else if (option == "question") {

            const getQuestion = await openai.createCompletion({
                model: "text-davinci-003",
                prompt: response,
                max_tokens: 16,
                temperature: 0,
            });

            let answer = getQuestion.data.choices[0].text.toLocaleLowerCase().trim();

            let result = {
                id: null,
                command: "question",
                question: response,
                answer: answer,
            }

            let newID = user.history.length;
            result.id = newID;

            // Update user history
            user.history.push(result);

            // Update the user in the database
            const updateResult = await collection.updateOne(
                { username: user.username },
                {
                    $set: {
                        history: user.history
                    }
                }
            );

            if (updateResult.modifiedCount === 0) {
                res.status(500).send("Internal Server Error: User update failed");
                return;
            }


            //update the user on the DB

            res.status(200).send(JSON.stringify(result))
            return
        }

        else {

            let result = {
                command: "ERROR",
                question: "",
                answer: "",
            }

            res.status(400).send(JSON.stringify(result))
        }

        res.status(200).send(response)
        return
    }

    catch (error) {
        console.error("Error during transcription:", error);
        res.status(500).send("Internal Server Error during transcription");
        return
    }

    finally {
        await client.close();
    }
})


app.listen(3000, () => {
    console.log("listening on port 3000")
})