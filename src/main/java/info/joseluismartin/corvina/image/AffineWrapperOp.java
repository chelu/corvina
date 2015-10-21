package info.joseluismartin.corvina.image;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;

/**
 * Wrapper for Repeatable {@link AffineTransformOp}
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public abstract class AffineWrapperOp implements BufferedImageOp {

	protected AffineTransformOp op;
	
	public AffineWrapperOp() {
		this.op = new AffineTransformOp(new AffineTransform(), AffineTransformOp.TYPE_BILINEAR);
	}

	@Override
	public abstract BufferedImage filter(BufferedImage src, BufferedImage dest);

	@Override
	public Rectangle2D getBounds2D(BufferedImage src) {
		return this.op.getBounds2D(src);
	}

	@Override
	public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
		return this.op.createCompatibleDestImage(src, destCM);
	}

	@Override
	public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
		return this.op.getPoint2D(srcPt, dstPt);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return this.op.getRenderingHints();
	}
	
	
	
}
