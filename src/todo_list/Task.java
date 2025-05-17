package todo_list;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Task extends JPanel {

	private static final long serialVersionUID = 1L;
	private String description;
	private JLabel descLabel;
	private JButton deleteTask;
	private Font labelFont;
	
	Task(String description) {
		setBackground(TodoList.bgCol);
		setLayout(new FlowLayout(FlowLayout.LEFT));
		ImageIcon minus = new ImageIcon("src/asset/minus.png");
		deleteTask = new JButton(minus);
		deleteTask.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
		deleteTask.setContentAreaFilled(false);
		this.description = description;
		descLabel = new JLabel(description);
		labelFont = TodoList.defFont;
		descLabel.setFont(labelFont);
		add(deleteTask, BorderLayout.WEST);
		add(descLabel, BorderLayout.EAST);
	}
	
	public String getDescription() {
		return description;
	}
	
	public JButton getDeleteTaskBtn() {
		return deleteTask;
	}
	
	@Override
	public String toString() {
		return description;
	}
	
}
