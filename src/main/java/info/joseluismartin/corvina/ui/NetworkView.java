package info.joseluismartin.corvina.ui;

import java.awt.BorderLayout;
import java.awt.Panel;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdal.swing.AbstractView;
import org.jdal.swing.form.FormUtils;
import org.numenta.nupic.network.Network;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 2D View for {@link Network}
 * 
 * @author Jose Luis Martin.
 * @since 1.0	
 */
public class NetworkView extends AbstractView<Network> {

	@Autowired
	private LayerView layerView;
	
	public NetworkView() {
		super();
	}

	public NetworkView(Network model) {
		super(model);
	}

	@Override
	protected JComponent buildPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(FormUtils.createEmptyBorder(5));
		panel.add(new JScrollPane(this.layerView.getPanel()), BorderLayout.CENTER);

		return panel;
	}

	@Override
	protected void doRefresh() {
		Network model = getModel();
		
		if (model == null)
			return;
		
		this.layerView.setModel(model.getRegions().get(0).getTail());
		this.layerView.refresh();
	}

}
