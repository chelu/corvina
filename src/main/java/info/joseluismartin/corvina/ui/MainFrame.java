package info.joseluismartin.corvina.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdal.swing.SimpleDialog;
import org.jdal.swing.form.FormUtils;
import org.numenta.nupic.algorithms.CLAClassifier;
import org.numenta.nupic.algorithms.Classifier;
import org.numenta.nupic.algorithms.SDRClassifier;
import org.numenta.nupic.network.Network;
import org.numenta.nupic.network.Persistence;
import org.numenta.nupic.serialize.HTMObjectInput;
import org.numenta.nupic.serialize.HTMObjectOutput;
import org.numenta.nupic.serialize.SerializerCore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import info.joseluismartin.corvina.Corvina;
import info.joseluismartin.corvina.model.CorvinaModel;

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
	private static final String SDR_CLASSIFIER = "SDRClassifer";
	private static final String CLA_CLASSIFIER = "CLAClassifer";
	private static final String START = "Start";
	private static final String STOP = "Stop";
	private static final String RESET = "RESET";
	private static final String UNKNOWN = "UNKNOWN";
	private static final String SAVE_ICON = "org/freedesktop/tango/22x22/actions/document-save.png";
	private static final String LOAD_ICON = "org/freedesktop/tango/22x22/actions/document-open.png";
	private static final String SAVE_AS_ICON = "org/freedesktop/tango/22x22/actions/document-save-as.png";
	private static final String NEW_ICON =  "org/freedesktop/tango/22x22/actions/document-new.png";

	private JMenu menu = new JMenu();
	private JToolBar toolBar = new JToolBar();
	private JTabbedPane tab = new JTabbedPane();
	private JLabel hit = new JLabel();
	private JToggleButton startButton = new JToggleButton(START);
	private JButton saveButton = new JButton(FormUtils.getIcon(SAVE_ICON));
	private JButton loadButton = new JButton(FormUtils.getIcon(LOAD_ICON));
	private JButton saveAsButton = new JButton(FormUtils.getIcon(SAVE_AS_ICON));
	private JButton newButton = new JButton(FormUtils.getIcon(NEW_ICON));
	private File networkFile;
	private JCheckBox usingSDR = new JCheckBox();
	private JCheckBox learn  = new JCheckBox("Learn");
	private JComboBox<String> classifiers = new JComboBox<>();
	private JCheckBox classifierLearn = new JCheckBox("Classifer Learn");
	
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
		getContentPane().add(split, BorderLayout.CENTER);
		setSize(new Dimension(1024, 768));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.corvina.setNetwork(this.networkView.getModel());
		this.usingSDR.addActionListener(l -> this.corvina.setUsingSDR(this.usingSDR.isSelected()));
		this.classifierLearn.addActionListener(
				l -> this.corvina.setClassifierLearn(this.classifierLearn.isSelected()));
		
		this.classifiers.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXX");
		Dimension size = this.classifiers.getPreferredSize();
		size.width = 200;
		this.classifiers.setMaximumSize(size);
		this.classifiers.addItem(CLA_CLASSIFIER);
		this.classifiers.addItem(SDR_CLASSIFIER);
		this.classifiers.addActionListener(l -> createClassfier());
		
		refresh();
	}

	
	/**
	 * Create classifer based on classifiers combobox.
	 */
	private void createClassfier() {
		String name = (String) this.classifiers.getSelectedItem();
		
		if (log.isDebugEnabled())
			log.debug("Creating classifer [" + name +"]");
		
		Classifier classifier = null;
		
		if (CLA_CLASSIFIER.equals(name))
			classifier = new CLAClassifier();
		else if (SDR_CLASSIFIER.equals(name))
			classifier = new SDRClassifier();
		
		if (classifier != null)
			this.corvina.setClassifier(classifier);
	}

	/**
	 * Initialize the application toolbar.
	 */
	protected void initToolBar() {
	
		this.startButton.addActionListener(e -> {
				if (!corvina.isRunning())
					corvina.start();
				else {
					corvina.stop();
				}
				
				((JToggleButton) e.getSource()).setText(corvina.isRunning() ? STOP : START);
		});
		
		this.newButton.addActionListener(e -> newNetwork());
		this.saveButton.addActionListener(e -> save());
		this.saveAsButton.addActionListener(e -> saveAs());
		this.loadButton.addActionListener(e -> load());
		this.learn.setSelected(true);
		this.learn.addActionListener(e -> this.corvina.getNetwork().setLearn(this.learn.isSelected()));
		
		// Load and save buttons.
		this.toolBar.add(this.newButton);
		this.toolBar.add(this.saveButton);
		this.toolBar.add(this.saveAsButton);
		this.toolBar.add(this.loadButton);
		this.toolBar.addSeparator();
		// Operation control
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
		this.toolBar.add(this.usingSDR);
		this.toolBar.add(new JLabel("SDR"));
		this.toolBar.addSeparator();
		this.toolBar.add(this.learn);
		this.toolBar.addSeparator();
		this.toolBar.add(this.classifiers);
		this.toolBar.addSeparator();
		this.toolBar.add(this.classifierLearn);
		this.toolBar.addSeparator();
		this.toolBar.add(new JLabel("Hits: "));
		this.toolBar.add(this.hit);
		
	}
	
	/**
	 * Load persistent data from file.
	 */
	private void load() {
		JFileChooser chooser = new JFileChooser();
		
		if (this.networkFile  != null) {
			File directory = this.networkFile.getParentFile();
			if (directory != null)
				chooser.setCurrentDirectory(directory);
		}
		
		int value = chooser.showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			try {
				SerializerCore serializer = Persistence.get().serializer();
				serializer.registerClass(CorvinaModel.class);
				HTMObjectInput reader = serializer.getObjectInput(new FileInputStream(f));
		        CorvinaModel cm = (CorvinaModel) reader.readObject(CorvinaModel.class);
				setNetwork(cm.getNetwork());
				this.corvina.setClassifier(cm.getClassifier());
			} catch (Exception e) {
				log.error(e);
				FormUtils.showError("Cannot open file");
			}
		}
	}

	/**
	 * Save model to file
	 */
	private void saveAs() {
		JFileChooser chooser = new JFileChooser();
		int value = chooser.showOpenDialog(this);
		if (value == JFileChooser.APPROVE_OPTION) {
			this.networkFile  = chooser.getSelectedFile();
			save();
		}
	}

	/**
	 * Save model to file.
	 */
	private void save() {
		if (this.networkFile  == null) {
			saveAs();
			return;
		}

		try {
			SerializerCore serializer = Persistence.get().serializer();
			serializer.registerClass(CorvinaModel.class);
			HTMObjectOutput writer = serializer.getObjectOutput(new FileOutputStream(this.networkFile));
			CorvinaModel cm = new CorvinaModel();
			cm.setNetwork(this.corvina.getNetwork());
			cm.setClassifier(this.corvina.getClassifier());
			writer.writeObject(cm, CorvinaModel.class);
            writer.flush();
            writer.close();
		} catch (Exception e) {
			log.equals(e);
			FormUtils.showError("Cannot save network");
		}
	}

	/**
	 * Create new network
	 */
	private void newNetwork() {
		NetworkDialog dlg  =  this.context.getBean(NetworkDialog.class);
		dlg.setLocationRelativeTo(this);
		dlg.setModal(true);
		dlg.setVisible(true);
		
		if (dlg.isAccepted()) {
			setNetwork(dlg.createNetwork());
		}
	}

	/**
	 * Expand network to corvina and network view.
	 * @param network network to set.
	 */
	private void setNetwork(Network network) {
		this.corvina.setNetwork(network);
		this.networkView.setModel(network);
		this.networkView.updateLayerCombo();
		this.networkView.refresh();
		
	}
	
	/** 
	 * Restet de image sensor.
	 */
	private void reset() {
		if (this.imageSensorView.getModel() != null) 
			this.imageSensorView.getModel().reset();
			this.corvina.getStats().clear();
	}

	/**
	 * Show current classication report.
	 */
	private void showClassifierReport() {
		JTextArea area = new JTextArea();
		area.setMargin(new Insets(20, 20, 20, 20));
		area.setEditable(false);
		// area.setFont(Font.getFont(Font.MONOSPACED));
		area.setText(this.corvina.getReport());
		SimpleDialog dlg = new SimpleDialog(this, new JScrollPane(area), "Classifer Report");
		dlg.setLocationRelativeTo(null);
		dlg.setSize(600, 600);
		dlg.setVisible(true);
	}

	/**
	 * Refresh control values.
	 */
	public void refresh() {
		this.networkView.refresh();
		this.imageSensorView.refreshImage();
		this.startButton.setSelected(this.corvina.isRunning());
		this.startButton.setText(corvina.isRunning() ? STOP : START);
	 	this.usingSDR.setSelected(this.corvina.isUsingSDR());
	 	this.learn.setSelected(this.corvina.getNetwork().isLearn());
	}
	
	/**
	 * Update the hit label.
	 * @param name name to set.
	 */
	public void setHit(String name) {
		this.hit.setText(name == null ? UNKNOWN : name);
	}

}

