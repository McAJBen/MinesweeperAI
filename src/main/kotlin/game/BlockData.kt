package game

enum class BlockData {
	ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, UNKNOWN, FLAGGED;

	val isClear: Boolean
		get() = ordinal <= 8

	val isFlagged: Boolean
		get() = this == FLAGGED

	val isUnknown: Boolean
		get() = this == UNKNOWN

	val nearbyMines: Int
		get() = if (ordinal > 8) {
			-1
		} else {
			ordinal
		}
}