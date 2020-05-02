package game

import java.awt.Point

data class Click(
	val clickType: ClickType,
	val point: Point
) {
	constructor(
		clickType: ClickType,
		x: Int,
		y: Int
	) : this(
		clickType,
		Point(x, y)
	)
}
