package game;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class FieldPanel extends JPanel {

	private static final long serialVersionUID = 7588679733829997285L;
	private final Field field;
	private final FieldView fieldView;
	
	public FieldPanel(Field field, FieldView fieldView) {
		this.field = field;
		this.fieldView = fieldView;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Dimension size = getSize();
		field.paint(g, size);
		if (fieldView != null) {
			fieldView.paint(g, size);
		}
	}
}