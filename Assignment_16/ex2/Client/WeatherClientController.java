// WeatherClientController.java
import javafx.application.Platform;
import java.util.Arrays;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.IOException;
import java.net.UnknownHostException;

// controller class for a weather client application
public class WeatherClientController {
	// text area that displays information from the server and client
	@FXML private TextArea displayArea; 
	// titled pane containing information about the server
	@FXML private TitledPane serverInformationArea; 
	// address of the requested server
	@FXML private TextField serverNameTextField; 
	// port of the requested server
	@FXML private TextField serverPortTextField;
	// button for requesting the list of available cities from the currently selected server
	@FXML private Button requestCitiesListButton;
	// button for requesting the server to update its list from file and send the new list of cities
	@FXML private Button requestServerRefreshButton;
	// button for requesting the weather forecast of the currently selected city from the server
	@FXML private Button requestForecastButton;
	// button for clearing the list of servers and the server information, allowing an attempt to reach
	// a new server
	@FXML private Button clearServerSelectionButton;
	
	@FXML private ListView<String> citiesListView;
	// stores the list of cities with available forecast updates from the server
	private final ObservableList<String> cities =
				FXCollections.observableArrayList();
	
	private DatagramSocket client; // socket to communicate with server using UDP
	private InetAddress serverAddress;
	private int serverPort;
	private String selectedCity; // the city currently selected within citiesListView
	private int packetSize = 512; // size of a datagram packet
	private int socketTimeout = 5000; // wait for 5 seconds before declaring failure to receive packet
	// initialize the client
	public void initialize() {
		try {
			// attempt to get a new socket and place a timeout timer on it
			client = new DatagramSocket();
			client.setSoTimeout(socketTimeout);
		}
		catch (SocketException exception) {
			System.err.printf("Encountered error while opening a socket for the client");
			System.exit(1);
		}
		displayArea.setEditable(false);
		clearServerSelectionButton.setDisable(true);
		
		// when citiesListView selection changes, request the currently selected city's info from the server
		citiesListView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> ov,
							String oldValue, String newValue) {
							selectedCity = newValue;
					}
				}
		);
	}
	
	// request a list of available cities from the server
	@FXML
	void requestCitiesListButtonPressed(ActionEvent event) {
		try {
			serverAddress = InetAddress.getByName(serverNameTextField.getText().trim());
			serverPort = Integer.parseInt(serverPortTextField.getText());
			
			allowInput(false);
			
			sendMessage("Request cities list");
			serverNameTextField.setEditable(false);
			serverPortTextField.setEditable(false);
			clearServerSelectionButton.setDisable(false);
			requestCitiesListButton.setDisable(true);
			
		}
		catch (NumberFormatException numberException) {
			displayMessage("Please enter a valid integer as a port number.");
		}
		catch (UnknownHostException unknownHostException) {
			displayMessage("Unrecognized server selected.");
		}
	}
	
	// clear the client of information from the previous server and allow contact with a new server
	@FXML
	void clearServerSelectionButtonPressed(ActionEvent event) {
		cities.clear(); // clear the list of saved cities
		citiesListView.getSelectionModel().clearSelection(); // clear the currently selected city
		citiesListView.setItems(cities);
		allowInput(false);
		serverNameTextField.setEditable(true);
		serverPortTextField.setEditable(true);
		requestCitiesListButton.setDisable(false);
		clearServerSelectionButton.setDisable(true);
	}
	
	// request a forecast update from the server for the currently selected city
	@FXML
	void requestForecastButtonPressed(ActionEvent event) {
		if (selectedCity == null) {
			displayMessage("Must select a valid city in order to request forecast");
		}
		else {
			allowInput(false);
			sendMessage(selectedCity.trim()); // request termination from server
		
			citiesListView.getSelectionModel().clearSelection();
		}
	}
	
	// request the server to update its database and send the updated list back
	@FXML
	void requestServerRefreshButtonPressed(ActionEvent event) {
		allowInput(false);
		citiesListView.getSelectionModel().clearSelection();
		citiesListView.setItems(cities);
		sendMessage("Request list refresh");
	}
	
	
	// send message to server
	private void sendMessage(final String message) {
		// send message to server
		try { // send datagram to server
			byte[] data = message.getBytes();
			
			// create sendPacket
			DatagramPacket sendPacket = new DatagramPacket(data, data.length,
					serverAddress, serverPort);
			client.send(sendPacket);
			
			receiveMessage(); // attempt to receive message from the server
		}
		catch (IOException ioException) {
			displayMessage("Client:\nError sending Packet\n");
		}
	}
	
	// attempt to receive information back from the server
	private void receiveMessage() {
		// block input until response has been received
		allowInput(false);
		// run on a separate thread
		Platform.runLater(new Runnable() {
			@Override
			public void run() { // attempt to receive data from the server
				try {
					String message;
					byte[] data = new byte[packetSize]; // setup packet
					DatagramPacket receivePacket = new DatagramPacket(data, data.length);
					
					client.receive(receivePacket); // wait for packet
					
					// check if we reached our weather server
					message = new String(receivePacket.getData(), 0, 14);
					//Weather server -> this is the weather server we're looking for
					
					// figure out what to do with the received message
					if (message.equals("Weather server")) {
						// this is a message from our specific server, determine which kind of message
						message = new String(receivePacket.getData(), 14, 13);
						//Cities update -> this message contains a list of cities the server supports
						//City forecast -> this message contains a forecast for a previous request
						//Server report
						if (message.equals("Cities update")) {
							message = new String(receivePacket.getData(), 27, receivePacket.getLength());
							cities.clear(); // remove the previous list of cities from the client
							cities.addAll(Arrays.asList(message.split(",")));
							citiesListView.setItems(cities); // bind citiesListView to cities
							displayMessage("Updated list of cities from the server");
						}
						else if (message.equals("City forecast")) {
							message = new String(receivePacket.getData(), 27, receivePacket.getLength());
							displayMessage(message);
						}
						else if (message.equals("Server report")) {
							message = new String(receivePacket.getData(), 27, receivePacket.getLength());
							displayMessage(message);
						}
						else {
							// received an unsupported command
							displayMessage("Unsuppported command received. please try again");
						}
						
						// release the controls for future requests
						allowInput(true);
					}
					else {
						// server input not recognized - ask user to check the server address
						displayMessage("Could not recognize server. please check the server information");
						
						allowInput(false);
					}	
				}
				catch (SocketTimeoutException timeoutException) {
					displayMessage("Message request timed out. please check server information");
				}
				catch (IOException ioException) {
					displayMessage("Encountered an error, please try again");
				}
			}
		});
	}
	
	
	// manipulates displayArea to show a given message
	private void displayMessage(final String messageToDisplay) {
				displayArea.setText(messageToDisplay);
	}
	
	// enable or disable the ability to request information from the server via the buttons
	private void allowInput(boolean state) {
		requestServerRefreshButton.setDisable(!state);
		requestForecastButton.setDisable(!state);
	}
	
}
