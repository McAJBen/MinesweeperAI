package game;

public class BlockData {
	
	private static enum BDState {ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, UNKNOWN, FLAGGED};
	private final BDState state;
	
	public BlockData(boolean isClear, boolean isFlagged, byte nearbyMines) {
		if (isClear) {
			state = BDState.values()[nearbyMines];
		}
		else if (isFlagged) {
			state = BDState.FLAGGED;
		}
		else {
			state = BDState.UNKNOWN;
		}
	}

	public boolean isClear() {
		return state.ordinal() <= 8;
	}

	public boolean isFlagged() {
		return state == BDState.FLAGGED;
	}
	
	public boolean isUnknown() {
		return state == BDState.UNKNOWN;
	}

	public int getNearbyMines() {
		if (state.ordinal() > 8) {
			return -1;
		}
		else {
			return state.ordinal();
		}
	}
}