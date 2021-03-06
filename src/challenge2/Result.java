package challenge2;
/**
 * Result class converts JSON to POJO
 * 
 * @author Adams Ombonga
 * @version 1.0
 *
 */
public class Result {
	
	private String needle;
	private String[] haystack;
	
	public String getNeedle() {
		return needle;
	}
	
	public String[] getHaystack() {
		return haystack;
	}
	
	public void setNeedle(String needle) {
		this.needle = needle;
	}
	
	public void setHaystack(String[] haystack) {
		this.haystack = haystack;
	}
	
	@Override
	public String toString() {
		return "Needle [needle=" + needle + "]";
	}
	
}
