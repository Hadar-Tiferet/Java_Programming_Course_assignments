// WeatherServer.java
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// server sending weather updates for cities backed up by a file to client applications using UDP
public class WeatherServer {
	public static void main(String[] args) {
		final int LINES_PER_ENTRY = 4; // number of lines per entry expected within the data file
		DatagramSocket socket; // socket to connect to client
		int packetSize = 512; // size of a datagram packet
		// number of bytes reserved for message id between the server and client
		int reservedPacketSpace = 27;
		// selected port for the server
		int serverPortNumber = 5511;
		// time before the server times out if idle (5 minutes)
		int serverTimeout = 300000;
		// name of the file containing the information for the data base
		String forecastFileName;
		// if a file was given as a command argument - use it, otherwise use the default file
		if (args.length == 0) {
			forecastFileName = "forecast.txt";
		}
		else {
			forecastFileName = args[0];
		}
		
		// shared data base, loading information from a file
		ConcurrentHashMap<String, String> cities = new ConcurrentHashMap<String, String>();
		
		// attempt to insert the information (if correctly exits) from the 
		// forecast file into the data structure
		
		try(Scanner input = new Scanner(new File(forecastFileName))) {
			
			int currentLine = 0; // counts the number of lines since a new entry was configured
			String[] entries = new String[LINES_PER_ENTRY]; // string array for city initialization
			
			// read from file until end of file is reached
    		while (input.hasNextLine()) {
    			entries[currentLine] = input.nextLine();
    			currentLine++;
    			
    			if (currentLine == LINES_PER_ENTRY) {
    				// enough entries to create a new entry
    				String forecast = String.format("%s:%n%s%n%s%n%s", entries[0], 
    						entries[1], entries[2], entries[3]);
    				
    				if (forecast.length() > (packetSize - reservedPacketSpace)) {
    					// if the message attached to this entry is larger than can be sent in a 
    					// packet - notify that some data might be cut off
    					System.out.printf("%nWarning: data for entry: %s %s %d %s",
    							entries[0], "exceeds the allowed space of", 
    							(packetSize - reservedPacketSpace), "characters");
    				}
    				// add the current entry to the data base
    				cities.put(entries[0], forecast);
    				// reset line count to accept a potential new entry
    				currentLine = 0;
    			}
    		}
    		
    		if (currentLine != 0) {
    			// the number of lines in the current entry is not 4
    			throw new NoSuchElementException();
    		}
    	// send specific error messages to the default output of the server in case of a problem
    	} catch (FileNotFoundException | IllegalStateException | SecurityException e) {
    		System.err.printf("%nFile: %s could not be opened%n%s%n",
    				forecastFileName,
    				"To use a different file - relaunch the server with the file as an argument");
    		e.printStackTrace();
    		System.exit(1);
    	} catch (NoSuchElementException e) {
    		System.err.printf("%nFile: %s does not follow to the format of 4 lines per entry%n",
    				forecastFileName);
    		e.printStackTrace();
    		System.exit(1);
		}
		
		ExecutorService clientRequests = Executors.newCachedThreadPool();
		// data structure cities is initialized, start accepting requests
		try {
			socket = new DatagramSocket(serverPortNumber);
			// close the socket if no request comes for within 5 minutes
			socket.setSoTimeout(serverTimeout);
			System.out.printf("Server is active on port: %d%nReading information from file: %s%n"
					, serverPortNumber, forecastFileName);
			
			while (true) {
				try { // receive packet, send it to a new thread for execution
					// and continue listening for new packets
					byte[] data = new byte[packetSize]; // setup packet
					DatagramPacket receivePacket = new DatagramPacket(data, data.length);
					
					// receive a new packet
					socket.receive(receivePacket);
					// send client request to a separated thread for execution
					clientRequests.execute(new WeatherRequestTask(socket, receivePacket, cities,
							forecastFileName, LINES_PER_ENTRY, packetSize,
							(packetSize - reservedPacketSpace)));
				}
				catch (SocketTimeoutException exception) {
					// server did not get any request for the specified amount of time - shutdown
					System.out.printf("Socket timed out - no activity found on the server port.%n");
					return;
				}
				catch (IOException ioException) {
					System.err.printf("%nException receiving a packet%n");
				}
			}
		}
		catch (SocketException socketException) {
			socketException.printStackTrace();
			System.exit(1);
		}
		finally {
			clientRequests.shutdown();
			System.out.printf("Server shutting down.%n");
		}
	}
}