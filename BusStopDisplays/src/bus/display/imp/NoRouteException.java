package bus.display.imp;

public class NoRouteException extends Exception {
	private static final long serialVersionUID = 1L;
	String routeNo;
	
	NoRouteException(String routeNo) {
		this.routeNo = routeNo;
	}
}

