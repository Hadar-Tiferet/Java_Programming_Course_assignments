// WeatherRequestTask.java
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// a task to service a client request from the server on a separate thread
public class WeatherRequestTask implements Runnable {
	private DatagramSocket socket;
	private DatagramPacket packet;
	// database for storing and referencing entries and values
	private ConcurrentHashMap<String, String> dataBase; 
	// number of lines expected per data entry in the given file
	private final int linesPerEntry; 
	// size of a packet to be sent to clients
	private final int packetSize; 
	// number of bytes available for a message, without the contents id
	private final int dataLength; 
	// name of file to update the database from
	private final File fileName; 
	// template for a client request for the current list
	private final String requestList; 
	// template for a client request for an updated list
	private final String requestRefreshedList; 
	// part of a sent message to client that identifies messages sent from this server
	private final String idServer; 
	// part of a sent message to client that identifies messages containing a city list
	private final String idList; 
	// part of a sent message to client that identifies messages containing an entry
	private final String idEntry; 
	// part of a sent message to client that identifies messages containing information
	private final String idReport; 
	
	// constructor
	public WeatherRequestTask(DatagramSocket socket, DatagramPacket packet,
			ConcurrentHashMap<String, String> dataBase, String fileName,
			int linesPerEntry, int packetSize, int dataLength) {
		this.socket = socket;
		this.packet = packet;
		this.dataBase = dataBase;
		this.fileName = new File(fileName);
		this.packetSize = packetSize;
		this.linesPerEntry = linesPerEntry;
		this.dataLength = dataLength;
		// set the consistent messages
		requestList = "Request cities list";
		requestRefreshedList = "Request list refresh";
		idServer = "Weather server";
		idList = "Cities update";
		idEntry = "City forecast";
		idReport = "Server report";
	}
	
	@Override
	public void run() {
		try {
			// get the details of the received packet
			String message = new String(packet.getData(), 0, packet.getLength());
			InetAddress clientAddress = packet.getAddress();
			int clientPort = packet.getPort();
			
			// service the requested operation
			if (message.equals(requestRefreshedList)) {
				// attempt to update the database from file
				ConcurrentHashMap<String, String> tempDataBase = new ConcurrentHashMap<String, String>();
				// try to update a temporary data base before changing the current one
				if (getEntriesFromFile(tempDataBase)) {
					// update from file successful, insert the changed and new data into the shared data base
					dataBase.putAll(tempDataBase);
					// remove entries that do not exist in the new version of the file
					dataBase.keySet().retainAll(tempDataBase.keySet());
					
					// send to client a response containing the response id and a comma separated city list
					sendMessage(String.format("%s%s%s", idServer, idList, commaSeparatedKeys()), 
							clientAddress, clientPort);
				}
				else {
					// update from file failed, send client a fitting message
					sendMessage(String.format("%s%s%s", idServer, idReport,
							"Update operation is currently unavaliable due to a server error."),
							clientAddress, clientPort);
				}
			}
			else if (message.equals(requestList)) {
				// send to client a response containing the response id and a comma separated city list
				sendMessage(String.format("%s%s%s", idServer, idList, commaSeparatedKeys()), 
						clientAddress, clientPort);
			}
			else if (dataBase.containsKey(message)) {
				// client sent an update request for a valid city
				// send the value associated with the city within the data base
				sendMessage(String.format("%s%s%s", idServer, idEntry, dataBase.get(message)),
						clientAddress, clientPort);
			}
			else {
				// client sent an unrecognized command, send a report as such
				sendMessage(String.format("%s%s%s", idServer, idReport,
						"Unrecognized command."),
						clientAddress, clientPort);
			}
		}
		catch (IOException ioException) {
			// operation failed, inform the server itself and close thread.
			// (the server will continue operation)
			System.err.printf("%nWarning: a WheatherRequestTask operation has failed%n");
		}
	}
	
	// send message to client
	private void sendMessage(final String message, InetAddress clientAddress,
			int clientPort) throws IOException {
		// send datagram to client	
		byte[] data = new byte[packetSize];
		data = message.getBytes();	
		
		// create sendPacket
		DatagramPacket sendPacket = new DatagramPacket(data, data.length,
				clientAddress, clientPort);	
		// send packet to the client
		socket.send(sendPacket);
	}
	
	// attempt to read data from file
	private boolean getEntriesFromFile(ConcurrentHashMap<String, String> dataStructure) {
    	// attempt to read forecast information in the format of 4 non empty lines
		// per entry from a given file
    	// write each entry into ConcurrentHashMap cities
    	try(Scanner input = new Scanner(fileName)) { // close Scanner upon exit
    		
    		int currentLine = 0; // counts the number of lines since a new entry was configured
    		String[] entries = new String[linesPerEntry]; // string array for city initialization
    		
    		// read from file until end of file is reached
    		while (input.hasNextLine()) {
    			entries[currentLine] = input.nextLine();
    			currentLine++;
    			
    			if (currentLine == linesPerEntry) {
    				// enough entries to create a new entry
    				String forecast = String.format("%s:%n%s%n%s%n%s", entries[0], 
    						entries[1], entries[2], entries[3]);
    				
    				if (forecast.length() > dataLength) {
    					// if the message attached to this entry is larger than can be sent in a 
    					// packet - notify that some data might be cut off
    					System.out.printf("%nWarning: data for entry: %s %s %d %s",
    							entries[0], "exceeds the allowed space of", 
    							dataLength, "characters");
    				}
    				// add the current entry to the data base
    				dataStructure.put(entries[0], forecast);
    				
    				// reset line count to accept a potential new entry
    				currentLine = 0;
    			}
    		}
    		
    		if (currentLine != 0) {
    			// the number of lines is not divisible by 5
    			throw new NoSuchElementException();
    		}
    		// managed to get entries from file successfully
    		return true;
    		
    	} catch (FileNotFoundException | IllegalStateException | SecurityException e) {
    		System.err.printf("%nFile: %s could not be opened%n", fileName);
    		e.printStackTrace();
    		return false;
    	} catch (NoSuchElementException e) {
    		System.err.printf("%nFile: %s does not follow to the format of 4 lines per entry%n", fileName);
    		e.printStackTrace();
    		return false;
    	}
    }
	
	// return a comma separated list of the keys in dataBase
	private String commaSeparatedKeys() {
		Set<String> keySet = dataBase.keySet();
		return String.join(",", keySet);
	}
}
