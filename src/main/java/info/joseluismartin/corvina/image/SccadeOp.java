package info.joseluismartin.corvina.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class SccadeOp extends AffineWrapperOp {

	private double[] tx = {-0.5, 0,  0.5, -0.5, 0, 0.5, -0.5};
	private double[] ty = {0, 0, 0, -0.5, -0.5, -0.5, 0};
	private int index = 0;
	private int exposure = 5;
	
	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest) {
		double x = tx[index] * src.getWidth();
		double y = ty[index] * src.getHeight();
		
		this.op = new AffineTransformOp(
				AffineTransform.getTranslateInstance(x, y), AffineTransformOp.TYPE_BICUBIC);
		
		if (this.exposure-- == 0) {
			this.exposure = 5;
			if (index++ ==  this.tx.length - 1)
				index = 0;
		}
		
		return this.op.filter(src, dest);
	}

	@Override
	public String toString() {
		return "Sccade";
	}
	
	
}
