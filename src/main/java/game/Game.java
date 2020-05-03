package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JFrame;
import ai.AI;

public class Game {
	private static final String WINDOW_NAME = "Minesweeper";
	private static final Font FONT = new Font("Calibri", Font.PLAIN, 20);
	private static final Dimension SCREEN_SIZE = new Dimension(700, 500);
	private static final int FRAME_TICK = 50;
	private static final int MOVE_TICK = 0;
	
	private final GameController controller;
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
	
	public Game(GameController controller, GameConfig config) {
		this.controller = controller;
		field = new Field(config, controller == GameController.AI);
		JFrame frame = new JFrame(WINDOW_NAME);
		frame.setSize(SCREEN_SIZE.width, SCREEN_SIZE.height);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setLayout(new BorderLayout());
		frame.setFont(FONT);
		if (controller == GameController.AI) {
			fieldView = new FieldView(config.getWidth(), config.getHeight());
			ai = new AI(config.getWidth(), config.getHeight(), fieldView);
		}
		fieldPanel = new FieldPanel(field, fieldView);
		gameMouse = new GameMouseListener(fieldPanel, config);
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
		List<Click> clicks = null;

		if (controller == GameController.Player) {
			clicks = gameMouse.getClicks();
		} else if (controller == GameController.AI) {
			clicks = ai.solve(field.getBoard(), field.getMinesLeft());
		}

		if (clicks != null && !clicks.isEmpty()) {
			for (Click c : clicks) {
				if (c.getClickType() == ClickType.REVEAL) {
					field.changeBlock(c.getPoint());
				} else if (c.getClickType() == ClickType.FLAG) {
					field.flagBlock(c.getPoint());
				}
			}
			checkDone();
		}
    }

	public String getScore() {
		return String.format("Win Rate %.3f%%  [%,d  /  %,d]",
				100.0 * gamesWon / gamesComplete,
				gamesWon, gamesComplete);
	}
	
	private void checkDone() {
		if (field.checkWon()) {
			gamesWon++;
			reset();
		}
		else if (field.checkLost()) {
			reset();
		}
	}

	private void reset() {
		gamesComplete++;
		long totalTime = System.currentTimeMillis() - startTime;
		if (totalTime > worstTime) {
			worstTime = totalTime;
			field.save(totalTime);
		}

//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		startTime = System.currentTimeMillis();
		field.reset();
		if (controller == GameController.AI) {
			ai.reset();
		}
	}

	public long getTime() {
		return System.currentTimeMillis() - startTime;
	}
}