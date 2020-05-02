package ai;

import java.awt.Point;
import java.util.LinkedList;

public class BasicRule {
	
	private final int mines;
	private final boolean[] bools;
	
	public BasicRule(boolean[] b, int m) {
		mines = m;
		bools = b;
	}
	
	@Override
	public String toString() {
		String s = mines + ":";
		for (boolean b: bools) {
			s += b ? "T" : "F";
		}
		
		return s;
	}

	public int count(LinkedList<Integer> toBeDone) {
		int count = 0;
		for (int i: toBeDone) {
			if (bools[i]) {
				count++;
			}
		}
		return count;
	}

	public static boolean follows(LinkedList<BasicRule> basicRules, LinkedList<Integer> toBeDone, int[] b) {
		for (BasicRule br: basicRules) {
			if (!br.follows(toBeDone, b)) {
				return false;
			}
		}
		return true;
	}

	private boolean follows(LinkedList<Integer> toBeDone, int[] b) {
		int m = 0;
		for (int i = 0; i < bools.length; i++) {
			if (bools[i] && !toBeDone.contains(i)) {
				m += b[i];
			}
		}
		return m <= mines;
	}

	public int getDetermineIndex(LinkedList<Integer> toBeDone) {
		for (int i: toBeDone) {
			if (bools[i]) {
				return i;
			}
		}
		return -1;
	}

	public int getMines() {
		return mines;
	}
	
	public boolean uses(int i) {
		return bools[i];
	}
	
	public int length() {
		return bools.length;
	}

	public int getMines(LinkedList<Integer> toBeDone, int[] is) {
		int count = 0;
		for (int i = 0; i < bools.length; i++) {
			if (bools[i] && !toBeDone.contains(i)) {
				count += is[i];
			}
		}
		return count;
	}

	public void print(LinkedList<LinkedList<Point>> points) {
		for (int i = 0; i < bools.length; i++) {
			if (bools[i]) {
				System.out.print(points.get(i).size() + " ");
			}
			else {
				System.out.print("-");
				int blanks = (points.get(i).size() + "").length();
				for (int ch = 0; ch < blanks; ch++) {
					System.out.print(" ");
				}
			}
		}
		System.out.println(" : " + mines);
	}
	
	
	
	
	
	
}
