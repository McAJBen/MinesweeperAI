package ai;

import java.awt.Point;
import java.util.LinkedList;

public class Rule {
	
	private int mines;
	private LinkedList<Point> points;
	
	public Rule(LinkedList<Point> points, int mines) {
		this.points = points;
		this.mines = mines;
	}
	
	public Rule subtract(Rule rule) {
		LinkedList<Point> newPoints = new LinkedList<>();
		newPoints.addAll(points);
		newPoints.removeAll(rule.points);
		return new Rule(newPoints, mines - rule.mines);
	}
	
	public void subtractBy(Rule rule) {
		points.removeAll(rule.points);
		mines -= rule.mines;
	}
	
	public boolean contains(Point p) {
		return points.contains(p);
	}

	public boolean contains(Rule r) {
		return points.containsAll(r.points);
	}
	
	public boolean isEmpty() {
		return points.isEmpty();
	}
	
	public LinkedList<Point> getPoints() {
		return points;
	}

	public int getMines() {
		return mines;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass().equals(this.getClass())) {
			Rule c = (Rule) obj;
			if (c.mines == mines) {
				if (c.points.containsAll(points) && points.containsAll(c.points)) {
					return true;
				}
			}
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		String s = mines + ":";
		for (Point p: points) {
			s += "\t" + p.x + "," + p.y;
		}
		return s;
	}
}