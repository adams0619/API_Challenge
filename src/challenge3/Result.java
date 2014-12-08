package challenge3;
/**
 * Result class converts JSON to POJO
 * 
 * @author Adams Ombonga
 * @version 1.0
 *
 */
public class Result {

	private String prefix;
	private String[] array;
	
	public String getNeedle() {
		return prefix;
	}
	
	public String[] getHaystack() {
		return array;
	}
	
	public void setNeedle(String needle) {
		this.prefix = needle;
	}
	
	public void setHaystack(String[] haystack) {
		this.array = haystack;
	}
	
	@Override
	public String toString() {
		return "Needle [needle=" + prefix + "]";
	}
	
}
