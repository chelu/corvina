package info.joseluismartin.corvina.config;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.numenta.nupic.Parameters;
import org.numenta.nupic.algorithms.Anomaly;
import org.numenta.nupic.algorithms.SpatialPooler;
import org.numenta.nupic.algorithms.TemporalMemory;
import org.numenta.nupic.network.Layer;
import org.numenta.nupic.network.Network;
import org.numenta.nupic.network.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	public static final String LAYER_5 = "Layer 4";

	/**
	 * Creates the corvina htm network
	 * @return 
	 */
	@Bean
	public Network network() {
		Network network = new Network(CORVINA, parameters());
		network.add(region1(network))
		.add(region2(network))
		.connect(REGION_2, REGION_1);

		return network;
	}

	/**
	 * First region with two layers.
	 * @return
	 */
	@Bean
	public Region region1(Network network) {
		Region region = new Region(REGION_1, network);
		region.add(Network.createLayer(LAYER_23, parameters())
				.add(new TemporalMemory())
				.add(Anomaly.create())
				.add(new SpatialPooler()))
		.add(Network.createLayer(LAYER_4, parameters())
				.add(new TemporalMemory())
				.add(new SpatialPooler()))
		.connect(LAYER_4, LAYER_23);

		return region;
	}

	/**
	 * Second region with two layers
	 * @return
	 */
	@Bean
	public Region region2(Network network) {
		Region region = new Region(REGION_2, network);
		region.add(Network.createLayer(LAYER_23, parameters())
				.add(new TemporalMemory())
				.add(Anomaly.create())
				.add(new SpatialPooler()))
		.add(Network.createLayer(LAYER_4, parameters())
				.add(new TemporalMemory())
				.add(new SpatialPooler()))
		.connect(LAYER_4, LAYER_23);

		return region;
	}


	@Bean
	public Parameters parameters() {
		Parameters p =  Parameters.getAllDefaultParameters();
		p.setColumnDimensions(new int[] {15625});

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
	public JFrame mainFrame() {
		MainFrame mainFrame = new MainFrame(); 
		
		return mainFrame;
	}
	
	/**
	 * Network viewer
	 * @return
	 */
	@Bean
	public NetworkView networkView() {
		NetworkView networkView = new NetworkView(network());
		
		return networkView;
	}
}

