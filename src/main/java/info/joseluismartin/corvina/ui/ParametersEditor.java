package info.joseluismartin.corvina.ui;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jdal.swing.AbstractView;
import org.jdal.swing.form.BoxFormBuilder;
import org.numenta.nupic.Parameters;

public class ParametersEditor extends AbstractView<Parameters> {
	
	private JTextField cellsPerColumn = new JTextField();
	private JTextField potentialRadius = new JTextField();
	private JTextField synPermConnected = new JTextField();
	private JTextField synPermTrimThresold = new JTextField();
	private JCheckBox globalInhibition = new JCheckBox();
	private JTextField permanenceDecrement = new JTextField();
	private JTextField permanenceIncrement = new JTextField();
	private JTextField maxBoost = new JTextField();
	private JTextField potentialPct = new JTextField();
	private JTextField localAreaDensity = new JTextField();
	private JTextField intialPermanence = new JTextField();
	private JTextField connectedPermanence = new JTextField();
	private JTextField minThresold = new JTextField();
	private JTextField activationThresold = new JTextField();
	private JTextField maxNewSypnaseCount = new JTextField();
	private JTextField seed = new JTextField();
	private JTextField learningRadius = new JTextField();
	
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
		BoxFormBuilder fb = new BoxFormBuilder();fb.add(new JLabel());
		fb.row();
		fb.setDebug(true);
		fb.add(getMessage("cellPerColumn"), this.cellsPerColumn);
		fb.add(getMessage("potentialRadius"), this.potentialRadius);
		fb.add(getMessage("symPermConnected"), this.synPermConnected);
		fb.add(getMessage("symPermTrimThresold"), this.synPermTrimThresold);
		fb.row();
		fb.add(getMessage("learningRadius"), this.learningRadius);
		fb.add(getMessage("permanenceDecrement"),this.permanenceDecrement);
		fb.add(getMessage("permanenceIncrement"), this.permanenceIncrement);
		fb.add(getMessage("maxBoost"), this.maxBoost);
		fb.row();
		fb.add(getMessage("potencialPtc"), this.potentialPct);
		fb.add(getMessage("localAreaDensity"), this.localAreaDensity);
		fb.add(getMessage("intialPermanence"), this.intialPermanence);
		fb.add(getMessage("connectedPermanence"), this.connectedPermanence);
		fb.row();
		fb.add(getMessage("minThresold"), this.minThresold);
		fb.add(getMessage("activationThresold"), this.activationThresold);
		fb.add(getMessage("maxNewSypnaseCount"), this.maxNewSypnaseCount);
		fb.add(getMessage("seed"), this.seed);
		fb.row();
		fb.add(getMessage("globalInhibition"), this.globalInhibition);
		fb.add("", new JLabel());
		fb.add("", new JLabel());
		fb.add("", new JLabel());
		
		return fb.getForm();
	}

	public static void main(String args[]) {
		JFrame f = new JFrame();
		f.setSize(800, 600);
		ParametersEditor pe = new ParametersEditor();
		pe.init();
		
		f.getContentPane().add(pe.getPanel());
		f.setVisible(true);
	}
}
