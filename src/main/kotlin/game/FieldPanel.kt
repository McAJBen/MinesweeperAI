package game

import ai.AIView
import java.awt.Graphics
import javax.swing.JPanel

class FieldPanel(
	private val field: Field,
	private val aiView: AIView?
) : JPanel() {
	override fun paint(g: Graphics) {
		super.paint(g)
		field.paint(g, size)
		aiView?.paint(g, size)
	}
}