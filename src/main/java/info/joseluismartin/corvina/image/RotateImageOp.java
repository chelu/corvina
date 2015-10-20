package info.joseluismartin.corvina.image;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

public class RotateImageOp implements BufferedImageOp {
	
	private AffineTransformOp op;
	private AffineTransform tx = AffineTransform.getRotateInstance(0);
	private double theta = 0;
	private double increment = 0.1d;
	

	public RotateImageOp() {
		this.op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);
	}
	
	public String toString() {
		return "Rotate";
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest) {
		this.theta += this.increment;
		this.op = new AffineTransformOp(
				AffineTransform.getRotateInstance(this.theta, src.getWidth() / 2, src.getHeight() / 2),
				AffineTransformOp.TYPE_BICUBIC);
		return op.filter(src, dest);
	}

	@Override
	public Rectangle2D getBounds2D(BufferedImage src) {
		return op.getBounds2D(src);
	}

	@Override
	public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
		return op.createCompatibleDestImage(src, destCM);
	}

	@Override
	public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
		return op.getPoint2D(srcPt, dstPt);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return op.getRenderingHints();
	}

}
