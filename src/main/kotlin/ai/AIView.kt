package ai

import ai.BlockStep.*
import game.BlockData
import game.GameConfig
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Point

class AIView(
	private val config: GameConfig
) {

	private val steps: Array<Array<BlockStep>> = Array(config.width) { Array(config.height) { UNKNOWN } }
	private val nearby: Array<Array<Byte>> = Array(config.width) { Array(config.height) { 0.toByte() } }

	init {
		reset()
	}

	fun reset() {
		forEachPoint { i, j ->
			steps[i][j] = UNKNOWN
			nearby[i][j] = 0
		}
	}

	fun paint(g: Graphics, size: Dimension) {
		val pixelW = size.width.toDouble() / config.width
		val pixelH = size.height.toDouble() / config.height
		val w = pixelW.toInt() + 1
		val h = pixelH.toInt() + 1

		forEachPoint { i, j ->
			g.color = steps[i][j].color
			val x = (i * pixelW).toInt()
			val y = (j * pixelH).toInt()

			g.fillRect(x, y, w, 2)
			g.fillRect(x, y, 2, h)
			g.fillRect(x, y + h - 2, w, 2)
			g.fillRect(x + w - 2, y, 2, h)
		}
	}

	fun checkMemory(blockData: Array<Array<BlockData>>) {
		forEachPoint { i, j ->
			when (steps[i][j]) {
				NEEDED -> when {
					isSolved(blockData, i, j) -> steps[i][j] = SOLVED
					else -> nearby[i][j] = blockData[i][j].nearbyMines.toByte()
				}
				UNKNOWN -> when {
					blockData[i][j].isFlagged -> steps[i][j] = FLAGGED
					blockData[i][j].isClear -> when {
						isSolved(blockData, i, j) -> steps[i][j] = SOLVED
						else -> {
							steps[i][j] = NEEDED
							nearby[i][j] = blockData[i][j].nearbyMines.toByte()
						}
					}
				}
			}
		}
	}

	fun getRuleSet(minesLeft: Int): RuleSet {
		val rules = mutableListOf<Rule>()
		val unknown = mutableListOf<Point>()
		forEachPoint { i, j ->
			when (steps[i][j]) {
				NEEDED -> rules.add(
					Rule(
						getUnknownAround(i, j),
						getMinesAround(i, j)
					)
				)
				UNKNOWN -> unknown.add(Point(i, j))
			}
		}
		rules.add(Rule(unknown, minesLeft))
		return RuleSet(rules)
	}

	private fun getMinesAround(i: Int, j: Int): Int {
		var mines = nearby[i][j].toInt()
		forEachAround(i, j) { x, y ->
			if (steps[x][y] == FLAGGED) {
				mines--
			}
		}
		return mines
	}

	private fun getUnknownAround(i: Int, j: Int): MutableList<Point> {
		val near = mutableListOf<Point>()
		forEachAround(i, j) { x, y ->
			if (steps[x][y] == UNKNOWN) {
				near.add(Point(x, y))
			}
		}
		return near
	}

	private fun isSolved(blockData: Array<Array<BlockData>>, i: Int, j: Int): Boolean {
		return !anyAround(i, j) { x, y ->
			blockData[x][y].isUnknown
		}
	}

	private fun forEachPoint(action: (i: Int, j: Int) -> Unit) {
		(0 until config.width).forEach { i ->
			(0 until config.height).forEach { j ->
				action(i, j)
			}
		}
	}

	private fun forEachAround(i: Int, j: Int, action: (x: Int, y: Int) -> Unit) {
		for (x in (i - 1).coerceAtLeast(0) until (i + 2).coerceAtMost(config.width)) {
			for (y in (j - 1).coerceAtLeast(0) until (j + 2).coerceAtMost(config.height)) {
				action(x, y)
			}
		}
	}

	private fun anyAround(i: Int, j: Int, predicate: (x: Int, y: Int) -> Boolean): Boolean {
		return ((i - 1).coerceAtLeast(0) until (i + 2).coerceAtMost(config.width)).any { x ->
			((j - 1).coerceAtLeast(0) until (j + 2).coerceAtMost(config.height)).any { y ->
				predicate(x, y)
			}
		}
	}
}