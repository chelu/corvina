package info.joseluismartin.corvina.ui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdal.swing.AbstractView;
import org.jdal.swing.form.BoxFormBuilder;
import org.jdal.swing.form.SimpleBoxFormBuilder;
import org.numenta.nupic.Parameters;
import org.numenta.nupic.algorithms.SpatialPooler;
import org.numenta.nupic.algorithms.TemporalMemory;
import org.numenta.nupic.network.Layer;
import org.numenta.nupic.network.Network;
import org.numenta.nupic.network.Region;

public class NetworkEditor extends AbstractView<Network> implements ActionListener {

	private final static Log log = LogFactory.getLog(NetworkEditor.class);

	private JTextField numberOfLayers = new JTextField();
	private JButton createButton = new JButton("Create");
	private JPanel layerPanel = new JPanel();
	private List<LayerEditor> layers = new ArrayList<>(); 

	@Override
	protected JComponent buildPanel() {
		this.layerPanel.setLayout(new BorderLayout());
		this.createButton.addActionListener(this);

		BoxFormBuilder fb = new BoxFormBuilder(new TitledBorder("Network"));
		fb.row();
		fb.startBox();
		fb.setFixedHeight(true);
		fb.add("Number of layers", this.numberOfLayers );
		fb.add(this.createButton, 100);
		fb.endBox();
		fb.row(SimpleBoxFormBuilder.SIZE_UNDEFINED);
		fb.add(this.layerPanel);

		return fb.getForm();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.createButton)
			createLayerEditor();
	}

	private void createLayerEditor() {
		int nLayers = getNumberOfLayers();
		if (nLayers > 0) {
			BoxFormBuilder fb = new BoxFormBuilder();
			for (int i = 0; i < nLayers; i++) {
				LayerEditor le = new LayerEditor();
				layers.add(le);
				fb.row(60);
				fb.add(le.getPanel());
			}
			this.layerPanel.removeAll();
			this.layerPanel.add(fb.getForm());
		}
		
		Window w = SwingUtilities.getWindowAncestor(this.layerPanel);
		
		if (w != null) {
			Rectangle rec = w.getBounds();
			rec.height = rec.height + 1;
			w.setBounds(rec);
		}
		
	}

	private int getNumberOfLayers() {
		String value = this.numberOfLayers.getText();
		int number = 0;

		try {
			number = Integer.valueOf(value);
		}
		catch (NumberFormatException nfe) {
			log.error(nfe);// TODO Auto-generated method stub
		}

		return number;
	}

	public Network createNetwork() {
		// One region with all layers
		Parameters networkParams = Parameters.getAllDefaultParameters();
		
		if (this.layers.size() > 0) {
			layers.get(0).update();
			networkParams.setInputDimensions(layers.get(0).getModel().getInputDimensions());
			networkParams.setColumnDimensions(layers.get(0).getModel().getColumnsDimensions());
		}
		
		Network network = new Network("CORVINA", networkParams);
		
		int regionIdx = 1;
		
		for (LayerEditor le : layers) {
			le.update();
			Parameters p = le.getModel().getParameters();
			p.setColumnDimensions(le.getModel().getColumnsDimensions());
			p.setInputDimensions(le.getModel().getInputDimensions());
			Layer<?> layer = Network.createLayer(le.getModel().getName(), p);
			
			if (le.isUsingSpatialPooler())
				layer.add(new SpatialPooler());
			
			if (le.isUsingTemporalMemory())
				layer.add(new TemporalMemory());
			
			Region region = new Region("R" + regionIdx++, network);
			region.add(layer);
			network.add(region);
					
		}
		
		List<Region> regions = network.getRegions();

		if (regions.size() > 1) {
			for (int i = 0; i < regions.size() - 1; i++) {
				String nameDown = regions.get(i).getName();
				String nameUp = regions.get(i+1).getName();

				if (log.isDebugEnabled()) 
					log.debug("Connecting region [" + nameDown + "] to region [" + nameUp + "].");

				network.connect(nameUp, nameDown);
			}
		}
		
		
		return network;
	}
	
	public static final void main(String[] args) {
		JFrame f = new JFrame();
		f.setSize(800, 300);
		NetworkEditor networkEditor = new NetworkEditor();
		f.getContentPane().add(networkEditor.buildPanel());
		f.setVisible(true);
	}
}
