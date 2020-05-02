package game;

public enum BlockData {
	
	ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, UNKNOWN, FLAGGED;
	
	public boolean isClear() {
		return ordinal() <= 8;
	}

	public boolean isFlagged() {
		return this == BlockData.FLAGGED;
	}
	
	public boolean isUnknown() {
		return this == BlockData.UNKNOWN;
	}

	public int getNearbyMines() {
		if (ordinal() > 8) {
			return -1;
		}
		else {
			return ordinal();
		}
	}
}