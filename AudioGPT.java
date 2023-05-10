import java.awt.*;
import java.io.*;
import java.util.*;

import javax.print.DocFlavor.STRING;
import javax.swing.*;
import javax.swing.border.Border;

import javax.sound.sampled.*;
import java.awt.event.*;

class Question extends JPanel{
    JLabel indexQ;
    JLabel questionContent;
    Color gray = new Color(218, 229, 234);
    Whisper whisper;
    String questionStr;

    Question() {
        whisper = new Whisper();
        questionStr = "";
        this.setPreferredSize(new Dimension(400,200)); //set size of question
        this.setBackground(gray);
        this.setLayout(new BorderLayout());
        
        indexQ = new JLabel("Q:");
        indexQ.setPreferredSize(new Dimension(20, 20));
        indexQ.setHorizontalAlignment(JLabel.CENTER);
        this.add(indexQ,BorderLayout.WEST);

        questionContent = new JLabel("What is your name?");
        questionContent.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        this.add(questionContent, BorderLayout.CENTER);

    }
    public void setQstring(String str){
      this.questionStr = str;
      questionContent.setText(questionStr);
    }
    public String toString(){
      return questionStr;
    }
    public void updateContent(){
      String transcription;
      try {
        transcription = whisper.transcribe("recording.wav");
        this.questionStr = transcription;
      if (transcription != null) {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            questionContent.setText(transcription);
          }
        });
      } else {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            questionContent.setText("Error: Could not transcribe audio");   
          }
        });
      }
      
    }
    catch (Exception exc) {
      exc.printStackTrace();
   
    }
      

    }
    
}
class Answer extends JPanel{
    JLabel indexA;
    JLabel answerContent;
    Color green = new Color(188,226,158);
    ChatGPT chatgpt;
    String ansStr;
   
    Answer() {
        chatgpt = new ChatGPT();
        ansStr = "";
        this.setPreferredSize(new Dimension(400, 200));
        this.setBackground(green);
        this.setLayout(new BorderLayout());

        indexA = new JLabel("A:");
        indexA.setPreferredSize(new Dimension(20, 20));
        indexA.setHorizontalAlignment(JLabel.CENTER);
        this.add(indexA,BorderLayout.WEST);

        answerContent = new JLabel("I am AudioGPT.");
        answerContent.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        this.add(answerContent, BorderLayout.CENTER);
    }
    public void setAstr(String str){
      this.ansStr = str;
      answerContent.setText(ansStr);
    }
    public String toString(){
      return this.ansStr;
    }
    public void updateContent(String content) {
      try {
        String answer = chatgpt.getAnswer(content);
        answerContent.setText(answer);
        this.ansStr = answer;
      } catch (Exception e) {
          e.printStackTrace();
         
      }
    }
}

class QandA extends JPanel{
    Color backgroundColor = new Color(240,248,255);

    QandA() {
        GridLayout layout = new GridLayout(2,1);
        layout.setVgap(5);

        this.setLayout(layout);
        this.setPreferredSize(new Dimension(600, 560));
        this.setBackground(backgroundColor);
    }
}

class Footer extends JPanel{
    JButton askButton;
    JButton stopButton;
    JLabel recordingLabel;

    Color backgroudColor = new Color(240,248,255);
    Border emptyBorder = BorderFactory.createEmptyBorder();
    
    Footer() {
        this.setPreferredSize(new Dimension(60, 60));
        this.setBackground(backgroudColor);

        GridLayout layout = new GridLayout(1,4);
        layout.setHgap(5);
        this.setLayout(layout);

        askButton = new JButton("Ask a Question");
        askButton.setFont(new Font("San-serif", Font.ITALIC, 10));
        this.add(askButton);

        stopButton = new JButton("Stop recording");
        stopButton.setFont(new Font("San-serif", Font.ITALIC, 10));
        this.add(stopButton);

        recordingLabel = new JLabel("Recording");
        recordingLabel.setForeground(Color.RED);
        recordingLabel.setPreferredSize(new Dimension(20, 20));
        recordingLabel.setVisible(false);
        this.add(recordingLabel);

    }
    public JButton getaskButton(){
        return askButton;
    }
    public JButton getstopButton() {
        return stopButton;
    }
    public JLabel getrecordingLabel() {
        return recordingLabel;
    }
}

class Header extends JPanel{
    
    Color backgroundColor  = new Color(240, 248,255);

    Header() {
        this.setPreferredSize(new Dimension(400, 60));
        this.setBackground(backgroundColor);
        JLabel titleText = new JLabel("AudioGPT");
        titleText.setPreferredSize(new Dimension(200, 60));
        titleText.setFont(new Font("San-serif", Font.BOLD, 20));
        titleText.setHorizontalAlignment(JLabel.CENTER);
        this.add(titleText);

    }

}
class OldQuestion extends JPanel {
  
  JLabel index;
  JLabel questionLabel;
  JLabel answerLabel;
  JButton display;
  Answer answer;
  Question question;

  Color gray = new Color(218, 229, 234);
  Color green = new Color(188, 226, 158);

  OldQuestion(Question Q, Answer A) {
    this.question = Q;
    this.answer = A;
    this.setPreferredSize(new Dimension(200, 20));
    this.setBackground(gray);
    this.setLayout(new BorderLayout());

    index = new JLabel("");
    index.setPreferredSize(new Dimension(20, 20));
    index.setHorizontalAlignment(JLabel.CENTER);
    this.add(index, BorderLayout.WEST);

    questionLabel = new JLabel(Q.toString());
    questionLabel.setBorder(BorderFactory.createEmptyBorder());
    questionLabel.setBackground(gray);

    answerLabel = new JLabel(A.toString());

    this.add(questionLabel, BorderLayout.CENTER);

    display = new JButton("Display");
    display.setPreferredSize(new Dimension(40, 20));
    display.setBorder(BorderFactory.createEmptyBorder());
    display.setFocusPainted(false);

    this.add(display, BorderLayout.EAST);
  }

  public void changeIndex(int num) {
    this.index.setText(num + ""); // num to String
    this.revalidate(); // refresh
  }

  public JButton getDisplay() {
    return display;
  }

  public void displayAnswer(QandA qanda) {
      
      qanda.removeAll();
      Question qCopy = new Question();
      qCopy.setQstring(question.toString());
      qanda.add(qCopy);
      
      Answer aCopy = new Answer();
      aCopy.setAstr(answer.toString());
      qanda.add(aCopy);
      
      

  }
}

class SideBar extends JPanel{

  Color backgroundColor  = new Color(173, 216,230);

  SideBar() {
    this.setPreferredSize(new Dimension(200, 600));
    this.setBackground(backgroundColor);
    JLabel titleText = new JLabel("Prompt History");

    
    titleText.setPreferredSize(new Dimension(50, 60));
    titleText.setFont(new Font("San-serif", Font.BOLD, 12));
    titleText.setHorizontalAlignment(JLabel.CENTER);
    titleText.setVerticalAlignment(SwingConstants.TOP);

    GridLayout layout = new GridLayout(8,1);
    layout.setVgap(5);
    this.setLayout(layout);
    this.setPreferredSize(new Dimension(200, 600));
    this.add(titleText);
  }


  public void updateNumbers() {
    Component[] listItems = this.getComponents();

    for (int i = 0; i < listItems.length; i++) {
      if (listItems[i] instanceof OldQuestion) {
        ((OldQuestion) listItems[i]).changeIndex(i);
      }
    }
  }
  public ArrayList<OldQuestion> getOldQList(){
    Component[] listItems = this.getComponents();
    ArrayList<OldQuestion> oldQlist = new ArrayList<>();
    for (int i = 0; i < listItems.length; i++) {
      if (listItems[i] instanceof OldQuestion) {
        oldQlist.add(((OldQuestion)listItems[i]));
      }
    }
    return oldQlist;
  }

  public void loadHistory() {
    String line1;
    String line2;

    try (BufferedReader b = new BufferedReader(new FileReader("history.txt"))) {
        while ((line1 = b.readLine()) != null) {
          b.readLine();
          line2 = b.readLine();

          Question l1 = new Question();
          l1.setQstring(line1);
          Answer l2 = new Answer();
          l2.setAstr(line2);
          OldQuestion oldQuestion = new OldQuestion(l1, l2);
          this.add(oldQuestion);
          this.updateNumbers();
          revalidate();
          }
          b.close();
      } catch (IOException e) { 
        e.printStackTrace();
      }
  }

  public void writeHistory(String question, String answer) {

    try (FileWriter fw = new FileWriter("history.txt",true)) {
      fw.append(question);
      fw.append(answer);
      fw.append("\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}


class AppFrame extends JFrame {
    private Header header;
    private Footer footer;
    private QandA qanda;
    private SideBar sidebar;
    
    
    private JButton askButton;
    private JButton stopButton;
    

    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private JLabel recordingLabel;

    

    AppFrame() {
        this.setSize(1000, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        header = new Header();
        footer = new Footer();
        qanda = new QandA();
        sidebar = new SideBar();
      
        
        this.add(header, BorderLayout.NORTH);
        this.add(footer, BorderLayout.SOUTH);
        this.add(qanda, BorderLayout.CENTER);
        this.add(sidebar, BorderLayout.WEST);

        askButton = footer.getaskButton();
        stopButton = footer.getstopButton();
        recordingLabel = footer.getrecordingLabel();
        
        
        

        audioFormat = getAudioFormat();
        sidebar.loadHistory();
        addListeners();
        revalidate();
    }
    
    public void addListeners() {
        askButton.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              startRecording();
            }
          }
        );
        stopButton.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              stopRecording();
              qanda.removeAll();
              Question question = new Question();
              
              try {
                question.updateContent();
                String transcription = question.toString();
                Question qCopy = new Question();
                qCopy.setQstring(question.toString());
                qanda.add(qCopy);

                Answer answer = new Answer();
                answer.updateContent(transcription);
                Answer aCopy = new Answer();
                aCopy.setAstr(answer.toString());
                qanda.add(aCopy);

                OldQuestion oldQuestion = new OldQuestion(question, answer);
                sidebar.writeHistory(transcription, answer.answerContent.getText());
                sidebar.add(oldQuestion);
                sidebar.updateNumbers();
                revalidate();
              }
              catch (Exception exc) {
                exc.printStackTrace();
              }
            }
          }
        );
        ArrayList<OldQuestion> oldqlist = sidebar.getOldQList();
        for (OldQuestion i: oldqlist){
          i.getDisplay().addActionListener(
            new ActionListener(){
              @Override
              public void actionPerformed(ActionEvent e) {
                i.displayAnswer(qanda);
                revalidate();
              }
            }
          );
        }

        

    }

    private AudioFormat getAudioFormat() {
        // the number of samples of audio per second.
        // 44100 represents the typical sample rate for CD-quality audio.
        float sampleRate = 44100;
    
        // the number of bits in each sample of a sound that has been digitized.
        int sampleSizeInBits = 16;
    
        // the number of audio channels in this format (1 for mono, 2 for stereo).
        int channels = 2;
    
        // whether the data is signed or unsigned.
        boolean signed = true;
    
        // whether the audio data is stored in big-endian or little-endian order.
        boolean bigEndian = false;
    
        return new AudioFormat(
          sampleRate,
          sampleSizeInBits,
          channels,
          signed,
          bigEndian
        );
    }

    private void startRecording() {
        Thread t = new Thread(new Runnable(){ 
          @Override
          public void run() {
        try {
          // the format of the TargetDataLine
          DataLine.Info dataLineInfo = new DataLine.Info(
            TargetDataLine.class,
            audioFormat
          );
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
    }
        );
        t.start();
      }
    
      private void stopRecording() {
        targetDataLine.stop();
        targetDataLine.close();
      }

}

 public class AudioGPT {
    public static void main(String[] args) throws IOException {
        new AppFrame();
    }
 }