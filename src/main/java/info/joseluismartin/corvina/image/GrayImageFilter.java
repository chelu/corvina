package info.joseluismartin.corvina.image;

import java.awt.image.RGBImageFilter;

/**
 * Convert a color image to a GrayScale.
 * 
 * @author Jose Luis Martin.
 * @since 1.0
 */
public class GrayImageFilter extends RGBImageFilter {

	private static float DEFAULT_RED_FACTOR = 0.299F;
	private static float DEFAULT_GREEN_FACTOR = 0.587f;
	private static float DEFAULT_BLUE_FACTOR = 0.114f;
	
	private float redFactor = DEFAULT_RED_FACTOR;
	private float greenFactor = DEFAULT_GREEN_FACTOR;
	private float blueFactor = DEFAULT_BLUE_FACTOR;
	
	public GrayImageFilter() {
		this.canFilterIndexColorModel = true;
	}
	
	public GrayImageFilter(float redFactor, float greenFactor, float blueFactor) {
		super();
		this.redFactor = redFactor;
		this.greenFactor = greenFactor;
		this.blueFactor = blueFactor;
	}
	
	@Override
	public int filterRGB(int x, int y, int rgb) {
		int  red   = (rgb & 0x00ff0000) >> 16;
		int  green = (rgb & 0x0000ff00) >> 8;
		int  blue  =  rgb & 0x000000ff;
		
		return (int) (redFactor*red + greenFactor*green + blueFactor*blue); 
	}
	
}
