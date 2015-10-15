package info.joseluismartin.corvina.sensor;

import java.util.ArrayList;

import org.numenta.nupic.ValueList;
import org.numenta.nupic.util.Tuple;

/**
 * {@link ValueList} implementation.
 * 
 * @author Jose Luis Martin.
 * @since 1.0
 */
public class ListValueList implements ValueList {
	
	private ArrayList<Tuple> tuples = new ArrayList<>();

	@Override
	public Tuple getRow(int row) {
		return this.tuples.get(row);
	}

	@Override
	public int size() {
		return this.tuples.size();
	}
	
	public void addTuple(Tuple tuple) {
		this.tuples.add(tuple);
	}
}
