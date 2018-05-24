package info.joseluismartin.corvina.htm;


/**
 * Result from Classification.
 * 
 * @author Jose Luis Martin.
 */
public class ClassifierResult {

	private String name;
	private long steps = 0;
	private long hits = 0;
	private long wrongs = 0;

	public ClassifierResult() {

	}
	
	public ClassifierResult(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the tests
	 */
	public long getSteps() {
		return steps;
	}

	/**
	 * @param tests the tests to set
	 */
	public void seetSteps(long steps) {
		this.steps = steps;
	}

	/**
	 * @return the hits
	 */
	public long getHits() {
		return hits;
	}

	/**
	 * @param hits the hits to set
	 */
	public void setHits(long hits) {
		this.hits = hits;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(": ");
		sb.append("tests: ");
		sb.append(this.steps);
		sb.append(" hits: ");
		sb.append(this.hits);
		sb.append(" (" + (double) this.hits / this.steps * 100 + " %) ");
		sb.append("wrongs: ");
		sb.append(this.wrongs);
		
		return sb.toString();
	}

	public void addHit() {
		this.hits++;
	}

	public void addStep() {
		this.steps++;
	}
	
	public void addWrong() {
		this.wrongs++;
	}
	
}
