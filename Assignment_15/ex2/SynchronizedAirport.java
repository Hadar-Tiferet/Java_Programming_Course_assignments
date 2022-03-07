// SynchronizedAirport.java
// class managing a single airport as part of an airport and flight simulation done with concurrency in mind
// access to mutable data is thread safe and allowed for use by one thread at a time
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import java.util.LinkedList;

public class SynchronizedAirport {
	// Lock to control synchronization with this airport object, using a fair ordering policy
	private final Lock airportAccessLock = new ReentrantLock(true);
	
	// condition to control runway allocation
	private final Condition canInteract = airportAccessLock.newCondition();
	
	// instance variables
	private final String name; // airport designated name
	// List facilitating free for use runways
	// item getting removed from the list = runway unavailable for use
	// item getting added to the list = runway becomes available for use
	private final LinkedList<Integer> availableRunways;
	
	// constructor
	public SynchronizedAirport(String name, int numberOfRunways) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException("Airport name must be a valid string");
		}
		else if (numberOfRunways < 1) {
			throw new IllegalArgumentException("An airport must contain at least one runway");
		}
		
		this.name = name;
		
		availableRunways = new LinkedList<Integer>();
		for (int i = 1; i <= numberOfRunways; i++) {
			availableRunways.add(i);
		}
	}
	
	// allocate a free runway for a flight departure
	public int depart(int flightNumber) throws InterruptedException {
		int freeRunway = 0;
		airportAccessLock.lock(); // lock for use of one task at a time
		
		// try to acquire a free runway, otherwise wait on interaction
		try {
			// while no runway is free, place thread in waiting state
			while (availableRunways.isEmpty()) {
				canInteract.await(); // wait until a runway becomes available
			}
			
			// a free runway is available, allocate the one which was least recently used
			freeRunway = availableRunways.remove();
			
			// display info about the flight
			displayState(flightNumber, freeRunway, "departed from");
		}
		finally {
			airportAccessLock.unlock(); // unlock access to the airport methods
		}
		
		return freeRunway;
	}
	
	// allocate a free runway for a flight landing
	public int land(int flightNumber) throws InterruptedException {
		int freeRunway = 0;
		airportAccessLock.lock(); // lock for use of one task at a time
		
		// try to acquire a free runway, otherwise wait on interaction
		try {
			// while no runway is free, place thread in waiting state
			while (availableRunways.isEmpty()) {
				canInteract.await(); // wait until a runway becomes available
			}
			
			// a free runway is available, allocate the one which was least recently used
			freeRunway = availableRunways.remove();
			
			// display info about the flight
			displayState(flightNumber, freeRunway, "landed on    ");
		}
		finally {
			airportAccessLock.unlock(); // unlock access to the airport methods
		}
		
		return freeRunway;
	}
	
	// add a runway back to the available runways list
	public void freeRunway(int flightNumber, int freedRunway) throws InterruptedException {
		airportAccessLock.lock(); // lock for use of one task at a time
		try {
			availableRunways.add(freedRunway);
			
			displayState(flightNumber, freedRunway, "freed        ");
			canInteract.signal(); // signal the thread waiting on the lock the longest
			// as per the requirement that flights will be handled with fairness in mind
		}
		finally {
			airportAccessLock.unlock(); // unlock access to the airport methods
		}
	}
	
	// print information about the flight currently using the facilities of the airport
	// contains IO operations and thus optimally would not have been a part of the locked block
	public void displayState(int flightNumber, int runwayNumber, String action) {
		try {
			airportAccessLock.lock(); // lock for use of one task at a time
			System.out.printf("Flight: %3d %s runway: %3d of airport: %s%n",
					flightNumber, action, runwayNumber, name);
		}
		finally {
			airportAccessLock.unlock(); // unlock for usage
		}
	}
}
