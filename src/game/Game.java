package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedList;

import javax.swing.JFrame;
import ai.AI;

public class Game {
	private static final Font FONT = new Font("Calibri", Font.PLAIN, 20);
	private static final Dimension SCREEN_SIZE = new Dimension(600, 600);
	private static final int FRAME_TICK = 15;
	private static final int MOVE_TICK = 0;
	
	private final boolean useAI;
	private Thread runThread;
	private Thread paintThread;
	private FieldPanel fieldPanel;
	private StatPanel statPanel;
	private GameMouseListener gameMouse;
	private Field field;
	private AI ai;
	private int gamesComplete = 0;
	private int gamesWon = 0;
	private double accChance = 0;
	private long longestWait = 0;
	private long currentWait = 0;
	
	private long winWait = 0;
	private long loseWait = 0;
	
	public Game(String name, FieldDifficulty fieldDifficulty, boolean runAI) {
		useAI = runAI;
		if (useAI) {
			ai = new AI(fieldDifficulty);
		}
		gameMouse = new GameMouseListener();
		field = new Field(fieldDifficulty);
		JFrame frame = new JFrame(name);
		frame.setSize(SCREEN_SIZE.width, SCREEN_SIZE.height);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setFont(FONT);
		fieldPanel = new FieldPanel(field, ai);
		fieldPanel.addMouseListener(gameMouse);
        frame.add(fieldPanel);
        statPanel = new StatPanel(field, this);
        frame.add(statPanel, BorderLayout.NORTH);
        frame.setVisible(true);
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
			long time = System.nanoTime();
			LinkedList<Click> aiClicks = ai.getClicks();
			checkTime(System.nanoTime() - time);
			//System.out.println("found: " + aiClicks.size());
			for (Click c: aiClicks) {
				//System.out.println("\t" + c);
				if (c.isLeft()) {
					field.changeBlockDirect(c.getPoint());
					if (checkDone()) {
						break;
					}
				}
				else {
					field.FlagBlockDirect(c.getPoint());
				}
			}
			ai.updateMemory(field.getBoard(), field.getMinesLeft());
		}
    }
	
    private void checkTime(long time) {
    	if (time > longestWait) {
			longestWait = time;
		}
    	currentWait += time;
	}

	public GameMouseListener getGameMouse() {
    	return gameMouse;
    }

	public String getScore() {
		return String.format("Win Rate %.2f%%  [%,d  /  %,d]",(double)gamesWon / gamesComplete * 100.0, gamesWon, gamesComplete);
	}
	
	private boolean checkDone() {
		if (field.checkWon()) {
			gamesWon++;
			winWait += currentWait;
			reset();
			return true;
		}
		else if (field.checkLost()) {
			loseWait += currentWait;
			reset();
			return true;
		}
		return false;
	}

	private void reset() {
		gamesComplete++;
		field.reset();
		currentWait = 0;
		if (useAI) {
			accChance += ai.getChance();
			ai.reset();
		}
		if (gamesComplete % 1_000 == 0) {
			System.out.println(getStats());
		}
	}
	
	private String getStats() {
		return String.format("%6d%6.2f%,10d%,6d%,6d%,6d",
				gamesComplete,
				accChance / gamesComplete * 100,
				longestWait / 1_000_000,
				winWait / gamesWon / 1_000_000,
				(winWait + loseWait) / gamesComplete / 1_000_000,
				loseWait / (gamesComplete - gamesWon) / 1_000_000);
	}

	public void copyField(Game game) {
		field.copy(game.field);
		
	}

	public boolean finished() {
		return !field.hasCreated();
	}
}