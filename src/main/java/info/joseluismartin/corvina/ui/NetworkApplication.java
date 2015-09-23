package info.joseluismartin.corvina.ui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.numenta.nupic.Connections;
import org.numenta.nupic.model.Cell;
import org.numenta.nupic.model.Column;
import org.numenta.nupic.network.Layer;
import org.numenta.nupic.network.Network;
import org.numenta.nupic.network.Region;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/**
 * JMonkeyEngine application, used to show a network in a AWT canvas.
 * 
 * @author Jose Luis Martin.
 * @since 1.0
 */
public class NetworkApplication extends SimpleApplication {

	private static final Log log = LogFactory.getLog(NetworkApplication.class);
	private static final float DEFAULT_REGION_GAP = 10f;
	private static final float DEFAULT_LAYER_GAP = 5f;
	
	/** Gap between regions */
	private float regionGap = DEFAULT_REGION_GAP;
	/** Gap between layers */
	private float layerGap = DEFAULT_LAYER_GAP;
	
	/** Map holding {@link Spatial} of regions */
	private Map<Region, Node> regions = new HashMap<>();
	/** Map holding {@link Spatial} of Layers */
	private Map<Layer<?>, Node> layers = new HashMap<>();
	private Material material = new Material();
	private boolean showOnlyColumns = true;
	private Network network;
	private Node networkNode;
	private CortexFactory cortexFactory;

	@Override
	public void simpleInitApp() {
		setDisplayFps(true);
		setDisplayStatView(true);
		viewPort.setBackgroundColor(ColorRGBA.DarkGray);
		flyCam.setMoveSpeed(50f);
		// activate windowed behavior
		flyCam.setDragToRotate(true);
		viewPort.getCamera().setLocation(new Vector3f(400f, 400f, 800f));
		
		/* light sources */
		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(1f, -1f, -1f));
		sun.setColor(ColorRGBA.White);
		rootNode.addLight(sun);
		sun = new DirectionalLight();
		sun.setDirection(new Vector3f(0f, 0f, -1f));
		sun.setColor(ColorRGBA.White);
		rootNode.addLight(sun);
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White.mult(5f));
		rootNode.addLight(ambient); 
		// FIXME: Move to DI
		this.cortexFactory = new DefaultCortexFactory(assetManager);
		
		load();
	}
	
	public void load() {
		if (this.network == null)
			return;
		
		if (log.isInfoEnabled()) 
			log.info("Loading HTM Network [" + network.getName() + "]");
		
		this.networkNode = new Node(this.network.getName());
		this.rootNode.attachChild(networkNode);
		
		for (Region region : network.getRegions())
			addRegion(region);
		
	}

	private void addRegion(Region region) {
		if (log.isDebugEnabled())
			log.debug("Adding region [" + region.getName() + "]");
		
		Node regionNode = new Node(region.getName());
		this.networkNode.attachChild(regionNode);
		regionNode.move(nextRegionLocation());
		this.regions.put(region, regionNode);
		
		Layer<?> layer = region.getHead();
		int index = 0;
		while (layer != null) {
			addLayer(layer, region, index++);
			layer = layer.getNext();
		}
	}

	/**
	 * Calculate {@link Region} location.
	 * @return location for next region.
	 */
	private Vector3f nextRegionLocation() {
		return new Vector3f(0, 0, this.regions.size() * this.regionGap);
	}
	
	/**
	 * Calculate {@link Layer} location.
	 * @param layer the layer.
	 * @param region partent layer region.
	 * @param index layer index on region.
	 * @return the layer location.
	 */
	private Vector3f nextLayerLocation(int index) {
		return new Vector3f(0, 0, index * this.layerGap);
	}
	
	/**
	 * Add {@link Layer} to the scene.
	 * @param layer the layer to add
	 * @param region parent layer region
	 * @param index layer index.
	 */
	private void addLayer(Layer<?> layer, Region region, int index) {
		if (log.isDebugEnabled())
			log.debug("Adding layer [" + layer.getName() + "]");
		
		Node layerNode = new Node(layer.getName());
		this.layers.put(layer, layerNode);
		Node regionNode = regions.get(region);
		regionNode.attachChild(layerNode);
		layerNode.move(nextLayerLocation(index));
		// Add cells
		Connections conns = layer.getMemory();

		if (showOnlyColumns) {
			int ncols = conns.getNumColumns();
			for (int i = 0; i < ncols; i++) {
				addColumn(conns.getColumn(i), i, layer);
			}
		}
		else {
			Cell[] cells = conns.getCells();
			int i = 0;
			
			for (Cell cell : cells) 
				addCell(cell, i++, layer);
		}
	}

	/**
	 * Add column to scene.
	 * @param column column to add.
	 * @param i cylinder index.
	 * @param layer owner layer.
	 */
    private void addColumn(Column column, int i, Layer<?> layer) {
    	if (log.isDebugEnabled())
    		log.debug("Adding column [" + i +"]");
    	
		Node layerNode = layers.get(layer);
		Geometry geo = this.cortexFactory.createColumn(String.valueOf(i));
		geo.move(getCylinderOffset(i, layer));
		layerNode.attachChild(geo);
		
	}

    /**
     * Calculate offset of a cylinder.
     * @param i cylinder index.
     * @param layer owner layer.
     * @return cylinder offset.
     */
	private Vector3f getCylinderOffset(int i, Layer<?> layer) {
		int columnNumber = layer.getConnections().getNumColumns();
		int m = (int) Math.sqrt(columnNumber);
		int row = i / m;
		int col = i - row * m;
		
		if (log.isDebugEnabled())
			log.debug("Cylinder offset [" + row + "," + col + "]");
		
		return new Vector3f(row * 5 , col * 5, 0);	
	}

	/**
     * Add a shape for cell in layer
     * @param i cell number
     * @param layer owner layer
     */
	private void addCell(Cell cell, int i, Layer<?> layer) {
		Node layerNode = layers.get(layer);
		Sphere sphere = new Sphere(10, 10, 1f);
		Geometry geo = new Geometry(String.valueOf(i), sphere);
		geo.setMaterial(this.material);
		geo.move(getCellOffset(i, layer));
		layerNode.attachChild(geo);
		
	}

	private Vector3f getCellOffset(int i, Layer<?> layer) {
		int columnNumber = layer.getConnections().getNumColumns();
		int ncapa = (int) (i / columnNumber);
		int offset = i - ncapa * columnNumber;
		int m = (int) Math.sqrt(columnNumber);
		int row = offset / m;
		int col = offset - row * m;
		
		return new Vector3f(row, col, ncapa);	
	}
	
	public Network getNetwork() {
		return network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}
	
	public CortexFactory getCortexFactory() {
		return cortexFactory;
	}

	public void setCortexFactory(CortexFactory cortexFactory) {
		this.cortexFactory = cortexFactory;
	}

}
