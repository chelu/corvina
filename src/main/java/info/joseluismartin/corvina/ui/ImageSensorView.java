package info.joseluismartin.corvina.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.jdal.swing.AbstractView;
import org.jdal.swing.Selector;
import org.jdal.swing.TitledSeparator;
import org.jdal.swing.form.BoxFormBuilder;
import org.jdal.swing.form.FormUtils;
import org.jdal.swing.form.SimpleBoxFormBuilder;

import info.joseluismartin.corvina.sensor.DirectoryNameGenerator;
import info.joseluismartin.corvina.sensor.FileNameGenerator;
import info.joseluismartin.corvina.sensor.ImageSensor;
import info.joseluismartin.corvina.sensor.ImageSensorListener;
import info.joseluismartin.corvina.sensor.NameGenerator;

/**
 * Swing viewer for {@link ImageSensor}.
 * 
 * @author Jose Luis Martin.
 * @since 1.0
 */
public class ImageSensorView extends AbstractView<ImageSensor> implements ImageSensorListener {
	
	private static String[] EXTENSIONS = {"png", "bmp", "jpg", "gif" };
	
	private JLabel imageLabel = new JLabel() ;
	private ImageIcon imageIcon = new ImageIcon();
	private List<BufferedImageOp> availableFilters = new ArrayList<>();
	private Selector<ImageFilter> filters;
	private List<ImageFilter> imageFilters = new ArrayList<>();
	private Selector<BufferedImageOp> dinamycFilters;
	private JList<String> imagesToLoad = new JList<>();
	private JTextField imageCicles = new JTextField();
	private JButton saveButton = new JButton("Save");
	private JCheckBox display = new JCheckBox("Paint");
	private JLabel imageName = new JLabel();
	private Box leftPanel;
	private JComponent northPanel;
	private JButton applyFilterButton;
	private JButton chooserButton;
	private File lastDirectory;
	private JComboBox<NameGenerator> nameGenerator = new JComboBox<>();
	private JTextField repeatCicles = new JTextField();
	private JButton shuffle = new JButton("Suffle");
	
	@PostConstruct
	public void init() {
		this.filters = new Selector<>(this.imageFilters);
		this.filters.setListWidth(200);
		this.filters.init();
		this.dinamycFilters = new Selector<>(this.availableFilters);
		this.dinamycFilters.setListWidth(200);
		this.dinamycFilters.init();
		this.applyFilterButton = new JButton(getMessage("applyFilter"));
		this.applyFilterButton.addActionListener(e -> update());
		this.chooserButton = new JButton(getMessage("File"));
		this.chooserButton.addActionListener(e -> chooseImageFile());
		this.saveButton.addActionListener(e -> update());
		this.saveButton.setAlignmentY(Component.LEFT_ALIGNMENT);
		this.nameGenerator.addItem(new DirectoryNameGenerator());
		this.nameGenerator.addItem(new FileNameGenerator());
		this.nameGenerator.addActionListener(l -> 
			getModel().setNameGenerator((NameGenerator) nameGenerator.getSelectedItem()));
		this.shuffle.addActionListener(l -> { getModel().shuffle(); refresh(); });
		
		autobind();
		refresh();
	}

	@Override
	protected JComponent buildPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		this.imageLabel.setIcon(this.imageIcon);
		this.imageLabel.setHorizontalAlignment(JLabel.CENTER);
		JScrollPane scroll = new JScrollPane(this.imageLabel);
		scroll.setBorder(FormUtils.createEmptyBorder(5));
		panel.add(scroll, BorderLayout.CENTER);
		this.leftPanel = createLeftPanel();
		panel.add(this.leftPanel, BorderLayout.EAST);
		this.northPanel = createNorthPanel();
		panel.add(this.northPanel, BorderLayout.NORTH);
		panel.add(createWestPanel(), BorderLayout.WEST);
		
		return panel;
	}

	/**
	 * Create north panel for image selection.
	 * @return a horizontal box.
	 */
	private JComponent createNorthPanel() {
		BoxFormBuilder fb = new BoxFormBuilder(FormUtils.createEmptyBorder(5));
		fb.row();
		fb.add(this.display);
		fb.setMaxWidth(50);
		// fb.add(Box.createHorizontalStrut(10), 10);
		fb.add("Classifier name: ", this.nameGenerator);
		fb.setMaxWidth(200);
		// fb.add(Box.createHorizontalStrut(10));
		fb.add("Image: ", this.imageName);
		fb.add(Box.createHorizontalGlue());
		
		return fb.getForm();
	}
	
	private JComponent createWestPanel() {
		BoxFormBuilder fb = new BoxFormBuilder(BorderFactory.createCompoundBorder(
				FormUtils.createEmptyBorder(5), FormUtils.createEmptyBorder(5)));
		
		// fb.setDebug(true);	
		fb.row();
		fb.add(new TitledSeparator("Load Files"));
		fb.row();
		fb.startBox();
		fb.row();
		fb.add(this.chooserButton);
		fb.add(this.shuffle);
		// fb.add(Box.createHorizontalGlue());
		fb.endBox();
		// fb.row();
		fb.row(SimpleBoxFormBuilder.SIZE_UNDEFINED);
		fb.add(new JScrollPane(this.imagesToLoad));
		fb.row();
		fb.startBox();
		fb.row();
		fb.add(getMessage("Image"), this.imageCicles);
		fb.add(getMessage("Cicles"), this.repeatCicles);
		fb.add(this.saveButton);
		fb.endBox();
		
		return fb.getForm();
	}
	

	/**
	 * Create left panel with filter controls.
	 * @return a vertical box.-
	 */
	private Box createLeftPanel() {
		BoxFormBuilder fb = new BoxFormBuilder(FormUtils.createEmptyBorder(5));
		fb.row();
		fb.add(new TitledSeparator(getMessage("Load Filters")));
		fb.row(SimpleBoxFormBuilder.SIZE_UNDEFINED);
		fb.add(this.filters);
		fb.row();
		fb.add(new TitledSeparator(getMessage("Input Filters")));
		fb.row(SimpleBoxFormBuilder.SIZE_UNDEFINED);
		fb.add(this.dinamycFilters);
		fb.row();
		fb.startBox();
		fb.row();
		fb.add(Box.createHorizontalGlue());
		fb.add(this.applyFilterButton);
		fb.add(Box.createHorizontalGlue());
		fb.endBox();
		
		return (Box) fb.getForm();
	}

	@Override
	protected void doRefresh() {
		ImageSensor model = getModel();
		if (model == null)
			return;
		
		refreshImage();
	}

	/**
	 * @param model
	 */
	public void refreshImage() {
		if (!this.display.isSelected())
			return;
		
		BufferedImage image = getModel().getImage();
		
		if (image == null)
			return;
		
		this.imageIcon.setImage(getModel().getImage());
		this.imageLabel.repaint();
	}
	
	/**
	 * Open a {@link JFileChooser} to select an image file.
	 */
	private void chooseImageFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		if (this.lastDirectory != null)
			chooser.setCurrentDirectory(lastDirectory);
			
		if (chooser.showOpenDialog(getPanel()) == JFileChooser.APPROVE_OPTION) {
			File[] files = chooser.getSelectedFiles();
			this.lastDirectory = files[0].getParentFile();
			
			if (files.length == 1) {
				File file = files[0];
				
				if (file.isDirectory()) {
					// find images on directory
					loadDirectory(file);  
				}
				else {
					// single file
					getModel().loadImage(file.getAbsolutePath());
					getModel().setSingleImage(true);
				}
			}
			else {
				// list of files
				getModel().setImagesToLoad(Arrays.stream(files).<String>map(f -> f.getAbsolutePath())
						.collect(Collectors.toList()));
				
				getModel().setSingleImage(false);
			}
			
			refresh();
		}
		
	}
	
	// Load all files in a directory
	private void loadDirectory(File directory) {
		List<String> fileNames = FileUtils.listFiles(directory, EXTENSIONS, true)
				.stream().<String>map(f -> f.getAbsolutePath())
				.collect(Collectors.toList());
		
		getModel().setImagesToLoad(fileNames);
	}

	@Override
	protected void onSetModel(ImageSensor model) {
		if (model != null) {
			model.addListener(this);
		}
	}

	/**
	 * @return the availableFilters
	 */
	public List<BufferedImageOp> getAvailableFilters() {
		return availableFilters;
	}

	/**
	 * @param availableFilters the availableFilters to set
	 */
	public void setAvailableFilters(List<BufferedImageOp> availableFilters) {
		this.availableFilters = availableFilters;
	}

	@Override
	public void imageChanged() {
		this.imageName.setText(getModel().getImageName());
		
	}

	@Override
	public void finished() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return the imageFilters
	 */
	public List<ImageFilter> getImageFilters() {
		return imageFilters;
	}

	/**
	 * @param imageFilters the imageFilters to set
	 */
	public void setImageFilters(List<ImageFilter> imageFilters) {
		this.imageFilters = imageFilters;
	}


}
