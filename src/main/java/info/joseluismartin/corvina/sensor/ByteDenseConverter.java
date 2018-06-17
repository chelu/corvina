package info.joseluismartin.corvina.sensor;

import java.util.BitSet;

/**
 * Convert an integer array to a byte dense array.
 * 
 * @author Jose Luis Martin
 * @since 1.1
 */
public class ByteDenseConverter implements DenseConverter {
	
	private int size = Byte.SIZE;
	
	public ByteDenseConverter() {
		
	}
	
	public ByteDenseConverter(int size) {
		this.size = size;
	}

	@Override
	public int[] convert(int[] array) {
		int[] dense = new int[array.length * this.size];
		for (int i = 0; i < array.length;  i++) {
			BitSet bs = BitSet.valueOf(new byte[] { (byte) array[i] });
			for (int j = 0; j < this.size; j++) {
				dense[i * this.size + j] = bs.get(j) ? 1 : 0;
			}
		}
		
		return dense;
	}

	public static ByteDenseConverter createRGBDenseConverter() {
		return new ByteDenseConverter(32);
	}
}
