import java.awt.*;
import java.io.*;
import java.util.*;

import javax.print.DocFlavor.STRING;
import javax.swing.*;
import javax.swing.border.Border;

import javax.sound.sampled.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.geom.RoundRectangle2D;

class Question extends JPanel {
  JLabel indexQ;
  JLabel questionContent;
  Color gray = new Color(218, 229, 234);

  Question() {

    this.setPreferredSize(new Dimension(400, 200)); // set size of question
    this.setBackground(gray);
    this.setLayout(new BorderLayout());

    indexQ = new JLabel("Q:");
    indexQ.setPreferredSize(new Dimension(20, 20));
    indexQ.setHorizontalAlignment(JLabel.CENTER);
    this.add(indexQ, BorderLayout.WEST);

    questionContent = new JLabel("What is your name?");
    questionContent.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    this.add(questionContent, BorderLayout.CENTER);

  }

  public void updateContent(String content) {

    questionContent.setText(content);

  }

}

class Answer extends JPanel {
  JLabel indexA;
  JLabel answerContent;
  Color green = new Color(188, 226, 158);

  Answer() {
    this.setPreferredSize(new Dimension(400, 200));
    this.setBackground(green);
    this.setLayout(new BorderLayout());

    indexA = new JLabel("A:");
    indexA.setPreferredSize(new Dimension(20, 20));
    indexA.setHorizontalAlignment(JLabel.CENTER);
    this.add(indexA, BorderLayout.WEST);

    answerContent = new JLabel("I am AudioGPT.");
    answerContent.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    this.add(answerContent, BorderLayout.CENTER);
  }

  public void updateContent(String content) {
    answerContent.setText(content);
  }
}

class QandA extends JPanel {
  Color backgroundColor = new Color(240, 248, 255);

  QandA() {
    GridLayout layout = new GridLayout(2, 1);
    layout.setVgap(5);

    this.setLayout(layout);
    this.setPreferredSize(new Dimension(600, 560));
    this.setBackground(backgroundColor);
  }
}

class RoundedButton extends JButton {
  private int cornerRadius;

  public RoundedButton(String text, int cornerRadius) {
    super(text);
    this.cornerRadius = cornerRadius;
    setForeground(Color.WHITE);
    setBackground(new Color(0x424242));
    setFocusPainted(false);
    setContentAreaFilled(false);
    setOpaque(false);
    setBorderPainted(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(getBackground());
    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
    super.paintComponent(g);
  }

  @Override
  protected void paintBorder(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(getForeground());
    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
  }
}

class Footer extends JPanel {
  JButton askButton;
  JButton stopButton;
  JLabel recordingLabel;

  Color backgroundColor =(new Color(123, 123, 123));
  Border emptyBorder = BorderFactory.createEmptyBorder();

  Footer() {
    this.setPreferredSize(new Dimension(60, 60));

    // this.getContentPane().setBackground(new Color(123, 123, 123));
    this.setBackground(backgroundColor);

    // Replace GridLayout with GridBagLayout
    GridBagLayout layout = new GridBagLayout();
    this.setLayout(layout);

    GridBagConstraints constraints = new GridBagConstraints();

    askButton = new RoundedButton("Ask a Question", 15);
    askButton.setFont(new Font("Ariel", Font.PLAIN, 10));

    // Set constraints for askButton
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.weightx = 1;
    constraints.weighty = 1;
    constraints.anchor = GridBagConstraints.CENTER;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(5, 5, 5, 5);

    this.add(askButton, constraints);

    stopButton = new RoundedButton("Stop recording", 15);
    stopButton.setFont(new Font("Ariel", Font.PLAIN, 10));

    // Set constraints for stopButton
    constraints.gridx = 1;
    constraints.gridy = 0;
    constraints.weightx = 1;
    constraints.weighty = 1;
    constraints.anchor = GridBagConstraints.CENTER;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(5, 5, 5, 5);

    this.add(stopButton, constraints);

    recordingLabel = new JLabel("Recording");
    recordingLabel.setForeground(Color.RED);
    recordingLabel.setPreferredSize(new Dimension(20, 20));
    recordingLabel.setVisible(false);

    // Set constraints for recordingLabel
    constraints.gridx = 2;
    constraints.gridy = 0;
    constraints.weightx = 0;
    constraints.weighty = 0;
    constraints.anchor = GridBagConstraints.WEST;
    constraints.fill = GridBagConstraints.NONE;
    constraints.insets = new Insets(5, 5, 5, 5);

    this.add(recordingLabel, constraints);
  }

  public JButton getaskButton() {
    return askButton;
  }

  public JButton getstopButton() {
    return stopButton;
  }

  public JLabel getrecordingLabel() {
    return recordingLabel;
  }
}

class Header extends JPanel {

  Color backgroundColor = new Color(66, 66, 66);
  int cornerRadius = 15;

  Header() {
    this.setPreferredSize(new Dimension(400, 45));
    this.setBackground(backgroundColor);
    this.setOpaque(false);

    JLabel titleText = new JLabel("AudioGPT");
    titleText.setPreferredSize(new Dimension(200, 40));
    titleText.setFont(new Font("Arial", Font.BOLD, 20));
    titleText.setForeground(Color.WHITE); // Set font color to white
    titleText.setHorizontalAlignment(JLabel.CENTER);
    this.add(titleText);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Create a rounded rectangle shape with the desired corner radius
    RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius,
        cornerRadius);

    // Draw the drop shadow
    g2d.setColor(new Color(0, 0, 0, 64));
    g2d.translate(3, 3);
    g2d.fill(roundedRectangle);
    g2d.translate(-3, -3);

    // Draw the background color
    g2d.setColor(backgroundColor);
    g2d.fill(roundedRectangle);

    // Draw the border
    g2d.setColor(backgroundColor.darker());
    g2d.draw(roundedRectangle);
  }
}

class OldQuestion extends JPanel {

  JLabel index;
  JTextField Question;
  JTextField Answer;
  JButton displayAnswer;

  Color gray = new Color(218, 229, 234);
  Color green = new Color(188, 226, 158);

  OldQuestion(String Q, String A) {
    this.setPreferredSize(new Dimension(200, 20));
    this.setBackground(gray);
    this.setLayout(new BorderLayout());

    index = new JLabel("");
    index.setPreferredSize(new Dimension(20, 20));
    index.setHorizontalAlignment(JLabel.CENTER);
    this.add(index, BorderLayout.WEST);

    Question = new JTextField(Q);
    Question.setBorder(BorderFactory.createEmptyBorder());
    Question.setBackground(gray);

    Answer = new JTextField(A);

    this.add(Question, BorderLayout.CENTER);

    displayAnswer = new JButton("Display");
    displayAnswer.setPreferredSize(new Dimension(40, 20));
    displayAnswer.setBorder(BorderFactory.createEmptyBorder());
    displayAnswer.setFocusPainted(false);

    this.add(displayAnswer, BorderLayout.EAST);
  }

  public void changeIndex(int num) {
    this.index.setText(num + ""); // num to String
    this.revalidate(); // refresh
  }

  public JButton getDisplayAnswer() {
    return displayAnswer;
  }

  public void displayAnswer() {
    Answer.setBorder(BorderFactory.createEmptyBorder());
    Answer.setBackground(gray);
  }
}

class SideBar extends JPanel {

  // Color backgroundColor = new Color(173, 216, 230);
  Color backgroundColor =(new Color(123, 123, 123));

  SideBar() {
    this.setPreferredSize(new Dimension(200, 600));
    this.setBackground(backgroundColor);
    JLabel titleText = new JLabel("Prompt History");

    titleText.setPreferredSize(new Dimension(50, 60));
    titleText.setFont(new Font("San-serif", Font.BOLD, 12));
    titleText.setHorizontalAlignment(JLabel.CENTER);
    titleText.setVerticalAlignment(SwingConstants.TOP);

    GridLayout layout = new GridLayout(8, 1);
    layout.setVgap(5);
    this.setLayout(layout);
    this.setPreferredSize(new Dimension(200, 600));
    this.add(titleText);
  }

  public void updateNumbers() {
    Component[] listItems = this.getComponents();

    for (int i = 0; i < listItems.length; i++) {
      if (listItems[i] instanceof OldQuestion) {
        ((OldQuestion) listItems[i]).changeIndex(i + 1);
      }
    }
  }

  class RoundButton extends JButton {
    public RoundButton(String text) {
      super(text);
      setBackground(new Color(123, 123, 123));
      setForeground(new Color(123, 123, 123));
      setFocusPainted(false);
      setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
      if (getModel().isArmed()) {
        g.setColor(getBackground().darker());
      } else {
        g.setColor(getBackground());
      }
      g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
      super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
      g.setColor(getForeground());
      g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
    }

    @Override
    public boolean contains(int x, int y) {
      if (shape == null || !shape.getBounds().equals(getBounds())) {
        shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
      }
      return shape.contains(x, y);
    }

    private Shape shape;
  }

  public void loadHistory() {
    String line1;
    String line2;

    try (BufferedReader b = new BufferedReader(new FileReader("history.txt"))) {
      while ((line1 = b.readLine()) != null) {
        b.readLine();
        line2 = b.readLine();
        OldQuestion oldQuestion = new OldQuestion(line1, line2);
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
    try (FileWriter fw = new FileWriter("history.txt", true)) {
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

  private Whisper whisper;
  private ChatGPT chatgpt;

  AppFrame() {
    this.setSize(1000, 600);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);

    this.getContentPane().setBackground(new Color(123, 123, 123));


    header = new Header();
    footer = new Footer();
    qanda = new QandA();
    sidebar = new SideBar();
    whisper = new Whisper();
    chatgpt = new ChatGPT();

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
        });
    stopButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            stopRecording();
            qanda.removeAll();
            Question question = new Question();

            try {
              String transcription = whisper.transcribe("recording.wav");
              if (transcription != null) {
                SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                    question.updateContent(transcription);
                    // Refresh the QandA panel
                    qanda.revalidate();
                    qanda.repaint();
                  }
                });
              } else {
                SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                    question.updateContent("Error: Could not transcribe audio");

                    // Refresh the QandA panel
                    qanda.revalidate();
                    qanda.repaint();
                  }
                });
              }

              qanda.add(question);

              Answer answer = new Answer();
              answer.updateContent(chatgpt.getAnswer(transcription));
              qanda.add(answer);

              OldQuestion oldQuestion = new OldQuestion(transcription, answer.answerContent.getText());
              sidebar.writeHistory(transcription, answer.answerContent.getText());
              sidebar.add(oldQuestion);
              sidebar.updateNumbers();
              JButton displayAnswer = oldQuestion.getDisplayAnswer();
              displayAnswer.addActionListener(
                  (ActionEvent ee) -> {
                    oldQuestion.displayAnswer();
                    sidebar.updateNumbers();
                    revalidate();
                  });
              revalidate();
            } catch (Exception exc) {
              exc.printStackTrace();
            }
          }
        });
  }

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
            AudioFormat format = new AudioFormat(encoding, sampleRate, sampleSize, channel, (sampleSize / 8) * channel,
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

public class AudioGPT {
  public static void main(String[] args) throws IOException {
    new AppFrame();
  }
}
