/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package gradle;

import java.awt.*;
import java.io.*;
import java.util.*;


import javax.swing.*;
import javax.swing.border.Border;

import javax.sound.sampled.*;
import java.awt.event.*;

class Question extends JPanel{
  JLabel indexQ;
  JTextArea questionContent;
  Color gray = new Color(218, 229, 234);
  Whisper whisper;
  String questionStr;
    Question(Whisper whisper) {
      this.whisper = whisper;
      questionStr = "";
      this.setPreferredSize(new Dimension(400,200)); //set size of question
      this.setBackground(gray);
      this.setLayout(new BorderLayout());
        
      indexQ = new JLabel("Q:");
      indexQ.setPreferredSize(new Dimension(20, 20));
      indexQ.setHorizontalAlignment(JLabel.CENTER);
      this.add(indexQ,BorderLayout.WEST);

      questionContent = new JTextArea(questionStr);
      questionContent.setEditable(false);
      questionContent.setFont(new Font("San-serif", Font.BOLD, 15));
      questionContent.setBackground(gray);

      questionContent.setLineWrap(true);
      questionContent.setWrapStyleWord(true);

      JScrollPane scrollPane = new JScrollPane(questionContent);
      scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
      scrollPane.setAlignmentY(Component.CENTER_ALIGNMENT);
      
      
      this.add(scrollPane, BorderLayout.CENTER);
    }
    
    public void setString(String str){
      this.questionStr = str;
      questionContent.setText(str);
    }
    
    public String toString(){
      return questionStr;
    }

    public void updateContent()throws Exception{
      String transcription = whisper.transcribe("recording.wav");
      this.questionStr = transcription;
      questionContent.setText(transcription);        
    }
}

class OldQuestion extends JPanel {
  
      
      JButton displayButton;
      JButton deleteButton;

      MainScreen mainscreen;
      QuestionHistory questionhistory;

      Answer answer;
      Question question;
    
      Color black = new Color(0,0,0);
      Color white = new Color(255,255,255);
    
      OldQuestion(Question Q, Answer A, MainScreen mainscreen, QuestionHistory questionhistory) {
        this.mainscreen = mainscreen;
        this.questionhistory = questionhistory;

        this.question = Q;
        this.answer = A;
        this.setPreferredSize(new Dimension(200, 20));
        this.setBackground(white);
        this.setLayout(new GridLayout(1,2));
        
        
        addDisplayButton();
        addDeleteButton();
        addListeners();
  
      }
      public void addDisplayButton(){
        displayButton = new JButton(question.toString());
        displayButton.setPreferredSize(new Dimension(180, 20));
        displayButton.setBackground(white);
        displayButton.setBorder(BorderFactory.createEmptyBorder());
        this.add(displayButton);
      }

      public void addDeleteButton(){
        deleteButton = new JButton("X");
        deleteButton.setPreferredSize(new Dimension(20, 20));
        deleteButton.setBackground(white);
        deleteButton.setBorder(BorderFactory.createEmptyBorder());
        deleteButton.setForeground(Color.RED);
        this.add(deleteButton);
      }

      public void addListeners() {
        displayButton.addActionListener(
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
  JTextArea answerContent;
  Color green = new Color(188,226,158);
  ChatGPT chatgpt;
  String answerStr;     
  Answer(ChatGPT chatgpt) {
    this.chatgpt = chatgpt;
    answerStr = "";
    this.setPreferredSize(new Dimension(400, 200));
    this.setBackground(green);
    this.setLayout(new BorderLayout());

    indexA = new JLabel("A:");
    indexA.setPreferredSize(new Dimension(20, 20));
    indexA.setHorizontalAlignment(JLabel.CENTER);
    this.add(indexA,BorderLayout.WEST);

    answerContent = new JTextArea(answerStr);
    answerContent.setEditable(false);
    answerContent.setFont(new Font("San-serif", Font.BOLD, 15));
    answerContent.setBackground(green);

    answerContent.setLineWrap(true);
    answerContent.setWrapStyleWord(true);

      JScrollPane scrollPane = new JScrollPane(answerContent);
      scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
      scrollPane.setAlignmentY(Component.CENTER_ALIGNMENT);

    this.add(scrollPane, BorderLayout.CENTER);
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

  MainScreen(Whisper whisper, ChatGPT chatgpt) throws Exception{
    GridLayout layout = new GridLayout(2,1);
    layout.setVgap(5);

    this.setLayout(layout);
    this.setPreferredSize(new Dimension(600, 560));
    this.setBackground(backgroundColor);

    this.whisper = whisper; 
    this.chatgpt = chatgpt;

    revalidate();
    repaint();
  }

  public Answer AskQuestion(Question newQuestion)  throws Exception {
    Answer newAnswer = new Answer(chatgpt);
    newQuestion.updateContent();
    newAnswer.updateContent(newQuestion.toString());
    
    removeAll();
    add(newQuestion, 0); // add the latest question panel to the first row (index 0) of the GridLayout
    add(newAnswer);
    
    
    revalidate();
    repaint();
    return newAnswer;
  }
  public void updateHistory(Question newQuestion, Answer newAnswer ,QuestionHistory questionhistory) throws Exception{
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
  Whisper whisper;
  ChatGPT chatgpt;


  QuestionHistory(MainScreen mainscreen, Whisper whisper, ChatGPT chatgpt){
    this.mainscreen = mainscreen;
    this.whisper = whisper;
    this.chatgpt = chatgpt;

    this.setPreferredSize(new Dimension(200, 600));
    this.setBackground(backgroundColor);
  
    GridLayout layout = new GridLayout(8,1);
    layout.setVgap(5);
    this.setLayout(layout);
    this.setPreferredSize(new Dimension(200, 600));
    
    
    answerPanels = new HashMap<>();
    
  }
  public void setTitle(){
    titleText = new JLabel("Prompt History");
    
    titleText.setPreferredSize(new Dimension(50, 60));
    titleText.setFont(new Font("San-serif", Font.BOLD, 12));
    titleText.setHorizontalAlignment(JLabel.CENTER);
    titleText.setVerticalAlignment(SwingConstants.TOP);
    this.add(titleText);
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
    this.setTitle();
    BufferedReader reader = new BufferedReader(new FileReader("history.txt"));
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split("\\|");
        if (parts.length == 2) {
          Question question = new Question(whisper);
          question.setString(parts[0]);
          Answer answer = new Answer(chatgpt);
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
  Whisper whisper;
  ChatGPT chatgpt;

  private JButton askButton;
  private JButton stopButton;
    
  private AudioFormat audioFormat;
  private TargetDataLine targetDataLine;
  private JLabel recordingLabel;

  AppFrame() throws Exception{
    whisper = new Whisper();
    chatgpt = new ChatGPT();

    this.setSize(1000, 600);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
    
    header = new Header();
    footer = new Footer();
    mainscreen = new MainScreen(whisper,chatgpt);
    questionhistory = new QuestionHistory(mainscreen,whisper,chatgpt);
        
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
          Question newQuestion = new Question(whisper);
          Answer newAnswer = mainscreen.AskQuestion(newQuestion);
          mainscreen.updateHistory(newQuestion, newAnswer, questionhistory);
          
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
