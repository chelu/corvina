package info.joseluismartin.corvina.ui;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.View;

import org.jdal.swing.AbstractView;
import org.jdal.swing.form.BoxFormBuilder;
import org.jdal.swing.form.FormUtils;
import org.numenta.nupic.Parameters;

/**
 * {@link View} for Layer related parameters.
 * 
 * @author Jose Luis Martin.
 * @since 1.0
 */
public class LayerParametersView extends AbstractView<Parameters> {
	
	private JTextField rows = new JTextField();
	private JTextField columns = new JTextField();
	private JTextField potentialRadius = new JTextField();
	private JTextField synPermTrimThreshold = new JTextField();

	@Override
	protected JComponent buildPanel() {
		BoxFormBuilder fb = new BoxFormBuilder(FormUtils.createEmptyBorder(5));
		fb.row();
		fb.add(getMessage("rows"), this.rows);
		fb.row();
		fb.add(getMessage("columns"), this.columns);
		fb.row();
		fb.add(getMessage("potentialRadius"), this.potentialRadius);
		fb.row();
		fb.add(getMessage("synPermTrimThreshold"), this.synPermTrimThreshold);
		
		return fb.getForm();
	}

}
