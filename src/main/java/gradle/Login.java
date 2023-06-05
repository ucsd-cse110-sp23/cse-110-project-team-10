package gradle;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
//import org.bson.types.ObjectId;
import com.mongodb.client.FindIterable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JOptionPane;


public class Login {
    
    String uri = "mongodb+srv://haz042:Dan13697748680@lab7.nxlm4ex.mongodb.net/?retryWrites=true&w=majority";
    boolean ex;

    public Login(String email, String password) {

        try (MongoClient mongoClient = MongoClients.create(uri)) {


            MongoDatabase ProjectDB = mongoClient.getDatabase("Project");
            MongoCollection<Document> userCollection = ProjectDB.getCollection("Email");

            // Document user = new Document("_id", new ObjectId());
            Document user = new Document(email, password);

            FindIterable<Document> results = userCollection.find(user);
            boolean exists = results.iterator().hasNext();
            this.ex = exists;
            if (exists) {
                File file = new File("credentials.txt");
        
                try {
                    Scanner scanner = new Scanner(file);
                    int count = 0;
            
                    // Count the number of strings
                    while (scanner.hasNext()) {
                        scanner.next();
                        count++;
                    }
                    scanner.close();
                    if (count == 1) {
                        file.delete();
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
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                boolean automaticLogin = showAutomaticLoginDialog();
                String content;
                if (automaticLogin) {

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line = reader.readLine();
            
                        if (line != null) {
                            String[] values = line.split(" ");
            
                            // Store each value in separate variables
                            String autologin = values[0];
                            String preEmail = values[1];
                            String prePassword = values[2];
                            String firstName = values[3];
                            String lastName = values[4];
                            String displayName = values[5];
                            String emailAddress = values[6];
                            String emailPassword = values[7];
                            String SMTPHost = values[8];
                            String TLSPort = values[9];

                            file.delete();
                            String fileName = "credentials.txt";
                            if (email != preEmail) {
                                content = "1 " + email + " " + password + " " + "Fill_in_first_name " + "Fill_in_last_name " + "Fill_in_display_name " + "Fill_in_email_address " + "Fill_in_email_password " + "Fill_in_SMTP_host " + "Fill_in_TLS_port";
                            }
                            else {
                                content = "1 " + preEmail + " " + prePassword + " " + firstName + " " + lastName + " " + displayName + " " + emailAddress + " " + emailPassword + " " + SMTPHost + " " + TLSPort;
                            }

                            try {
                                FileWriter fileWriter = new FileWriter(fileName);
                                fileWriter.write(content);
                                fileWriter.close();
                                System.out.println("File created and content written successfully.");
                            } catch (IOException e) {
                                System.out.println("An error occurred while writing to the file.");
                                e.printStackTrace();
                            }
            
                        } else {
                            System.out.println("File is empty.");
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading the file: " + e.getMessage());
                    }

                    JOptionPane.showMessageDialog(null, "Logged in successfully! Automatic login enabled.");
                } else {

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line = reader.readLine();
            
                        if (line != null) {
                            String[] values = line.split(" ");
            
                            // Store each value in separate variables
                            String autologin = values[0];
                            String preEmail = values[1];
                            String prePassword = values[2];
                            String firstName = values[3];
                            String lastName = values[4];
                            String displayName = values[5];
                            String emailAddress = values[6];
                            String emailPassword = values[7];
                            String SMTPHost = values[8];
                            String TLSPort = values[9];

                            file.delete();
                            String fileName = "credentials.txt";
                            if (email != preEmail) {
                                content = "0 " + email + " " + password + " " + "Fill_in_first_name " + "Fill_in_last_name " + "Fill_in_display_name " + "Fill_in_email_address " + "Fill_in_email_password " + "Fill_in_SMTP_host " + "Fill_in_TLS_port";
                            }
                            else {
                                content = "0 " + preEmail + " " + prePassword + " " + firstName + " " + lastName + " " + displayName + " " + emailAddress + " " + emailPassword + " " + SMTPHost + " " + TLSPort;
                            }

                            try {
                                FileWriter fileWriter = new FileWriter(fileName);
                                fileWriter.write(content);
                                fileWriter.close();
                                System.out.println("File created and content written successfully.");
                            } catch (IOException e) {
                                System.out.println("An error occurred while writing to the file.");
                                e.printStackTrace();
                            }
            
                        } else {
                            System.out.println("File is empty.");
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading the file: " + e.getMessage());
                    }

                    JOptionPane.showMessageDialog(null, "Logged in successfully! Automatic login disabled.");
                }
                try {
                    new AppFrame();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "Invalid Email or Password");
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean showAutomaticLoginDialog() {
        int choice = JOptionPane.showConfirmDialog(null, "Do you want to enable automatic login?", "Automatic Login", JOptionPane.YES_NO_OPTION);
        return choice == JOptionPane.YES_OPTION;
	}

}
