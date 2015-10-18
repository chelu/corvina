package info.joseluismartin.corvina.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;

import javax.annotation.PostConstruct;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdal.swing.AbstractView;
import org.numenta.nupic.network.Network;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

/**
 * Viewer for HTM Networks.
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public class Network3DView extends AbstractView<Network> {
	
	private JLabel name = new JLabel();
	private Canvas canvas;
	private JmeCanvasContext canvasContext;

	private NetworkApplication app;
	
	public Network3DView() {
		super();
	}

	public Network3DView(Network model) {
		super(model);
	}

	@PostConstruct
	public void init() {
		AppSettings settings = new AppSettings(true);
		settings.setWidth(1024);
		settings.setHeight(768);
		this.app = new NetworkApplication();
		this.app.setNetwork(getModel());
		this.app.setSettings(settings);
		this.app.createCanvas();
		this.canvasContext = (JmeCanvasContext) app.getContext();
		this.canvas = this.canvasContext.getCanvas();
		this.canvas.setPreferredSize(new Dimension(1024, 768));
		
		autobind();
	}

	@Override
	protected JComponent buildPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(createNorthPanel(), BorderLayout.NORTH);
		panel.add(this.canvas, BorderLayout.CENTER);
		this.app.startCanvas();
		
		return panel;
	}

	protected Component createNorthPanel() {
		return this.name;
	}
		
}

