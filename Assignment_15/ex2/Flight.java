// Flight.java
// class containing information about a single flight, departing from one airport and landing in another one.
// Flight extends Thread and it's run method can be executed on a seperate thread within the application
import java.security.SecureRandom;

// class containing information about a specific flight
// extends thread as per the assignment requirements (otherwise I'd implement interface Runnable)
public class Flight extends Thread {
	private final int flightNumber;
	private final SynchronizedAirport departureAirport;
	private final SynchronizedAirport landingAirport;
	
	private final SecureRandom generator = new SecureRandom();
	// minimum time to spend on an action, in milliseconds
	private final int minActionTime = 2000;
	// upper bound for a randomly generated time required for runway interactions, in milliseconds
	private final int runwayTimeBound = 3000;
	// upper bound for a randomly generated time required for flight, in milliseconds
	private final int flightTimeBound = 8000;
	
	public Flight(int flightNumber, SynchronizedAirport departureAirport,
			SynchronizedAirport landingAirport) {
		this.flightNumber = flightNumber;
		this.departureAirport = departureAirport;
		this.landingAirport = landingAirport;
	}
	
	@Override
	public void run() {
		try {
			int occupiedRunway = 0;
			
			// request a free runway from the departure airport
			occupiedRunway = departureAirport.depart(flightNumber);
			// simulate departure by sleeping for a random amount of time
			sleep(minActionTime + generator.nextInt(runwayTimeBound));
			// free the occupied runway
			departureAirport.freeRunway(flightNumber, occupiedRunway);
			
			occupiedRunway = 0; // currently not using a runway
			
			// simulate flight from departure to landing airport
			sleep(minActionTime + generator.nextInt(flightTimeBound));
			
			// request a free runway from the landing airport
			occupiedRunway = landingAirport.land(flightNumber);
			// simulate landing by sleeping for a random amount of time
			sleep(minActionTime + generator.nextInt(runwayTimeBound));
			// free the occupied runway
			landingAirport.freeRunway(flightNumber, occupiedRunway);
			
			// flight is done
		}
		catch (InterruptedException exception) {
			Thread.currentThread().interrupt(); // re-interrupt the thread
		}
	}
}
