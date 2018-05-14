package info.joseluismartin.corvina.ui;

/**
 * Panel to draw column active columns.
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MatrixPanel extends JPanel {
	
	private static final Log log = LogFactory.getLog(MatrixPanel.class);

	private int[] dimensions;
	private int[] values;
	private int gap = 5;
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.values == null)
			return;
		
		Graphics2D g2 = (Graphics2D) g;
		Color oldColor = g2.getColor();
		for (int i = 0; i < dimensions[0]; i++) {
			for (int j = 0; j < dimensions[1]; j++) {
				int value = values[computeIndex(i, j)];
				Color color = value == 0 ? Color.LIGHT_GRAY : Color.RED;
				Ellipse2D e = new Ellipse2D.Float(i*gap, j*gap, 5, 5);
				g2.setColor(color);
				g2.fill(e);
			}
		}
		
		g2.setColor(oldColor);
	}

	private int computeIndex(int i, int j) {
		return i * dimensions[0] + j;
	}
	
	
	/**
	 * @return the dimensions
	 */
	public int[] getDimensions() {
		return dimensions;
	}

	/**
	 * @param dimensions the dimensions to set
	 */
	public void setDimensions(int[] dimensions) {
		this.dimensions = dimensions;
		Dimension d = new Dimension(dimensions[0]*gap + 20, dimensions[1]*gap + 20);
		setPreferredSize(d);
	}

	/**
	 * @return the values
	 */
	public int[] getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(int[] values) {
		this.values = values;
	}

	/**
	 * @return the gap
	 */
	public int getGap() {
		return gap;
	}

	/**
	 * @param gap the gap to set
	 */
	public void setGap(int gap) {
		this.gap = gap;
	}
	
}
