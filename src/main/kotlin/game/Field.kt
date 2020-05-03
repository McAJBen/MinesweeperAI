package game

import forAll
import game.BlockState.*
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Point
import java.io.File
import kotlin.math.abs
import kotlin.random.Random

class Field(
	private val config: GameConfig,
	private val cheatMode: Boolean
) {
	companion object {
		private const val SUPER_SAFE_FIRST_CLICK = false
	}

	private var minesLeft = 0
	private lateinit var state: FieldState
	private val blocks: Array<Array<Block>> = Array(config.width) { Array(config.height) { Block() } }

	val board: Array<Array<BlockData>>
		get() = synchronized(blocks) {
			Array(config.width) { i ->
				Array(config.height) { j ->
					blocks[i][j].blockData
				}
			}
		}

	init {
		reset()
	}

	fun reset() {
		minesLeft = config.mines
		blocks.forAll { it.reset() }
		state = FieldState.RESET
	}

	@Synchronized
	fun paint(g: Graphics, size: Dimension) {
		val pixelW = size.width.toDouble() / config.width
		val pixelH = size.height.toDouble() / config.height
		forEachPoint { i, j ->
			blocks[i][j].paint(
				g,
				state == FieldState.LOST,
				cheatMode,
				(i * pixelW).toInt(),
				(j * pixelH).toInt(),
				pixelW.toInt(),
				pixelH.toInt()
			)
		}
	}

	fun flagBlock(point: Point) = synchronized(blocks) {
		when (blocks[point.x][point.y].state) {
			HIDDEN -> {
				blocks[point.x][point.y].state = FLAGGED
				minesLeft--
			}
			FLAGGED -> {
				blocks[point.x][point.y].state = HIDDEN
				minesLeft++
			}
			REVEALED -> {}
		}
	}

	fun changeBlock(point: Point) = synchronized(blocks) {
		if (state == FieldState.RESET) {
			state = FieldState.GENERATED
			plantMines(point)
		}
		clearBlock(point.x, point.y)
	}

	fun checkWon() = blocks.all { row ->
		row.all { block ->
			block.isMine || block.state === REVEALED
		}
	}

	fun checkLost() = blocks.any { row ->
		row.any { block ->
			block.isMine && block.state == REVEALED
		}
	}

	fun getMinesLeft() = synchronized(blocks) {
		minesLeft
	}

	fun save(totalTime: Long) {
		val sb = StringBuffer("Time in milliseconds: $totalTime\n")

		for (i in 0 until config.width) {
			for (j in 0 until config.height) {
				sb.append(when(blocks[i][j].isMine) {
					true -> 'M'
					false -> '-'
				})
			}
			sb.append('\n')
		}

		val file = File("$totalTime.txt")
		file.writeText(sb.toString())
	}

	private fun plantMines(initialPoint: Point) {
		val possibleMines = when {
			SUPER_SAFE_FIRST_CLICK -> getSuperSafeMines(initialPoint)
			else -> getSafeMines(initialPoint)
		}.toMutableList()

		for (i in 0 until config.mines) {
			val point = possibleMines.removeAt(
				Random.nextInt(possibleMines.size)
			)
			blocks[point.x][point.y].run {
				isMine = true
				state = HIDDEN
				nearbyMines = 0.toByte()
			}
		}

		forEachPoint { i, j ->
			if (blocks[i][j].isMine) {
				forEachAround(i, j) { x, y ->
					blocks[x][y].nearbyMines++
				}
			}
		}
	}

	private fun getSafeMines(initialPoint: Point): List<Point> {
		return filterPoints { i, j ->
			i != initialPoint.x || j != initialPoint.y
		}
	}

	private fun getSuperSafeMines(initialPoint: Point): List<Point> {
		return filterPoints { i, j ->
			abs(i - initialPoint.x) > 1 || abs(j - initialPoint.y) > 1
		}
	}

	private fun clearBlock(i: Int, j: Int) {
		when (blocks[i][j].state) {
			FLAGGED -> {}
			REVEALED -> {
				var flagged = 0
				forEachAround(i, j) { x, y ->
					if (blocks[x][y].state == FLAGGED) {
						flagged++
					}
				}
				if (flagged == blocks[i][j].nearbyMines.toInt()) {
					forEachAround(i, j) { x, y ->
						if (blocks[x][y].state == HIDDEN) {
							clearBlock(x, y)
						}
					}
				}
			}
			HIDDEN -> {
				blocks[i][j].state = REVEALED
				if (blocks[i][j].isMine) {
					state = FieldState.LOST
				} else if (blocks[i][j].nearbyMines.toInt() == 0) {
					forEachAround(i, j) { x, y ->
						if (blocks[x][y].state == HIDDEN) {
							clearBlock(x, y)
						}
					}
				}
			}
		}
	}

	private fun filterPoints(predicate: (i: Int, j: Int) -> Boolean): List<Point> {
		return (0 until config.width).flatMap { i ->
			(0 until config.height).filter { j ->
				predicate(i, j)
			}.map { j ->
				Point(i, j)
			}
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
}