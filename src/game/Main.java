package game;

import javax.swing.JOptionPane;

public class Main {
	
	private static final String WINDOW_NAME = "Minesweeper";
	private static final String AI_QUESTION = "Do you want to play, or watch the AI play?";
	private static final String SIZE_QUESTION = "Please select field size";
	private static final String[] STANDARD_V_AI = {
		"Standard", "AI controlled"
	};
	private static final String[] FIELD_SIZE = {
		"Beginner", "Intermediate", "Expert", "Custom"
	};
	
	public static void main(String[] args) {
		
		Block.loadImages();
        
		boolean runAI = JOptionPane.showOptionDialog(null, AI_QUESTION, WINDOW_NAME,
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null, STANDARD_V_AI, 0) == 1;
		
		int choice = JOptionPane.showOptionDialog(null, SIZE_QUESTION, WINDOW_NAME,
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null, FIELD_SIZE, 0);
		
		int width, height, mines;
		
		switch (choice) {
			case 0:
				width = 9;
				height = 9;
				mines = 10;
				break;
			case 1:
				width = 16;
				height = 16;
				mines = 40;
				break;
			case 2:
				width = 30;
				height = 16;
				mines = 99;
				break;
			default:
				do {
					width = Integer.parseInt(JOptionPane.showInputDialog("Please enter field width", 9));
					height = Integer.parseInt(JOptionPane.showInputDialog("Please enter field height", 9));
					mines = Integer.parseInt(JOptionPane.showInputDialog("Please enter number of mines", 10));
				} while (width * height <= mines);
		}
		
		Game game = new Game(runAI, width, height, mines);
		game.start();
	}
}
