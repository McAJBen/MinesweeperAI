package game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Field {
	
	private static final boolean SUPER_SAFE_FIRST_CLICK = false;
	
	private final int width;
	private final int height;
	private final int totalMines;
	private int minesLeft;
	private boolean created;
	private boolean lost;
	private boolean cheatMode;
	private Block blocks[][];
	
	public Field(int width, int height, int mines, boolean cheatMode) {
		this.width = width;
		this.height = height;
		this.totalMines = mines;
		blocks = new Block[width][height];
		this.cheatMode = cheatMode;
		reset();
	}
	
	public void reset() {
		lost = false;
		minesLeft = totalMines;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				blocks[i][j] = new Block(false);
			}
		}
		created = false;
	}

	public synchronized void paint(Graphics g, Dimension size) {
		final double pixelW = (double)size.width / width;
		final double pixelH = (double)size.height / height;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				blocks[i][j].paint(g, lost, cheatMode,
						(int)(i * pixelW),
						(int)(j * pixelH),
						(int)((i + 1) * pixelW) - (int)(i * pixelW),
						(int)((j + 1) * pixelH) - (int)(j * pixelH));
			}
		}
	}
	
	public void FlagBlock(Point point, Dimension dimension) {
		synchronized (blocks) {
			FlagBlockDirect(pointToCoord(point, dimension));
		}
	}
	
	public void FlagBlockDirect(Point point) {
		synchronized (blocks) {
			if (!blocks[point.x][point.y].isClear) {
				if (blocks[point.x][point.y].isFlagged) {
					blocks[point.x][point.y].isFlagged = false;
					minesLeft++;
				}
				else {
					blocks[point.x][point.y].isFlagged = true;
					minesLeft--;
				}
			}
		}
	}

	public void changeBlock(Point point, Dimension dimension) {
		synchronized (blocks) {
			changeBlockDirect(pointToCoord(point, dimension));
		}
	}
	
	public void changeBlockDirect(Point point) {
		synchronized (blocks) {
			if (!created) {
				created = true;
				plantMines(point);
			}
			clearBlock(point.x, point.y);
		}
	}
	
	public boolean checkWon() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (!blocks[i][j].isClear && !blocks[i][j].isMine) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean checkLost() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (blocks[i][j].isClear && blocks[i][j].isMine) {
					return true;
				}
			}
		}
		return false;
	}
	
	public BlockData[][] getBoard() {
		synchronized (blocks) {
			BlockData[][] bd = new BlockData[width][height];
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					bd[i][j] = blocks[i][j].getBlockData();
				}
			}
			return bd;
		}
	}

	public int getMines() {
		return totalMines;
	}

	public int getMinesLeft() {
		synchronized (blocks) {
			return minesLeft;
		}
	}
	
	private Point pointToCoord(Point p, Dimension size) {
		
		final double pixelW = (double)size.width / width;
		final double pixelH = (double)size.height / height;
		
		int x = (int) (p.x / pixelW);
		int y = (int) (p.y / pixelH);
		
		return new Point(x, y);
	}
	
	private void plantMines(Point point) {
		
		if (SUPER_SAFE_FIRST_CLICK) {
			superSafeSetup(point);
		}
		else {
			safeSetup(point);
		}
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (blocks[i][j].isMine) {
					blocks[i][j].setNearbyMines(0);
				}
				else {
					int neighbors = 0;
					int cellXMax = Math.min(i + 2, width);
					int cellYMax = Math.min(j + 2, height);
					for (int cellX = Math.max(i - 1, 0); cellX < cellXMax; cellX++) {
						for (int cellY = Math.max(j - 1, 0); cellY < cellYMax; cellY++) {
							if (blocks[cellX][cellY].isMine) {
								neighbors++;
							}
						}	
					}
					blocks[i][j].setNearbyMines(neighbors - (blocks[i][j].isMine ? 1 : 0));
				}
			}
		}
	}

	private void safeSetup(Point point) {
		ArrayList<Point> possiblePoints = new ArrayList<Point>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (i != point.x || j != point.y) {
					possiblePoints.add(new Point(i, j));
				}
			}
		}
		Random rand = new Random();
		for (int i = 0; i < totalMines; i++) {
			int index = rand.nextInt(possiblePoints.size());
			blocks[possiblePoints.get(index).x][possiblePoints.get(index).y].setMine(true);
			possiblePoints.remove(index);
		}
	}

	private void superSafeSetup(Point point) {
		ArrayList<Point> possiblePoints = new ArrayList<Point>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {		
				if (Math.abs(i - point.x) > 1 || Math.abs(j - point.y) > 1) {
					possiblePoints.add(new Point(i, j));
				}
			}
		}
		Random rand = new Random();
		for (int i = 0; i < totalMines; i++) {
			int index = rand.nextInt(possiblePoints.size());
			blocks[possiblePoints.get(index).x][possiblePoints.get(index).y].setMine(true);
			possiblePoints.remove(index);
		}
	}
	
	private void clearBlock(int x, int y) {
		if (blocks[x][y].isClear) {
			int flagged = 0;
			int cellXMax = Math.min(x + 2, width);
			int cellYMax = Math.min(y + 2, height);
			for (int cellX = Math.max(x - 1, 0); cellX < cellXMax; cellX++) {
				for (int cellY = Math.max(y - 1, 0); cellY < cellYMax; cellY++) {
					if (blocks[cellX][cellY].isFlagged) {
						flagged++;
					}
				}	
			}
			if (flagged == blocks[x][y].getNearbyMines()) {
				for (int cellX = Math.max(x - 1, 0); cellX < cellXMax; cellX++) {
					for (int cellY = Math.max(y - 1, 0); cellY < cellYMax; cellY++) {
						if (!blocks[cellX][cellY].isClear) {
							clearBlock(cellX, cellY);
						}
					}	
				}
			}
		}
		else if (!blocks[x][y].isFlagged) {
			if (blocks[x][y].clear()) {
				lost = true;
			}
			else if (blocks[x][y].getNearbyMines() == 0) {
				int cellXMax = Math.min(x + 2, width);
				int cellYMax = Math.min(y + 2, height);
				for (int cellX = Math.max(x - 1, 0); cellX < cellXMax; cellX++) {
					for (int cellY = Math.max(y - 1, 0); cellY < cellYMax; cellY++) {
						if (!blocks[cellX][cellY].isClear) {
							clearBlock(cellX, cellY);
						}
					}
				}
			}
		}
	}

	public void save(long totalTime) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(totalTime + ".txt"))) {
			bw.write("Time in milliseconds: " + totalTime + "\n");
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if (blocks[i][j].isMine) {
						bw.write('M');
					}
					else {
						bw.write('-');
					}
				}
				bw.write('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}