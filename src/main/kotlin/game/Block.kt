package game

import game.BlockState.*
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class Block {
	companion object {
		private val FONT_SIZE = Dimension(8, 13)
		private val BLANK_COLOR = Color(185, 185, 185)

		private var IMAGE_BLANK = load("/blank.png")
		private var IMAGE_FLAG = load("/flag.png")
		private var IMAGE_HIT_MINE = load("/hitMine.png")
		private var IMAGE_MINE = load("/mine.png")

		private fun load(resource: String): BufferedImage = ImageIO.read(Block::class.java.getResource(resource))
	}

	var nearbyMines: Byte = 0

	var isMine: Boolean = false

	var state: BlockState = HIDDEN

	val blockData: BlockData
		get() = when(state) {
			HIDDEN -> BlockData.UNKNOWN
			REVEALED -> BlockData.values()[nearbyMines.toInt()]
			FLAGGED -> BlockData.FLAGGED
		}

	fun paint(g: Graphics, showMines: Boolean, cheat: Boolean, x: Int, y: Int, width: Int, height: Int) {
		if (state == REVEALED && !isMine) {
			g.color = BLANK_COLOR
			g.fillRect(x, y, width, height)
			if (nearbyMines.toInt() != 0) {
				g.color = getFontColor()
				g.drawString(
					nearbyMines.toString(),
					x + (width - FONT_SIZE.width) / 2,
					y + (height + FONT_SIZE.height) / 2
				)
			}
		}
		else {
			g.drawImage(
				when {
					state == REVEALED && isMine -> IMAGE_HIT_MINE
					state == FLAGGED -> IMAGE_FLAG
					showMines && isMine -> IMAGE_MINE
					else -> IMAGE_BLANK
				},
				x,
				y,
				width,
				height,
				null
			)
		}

		g.color = Color.GRAY
		g.drawRect(x, y, width, height)

		if (cheat && isMine) {
			g.color = Color.RED
			g.fillRect(x, y, width / 4, height / 4)
		}
	}

	private fun getFontColor(): Color {
		return when (nearbyMines) {
			1.toByte() -> Color.BLUE
			2.toByte() -> Color.GREEN
			3.toByte() -> Color.RED
			4.toByte() -> Color.DARK_GRAY
			5.toByte() -> Color.MAGENTA
			6.toByte() -> Color.CYAN
			7.toByte() -> Color.PINK
			8.toByte() -> Color.BLACK
			else -> Color.MAGENTA
		}
	}
}