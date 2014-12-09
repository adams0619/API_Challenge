package challenge4;
/**
 * Result class converts JSON to POJO
 * 
 * @author Adams Ombonga
 * @version 1.0
 *
 */
public class Result {

	private String datestamp;
	int interval;
	
	public String getDateStamp() {
		return datestamp;
	}
	
	public int getInterval() {
		return interval;
	}
	
	public void setDateStamp(String datestamp) {
		this.datestamp = datestamp;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	@Override
	public String toString() {
		return "Datestamp [datestamp=" + datestamp + "]";
	}
	
}
