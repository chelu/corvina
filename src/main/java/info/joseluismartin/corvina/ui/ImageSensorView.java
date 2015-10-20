package info.joseluismartin.corvina.ui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdal.swing.AbstractView;
import org.jdal.swing.Selector;
import org.jdal.swing.TitledSeparator;
import org.jdal.swing.form.BoxFormBuilder;
import org.jdal.swing.form.FormUtils;
import org.jdal.swing.form.SimpleBoxFormBuilder;

import info.joseluismartin.corvina.sensor.ImageSensor;

/**
 * Swing viewer for {@link ImageSensor}.
 * 
 * @author Jose Luis Martin.
 * @since 1.0
 */
public class ImageSensorView extends AbstractView<ImageSensor> {
	
	private JLabel imageLabel = new JLabel() ;
	private ImageIcon imageIcon = new ImageIcon();
	private List<BufferedImageOp> availableFilters = new ArrayList<>();
	private Selector<ImageFilter> filters;
	private Selector<BufferedImageOp> dinamycFilters;
	private Box leftPanel;
	private Box northPanel;
	private JButton applyFilterButton;
	private JButton chooserButton;
	
	@PostConstruct
	public void init() {
		this.filters = new Selector<>();
		this.filters.setListWidth(200);
		this.filters.init();
		this.dinamycFilters = new Selector<>(this.availableFilters);
		this.dinamycFilters.setListWidth(200);
		this.dinamycFilters.init();
		this.applyFilterButton = new JButton(getMessage("applyFilter"));
		this.applyFilterButton.addActionListener(e -> update());
		this.chooserButton = new JButton(getMessage("File"));
		this.chooserButton.addActionListener(e -> chooseImageFile());
		autobind();
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
		return panel;
	}

	/**
	 * Create north panel for image selection.
	 * @return a horizontal box.
	 */
	private Box createNorthPanel() {
		Box box = Box.createHorizontalBox();
		box.setBorder(FormUtils.createEmptyBorder(5));
		box.add(this.chooserButton);
		
		return box;
	}

	/**
	 * Create left panel with filter controls.
	 * @return a vertical box.-
	 */
	private Box createLeftPanel() {
		BoxFormBuilder fb = new BoxFormBuilder(FormUtils.createEmptyBorder(5));
		fb.row();
		fb.add(new TitledSeparator(getMessage("Filters")));
		fb.row(SimpleBoxFormBuilder.SIZE_UNDEFINED);
		fb.add(this.filters);
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
		this.imageIcon.setImage(getModel().getImage());
		this.imageLabel.repaint();
	}
	
	/**
	 * Open a {@link JFileChooser} to select an image file.
	 */
	private void chooseImageFile() {
		JFileChooser chooser = new JFileChooser();
		
		if (chooser.showOpenDialog(getPanel()) == JFileChooser.APPROVE_OPTION) {
			getModel().loadImage(chooser.getSelectedFile().getAbsolutePath());
			refresh();
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


}
