package info.joseluismartin.corvina.config;

import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.List;

import org.numenta.nupic.Parameters;
import org.numenta.nupic.algorithms.TemporalMemory;
import org.numenta.nupic.network.Layer;
import org.numenta.nupic.network.Network;
import org.numenta.nupic.network.Region;
import org.numenta.nupic.network.sensor.SensorParams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import info.joseluismartin.corvina.Corvina;
import info.joseluismartin.corvina.htm.CorvinaClassifier;
import info.joseluismartin.corvina.htm.LowMemorySpatialPooler;
import info.joseluismartin.corvina.image.RandomSweepOp;
import info.joseluismartin.corvina.image.RotateImageOp;
import info.joseluismartin.corvina.sensor.ImageSensor;
import info.joseluismartin.corvina.ui.ImageSensorView;
import info.joseluismartin.corvina.ui.LayerView;
import info.joseluismartin.corvina.ui.MainFrame;
import info.joseluismartin.corvina.ui.NetworkView;
import rx.observables.ConnectableObservable;

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
	public static final String LAYER_5 = "Layer 5";
	public static final String LAYER_6 = "Layer 6";
	
	private int[] dimensions = {64, 64};

	/**
	 * Creates the corvina htm network
	 * @return 
	 */
	@Bean
	public Network network() {
		Network network = new Network(CORVINA, networkParameters());
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
				.add(new LowMemorySpatialPooler()))
		//*
		.add(Network.createLayer(LAYER_4, parameters4())
				.add(new TemporalMemory())
				.add(new LowMemorySpatialPooler()))
		.add(Network.createLayer(LAYER_5, parameters5())
				.add(new TemporalMemory())
				.add(new LowMemorySpatialPooler()))
		.add(Network.createLayer(LAYER_6, parameters6())
				.add(new TemporalMemory())
				.add(new LowMemorySpatialPooler()))
		.connect(LAYER_4, LAYER_23)
		.connect(LAYER_5, LAYER_4)
		.connect(LAYER_6, LAYER_5);
		//*/
		return region;
	}

	@Bean
	public ImageSensor imageSensor() {
		return new ImageSensor("/home/chelu/workspaces/htm/corvina/src/main/resources/images/test64.png");
	}
	
	@Bean 
	public ImageSensorView imageSensorView() {
		ImageSensorView imsv = new ImageSensorView();
		imsv.setModel(imageSensor());
		List<BufferedImageOp> available = new ArrayList<>();
		available.add(new RotateImageOp());
		available.add(new RandomSweepOp());
		imsv.setAvailableFilters(available);
		imsv.refresh();
		
		return imsv;
	}

	@Bean
	public Parameters networkParameters() {
		Parameters p =  Parameters.getAllDefaultParameters();
		p.setColumnDimensions(dimensions);
		p.setInputDimensions(dimensions);

		return p;
	}

	@Bean
	public Parameters parameters23() {
		Parameters p =  Parameters.getAllDefaultParameters();
		p.setColumnDimensions(dimensions);
		p.setInputDimensions(dimensions);
		p.setCellsPerColumn(16);
		p.setSynPermTrimThreshold(0.1d);
		p.setPotentialRadius(2);
		p.setPotentialPct(1);
	

		return p;
	}
	
	@Bean
	public Parameters parameters4() {
		Parameters p =  Parameters.getAllDefaultParameters();
		p.setColumnDimensions(new int[] {48, 48});
		p.setInputDimensions(this.dimensions);
		p.setCellsPerColumn(16);
		p.setPotentialRadius(4);
		p.setSynPermTrimThreshold(0.1d);
	
		
		return p;
	}
	
	@Bean
	public Parameters parameters5() {
		Parameters p =  Parameters.getAllDefaultParameters();
		p.setColumnDimensions(new int[] {32, 32});
		p.setInputDimensions(new int[] {48, 48});
		p.setCellsPerColumn(8);
		p.setPotentialRadius(4);
		p.setSynPermTrimThreshold(0.1d);
		
		return p;
	}
	
	@Bean
	public Parameters parameters6() {
		Parameters p =  Parameters.getAllDefaultParameters();
		p.setColumnDimensions(new int[] {16, 16});
		p.setInputDimensions(new int[] {32, 32});
		p.setCellsPerColumn(8);
		p.setPotentialRadius(2);
		p.setSynPermTrimThreshold(0.1d);
		
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

