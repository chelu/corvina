package info.joseluismartin.corvina.model;

import org.numenta.nupic.algorithms.Classifier;
import org.numenta.nupic.model.Persistable;
import org.numenta.nupic.network.Network;

/**
 * Holder for persistent data.
 * 
 * @author Jose Luis Martin
 * @since 1.1
 */
public class CorvinaModel implements Persistable {
	
	private Network network;
	private Classifier classifier;
	/**
	 * @return the network
	 */
	public Network getNetwork() {
		return network;
	}
	/**
	 * @param network the network to set
	 */
	public void setNetwork(Network network) {
		this.network = network;
	}
	/**
	 * @return the classifier
	 */
	public Classifier getClassifier() {
		return classifier;
	}
	/**
	 * @param classifier the classifier to set
	 */
	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}
}
