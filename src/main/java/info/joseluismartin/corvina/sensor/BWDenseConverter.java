package info.joseluismartin.corvina.sensor;

/** 
 * Convert an int array to BW dense form.
 * 
 * @author Jose Luis martin.
 */
public class BWDenseConverter implements DenseConverter {

	/** values upper this value are white */
	private int blackLimit = 100;
	
	/**
	 * @return the blackLimit
	 */
	public int getBlackLimit() {
		return blackLimit;
	}

	/**
	 * @param blackLimit the blackLimit to set
	 */
	public void setBlackLimit(int blackLimit) {
		this.blackLimit = blackLimit;
	}

	@Override
	public int[] convert(int[] array) {
		int[] dense = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			dense[i] = array[i]  > blackLimit ? 1 : 0;
		}
		
		return dense;
	}

}
