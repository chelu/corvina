package info.joseluismartin.corvina.htm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.numenta.nupic.algorithms.Classification;
import org.numenta.nupic.algorithms.Classifier;
import org.numenta.nupic.util.ArrayUtils;

/**
 * Simple dot matrix classifier
 * 
 * @author Jose Luis Martin.
 * @since 1.1
 */
public class DotClassifier implements Classifier {
	
	private static String BUCKET_IDX = "bucketIdx";
	private static String ACT_VALUE = "actValue";
	private int learnIteration = 0;
	private Map<Integer, double[]> distributionMatrix = new HashMap<>();
	private List<Object> actualValues = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> Classification<T> compute(int recordNum, Map<String, Object> classification, int[] patternNZ,
			boolean learn, boolean infer) {
		
		Classification<T> value  = new Classification<T>();
		
		if (learn && classification.get(BUCKET_IDX) != null) {
			// learn
			this.learnIteration++;
			int bucketIdx = (int) classification.get(BUCKET_IDX);
			Object actualValue = classification.get(ACT_VALUE);
			
			if (!this.distributionMatrix.containsKey(bucketIdx)) {
				this.distributionMatrix.put(bucketIdx, new double[patternNZ.length]);
				this.actualValues.add(actualValue);
			}
			
			updateDistributionmatrix(bucketIdx, patternNZ);
		}
		
		if (infer) {
			Set<Integer> ids = this.distributionMatrix.keySet();
			double[] values = new double[ids.size()];
			
			for (int i = 0; i < ids.size(); i++) {
				double[] likehoods = this.distributionMatrix.get(i);
				double[] parcial = ArrayUtils.multiply(likehoods, patternNZ);
				double total = ArrayUtils.sum(parcial);
				values[i] = total;
			}
			
			value.setActualValues(((List<T>)this.actualValues).toArray((T[]) new Object[0]));
			value.setStats(1, values);
		}
		
			
		return value;
	}

	/** 
	 * Update the distribution matrix
	 * @param bucketIdx actual bucketIdx
	 * @param patternNZ the input values.
	 */
	protected void updateDistributionmatrix(int bucketIdx, int[] patternNZ) {
		double[] likehoods = this.distributionMatrix.get(bucketIdx);

		for (int i = 0; i < patternNZ.length; i++) {
			double n = likehoods[i] * (this.learnIteration - 1);
			double likehood = patternNZ[i] > 0 ? (n + 1) / this.learnIteration : n / this.learnIteration;
			likehoods[i] = likehood;
		}
	}

}
