package ai

import java.util.LinkedList

class BasicRule(
	private val bools: BooleanArray,
	val mines: Int
) {
	companion object {
		fun follows(basicRules: LinkedList<BasicRule>, toBeDone: LinkedList<Int>, b: IntArray): Boolean {
			return basicRules.all { br ->
				br.run {
					bools.indices.filter {
						bools[it] && !toBeDone.contains(it)
					}.sumBy {
						b[it]
					} <= mines
				}
			}
		}
	}

	fun count(toBeDone: LinkedList<Int>): Int {
		return toBeDone.count { bools[it] }
	}

	fun getMines(toBeDone: LinkedList<Int?>, `is`: IntArray): Int {
		return bools.indices.filter {
			bools[it] && !toBeDone.contains(it)
		}.sumBy {
			`is`[it]
		}
	}

	fun getDetermineIndex(toBeDone: LinkedList<Int>): Int {
		for (i in toBeDone) {
			if (bools[i]) {
				return i
			}
		}
		return -1
	}

	fun uses(i: Int): Boolean {
		return bools[i]
	}

	fun length(): Int {
		return bools.size
	}

	override fun toString(): String {
		return bools.joinToString(prefix = "$mines:") { if (it) "T" else "F" }
	}
}