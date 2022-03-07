/*
 * Author: Hadar Tiferet
 * File: LetterSet.java
 */

// class LetterSet represents a letter and a boolean state associated with said letter
public class LetterSet {
	private final String letter; // a single letter, as a String
	private boolean usedState; // boolean variable, used to 
	
	// Constructor, sets the letter according to input, converted to lower case and the default used state of false
	public LetterSet(String letter) {
		this.letter = letter.toLowerCase();
		usedState = false;
	}
	
	
	public String getLetter() {
		return letter;
	}
	// set the usedState of the object to true, indicates that the letter was used in the current guess of a word
	public void setAsUsed() {
		this.usedState = true;
	}
	
	public boolean getUsedState() {
		return usedState;
	}
	// reset the usedState of the object back to the default value - false
	public void resetUsedState() {
		this.usedState = false;
	}
}
