package info.joseluismartin.corvina.ui;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;

/**
 * Default implementation for {@link CortexFactory}.
 * 
 * @author Jose Luis Martin
 * @since 1.0
 *
 */
public class DefaultCortexFactory implements CortexFactory {
	
	private Material columnMaterial;
	private AssetManager assetManager;

	public DefaultCortexFactory(AssetManager assetManager) {
		this.assetManager = assetManager;
		this.columnMaterial = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  
		this.columnMaterial.setColor("Color", ColorRGBA.Orange);
	}

	@Override
	public Geometry createCell() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Geometry createColumn(String name) {
		Cylinder cylinder = new Cylinder(10, 10, 1, 4, true);
		Geometry geo = new Geometry(name, cylinder);
		geo.setMaterial(this.columnMaterial); 
		
		return geo;
	}

	@Override
	public Geometry createDentrite() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Material getColumnMaterial() {
		return columnMaterial;
	}

	public void setColumnMaterial(Material columnMaterial) {
		this.columnMaterial = columnMaterial;
	}

}
