package ai;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;
import game.BlockData;
import game.ChanceClick;
import game.Click;
import game.FieldDifficulty;


public class AI {
	
	private Memory[][] memory;
	private int minesLeft;
	private FieldDifficulty fieldDifficulty;
	private double chance; // chance of hitting a mine
	private boolean firstClick;
	
	public AI(FieldDifficulty fd) {
		this.fieldDifficulty = fd;
		memory = new Memory[fieldDifficulty.width][fieldDifficulty.height];
		reset();
	}
	
	public synchronized void reset() {
		firstClick = true;
		chance = 0;
		for (int i = 0; i < memory.length; i++) {
			for (int j = 0; j < memory[i].length; j++) {
				memory[i][j] = new Memory();
			}
		}
	}
	
	public void updateMemory(BlockData[][] blockDatas, int minesLeft) {
		checkMemory(blockDatas);
		this.minesLeft = minesLeft;
	}
	
	public LinkedList<Click> getClicks() {
		LinkedList<Click> clickPoints = new LinkedList<>();
		
		if (firstClick) {
			clickPoints.add(new Click(true, 0, 0));
			firstClick = false;
			return clickPoints;
		}
		
		RuleSet ruleSet = getRuleSet(minesLeft);
		
		clickPoints.addAll(ruleSet.solve());
		if (!clickPoints.isEmpty()) {
			return clickPoints;
		}
		
		MatrixHandler matrix = new MatrixHandler(ruleSet.getRules());
		
		LinkedList<Click> clicks = matrix.solve();
		
		clickPoints.addAll(clicks);
		
		for (Click c: clickPoints) {
			if (c.getClass().equals(ChanceClick.class)) {
				addChance(((ChanceClick)c).getChance());
			}
		}
		return clickPoints;
	}
	
	public void paint(Graphics g, Dimension size) {
		final double pixelW = (double)size.width / fieldDifficulty.width;
		final double pixelH = (double)size.height / fieldDifficulty.height;
		
		
		for (int i = 0; i < fieldDifficulty.width; i++) {
			for (int j = 0; j < fieldDifficulty.height; j++) {
				memory[i][j].paint(g,
						(int)(i * pixelW),
						(int)(j * pixelH),
						(int)((i + 1) * pixelW) - (int)(i * pixelW),
						(int)((j + 1) * pixelH) - (int)(j * pixelH));
			}
		}
	}
	
	public double getChance() {
		return chance;
	}
	
	private void addChance(double ch) {
		chance += ch * (1 - chance);
	}

	private RuleSet getRuleSet(int minesLeft) {
		LinkedList<Rule> rules = new LinkedList<>();
		for (int i = 0; i < memory.length; i++) {
			for (int j = 0; j < memory[i].length; j++) {
				if (memory[i][j].isNeeded()) {
					rules.add(new Rule(
							getUnknownAround(i, j),
							getMinesAround(i, j)));
				}
			}
		}
		rules.add(new Rule(getAllUnknown(), minesLeft));
		RuleSet ruleSet = new RuleSet(rules);
		return ruleSet;
	}

	private LinkedList<Point> getAllUnknown() {
		LinkedList<Point> p = new LinkedList<>();
		for (int i = 0; i < memory.length; i++) {
			for (int j = 0; j < memory[i].length; j++) {
				if (memory[i][j].isUnknown() || memory[i][j].isCouldBeFound()) {
					p.add(new Point(i, j));
				}
			}
		}
		return p;
	}

	private int getMinesAround(int i, int j) {
		int mines = memory[i][j].getMinesNearby();
		for (int cellX = Math.max(i - 1, 0); cellX < Math.min(i + 2, memory.length); cellX++) {
			for (int cellY = Math.max(j - 1, 0); cellY < Math.min(j + 2, memory[i].length); cellY++) {
				if (i != cellX || j != cellY) {
					if (memory[cellX][cellY].isFlagged()) {
						mines--;
					}
				}
			}
		}
		return mines;
	}

	private LinkedList<Point> getUnknownAround(int i, int j) {
		LinkedList<Point> near = new LinkedList<>();
		for (int cellX = Math.max(i - 1, 0); cellX < Math.min(i + 2, memory.length); cellX++) {
			for (int cellY = Math.max(j - 1, 0); cellY < Math.min(j + 2, memory[i].length); cellY++) {
				if (i != cellX || j != cellY) {
					if (memory[cellX][cellY].isCouldBeFound()) {
						near.add(new Point(cellX, cellY));
					}
				}
			}
		}
		return near;
	}
	
	private void checkMemory(BlockData[][] blockDatas) {
		for (int i = 0; i < memory.length; i++) {
			for (int j = 0; j < memory[i].length; j++) {
				/*
				 * UNKNOWN		know nothing about
				 * NEEDED		has mines not flagged nearby
				 * COULD_BE_FOUND is near needed
				 * UNRELATED unknown not near could be found
				 */
				if (!memory[i][j].isDone()) {
					if (blockDatas[i][j].isFlagged()) {
						memory[i][j].setFlagged();
					}
					else if (!blockDatas[i][j].isClear()) {
						if (isCouldBeFound(blockDatas, i, j)) {
							memory[i][j].setCouldBeFound();
						}
						else if (isNearCouldBeFound(blockDatas, i, j)) {
							memory[i][j].setUnknown();
						}
					}
					else if (isSolved(blockDatas, i, j)) {
						memory[i][j].setSolved();
					}
					else {
						memory[i][j].setNeeded();
						memory[i][j].setMinesNearby(blockDatas[i][j].getNearbyMines());
						
					}
				}
			}
		}
	}

	private boolean isNearCouldBeFound(BlockData[][] blockDatas, int i, int j) {
		int cellXMax = Math.min(i + 3, blockDatas.length);
		int cellYMax = Math.min(j + 3, blockDatas[0].length);
		for (int cellX = Math.max(i - 2, 0); cellX < cellXMax; cellX++) {
			for (int cellY = Math.max(j - 2, 0); cellY < cellYMax; cellY++) {
				if (blockDatas[cellX][cellY].isClear()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isCouldBeFound(BlockData[][] blockDatas, int i, int j) {
		int cellXMax = Math.min(i + 2, blockDatas.length);
		int cellYMax = Math.min(j + 2, blockDatas[0].length);
		for (int cellX = Math.max(i - 1, 0); cellX < cellXMax; cellX++) {
			for (int cellY = Math.max(j - 1, 0); cellY < cellYMax; cellY++) {
				if (blockDatas[cellX][cellY].isClear()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isSolved(BlockData[][] blockDatas, int i, int j) {
		int cellXMax = Math.min(i + 2, blockDatas.length);
		int cellYMax = Math.min(j + 2, blockDatas[0].length);
		for (int cellX = Math.max(i - 1, 0); cellX < cellXMax; cellX++) {
			for (int cellY = Math.max(j - 1, 0); cellY < cellYMax; cellY++) {
				if (blockDatas[cellX][cellY].isUnknown()) {
					return false;
				}
			}
		}
		return true; 
	}
}