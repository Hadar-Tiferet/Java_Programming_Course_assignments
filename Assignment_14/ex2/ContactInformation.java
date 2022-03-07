// Contact.java
// information about a contact in a phone book with comparable names.
import java.io.*;

public class ContactInformation implements Serializable{
	// serialization to support saving data to a file
	private static final long serialVersionUID = 1L;
	
	// instance variables
	String phoneNumber;
	String notes;
	
	// empty constructor
	public ContactInformation() {
		this("","");
	}
	
	// default constructor
	public ContactInformation(String phoneNumber, String notes) {
		// check input using regular expressions
		this.phoneNumber = phoneNumber.trim();
		this.notes = notes.trim();
	}
	
	// get and set methods
	public String getPhoneNumber() {return phoneNumber;}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getNotes() {return notes;}
	
	public void setNotes(String notes) {this.notes = notes;}
}