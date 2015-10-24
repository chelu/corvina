package info.joseluismartin.corvina.htm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

/**
 * CLA Classifier for Corvina
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public class CorvinaClassifier {

	private static final Log log = LogFactory.getLog(CorvinaClassifier.class);
	private MultiValueMap<String, Object> outputs = new LinkedMultiValueMap<>();
	private int historyLenght = 10;
	private double threshold = 0.1d;
	private int steps;
	private int hits;
	
	public String compute(int[] values, String name, boolean infer) {
		if (infer)
			return infer(name, values);
		
		if (values.length == 0)
			return null;
		
		if (!this.outputs.containsKey(name)) {
			this.outputs.add(name, values);
			return null;
		}
		
		List<Object> history = this.outputs.get(name);
		
		if (history.size() < historyLenght) {
			this.outputs.add(name, values);
		
			return null;
		}
		
		history.add(0, values);
		history.remove(historyLenght);
		int[] diff = difference((int[]) history.get(1), values);
		
		if (log.isDebugEnabled())
			log.debug("Difference: " + Arrays.toString(diff));
		
		double d = (double) diff.length / values.length; 
		
		if (d < this.threshold) {
			log.info("Seeing: " + name);
			return name;
		}
		
		return null;
	}
	
	public String infer(String realName, int[] values) {
		this.steps++;
		
		for (String name : this.outputs.keySet()) {
			List<Object> records = this.outputs.get(name);
			for (Object record : records) {
				if (match(values, (int[]) record) && name.equals(realName)) {
					this.hits++;
					return name;
				}
				else if (!name.equals(realName)) {
					log.warn("Bad Hit: [" + name + "]");
				}
			}
		}
		
		return null;
	}
	
	private boolean match(int[] values, int[] record) {
		int[] diff = difference(values, record);
		double d = (double) diff.length / values.length; 
		
		return d < this.threshold;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int[]  difference(int[] one, int[] other) {
		List<Object> oneList = Arrays.asList(ObjectUtils.toObjectArray(one));
		List<Object> otherList = Arrays.asList(ObjectUtils.toObjectArray(other));
		List diff = (List<Object>) CollectionUtils.disjunction(oneList, otherList);
		Collections.sort(diff);

		return convertToArray(diff);
	}

	private int[] convertToArray(Collection<Object> collection) {
		int[] array = new int[collection.size()];
		int i = 0;
		for (Object o : collection)
			array[i++] = (Integer) o;
		
		return array;
	}
	
	public String getStatsString() {
		return "Steps: " + this.steps + " Hits: " + this.hits + " (" + (double) this.hits / this.steps * 100 + " %)";
	}

}
