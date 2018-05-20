package info.joseluismartin.corvina.htm;

import java.util.Arrays;

import org.numenta.nupic.algorithms.SpatialPooler;
import org.numenta.nupic.model.Column;
import org.numenta.nupic.model.Connections;
import org.numenta.nupic.model.Pool;
import org.numenta.nupic.util.FastConnectionsMatrix;
import org.numenta.nupic.util.SparseBinaryMatrix;
import org.numenta.nupic.util.SparseObjectMatrix;
import org.numenta.nupic.util.Topology;

/**
 * Memory conservative {@link SpatialPooler} suitable for 
 * use with image inputs.
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public class LowMemorySpatialPooler extends SpatialPooler {

	@Override
	public void initMatrices(Connections c) {
		SparseObjectMatrix<Column> mem = c.getMemory();
        c.setMemory(mem == null ? 
            mem = new SparseObjectMatrix<>(c.getColumnDimensions()) : mem);
        
        c.setInputMatrix(new SparseBinaryMatrix(c.getInputDimensions()));
        
        // Initiate the topologies
        c.setColumnTopology(new Topology(c.getColumnDimensions()));
        c.setInputTopology(new Topology(c.getInputDimensions()));

        //Calculate numInputs and numColumns
        int numInputs = c.getInputMatrix().getMaxIndex() + 1;
        int numColumns = c.getMemory().getMaxIndex() + 1;
        c.setNumInputs(numInputs);
        c.setNumColumns(numColumns);
        
        //Fill the sparse matrix with column objects
        for(int i = 0;i < numColumns;i++) { mem.set(i, new Column(c.getCellsPerColumn(), i)); }

        c.setPotentialPools(new SparseObjectMatrix<Pool>(c.getMemory().getDimensions()));

        c.setConnectedMatrix(new FastConnectionsMatrix(new int[] { numColumns, numInputs }));

        //Initialize state meta-management statistics
        c.setOverlapDutyCycles(new double[numColumns]);
        c.setActiveDutyCycles(new double[numColumns]);
        c.setMinOverlapDutyCycles(new double[numColumns]);
        c.setMinActiveDutyCycles(new double[numColumns]);
        c.setBoostFactors(new double[numColumns]);
        Arrays.fill(c.getBoostFactors(), 1);
	}
	
}
