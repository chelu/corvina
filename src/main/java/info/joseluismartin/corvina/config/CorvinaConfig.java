package info.joseluismartin.corvina.config;

import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.numenta.nupic.Parameters;
import org.numenta.nupic.algorithms.TemporalMemory;
import org.numenta.nupic.algorithms.Anomaly.AveragedAnomalyRecordList;
import org.numenta.nupic.network.Layer;
import org.numenta.nupic.network.Network;
import org.numenta.nupic.network.Region;
import org.numenta.nupic.network.sensor.SensorParams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import info.joseluismartin.corvina.Corvina;
import info.joseluismartin.corvina.htm.LowMemorySpatialPooler;
import info.joseluismartin.corvina.image.RotateImageFilter;
import info.joseluismartin.corvina.sensor.ImageSensor;
import info.joseluismartin.corvina.ui.ImageSensorView;
import info.joseluismartin.corvina.ui.LayerView;
import info.joseluismartin.corvina.ui.MainFrame;
import info.joseluismartin.corvina.ui.NetworkView;

/**
 * Configuration class for corvina project.
 * 
 * @author Jose Luis Martin.
 * @since 1.0
 */
@Configuration
public class CorvinaConfig {
	public static final String CORVINA = "corvina";
	public static final String MAIN_REGION = "main region";
	public static final String REGION_1 = "Region 1";
	public static final String REGION_2 = "Region 2";
	public static final String LAYER_23 = "Layer 2/3";
	public static final String LAYER_4 = "Layer 4";
	public static final String LAYER_1 = "Layer 1";

	/**
	 * Creates the corvina htm network
	 * @return 
	 */
	@Bean
	public Network network() {
		Network network = new Network(CORVINA, parameters23());
		network.add(region1(network));
		
		// network.add(region2(network))
		//	.connect(REGION_2, REGION_1);

		return network;
	}

	/**
	 * First region with two layers, layer 23 see a gray scale image of 128x128 pixels.
	 * (1024x1024 columns) Second has 128x128 columns.
	 * @return a new Region
	 */
	@Bean
	public Region region1(Network network) {
		Region region = new Region(REGION_1, network);
		region.add(Network.createLayer(LAYER_23, parameters23())
				.add(new TemporalMemory())
				.add(new LowMemorySpatialPooler()));
		
//		.add(Network.createLayer(LAYER_4, parameters4())
//				.add(new TemporalMemory())
//				.add(new SpatialPooler()))
//		.connect(LAYER_4, LAYER_23);
		
		return region;
	}

	@Bean
	public ImageSensor imageSensor() {
		return new ImageSensor("/home/chelu/workspaces/htm/corvina/src/main/resources/images/test.png");
	}
	
	@Bean 
	public ImageSensorView imageSensorView() {
		ImageSensorView imsv = new ImageSensorView();
		imsv.setModel(imageSensor());
		List<ImageFilter> available = new ArrayList<>();
		available.add(new RotateImageFilter());
		imsv.setAvailableFilters(available);
		imsv.refresh();
		
		return imsv;
	}

	@Bean
	public SensorParams sensorParams() {
		SensorParams sensorParams = SensorParams.create(
				new String[] {"PATH"}, "/home/chelu/workspaces/htm/corvina/src/main/resources/images/test.png");
		
		return sensorParams;
	}


	@Bean
	public Parameters parameters23() {
		int[] dimensions = {128, 128};
		Parameters p =  Parameters.getAllDefaultParameters();
		// 512x512 colums, 32 cells/column.
		p.setColumnDimensions(dimensions);
		p.setInputDimensions(new int[] {128, 128});

		return p;
	}
	
	@Bean
	public Parameters parameters4() {
		int[] dimensions = {64, 64};
		Parameters p =  Parameters.getAllDefaultParameters();
		// 128x128 colums, 32 cells/column.
		p.setColumnDimensions(dimensions);
		p.setInputDimensions(new int[] {128, 128});
		return p;
	}

	public List<Layer<?>> layers() {
		ArrayList<Layer<?>> layers = new ArrayList<>();

		return layers;
	}
	
	/**
	 * Application main Frame
	 * @return
	 */
	@Bean
	@Lazy
	public MainFrame mainFrame() {
		MainFrame mainFrame = new MainFrame(); 
		
		return mainFrame;
	}
	
	/**
	 * Network viewer
	 * @return
	 */
	@Bean
	@Lazy
	public NetworkView networkView() {
		NetworkView networkView = new NetworkView(network());
		
		return networkView;
	}
	
	@Bean
	@Lazy
	public LayerView layerView() {
		return new LayerView();
	}
	
	@Bean
	public Corvina corvina() {
		return new Corvina();
	}
}

