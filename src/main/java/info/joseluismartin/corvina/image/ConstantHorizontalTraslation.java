package info.joseluismartin.corvina.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * A Constant translation op
 *
 * @author chelu
 */
public class ConstantHorizontalTraslation extends AffineWrapperOp {

	double tx = 0;
	double increment = 1;
	
	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest) {
		
		double w = src.getWidth();
		if (this.tx > w / 2 || this.tx <  - w / 2) {
			this.increment = -this.increment;	
			this.tx += this.increment;
		}
		
		this.op = new AffineTransformOp(
				AffineTransform.getTranslateInstance(this.tx, 0), AffineTransformOp.TYPE_BICUBIC);
		
		return op.filter(src, dest);
	}
	

	@Override
	public String toString() {
		return "Horizontal Translation";
	}
	
}
