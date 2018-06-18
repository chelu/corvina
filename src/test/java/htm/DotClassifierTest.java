package htm;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.numenta.nupic.algorithms.Classification;

import info.joseluismartin.corvina.htm.DotClassifier;

/**
 * Test for DotClassifier
 * 
 * @author Jose Luis Martin
 * @since 1.1
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class DotClassifierTest {
	private static final String ACTUAL_VALUE = "actValue";
	private static final String A = "A";
	private static final String B = "B"; 
	private static final String BUCKET_IDX = "bucketIdx";
	
	private int[][] input = {{ 0, 1 }, { 0, 2 }, { 0, 1 }, { 3 } };
	
	@Test
	public void testLearn() {
		DotClassifier dc  = new DotClassifier();
		Map<String, Object> classification = new HashMap<>();
		classification.put("inputLength", 4);
		classification.put(ACTUAL_VALUE, A);
		classification.put(BUCKET_IDX, 0);
		dc.compute(1, classification, input[0], true, false);
		classification.put(ACTUAL_VALUE, A);
		classification.put(BUCKET_IDX, 0);
		dc.compute(1, classification, input[1], true, false);
		classification.put(BUCKET_IDX, 0);
		dc.compute(1, classification, input[2], true, false);
		classification.put(ACTUAL_VALUE, B);
		classification.put(BUCKET_IDX, 1);
		Classification<Object> c = dc.compute(1, classification, input[3], true, true);
		
		assertEquals(B, c.getMostProbableValue(1));
		assertEquals(0.0, c.getStat(1, 0) , 0.001);
	
	}
	
}
