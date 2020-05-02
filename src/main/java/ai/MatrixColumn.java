package ai;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;

public class MatrixColumn {
	
	final boolean[] column;
	private LinkedList<Point> points;
	
	public MatrixColumn(Point p, boolean[] column) {
		points = new LinkedList<>();
		points.add(p);
		this.column = column;
	}
	
	public void add(MatrixColumn mc) {
		points.addAll(mc.points);
	}
	
	public int size() {
		return points.size();
	}
	
	public LinkedList<Point> getPoints() {
		return points;
	}
	
	public boolean equalsColumn(MatrixColumn matrixColumn) {
		return Arrays.equals(column, matrixColumn.column);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass().equals(this.getClass())) {
			MatrixColumn mc = (MatrixColumn) obj;
			return Arrays.equals(column, mc.column) && points.equals(mc.points);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		String s = "";
		for (boolean b: column) {
			s += b ? "X" : "-";
		}
		s += ":";
		for (Point p: points) {
			s += p.x + "," + p.y + " ";
		}
		return s;
	}
}