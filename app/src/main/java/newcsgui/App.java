package newcsgui;

import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

import javax.sound.sampled.*;

public class App {
    private JFrame frame;
    private JPanel footerPanel, headerPanel;
    private JLabel questionLabel, answerLabel;
    private JDialog loginDialog;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, createAccountButton;
    private JButton startRecordingButton, stopRecordingButton, updateHistory;
    private TargetDataLine targetDataLine;
    private AudioFormat audioFormat = getAudioFormat();

    private static final String CRED_FILE = "cred.txt";

    public App() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {

        frame = new JFrame("App GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 900);

        questionLabel = new JLabel("Question:");
        frame.getContentPane().add(questionLabel, BorderLayout.WEST);

        answerLabel = new JLabel("Answer:");
        frame.getContentPane().add(answerLabel, BorderLayout.EAST);

        footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.X_AXIS));
        loadHistoryFromFile();

        JScrollPane footerScrollPane = new JScrollPane(footerPanel);
        footerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.getContentPane().add(footerScrollPane, BorderLayout.SOUTH);

        startRecordingButton = new JButton("Start Recording");
        stopRecordingButton = new JButton("Stop Recording");
        updateHistory = new JButton("Update History");

        headerPanel = new JPanel();
        headerPanel.add(startRecordingButton);
        headerPanel.add(stopRecordingButton);
        headerPanel.add(updateHistory);

        frame.getContentPane().add(headerPanel, BorderLayout.NORTH);

        updateHistory.addActionListener(e -> {
            updateHistoryFile();
            loadHistoryFromFile();
        });

        startRecordingButton.addActionListener(e -> {
            startRecording();
        });

        stopRecordingButton.addActionListener(e -> {
            stopRecording();

            try {
                // Read file into byte array
                File file = new File("recording.wav");
                byte[] fileContent = Files.readAllBytes(file.toPath());

                // Convert byte array to Base64
                String encodedString = Base64.getEncoder().encodeToString(fileContent);

                // Create URL object for POST request
                URL url = new URL("http://127.0.0.1:3000/preformLogic");

                // Open connection to URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Create JSON payload
                String payload = String.format(
                        "{\"username\":\"%s\", \"password\":\"%s\", \"audioData\":\"%s\", \"selected\":\"null\"}",
                        usernameField.getText(),
                        new String(passwordField.getPassword()),
                        encodedString);

                // Write payload to request body
                try (OutputStream outputStream = connection.getOutputStream()) {
                    byte[] payloadBytes = payload.getBytes("UTF-8");
                    outputStream.write(payloadBytes, 0, payloadBytes.length);
                }

                // Get response code
                int responseCode = connection.getResponseCode();

                // Handle the response based on the code
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();

                    // Loop through each line of input.
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    // Close connections.
                    in.close();
                    connection.disconnect();

                    String[] responseArr = content.toString().split(":");
                    String command = responseArr[0];
                    command = command.trim().toLowerCase();

                    // udpate file
                    // updateHistoryFile();
                    // dispaly new file

                    if (command.equals("question")) {

                        String[] questionArr = responseArr[1].split("%\\$%");

                        String answer = questionArr[1];
                        String question = questionArr[0];

                        answer = insertNewlines(answer, 40);
                        question = insertNewlines(question, 40);

                        questionLabel.setText("Question: " + question);
                        answerLabel.setText("Answer: " + answer);
                    }

                    else if (command.equals("")) {

                    }

                    else {

                    }

                }

                else {
                    JOptionPane.showMessageDialog(frame, "Error getting response from server code:" + responseCode,
                            "Logic Error", JOptionPane.ERROR_MESSAGE);
                }

                // Close the connection
                connection.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        createLoginDialog();

    }

    public static String insertNewlines(String original, int splitLength) {
        StringBuilder sb = new StringBuilder(original);

        int i = 0;
        while ((i = sb.indexOf(" ", i + splitLength)) != -1) {
            sb.replace(i, i + 1, "\n");
        }

        return sb.toString();
    }

    private void loadHistoryFromFile() {

        try {
            File file = new File("history.txt");
            if (file.exists() && file.length() > 0) {
                String content = new String(Files.readAllBytes(file.toPath()));
                JSONObject json = new JSONObject(content);
                JSONArray historyArray = json.getJSONArray("history");

                footerPanel.removeAll();
    
                for (int i = 0; i < historyArray.length(); i++) {
                    JSONObject historyObj = historyArray.getJSONObject(i);
                    String dataString = historyObj.getString("data");
                    JSONObject dataObj = new JSONObject(dataString);
    
                    String question = dataObj.optString("question", "");
                    String response = dataObj.optString("response", "");
    
                    JButton button = new JButton("<html><div style='text-align: center; width: 100px; height: 50px;'>"
                            + question + "</div></html>");
                    button.addActionListener(e -> {
                        questionLabel.setText("Question: " + question);
                        answerLabel.setText("Answer: " + response);
                    });
    
                    button.setPreferredSize(new Dimension(300, 200)); // Set button size
    
                    footerPanel.add(button);
                    footerPanel.add(Box.createRigidArea(new Dimension(10, 0)));
                }

                footerPanel.revalidate();
                footerPanel.repaint();
            } else {
                System.out.println("History file not found or empty.");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    

    private String[] getCredsFromFile() throws FileNotFoundException, IOException {
        String[] creds = new String[3];
        try(BufferedReader br = new BufferedReader(new FileReader("cred.txt"))) {
            String line = br.readLine();
            if (line != null) {
                creds = line.split(",");
            }
        }
        return creds;
    }

    
    private void updateHistoryFile() {

        String[] creds;
        try {
            creds = getCredsFromFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error getting credentials from file.", "Credential Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = creds[1];
        String password = creds[2];
        

        try {
            // Create the URL object for the POST request
            URL url = new URL("http://127.0.0.1:3000/getHistory");

            // Create the JSON payload
            String payload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Write the payload to the request body
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] payloadBytes = payload.getBytes("UTF-8");
                outputStream.write(payloadBytes, 0, payloadBytes.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Handle the response based on the code
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;

                // Read the response message
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();

                // Dump the response message into history.txt
                FileWriter fileWriter = new FileWriter("history.txt", false);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                bufferedWriter.write(content.toString());
                bufferedWriter.newLine();
                bufferedWriter.close();

            } else {
                JOptionPane.showMessageDialog(frame, "Error getting history. Response code: " + responseCode,
                        "History Error", JOptionPane.ERROR_MESSAGE);
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error getting history. Server is unreachable.", "History Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private AudioFormat getAudioFormat() {
        // the number of samples of audio per second.
        // 44100 represents the typical sample rate for CD-quality audio.
        float sampleRate = 44100;

        // the number of bits in each sample of a sound that has been digitized.
        int sampleSizeInBits = 16;

        // the number of audio channels in this format (1 for mono, 2 for stereo).
        int channels = 1;

        // whether the data is signed or unsigned.
        boolean signed = true;

        // whether the audio data is stored in big-endian or little-endian order.
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    private void startRecording() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
                    targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                    targetDataLine.open(audioFormat);
                    targetDataLine.start();

                    AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);
                    File audioFile = new File("recording.wav");
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void stopRecording() {
        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
        }
    }

    private void createLoginDialog() {
        loginDialog = new JDialog(frame, "Login", true);
        loginDialog.setLayout(new GridLayout(4, 2));

        loginDialog.add(new JLabel("Username:"));
        usernameField = new JTextField();
        loginDialog.add(usernameField);

        loginDialog.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        loginDialog.add(passwordField);

        JCheckBox rememberMe = new JCheckBox("Remember Me?");
        loginDialog.add(rememberMe);


        loginButton = new JButton("Login");
        loginDialog.add(loginButton);

        File credsFile = new File(CRED_FILE);
        if (credsFile.exists() && credsFile.length() != 0) {
            try (BufferedReader reader = new BufferedReader(new FileReader(credsFile))) {
                String firstLine = reader.readLine();
                if (firstLine != null && firstLine.startsWith("1")) {
                    String[] parts = firstLine.split(",");
                    if (parts.length > 2) {
                        String username = parts[1];
                        String password = parts[2];

                        URL url = new URL("http://127.0.0.1:3000/login");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);

                        JSONObject json = new JSONObject();
                        json.put("username", username);
                        json.put("password", password);

                        String input = json.toString();

                        OutputStream os = conn.getOutputStream();
                        os.write(input.getBytes());
                        os.flush();

                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                            // updateHistoryFile();

                            showMainGUI();
                            loginDialog.dispose();
                            loginDialog.setVisible(false);
                            return;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            boolean rememberMeSelected = rememberMe.isSelected();

            if (rememberMeSelected) {
                // Write credentials to file
                try (FileWriter fileWriter = new FileWriter(CRED_FILE)) {
                    String credentials = "1," + usernameField.getText() + "," + new String(passwordField.getPassword());
                    fileWriter.write(credentials);

                    // updateHistoryFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }


            try {
                // Create the URL object for the POST request
                URL url = new URL("http://127.0.0.1:3000/login");

                // Create the JSON payload
                String payload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

                // Open a connection to the URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Write the payload to the request body
                try (OutputStream outputStream = connection.getOutputStream()) {
                    byte[] payloadBytes = payload.getBytes("UTF-8");
                    outputStream.write(payloadBytes, 0, payloadBytes.length);
                }

                // Get the response code
                int responseCode = connection.getResponseCode();

                // Handle the response based on the code
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    loginDialog.dispose();
                    showMainGUI(); // Show the main application GUI
                } else {
                    JOptionPane.showMessageDialog(frame, "Error logging in. Response code: " + responseCode,
                            "Login Error", JOptionPane.ERROR_MESSAGE);
                }

                // Close the connection
                connection.disconnect();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error logging in. Server is unreachable.", "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        createAccountButton = new JButton("Create Account");
        // Add an action listener to the create account button
        createAccountButton.addActionListener(e -> {
            createAccountDialog();
        });
        loginDialog.add(createAccountButton);

        loginDialog.pack();
        loginDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        loginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        loginDialog.setVisible(true);
    }

    private void showMainGUI() {
        frame.setVisible(true);
    }

    private void createAccountDialog() {

        loginDialog.setVisible(false);
        JDialog accountDialog = new JDialog(frame, "Create Account", true);
        accountDialog.setLayout(new GridLayout(9, 2));

        accountDialog.add(new JLabel("Username:"));
        JTextField accountUsernameField = new JTextField();
        accountDialog.add(accountUsernameField);

        accountDialog.add(new JLabel("Password:"));
        JPasswordField accountPasswordField = new JPasswordField();
        accountDialog.add(accountPasswordField);

        accountDialog.add(new JLabel("Email Address:"));
        JTextField emailField = new JTextField();
        accountDialog.add(emailField);

        accountDialog.add(new JLabel("Email Password:"));
        JPasswordField emailPasswordField = new JPasswordField();
        accountDialog.add(emailPasswordField);

        accountDialog.add(new JLabel("SMTP:"));
        JTextField smtpField = new JTextField();
        accountDialog.add(smtpField);

        accountDialog.add(new JLabel("TLS:"));
        JTextField tlsField = new JTextField();
        accountDialog.add(tlsField);

        accountDialog.add(new JLabel("First Name:"));
        JTextField firstNameField = new JTextField();
        accountDialog.add(firstNameField);

        accountDialog.add(new JLabel("Last Name:"));
        JTextField lastNameField = new JTextField();
        accountDialog.add(lastNameField);

        JButton createAccountButton = new JButton("Create Account");
        accountDialog.add(createAccountButton);

        JCheckBox rememberMe = new JCheckBox("Remember Me?");
        accountDialog.add(rememberMe);

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the input values from the text fields
                String username = accountUsernameField.getText();
                String password = new String(accountPasswordField.getPassword());
                String emailAddress = emailField.getText();
                String emailPassword = new String(emailPasswordField.getPassword());
                String smtp = smtpField.getText();
                String tls = tlsField.getText();
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();

                boolean rememberMeSelected = rememberMe.isSelected();

                if (rememberMeSelected) {
                    // Write credentials to file
                    try (FileWriter fileWriter = new FileWriter(CRED_FILE)) {
                        String credentials = "1," + usernameField.getText() + ","
                                + new String(passwordField.getPassword());
                        fileWriter.write(credentials);
                        // updateHistoryFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                // Create the JSON payload
                String payload = String.format("{"
                        + "\"username\":\"%s\","
                        + "\"password\":\"%s\","
                        + "\"emailAddress\":\"%s\","
                        + "\"emailPassword\":\"%s\","
                        + "\"smtp\":\"%s\","
                        + "\"tls\":\"%s\","
                        + "\"firstName\":\"%s\","
                        + "\"lastName\":\"%s\""
                        + "}", username, password, emailAddress, emailPassword, smtp, tls, firstName, lastName);

                try {
                    // Create the URL object for the POST request
                    URL url = new URL("http://127.0.0.1:3000/createAccount");

                    // Open a connection to the URL
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");

                    // Write the payload to the request body
                    try (OutputStream outputStream = connection.getOutputStream()) {
                        byte[] payloadBytes = payload.getBytes("UTF-8");
                        outputStream.write(payloadBytes, 0, payloadBytes.length);
                    }

                    // Get the response code
                    int responseCode = connection.getResponseCode();

                    // Handle the response based on the code
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        accountDialog.dispose();
                        showMainGUI(); // Show the main application GUI
                    } else {
                        JOptionPane.showMessageDialog(frame, "Error creating account. Response code: " + responseCode,
                                "Account Creation Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Close the connection
                    connection.disconnect();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error creating account server is unreachable.",
                            "Account Creation Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        accountDialog.pack();
        accountDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        accountDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        accountDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
