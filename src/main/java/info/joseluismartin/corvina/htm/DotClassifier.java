package info.joseluismartin.corvina.htm;

import java.util.Map;

import org.numenta.nupic.algorithms.Classification;
import org.numenta.nupic.algorithms.Classifier;

/**
 * Simple dot matrix classifier
 * 
 * @author Jose Luis Martin.
 * @
 */
public class DotClassifier implements Classifier {

	@Override
	public <T> Classification<T> compute(int recordNum, Map<String, Object> classification, int[] patternNZ,
			boolean learn, boolean infer) {
		// TODO Auto-generated method stub
		return null;
	}

}
