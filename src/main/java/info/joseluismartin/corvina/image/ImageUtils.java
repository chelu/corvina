package info.joseluismartin.corvina.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class ImageUtils {

	public static BufferedImage deepCopy(BufferedImage image) {
		BufferedImage clone = new BufferedImage(image.getWidth(),
				image.getHeight(), image.getType());
		Graphics2D g2d = clone.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		return clone;
	}
}
