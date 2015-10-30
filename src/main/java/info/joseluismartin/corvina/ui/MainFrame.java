package info.joseluismartin.corvina.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.logging.Handler;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.jdal.swing.SimpleDialog;
import org.numenta.nupic.network.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import info.joseluismartin.corvina.Corvina;

/**
 * Corvina main frame.
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
@Component
public class MainFrame extends JFrame {

	private static final Log log = LogFactory.getLog(MainFrame.class);

	private static final String NETWORK = "Network";
	private static final String IMAGE_SENSOR = "Image Sensor";
	private static final String LAYER = "Layer";
	private static final String START = "Start";
	private static final String STOP = "Stop";
	private static final String RESET = "RESET";
	private static final String UNKNOWN = "UNKNOWN";

	private JMenu menu = new JMenu();
	private JToolBar toolBar = new JToolBar();
	private JTabbedPane tab = new JTabbedPane();
	private JLabel hit = new JLabel();
	private JToggleButton startButton = new JToggleButton(START);
	
	@Autowired
	private NetworkView networkView;
	@Autowired
	private ImageSensorView imageSensorView;
	@Autowired
	private Corvina corvina;
	@Autowired
	private ApplicationContext context;

	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@PostConstruct
	public void init() {
		initToolBar();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(this.toolBar, BorderLayout.PAGE_START);
		this.networkView.refresh();
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.imageSensorView.getPanel(), 
				this.networkView.getPanel());
		// this.tab.add(IMAGE_SENSOR, this.imageSensorView.getPanel());
		
		// this.tab.add(NETWORK, this.networkView.getPanel());
		// JTextArea area = new JTextArea();
		// area.setRows(10);
		// Logger.getRootLogger().addAppender(new TextAreaAppender(area));
		// JScrollPane scroll = new JScrollPane(area);
		// JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.tab, scroll);
		// split.setDividerLocation(0.75d);
		getContentPane().add(split, BorderLayout.CENTER);
		setSize(new Dimension(1024, 768));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	protected void initToolBar() {
	
		this.startButton.addActionListener(e -> {
				if (!corvina.isRunning())
					corvina.start();
				else {
					corvina.stop();
				}
				
				((JToggleButton) e.getSource()).setText(corvina.isRunning() ? STOP : START);
		});
		
		this.toolBar.add(startButton);
		this.toolBar.addSeparator();
		JToggleButton inferButton = new JToggleButton("Infer");
		inferButton.addActionListener(e -> corvina.setInfer(((JToggleButton)e.getSource()).isSelected()));
		this.toolBar.add(inferButton);
		this.hit.setPreferredSize(new Dimension(200, 30));
		this.toolBar.addSeparator();
		JButton report = new JButton("Report");
		report.addActionListener(e -> showClassifierReport());
		this.toolBar.add(report);
		this.toolBar.addSeparator();
		JButton resetButton = new JButton(RESET);
		resetButton.addActionListener(l -> reset());
		this.toolBar.add(resetButton);
		this.toolBar.addSeparator();
		this.toolBar.add(new JLabel("Hits: "));
		this.toolBar.add(this.hit);
	}
	
	private void reset() {
		this.corvina.getClassifier().reset();
		Network network = this.context.getBean(Network.class);
		this.networkView.setModel(network);
		this.networkView.refresh();
		this.corvina.setNetwork(network);
	}

	private void showClassifierReport() {
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setText(this.corvina.getClassifier().getReport());
		SimpleDialog dlg = new SimpleDialog(this, new JScrollPane(area), "Classifer Report");
		dlg.setSize(600, 600);
		dlg.setVisible(true);
		
	}

	public void refresh() {
		this.networkView.refresh();
		this.imageSensorView.refreshImage();
		this.startButton.setSelected(this.corvina.isRunning());
		this.startButton.setText(corvina.isRunning() ? STOP : START);
	}
	
	public void setHit(String name) {
		this.hit.setText(name == null ? UNKNOWN : name);
	}
}

