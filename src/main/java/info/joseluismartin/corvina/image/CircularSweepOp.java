package info.joseluismartin.corvina.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CircularSweepOp extends AffineWrapperOp {

	double tx = 0;
	double ty = 0;
	double theta = 0;
	double increment = 5;
	double thetaInc = 0.785;
	int wait = 0;
	private Random random = new Random();

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest) {
		if (wait > 0) {
			wait--;
			return src;
		}
		this.tx += Math.cos(theta) * increment;
		this.ty += Math.sin(theta) * increment;
		
		this.op = new AffineTransformOp(
				AffineTransform.getTranslateInstance(this.tx, this.ty), AffineTransformOp.TYPE_BICUBIC);
		
		if (Math.abs(this.tx) > src.getWidth() / 2 || Math.abs(this.ty) > src.getHeight() / 2) {
			this.tx = 0;
			this.ty = 0;
			this.theta = (2 * random.nextDouble() -1) * Math.PI;
			wait = 5;
		}

		return op.filter(src, dest);
	}


	@Override
	public String toString() {
		return "Circular Sweep";
	}
}
