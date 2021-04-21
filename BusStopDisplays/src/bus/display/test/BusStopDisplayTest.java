package bus.display.test;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

import bus.display.controller.*;
import bus.display.imp.*;
import bus.display.imp.NoRouteException;

class BusStopDisplayTest {

	BusStopDisplay bsd;
	Time t = null;
	Route rt = null;
	String bStopId = null, bStopName = null;
	int routeNo = 0;
	String scheduleStr = "07:10,07:15,07:30,04:50,08:40";
	String nBusTime = null;
	
	@Test
	public void testInit() {
		bsd = new BusStopDisplay("B100","City centre");
		bStopId = bsd.getBusStopId();
		bStopName = bsd.getBusStopName();	
		System.out.println(bsd);
		assertEquals(bsd.getBusStopId(),"B100");
		assertEquals(bsd.getBusStopName(),"City centre");
	}
	
	@Test
	public void testInit2() throws NoRouteException {
		
		testInit();		
		routeNo = 100;
		String destination = "City centre";
		String origin = "West Midlands";
		
		bsd.addRoute(routeNo, destination, origin);
		rt = bsd.findRoute(routeNo);
		rt.getRouteNo();
		rt.addschedule(scheduleStr);
		
		assertEquals(rt.getRouteNo(),100);
		assertEquals(rt.getDestination(),"City centre");
		assertEquals(rt.getOrigin(),"West Midlands");
		assertEquals(rt.getSchedule(),scheduleStr);
	}
	
	@Test
	public void testInit3() throws NoRouteException {
		testInit2();
		
		Time t = new Time("07","10");
		nBusTime = bsd.getTimeOfNextBus(rt.getRouteNo(), t);
		String hours = t.getHours();
		String minutes = t.getMinutes();
		String tm = hours+":"+minutes;
		assertNotEquals(nBusTime,tm);
		assertEquals(t.getHours(),"07");
		assertEquals(t.getMinutes(),"10");
	}
	
	@Test
	public void testInit4() throws NoRouteException {			
		testInit2();
		Time t = new Time("07","10");
		bsd.getCallingRoutes();
		System.out.println(bsd.getRouteSchedule(100));
		assertEquals(t.getHours(),"07");
	}
	
	@Test
	public void testCratePositive() throws NoRouteException {
		testInit();
		
		String scheduleStr1 = "07:10,07:15,07.30,04:50,08:40,08:50,08:57";
		String scheduleStr2 = "07:30,07:35,07.45,04:50,08:40";
		
		bsd.addRoute(60, "Wholy Castle", "Easter");
		bsd.addRoute(61, "Wholy Castle1", "Easter1");
		bsd.addRoute(62, "Wholy Castle2", "Easter2");
		
		Route rObj = bsd.findRoute (60);
		Route rObj1 = bsd.findRoute (61);
		Route rObj2 = bsd.findRoute (62);
		rObj.addschedule(scheduleStr);
		rObj1.addschedule(scheduleStr1);
		rObj2.addschedule(scheduleStr2);

		assertEquals(bsd.getBusStopId(),"B100");
		assertEquals(bsd.getBusStopName(),"City centre");
		assertEquals(rObj.getRouteNo(),60);
		assertEquals(rObj.getDestination(),"Wholy Castle");
		assertEquals(rObj.getOrigin(),"Easter");
		
		bsd.addScheduledToExpected();
		System.out.println(BusStopDisplay.expectedBus.size()+" Scheduled have been added");
	}
	
	@Test
	public void testgetdepartedTimeNegative() throws NoRouteException {
		testInit();
		
		bsd.addRoute(60, "Wholy Castle", "Easter");
		
		Route rObj = bsd.findRoute (60);
		
		ExpectedBus eb = new ExpectedBus (60, 1, rObj.getDestination(), rObj.getSchedule());
		assertEquals(eb.getStatus(), BusStatus.onTime);
		assertNotEquals(eb.getdepartedTime(), "14:20");
		assertEquals(eb.getCancelTime(),"0");
		
		try {
			if (!eb.getStatus().equals(BusStatus.onTime))
				fail();
		}
		catch(Exception e) {
			assertTrue (true);
		}
	}
	
	@Test
	public void testGetCallingRoutesPositive() throws NoRouteException {
		testInit2();
		
		bsd.addRoute(60, "Wholy Castle", "Easter");
		
		Route rObj = bsd.findRoute (60);
		rObj.addschedule(scheduleStr);
		bsd.getCallingRoutes();
		assertEquals(rObj.getSchedule(),scheduleStr);
	}
	
	@Test
	public void testGetCallingRoutesNegative() throws NoRouteException {
		testInit2();
		
		String sch = "10:01,10:20,10:35,10:50,11:20";
		bsd.addRoute(69, "Wholy Castle", "Easter");
		Route rObj = bsd.findRoute (69);
		String prevSchedule = rObj.getSchedule();
		rObj.addschedule(sch);
		assertNotEquals(rObj.getSchedule(),prevSchedule);
	}	
	
	@Test
	public void testDepartuteTimesPositive() throws NoRouteException {
		
		testInit2();
		assertEquals(rt.getSchedule(), scheduleStr);
	}
	
	@Test
	public void testGetTimeoNextBusesPositive() throws NoRouteException {
		
		testInit2();
		Time t = new Time("07","15");

		String nTime = bsd.getTimeOfNextBus(rt.getRouteNo(),t);
		assertEquals(nTime, "07:30");
	}
	
	@Test
	public void testGetTimeoNextBusesNegative() throws NoRouteException {
		
		testInit2();
		Time t = new Time("07","15");

		String nTime = bsd.getTimeOfNextBus(rt.getRouteNo(),t);
		assertNotEquals(nTime, "07:10");
		System.out.println(nTime);
	}


}
