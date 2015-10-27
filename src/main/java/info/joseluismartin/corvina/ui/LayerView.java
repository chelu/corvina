package info.joseluismartin.corvina.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdal.swing.AbstractView;
import org.jdal.swing.form.BoxFormBuilder;
import org.jdal.swing.form.FormUtils;
import org.numenta.nupic.Connections;
import org.numenta.nupic.network.Layer;
import org.numenta.nupic.util.ArrayUtils;

/**
 * 2D View for {@link Layer}
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public class LayerView extends AbstractView<Layer<?>> implements ChangeListener {
	
	private static Log log = LogFactory.getLog(LayerView.class);
	
	private MatrixPanel spatial = new MatrixPanel();
	private MatrixPanel temporal = new MatrixPanel();
	private JSlider potentialRadius = new JSlider();
	private JSlider synPermTrimTreshold = new JSlider();
	private JSlider localAreaDensity = new JSlider();
	private JCheckBox globalInhibition = new JCheckBox();
	private JSlider permanenceDecrement = new JSlider();
	private JSlider permanenceIncrement = new JSlider();
	private JSlider synPermConnected = new JSlider();
	private boolean disabledListeners;
	
	public LayerView() {
	}
	
	public  LayerView(Layer<?> layer) {
		super(layer);
	}
	
	@Override
	protected JComponent buildPanel() {
		JScrollPane spatialScroll = new JScrollPane(this.spatial);
		JScrollPane temporalScroll = new JScrollPane(this.temporal);
		JPanel panel = new JPanel(new BorderLayout());
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spatialScroll, temporalScroll);
		split.setDividerLocation(0.5);
		split.setResizeWeight(0.5);
		panel.add(split, BorderLayout.CENTER);
		
		panel.add(createFormPanel(), BorderLayout.PAGE_START);
		
		configureSlider(this.potentialRadius);
		configureSlider(this.synPermTrimTreshold);
		configureSlider(this.localAreaDensity);
		configureSlider(this.permanenceDecrement);
		configureSlider(this.permanenceIncrement);
		
		this.globalInhibition.addActionListener( e -> {
			if (getModel() == null) 
				getModel().getConnections().setGlobalInhibition(globalInhibition.isSelected());
		});
		
		return panel;
	}
	
	private Component createFormPanel() {
		BoxFormBuilder fb = new BoxFormBuilder(FormUtils.createEmptyBorder(5));
		fb.row(50);
		fb.add("Potential Radius", this.potentialRadius);
		fb.add("SynPermTrimTreshold", this.synPermTrimTreshold);
		fb.add("Local Area Density", this.localAreaDensity);
		fb.row(50);
		fb.add("Permanence Inc", this.permanenceIncrement);
		fb.add("Permanence Dec", this.permanenceDecrement);
		fb.add("Global Innibition", globalInhibition);
		
		return fb.getForm();
	}

	private void configureSlider(JSlider slider) {
		slider.setMaximum(100);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		
		slider.addChangeListener(this);
	}

	@Override
	protected void doRefresh() {
		Layer<?> layer = getModel();
		
		if (layer == null)
			return;
		
		
		this.temporal.setDimensions(layer.getConnections().getMemory().getDimensions());
		this.spatial.setDimensions(layer.getConnections().getMemory().getDimensions());
		int[] values = new int[layer.getConnections().getNumColumns()];
		ArrayUtils.setIndexesTo(values, layer.getPredictedColumns(), 1);
		this.temporal.setValues(values);
		this.temporal.repaint();
		this.spatial.setValues(layer.getActiveColumns());
		this.spatial.repaint();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (this.disabledListeners || source.getValueIsAdjusting())
			return;
		
		Layer<?> model = getModel();
		
		if (model == null)
			return;
		
		if (source == this.potentialRadius) {
			model.getConnections().setPotentialRadius(this.potentialRadius.getValue());
		}
		else if (source == this.synPermTrimTreshold) {
			model.getConnections().setSynPermTrimThreshold(toPercent(this.synPermTrimTreshold.getValue()));
		}
		else if (source == this.localAreaDensity) {
			model.getConnections().setLocalAreaDensity(toPercent(this.localAreaDensity.getValue()));
		}
		else if (source == this.permanenceDecrement) {
			model.getConnections().setPermanenceDecrement(toPercent(this.permanenceDecrement.getValue()));
		}
		else if (source == this.permanenceIncrement) {
			model.getConnections().setPermanenceIncrement(toPercent(this.permanenceIncrement.getValue()));
		}
		else if (source == this.synPermConnected) {
			model.getConnections().setSynPermConnected(toPercent(this.permanenceIncrement.getValue()));
		}
		
	}
	
	@Override
	protected void onSetModel(Layer<?> model) {
		setDisabledListeners(true);
		refreshControls(model);
		setDisabledListeners(false);
		
	}
	
	private void refreshControls(Layer<?> model) {
		if (model == null)
			return;
		
		Connections c = model.getConnections();
		
		this.potentialRadius.setValue(c.getPotentialRadius());
		this.synPermTrimTreshold.setValue(fromPercent(c.getSynPermTrimThreshold()));
		this.permanenceDecrement.setValue(fromPercent(c.getPermanenceDecrement()));
		this.permanenceIncrement.setValue(fromPercent(c.getPermanenceIncrement()));
		this.synPermConnected.setValue(fromPercent(c.getSynPermConnected()));
		this.globalInhibition.setSelected(c.getGlobalInhibition());
		
		
		this.localAreaDensity.setValue(fromPercent(c.getLocalAreaDensity()));
	}

	private int fromPercent(double value) {
		int full =  (int) value * 100;
		log.debug("value: [" + full + "]");
		
		return full;
 	}
	
	private double toPercent(int value) {
		return (double) value / 100;
	}
	
	/**
	 * @return the disabledListeners
	 */
	public boolean isDisabledListeners() {
		return disabledListeners;
	}

	/**
	 * @param disabledListeners the disabledListeners to set
	 */
	public void setDisabledListeners(boolean disabledListeners) {
		this.disabledListeners = disabledListeners;
	}
	

}
