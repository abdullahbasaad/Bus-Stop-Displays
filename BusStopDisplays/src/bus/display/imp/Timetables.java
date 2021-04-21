package bus.display.imp;

public class Timetables {
	
	String routeId;
	String schedule;
	
	public Timetables(String routeId, String schedule) {
		this.routeId = routeId;
		this.schedule = schedule;
	}

	public String getSchedule() {
		return schedule;
	}

}
