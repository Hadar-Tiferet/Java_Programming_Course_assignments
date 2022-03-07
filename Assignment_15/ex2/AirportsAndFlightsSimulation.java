// AirportsAndFlightsSimulation.java
// Main class for a simulation of a number of flights (10 by default)
// departing from and landing on randomly chosen airports (2 in total by default)
// each airport facilitating multiple runways each (3 by default)
// with each flight operating on a separated thread concurrently
import java.security.SecureRandom;

public class AirportsAndFlightsSimulation {
	public static void main(String[] args) {
		// total number of airports in the simulation, can be changed
		final int numberOfAirports = 2;
		// number of runways per airport in the simulation, can be changed
		final int numberOfRunways = 3;
		// total number of flights in the simulation, can be changed
		final int numberOfFlights = 10;
		// a string to be used in the process of creating the name of each airport
		final String airportDesignation = "Airport";
		
		final SecureRandom generator = new SecureRandom();
		// create a given amount of airport objects
		SynchronizedAirport[] airports = new SynchronizedAirport[numberOfAirports];
		// initialize the airports
		for (int i = 0; i < numberOfAirports; i++) {
			airports[i] = new SynchronizedAirport(
					airportDesignation + String.valueOf(i + 1), numberOfRunways);
		}
		
		// create a given amount of flight objects
		// as Threads, per the assignment requirement
		Thread[] flights = new Flight[numberOfFlights];
		// initialize each flight
		for (int i = 0; i < numberOfFlights; i++) {
			// implementation for an arbitrary amount of airports
			int departureAirport = generator.nextInt(airports.length);
			int landingAirport = generator.nextInt(airports.length);
			
			// ensure generation of different airports for departure and landing
			while (landingAirport == departureAirport) {
				// generate a new airport for landing airport
				landingAirport = generator.nextInt(airports.length);
			}
			
			flights[i] = new Flight(i + 1, airports[departureAirport],
					airports[landingAirport]);
		}
		
		// start of simulation message
		System.out.printf(
				"Simulating %d flights between %d airports containing %d airports each:%n",
				numberOfFlights, numberOfAirports, numberOfRunways);
		
		// run the threads
		for (int i = 0; i < numberOfFlights; i++) {
			flights[i].start();
		}
		
		// ensure main thread finishes last
		try {
			for (int i = 0; i < numberOfFlights; i++) {
				flights[i].join(); // wait for the thread to die
			}
		} catch(InterruptedException exception) {
			System.out.println("main interrupted");
		}
		
		// end of simulation message
		System.out.println("Simulation ended.");
	}
}
