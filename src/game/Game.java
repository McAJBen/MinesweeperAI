package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedList;

import javax.swing.JFrame;
import ai.AI;

public class Game {
	private static final String WINDOW_NAME = "Minesweeper";
	private static final Font FONT = new Font("Calibri", Font.PLAIN, 20);
	private static final Dimension SCREEN_SIZE = new Dimension(700, 500);
	private static final int FRAME_TICK = 50;
	private static final int MOVE_TICK = 0;
	
	private final boolean useAI;
	private AI ai;
	private Thread runThread;
	private Thread paintThread;
	private FieldPanel fieldPanel;
	private StatPanel statPanel;
	private GameMouseListener gameMouse;
	private Field field;
	private FieldView fieldView;
	private long startTime;
	private long worstTime = 0;
	private int gamesComplete = 0;
	private int gamesWon = 0;
	
	public Game(boolean runAI, int width, int height, int mines) {
		useAI = runAI;
		gameMouse = new GameMouseListener();
		field = new Field(width, height, mines, useAI);
		JFrame frame = new JFrame(WINDOW_NAME);
		frame.setSize(SCREEN_SIZE.width, SCREEN_SIZE.height);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setFont(FONT);
		if (useAI) {
			fieldView = new FieldView(width, height);
			ai = new AI(width, height, fieldView);
		}
		fieldPanel = new FieldPanel(field, fieldView);
		fieldPanel.addMouseListener(gameMouse);
        frame.add(fieldPanel);
        statPanel = new StatPanel(field, this);
        frame.add(statPanel, BorderLayout.NORTH);
        frame.setVisible(true);
        startTime = System.currentTimeMillis();
	}
	
	public synchronized void start() {
		startPaint();
		runThread = new Thread("runThread") {
			public void run() {
				long nextTime = System.currentTimeMillis() + MOVE_TICK;
				while (!interrupted()) {
					try {
						sleep(Math.max(nextTime - System.currentTimeMillis(), 0));
					} catch (InterruptedException e) {
						break;
					}
					move();
					nextTime += MOVE_TICK;
				}
			}
		};
		runThread.start();
	}
	
	public synchronized void startPaint() {
		paintThread = new Thread("paintThread") {
			@Override
			public void run() {
				long nextTime = System.currentTimeMillis() + FRAME_TICK;
				while (!interrupted()) {
					try {
						sleep(Math.max(nextTime - System.currentTimeMillis(), 0));
					} catch (InterruptedException e) {
						break;
					}
					fieldPanel.repaint();
					statPanel.repaint();
					nextTime += FRAME_TICK;
				}
			}
		};
		paintThread.start();
	}

	public void move() {
		while (gameMouse.hasLeftClick()) {
			field.changeBlock(gameMouse.getPoint(), fieldPanel.getSize());
			checkDone();
		}
		while (gameMouse.hasRightClick()) {
			field.FlagBlock(gameMouse.getPoint(), fieldPanel.getSize());
		}
		if (useAI) {
			LinkedList<Click> clicks = ai.solve(field.getBoard(), field.getMinesLeft());
			for (Click c: clicks) {
				if (c.isLeft()) {
					field.changeBlockDirect(c.getPoint());
				}
				else {
					field.FlagBlockDirect(c.getPoint());
				}
			}
			checkDone();
		}
    }

	public GameMouseListener getGameMouse() {
    	return gameMouse;
    }

	public String getScore() {
		return String.format("Win Rate %.3f%%  [%,d  /  %,d]",
				100.0 * gamesWon / gamesComplete,
				gamesWon, gamesComplete);
	}
	
	private boolean checkDone() {
		if (field.checkWon()) {
			gamesWon++;
			reset();
			return true;
		}
		else if (field.checkLost()) {
			reset();
			return true;
		}
		return false;
	}

	private void reset() {
		gamesComplete++;
		long totalTime = System.currentTimeMillis() - startTime;
		if (totalTime > worstTime) {
			worstTime = totalTime;
			field.save(totalTime);
		}
		startTime = System.currentTimeMillis();
		field.reset();
		if (useAI) {
			ai.reset();
		}
	}

	public long getTime() {
		return System.currentTimeMillis() - startTime;
	}
}