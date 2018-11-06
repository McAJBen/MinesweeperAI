package game;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import ai.AI;

public class FieldPanel extends JPanel {

	private static final long serialVersionUID = 7588679733829997285L;
	private final Field field;
	private final AI ai;
	
	public FieldPanel(Field field, AI ai) {
		this.field = field;
		this.ai = ai;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Dimension size = getSize();
		field.paint(g, size);
		if (ai != null) {
			ai.paint(g, size);
		}
	}
}