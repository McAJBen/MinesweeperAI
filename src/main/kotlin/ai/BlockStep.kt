package ai

import java.awt.Color

enum class BlockStep {
	NEEDED,
	FLAGGED,
	SOLVED,
	UNKNOWN;

	val color: Color
		get() = when (this) {
			FLAGGED -> Color.RED
			SOLVED -> Color.GREEN
			NEEDED -> Color.ORANGE
			UNKNOWN -> Color.BLACK
		}
}