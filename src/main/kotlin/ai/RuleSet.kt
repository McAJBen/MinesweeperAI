package ai

import game.Click
import game.ClickType

class RuleSet(
	inputRules: List<Rule>
) {

	val rules: MutableList<Rule> = inputRules.distinct().toMutableList()

	init {
		var change: Boolean
		do {
			change = false
			rules.indices.forEach { i ->
				(i + 1 until rules.size).forEach { j ->
					if (rules[j].points.isNotEmpty() && rules[i].contains(rules[j])) {
						change = true
						rules[i] -= rules[j]
					}
					if (rules[i].points.isNotEmpty() && rules[j].contains(rules[i])) {
						change = true
						rules[j] -= rules[i]
					}
				}
			}
			rules.removeIf { it.points.isEmpty() }
		} while (change)
	}

	fun solve(): Set<Click> {
		val clicks = mutableSetOf<Click>()
		rules.forEach { (points, mines) ->
			if (points.size == mines) {
				points.forEach { clicks.add(Click(ClickType.FLAG, it)) }
			}
			if (mines == 0) {
				points.forEach { clicks.add(Click(ClickType.REVEAL, it)) }
			}
		}
		return clicks
	}
}