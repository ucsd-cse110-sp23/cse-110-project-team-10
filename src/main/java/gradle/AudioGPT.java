package gradle;

import java.awt.*;
// import java.awt.desktop.QuitEvent;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.sound.sampled.*;
import java.awt.event.*;

class Question extends JPanel {
	UUID id;
	JLabel indexQ;
	JTextArea questionContent;
	Color lightblue = new Color(240, 248, 255);
	Whisper whisper;
	String questionStr;

	public Question(Whisper whisper, UUID id) {
		this.id = id;
		this.whisper = whisper;
		questionStr = "";
		this.setPreferredSize(new Dimension(400, 200)); // set size of question
		this.setBackground(lightblue);
		this.setLayout(new BorderLayout());

		indexQ = new JLabel("Q:");
		indexQ.setPreferredSize(new Dimension(20, 20));
		indexQ.setHorizontalAlignment(JLabel.CENTER);
		this.add(indexQ, BorderLayout.WEST);

		questionContent = new JTextArea(questionStr);
		questionContent.setEditable(false);
		questionContent.setFont(new Font("San-serif", Font.BOLD, 15));
		questionContent.setBackground(lightblue);

		questionContent.setLineWrap(true);
		questionContent.setWrapStyleWord(true);

		JScrollPane scrollPane = new JScrollPane(questionContent);
		scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		scrollPane.setAlignmentY(Component.CENTER_ALIGNMENT);

		this.add(scrollPane, BorderLayout.CENTER);
	}

	public UUID getId() {
		return id;
	}

	public void setString(String str) {
	    if (str == null) {
	    	this.questionStr = "";
	        questionContent.setText("");
	    } else {
	    	this.questionStr = str;
	        questionContent.setText(str);
	    }
	}

	public String toString() {
		return questionStr;
	}

	public void updateContent() throws Exception {
		String transcription = whisper.transcribe("recording.wav");
		this.questionStr = transcription;
		questionContent.setText(transcription);
	}
}

class Answer extends JPanel {
	JLabel indexA;
	JTextArea answerContent;
	Color green = new Color(240, 248, 255);
	ChatGPT chatgpt;
	String answerStr;

	public Answer(ChatGPT chatgpt) {
		this.chatgpt = chatgpt;
		answerStr = "";
		this.setPreferredSize(new Dimension(400, 200));
		this.setBackground(green);
		this.setLayout(new BorderLayout());

		indexA = new JLabel("A:");
		indexA.setPreferredSize(new Dimension(20, 20));
		indexA.setHorizontalAlignment(JLabel.CENTER);
		this.add(indexA, BorderLayout.WEST);

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

	public void setString(String str) {
	    if (str == null) {
	    	this.answerStr = "";
			answerContent.setText("");
	    } else {
	    	this.answerStr = str;
			answerContent.setText(str);
	    }
	}


	public String toString() {
		return this.answerStr;
	}

	public void updateContent(String content) {
		try {
			this.answerStr = chatgpt.getAnswer(content);
			answerContent.setText(answerStr);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}

class MainScreen extends JPanel {
	Color backgroundColor = new Color(240, 248, 255);
	Question questionOnMain;
	Whisper whisper;
	ChatGPT chatgpt;

	public MainScreen(Whisper whisper, ChatGPT chatgpt) throws Exception {
		UUID questionId = UUID.randomUUID();
		questionOnMain = new Question(whisper, questionId);

		GridLayout layout = new GridLayout(2, 1);
		layout.setVgap(5);

		this.setLayout(layout);
		this.setPreferredSize(new Dimension(600, 560));
		this.setBackground(backgroundColor);

		this.whisper = whisper;
		this.chatgpt = chatgpt;

		revalidate();
		repaint();
	}

	public Answer AskQuestion(Question newQuestion) throws Exception {
		Answer newAnswer = new Answer(chatgpt);
		newQuestion.updateContent();
		newAnswer.updateContent(newQuestion.toString());

		removeAll();
		add(newQuestion, 0); // add the latest question panel to the first row (index 0) of the GridLayout
		add(newAnswer);
		this.setQuestionOnMain(newQuestion);

		revalidate();
		repaint();
		return newAnswer;
	}

	public OldQuestion updateHistory(Question question, Answer answer, QuestionHistory questionhistory) throws Exception {
		OldQuestion oldquestion = new OldQuestion(question, answer, this, questionhistory);
		questionhistory.updateMap(question, answer);
		questionhistory.add(oldquestion);
		questionhistory.saveToFile();
		revalidate();
		repaint();
		return oldquestion;
	}

	public void setQuestionOnMain(Question question) {
		questionOnMain = question;
	}

	public Question getQuestionOnMain() {
		return questionOnMain;
	}

	public void clearAll() {
		this.removeAll();
		this.questionOnMain = null;
		this.revalidate();
		this.repaint();
	}

}

class QuestionHistory extends JPanel {
	Color backgroundColor = new Color(173, 216, 230);
	MainScreen mainscreen;
	Map<Question, Answer> answerPanels;
	Whisper whisper;
	ChatGPT chatgpt;

	public QuestionHistory(MainScreen mainscreen, Whisper whisper, ChatGPT chatgpt) {
		this.mainscreen = mainscreen;
		this.whisper = whisper;
		this.chatgpt = chatgpt;

		this.setPreferredSize(new Dimension(200, 600));
		this.setBackground(backgroundColor);

		GridLayout layout = new GridLayout(8, 1);
		layout.setVgap(5);
		this.setLayout(layout);
		this.setPreferredSize(new Dimension(200, 600));

		answerPanels = new HashMap<>();

	}

	public void saveToFile() throws IOException {
		File filename = new File("history.txt");
		FileWriter writer = new FileWriter(filename);

		Component[] oldQuestionItem = this.getComponents();
		for (int i = 0; i < oldQuestionItem.length; i++) {
			if (oldQuestionItem[i] instanceof OldQuestion) {
				String questionstr = ((OldQuestion) oldQuestionItem[i]).qToString();
				String answerstr = ((OldQuestion) oldQuestionItem[i]).aToString();
				UUID id = ((OldQuestion) oldQuestionItem[i]).question.getId();
				questionstr = questionstr.replace("\n", "");
				answerstr = answerstr.replace("\n", "");
				String result = id.toString() + "|" + questionstr + "|" + answerstr + "\n";
				writer.write(result);
			}
		}
		writer.close();

	}

	public void loadFromFile() throws IOException {
		this.removeAll();

		BufferedReader reader = new BufferedReader(new FileReader("history.txt"));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\\|");
			if (parts.length == 3) {
				UUID id = UUID.fromString(parts[0]);
				String questionStr = parts[1];
				String answerStr = parts[2];

				Question question = new Question(whisper, id);
				question.setString(questionStr);
				Answer answer = new Answer(chatgpt);
				answer.setString(answerStr);

				OldQuestion oldQuestion = new OldQuestion(question, answer, mainscreen, this);
				this.add(oldQuestion);

				revalidate();
				mainscreen.repaint();
			}
		}
		reader.close();
	}

	public void updateMap(Question question, Answer answer) {
		answerPanels.put(question, answer);

	}

	public void deleteQuestion(OldQuestion question) throws Exception {
		this.remove(question);
		this.saveToFile();
		this.revalidate();
		this.repaint();
	}

	public void clearAll() throws Exception {
		this.removeAll();
		this.saveToFile();
		this.revalidate();
		this.repaint();
	}
}

class OldQuestion extends JPanel {
	JButton displayButton;
	MainScreen mainscreen;
	QuestionHistory questionhistory;

	Answer answer;
	Question question;

	Color black = new Color(0, 0, 0);
	Color white = new Color(255, 255, 255);

	public OldQuestion(Question Q, Answer A, MainScreen mainscreen, QuestionHistory questionhistory) {

		this.mainscreen = mainscreen;
		this.questionhistory = questionhistory;

		this.question = Q;
		this.answer = A;
		this.setPreferredSize(new Dimension(200, 20));
		this.setBackground(white);
		this.setLayout(new GridLayout(1, 2));

		addDisplayButton();
		addListeners();

	}

	public OldQuestion getOldQuestion() {
		return this;
	}

	public String qToString() {
		return question.toString();
	}

	public String aToString() {
		return answer.toString();
	}

	public boolean checkIfOnMain() {
		if ((this.question.getId()).equals(mainscreen.getQuestionOnMain().getId())) {
			return true;
		} else {
			return false;
		}
	}

	public void addDisplayButton() {
		displayButton = new JButton(question.toString());
		displayButton.setPreferredSize(new Dimension(180, 20));
		displayButton.setBackground(white);
		displayButton.setBorder(BorderFactory.createEmptyBorder());
		this.add(displayButton);
	}

	public void addListeners() {
		displayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainscreen.removeAll();
				try {
					questionhistory.loadFromFile();
				} catch (Exception exc) {
					exc.printStackTrace();
				}

				mainscreen.add(question);
				mainscreen.add(answer);
				mainscreen.setQuestionOnMain(question);
			}
		});
	}
}


class Footer extends JPanel {
	JButton askButton;
	JButton stopButton;
	// JButton deleteButton;
	// JButton clearAllButton;

	JLabel recordingLabel;

	Color backgroudColor = new Color(240, 248, 255);
	Border emptyBorder = BorderFactory.createEmptyBorder();

	public Footer() {
		this.setPreferredSize(new Dimension(60, 60));
		this.setBackground(backgroudColor);

		GridLayout layout = new GridLayout(1, 4);
		layout.setHgap(5);
		this.setLayout(layout);

		// deleteButton = new JButton("Delete Question");
		// deleteButton.setFont(new Font("San-serif", Font.ITALIC, 10));
		// this.add(deleteButton);

		// clearAllButton = new JButton("Clear All Question");
		// clearAllButton.setFont(new Font("San-serif", Font.ITALIC, 10));
		// this.add(clearAllButton);

		askButton = new JButton("Start");
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

	public JButton getaskButton() {
		return askButton;
	}

	// public JButton getDeleteButton() {
	// 	return deleteButton;
	// }

	// public JButton getClearButton() {
	// 	return clearAllButton;
	// }

	public JButton getstopButton() {
		return stopButton;
	}

	public JLabel getrecordingLabel() {
		return recordingLabel;
	}
}

class Header extends JPanel {

	Color backgroundColor = new Color(240, 248, 255);

	public Header() {

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
	// private JButton deleteButton;
	// private JButton clearButton;

	private AudioFormat audioFormat;
	private TargetDataLine targetDataLine;
	private JLabel recordingLabel;

	public AppFrame() throws Exception {
		whisper = new Whisper();
		chatgpt = new ChatGPT();

		this.setSize(1000, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		header = new Header();
		footer = new Footer();
		mainscreen = new MainScreen(whisper, chatgpt);
		questionhistory = new QuestionHistory(mainscreen, whisper, chatgpt);

		this.add(header, BorderLayout.NORTH);
		this.add(footer, BorderLayout.SOUTH);
		this.add(mainscreen, BorderLayout.CENTER);
		this.add(questionhistory, BorderLayout.WEST);

		askButton = footer.getaskButton();
		stopButton = footer.getstopButton();

		// deleteButton = footer.getDeleteButton();
		// clearButton = footer.getClearButton();
		recordingLabel = footer.getrecordingLabel();

		audioFormat = getAudioFormat();

		questionhistory.loadFromFile();
		addListeners();
		revalidate();
		repaint();
	}

	public void addListeners() {

		askButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startRecording();
			}
		});

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopRecording();
				try {
					UUID questionId = UUID.randomUUID();
					Question newQuestion = new Question(whisper, questionId);
					String command = whisper.transcribe("recording.wav");
					processVoiceCommand(command);
					if (!command.equalsIgnoreCase("Delete prompt.") 
					&& !command.equalsIgnoreCase("Delete prompt")
					&& !command.equalsIgnoreCase("Clear All.")
					&& !command.equalsIgnoreCase("Clear All")) {
						Answer newAnswer = mainscreen.AskQuestion(newQuestion);
						mainscreen.updateHistory(newQuestion, newAnswer, questionhistory);
					}
					
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		});


	// 	deleteButton.addActionListener(new ActionListener() {
	// 		@Override
	// 		public void actionPerformed(ActionEvent e) {

	// 			mainscreen.removeAll();
	// 			mainscreen.revalidate();
	// 			mainscreen.repaint();

	// 			try {
	// 				OldQuestion toDelete = null;
	// 				for (Component i : questionhistory.getComponents()) {
	// 					System.out.println(((OldQuestion) i).question.getId());
	// 					System.out.println(mainscreen.getQuestionOnMain().getId());
	// 					if (i instanceof OldQuestion
	// 							&& ((OldQuestion) i).question.getId().equals(mainscreen.getQuestionOnMain().getId())) {
	// 						toDelete = (OldQuestion) i;
	// 						break;
	// 					}
	// 				}
	// 				questionhistory.deleteQuestion(toDelete);
	// 			} catch (Exception exc) {
	// 				exc.printStackTrace();
	// 			}
	// 		}
	// 	});

	// 	clearButton.addActionListener(new ActionListener() {
	// 		@Override
	// 		public void actionPerformed(ActionEvent e) {
	// 			try {
	// 				questionhistory.clearAll();
	// 				mainscreen.clearAll();
	// 			} catch (Exception exc) {
	// 				exc.printStackTrace();
	// 			}
	// 		}
	// 	});
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
					// the format of the TargetDataLine
					DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
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

	private void processVoiceCommand(String command) {
		if (command.equalsIgnoreCase("Delete prompt.") || command.equalsIgnoreCase("Delete prompt")) {
			mainscreen.removeAll();
			mainscreen.revalidate();
			mainscreen.repaint();

			try {
				OldQuestion toDelete = null;
				for (Component i : questionhistory.getComponents()) {
					System.out.println(((OldQuestion) i).question.getId());
					System.out.println(mainscreen.getQuestionOnMain().getId());
					if (i instanceof OldQuestion && ((OldQuestion) i).question.getId().equals(mainscreen.getQuestionOnMain().getId())) {
						toDelete = (OldQuestion) i;
						break;
					}
				}
				questionhistory.deleteQuestion(toDelete);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}

		if (command.equalsIgnoreCase("Clear all.") || command.equalsIgnoreCase("Clear all")) {
			try {
				questionhistory.clearAll();
				mainscreen.clearAll();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}

	}

}

class CreateAccountUI extends JFrame {
		
	public JTextField emailField;
	public JPasswordField passwordField;
	public JPasswordField verifyPasswordField;

	public JButton createAccountButton;


	public CreateAccountUI() {

		setTitle("Create Account");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create labels for the text fields
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel verifyPasswordLabel = new JLabel("Verify Password:");

        // Create text fields
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        verifyPasswordField = new JPasswordField(20);

        // Create the create account button
        createAccountButton = new JButton("Create Account");

        // Add action listener to the create account button
        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the values from the text fields
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String verifyPassword = new String(verifyPasswordField.getPassword());

                // Perform validation checks
                if (email.isEmpty() || password.isEmpty() || verifyPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!password.equals(verifyPassword)) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    new Create(email, password);
					try {
						new AppFrame();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
                    //JOptionPane.showMessageDialog(null, "Account created successfully!");
					dispose();
                }
            }
        });

        // Set the layout of the frame
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add components to the frame
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(emailLabel, gbc);

        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(verifyPasswordLabel, gbc);

        gbc.gridx = 1;
        add(verifyPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(createAccountButton, gbc);

        // Display the frame
        setVisible(true);

	}
}

public class AudioGPT {
	public static void main(String[] args) throws Exception {
		//new AppFrame();
		//new CreateAccountUI();
	}
}
