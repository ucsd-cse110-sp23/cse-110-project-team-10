/**
 * 
 * FILENAME: RequestHandle.java
 * 
 * 
 * Request Handle takes in and handles endpoints 
 * for JavaServer. Tasks beyond routing and
 * error handling for the endpoints are sent
 * off and delegated to other java files.
 * 
 * -Vincent Sgherzi
 * 
 * TODO:
 * email endpoint
 * login endpoint
 * create account endpoint
 * CORS
 * async chat gpt?
 * magic number cleanup
 */

package server.src;

//imports
import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;
import org.json.JSONArray;


public class RequestHandle implements HttpHandler {

    private String endpoint;

    //enpoint capture for logging
    public RequestHandle(String endpoint) {
        this.endpoint = endpoint;
    }


    /**
     * This function ensure the handler
     * only accepts POST requests
     * anything else is rejected with 403
     * 
     * @param httpExchange - http object with information about client server
     *                       exchanges
     */
    public void handle(HttpExchange httpExchange) throws IOException {

        String response = "";
        String method; 

        try {

            method = httpExchange.getRequestMethod();

            //check if method sent is POST
            if (method.equals("POST")) {
                response = handlePost(httpExchange);
            }

            //reject non POST
            else {
                handleReturn(httpExchange, 403, "Method Not Accepted");
            }

        }

        //error catch if peeking into method type fails
        catch (Exception e) {
            handleReturn(httpExchange, 500, "Internal Server Error Processing HTTP method");
            writeError(e, "handle", endpoint);
        }

        handleReturn(httpExchange, 200, response);
    }


    /**
     * This function handles all post requests
     * and breaks down the body into a JSON
     * object. The respective parameters 
     * are then checked if a matching enpoint
     * is located. Work is then delegated to
     * other java classes for logic
     * 
     * @param httpExchange - http object with information about client server
     *                       exchanges
     * 
     * @return - returns a response string of data if valid request
     */
    private String handlePost(HttpExchange httpExchange) throws IOException {

        InputStream inStream = httpExchange.getRequestBody();

        //parse body
        Scanner scanner = new Scanner(inStream);
        String postData = scanner.nextLine();
        String response = "";


        if (endpoint.equals("/transcribeAudio")) {

            JSONObject jsonObject = new JSONObject();


            //try to extract params
            try {

                jsonObject = new JSONObject(postData);

            } catch (Exception e) {
                scanner.close();
                handleReturn(httpExchange, 400, "Bad Body");
                writeError(e, "handlePost", endpoint);
            }

            //try to transcribe base64 audio
            try {

                String audioDataString = jsonObject.getString("audio");

                byte[] audioBytes = Base64.getDecoder().decode(audioDataString);
                Whisper transcriber = new Whisper();

                response = transcriber.transcribeBytes(audioBytes);

            } catch (Exception e) {
                scanner.close();
                handleReturn(httpExchange, 500, "Internal Server Error Referencing ChatGPT");
                writeError(e, "handlePost", endpoint);
            }
        }


        if (endpoint.equals("/getAnswer")) {

            JSONObject question = new JSONObject();


            //try to extract params
            try {

                question = new JSONObject(postData);

            } catch (Exception e) {
                scanner.close();
                handleReturn(httpExchange, 400, "Bad body");
                writeError(e, "handlePost", endpoint);
            }

            //ask chatGPT for a response
            try {

                // consider async for timeout
                ChatGPT chatgpt = new ChatGPT();
                response = chatgpt.getAnswer(question.getString("question"));

            } catch (Exception e) {
                scanner.close();
                handleReturn(httpExchange, 500, "Internal Server Error Referencing ChatGPT");
                writeError(e, "handlePost", endpoint);
            }

        }

        if (endpoint.equals("/sendEmail")) {
            // email someone likely will be a a self call
        }

        if (endpoint.equals("login")) {
        }

        if (endpoint.equals("createAccount")) {
        }

        //returning relevant data from endpoint
        scanner.close();
        return response;
    }


    /**
     * this function handles the return code and message.
     * this allows for errors and 200s to be sent and logged.
     * 
     * @param httpExchange - http object with information about client server
     *                       exchanges
     * @param code - http status code
     * @param msg - relevant data payload
     */
    public void handleReturn(HttpExchange httpExchange, int code, String msg) {

        try {

            //send out response
            httpExchange.sendResponseHeaders(code, msg.length());
            OutputStream outStream = httpExchange.getResponseBody();

            //pipe stream
            outStream.write(msg.getBytes());
            outStream.close();

        } 
        
        //logging if server chokes on sending
        catch (Exception e) {
            writeError(e, "handleReturn", endpoint);
        }
    }


    /**
     * this function writes errors to a logging file with
     * the timestamp location and error message
     * 
     * @param e - string of error 
     * @param errorFunction - function where error occured
     * @param endpoint - endpoint that caused error
     */
    public void writeError(Exception e, String errorFunction, String endpoint) {

        try {

            //get date and remove newlines
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String formattederrorFunction = errorFunction.replaceAll("[\r\n]", "");

            //format entry
            String error = timestamp + ":" + formattederrorFunction + ":" + endpoint + ":" + e.toString() + "\n";

            //write log
            Files.write(new File("errorLog.txt").toPath(), error.getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
