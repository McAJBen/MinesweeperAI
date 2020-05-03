package game

import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JLabel
import javax.swing.JPanel

class StatPanel(
	private val field: Field,
	private val game: Game
) : JPanel() {

	private val winRate = JLabel()
	private val minesLeft = JLabel()
	private val time = JLabel()

	companion object {
		private fun Long.formatMs(): String {
			return "%2d:%02d:%02d:%03d".format(
				this / 3600000 % 60,
				this / 60000 % 60,
				this / 1000 % 60,
				this % 1000
			)
		}
	}

	init {
		minimumSize = Dimension(100, 50)
		add(winRate)
		add(JLabel("    "))
		add(minesLeft)
		add(JLabel("    "))
		add(time)
	}

	override fun paint(g: Graphics) {
		super.paint(g)
		winRate.text = game.score
		minesLeft.text = "Mines Left: ${field.getMinesLeft()}"
		time.text = "Time: ${game.time.formatMs()}"
	}
}