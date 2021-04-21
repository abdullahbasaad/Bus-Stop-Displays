package bus.info.controller;

import bus.display.common.Notifier;
import bus.display.controller.BusStopDisplay;
import bus.display.imp.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BusInfo implements Notifier {
	
	static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmm");
	List<ExpectedBus> cancelList;
	List<ExpectedBus> delayList;
	List<ExpectedBus> departedList;
	List<ExpectedBus> expectedJourneys;
	List<BusStopDisplay> departedReqular;
	
	public BusInfo() {
		expectedJourneys = new ArrayList<ExpectedBus>();
		loadExpecetedBusInfo ();
		cancelList = new ArrayList<ExpectedBus>();
		delayList = new ArrayList<ExpectedBus>();
		departedList = new ArrayList<ExpectedBus>();
		departedReqular = new ArrayList<BusStopDisplay>();
	}

	void loadExpecetedBusInfo () {
		expectedJourneys.addAll(BusStopDisplay.ebs);
	}
	
	void receiveNotification () {
		for (ExpectedBus exB: BusStopDisplay.sendNotiy) {
			for (ExpectedBus exJ: expectedJourneys) {
				if (exB.equals(exJ)) {
					exJ.setStatus(exB.getStatus());
					exJ.setdepartedTime(exB.getdepartedTime());
				}
			}
		}
	}
	
	public void simulate () throws InterruptedException {
		journyesCancelled ();
		journyesDelayed ();
		journyesDeparted ();
		TimeUnit.SECONDS.sleep(2);
	}
	
	@Override
	public void updateState () {
		journyesCancelled ();
		journyesDelayed ();
		journyesDeparted ();
	}
	
	public List<ExpectedBus> journyesCancelled () {	
		LocalDateTime now = LocalDateTime.now(); 
		int tNow = Integer.parseInt(dtf.format(now).toString());
		
		for (ExpectedBus b: expectedJourneys) {
			if (!b.getStatus().equals(BusStatus.cancelled)) {
				if ((((b.getRouteNo() == 33) && (b.getStatus().equals(BusStatus.onTime))) &&
						(tNow >= Integer.parseInt(BusStopDisplay.addMinutes(b.getTime().replace(":", ""),3).replace(":", ""))) ||
						((b.getStatus().equals(BusStatus.delayed)) && (b.getDelay() >= 10))) || 
						((b.getRouteNo() == 12 && (Integer.parseInt(b.getTime().replace(":", "")) >= Integer.parseInt(BusStopDisplay.addMinutes(dtf.format(now).toString().replace(":", ""),8).replace(":", "") ))))){ 
						cancelList.add(b);
				}
			}	
		}
		return cancelList;
	}
	
	public List<ExpectedBus> journyesDelayed () {
		LocalDateTime now = LocalDateTime.now(); 
		int tNow = Integer.parseInt(dtf.format(now).toString());
		
		for (ExpectedBus b: expectedJourneys) {
			if (!b.getStatus().equals(BusStatus.cancelled)) {
				if (((b.getRouteNo() == 25) || (b.getRouteNo() == 15)) && (b.getStatus().equals(BusStatus.onTime)) && 
						 (tNow >= Integer.parseInt(BusStopDisplay.addMinutes(b.getTime().toString().replace(":", ""),3).replace(":", "")))) {
					delayList.add(b);
				}	
			}
		}
		return delayList;
	}
	
	public List<ExpectedBus> journyesDeparted () {
		LocalDateTime now = LocalDateTime.now(); 
		int tNow = Integer.parseInt(dtf.format(now).toString());

		for (ExpectedBus b: expectedJourneys) {
			if (!b.getStatus().equals(BusStatus.departed)) {
				if ((b.getStatus().equals(BusStatus.onTime)) && (b.getRouteNo() == 17) &&
						(tNow >= Integer.parseInt(BusStopDisplay.addMinutes(b.getTime().toString().replace(":", ""),3).replace(":", "")))) {
						departedList.add(b);
				}
			}	
		}
		return departedList;
	}
	
	public static String getLastBusStop (int routeNo) {
		for (ExpectedBus l: BusStopDisplay.lstSorted) {	
			if (l.getRouteNo() == routeNo) {
				return l.getLastBusStop();
			}
		}
		return null;
	}
	
	public void getBusInfoUpdates () {
		LocalDateTime now = LocalDateTime.now();
		
		for (ExpectedBus i: BusStopDisplay.sendNotiy) {
			for (ExpectedBus j: expectedJourneys) {
				if ((i==j) && (!j.getStatus().equals(BusStatus.delayed))) {
					j.setStatus(BusStatus.departed);
					j.setdepartedTime(dtf.format(now).toString());
				}
			}
		}
	}	
		
	public void cleanTransactions() {
		cancelList.clear();
		delayList.clear();
		departedList.clear();
	}

}
