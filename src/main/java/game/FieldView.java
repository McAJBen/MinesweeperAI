package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import ai.Rule;
import ai.RuleSet;

public class FieldView {

	int width;
	int height;
	
	private static enum Step {
		NEEDED, FLAGGED, SOLVED, UNKNOWN
	}
	private Step[][] steps;
	private byte[][] nearby;
	
	public FieldView(int width, int height) {
		this.width = width;
		this.height = height;
		steps = new Step[width][height];
		nearby = new byte[width][height];
		reset();
	}
	
	public void reset() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				steps[i][j] = Step.UNKNOWN;
				nearby[i][j] = 0;
			}
		}
	}

	public void paint(Graphics g, Dimension size) {
		final double pixelW = (double)size.width / width;
		final double pixelH = (double)size.height / height;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				g.setColor(getColor(steps[i][j]));
				int x = (int)(i * pixelW);
				int y = (int)(j * pixelH);
				int w = (int)((i + 1) * pixelW) - (int)(i * pixelW);
				int h = (int)((j + 1) * pixelH) - (int)(j * pixelH);
				g.drawRect(1 + x, 1 + y, w - 1, h - 1);
				g.drawRect(2 + x, 2 + y, w - 3, h - 3);
			}
		}
	}
	
	public void checkMemory(BlockData[][] blockData) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (steps[i][j] == Step.NEEDED) {
					if (isSolved(blockData, i, j)) {
						steps[i][j] = Step.SOLVED;
					}
					else {
						nearby[i][j] = (byte) blockData[i][j].getNearbyMines();
					}
				}
				else if (steps[i][j] == Step.UNKNOWN) {
					if (blockData[i][j].isFlagged()) {
						steps[i][j] = Step.FLAGGED;
					}
					else if (blockData[i][j].isClear()) {
						if (isSolved(blockData, i, j)) {
							steps[i][j] = Step.SOLVED;
						}
						else {
							steps[i][j] = Step.NEEDED;
							nearby[i][j] = (byte) blockData[i][j].getNearbyMines();
						}
					}
				}
			}
		}
	}
	
	public RuleSet getRuleSet(int minesLeft) {
		LinkedList<Rule> rules = new LinkedList<>();
		LinkedList<Point> unknown = new LinkedList<>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (steps[i][j] == Step.NEEDED) {
					rules.add(new Rule(
							getUnknownAround(i, j),
							getMinesAround(i, j)));
				}
				else if (steps[i][j] == Step.UNKNOWN) {
					unknown.add(new Point(i, j));
				}
			}
		}
		rules.add(new Rule(unknown, minesLeft));
		RuleSet ruleSet = new RuleSet(rules);
		return ruleSet;
	}
	
	public byte getNearby(int i, int j) {
		return nearby[i][j];
	}
	
	private Color getColor(Step step) {
		switch (step) {
		case FLAGGED:
			return Color.RED;
		case SOLVED:
			return Color.GREEN;
		case NEEDED:
			return Color.ORANGE;
		default:
		case UNKNOWN:
			return Color.BLACK;
		}
	}

	private int getMinesAround(int i, int j) {
		int mines = nearby[i][j];
		int cellXMax = Math.min(i + 1, width - 1);
		int cellYMax = Math.min(j + 1, height - 1);
		for (int cellX = Math.max(i - 1, 0); cellX <= cellXMax; cellX++) {
			for (int cellY = Math.max(j - 1, 0); cellY <= cellYMax; cellY++) {
				if (steps[cellX][cellY] == Step.FLAGGED) {
					mines--;
				}
			}
		}
		return mines;
	}

	private LinkedList<Point> getUnknownAround(int i, int j) {
		LinkedList<Point> near = new LinkedList<>();
		int cellXMax = Math.min(i + 1, width - 1);
		int cellYMax = Math.min(j + 1, height - 1);
		for (int cellX = Math.max(i - 1, 0); cellX <= cellXMax; cellX++) {
			for (int cellY = Math.max(j - 1, 0); cellY <= cellYMax; cellY++) {
				if (steps[cellX][cellY] == Step.UNKNOWN) {
					near.add(new Point(cellX, cellY));
				}
			}
		}
		return near;
	}

	private boolean isSolved(BlockData[][] blockData, int i, int j) {
		int cellXMax = Math.min(i + 1, width - 1);
		int cellYMax = Math.min(j + 1, height - 1);
		for (int cellX = Math.max(i - 1, 0); cellX <= cellXMax; cellX++) {
			for (int cellY = Math.max(j - 1, 0); cellY <= cellYMax; cellY++) {
				if (blockData[cellX][cellY].isUnknown()) {
					return false;
				}
			}
		}
		return true; 
	}
}
