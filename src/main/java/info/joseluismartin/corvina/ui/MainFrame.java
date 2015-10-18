package info.joseluismartin.corvina.ui;

import java.awt.Dimension;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import org.numenta.nupic.network.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Corvina main frame.
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
@Component
public class MainFrame extends JFrame {

	private static final String NETWORK = "Network";
	private static final String IMAGE_SENSOR = "Image Sensor";
	private static final String LAYER = "Layer";
	
	private JMenu menu = new JMenu();
	private JToolBar toolBar = new JToolBar();
	private JTabbedPane tab = new JTabbedPane();
	
	@Autowired
	private NetworkView networkView;
	@Autowired
	private ImageSensorView imageSensorView;
	@Autowired
	private Network network;
	
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@PostConstruct
	public void init() {
		this.tab.add(IMAGE_SENSOR, this.imageSensorView.getPanel());
		this.networkView.refresh();
		this.tab.add(NETWORK, this.networkView.getPanel());
		getContentPane().add(this.tab);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setPreferredSize(new Dimension(1024, 768));
	}

	public void refresh() {
		this.networkView.refresh();
		this.imageSensorView.refresh();
	}
}

