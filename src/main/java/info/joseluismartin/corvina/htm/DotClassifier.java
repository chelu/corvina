package info.joseluismartin.corvina.htm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.numenta.nupic.algorithms.Classification;
import org.numenta.nupic.algorithms.Classifier;
import org.numenta.nupic.model.Persistable;
import org.numenta.nupic.util.ArrayUtils;

/**
 * Simple dot matrix classifier. 
 * 
 * @author Jose Luis Martin.
 * @since 1.1
 */
public class DotClassifier implements Persistable, Classifier {
	
	private static String BUCKET_IDX = "bucketIdx";
	private static String ACT_VALUE = "actValue";
	private static String INPUT_LENGTH = "outputLength";
	private static String UNKNOWN = "UNKNOWN";
	private Map<Integer, double[]> distributionMatrix = new HashMap<>();
	private List<Integer> learns = new ArrayList<>();
	private List<Object> actualValues = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> Classification<T> compute(int recordNum, Map<String, Object> classification, int[] patternNZ,
			boolean learn, boolean infer) {
		
		int outputLength = (int) classification.get(INPUT_LENGTH);
		int[] output = ArrayUtils.asDense(patternNZ, outputLength);
		
		Classification<T> value  = new Classification<T>();
		
		if (learn && classification.get(BUCKET_IDX) != null) {
			// learn
			int bucketIdx = (int) classification.get(BUCKET_IDX);
			Object actualValue = classification.get(ACT_VALUE);
			
			if (!this.distributionMatrix.containsKey(bucketIdx)) {
				
				this.distributionMatrix.put(bucketIdx, new double[outputLength]);
				// Ensure that we have room.
				while(bucketIdx > this.actualValues.size()) {
					actualValues.add(UNKNOWN);
					this.learns.add(0);
				}
				this.actualValues.add(actualValue);
				this.learns.add(1);
			}
			else {
				int bucketLearn = this.learns.get(bucketIdx);
				this.learns.set(bucketIdx, bucketLearn + 1);
			}
			
			updateDistributionmatrix(bucketIdx, output);
		}
		
		if (infer) {
			int size = this.actualValues.size();
			double[] values = new double[size];
			
			for (int i = 0; i < size; i++) {
				double total = 0.0;
				double[] likehoods = this.distributionMatrix.get(i);
				
				if (likehoods != null) {
					double[] parcial = ArrayUtils.multiply(likehoods, output);
					total = ArrayUtils.sum(parcial);
				}
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
	 * @param output the input values.
	 */
	protected void updateDistributionmatrix(int bucketIdx, int[] output) {
		double[] likehoods = this.distributionMatrix.get(bucketIdx);
		int learn = this.learns.get(bucketIdx);

		for (int i = 0; i < output.length; i++) {
			double n = likehoods[i] * (learn - 1);
			double likehood = output[i] > 0 ? (n + 1) / learn : n / learn;
			likehoods[i] = likehood;
		}
	}

}
