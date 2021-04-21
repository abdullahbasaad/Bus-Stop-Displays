package bus.display.imp;

public class Route {
	
	int routeNo;
	String destination, origin;
	String schedule;
	
	public Route(int routeNo, String destination, String origin) {
		this.routeNo = routeNo;
		this.destination = destination;
		this.origin = origin;
	}
	
	public Route() {
		
	}
	
	public void setRouteNo(int r) {
		 this.routeNo = r;
	}
	
	public int getRouteNo() {
		return routeNo;
	}
	
	public void setDestination(String d) {
		 this.destination = d;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public void setOrigin(String o) {
		 this.origin = o;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public String getSchedule() {
		return schedule;
	}
	
	public void addschedule(String s) {
		schedule = s;
	}
}
