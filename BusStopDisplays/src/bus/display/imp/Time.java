package bus.display.imp;

public class Time {
	String hours;
	String minutes;
	
	public Time (String hours, String minutes) {
		this.hours = hours;
		this.minutes = minutes;
	}
	
	public String getHours() {
		return hours;
	}
	
	public String getMinutes() {
		return minutes;
	}
	
	public int getTime() {
		String tm = hours+minutes;
		tm = tm.replace(":", "");
		return Integer.parseInt(tm);
	}
}
