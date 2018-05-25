package info.joseluismartin.corvina.image;

import java.awt.image.ColorModel;
import java.awt.image.ImageFilter;

public class InverseFilter extends ImageFilter {

	@Override
	public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize) {
		byte[] tx = new byte[pixels.length];
		
		for (int i = 0; i < pixels.length; i++)
			tx[i] = (byte) ((pixels[i] & 0xFF) < 100 ? 0xFF : 0x00);
		
		super.setPixels(x, y, w, h, model, tx, off, scansize);
	
	}

	@Override
	public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
		int[] tx = new int[pixels.length];
		
		for (int i = 0; i < pixels.length; i++)
			tx[i] = ((byte) pixels[i]) >  -1 ? -1 : 0;
		
		super.setPixels(x, y, w, h, model, tx, off, scansize);
	}
	
	@Override
	public String toString() {
		return "Inverse";
	}
}
