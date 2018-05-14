package info.joseluismartin.corvina.ui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.jdal.swing.AbstractView;
import org.jdal.swing.form.BoxFormBuilder;

/**
 * Layer editor to define networks.
 * 
 * @author Jose Luis Martin.
 * @since 1.1
 */
public class LayerEditor extends AbstractView<LayerData> {
	
	private JTextField nColumns = new JTextField();
	private JTextField nInputs = new JTextField();
	private JButton propertiesButton = new JButton("Properties");
	
	public LayerEditor() {
		this(new LayerData());
	}

	/**
	 * @param model
	 */
	public LayerEditor(LayerData model) {
		super(model);
	}

	@Override
	protected JComponent buildPanel() {
		BoxFormBuilder fb = new BoxFormBuilder();
		fb.row();
		fb.add(getMessage("nColumns"), this.nColumns);
		fb.add(getMessage("nInputs"), this.nInputs);
		fb.add(this.propertiesButton);
		
		return fb.getForm();
	}
	
	

}
