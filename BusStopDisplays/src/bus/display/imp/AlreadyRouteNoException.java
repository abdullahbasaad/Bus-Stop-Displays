package bus.display.imp;

public class AlreadyRouteNoException extends Exception {
	private static final long serialVersionUID = 1L;
	int routeNo;
	
	public AlreadyRouteNoException(int routeNo) {
		this.routeNo = routeNo;
	}

}

