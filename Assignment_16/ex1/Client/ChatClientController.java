// ChatClientController.java
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// controller class for a chat client application
public class ChatClientController {
	@FXML private TextField enterField; // area for input to the server
	@FXML private TextArea displayArea; // text area for messages from the client and the server
	@FXML private Button connectButton;
	@FXML private Button disconnectButton;
	@FXML private TitledPane serverInformationArea; // titled pane containing the server information
	@FXML private TextField serverNameTextField;
	@FXML private TextField serverPortTextField;
	@FXML private TextField userNameTextField;
	
	private Socket client; // socket to communicate with server
	private String serverName; // address of the requested server
	private int portNumber; // port number of the server within the requested address
	private String userName; // optional user name to be seen by the partner of the chat program
	private ObjectOutputStream output; // output stream to server
	private ObjectInputStream input; // input stream from server
	
	private IncomingMessagesTask task; // listen for messages from the server on a separate thread
	
	// initialize the client
	public void initialize() {
		// disable the ability to disconnect from a server before a connection is established
		disconnectButton.setDisable(true);
		// prevent the user from typing a message before a connection is established
		enterField.setEditable(false);
		
		displayArea.setEditable(false);
	}
	
	// attempt to connect to the specified chat server
	// enable writing if successful, indicate state of success or failure 
	@FXML
	void connectButtonPressed(ActionEvent event) {

		if ((userName = userNameTextField.getText().trim()) == "") {
			// user did not enter a user name, use 'Anonymous' instead
			userName = "Anonymous";
		}
		try {
			serverName = serverNameTextField.getText().trim();
			portNumber = Integer.parseInt(serverPortTextField.getText());
		
			connectToServer(serverName, portNumber); // create a Socket to make connection
			getStreams(); // get the input and output streams
			
			allowServerInformationEdits(false);
			disconnectButton.setDisable(false);
			connectButton.setDisable(true);
			
			// set up a thread listening for incoming messages from the server
			task = new IncomingMessagesTask(input, userName);
			
			// store messages received from the server by the task in the display area
			task.valueProperty().addListener((Observable, oldValue, newValue) -> {
				if (newValue != ".End.") { // task returns \n when it terminates
					displayMessage(newValue);
				}
			});
			
			// when task begins: enable the enterField for outgoing messages
			task.setOnRunning((succeededEvent) -> {
				setTextFieldEditable(true);
			});
			
			// when task completes successfully: set stage for a new connection
			task.setOnSucceeded((succeededEvent) -> {
				closeConnection();
			});
			
			task.setOnCancelled((succeededEvent) -> {
				closeConnection();
			});
			
			// create ExecutorService to manage threads
			ExecutorService incomingConnection = Executors.newFixedThreadPool(1);
			incomingConnection.execute(task); // start the task
			incomingConnection.shutdown();
		}
		catch (NumberFormatException numberException) {
			// enter a non integer port number
			displayMessage(String.format("%s%n", "Please enter a valid port number for the server."));
		}
		catch (IOException ioException) {
			// try again
			displayMessage(String.format("%s%n", "Connection attempt failed, please try again."));
		}
	}
	
	// send a disconnect request to the server and close the connection from the client side
	@FXML
	void disconnectButtonPressed(ActionEvent event) {
		enterField.setText("");
		sendMessage("TERMINATE CONNECTION"); // request termination from server
		displayMessage(String.format("%s%n", "Disconnecting from chat."));
		// close the connection to the server
		task.cancel();
	}
	
	// send the message currently present in the enter field to the server when the enter key is pressed
	@FXML
	void enterFieldEnterKeyPressed(ActionEvent event) {
		String message = enterField.getText();
		displayMessage(String.format("%s ->%n%s%n", "Me", message));
		sendMessage(String.format("%s:%n%s", userName, message));
		enterField.setText("");
	}
	
	
	// send message to server
	private void sendMessage(final String message) {
				try { // send object to server
					output.writeObject(message);
					output.flush(); // flush data to output
				}
				catch (IOException ioException) {
					displayArea.appendText("Client:\nError sending message\n");
				}
	}
	
	// manipulates displayArea in the event-dispatch thread
	private void displayMessage(final String messageToDisplay) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() { // updates displayArea
				displayArea.appendText(messageToDisplay);
			}
		});
	}
	
	// manipulates enterField in the event-dispatch thread
	private void setTextFieldEditable(final boolean editable) {
		Platform.runLater(new Runnable() {
			public void run() { // sets enterField's editability
				enterField.setEditable(editable);
			}
		});
	}
	
	// allow edits to the connection information, when not currently connected to a server
	private void allowServerInformationEdits(final boolean state) {
		Platform.runLater(new Runnable() {
			public void run() {
				serverNameTextField.setEditable(state);
				serverPortTextField.setEditable(state);
				userNameTextField.setEditable(state);
				serverInformationArea.setExpanded(state);
			}
		});
	}
	
	// close streams and socket
	private void closeConnection() {
		displayMessage("\nClosing connection");
		setTextFieldEditable(false); // disable enterField
		
		try {
			output.close(); // close output stream
			input.close(); // close input stream
			client.close(); // close socket
		}
		catch (IOException ioException) {
			ioException.printStackTrace();
		}
		
		// prepare for a new potential connection
		allowServerInformationEdits(true);
		connectButton.setDisable(false);
		disconnectButton.setDisable(true);
	}
	

	// connect to server with the specified name and port number
	private void connectToServer(String serverName, int portNumber) throws IOException {
		displayMessage("Attempting connection\n");
		try {
			// create Socket to make connection to server
			client = new Socket(InetAddress.getByName(serverName), portNumber);
			
			// display connection information
			displayMessage("Connected to: " + client.getInetAddress().getHostName());
		}
		catch (IOException ioException) {
			displayMessage(String.format("%s%n%s%n", "Unable to connect to the specified server.",
					"Please validate the desired server name and port and try again"));
			throw ioException;
		}
		
	}
	
	// get streams to send and receive data
	private void getStreams() throws IOException {
		// set up output stream for objects
		output = new ObjectOutputStream(client.getOutputStream());
		output.flush(); // flush output buffer to send header information
		
		// set up input stream for objects
		input = new ObjectInputStream(client.getInputStream());
	}
}
