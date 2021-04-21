package bus.display.main;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.io.IOException;
import java.text.ParseException;

import bus.display.controller.BusStopDisplay;
import bus.display.imp.*;
import bus.info.controller.BusInfo;

public class Main {
	public static BusStopDisplay bsd;
	
	private static void loadData() throws AlreadyRouteNoException, IOException, AlreadyBusStopException, ParseException {
		
		String ROUTES_FILE = "routes.csv";
		String BUSSTOPINFO_FILE = "stop_info.csv";
		String TIMETABLE_FILE = "timetable.csv";
		
		BusStopDisplay.create(BUSSTOPINFO_FILE, ROUTES_FILE, TIMETABLE_FILE);
		bsd = BusStopDisplay.getBusStop("BS05");
		bsd.addScheduledToExpected();
	}

	public static void main(String[] args) throws InterruptedException, ParseException, NoRouteException, 
											  AlreadyRouteNoException, IOException, AlreadyBusStopException {
		
		loadData();
		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HH:mm");
	    String formattedDate = myDateObj.format(myFormatObj);
		String hr = formattedDate.substring(0, 2);
	    String mi = formattedDate.substring(3, 5);
	    Time tm= new Time(hr,mi);  
	    BusInfo bI = new BusInfo();
	    
		while (true) {		
			bsd.display(tm);	
			bI.simulate();
			TimeUnit.SECONDS.sleep(10);
		}
	}
}
