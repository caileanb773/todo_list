package todo_list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class TodoList extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel newTaskPanel;
	private JPanel currentTasks;
	private JTextField newTaskField;
	private List<Task> taskList;
	private List<ImageIcon> btnIcons;
	static final Color bgCol = Color.white;
	static final Font defFont = new Font("Monospaced", Font.BOLD, 14);
	
	public TodoList() {
		
		// Frame settings
		URL iconURL = TodoList.class.getResource("/asset/icon.png");
		ImageIcon appIcon = new ImageIcon(iconURL);
		setIconImage(appIcon.getImage());
		setTitle("To Do List");
		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				autoSave();
			}
		});
		
		// Init fields
		btnIcons = new ArrayList<ImageIcon>();
		loadImgs();
		taskList = new ArrayList<Task>();
		
		// configure the current task panel
		currentTasks = new JPanel();
		currentTasks.setBorder(BorderFactory.createEmptyBorder(0,5,5,5));
		currentTasks.setLayout(new BoxLayout(currentTasks, BoxLayout.Y_AXIS));
		currentTasks.setAlignmentX(LEFT_ALIGNMENT);
        currentTasks.setBackground(bgCol);

		newTaskPanel = new JPanel(new BorderLayout());
		newTaskPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
        newTaskPanel.setBackground(bgCol);

        // configure the text entry field
		newTaskField = new JTextField(20);
		newTaskField.setBorder(
				BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.black),
				BorderFactory.createEmptyBorder(0,5,0,0)));
		newTaskField.setFont(defFont);

		// Add task button
		JButton addTask = new JButton(btnIcons.get(0));
		addTask.setContentAreaFilled(false);
		addTask.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
		addTask.setFocusable(false);
		addTask.addActionListener(_ -> addTask());
		addTask.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				addTask.setIcon(btnIcons.get(1));
			}
			
			public void mouseExited(MouseEvent e) {
				addTask.setIcon(btnIcons.get(0));
			}
		});
		
        // bind Enter key to newTaskField to call addTask()
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        newTaskField.getInputMap(JComponent.WHEN_FOCUSED).put(enterKey, "addTask");
        newTaskField.getActionMap().put("addTask", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                addTask();
            }
        });
		
		// new task field
		newTaskPanel.add(newTaskField, BorderLayout.CENTER);
		newTaskPanel.add(addTask, BorderLayout.WEST);
		
		// add panels to frame
		add(newTaskPanel, BorderLayout.NORTH);
		add(currentTasks, BorderLayout.CENTER);
		
		//----------------------------------------------------------------------
		//
		// 		menu, currently not used
		//		JMenuBar menu = new JMenuBar();
		//		JMenu file = new JMenu("File");
		//		menu.add(file);
		//		
		//		JMenuItem save = new JMenuItem("Save");
		//		file.add(save);
		//		save.addActionListener(_ -> saveTasks());
		//		
		//		JMenuItem load = new JMenuItem("Load");
		//		file.add(load);
		//		load.addActionListener(_ -> loadTasks());
		//		setJMenuBar(menu);
		//
		//----------------------------------------------------------------------

		// final frame settings
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		
		// load any tasks from previous launch
		loadTasks();
	}
	
	public void autoSave() {
		saveTasks();
		dispose();
	}
	
	public void loadImgs() {		
		
		// load image URLs
		URL plusDefURL = TodoList.class.getResource("/asset/plus.png");
		URL plusSelURL = TodoList.class.getResource("/asset/plusSelected.png");
		URL minusDefURL = TodoList.class.getResource("/asset/minus.png");
		URL minusSelURL = TodoList.class.getResource("/asset/minusSelected.png");

		// plus default and selected images
		ImageIcon plusDef = new ImageIcon(plusDefURL);
		btnIcons.add(plusDef);
		ImageIcon plusSel = new ImageIcon(plusSelURL);
		btnIcons.add(plusSel);
		
		// minus def and selected images
		ImageIcon minusDef = new ImageIcon(minusDefURL);
		btnIcons.add(minusDef);
		ImageIcon minusSel = new ImageIcon(minusSelURL);
		btnIcons.add(minusSel);
	}
	
	public void addTask() {
		
		// get the text from the textfield
		String taskName = newTaskField.getText();
		if (taskName.isEmpty()) {
			return;
		}
		System.out.println("New task added: " + taskName);
		newTaskField.setText("");
		
		// make new task object
		Task newTask = new Task(taskName);
		JButton b = newTask.getDeleteTaskBtn();
		b.addActionListener(_ -> removeTask(newTask));
		b.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				b.setIcon(btnIcons.get(3));
			}
			
			public void mouseExited(MouseEvent e) {
				b.setIcon(btnIcons.get(2));
			}
		});
		
		// add task to the task panel
		newTask.setAlignmentX(LEFT_ALIGNMENT);
		taskList.add(newTask);
		displayTasks();
	}
	
	public void removeTask(Task t) {
		System.out.println("Removing task...");
		taskList.remove(t);
		displayTasks();
	}
	
	public void displayTasks() {
		if (currentTasks.getComponents() != null) {
			currentTasks.removeAll();
			for (Task t : taskList) {
				currentTasks.add(t);
			}
			pack();
		} else {
			System.out.println("Task list wasn't initialized properly.");
		}
	}
	
	public void saveTasks() {
		System.out.println("Saving task list...");
		
		String fileName = "default";
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".txt"))){
			for (Task t : taskList) {
				writer.write(t.toString());
				writer.write("\n");
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public void loadTasks() {
		System.out.println("Loading task list...");
		
		try (BufferedReader reader = new BufferedReader(new FileReader("default.txt"))){
			String line;
			
			while ((line = reader.readLine()) != null) {
				Task temp = new Task(line);
				temp.setAlignmentX(LEFT_ALIGNMENT);
				JButton b = temp.getDeleteTaskBtn();
				b.addActionListener(_ -> removeTask(temp));
				b.addMouseListener(new MouseAdapter() {
					public void mouseEntered(MouseEvent e) {
						b.setIcon(btnIcons.get(3));
					}
					
					public void mouseExited(MouseEvent e) {
						b.setIcon(btnIcons.get(2));
					}
				});
				taskList.add(temp);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File could not be found.");
		} catch (IOException e) {
			System.out.println(e);
		}
		
		displayTasks();
	}
	
}
