package info.joseluismartin.corvina.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImageFilter;

public class RotateImageFilter extends BufferedImageFilter {

	public RotateImageFilter() {
		super(new AffineTransformOp(AffineTransform.getRotateInstance(1d), 
				AffineTransformOp.TYPE_BILINEAR));
	}
	
	public String toString() {
		return "Rotate";
	}

}
