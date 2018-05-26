package info.joseluismartin.corvina.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.MutableComboBoxModel;

import org.jdal.swing.AbstractView;
import org.jdal.swing.form.BoxFormBuilder;
import org.jdal.swing.form.FormUtils;
import org.numenta.nupic.network.Layer;
import org.numenta.nupic.network.Network;
import org.numenta.nupic.network.Region;
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
	private JComboBox<LayerHolder> layerCombo;
	
	public NetworkView() {
		super();
	}

	public NetworkView(Network model) {
		super(model);
	}
	
	@PostConstruct
	public void init() {
		this.layerCombo = new JComboBox<LayerHolder>();
		this.layerCombo.addActionListener(e -> updateLayer());
	}

	@Override
	protected JComponent buildPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(FormUtils.createEmptyBorder(5));
		panel.add(new JScrollPane(this.layerView.getPanel()), BorderLayout.CENTER);
		BoxFormBuilder fb = new BoxFormBuilder(FormUtils.createEmptyBorder(5));
		fb.row();
		fb.add(this.layerCombo);
		panel.add(fb.getForm(), BorderLayout.NORTH);

		return panel;
	}

	@Override
	protected void doRefresh() {
		Network model = getModel();
		
		if (model == null)
			return;		
		
		MutableComboBoxModel<LayerHolder> comboModel = 
				(MutableComboBoxModel<LayerHolder>) this.layerCombo.getModel();
	
		
		if (comboModel.getSize() == 0) {
			for (Layer<?> l : getLayers())
				comboModel.addElement(new LayerHolder(l));
		}
		
		this.layerView.refresh();
	}
	
	public void updateLayerCombo() {
		
		this.layerCombo.removeAllItems();
		
		MutableComboBoxModel<LayerHolder> comboModel = 
				(MutableComboBoxModel<LayerHolder>) this.layerCombo.getModel();
		
		for (Layer<?> l : getLayers())
				comboModel.addElement(new LayerHolder(l));
		
		this.layerCombo.setSelectedIndex(0);
	}
	
	protected List<Layer<?>> getLayers() {
		List<Layer<?>> layers = new ArrayList<>();
		
		for (Region region : this.getModel().getRegions()) {
			Layer<?> layer = region.getTail();
		
			while (layer != null) {
				layers.add(layer);
				layer = layer.getNext();
			}
		}
		
		return layers;
	}
	
	private void updateLayer() {
		LayerHolder lh = (LayerHolder) this.layerCombo.getSelectedItem();
		
		if (lh != null) {
			this.layerView.setModel(lh.getLayer());
			this.layerView.refresh();
		}
	}
}

class LayerHolder {
	private Layer<?> layer;
	
	public LayerHolder(Layer<?> layer) {
		this.layer = layer;
	}
	
	public Layer<?> getLayer() {
		return this.layer;
	}
	
	public String toString() {
		return this.layer.getName();
	}
}

