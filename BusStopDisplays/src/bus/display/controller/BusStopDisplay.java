package bus.display.controller;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import bus.display.common.Notifier;
import bus.display.imp.AlreadyBusStopException;
import bus.display.imp.AlreadyRouteNoException;
import bus.display.imp.BusStatus;
import bus.display.imp.ExpectedBus;
import bus.display.imp.NoRouteException;
import bus.display.imp.Route;
import bus.display.imp.Time;
import bus.display.imp.Timetables;
import bus.info.controller.BusInfo;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BusStopDisplay implements Notifier {
    String busStopId;
    String name;

	public  static Map<String, BusStopDisplay> busStop = new HashMap<String, BusStopDisplay>();
	public  static Map<Integer, Route> routes = new HashMap<Integer, Route>();
	public  static Map<Integer, ExpectedBus> expectedBus = new HashMap<Integer, ExpectedBus>();
	public  static Map<String, Timetables> timetables = new HashMap<String, Timetables>();
	
	public static List<ExpectedBus> ebs;
	public static List<ExpectedBus> sendNotiy;
	public static List<ExpectedBus> lstSorted;
	public static List<ExpectedBus> removeBuses;
	public static List<ExpectedBus> cnclBus;
	public static List<ExpectedBus> dlyBus;
	public static List<ExpectedBus> dptBus;
	
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmm");
	
	public BusStopDisplay (String busStopId, String name) {
		this.busStopId = busStopId;
		this.name = name;
		
		ebs = new ArrayList<>();
		lstSorted = new ArrayList<>();
		removeBuses = new ArrayList<>();
		cnclBus = new ArrayList<>();
		dlyBus = new ArrayList<>();
		dptBus = new ArrayList<>();
		sendNotiy = new ArrayList<>();
	}
	
	public BusStopDisplay () {
		
	}
	
	public int addRoute (int routeNo, String destination, String origin) {
		Route r = new Route(routeNo, destination, origin);
		routes.put(routeNo, r);
		
		return routeNo;
	}
	
	public Route findRoute (int routeNo) throws NoRouteException {
		Route r = routes.get(routeNo);	
		return r;
	}
	
	public static BusStopDisplay getBusStop (String id){
		for (Entry<String, BusStopDisplay> bs : busStop.entrySet()) {
			if (bs.getKey().equals(id)) {
				return bs.getValue();
			}
		}
		return null;
	}
	
	public static void create(String stopInfoFile, String routeInfoFile, String timetableFile) throws AlreadyRouteNoException, IOException, AlreadyBusStopException {	
		callParseRoutes(routeInfoFile);
		callParseBusStop(stopInfoFile);
		callTimetables(timetableFile);		
	}
	
	public Map<Integer, ExpectedBus> addScheduledToExpected() {
		for (Map.Entry<Integer,Route> i : routes.entrySet()) {	
			String str[] = i.getValue().getSchedule().split(",");
			List<String> tmList = new ArrayList<String>();
			tmList = Arrays.asList(str);
			int count = 1;
			for(String s: tmList){
				ExpectedBus eb = new ExpectedBus (i.getKey(), count, i.getValue().getDestination(), s);
				expectedBus.put(eb.getId(), eb);
				ebs.add(eb);
				count += 1;
			}
		}
		return expectedBus;
	}
	
	public String getBusStopId() {
		return busStopId;
	}
	
	public void setBusStopId(String id) {
		busStopId = id;
	}
	
	public BusStopDisplay getBusStopDisplay() {
		return this;
	}
	
	public String getBusStopName() {
		return name;
	}
	
	public void setBusStopName(String name) {
		this.name = name;
	}
	
	public static void callParseRoutes (String routeFile) throws IOException, AlreadyRouteNoException {		
		BufferedReader file = new BufferedReader(
                new FileReader(routeFile)
        );     
        boolean eof = false;
        while (!eof) {
        	String route_data = file.readLine();
            if(route_data == null) {
                break;
            }
            else {
           		parseRoutes (route_data);
            }
        }
        file.close();
	}
	
	public static void callParseBusStop (String busStopFile) throws IOException, AlreadyBusStopException {	
		BufferedReader file = new BufferedReader(
                new FileReader(busStopFile)
        );
        
        boolean eof = false;
        while (!eof) {
        	String bStop_data = file.readLine();
            if(bStop_data == null) {
                break;
            }
            else {
            	parseBusStopInfo (bStop_data);
            }
        }
        file.close();
	}
	
	public static void callTimetables (String TimetableFile) throws IOException, AlreadyRouteNoException {	
		BufferedReader file = new BufferedReader(
                new FileReader(TimetableFile)
        );
        
        boolean eof = false;
        while (!eof) {
        	String timetable_data = file.readLine();
            if(timetable_data == null) {
                break;
            }
            else {
            	parseTimetable (timetable_data);
            }
        }
        file.close();
	}
	
	@Override
	public void updateState() {
		sendNotifyToBusInfo ();
	}
	
	public static Route parseRoutes (String route_info) throws AlreadyRouteNoException {
		String[] parts = route_info.split(",");			
		if (routes.containsKey(Integer.parseInt(parts[0]))) {
			throw new AlreadyRouteNoException(Integer.parseUnsignedInt(parts[0]));
		}
		
		Route r = new Route (Integer.parseInt(parts[0]), parts[1], parts[2]);
		routes.put(Integer.parseInt(parts[0]),r);	
		return r; 
	}
	
	public static BusStopDisplay parseBusStopInfo (String stop_info) throws AlreadyBusStopException {
		String[] parts = stop_info.split(",");			
		if (busStop.containsKey(parts[0])) {
			throw new AlreadyBusStopException(parts[0]);
		}	
		
		BusStopDisplay bsd  = new BusStopDisplay (parts[0], parts[1]);
		busStop.put(parts[0],bsd);
		return bsd;
	}
	
	public static Timetables parseTimetable (String timetable_info) throws AlreadyRouteNoException {
		String[] parts = timetable_info.split(",");		
		String timesStr = "";
		for (int i=1; i<parts.length;i++) {
			if (timesStr.length()==0)
				timesStr += parts[i];
			else
				timesStr += ","+parts[i];
		}
		Timetables tt = new Timetables (parts[0], timesStr);
		timetables.put(parts[0],tt);	
		for (Map.Entry<Integer,Route> entry : routes.entrySet()) {
			if (entry.getValue().getRouteNo() == Integer.parseInt(parts[0])) {
				entry.getValue().addschedule(timesStr);
			}
		}	
		return tt; 
	}
	
	public void getCallingRoutes () {
		for (Map.Entry<Integer,Route> entry : routes.entrySet()) 
            System.out.println(entry.getValue().getRouteNo()+"  "+
            				   entry.getValue().getDestination()+"  "+
            		           entry.getValue().getOrigin()+"  "+
            				   entry.getValue().getSchedule());
	}
	
	public String getRouteSchedule (int routeNo) {
		for (Map.Entry<Integer,Route> entry : routes.entrySet()) {
			if (entry.getKey().equals(routeNo)) {
				return entry.getValue().getSchedule();
			}     
		}
		return "No route";
	}
	
	public String getDepartureTimes (int routeNo)  {
		for (Entry<Integer, ExpectedBus> entry : expectedBus.entrySet()) {
			if (entry.getKey().equals(routeNo)) {
				return entry.getValue().getTime();
			}     
		}
		return "No route";
	}
	
	public String getTimeOfNextBus (int routeNo, Time t)  {
		String tm = null; 
		tm = getRouteSchedule(routeNo);
		if (!tm.equals("No route")){
			tm = tm.replace(":","");
			int tr = t.getTime();

			String str[] = tm.split(",");
			List<String> tmList = new ArrayList<String>();
			tmList = Arrays.asList(str);
			
			for(String s: tmList){
				int tt = Integer.parseInt(s);
				if (tt > tr) 
					return s.substring(0,2)+":"+s.substring(2,4);
			}
		}
		return "No buses";	
	}
	
	public void display (Time t) throws InterruptedException, ParseException {
		int rows =0,counter = 0;

		getBusInfoUpdates ();
		cleanList();
		cleanUpdateDisplay();
		getAllBuses (t);
		
		System.out.println("");
		System.out.println("-------------------------------------------------------------");
		System.out.println(rightPadding("Number",10)+rightPadding("Destination",25)+rightPadding("Due at",15)+rightPadding("Status",15));
		System.out.println("-------------------------------------------------------------");
		
		if (ebs.size() > 10) 
			counter = 10;
		else 
			counter = ebs.size();
		
		for(ExpectedBus s: lstSorted){			
			String busSta = "";
			LocalDateTime now = LocalDateTime.now(); 
			String time1 = s.getTime().replace(":", "");
			String time2 = dtf.format(now).toString();
			
			if (s.getStatus().equals(BusStatus.onTime)) {
				busSta = "On time";
			}else if (s.getStatus().equals(BusStatus.cancelled)) {
				busSta = "Cancelled";
			}else if (s.getStatus().equals(BusStatus.departed)) {
				busSta = "Departed";
			}else if (s.getStatus().equals(BusStatus.delayed)) {
				SimpleDateFormat format = new SimpleDateFormat("HHmm");
				Date date1 = format.parse(time1);
				Date date2 = format.parse(time2);
				long difference = date2.getTime() - date1.getTime();
				
				int dif = (int)((difference/1000)/60);

				if (dif == 1) {
					busSta = dif+" Minute delay";
				}else if (dif > 1) {
					busSta = dif+" Minutes delay";
				}		
				s.setDelay(dif);
			}
			System.out.println(rightPadding(String.valueOf(s.getRouteNo()),10)+rightPadding(s.getDestination(),25)+rightPadding(s.getTime(),15)+busSta);
			rows +=1;
			if (counter == rows)
				break;
		}
	}
	
	void cleanList() {
		ebs.removeAll(removeBuses);
		removeBuses.clear();
	}
	
	public static String rightPadding(String str, int num) {
	    return String.format("%1$-" + num + "s", str);
	}
	
	public void cleanUpdateDisplay() { 	
		LocalDateTime now = LocalDateTime.now(); 
		int tNow = Integer.parseInt(dtf.format(now).toString());
		
		for (ExpectedBus b: ebs) {
			
			if ((!(b.getRouteNo()==33) && !(b.getRouteNo()==17) && !(b.getRouteNo()==25) &&	!(b.getRouteNo()==15)) &&		
				(b.getStatus().equals(BusStatus.onTime)) && (tNow == Integer.parseInt(addMinutes(b.getTime().replace(":", ""),1).replace(":", "")))) {
				b.setStatus(BusStatus.departed);
				b.setdepartedTime(dtf.format(now).toString());
				sendNotiy.add(b);
			}
		   
			if ((b.getStatus().equals(BusStatus.cancelled)) && (tNow > Integer.parseInt(b.getCancelTime().replace(":","")))) {
				   removeBuses.add(b);
			}
				
		   if (b.getStatus().equals(BusStatus.departed)){
				if (Integer.parseInt(b.getdepartedTime().replace(":","")) < tNow) {
					b.setLastBusStop(busStopId);
					removeBuses.add(b);
				}
			}
		}
	}

	static List<ExpectedBus> getAllBuses (Time t){
		lstSorted.clear();
		String tm = t.getHours()+t.getMinutes();
		while (!tm.equals("2359")) {
			for (ExpectedBus s : ebs) {	
				if (s.getTime().replace(":", "").equals(tm)) {			   
					lstSorted.add(s);
				}
			}
			tm = addMinutes(tm, 1);
		}
		return lstSorted;
	}
	
	public static String addMinutes(String t, int n) {
		if (t.length()<5) {
			t = t.substring(0, 2)+":"+t.substring(2, 4);
		}
		DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm");
	    LocalTime lt = LocalTime.parse(t);
	    String newTime = df.format(lt.plusMinutes(n));

		return newTime.replace(":", "");	
	}	
	
	public  void getBusInfoUpdates () {
		LocalDateTime now = LocalDateTime.now();
		BusInfo bI = new BusInfo();
		cnclBus = bI.journyesCancelled();
		dlyBus = bI.journyesDelayed();
		dptBus = bI.journyesDeparted();
		
		for (ExpectedBus i: cnclBus) {
			for (ExpectedBus j: ebs) {
				if (i==j) {
					j.setStatus(BusStatus.cancelled);
					j.setCancelTime(dtf.format(now).toString());
				}
			}
		}
		
		for (ExpectedBus i: dlyBus) {
			for (ExpectedBus j: ebs) {
				if (i==j) {
					j.setStatus(BusStatus.delayed);
				}
			}
		}
		
		for (ExpectedBus i: dptBus) {
			for (ExpectedBus j: ebs) {
				if (i==j) {
					j.setStatus(BusStatus.departed);
					j.setdepartedTime(dtf.format(now).toString());
				}
			}
		}
		bI.cleanTransactions();
	}
	
	public List<ExpectedBus> sendNotifyToBusInfo () {
		return sendNotiy;
	}
	
}

