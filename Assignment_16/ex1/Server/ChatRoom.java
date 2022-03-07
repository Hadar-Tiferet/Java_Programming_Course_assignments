// ChatRoom.java
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

// operation manager for a chat program between two clients
public class ChatRoom implements Runnable {
	private ChatRoomState chatRoomState; // determine if either chat process reached an end point
	
	private Socket firstUser;
	private Socket secondUser;
	private ExecutorService chatRoomProcess;
	private ObjectInputStream firstInput; // input stream from the first user
	private ObjectOutputStream firstOutput; // output stream to the first user
	private ObjectInputStream secondInput; // input stream from the second user
	private ObjectOutputStream secondOutput; // output stream to the second user
	private int timeout; // amount of time to wait for an input before closing the connection
	
	public ChatRoom(Socket firstUser, int timeout) {
		this.firstUser = firstUser;
		openFirstStreams();
		
		chatRoomState = new ChatRoomState();
		this.timeout = timeout;
	}
	
	public void addSecondUser(Socket user) {
			secondUser = user;
			openSecondStreams();
	}
	
	
	
	public void openFirstStreams() {
		try { // obtain streams for socket
			firstOutput = new ObjectOutputStream(firstUser.getOutputStream());
			firstOutput.flush(); // flush output buffer to send header information
			
			firstInput = new ObjectInputStream(firstUser.getInputStream());
		}
		catch (IOException ioException) {
			// error setting input and output streams for user
			ioException.printStackTrace();
		}
	}
	
	public void openSecondStreams() {
		try { // obtain streams for socket
			secondOutput = new ObjectOutputStream(secondUser.getOutputStream());
			secondOutput.flush(); // flush output buffer to send header information
			
			secondInput = new ObjectInputStream(secondUser.getInputStream());
		}
		catch (IOException ioException) {
			// error setting input and output streams for user
			ioException.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		chatRoomProcess = Executors.newFixedThreadPool(2);
		try {
			// set timeout requirements for the clients in the session.
			// inactivity for 5 minutes will cause termination
			firstUser.setSoTimeout(timeout);
			secondUser.setSoTimeout(timeout);
		}
		catch (SocketException socketException) {
			// sockets are unavailable, close the operation of the chat room
			chatRoomState.disable();
		}
		
		
		chatRoomProcess.execute(new User(firstInput, secondOutput, chatRoomState, timeout));
		chatRoomProcess.execute(new User(secondInput, firstOutput, chatRoomState, timeout));
		chatRoomProcess.shutdown();
		
		try {
			// make sure the thread finishes, so the server can close at will
			chatRoomProcess.awaitTermination(30, TimeUnit.MINUTES);
		}
		catch (InterruptedException interruptedException) {
			// the thread got interrupted
			
		}
		// close the open sockets
		try {
			firstUser.close();
			secondUser.close();
		}
		catch (IOException ioException) {
			// error closing the sockets
			ioException.printStackTrace();
		}
	}
}

class User implements Runnable {
	private final ObjectInputStream input; // input stream from user
	private final ObjectOutputStream output; // output stream to chat partner
	private final ChatRoomState chat;
	private final int secondsToTimeout;
	
	public User(ObjectInputStream input, ObjectOutputStream output,
			ChatRoomState chatRoomState, int millisecToTimeout) {
		this.input = input;
		this.output = output;
		chat = chatRoomState;
		secondsToTimeout = millisecToTimeout / 1000 ;
	}
	
	@Override
	public void run() {
		
		processConnection();
		
		closeStreams();
	}
	
	// send message to chat partner
	private void sendMessage(String message) {
		try { // send object to chat partner
			output.writeObject(String.format("%s", message));
			output.flush();
		}
		catch (IOException ioException) {
			// encountered an error while sending message
			chat.disable();
		}
	}
	
	private void closeStreams() {
		try { // close the input and output streams in use by the user
			output.close();
			input.close();
		}
		catch (IOException ioException) {
			// encountered an error while closing streams
			chat.disable();
		}
	}
	
	// process connection between a sending and a receiving client
	private void processConnection() {
		
		String message = String.format("%s%n%s%d%s",
				"Chat room established, you're now connected to a chat partner.", 
				"Note that inactivity for: ", secondsToTimeout,
				"seconds will lead to termination from the server.");
		sendMessage(String.format("%s:%n%s", "Server", message)); // inform the receiving user of the connection
		
		// handle the chat between two user until one disconnects
		while (chat.isOperational()) {
			// two users are currently connected
			try { // read message from input
				message = (String) input.readObject();
				
				if (message.equals("TERMINATE CONNECTION")) {
					// disconnect request received, inform the chat partner and close the connection
					message = String.format("Chat partner has disconnected.%nConnection terminated.");
					sendMessage(String.format("%s:%n%s", "Server", message));
					
					chat.disable();
				}
				else {
					// chat continues as normal, no disconnect request issued
					sendMessage(message);
				}
			}
			catch (SocketTimeoutException socketTimeoutException) {
				// the input user has reached timeout through inactivity.
				// inform the output client if possible and close the chat.
				message = String.format("Chat partner timed out.%nConnection terminated.");
				sendMessage(String.format("%s:%n%s", "Server", message));
				chat.disable();
			}
			catch (ClassNotFoundException classNotFoundException) {
				// message was of an unexpected type - ignore said message and continue
			}
			catch (IOException ioException) {
				// error reading message from input
				chat.disable();
			}
		}
	}
}

// inner class used by two User threads
class ChatRoomState {
	private boolean operational; // the state of the chat room
	
	public ChatRoomState() {
		operational = true;
	}
	
	// check if the chat room is still actively in use by both clients
	public boolean isOperational() {return operational;}
	
	// disable the chat room operation, leading to the closure of both clients connection
	public void disable() {operational = false;}
}
