package info.joseluismartin.corvina.sensor;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.DataBufferByte;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.numenta.nupic.FieldMetaType;
import org.numenta.nupic.ValueList;
import org.numenta.nupic.network.sensor.MetaStream;
import org.numenta.nupic.network.sensor.SensorFlags;
import org.numenta.nupic.network.sensor.SensorParams;
import org.numenta.nupic.util.Tuple;

import info.joseluismartin.corvina.image.ImageUtils;

/**
 * Sensor to load images
 *   
 * @author Jose Luis Martin
 * @since 1.0
 */
public class ImageSensor {

	private static final Log log = LogFactory.getLog(ImageSensor.class);
	
	/** Filters to apply to image before expose it to HTM. */
	private List<ImageFilter> filters = new ArrayList<>();
	/** Filters to apply in every network image request */
	private List<BufferedImageOp> dinamycFilters = new ArrayList<>();
	private BufferedImage image;
	private BufferedImage original;
	private ValueList valueList;
	private SensorParams params;

	
	public ImageSensor(String path) {
		// FIXME: Do a lazy load of image
		loadImage(path);
	}

	/**
	 * Load image from filesystem.
	 * @param pathStr image path
	 */
	public void loadImage(String path) {
		BufferedImage source = null;
		try {
			source = ImageIO.read(new File(path));
		} 
		catch (IOException e) {
			log.error(e);
			return;
		}
		
		for (ImageFilter filter : filters) {
			source = applyFilter(source, filter);
		}

		// convert to gray scale
		this.image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		this.image.getGraphics().drawImage(source, 0, 0, null);
		this.original = ImageUtils.deepCopy(this.image);
	}

	private BufferedImage applyFilter(BufferedImage source, ImageFilter filter) {
		BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		FilteredImageSource fis = new FilteredImageSource(source.getSource(), filter);
		Image filtered = Toolkit.getDefaultToolkit().createImage(fis);
		bi.getGraphics().drawImage(filtered, 0, 0, null);
		
		return bi;
	}
	
	private BufferedImage applyOp(BufferedImage source, BufferedImageOp op) {
		BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		BufferedImage filtered = op.filter(source, null);
		bi.getGraphics().drawImage(filtered, 0, 0, null);
		
		return bi;
	}

	/**
	 * Seems that this method is forced to be used with String type.
	 */
	@SuppressWarnings("unchecked")
	public <K> MetaStream<K> getInputStream() {
		int[] data = getImageData();
		Stream<String> stream =  Arrays.stream(data).mapToObj(
				i -> { return String.valueOf(i);});
		MetaStreamDecorator<String> decorator = new MetaStreamDecorator<String>(stream, valueList);
		decorator.setValueList(getMetaInfo());
		
		return (MetaStream<K>) decorator;
	}
	
	public int[] getImageData() {
		 byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		 int width = image.getWidth();
		 int height = image.getHeight();
		 boolean hasAlphaChannel = image.getAlphaRaster() != null;
		 int[] result = new int[height*width];
		 
		 if (hasAlphaChannel) {
	         int pixelLength = 4;
	         for (int pixel = 0, i= 0; pixel < pixels.length; pixel += pixelLength) {
	            int argb = 0;
	            argb += (((int) pixels[pixel] & 0xff) << 24);     // alpha
	            argb += ((int) pixels[pixel + 1] & 0xff);         // blue
	            argb += (((int) pixels[pixel + 2] & 0xff) << 8);  // green
	            argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
	            result[i++] = argb;
	         }
		 } 
		 else {
	         int pixelLength = 3;
	         for (int pixel = 0, i= 0; pixel < pixels.length; pixel += pixelLength) {
	            int argb = 0;
	            argb += -16777216; // 255 alpha
	            argb += ((int) pixels[pixel] & 0xff);             // blue
	            argb += (((int) pixels[pixel + 1] & 0xff) << 8);  // green
	            argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
	            result[i++] = argb;
	         }
	      }

	      return result;
	}
	
	/** 
	 * @return dense in input representation of image data.
	 */
	public synchronized int[] getAsDense() {
	
		this.image = applyDinamycFilters(this.dinamycFilters); 
		
		byte[] data =  ((DataBufferByte) this.image.getRaster().getDataBuffer()).getData();
		List<Integer> dense = new ArrayList<>();
		int zeros = 0;
		int ones = 0;
		for (Byte b : data) {
			if (b.intValue() == 0) {
				zeros++;
				dense.add(0);
			}
			else {
				ones++;
				dense.add(1);
			}
		}
		
		log.debug("zeros [" + zeros + "] ones [" + ones + "]");
		
		return dense.stream().mapToInt(i -> i).toArray();
	}

	private synchronized BufferedImage applyDinamycFilters(List<BufferedImageOp> fs) {
		BufferedImage filtered = this.original;
		
		for (BufferedImageOp op : fs)
			filtered = applyOp(filtered, op);
		
		return filtered;
	}

	public SensorParams getParams() {
		return this.params;
	}
	
	public ValueList getMetaInfo() {
		if (this.valueList == null) {
			ListValueList vl = new ListValueList();
			vl.addTuple(new Tuple("pixel"));
			vl.addTuple(new Tuple(FieldMetaType.INTEGER));
			vl.addTuple(new Tuple(SensorFlags.B));
			this.valueList = vl;
		}
		
		return this.valueList;
	}
	
	public List<ImageFilter> getFilters() {
		return filters;
	}	

	public void setFilters(List<ImageFilter> filters) {
		this.filters = filters;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	/**
	 * @return the dinamycFilters
	 */
	public synchronized List<BufferedImageOp> getDinamycFilters() {
		return dinamycFilters;
	
	}

	/**
	 * @param dinamycFilters the dinamycFilters to set
	 */
	public synchronized void setDinamycFilters(List<BufferedImageOp> dinamycFilters) {
			this.dinamycFilters = dinamycFilters;
	}

}
