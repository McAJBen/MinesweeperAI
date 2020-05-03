package game

import java.awt.Graphics
import javax.swing.JPanel

class FieldPanel(
	private val field: Field,
	private val fieldView: FieldView?
) : JPanel() {
	override fun paint(g: Graphics) {
		super.paint(g)
		field.paint(g, size)
		fieldView?.paint(g, size)
	}
}