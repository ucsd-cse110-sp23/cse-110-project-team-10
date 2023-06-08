package gradle;

import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;
import java.io.FileWriter;
import java.io.IOException;

public class Create {
    
    boolean ex;

    public Create(String email, String password) {

        // generate setting file
        String fileName = "credentials.txt";
        String content = "0 " + email + " " + password + " " + "Fill_in_first_name " + "Fill_in_last_name " + "Fill_in_display_name " + "Fill_in_email_address " + "Fill_in_email_password " + "Fill_in_SMTP_host " + "Fill_in_TLS_port";

        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(content);
            fileWriter.close();
            System.out.println("File created and content written successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
       
        //send post request here
        try {
            URL url = new URL("http://localhost:3000/createAccount");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonInputString = "{\"username\": \"" + email + "\", \"password\": \"" + password + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int statusCode = connection.getResponseCode();
     
            boolean exists = false;

            if(statusCode == 401){
                exists = true;
            }
            ex= this.ex;
        
            if (exists) {
                JOptionPane.showMessageDialog(null, "Account already exists");
            }
            else {
                try {
                    JOptionPane.showMessageDialog(null, "Account created successfully!");
                    new AppFrame();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } 

        } catch (Exception e) {
            e.printStackTrace();

        }
        
     
    }
}
