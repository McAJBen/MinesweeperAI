package ai;
import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import game.BlockData;
import game.Click;
import game.ClickType;
import game.FieldView;

public class AI {
	
	private FieldView fieldView;
	
	public AI(int width, int height, FieldView fieldView) {
		this.fieldView = fieldView;
		reset();
	}
	
	public LinkedList<Click> solve(BlockData[][] blockData, int minesLeft) {
		fieldView.checkMemory(blockData);
		LinkedList<Click> clickPoints = new LinkedList<>();
		
		if (isFirstClick(blockData)) {
			clickPoints.add(new Click(ClickType.REVEAL, 0, 0));
			return clickPoints;
		}
		
		RuleSet ruleSet = fieldView.getRuleSet(minesLeft);
		
		clickPoints.addAll(ruleSet.solve());
		if (!clickPoints.isEmpty()) {
			return clickPoints;
		}
		
		clickPoints.addAll(findProbabilities(ruleSet.getRules()));
		return clickPoints;
	}

	private LinkedList<Click> findProbabilities(LinkedList<Rule> rules) { // TODO realllyy clean this up

		LinkedList<BasicRule> rows;
		LinkedList<LinkedList<Point>> points;
		
		int[] minesPerRow = new int[rules.size()];
		
		LinkedList<Point> pts = new LinkedList<>();
		
		for (int i = 0; i < rules.size(); i++) {
			for (Point p: rules.get(i).getPoints()) {
				if (!pts.contains(p)) {
					pts.add(p);
				}
			}
			minesPerRow[i] = rules.get(i).getMines();
		}
		
		LinkedList<MatrixColumn> matrixColumns = new LinkedList<>();
		
		for (int i = 0; i < pts.size(); i++) {
			boolean[] b = new boolean[rules.size()];
			for (int j = 0; j < rules.size(); j++) {
				if (rules.get(j).contains(pts.get(i))) {
					b[j] = true;
				}
			}
			matrixColumns.add(new MatrixColumn(pts.get(i), b));
		}
		for (int i = 0; i < matrixColumns.size(); i++) {
			for (int j = i + 1; j < matrixColumns.size(); j++) {
				if (matrixColumns.get(i).equalsColumn(matrixColumns.get(j))) {
					matrixColumns.get(i).add(matrixColumns.get(j));
					matrixColumns.remove(j);
					j--;
				}
			}
		}
		
		boolean[][] matrix = new boolean[rules.size()][matrixColumns.size()];
		points = new LinkedList<>();
		for (int i = 0; i < matrixColumns.size(); i++) {
			for (int j = 0; j < rules.size(); j++) {
				matrix[j][i] = matrixColumns.get(i).column[j];
			}
			points.add(matrixColumns.get(i).getPoints());
		}
		
		rows = new LinkedList<>();
		
		for (int i = 0; i < matrix.length; i++) {
			rows.add(new BasicRule(matrix[i], minesPerRow[i]));
		}
		
		LinkedList<Click> clicks = new LinkedList<>();
		
		LinkedList<LinkedList<BasicRule>> rowList = getRowList(rows);
		
		double[] percent = new double[points.size()];
		
		for (LinkedList<BasicRule> rowSubList: rowList) {
			LinkedList<int[]> list = getList(points, rowSubList);
			//printList(list);
			double[] subShittyPercent = new double[points.size()];
			
			for (int i: getToBeDone(points, rowSubList)) {
				for (int[] l: list) {
					subShittyPercent[i] += l[i];
				}
				subShittyPercent[i] /= list.size();
				subShittyPercent[i] /= points.get(i).size();
				if (subShittyPercent[i] >= 1) {
					for (Point p: points.get(i)) {
						clicks.add(new Click(ClickType.FLAG, p));
					}
				}
				else if (subShittyPercent[i] <= 0) {
					for (Point p: points.get(i)) {
						clicks.add(new Click(ClickType.REVEAL, p));
					}
				}
			}
			
			if (!clicks.isEmpty()) {
				return clicks;
			}
			
			double[] subPercent = getPercent(points, list);
			
			for (int i = 0; i < percent.length; i++) {
				percent[i] += subPercent[i];
			}
		}
		Click c = getBestClick(percent, points);
		
		clicks.add(c);
		return clicks;
	}
	
	private LinkedList<LinkedList<BasicRule>> getRowList(LinkedList<BasicRule> r) {
		LinkedList<BasicRule> allRows = new LinkedList<>();
		allRows.addAll(r);
		
		LinkedList<LinkedList<BasicRule>> rowList = new LinkedList<>();
		
		while (!allRows.isEmpty()) {
			LinkedList<BasicRule> tempRule = new LinkedList<>();
			tempRule.add(allRows.remove());
			for (int i = 0; i < tempRule.size(); i++) {
				for (int j = 0; j < tempRule.get(i).length(); j++) {
					if (tempRule.get(i).uses(j)) {
						for (int k = 0; k < allRows.size(); k++) {
							if (allRows.get(k).uses(j)) {
								tempRule.add(allRows.remove(k));
								k--;
							}
						}
					}
				}
			}
			rowList.add(tempRule);
		}
		return rowList;
	}
	
	private Click getBestClick(double[] percent, LinkedList<LinkedList<Point>> pts) {

		double bestPercent = percent[0];
		LinkedList<Point> bestPointList = new LinkedList<Point>();
		bestPointList.addAll(pts.get(0));
		for (int i = 1; i < percent.length; i++) {
			if (percent[i] <= bestPercent) {
				if (percent[i] < bestPercent) {
					bestPercent = percent[i];
					bestPointList.clear();
				}
				bestPointList.addAll(pts.get(i));
			}
		}
		return new Click(ClickType.REVEAL, closestToCorner(bestPointList));
	}
	
	private Point closestToCorner(LinkedList<Point> bestPointList) {
		Point bestP = bestPointList.removeFirst();
		double bestDist = bestP.distance(0, 0);
		for (Point p: bestPointList) {
			double dist = p.distance(0, 0);
			if (dist < bestDist) {
				bestDist = dist;
				bestP = p;
			}
		}
		return bestP;
	}
	
	private double[] getPercent(LinkedList<LinkedList<Point>> points, LinkedList<int[]> list) {
		double[] percent = new double[points.size()];
		BigInteger[] bigPercent = getBigIntArray(points.size());
		BigInteger wholeSpan = BigInteger.ZERO;
		for (int j = 0; j < list.size(); j++) {
			BigInteger span = getSpan(points, list.get(j));
			
			wholeSpan = wholeSpan.add(span);
			for (int i = 0; i < list.get(j).length; i++) {
				bigPercent[i] = bigPercent[i].add(BigInteger.valueOf(list.get(j)[i]).multiply(span));
			}
		}
		for (int i = 0; i < points.size(); i++) {
			percent[i] = bigPercent[i].divide(BigInteger.valueOf(points.get(i).size()))
					.multiply(BigInteger.valueOf(100_000)).divide(wholeSpan).doubleValue() / 100_000;
		}
		return percent;
	}
	
	private LinkedList<int[]> getList(LinkedList<LinkedList<Point>> points, LinkedList<BasicRule> rows) {
		LinkedList<int[]> list = new LinkedList<>();
		LinkedList<Integer> toBeDone = getToBeDone(points, rows);
		LinkedList<BasicRule> basicRules = new LinkedList<>();
		basicRules.addAll(rows);
		
		list.add(new int[points.size()]);
		while (!toBeDone.isEmpty()) {
			ArrayList<BasicRule> rulesThatDetermine = getDetermineRules(toBeDone, basicRules);
			if (rulesThatDetermine.isEmpty()) {
				int curIndex = toBeDone.removeFirst();
				int prevListSize = list.size();
				for (int i = 0; i < prevListSize; i++) {
					for (int j = 1; j <= points.get(curIndex).size(); j++) {
						int[] b = list.get(i).clone();
						b[curIndex] = j;
						if (BasicRule.follows(basicRules, toBeDone, b)) {
							list.add(b);
						}
					}
					if (!BasicRule.follows(basicRules, toBeDone, list.get(i))) {
						list.remove(i);
						i--;
						prevListSize--;
					}
				}
			}
			else {
				for (BasicRule br: rulesThatDetermine) {
					int curIndex = br.getDetermineIndex(toBeDone);
					if (curIndex != -1) {
						toBeDone.remove((Integer)curIndex);
						for (int i = 0; i < list.size(); i++) {
							int newMines = br.getMines() - br.getMines(toBeDone, list.get(i));
							if (newMines >= 0 && newMines <= points.get(curIndex).size()) {
								list.get(i)[curIndex] = newMines;
								if (!BasicRule.follows(basicRules, toBeDone, list.get(i))) {
									list.remove(i);
									i--;
								}
							}
							else {
								list.remove(i);
								i--;
							}
						}
					}
				}
			}
		}
		return list;
	}
	
	private LinkedList<Integer> getToBeDone(LinkedList<LinkedList<Point>> points, LinkedList<BasicRule> usesRows) {
		LinkedList<Integer> toBeDone = new LinkedList<>();
		
		for (int i = 0; i < points.size(); i++) {
			for (int j = 0; j < usesRows.size(); j++) {
				if (usesRows.get(j).uses(i)) {
					toBeDone.add(i);
					j = usesRows.size();
				}
			}
		}
		toBeDone.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return points.get(o1).size() - points.get(o2).size();
			}
		});
		return toBeDone;
	}
	
	private ArrayList<BasicRule> getDetermineRules(LinkedList<Integer> toBeDone, LinkedList<BasicRule> basicRules) {
		ArrayList<BasicRule> rets = new ArrayList<>();
		for (int i = 0; i < basicRules.size(); i++) {
			int count = basicRules.get(i).count(toBeDone);
			if (count == 1) {
				rets.add(basicRules.remove(i));
				i--;
			}
			else if (count == 0) {
				basicRules.remove(i);
				i--;
			}
		}
		return rets;
	}

	private BigInteger getSpan(LinkedList<LinkedList<Point>> points, int[] l) {
		BigInteger span = BigInteger.ONE;
		for (int i = 0; i < l.length; i++) {
			BigInteger ncr = ncr(points.get(i).size(), l[i]);
			span = span.multiply(ncr);
		}
		return span;
	}
	
	
	
	private static BigInteger[] getBigIntArray(int length) {
		BigInteger[] arr = new BigInteger[length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = BigInteger.ZERO;
		}
		return arr;
	}
	
	private static BigInteger ncr(int n, int r) {
	    BigInteger top = BigInteger.ONE;
	    BigInteger bot = BigInteger.ONE;
	    for (int i = r + 1; i <= n; i++) {
	        top = top.multiply(BigInteger.valueOf(i));
	    }
	    for (int i = 1; i <= n - r; i++) {
	        bot = bot.multiply(BigInteger.valueOf(i));
	    }
	    return top.divide(bot);
	}

	private boolean isFirstClick(BlockData[][] blockData) {
		for (int i = 0; i < blockData.length; i++) {
			for (int j = 0; j < blockData[i].length; j++) {
				if (!blockData[i][j].isUnknown()) {
					return false;
				}
			}
		}
		return true;
	}


	public void reset() {
		fieldView.reset();
	}
}