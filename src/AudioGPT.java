import java.awt.*;
import java.io.*;
import java.util.*;
import javax.print.DocFlavor.STRING;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.View;
import javax.sound.sampled.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.AbstractBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

class Question extends JPanel{
    JLabel indexQ;
    JLabel questionContent;
    Color gray = new Color(218, 229, 234);
    

    Question() {
        
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
    public void updateContent(String content){
        
      questionContent.setText(content);

    }
    
}
class Answer extends JPanel {
    JLabel indexA;
    JTextArea answerContent;
    JScrollPane scrollPane;
    Color green = new Color(188, 226, 158);

    Answer() {
        this.setPreferredSize(new Dimension(400, 200));
        this.setBackground(green);
        this.setLayout(new BorderLayout());

        indexA = new JLabel("A:");
        indexA.setPreferredSize(new Dimension(20, 20));
        indexA.setHorizontalAlignment(JLabel.CENTER);
        this.add(indexA, BorderLayout.WEST);

        answerContent = new JTextArea("I am AudioGPT.");
        answerContent.setLineWrap(true);
        answerContent.setWrapStyleWord(true);
        answerContent.setEditable(false);
        answerContent.setFont(new Font("San-serif", Font.PLAIN, 12));
        answerContent.setMargin(new Insets(5, 5, 5, 5));
        answerContent.setOpaque(false);
        
        scrollPane = new JScrollPane(answerContent);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void updateContent(String content) {
        answerContent.setText(content);
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


class AppFrame extends JFrame {
    private Header header;
    private Footer footer;
    private QandA qanda;
    
    
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

        header = new Header();
        footer = new Footer();
        qanda = new QandA();
        whisper = new Whisper();
        chatgpt = new ChatGPT();
        
        this.add(header, BorderLayout.NORTH);
        this.add(footer, BorderLayout.SOUTH);
        this.add(qanda, BorderLayout.CENTER);

        askButton = footer.getaskButton();
        stopButton = footer.getstopButton();
        recordingLabel = footer.getrecordingLabel();
        
        

        audioFormat = getAudioFormat();
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
              revalidate();
              }
              catch (Exception exc) {
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

//===========================================================================================================================================================================================


// class Header extends JPanel {
    
//   Color backgroundColor = new Color(66, 66, 66);
//   int cornerRadius = 15;

//   Header() {
//       this.setPreferredSize(new Dimension(400, 70));
//       this.setBackground(backgroundColor);
//       this.setOpaque(false);

//       JLabel titleText = new JLabel("AudioGPT");
//       titleText.setPreferredSize(new Dimension(200, 60));
//       titleText.setFont(new Font("Arial", Font.BOLD, 30)); // Change font to Arial and increase the size to 30
//       titleText.setForeground(Color.WHITE); // Set font color to white
//       titleText.setHorizontalAlignment(JLabel.CENTER);
//       this.add(titleText);
//   }

//   @Override
//   protected void paintComponent(Graphics g) {
//       super.paintComponent(g);

//       Graphics2D g2d = (Graphics2D) g;
//       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//       // Create a rounded rectangle shape with the desired corner radius
//       RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

//       // Draw the drop shadow
//       g2d.setColor(new Color(0, 0, 0, 64));
//       g2d.translate(3, 3);
//       g2d.fill(roundedRectangle);
//       g2d.translate(-3, -3);

//       // Draw the background color
//       g2d.setColor(backgroundColor);
//       g2d.fill(roundedRectangle);

//       // Draw the border
//       g2d.setColor(backgroundColor.darker());
//       g2d.draw(roundedRectangle);
//   }
// }

// class RecordButton extends JPanel {

//   JButton askButton;

//   Color backgroudColor = new Color(66, 66, 66);
//   Border emptyBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);
//   int cornerRadius = 15;

//   RecordButton() {
//       this.setPreferredSize(new Dimension(300, 45));
//       this.setBackground(backgroudColor);

//       GridLayout layout = new GridLayout(1,1);
//       this.setLayout(layout);

//       askButton = new JButton("Record a Question");
//       askButton.setFont(new Font("Arial", Font.PLAIN, 15));
//       askButton.setForeground(Color.WHITE);
//       askButton.setBackground(backgroudColor);
//       askButton.setBorder(emptyBorder);
//       this.add(askButton);
//   }

//   public JButton getaskButton(){
//       return askButton;
//   }
// }


// class ChatContainer extends JPanel {
//   int cornerRadius = 15;

//   ChatContainer() {
//       this.setOpaque(false);
//   }

//   @Override
//   protected void paintComponent(Graphics g) {
//       super.paintComponent(g);

//       Graphics2D g2d = (Graphics2D) g;
//       g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//       // Draw a large white rectangle with a corner radius of 15
//       int rectangleWidth = getWidth() - 500;
//       int rectangleHeight = getHeight() - 250;
//       int x = (getWidth() - rectangleWidth) / 2;
//       int y = (getHeight() - rectangleHeight) / 2;
//       g2d.setColor(Color.WHITE);
//       g2d.fillRoundRect(x, y, rectangleWidth, rectangleHeight, cornerRadius, cornerRadius);
//   }
// }

// class AppFrame extends JFrame {

//   private Whisper whisper;
//   private ChatGPT chatgpt;
//   private ChatContainer chatContainer;
//   private Header header;
//   private AudioFormat audioFormat;

//   private AudioFormat getAudioFormat() {
//       float sampleRate = 44100;
//       int sampleSizeInBits = 16;
//       int channels = 2;
//       boolean signed = true;
//       boolean bigEndian = false;

//       return new AudioFormat(
//           sampleRate,
//           sampleSizeInBits,
//           channels,
//           signed,
//           bigEndian
//       );
//   }

//   AppFrame() {
//       this.setSize(1500, 800);
//       this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//       this.setVisible(true);
//       this.getContentPane().setBackground(new Color(66, 66, 66));

//       header = new Header();
//       chatContainer = new ChatContainer();

//       whisper = new Whisper();
//       chatgpt = new ChatGPT();

//       JPanel centerPanel = new JPanel(new BorderLayout());
//       centerPanel.setBackground(new Color(123, 123, 123));
//       centerPanel.add(chatContainer, BorderLayout.CENTER);

//       JPanel buttonPanel = new JPanel(new GridBagLayout());
//       GridBagConstraints gridBagConstraints = new GridBagConstraints();

//       gridBagConstraints.anchor = GridBagConstraints.CENTER;
//       gridBagConstraints.insets = new Insets(0, 0, 30, 0);

//       RecordButton recordButton = new RecordButton();
//       buttonPanel.setBackground(new Color(123, 123, 123));
//       buttonPanel.add(recordButton, gridBagConstraints);

//       centerPanel.add(buttonPanel, BorderLayout.SOUTH);

//       this.add(header, BorderLayout.NORTH);
//       this.add(centerPanel, BorderLayout.CENTER);

//       audioFormat = getAudioFormat();
//       revalidate();
//   }
// }



public class AudioGPT {
  public static void main(String[] args) {
    new AppFrame();
  }
}