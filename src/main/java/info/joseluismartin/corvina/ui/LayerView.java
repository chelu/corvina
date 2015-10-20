package info.joseluismartin.corvina.ui;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdal.swing.AbstractView;
import org.numenta.nupic.network.Layer;
import org.numenta.nupic.network.Network;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 2D View for {@link Layer}
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public class LayerView extends AbstractView<Layer<?>> {

	@Autowired
	private Network network;
	
	public LayerView() {
	}
	
	public  LayerView(Layer<?> layer) {
		super(layer);
	}
	
	
	@Override
	protected JComponent buildPanel() {
		JPanel panel =  new MatrixPanel();
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		return panel;
	}
	
	@Override
	protected void doRefresh() {
		Layer<?> layer = getModel();
		
		if (layer == null)
			return;
		
		MatrixPanel panel = (MatrixPanel) getPanel();
		panel.setDimensions(layer.getConnections().getMemory().getDimensions());
		panel.setValues(layer.getActiveColumns());
		panel.repaint();
	}
	

}
