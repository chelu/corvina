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
	
	
	@Override
	@SuppressWarnings("unchecked")
	public CorvinaModel preSerialize() {
		this.network.preSerialize();
	
		if (this.classifier instanceof Persistable)
			((Persistable) this.classifier).preSerialize();
		
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CorvinaModel postDeSerialize() {
		this.network.postDeSerialize();
		
		if (this.classifier instanceof Persistable)
			((Persistable) this.classifier).postDeSerialize();
		
		return this;
	}

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
