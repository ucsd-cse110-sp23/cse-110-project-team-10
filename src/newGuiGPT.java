import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.sound.sampled.*;
import javax.swing.border.*;
import java.io.FileWriter;
import javax.imageio.ImageIO;

//Header class that displays the title
class Header extends JPanel {

    Header() {
        // create label
        this.setPreferredSize(new Dimension(400, 60));
        JLabel title = new JLabel("Audio GPT");
        title.setFont(new Font("Arial", Font.PLAIN, 20));

        // create border
        Border line = BorderFactory.createLineBorder(new Color(128, 128, 128), 1);
        Border padding = BorderFactory.createEmptyBorder(10, 40, 10, 40);
        Border compound = BorderFactory.createCompoundBorder(line, padding);

        title.setBorder(compound);

        // middle align
        title.setVerticalAlignment(JLabel.CENTER);
        title.setHorizontalAlignment(JLabel.CENTER);

        add(title, BorderLayout.CENTER);
    }
}

// Button to control the recording or stopping of audio
class PlayPauseButton extends JButton {

    PlayPauseButton() {
        super("Record / Pause");
        this.setBackground(new Color(244, 244, 244));
    }
}

// Rectangle to hold the transcript of the question
class Question extends JPanel {

    private JLabel label;

    Question() {

        // create rectangle
        this.setBackground(new Color(117, 159, 172));
        label = new JLabel("");
        label.setForeground(Color.WHITE);
        this.add(label);
    }

    // updates and truncates text
    public void updateText(String text) {

        String[] words = text.split(" ");
        StringBuilder formattedText = new StringBuilder();

        // uses html syntax to split word over 9 characters
        for (int i = 0; i < words.length; i++) {

            formattedText.append(words[i]);

            if ((i + 1) % 9 == 0 && i != words.length - 1) {
                formattedText.append("<br>");
            } else {
                formattedText.append(" ");
            }
        }

        // telling label to use html format
        label.setText("<html>" + formattedText.toString() + "</html>");
    }

}

// Rectangle to hold answer from ChatGPT
class Answer extends JPanel {
    private JLabel label;

    Answer() {

        // create Rectangle
        this.setBackground(new Color(154, 95, 12));
        label = new JLabel("");
        label.setForeground(Color.WHITE);
        this.add(label);
    }

    // updates and truncates text
    public void updateText(String text) {

        String[] words = text.split(" ");
        StringBuilder formattedText = new StringBuilder();

        for (int i = 0; i < words.length; i++) {

            formattedText.append(words[i]);

            if ((i + 1) % 9 == 0 && i != words.length - 1) {
                formattedText.append("<br>");
            } else {
                formattedText.append(" ");
            }
        }

        // telling label to use html format
        label.setText("<html>" + formattedText.toString() + "</html>");
    }
}

// center bordered ractangle to hold Question
// and answers
class Center extends JPanel {

    PlayPauseButton button;
    JPanel topRightPanel;
    JPanel middleLeftPanel;

    Center() {

        // create cetner
        this.setPreferredSize(new Dimension(800, 400));
        this.setBackground(Color.WHITE);

        Border line = BorderFactory.createLineBorder(new Color(128, 128, 128), 1);
        this.setBorder(line);

        // Border layout for the main panel
        setLayout(new BorderLayout());

        // bottom pannel to hold button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        button = new PlayPauseButton();
        bottomPanel.add(button, BorderLayout.SOUTH);

        // top right pannel to hold questio
        topRightPanel = new JPanel();

        // middle pannel to hold answer
        middleLeftPanel = new JPanel();

        // Panel for center
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(middleLeftPanel, BorderLayout.WEST);

        // Panel for top
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(topRightPanel, BorderLayout.EAST);

        // Add panels to the main panel
        this.add(topPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    public JButton getButton() {
        return button;
    }
}

// Footer to hold and clear question history
class Footer extends JPanel {

    private JPanel questionPanel;
    private Map<JButton, String> historyMap = new HashMap<>();

    Footer() {

        // create area
        this.setPreferredSize(new Dimension(900, 100));
        this.setBackground(new Color(238, 235, 235));
        this.setLayout(new BorderLayout());

        // create question area
        questionPanel = new JPanel();
        questionPanel.setBackground(new Color(238, 235, 235));
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.X_AXIS));

        // make area scroll
        JScrollPane scrollPane = new JScrollPane(questionPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(238, 235, 235));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);

        // button to clear history
        JButton clearHistoryButton = new JButton();
        try {
            Image img = ImageIO.read(getClass().getResource("clear.png"));
            clearHistoryButton.setIcon(new ImageIcon(img));
          } catch (Exception ex) {
            System.out.println(ex);
          }
        clearHistoryButton.setPreferredSize(new Dimension(90, 30));
        clearHistoryButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        clearHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearHistory();
            }
        });
        this.add(clearHistoryButton, BorderLayout.WEST);

        // populate history from file
        readHistoryFromFile();
    }

    public void readHistoryFromFile() {

        // clear exisitng old entries in UI
        questionPanel.removeAll();
        historyMap.clear();

        // read history from file
        try (FileReader fileReader = new FileReader("history.csv");
                BufferedReader reader = new BufferedReader(fileReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] entry = line.split("%,%");
                String question = entry[0].trim();
                String answer = entry[1].trim();

                // limit questino string to 20 characters
                String buttonText = question;
                if (buttonText.length() > 20) {
                    buttonText = buttonText.substring(0, 20) + "...";
                }

                // turn each question into a button
                JButton questionButton = new JButton(buttonText);
                questionButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                questionButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ((AppFrame) SwingUtilities.getWindowAncestor(questionButton)).displayQuestionAndAnswer(question,
                                historyMap.get(questionButton));
                    }
                });

                questionPanel.add(questionButton);
                historyMap.put(questionButton, answer);
            }

            questionPanel.revalidate();
            questionPanel.repaint();
        }

        catch (IOException e) {
            System.out.println("Error reading history file: " + e.getMessage());
        }
    }

    //removes all UI components and empty the CSV
    public void clearHistory() {

        try (FileWriter writer = new FileWriter("history.csv", false)) {
            writer.write("");
            questionPanel.removeAll();
            questionPanel.revalidate();
            questionPanel.repaint();
        } 
        catch (IOException e) {
            System.out.println("Error clearing history file: " + e.getMessage());
        }
    }
}

//main frame
class AppFrame extends JFrame {

    private Header header;
    private Center center;
    private Footer footer;

    private AudioFormat audioFormat;

    private boolean isRecording = false;
    private JButton playPauseButton;

    private Whisper whisper;
    private ChatGPT chatgpt;

    private TargetDataLine targetDataLine;
    private JLabel recordingLabel;

    AppFrame() {

        // creating an empty frame
        this.setSize(1000, 600);

        // style frame
        this.setTitle("Audio GPT");
        ImageIcon image = new ImageIcon("icon.png");
        this.setIconImage(image.getImage());
        this.getContentPane().setBackground(new Color(238, 235, 235));

        // set desktop icon
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            try {
                Image icon = Toolkit.getDefaultToolkit().getImage("icon.png");
                taskbar.setIconImage(icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // replace hide with kill on close
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // stop resize
        this.setResizable(false);

        recordingLabel = new JLabel("Recording...");
        recordingLabel.setVisible(false);

        // adding header
        header = new Header();
        center = new Center();
        whisper = new Whisper();
        chatgpt = new ChatGPT();
        footer = new Footer();

        // adding center
        JPanel centerHolder = new JPanel();
        centerHolder.setLayout(new FlowLayout());
        centerHolder.add(center);

        this.add(header, BorderLayout.NORTH);
        this.add(centerHolder, BorderLayout.CENTER);
        this.add(footer, BorderLayout.SOUTH);

        this.setVisible(true);


        //capture button and audio handler
        playPauseButton = center.getButton();
        audioFormat = getAudioFormat();
        addListeners();
        revalidate();
    }


    //triggers for button presses
    public void addListeners() {

        //handle if record or pause button is hit
        playPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

    
                if (isRecording) {
                    stopRecording();
                    playPauseButton.setText("Record");
                    isRecording = false;

                    Question question = new Question();
                    Answer answer = new Answer();

                    try {

                        //send recording to whisper
                        String transcription = whisper.transcribe("recording.wav");

                        if (transcription != null) {

                            //set question label on center
                            question.updateText(transcription);

                            // clear previous components and add new
                            center.topRightPanel.removeAll(); 
                            center.topRightPanel.add(question, BorderLayout.NORTH);
                            center.topRightPanel.revalidate();
                            center.topRightPanel.repaint();

                            // get chat gpt question
                            String chatGPTAnswer = chatgpt.getAnswer(transcription);
                            answer.updateText(chatGPTAnswer);

                             // clear previous components and add new
                            center.middleLeftPanel.removeAll();
                            center.middleLeftPanel.add(answer, BorderLayout.NORTH);
                            center.middleLeftPanel.revalidate();
                            center.middleLeftPanel.repaint();


                            //add new question to history and write to file
                            try (FileWriter writer = new FileWriter("history.csv", true)) {

                                String entry = transcription.trim() + "%,%" + chatGPTAnswer.trim() + "\n";
                                writer.write(entry);

                                footer.readHistoryFromFile();
                            } catch (IOException err) {
                                System.out.println("error writing to file " + err);
                            }
                        }

                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }

                } 
                
                else {

                    //clean old question and answer
                    center.topRightPanel.removeAll(); 
                    center.middleLeftPanel.removeAll(); 

                    center.topRightPanel.revalidate();
                    center.topRightPanel.repaint();

                    center.middleLeftPanel.revalidate();
                    center.middleLeftPanel.repaint();

                    //start recoding looop
                    startRecording();
                    playPauseButton.setText("Pause");
                    isRecording = true;
                }
            }
        });
    }

    //updates the UI for question and answer
    public void displayQuestionAndAnswer(String question, String answer) {
        Question questionPanel = new Question();
        Answer answerPanel = new Answer();

        questionPanel.updateText(question);
        answerPanel.updateText(answer);

        center.topRightPanel.removeAll(); 
        center.topRightPanel.add(questionPanel, BorderLayout.NORTH);
        center.topRightPanel.revalidate();
        center.topRightPanel.repaint();

        center.middleLeftPanel.removeAll();
        center.middleLeftPanel.add(answerPanel, BorderLayout.NORTH);
        center.middleLeftPanel.revalidate();
        center.middleLeftPanel.repaint();
    }

    //audio function from ChatGPT to work on mac as well
    private AudioFormat getAudioFormat() {
        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float[] sampleRates = { 44100.0f, 48000.0f, 32000.0f, 22050.0f, 16000.0f, 11025.0f, 8000.0f };
        int[] sampleSizes = { 16, 8 };
        int[] channels = { 1, 2 };
        boolean[] endianness = { false, true };

        for (float sampleRate : sampleRates) {
            for (int sampleSize : sampleSizes) {
                for (int channel : channels) {
                    for (boolean endian : endianness) {
                        AudioFormat format = new AudioFormat(encoding, sampleRate, sampleSize, channel,
                                (sampleSize / 8) * channel,
                                sampleRate, endian);
                        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                        if (AudioSystem.isLineSupported(info)) {
                            return format;
                        }
                    }
                }
            }
        }

        throw new RuntimeException("No supported audio format found");
    }

    private void startRecording() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // the format of the TargetDataLine
                    DataLine.Info dataLineInfo = new DataLine.Info(
                            TargetDataLine.class,
                            audioFormat);
                    // the TargetDataLine used to capture audio data from the microphone
                    targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                    targetDataLine.open(audioFormat);
                    targetDataLine.start();
                    recordingLabel.setVisible(true);

                    // the AudioInputStream that will be used to write the audio data to a file
                    AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);

                    // the file that will contain the audio data
                    File audioFile = new File("recording.wav");
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioFile);
                    recordingLabel.setVisible(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void stopRecording() {
        targetDataLine.stop();
        targetDataLine.close();
    }
}

// =================================================================================

//main
public class newGuiGPT {

    public static void main(String[] args) throws IOException {

        System.out.println("Starting GUI");
        new AppFrame();
    }
}



//run build script to launch

// cd /src
// ./buildAndRun.sh


//Alternative method

//compile info 
//javac -cp ../lib/json-20230227.jar Whisper.java ChatGPT.java newGuiGPT.java


//running info
//java -cp ../lib/json-20230227.jar:. newGuiGPT 