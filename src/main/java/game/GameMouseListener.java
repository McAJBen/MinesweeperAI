package game;


import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GameMouseListener implements MouseListener {
	
	private Point point = new Point();
	
	private boolean hasClick;
	private boolean beingHeld;
	private boolean left;

	@Override
	public void mouseClicked( MouseEvent arg0 ) {
		
	}

	@Override
	public void mouseEntered( MouseEvent arg0 ) {
		
	}

	@Override
	public void mouseExited( MouseEvent arg0 ) {
		
	}

	@Override
	public void mousePressed( MouseEvent arg0 ) {
		point = arg0.getPoint();
		beingHeld = true;
		if (arg0.getButton() == MouseEvent.BUTTON1) {
			left = true;
		}
		else {
			left = false;
		}
	}

	@Override
	public void mouseReleased( MouseEvent arg0 ) {
		if (beingHeld) {
			beingHeld = false;
			hasClick = true;
		}
	}
	
	public boolean hasLeftClick() {
		if (hasClick && left) {
			hasClick = false;
			return true;
		}
		return false;
	}
	
	
	public Point getPoint() {
		return point;
	}

	public boolean hasRightClick() {
		if (hasClick && !left) {
			hasClick = false;
			return true;
		}
		return false;
	}
	
}
