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
import org.numenta.nupic.network.sensor.MetaStream;
import org.numenta.nupic.network.sensor.SensorFlags;
import org.numenta.nupic.network.sensor.SensorParams;
import org.numenta.nupic.network.sensor.ValueList;
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
	private String imageName;
	private List<String> imagesToLoad = new ArrayList<>();
	private int cicles = 100;
	private int imageCicles = 100;
	private int imageStep = 0;
	private int currentImage = 0;
	
	private boolean singleImage = true;
	private List<ImageSensorListener> listeners = new ArrayList<>();

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
		File file = new File(path);
		try {
			source = ImageIO.read(file);
		} 
		catch (IOException e) {
			return;
		}
		// have a image?
		if (source == null)
			return;
		
		this.imageName = file.getName();
		
		for (ImageFilter filter : filters) {
			source = applyFilter(source, filter);
		}

		// convert to gray scale
		this.image = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		this.image.getGraphics().drawImage(source, 0, 0, null);
		this.original = ImageUtils.deepCopy(this.image);
		fireImageChange();
	}

	private void fireImageChange() {
		this.listeners.forEach(l -> l.imageChanged());
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
		
		if (!nextImage())
			return null;
		
		if (this.image == null)
			return new int[0];
	
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
		
		int[] array = dense.stream().mapToInt(i -> i).toArray();
		
		return array;
	}

	private boolean nextImage() {
		if (this.singleImage)
			return true;
		
		if (this.currentImage == this.imagesToLoad.size()) {
			reset();
			return false;
		}
		
		if (this.imageStep == 0)
			loadImage(this.imagesToLoad.get(this.currentImage));
		
		if (this.imageStep++ == this.imageCicles) {
			this.currentImage++;
			this.imageStep = 0;
			return nextImage();
		}
		
		return true;
	}

	private BufferedImage applyDinamycFilters(List<BufferedImageOp> fs) {
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
	
	public void reset() {
		this.currentImage = 0;
		this.imageStep = 0;
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

	public synchronized void setImage(BufferedImage image) {
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
	
	/**
	 * @return the imageName
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * @param imageName the imageName to set
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	

	/**
	 * @return the imagesToLoad
	 */
	public List<String> getImagesToLoad() {
		return imagesToLoad;
	}

	/**
	 * @param imagesToLoad the imagesToLoad to set
	 */
	public void setImagesToLoad(List<String> imagesToLoad) {
		this.imagesToLoad.clear();
		this.imagesToLoad.addAll(imagesToLoad);
		this.setSingleImage(false);
	}

	/**
	 * @return the cicles
	 */
	public int getCicles() {
		return cicles;
	}

	/**
	 * @param cicles the cicles to set
	 */
	public void setCicles(int cicles) {
		this.cicles = cicles;
	}

	/**
	 * @return the imageCicles
	 */
	public int getImageCicles() {
		return imageCicles;
	}

	/**
	 * @param imageCicles the imageCicles to set
	 */
	public void setImageCicles(int imageCicles) {
		this.imageCicles = imageCicles;
		this.imageStep = 0;
	}

	/**
	 * @return the singleImage
	 */
	public boolean isSingleImage() {
		return singleImage;
	}

	/**
	 * @param singleImage the singleImage to set
	 */
	public void setSingleImage(boolean singleImage) {
		this.singleImage = singleImage;
	}
	
	public void addListener(ImageSensorListener l) {
		if (!this.listeners.contains(l))
			this.listeners.add(l);
	}
	
	public void removeListener(ImageSensorListener l) {
		this.listeners.remove(l);
	}

}
