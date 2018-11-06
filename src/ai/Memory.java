package ai;
import java.awt.Color;
import java.awt.Graphics;

public class Memory {
	
	/*
	 * UNKNOWN		know nothing about
	 * NEEDED		has mines not flagged nearby
	 * FLAGGED		is a flagged unknown
	 * SOLVED		is figured out
	 * COULD_BE_FOUND is near needed
	 * UNRELATED unknown not near could be found
	 */
	
	private enum Step {
		UNKNOWN, NEEDED, FLAGGED, SOLVED, COULD_BE_FOUND, UNRELATED
	}
	private Step step;
	private byte minesNearby;
	
	public Memory() {
		step = Step.UNRELATED;
		minesNearby = -1;
	}
	
	public void setMinesNearby(int i) {
		minesNearby = (byte) i;
	}
	
	public void setFlagged() {
		step = Step.FLAGGED;
	}

	public void setUnknown() {
		step = Step.UNKNOWN;
	}
	
	public void setSolved() {
		step = Step.SOLVED;
	}
	
	public void setNeeded() {
		step = Step.NEEDED;
	}
	
	public void setCouldBeFound() {
		step = Step.COULD_BE_FOUND;
	}

	public int getMinesNearby() {
		return minesNearby;
	}
	
	public boolean isNeeded() {
		return step == Step.NEEDED;
	}
	
	public boolean isUnknown() {
		return step == Step.UNKNOWN || step == Step.UNRELATED;
	}
	
	public boolean isDone() {
		return step == Step.FLAGGED || step == Step.SOLVED;
	}

	public boolean isCouldBeFound() {
		return step == Step.COULD_BE_FOUND;
	}

	public boolean isFlagged() {
		return step == Step.FLAGGED;
	}
	
	public void paint(Graphics g, int x, int y, int width, int height) {
		switch (step) {
			case FLAGGED:
				g.setColor(Color.RED);
				break;
			case SOLVED:
				g.setColor(Color.GREEN);
				break;
			case NEEDED:
				g.setColor(Color.ORANGE);
				break;
			case UNKNOWN:
				g.setColor(Color.MAGENTA);
				break;
			case COULD_BE_FOUND:
				g.setColor(Color.YELLOW);
				break;
			case UNRELATED:
				g.setColor(Color.BLACK);
				break;
		}
		g.drawRect(1 + x, 1 + y, width - 1, height - 1);
		g.drawRect(2 + x, 2 + y, width - 3, height - 3);
	}
}