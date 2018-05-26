package info.joseluismartin.corvina.ui;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
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
	private JCheckBox usingSpatialPooler = new JCheckBox("Add Spatial Pooler");
	private JCheckBox usingTemporalMemory = new JCheckBox("Add Temporal Memory");
	
	public LayerEditor() {
		this(new LayerData());
	}

	/**
	 * @param model
	 */
	public LayerEditor(LayerData model) {
		super(model);
		this.propertiesButton.addActionListener(e -> showProperties());
		this.usingSpatialPooler.setSelected(true);
	}

	@Override
	protected JComponent buildPanel() {
		BoxFormBuilder fb = new BoxFormBuilder();
		fb.setDebug(true);
		fb.row();
		fb.setFixedHeight(true);
		fb.add(getMessage("name"), this.name);
		fb.add(getMessage("nColumns"), this.nColumns);
		fb.add(getMessage("nInputs"), this.nInputs);
		fb.add(this.propertiesButton, 150);
		fb.row();
		fb.add(new JLabel());
		fb.add(this.usingSpatialPooler);
		fb.add(new JLabel());
		fb.add(this.usingTemporalMemory);
		
		
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
	
	public boolean isUsingSpatialPooler() {
		return this.usingSpatialPooler.isSelected();
	}
	
	public boolean isUsingTemporalMemory() {
		return this.usingTemporalMemory.isSelected();
	}
}
