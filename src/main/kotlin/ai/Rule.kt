package ai

import java.awt.Point

data class Rule(
	val points: MutableList<Point>,
	var mines: Int
) {

	operator fun minusAssign(rule: Rule) {
		points.removeAll(rule.points)
		mines -= rule.mines
	}

	operator fun contains(p: Point): Boolean {
		return points.contains(p)
	}

	operator fun contains(r: Rule): Boolean {
		return points.containsAll(r.points)
	}
}