package info.joseluismartin.corvina.sensor;

import java.awt.image.BufferedImage;
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
import org.numenta.nupic.ValueList;
import org.numenta.nupic.network.sensor.MetaStream;
import org.numenta.nupic.network.sensor.Sensor;
import org.numenta.nupic.network.sensor.SensorParams;

/**
 * {@link Sensor}r for Images
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public class ImageSensor implements Sensor<Object> {

	private static final int HEADER_SIZE = 3;
	private static final String PATH = "PATH";
	private static final Log log = LogFactory.getLog(ImageSensor.class);
	
	/** Filters to apply to image before expose it to HTM. */
	private List<ImageFilter> filters = new ArrayList<>();
	private BufferedImage image;
	private ValueList valueList;
	private SensorParams params;
	
	public ImageSensor(SensorParams params) {
		this.params = params;
		
		 if (!params.hasKey(PATH)) 
			 throw new IllegalArgumentException("Passed improperly formed Tuple: no key for \"PATH\"");
	      
	     // FIXME: Do a lazy load of image
		 String pathStr = (String) params.get(PATH);
		 loadImage(pathStr);
	}

	/**
	 * Load image from filesystem.
	 * @param pathStr image path
	 */
	private void loadImage(String pathStr) {
		try {
			ImageIO.read(new File(pathStr));
		} 
		catch (IOException e) {
			log.error(e);
		}
	}

	/**
	 * Seems that this method is forced to be used with String type.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <K> MetaStream<K> getInputStream() {
		int[] data = image.getRaster().getPixels(0, 0, image.getWidth(), image.getHeight(), new int[] {});
		Stream<String> stream =  Arrays.stream(data).mapToObj(
				i -> { return String.valueOf(i);});
		MetaStreamDecorator<String> decorator = new MetaStreamDecorator<String>(stream, valueList);
	
		return (MetaStream<K>) decorator;
	}

	@Override
	public SensorParams getParams() {
		return this.params;
	}
	
	@Override
	public ValueList getMetaInfo() {
		return this.valueList;
	}
	
	public List<ImageFilter> getFilters() {
		return filters;
	}	

	public void setFilters(List<ImageFilter> filters) {
		this.filters = filters;
	}

}
