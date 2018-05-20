package info.joseluismartin.corvina.ui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.jdal.swing.AbstractView;
import org.jdal.swing.SimpleDialog;
import org.jdal.swing.form.BoxFormBuilder;

/**
 * Layer editor to define networks.
 * 
 * @author Jose Luis Martin.
 * @since 1.1
 */
public class LayerEditor extends AbstractView<LayerData> {
	
	private JTextField name = new JTextField();
	private JTextField nColumns = new JTextField();
	private JTextField nInputs = new JTextField();
	private JButton propertiesButton = new JButton("Properties");
	
	public LayerEditor() {
		this(new LayerData());
		this.propertiesButton.addActionListener(e -> showProperties());
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
		fb.setFixedHeight(true);
		fb.add(getMessage("name"), this.name);
		fb.add(getMessage("nColumns"), this.nColumns);
		fb.add(getMessage("nInputs"), this.nInputs);
		fb.add(this.propertiesButton, 150);
		
		return fb.getForm();
	}
	
	
	@Override
	protected void doUpdate() {
		LayerData data = getModel();
		data.setColumnsDimensions(toIntArray(this.nColumns.getText()));
		data.setInputDimensions(toIntArray(this.nInputs.getText()));
		data.setName(this.name.getText());
	}

	private int[] toIntArray(String value) {
		String[] part = value.split(",");
		int[] arry = new int[part.length];
		try {
			for (int i = 0; i < part.length; i++) {
				arry[i] = Integer.parseInt(part[i]);
			}
		}
		catch (NumberFormatException e) {
			
		}
		
		return arry;
	}

	private void showProperties() {
		ParametersEditor editor = new ParametersEditor(getModel().getParameters());
		editor.init();
		editor.refresh();
		SimpleDialog dlg = new SimpleDialog(editor.getPanel());
		dlg.setSize(800, 300);
		dlg.setModal(true);
		dlg.setLocationRelativeTo(this.getPanel());
		dlg.setVisible(true);
		
		if (dlg.isAccepted())
			editor.update();
	}
}
