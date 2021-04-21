package bus.display.imp;

public class ExpectedBus {
	private static int last_id = 0;
	int id = 0;
	int routeId;
	int journeyNo;
	String destination;
	String time;
	String cancelTime;
	String receivedTime;
	String departedTime;
	String lastBusStop;
	int delay;
	BusStatus busStatus;
	
	public ExpectedBus (int routeId, int journeyNo, String destination, String time) {   
		   last_id++;
		   this.routeId = routeId;
		   this.busStatus = BusStatus.onTime;
		   this.journeyNo = journeyNo;
		   this.destination = destination;
		   this.time = time;
		   this.delay = 0;
		   this.id = last_id;
		   this.cancelTime = "0";
		   this.receivedTime = "0";
		   this.lastBusStop = "";
		   this.departedTime = "0";
	}
	
	public int getId() {
		return id;
	}
	
	public int getRouteNo() {
		return routeId;
	}
	
	public String getTime() {
		return time;
	}
	
	public int getJourney() {
		return journeyNo;
	}
	   
	public String getDestination() {
		return destination;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public BusStatus getStatus() {
		return busStatus;
	}
	
	public void setStatus(BusStatus s) {
		busStatus = s;
	}
	
	public void setDelay(int d) {
		this.delay = d;
	}
	
	public String getCancelTime() {
		return cancelTime;
	}
	
	public void setCancelTime(String c) {
		this.cancelTime = c;
	}
	
	public String getReceivedlTime() {
		return receivedTime;
	}
	
	public void setReceivedlTime(String r) {
		receivedTime = r;
	}
	
	public String getLastBusStop () {
		return lastBusStop;
	}
	
	public void setLastBusStop (String lBS) {
		lastBusStop = lBS;
	}
	
	public String getdepartedTime () {
		return departedTime;
	}
	
	public void setdepartedTime (String dT) {
		departedTime = dT;
	}
}
