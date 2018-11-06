package game;

import javax.swing.JOptionPane;

public class Main {
	
	private static final String WINDOW_NAME = "Minesweeper";
	private static final String AI_QUESTION = "Do you want to play, or watch the AI play?";
	
	private static final String[] STANDARD_V_AI = {
		"Standard", "AI controlled"
	};
	
	
	public static void main(String[] args) {
		
		Block.loadImages();
        
		boolean runAI = JOptionPane.showOptionDialog(null, AI_QUESTION, WINDOW_NAME,
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null, STANDARD_V_AI, 0) == 1;
		
		FieldDifficulty fieldDifficulty = FieldDifficulty.getSize();
		
		Game game = new Game(WINDOW_NAME, fieldDifficulty, runAI);
		game.start();
	}
	
	
	
}
