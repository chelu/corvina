package info.joseluismartin.corvina.ui;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jdal.swing.AbstractView;
import org.jdal.swing.form.BoxFormBuilder;
import org.numenta.nupic.Parameters;
import org.numenta.nupic.Parameters.KEY;

public class ParametersEditor extends AbstractView<Parameters> {
	
	private JTextField cellsPerColumn = new JTextField();
	private JTextField potentialRadius = new JTextField();
	private JTextField synPermConnected = new JTextField();
	private JTextField synPermTrimThreshold = new JTextField();
	private JCheckBox globalInhibition = new JCheckBox();
	private JTextField permanenceDecrement = new JTextField();
	private JTextField permanenceIncrement = new JTextField();
	private JTextField maxBoost = new JTextField();
	private JTextField potentialPct = new JTextField();
	private JTextField localAreaDensity = new JTextField();
	private JTextField initialPermanence = new JTextField();
	private JTextField connectedPermanence = new JTextField();
	private JTextField minThresold = new JTextField();
	private JTextField activationThresold = new JTextField();
	private JTextField maxNewSypnaseCount = new JTextField();
	private JTextField seed = new JTextField();
	private JTextField learningRadius = new JTextField();
	private JTextField numActiveColumnsPerInhArea = new JTextField();
	
	private JTextField maxSynapsesPerSegment = new JTextField();
	private JTextField maxSegmentsPerCell = new JTextField();
	private JTextField inhibitionRadius = new JTextField();
	private JTextField stimulusThreshold = new JTextField();
	private JTextField synPermInactiveDec = new JTextField();
	private JTextField synPermActiveInc = new JTextField();
	private JTextField synPermBelowStimulusInc = new JTextField();
	private JTextField minPctOverlapDutyCycles = new JTextField();
	private JTextField minPctActiveDutyCycles = new JTextField();
	private JTextField dutyCyclePeriod = new JTextField();
	
	
	/**
	 * 
	 */
	public ParametersEditor() {
		this(Parameters.getAllDefaultParameters());
	}

	/**
	 * @param model
	 */
	public ParametersEditor(Parameters model) {
		super(model);
	}
	
	public void init() {
		
	}

	@Override
	protected JComponent buildPanel() {
		BoxFormBuilder fb = new BoxFormBuilder();
		fb.setDebug(true);
		fb.row();
		fb.setDebug(false);
		fb.add(getMessage("cellPerColumn"), this.cellsPerColumn);
		fb.add(getMessage("potentialRadius"), this.potentialRadius);
		fb.add(getMessage("symPermConnected"), this.synPermConnected);
		fb.row();
		fb.add(getMessage("synPermTrimThreshold"), this.synPermTrimThreshold);
		fb.add(getMessage("learningRadius"), this.learningRadius);
		fb.add(getMessage("permanenceDecrement"),this.permanenceDecrement);
		fb.row();
		fb.add(getMessage("permanenceIncrement"), this.permanenceIncrement);
		fb.add(getMessage("maxBoost"), this.maxBoost);
		fb.add(getMessage("potencialPtc"), this.potentialPct);
		fb.row();
		fb.add(getMessage("localAreaDensity"), this.localAreaDensity);
		fb.add(getMessage("initialPermanence"), this.initialPermanence);
		fb.add(getMessage("connectedPermanence"), this.connectedPermanence);
		fb.row();
		fb.add(getMessage("minThresold"), this.minThresold);
		fb.add(getMessage("activationThresold"), this.activationThresold);
		fb.add(getMessage("maxNewSypnaseCount"), this.maxNewSypnaseCount);
		fb.row();
		fb.add(getMessage("seed"), this.seed);
		fb.add(getMessage("numActiveColumnsPerInhArea"), this.numActiveColumnsPerInhArea);
		fb.add(getMessage("globalInhibition"), this.globalInhibition);
		fb.row();
		fb.add(getMessage("maxSynapsesPerSegment"), this.maxSynapsesPerSegment);
		fb.add(getMessage("maxSegmentsPerCell"), this.maxSegmentsPerCell);
		fb.add(getMessage("inhibitionRadius"), this.inhibitionRadius);
		fb.row();
		fb.add(getMessage("stimulusThreshold"), this.stimulusThreshold);
		fb.add(getMessage("synPermBelowStimulusInc"), this.synPermBelowStimulusInc);
		fb.add(getMessage("synPermActiveInc"), this.synPermActiveInc);
		fb.row();
		fb.add(getMessage("synPermInactiveDec"), this.synPermInactiveDec);
		fb.add(getMessage("minPctOverlapDutyCycles"), this.minPctOverlapDutyCycles);
		fb.add(getMessage("minPctActiveDutyCycles"), this.minPctActiveDutyCycles);
		fb.row();
		fb.add(getMessage("dutyCyclePeriod"), this.dutyCyclePeriod);
		fb.add("", new JLabel(""));
		fb.add("", new JLabel(""));
		
		return fb.getForm();
	}

	@Override
	protected void doUpdate() {
		Parameters params  = this.getModel();
		params.setCellsPerColumn(toInt(this.cellsPerColumn.getText()));
		params.setPotentialRadius(toInt(this.potentialRadius.getText()));
		params.setSynPermConnected(toDouble(this.synPermConnected.getText()));
		params.setSynPermTrimThreshold(toDouble(this.synPermTrimThreshold.getText()));
		params.setGlobalInhibition(this.globalInhibition.isSelected());
		params.setPermanenceDecrement(toDouble(this.permanenceDecrement.getText()));
		params.setMaxBoost(toDouble(this.maxBoost.getText()));
		params.setPotentialPct(toDouble(this.potentialPct.getText()));
		params.setLocalAreaDensity(toDouble(this.localAreaDensity.getText()));
		params.setInitialPermanence(toDouble(this.initialPermanence.getText()));
		params.setConnectedPermanence(toDouble(this.connectedPermanence.getText()));
		params.setMinThreshold(toInt(this.minThresold.getText()));
		params.setActivationThreshold(toInt(this.activationThresold.getText()));
		params.setMaxNewSynapseCount(toInt(this.maxNewSypnaseCount.getText()));
		params.setSeed(toInt(this.seed.getText()));
		params.setLearningRadius(toInt(this.learningRadius.getText()));
		params.setNumActiveColumnsPerInhArea(toDouble(this.numActiveColumnsPerInhArea.getText()));
		params.setMaxSynapsesPerSegment(toInt(this.maxSynapsesPerSegment.getText()));
		params.setMaxSegmentsPerCell(toInt(this.maxSegmentsPerCell.getText()));
		params.setInhibitionRadius(toInt(this.inhibitionRadius.getText()));
		params.setStimulusThreshold(toDouble(this.stimulusThreshold.getText()));
		params.setSynPermActiveInc(toDouble(synPermActiveInc.getText()));
		params.setSynPermInactiveDec(toDouble(this.synPermInactiveDec.getText()));
		params.setSynPermBelowStimulusInc(toDouble(this.synPermBelowStimulusInc.getText()));
		params.setMinPctOverlapDutyCycles(toDouble(this.minPctOverlapDutyCycles.getText()));
		params.setMinPctActiveDutyCycles(toDouble(this.minPctActiveDutyCycles.getText()));
		params.setDutyCyclePeriod(toInt(this.dutyCyclePeriod.getText()));
	}

	@Override
	protected void doRefresh() {
		Parameters p  = getModel();
		this.cellsPerColumn.setText(String.valueOf(p.get(KEY.CELLS_PER_COLUMN)));
		this.potentialRadius.setText(String.valueOf(p.get(KEY.POTENTIAL_RADIUS)));
		this.synPermConnected.setText(String.valueOf(p.get(KEY.SYN_PERM_CONNECTED)));
		this.synPermTrimThreshold.setText(String.valueOf(p.get(KEY.SYN_PERM_TRIM_THRESHOLD)));
		this.globalInhibition.setSelected((boolean) p.get(KEY.GLOBAL_INHIBITION));
		this.permanenceDecrement.setText(String.valueOf(p.get(KEY.PERMANENCE_DECREMENT)));
		this.permanenceIncrement.setText(String.valueOf(p.get(KEY.PERMANENCE_INCREMENT)));
		this.maxBoost.setText(String.valueOf(p.get(KEY.MAX_BOOST)));
		this.potentialPct.setText(String.valueOf(p.get(KEY.POTENTIAL_PCT)));
		this.localAreaDensity.setText(String.valueOf(p.get(KEY.LOCAL_AREA_DENSITY)));
		this.initialPermanence.setText(String.valueOf(p.get(KEY.INITIAL_PERMANENCE)));
		this.connectedPermanence.setText(String.valueOf(p.get(KEY.CONNECTED_PERMANENCE)));
		this.minThresold.setText(String.valueOf(p.get(KEY.MIN_THRESHOLD)));
		this.activationThresold.setText(String.valueOf(p.get(KEY.ACTIVATION_THRESHOLD)));
		this.maxNewSypnaseCount.setText(String.valueOf(p.get(KEY.MAX_NEW_SYNAPSE_COUNT)));
		this.seed.setText(String.valueOf(p.get(KEY.SEED)));
		this.learningRadius.setText(String.valueOf(p.get(KEY.LEARNING_RADIUS)));
		this.numActiveColumnsPerInhArea.setText(String.valueOf(p.get(KEY.NUM_ACTIVE_COLUMNS_PER_INH_AREA)));
		this.maxSynapsesPerSegment.setText(String.valueOf(p.get(KEY.MAX_SYNAPSES_PER_SEGMENT)));
		this.maxSegmentsPerCell.setText(String.valueOf(p.get(KEY.MAX_SEGMENTS_PER_CELL)));
		this.inhibitionRadius.setText(String.valueOf(p.get(KEY.INHIBITION_RADIUS)));
		this.stimulusThreshold.setText(String.valueOf(p.get(KEY.STIMULUS_THRESHOLD)));
		this.synPermActiveInc.setText(String.valueOf(p.get(KEY.SYN_PERM_ACTIVE_INC)));
		this.synPermInactiveDec.setText(String.valueOf(p.get(KEY.SYN_PERM_INACTIVE_DEC)));
		this.synPermBelowStimulusInc.setText(String.valueOf(p.get(KEY.SYN_PERM_BELOW_STIMULUS_INC)));
		this.minPctActiveDutyCycles.setText(String.valueOf(p.get(KEY.MIN_PCT_ACTIVE_DUTY_CYCLES)));
		this.minPctOverlapDutyCycles.setText(String.valueOf(p.get(KEY.MIN_PCT_OVERLAP_DUTY_CYCLES)));
		this.dutyCyclePeriod.setText(String.valueOf(p.get(KEY.DUTY_CYCLE_PERIOD)));
	}
	
	private int toInt(String value) {
		try {
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}
	
	private double toDouble(String value) {
		try {
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

	public static void main(String args[]) {
		JFrame f = new JFrame();
		f.setSize(800, 600);
		ParametersEditor pe = new ParametersEditor();
		pe.init();
		
		f.getContentPane().add(pe.getPanel());
		pe.refresh();
		f.setVisible(true);
	}
}
