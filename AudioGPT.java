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

      questionContent = new JLabel(questionStr);
      questionContent.setAlignmentX(JLabel.CENTER_ALIGNMENT);
      this.add(questionContent, BorderLayout.CENTER);
    }
    
    public void setString(String str){
      this.questionStr = str;
      questionContent.setText(str);
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
          }
          else {
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

class OldQuestion extends JButton {
  
      
      JLabel questionLabel;
      JLabel answerLabel;

      MainScreen mainscreen;
      QuestionHistory questionhistory;

      Answer answer;
      Question question;
    
      Color gray = new Color(218, 229, 234);
      Color green = new Color(188, 226, 158);
    
      OldQuestion(Question Q, Answer A, MainScreen mainscreen, QuestionHistory questionhistory) {
        this.mainscreen = mainscreen;
        this.questionhistory = questionhistory;

        this.question = Q;
        this.answer = A;
        this.setPreferredSize(new Dimension(200, 20));
        this.setBackground(gray);
        this.setLayout(new BorderLayout());
        
    
        questionLabel = new JLabel(Q.toString());
        questionLabel.setBorder(BorderFactory.createEmptyBorder());
        questionLabel.setBackground(gray);
    
        answerLabel = new JLabel(A.toString());
        this.add(questionLabel, BorderLayout.CENTER);
        addListeners();
  
      }
      public void addListeners() {
        this.addActionListener(
          new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            mainscreen.removeAll();
            try{
              questionhistory.loadFromFile();
            }
            catch (Exception exc){
              exc.printStackTrace();
            }
            mainscreen.add(question);
            mainscreen.add(answer);
            revalidate();
          }
          }   
      );
      }
      
    
    
}










class Answer extends JPanel{
  JLabel indexA;
  JLabel answerContent;
  Color green = new Color(188,226,158);
  ChatGPT chatgpt;
  String answerStr;     
  Answer() {
    chatgpt = new ChatGPT();
    answerStr = "";
    this.setPreferredSize(new Dimension(400, 200));
    this.setBackground(green);
    this.setLayout(new BorderLayout());

    indexA = new JLabel("A:");
    indexA.setPreferredSize(new Dimension(20, 20));
    indexA.setHorizontalAlignment(JLabel.CENTER);
    this.add(indexA,BorderLayout.WEST);

    answerContent = new JLabel(answerStr);
    answerContent.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    this.add(answerContent, BorderLayout.CENTER);
  }
    
  public void setString(String str){
    this.answerStr = str;
    answerContent.setText(str);
  }
  
  public String toString(){
    return this.answerStr;
  }
  
  public void updateContent(String content) {
    try {
      this.answerStr = chatgpt.getAnswer(content);
      answerContent.setText(answerStr);
    } 
    catch (Exception e) {
      e.printStackTrace();
         
    }
  }
}

class MainScreen extends JPanel{
  Color backgroundColor = new Color(240,248,255);
  
  
  Whisper whisper;
  ChatGPT chatgpt;

  MainScreen() throws Exception{
    GridLayout layout = new GridLayout(2,1);
    layout.setVgap(5);

    this.setLayout(layout);
    this.setPreferredSize(new Dimension(600, 560));
    this.setBackground(backgroundColor);

    whisper = new Whisper();
    chatgpt = new ChatGPT();

    revalidate();
    repaint();
  }

  public void AskQuestion(Question newQuestion, QuestionHistory questionhistory)  throws Exception {
    Answer newAnswer = new Answer();
    newQuestion.updateContent();
    newAnswer.updateContent(newQuestion.toString());
    
    removeAll();
    add(newQuestion, 0); // add the latest question panel to the first row (index 0) of the GridLayout
    add(newAnswer);
    questionhistory.saveToFile(newQuestion,newAnswer);
    
    questionhistory.loadFromFile();
    revalidate();
    repaint();
  }

}

class QuestionHistory extends JPanel{
  Color backgroundColor = new Color(173, 216, 230);
  JLabel titleText;
  MainScreen mainscreen;
  Map<Question,Answer> answerPanels;


  QuestionHistory(MainScreen mainscreen){
    this.mainscreen = mainscreen;
    
    this.setPreferredSize(new Dimension(200, 600));
    this.setBackground(backgroundColor);
    titleText = new JLabel("Prompt History");
    
    titleText.setPreferredSize(new Dimension(50, 60));
    titleText.setFont(new Font("San-serif", Font.BOLD, 12));
    titleText.setHorizontalAlignment(JLabel.CENTER);
    titleText.setVerticalAlignment(SwingConstants.TOP);
  
    GridLayout layout = new GridLayout(8,1);
    layout.setVgap(5);
    this.setLayout(layout);
    this.setPreferredSize(new Dimension(200, 600));
    this.add(titleText);
    
    answerPanels = new HashMap<>();
    
  }
  
  public void saveToFile(Question question, Answer answer) throws IOException{
    
    FileWriter writer = new FileWriter("history.txt",true);
    
      String questionstr = question.toString();
      String answerstr = answer.toString();
      questionstr = questionstr.replace("\n","");
      answerstr = answerstr.replace("\n","");
      String result = questionstr + "|" + answerstr +"\n";
      
      writer.append(result);
    
    
    writer.close();
  }

  public void loadFromFile() throws IOException {
    this.removeAll();  
    BufferedReader reader = new BufferedReader(new FileReader("history.txt"));
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split("\\|");
        if (parts.length == 2) {
          Question question = new Question();
          question.setString(parts[0]);
          Answer answer = new Answer();
          answer.setString(parts[1]);
          OldQuestion oldQuestion = new OldQuestion(question,answer,mainscreen,this);
          
          this.add(oldQuestion);
          

          revalidate();
        }
      }
      reader.close();
    }
    
    public void updateMap(Question question, Answer answer){
      answerPanels.put(question, answer);

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

class AppFrame extends JFrame {
  private Header header;
  private Footer footer;
  private MainScreen mainscreen;
  private QuestionHistory questionhistory; 
  
  private JButton askButton;
  private JButton stopButton;
    
  private AudioFormat audioFormat;
  private TargetDataLine targetDataLine;
  private JLabel recordingLabel;

  AppFrame() throws Exception{
    this.setSize(1000, 600);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
    
    header = new Header();
    footer = new Footer();
    mainscreen = new MainScreen();
    questionhistory = new QuestionHistory(mainscreen);
        
    this.add(header, BorderLayout.NORTH);
    this.add(footer, BorderLayout.SOUTH);
    this.add(mainscreen, BorderLayout.CENTER);
    this.add(questionhistory, BorderLayout.WEST);

    askButton = footer.getaskButton();
    stopButton = footer.getstopButton();
    recordingLabel = footer.getrecordingLabel();
        
    audioFormat = getAudioFormat();
    
    questionhistory.loadFromFile();
    addListeners();
    revalidate();
    repaint();
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
        try{
          Question newQuestion = new Question();
          mainscreen.AskQuestion(newQuestion,questionhistory);
          
        }
        catch(Exception exc){
          exc.printStackTrace();
        }
      }
      }
      );    
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
          } 
          catch (Exception ex) {
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
  public static void main(String[] args) throws Exception {
    new AppFrame();
  }
}
