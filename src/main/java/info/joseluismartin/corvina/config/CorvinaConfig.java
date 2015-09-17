package info.joseluismartin.corvina.config;

import java.util.ArrayList;
import java.util.List;

import org.numenta.nupic.Parameters;
import org.numenta.nupic.algorithms.SpatialPooler;
import org.numenta.nupic.algorithms.TemporalMemory;
import org.numenta.nupic.network.Layer;
import org.numenta.nupic.network.Network;
import org.numenta.nupic.network.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

	/**
	  * Creates the corvina htm network
	  * @return 
	  */
	 @Bean
	 public Network network() {
		 Network network = new Network(CORVINA, parameters())
				 .add(region1())
				 .add(region2())
				 .connect(REGION_1, REGION_2);
				 
		 
		 return network;
	 }
	
	 
	 /**
	  * First region with two layers.
	  * @return
	  */
	 @Bean
	 public Region region1() {
		 Region region = new Region(REGION_1, network());
		 region.add(Network.createLayer(LAYER_23, parameters())
				 .add(new TemporalMemory()))
		 .add(Network.createLayer(LAYER_4, parameters())
				 .add(new SpatialPooler()))
		 .connect(LAYER_23, LAYER_4);

		 return region;
	 }
	 
	 @Bean
	 public Region region2() {
		 Region region = new Region(REGION_2, network());
		 region.add(Network.createLayer(LAYER_23, parameters())
				 .add(new TemporalMemory()))
		 .add(Network.createLayer(LAYER_4, parameters())
				 .add(new SpatialPooler()))
		 .connect(LAYER_23, LAYER_4);

		 return region;
	 }
	 
	 
	 @Bean
	 public Parameters parameters() {
		 Parameters p =  Parameters.getAllDefaultParameters();
		 
		 return p;
	 }
	 
	 public List<Layer<?>> layers() {
		 ArrayList<Layer<?>> layers = new ArrayList<>();
		 
		 return layers;
	 }
}

