package game;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Block {
	
	private static final Dimension FONT_SIZE = new Dimension(8, 13);
	private static final Color BLANK_COLOR = new Color(185, 185, 185);
	private static BufferedImage blank;
	private static BufferedImage flag;
	private static BufferedImage hitMine;
	private static BufferedImage mine;
	
	private byte nearbyMines;
	boolean isMine;
	boolean isClear;
	boolean isFlagged;
	
	public Block(boolean isMine) {
		this.isMine = isMine;
	}
	
	public void setMine(boolean isMine) {
		this.isMine = isMine;
		isClear = false;
		isFlagged = false;
		nearbyMines = 0;
	}

	public static void loadImages() {
		try {
			blank = ImageIO.read(Block.class.getResource("/resources/blank.png"));
			flag = ImageIO.read(Block.class.getResource("/resources/flag.png"));
			hitMine = ImageIO.read(Block.class.getResource("/resources/hitMine.png"));
			mine = ImageIO.read(Block.class.getResource("/resources/mine.png"));
		} catch (IOException e) {
			System.exit(-1);
		}
	}

	public void paint(Graphics g, boolean showMines, boolean cheat, int x, int y, int width, int height) {
		if (isClear) {
			paintClear(g, x, y, width, height);
		}
		else if (isFlagged) {
			g.drawImage(flag, x, y, width, height, null);
		}
		else if (showMines && isMine) {
			g.drawImage(mine, x, y, width, height, null);
		}
		else {
			g.drawImage(blank, x, y, width, height, null);
		}
		paintBorder(g, x, y, width, height);
		if (cheat && isMine) {
			g.setColor(Color.RED);
			g.fillRect(x, y, width / 4, height / 4);
		}
	}
	
	private void paintClear(Graphics g, int x, int y, int width, int height) {
		if (isMine) {
			g.drawImage(hitMine, x, y, width, height, null);
		}
		else {
			g.setColor(BLANK_COLOR);
			g.fillRect(x, y, width, height);
			if (nearbyMines != 0) {
				g.setColor(getNumberColor(nearbyMines));
				g.drawString(String.format("%d", nearbyMines),
					x + (width - FONT_SIZE.width) / 2,
					y + (height + FONT_SIZE.height) / 2);
			}
		}
	}
	
	private void paintBorder(Graphics g, int x, int y, int width, int height) {
		g.setColor(Color.GRAY);
		g.drawLine(x, y, 
				x, x + width);
		g.drawLine(x, y, 
				x + width, y);
	}
	
	public void setNearbyMines(int nearbyMines) {
		this.nearbyMines = (byte) nearbyMines;
	}

	private static Color getNumberColor(byte i) {
		switch(i) {
		case 1:
			return Color.BLUE;
		case 2:
			return Color.GREEN;
		case 3:
			return Color.RED;
		case 4:
			return Color.DARK_GRAY;
		case 5:
			return Color.MAGENTA;
		case 6:
			return Color.CYAN;
		case 7:
			return Color.PINK;
		case 8:
			return Color.BLACK;
		default:
			return Color.MAGENTA;
		}
	}

	public boolean clear() {
		isClear = true;
		return isMine;
	}

	public byte getNearbyMines() {
		return nearbyMines;
	}
	
	public BlockData getBlockData() {
		if (isClear) {
			return BlockData.values()[nearbyMines];
		}
		else if (isFlagged) {
			return BlockData.FLAGGED;
		}
		else {
			return BlockData.UNKNOWN;
		}
	}
}
