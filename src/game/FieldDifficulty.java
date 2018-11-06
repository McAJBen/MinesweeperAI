package game;

import javax.swing.JOptionPane;

public class FieldDifficulty {
	
	private static final String WINDOW_NAME = "Minesweeper";
	private static final String SIZE_QUESTION = "Please select field size";
	private static final String[] FIELD_SIZE = {
		"Beginner", "Intermediate", "Expert", "Custom"
	};
	private static final FieldDifficulty BEGINNER = new FieldDifficulty(9, 9, 10);
	private static final FieldDifficulty INTERMEDIATE = new FieldDifficulty(16, 16, 40);
	private static final FieldDifficulty EXPERT = new FieldDifficulty(30, 16, 99);
	
	public final int width;
	public final int height;
	public final int mines;
	
	public FieldDifficulty(int width, int height, int mines) {
		this.width = width;
		this.height = height;
		this.mines = mines;
	}

	public static FieldDifficulty getSize() {
		int choice = JOptionPane.showOptionDialog(null, SIZE_QUESTION, WINDOW_NAME,
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null, FIELD_SIZE, 0);
		
		switch (choice) {
			case 0:
				return BEGINNER;
			case 1:
				return INTERMEDIATE;
			case 2:
				return EXPERT;
		}
		return getCustomSize();
	}
	
	private static FieldDifficulty getCustomSize() {
		int w, h, m;
		do {
			w = Integer.parseInt(JOptionPane.showInputDialog("Please enter field width", BEGINNER.width));
			h = Integer.parseInt(JOptionPane.showInputDialog("Please enter field height", BEGINNER.height));
			m = Integer.parseInt(JOptionPane.showInputDialog("Please enter number of mines", BEGINNER.mines));
		} while (w * h <= m);
		return new FieldDifficulty(w, h, m);
	}
	
}
