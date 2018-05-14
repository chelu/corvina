package info.joseluismartin.corvina.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdal.swing.AbstractView;
import org.jdal.swing.form.BoxFormBuilder;
import org.jdal.swing.form.SimpleBoxFormBuilder;
import org.numenta.nupic.network.Network;

public class NetworkEditor extends AbstractView<Network> implements ActionListener {

	private final static Log log = LogFactory.getLog(NetworkEditor.class);
	
	private JTextField numberOfLayers = new JTextField();
	private JButton createButton = new JButton("Create");
	JScrollPane layerPane = new JScrollPane();
	
	
	@Override
	protected JComponent buildPanel() {
		this.createButton.addActionListener(this);
		
		BoxFormBuilder fb = new BoxFormBuilder(new TitledBorder("Network"));
		fb.row();
		fb.startBox();
		fb.setFixedHeight(true);
		fb.add("Number of layers", this.numberOfLayers );
		fb.add(this.createButton, 100);
		fb.endBox();
		fb.row(SimpleBoxFormBuilder.SIZE_UNDEFINED);
		fb.add(this.layerPane);
		return fb.getForm();
	}
	
	public static final void main(String[] args) {
		JFrame f = new JFrame();
		f.setSize(800, 600);
		NetworkEditor networkEditor = new NetworkEditor();
		f.getContentPane().add(networkEditor.buildPanel());
		f.setVisible(true);
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
				fb.row();
				fb.add(le.getPanel());
			}
			this.layerPane.removeAll();
			this.layerPane.add(fb.getForm());
		}
	}

	private int getNumberOfLayers() {
		String value = this.numberOfLayers.getText();
		int number = 0;
		
		try {
			number = Integer.valueOf(value);
		}
		catch (NumberFormatException nfe) {
			log.error(nfe);
		}
		
		return number;
	}

}
