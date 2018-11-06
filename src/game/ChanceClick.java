package game;

import java.awt.Point;

public class ChanceClick extends Click {

	double chance;
	
	public ChanceClick(boolean left, Point p, double chance) {
		super(left, p);
		this.chance = chance;
	}
	
	public double getChance() {
		return chance;
	}
	
	@Override
	public String toString() {
		return super.toString() + "\t" + chance;
	}
}