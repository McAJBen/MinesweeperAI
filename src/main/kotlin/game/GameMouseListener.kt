package game

import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class GameMouseListener(
	private val fieldPanel: FieldPanel,
	private val config: GameConfig) : MouseListener {

	private val clicks = mutableListOf<Click>()

	fun getClicks(): List<Click> {
		return synchronized(clicks) {
			val clicks = clicks.toList()
			this.clicks.clear()
			clicks
		}
	}

	private fun pointToCoordinate(p: Point): Point {
		val pixelW = fieldPanel.size.width.toDouble() / config.width
		val pixelH = fieldPanel.size.height.toDouble() / config.height
		val x = (p.x / pixelW).toInt()
		val y = (p.y / pixelH).toInt()
		return Point(x, y)
	}

	override fun mousePressed(e: MouseEvent) {
		val clickType = when (e.button) {
			MouseEvent.BUTTON1 -> ClickType.REVEAL
			MouseEvent.BUTTON3 -> ClickType.FLAG
			else -> return
		}

		clicks.add(
			Click(
				clickType,
				pointToCoordinate(e.point)
			)
		)
	}

	override fun mouseReleased(e: MouseEvent) {}

	override fun mouseClicked(arg0: MouseEvent) {}

	override fun mouseEntered(arg0: MouseEvent) {}

	override fun mouseExited(arg0: MouseEvent) {}
}