package game;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatPanel extends JPanel {

	private static final long serialVersionUID = 7588679733829997285L;
	private final Field field;
	private final Game game;
	private JLabel winRate;
	private JLabel minesLeft;
	private JLabel time;
	
	public StatPanel(Field field, Game game) {
		this.field = field;
		this.game = game;
		setMinimumSize(new Dimension(100, 50));
		winRate = new JLabel();
		add(winRate);
		add(new JLabel("     "));
		minesLeft = new JLabel();
		add(minesLeft);
		add(new JLabel("     "));
		time = new JLabel();
		add(time);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		winRate.setText(game.getScore());
		minesLeft.setText("Mines Left : " + field.getMinesLeft() + "");
		time.setText("Time:" + toTime(game.getTime()));
	}

	private String toTime(long milliseconds) {
		long ms = milliseconds % 1000;
		long s = (milliseconds / 1000) % 60;
		long m = (milliseconds / 60000) % 60;
		long h = (milliseconds / 3600000) % 60;
		return String.format("%2d:%02d:%02d:%03d", h, m, s, ms);
	}
}