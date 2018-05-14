package info.joseluismartin.corvina.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ConstantVerticalTranslation extends AffineWrapperOp {

	double ty = 0;
	double increment = 1;
	
	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest) {
		this.ty += this.increment;
		double h = src.getHeight();
		
		if (this.ty > h / 2|| this.ty < - h / 2) {
			this.increment = -this.increment;
		}
		
		this.op = new AffineTransformOp(
				AffineTransform.getTranslateInstance(0, this.ty), AffineTransformOp.TYPE_BICUBIC);
		
		return op.filter(src, dest);
	}
	
	@Override
	public String toString() {
		return "Vertical Translation";
	}
	

}
