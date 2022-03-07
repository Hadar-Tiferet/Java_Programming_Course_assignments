// IncomingMessagesTask.java
import javafx.concurrent.Task;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.EOFException;

// a task for managing incoming messages from a given input stream, sends updates to the creator application
public class IncomingMessagesTask extends Task<String> {
	private String userName; // user name given by the creator application, used to signal the user name back
	private ObjectInputStream input; // input stream from server
	private String incomingMessage = "";
	
	public IncomingMessagesTask(ObjectInputStream input, String userName) {
		this.input = input;
		this.userName = userName;
	}
	
	@Override
	protected String call() {
		
		processConnection();
		// signal process completion
		return ".End.";
	}
	
	// process connection with server, update the current value according to the received message
	private void processConnection() {
		
		try {
			updateValue(String.format("%s%n%s%s%s%n", "Connection to chat server established.",
					"using: ", userName, " as a username"));
			
			do { // process messages sent from server
				try { // read message and display it
					incomingMessage = (String) input.readObject(); // read new message
					updateValue(String.format("%s%n", incomingMessage)); // display message
				}
				catch (ClassNotFoundException classNotFoundException) {
					updateValue("\nUnknown object type received");
				}
			}
			while (!incomingMessage.equals("TERMINATE CONNECTION"));
		}
		catch (EOFException eofException) {
			updateValue("\nClient terminated connection");
		}
		catch (IOException ioException) {
			updateValue("\nClient terminated connection");
		}
		
	}
}
