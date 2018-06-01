package info.joseluismartin.corvina.ui;

import org.numenta.nupic.Parameters;

public class LayerData {
	
	private int[] columnsDimensions;
	private int[] inputDimensions;
	private String name;
	private Parameters parameters = Parameters.getAllDefaultParameters();
	
	public LayerData() {
		configureParamters(this.parameters);
	}
	
	private void configureParamters(Parameters p) {
		p.setSeed(1956);
		
//		p.setCellsPerColumn(32);
//		p.setPotentialRadius(8);
//		p.setSynPermConnected(0.2);
//		p.setSynPermTrimThreshold(0.1d);
//		p.setGlobalInhibition(true);
//		p.setPermanenceDecrement(0.1);
//		p.setPermanenceIncrement(0.1);
//		p.setMaxBoost(1);
//		p.setPotentialPct(0.5);
//		p.setLocalAreaDensity(-1);
//		p.setInitialPermanence(0.4);
//		p.setConnectedPermanence(0.2);
//		p.setMinThreshold(10);
//		p.setActivationThreshold(10);
//		p.setMaxNewSynapseCount(50);
//		p.setLearningRadius(8);
//		
	}
	
	/**
	 * @return the parameters
	 */
	public Parameters getParameters() {
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the columnsDimensions
	 */
	public int[] getColumnsDimensions() {
		return columnsDimensions;
	}

	/**
	 * @param columnsDimensions the columnsDimensions to set
	 */
	public void setColumnsDimensions(int[] columnsDimensions) {
		this.columnsDimensions = columnsDimensions;
	}

	/**
	 * @return the inputDimensions
	 */
	public int[] getInputDimensions() {
		return inputDimensions;
	}

	/**
	 * @param inputDimensions the inputDimensions to set
	 */
	public void setInputDimensions(int[] inputDimensions) {
		this.inputDimensions = inputDimensions;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
