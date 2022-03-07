// PhoneBookController.java
// Controller for Phone Book application
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import javafx.scene.control.ListCell;
import javafx.util.Callback;

import java.util.TreeMap;
// saving and loading objects to file
import java.io.*;

// saving and loading objects to file using xml
import javax.xml.bind.JAXB;
import java.nio.file.*;

// controller for a phone book application
// enables adding, deleting, updating of contacts
// as well as saving and loading of the entire phone book to/from a file
public class PhoneBookController {
	// tree map which maintains the order of names and their corresponding data
	private TreeMap<String, ContactInformation> contactTable;
	
	// class for enabling IO operations using XML
	TreeMapXML backup = new TreeMapXML();
	
	// messages to be displayed when adding a new contact
	String defaultNameMessage = "Enter name";
	String invalidNameMessage = "Invalid name";
	String existingNameMessage = "Contact already exists";
	
	String defaultFile = "PhoneBook.obj";
	String backupFile = "contacts.xml";

	// list view for contact browsing
    @FXML private ListView<String> phoneBookListView;
    
    // stores the list of String Objects
 	private final ObservableList<String> contacts =
 			FXCollections.observableArrayList();
 	// grid containing the contact view
    @FXML private GridPane contactGridPane;
    // text field containing the currently selected contact name
    @FXML private TextField nameTextField;
    // text field containing the currently selected contact number
    @FXML private TextField numberTextField;
    // text area containing the currently selected contact notes
    @FXML private TextArea notesTextArea;
    // text field containing the contact search bar
    @FXML private TextField searchTextField;
    // button for deleting the currently selected contact
    @FXML private Button deleteContactButton;
    // button for loading the phone book from a set file
    @FXML private Button loadPhoneBookButton;
    
    
    public void initialize() {
    	// initialize the contact table, making the comparison between keys ignore case difference
    	contactTable = new TreeMap<String, ContactInformation>(String.CASE_INSENSITIVE_ORDER);
    	
    	// when ListView selection changes, show 
    	// the selected contact in the contact view
    	// iff no contact is selected - hide the contact view
    	phoneBookListView.getSelectionModel().selectedItemProperty().addListener(
    			new ChangeListener<String>() {
    				@Override
    				public void changed(ObservableValue<? extends String> ov,
    						String oldValue, String newValue) {
    					if (newValue == null) {
    						// no contact selected - hide contact view
    						showContactView(false);
    					}
    					else {
    						// an existing contact is in focus
    						updateContactView(newValue);
    						
    						// disable editing the name field in case of an incomplete contact addition
    						if (nameTextField.isEditable()) {
    							nameTextField.setEditable(false);
    						}
    						
    						showDeleteButton(true);
    						// a contact is selected - show it in the contact view
    						showContactView(true);
    					}
    				}
    			}
    	);
    	
    	// when search text field changes, select the closest item currently in the list
    	searchTextField.textProperty().addListener(
    			new ChangeListener<String>() {
    				@Override
    				public void changed(ObservableValue<? extends String> ov,
    						String oldValue, String newValue) {
    					String fit = contactTable.ceilingKey(newValue);
    					// select the closest contact name bigger or equal to the search value
    					if (fit != null) {
    						phoneBookListView.getSelectionModel().select(fit);
    					}
    				}
    			}
    	);
    	
    	// create an item format for the list view
    	phoneBookListView.setCellFactory(
				new Callback<ListView<String>, ListCell<String>>() {
					@Override
					public ListCell<String> call(ListView<String> listView) {
						return new PhoneBookTextCell();
					}
				}
		);
    	
    	// hide the contact view when not in focus
    	showContactView(false);
    	
    	// disable the ability to load the phone book from file 
    	// if the proper file does not exist
    	setLoadPhoneBookState();
    }
    
    // prepare the contact view for an attempt to add a new contact
    @FXML
    void addNewContactButtonPressed(ActionEvent event) {
    	// clear the previously selected entry
    	clearContactFocus();
    	
    	// clear previous text
    	nameTextField.setEditable(true);
    	changeNamePromptText(defaultNameMessage);
    	numberTextField.clear();
    	notesTextArea.clear();
    	
    	showDeleteButton(false);
    	
    	showContactView(true);
    }

    // delete the currently selected contact from the phone book
    @FXML
    void deleteContactButtonPressed(ActionEvent event) {
    	contactTable.remove(nameTextField.getText());
    	updateContactList();
    	// disable the ability to delete when no item is in focus
    	showDeleteButton(false);
    }
    
    // load the phone book from one of two copies
    @FXML
    void loadPhoneBookButtonPressed(ActionEvent event) {
    	if (!loadPhoneBookFromFile(defaultFile)) {
    		// loading from first file failed, try loading the backup
    		// file using a different method
    		loadXML(backupFile);
    	}
    	
    	updateContactList();
    	clearContactFocus();
    	showContactView(false);
    }

    // save the contact information currently in view
    // if presented with a new contact - add it to the phone book.
    @FXML
    void saveContactButtonPressed(ActionEvent event) {
    	String contactName = nameTextField.getText().trim();
    	
    	if (nameTextField.isEditable()) {
    		// this is an addition of a potentially new contact
    		if (contactName == null || contactName.isEmpty()) {
    			// new contact name is not legal
    			changeNamePromptText(invalidNameMessage);
    			return;
    		}
    		else if (contactTable.containsKey(contactName)) {
    			// contact already exists
    			changeNamePromptText(existingNameMessage);
    			return;
    		}
    		else {
    			// disable further editing of name
    			nameTextField.setEditable(false);
    			
    			// add the new contact to the phone book
    			ContactInformation value = 
    					new ContactInformation(
    							numberTextField.getText().trim(),
    							notesTextArea.getText().trim());
    			
    			contactTable.put(contactName, value);
    			// update the list view
    			updateContactList();
    			// manually close the contact view since no item is currently selected
    			showContactView(false);
    		}
    	}
    	else {
    		// this is an update of an existing contact
    		ContactInformation contactInfo =
    				contactTable.get(contactName);
    		contactInfo.setPhoneNumber(numberTextField.getText().trim());
    		contactInfo.setNotes(notesTextArea.getText().trim());
    		// no need to sort the list again
    		clearContactFocus();
    	}
    }

    // save the phone book into a main save file and a backup file
    @FXML
    void savePhoneBookButtonPressed(ActionEvent event) {

    	savePhoneBookToFile(defaultFile);
    	saveXML(backupFile);
    	clearContactFocus();
    	showContactView(false);
    	
    	// enable the ability to load the phone book from a file if 
    	// such a file now exists
    	setLoadPhoneBookState();

    }
    
    // update the content of the contact view to refer
    // to the currently selected contact
    private void updateContactView(String item) {
    	// get a reference to the value object of current key
    	ContactInformation currentContact = contactTable.get(item);
    	// update contact information
    	nameTextField.setText(item);
    	numberTextField.setText(currentContact.getPhoneNumber());
    	notesTextArea.setText(currentContact.getNotes());
    }
    
    // update the list view of contact in the phone book to maintain a proper sorting
    private void updateContactList() {
    	contacts.setAll(contactTable.keySet());
    	phoneBookListView.setItems(contacts);
    }
    
    // clear the name text field and change the prompt shown
    private void changeNamePromptText(String newText) {
    	nameTextField.clear();
    	nameTextField.setPromptText(newText);
    }
    
    // show or hide the contact view
    private void showContactView(boolean state) {
    	contactGridPane.setVisible(state);
    }
    
    // enable the ability to delete the contact in the contact view
    private void showDeleteButton(boolean state) {
    	if (deleteContactButton.isDisabled() == state) {
    		deleteContactButton.setDisable(!state);
    	}
    }
    
    // remove the selection of any contact in the contact list
    private void clearContactFocus() {
    	phoneBookListView.getSelectionModel().clearSelection();
    }
    
    // save the phone book to a file using the default method
    private void savePhoneBookToFile(String fileName) {
    	try {
    		ObjectOutputStream output = new ObjectOutputStream(
    				new FileOutputStream(fileName));
    		
    		output.writeObject(contactTable);
    		output.flush();
    		
    		output.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    		System.err.println("Error writing to file");
    	}	
    }
    
    // load the phone book from a file using the default method
    // return a boolean state of success or failure to read
    @SuppressWarnings("unchecked")
	private boolean loadPhoneBookFromFile(String fileName) {
    	try {
    		ObjectInputStream input = new ObjectInputStream(
    				new FileInputStream(fileName));
    		
    		contactTable = (TreeMap<String, ContactInformation>)input.readObject();
    		
    		input.close();
    		return true;
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    		System.err.println("Error reading from file");
    		return false;
    	}
    	catch (ClassNotFoundException e) {
    		e.printStackTrace();
    		System.err.println("Error locating required object on file");
    		return false;
    	}
    }
    
    // backup option for saving the phone book - using XML
    private void saveXML(String fileName) {
    	try {
    		BufferedWriter output = 
    				Files.newBufferedWriter(Paths.get(fileName));
    		
    		backup.getTreeMap().putAll(contactTable);
    		JAXB.marshal(backup, output);
    		
    		output.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    		System.err.println("Error writing to file");
    	}
    }
    
    // backup option for loading the phone book - using XML
    private void loadXML(String fileName) {
    	try {
    		BufferedReader input =
    				Files.newBufferedReader(Paths.get(fileName));

    		backup = JAXB.unmarshal(input, TreeMapXML.class);
    		contactTable.clear();
    		contactTable.putAll(backup.getTreeMap());
    		input.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    		System.err.println("Error reading from file");
    	}
    }
    
    // check if a given file exists in the specified path
    private boolean checkForFile(String fileName) {
    	return Files.exists(Paths.get(fileName));
    }
    
    // enable load phone book button if at least one of the save files exists
    private void setLoadPhoneBookState () {
    	if (checkForFile(defaultFile) || checkForFile(backupFile)) {
    		// one of the two save file exists
    		loadPhoneBookButton.setDisable(false);
    	}
    	else {
    		loadPhoneBookButton.setDisable(true);
    	}
    }

}