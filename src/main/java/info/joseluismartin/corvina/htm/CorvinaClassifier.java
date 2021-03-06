package info.joseluismartin.corvina.htm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.numenta.nupic.util.ArrayUtils;
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
	private HashMap<String, ClassifierResult> results = new HashMap<>();
	
	private int historyLenght = 50;
	private double threshold = 0.2d;
	private int steps;
	private int hits;
	
	public String compute(int[] values, String name, boolean infer) {
		int[] sdr = ArrayUtils.isSparse(values) ? values : ArrayUtils.where(values, 
				ArrayUtils.INT_GREATER_THAN_0);
		
		if (infer)
			return infer(name, values);
		
		if (sdr.length == 0)
			return null;
		
		if (!this.outputs.containsKey(name)) {
			this.outputs.add(name, sdr);
			this.results.put(name, new ClassifierResult(name));
			return null;
		}
		
		List<Object> history = this.outputs.get(name);
		
		if (match(sdr, history)) {
		    // already have this pattern
		    return name;
		}
		
		history.add(0, sdr);
		
		if (history.size() > this.historyLenght)
			history.remove(this.historyLenght);
		
		return null;
	}
	
	public String infer(String realName, int[] values) {
		this.steps++;
		ClassifierResult result = this.results.get(realName);
		if (result != null)
			result.addStep();
		
		StringBuffer sb = new StringBuffer();
		
		for (String name : this.outputs.keySet()) {
			List<Object> records = this.outputs.get(name);
			for (Object record : records) {
				if (match(values, (int[]) record)) {
					if (name.equals(realName)) {
						this.hits++;
						if (result != null)
							result.addHit();
					}
					else {
						log.warn("Bad Hit: [" + name + "]");
						if (result != null)
							result.addWrong();
					}
					sb.append(name).append(" ");
					break;
				}
			}
		}
		
		return sb.length() > 0 ? sb.toString() : null;
	}
	
	private boolean match(int[] values, int[] record) {
		int[] diff = difference(values, record);
		double d = (double) diff.length / values.length; 
		
		return d <= this.threshold;
	}
	
	private boolean match(int[] values, List<Object> recordList) {
	    for (Object record : recordList)
	        if (match(values, (int[]) record))
	            return true;
	    
	    return false;
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
	
	public String getReport() {
		StringBuffer sb = new StringBuffer();
		
		for (ClassifierResult r : this.results.values()) {
			sb.append(r.toString());
			sb.append("\n");
		}
		
		return sb.toString();
	}

	public void reset() {
		this.outputs.clear();
		this.results.clear();
		this.steps = 0;
		this.hits = 0;
	}

}
