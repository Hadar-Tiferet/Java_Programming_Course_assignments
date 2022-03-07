// ChatServer.java
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.security.SecureRandom;

// a server managing chat rooms between sets of two clients
public class ChatServer {
	public static void main(String[] args) throws IOException {
		boolean listening = false; // listening for clients state
		// number of attempts to locate a free port for the server to listen on
		int portAcquisitionAttempts = 10;
		// number of client connection fails before the server will be shutdown
		int acceptableConnectionFails = 5;
		// current count of failed connections to the server
		int failedConnections = 0;
		// wait up to 5 minutes between each connection request
		int acceptNewConnectionTimeout = 300000;
		// client in an active chat will be disconnected for being idle for 1 minute
		int chatTimeout = 60000; 
		
		// attempt to secure a free socket
		ServerSocket serverListeningSocket = getFreeSocket(portAcquisitionAttempts);
		if (serverListeningSocket == null) {
			System.err.printf("Failed to locate an available port.%nSutting down.%n");
			System.exit(1);
		}
		else {
			// server can continue executing
			listening = true;
		}
		// print to default output the details of the server, including
		// its listening port for incoming clients
		String serverInfo = String.format("Server Host Name: %s%nServer IP address: %s%nServer port:%s%n",
				serverListeningSocket.getInetAddress().getHostName(),
				serverListeningSocket.getInetAddress().getHostAddress(),
				serverListeningSocket.getLocalPort());
		System.out.printf("Server ready for clients.%n%s", serverInfo);
		
		// executor service to manage chat rooms in separate threads
		ExecutorService chatRooms = Executors.newCachedThreadPool();
		// set timeout on listening port for 5 minutes without new traffic
		serverListeningSocket.setSoTimeout(acceptNewConnectionTimeout);
		
		// a socket for a client connection
		Socket client;
		
		while (listening) {
			
			try {
				// accept a new client and send it to a chat room
				client = serverListeningSocket.accept();
				ChatRoom task = new ChatRoom(client, chatTimeout);
				
				// look for a second client
				client = serverListeningSocket.accept();
				// add the second client to the chat room
				task.addSecondUser(client);
				
				// start the execution of the chat between the two accepted clients
				chatRooms.execute(task);
			}
			catch (SocketTimeoutException timeoutException) {
				// reached timeout - close the server for new operation
				System.out.printf("Server shutting down due to inactivity%n");
				listening = false;
			}
			catch (IOException ioException) {
				System.err.printf("Failed to accept a new client.%n");
				// failed to accept a new client, try again
				if (failedConnections < acceptableConnectionFails) {
					failedConnections++;
				}
				else {
					// passed the allowed fail threshold - don't accept new clients
					System.out.printf("Server shutting down due to repeated failed connection attempts%n");
					listening = false;
				}
			}
		}
		
		// close the listening socket
		serverListeningSocket.close();
		chatRooms.shutdown();
	}
	
	// method for attempting to acquire a free socket
	private static ServerSocket getFreeSocket(int numberOfAttempts) {
		int minPort = 1024;
		int maxPort = 65535;
		int portRange = maxPort - minPort;
		ServerSocket availableSocket;
		SecureRandom generator = new SecureRandom();
		
		System.out.printf("Attempting to allocate a port for the server to listen on.%n");
		
		for (int i = 1; i <= numberOfAttempts; i++) {
			int portToTry = minPort + generator.nextInt(portRange);
			try {
				availableSocket = new ServerSocket(portToTry);
				
				// a free socket was found, return it
				System.out.printf("Attempt %d out of %d succeded. Port %d is available.%n",
						i, numberOfAttempts, portToTry);
				return availableSocket;
			}
			catch (IOException ioException) {
				System.out.printf("Attempt %d out of %d failed. Port %d is unavailable.%n",
						i, numberOfAttempts, portToTry);
			}
		}
		
		// failed to allocate a free socket
		return null;
	}
}
