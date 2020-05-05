package game

import Main
import ai.AI
import ai.AIView
import game.ClickType.FLAG
import game.ClickType.REVEAL
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import javax.swing.JFrame
import kotlin.concurrent.thread

class Game(
	private val controller: GameController,
	config: GameConfig
) {
	companion object {
		private val FONT = Font("Calibri", Font.PLAIN, 20)
		private val SCREEN_SIZE = Dimension(700, 500)
		private const val FRAME_TICK = 5000
		private const val MOVE_TICK = 0
	}

	private var aiView: AIView? = null
	private var ai: AI? = null

	private val field: Field = Field(config, controller == GameController.AI)
	private val fieldPanel: FieldPanel
	private val statPanel: StatPanel

	private val gameMouse: GameMouseListener

	private var startTime: Long

	private var gamesComplete = 0L
	private var gamesWon = 0L

	val time: Long
		get() = System.currentTimeMillis() - startTime

	val score: String
		get() = String.format(
			"Win Rate %.3f%%  [%,d  /  %,d]",
			100.0 * gamesWon / gamesComplete,
			gamesWon, gamesComplete
		)

	init {

		if (controller == GameController.AI) {
			aiView = AIView(config)
			ai = AI(config.width, config.height, aiView)
		}

		fieldPanel = FieldPanel(field, aiView)
		gameMouse = GameMouseListener(fieldPanel, config)
		fieldPanel.addMouseListener(gameMouse)

		statPanel = StatPanel(field, this)

		JFrame(Main.WINDOW_NAME).apply {
			setSize(SCREEN_SIZE.width, SCREEN_SIZE.height)
			isResizable = true
			defaultCloseOperation = JFrame.EXIT_ON_CLOSE
			setLocationRelativeTo(null)
			layout = BorderLayout()
			font = FONT
			add(fieldPanel)
			add(statPanel, BorderLayout.NORTH)
			isVisible = true
		}

		startTime = System.currentTimeMillis()
	}

	fun start() {
		thread(name = "paintThread") {
			runBlocking {
				var nextTime = System.currentTimeMillis() + FRAME_TICK
				while (true) {
					delay(nextTime - System.currentTimeMillis())
					fieldPanel.repaint()
					statPanel.repaint()
					nextTime += FRAME_TICK.toLong()
				}
			}
		}
		thread(name = "runThread") {
			runBlocking {
				var nextTime = System.currentTimeMillis() + MOVE_TICK
				while (true) {
					delay(nextTime - System.currentTimeMillis())
					move()
					nextTime += MOVE_TICK.toLong()
				}
			}
		}
	}

	private fun move() {
		val clicks = when (controller) {
			GameController.Player -> gameMouse.getClicks()
			GameController.AI -> ai?.solve(field.board, field.getMinesLeft())
		}
		if (clicks != null && clicks.isNotEmpty()) {
			clicks.forEach { (clickType, point) ->
				when (clickType) {
					REVEAL -> field.changeBlock(point)
					FLAG -> field.flagBlock(point)
				}
			}
			checkDone()
		}
	}

	private fun checkDone() {
		if (field.checkWon()) {
			gamesWon++
			reset()
		} else if (field.checkLost()) {
			reset()
		}
	}

	private fun reset() {
		gamesComplete++

		// try {
		// 	Thread.sleep(1000)
		// } catch (e: InterruptedException) {
		// 	e.printStackTrace()
		// }

		field.reset()
		startTime = System.currentTimeMillis()
		if (controller == GameController.AI) {
			ai?.reset()
		}
	}
}