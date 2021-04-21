package bus.display.imp;

public class AlreadyBusStopException extends Exception{
	private static final long serialVersionUID = 1L;
	String busStopId;

	public AlreadyBusStopException(String busStopId) {
		this.busStopId = busStopId;
	}
}
