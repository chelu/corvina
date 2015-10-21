package info.joseluismartin.corvina.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Random sweep filter.
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public class RandomSweepOp extends AffineWrapperOp {
	
	/** maximun sweep radius */
	private int radius = 10;
	private Random random = new Random();
	
	public RandomSweepOp() {
	
	}
	
	public RandomSweepOp(int radius) {
		super();
		this.radius = radius;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest) {
		double tx =  (2 * random.nextDouble() -1) * this.radius;
		double ty =  (2 * random.nextDouble() -1) * this.radius;
		this.op = new AffineTransformOp(AffineTransform.getTranslateInstance(tx, ty), AffineTransformOp.TYPE_BILINEAR);
		return this.op.filter(src, dest);
	}
	
	public String toString() {
		return "Random Sweep";
	}

}
