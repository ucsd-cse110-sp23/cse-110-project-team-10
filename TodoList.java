/**
 * This code was refactored from the original code found at:
 * https://copyassignment.com/to-do-list-app-in-java/
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

class Task extends JPanel {

  JLabel index;
  JTextField taskName;
  JButton doneButton;

  Color gray = new Color(218, 229, 234);
  Color green = new Color(188, 226, 158);

  private boolean markedDone;

  Task() {
    this.setPreferredSize(new Dimension(400, 20)); // set size of task
    this.setBackground(gray); // set background color of task

    this.setLayout(new BorderLayout()); // set layout of task

    markedDone = false;

    index = new JLabel(""); // create index label
    index.setPreferredSize(new Dimension(20, 20)); // set size of index label
    index.setHorizontalAlignment(JLabel.CENTER); // set alignment of index label
    this.add(index, BorderLayout.WEST); // add index label to task

    taskName = new JTextField(""); // create task name text field
    taskName.setBorder(BorderFactory.createEmptyBorder()); // remove border of text field
    taskName.setBackground(gray); // set background color of text field

    this.add(taskName, BorderLayout.CENTER);

    doneButton = new JButton("Done");
    doneButton.setPreferredSize(new Dimension(80, 20));
    doneButton.setBorder(BorderFactory.createEmptyBorder());
    doneButton.setFocusPainted(false);

    this.add(doneButton, BorderLayout.EAST);
  }

  public void changeIndex(int num) {
    this.index.setText(num + ""); // num to String
    this.revalidate(); // refresh
  }

  public JButton getDone() {
    return doneButton;
  }

  public boolean getState() {
    return markedDone;
  }

  public void changeState() {
    this.setBackground(green);
    taskName.setBackground(green);
    markedDone = true;
    revalidate();
  }
}

class List extends JPanel {

  Color backgroundColor = new Color(240, 248, 255);

  List() {
    GridLayout layout = new GridLayout(10, 1);
    layout.setVgap(5); // Vertical gap

    this.setLayout(layout); // 10 tasks
    this.setPreferredSize(new Dimension(400, 560));
    this.setBackground(backgroundColor);
  }

  public void updateNumbers() {
    Component[] listItems = this.getComponents();

    for (int i = 0; i < listItems.length; i++) {
      if (listItems[i] instanceof Task) {
        ((Task) listItems[i]).changeIndex(i + 1);
      }
    }
  }

  public void removeCompletedTasks() {
    for (Component c : getComponents()) {
      if (c instanceof Task) {
        if (((Task) c).getState()) {
          remove(c); // remove the component
          updateNumbers(); // update the indexing of all items
        }
      }
    }
  }

  /**
   * Loads tasks from a file called "tasks.txt"
   * @return an ArrayList of Task
   */
  public ArrayList<Task> loadTasks() {
    List list = new List();
    String line;
    ArrayList<Task> taskList = new ArrayList<>();
    
    try (BufferedReader b = new BufferedReader(new FileReader("tasks.txt"))) {
      while ((line = b.readLine()) != null) {
        System.out.println(line);
        Task newtask = new Task();
        newtask.taskName.setText(line);
        taskList.add(newtask);
        list.add(newtask);
      }
      b.close();
      list.updateNumbers();
      revalidate();

    } catch (IOException e) { 
      e.printStackTrace();
    }
    return taskList;
  }

  /**
   * Saves tasks to a file called "tasks.txt"
   */
  public void saveTasks() {
    Component[] listItems = this.getComponents();

    try (FileWriter fw = new FileWriter("tasks.txt")) {
      
      for (int i = 0; i < listItems.length; i++) {
        if (listItems[i] instanceof Task) {
          String text = (((Task) listItems[i]).taskName).getText();
          fw.write(text);
        }
      }
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class Footer extends JPanel {

  JButton addButton;
  JButton clearButton;
  JButton loadButton;
  JButton saveButton;

  Color backgroundColor = new Color(240, 248, 255);
  Border emptyBorder = BorderFactory.createEmptyBorder();

  Footer() {
    this.setPreferredSize(new Dimension(400, 60));
    this.setBackground(backgroundColor);
    GridLayout layout = new GridLayout(1, 4);
    layout.setVgap(5);
    this.setLayout(layout);

    addButton = new JButton("Add Task"); // add task button
    addButton.setFont(new Font("Sans-serif", Font.ITALIC, 10)); // set font
    this.add(addButton); // add to footer

    clearButton = new JButton("Clear finished"); // clear button
    clearButton.setFont(new Font("Sans-serif", Font.ITALIC, 10)); // set font
    this.add(clearButton); // add to footer

    loadButton = new JButton("Load tasks from file");
    loadButton.setFont(new Font("Sans-serif", Font.ITALIC, 10)); // set font
    this.add(loadButton);

    saveButton = new JButton("Save tasks to file");
    saveButton.setFont(new Font("Sans-serif", Font.ITALIC, 10)); // set font
    this.add(saveButton);
  }

  public JButton getAddButton() {
    return addButton;
  }

  public JButton getClearButton() {
    return clearButton;
  }
  
  public JButton getLoadButton(){
    return loadButton;
  }

  public JButton getSaveButton(){
    return saveButton;
  }
}

class Header extends JPanel {

  Color backgroundColor = new Color(240, 248, 255);

  Header() {
    this.setPreferredSize(new Dimension(400, 60)); // Size of the header
    this.setBackground(backgroundColor);
    JLabel titleText = new JLabel("To Do List"); // Text of the header
    titleText.setPreferredSize(new Dimension(200, 60));
    titleText.setFont(new Font("Sans-serif", Font.BOLD, 20));
    titleText.setHorizontalAlignment(JLabel.CENTER); // Align the text to the center
    this.add(titleText); // Add the text to the header
  }
}

class AppFrame extends JFrame {

  private Header header;
  private Footer footer;
  private List list;

  private JButton addButton;
  private JButton clearButton;
  private JButton loadButton;
  private JButton saveButton;

  AppFrame() {
    this.setSize(400, 600); // 400 width and 600 height
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close on exit
    this.setVisible(true); // Make visible

    header = new Header();
    footer = new Footer();
    list = new List();

    this.add(header, BorderLayout.NORTH); // Add title bar on top of the screen
    this.add(footer, BorderLayout.SOUTH); // Add footer on bottom of the screen
    this.add(list, BorderLayout.CENTER); // Add list in middle of footer and title

    addButton = footer.getAddButton();
    clearButton = footer.getClearButton();
    loadButton = footer.getLoadButton();
    saveButton = footer.getSaveButton();
    list.loadTasks();

    addListeners();
    revalidate();
  }

  public void addListeners() {
    addButton.addActionListener(
      (ActionEvent e) -> {
          Task task = new Task();
          list.add(task); // Add new task to list
          list.updateNumbers(); // Updates the numbers of the tasks
          JButton doneButton = task.getDone();
          doneButton.addActionListener(
            (ActionEvent e2) -> {
                task.changeState(); // Change color of task
                list.updateNumbers(); // Updates the numbers of the tasks
                revalidate(); // Updates the frame
              }
          );
      }
    );

    clearButton.addActionListener(
      (ActionEvent e) -> {
          list.removeCompletedTasks(); // Removes all tasks that are done
          repaint(); // Repaints the list
        }
    );


    loadButton.addActionListener(
      (ActionEvent e) -> {
        ArrayList<Task> tasks = list.loadTasks();
        for (Task task : tasks){
          JButton doneButton = task.getDone();
          doneButton.addMouseListener(e);
        }
      }
    );

    saveButton.addActionListener(
      (ActionEvent e) -> {
        list.saveTasks();
      }
    );
  }


  
}

public class TodoList {

  public static void main(String args[]) {
    new AppFrame(); // Create the frame
  }
}

@interface override {
}
