package info.joseluismartin.corvina.ui;

import com.jme3.scene.Geometry;

/**
 * Factory interface for creating {@link Geometry Geometries} for cortical components.
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public interface CortexFactory {

	/**
	 * Create a {@link Geometry} for cells
	 */
	Geometry createCell();
	
	/**
	 * Crate a Geometry for columns.
	 */
	Geometry createColumn(String name);
	
	/**
	 * Create a Geometry for dentrites
	 */
	Geometry createDentrite();
	
}
