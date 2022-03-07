/*
 * Author: Hadar Tiferet
 * File: LegalInput.java
 */

// class LegalInput manages a list of letters that can be used in the program and validates user input of letters
public class LegalInput {
	private final int NUMBER_OF_LETTERS = 26; // numbers of letters in the alphabet
	private LetterSet[] letters = new LetterSet[NUMBER_OF_LETTERS];
	
	// Constructor, create an array of LetterSet objects, each containing a letter of the alphabet and a used state
	public LegalInput() {
		char c = 'a';
		for (int i = 0; i < NUMBER_OF_LETTERS; ++c, i++) {
			// convert the char c to a String type using String.valueOf
			letters[i] = new LetterSet(String.valueOf(c));
		}
	}
	
	// check the current input against the legal input options and send feedback on the input
	public boolean checkInput(String input) {
		for (int i = 0; i < letters.length; i++) {
			// check if the current input is equal to one of the allowed letters
			if (input.equalsIgnoreCase(letters[i].getLetter())) {
				// check if the letter was used before
				if (!letters[i].getUsedState()) {
					letters[i].setAsUsed(); // update the used letters list
					return true; // used letter array has been updated successfully
				}
				else {
					System.out.printf("->Letter %s is already in use, try again.%n%n", input);
					return false; // print a message informing the user of the issue and send an indication
				}
			}
		}
		
		// the input does not fit any of the legal letters
		System.out.printf("->Input: %s is not a valid letter, try again.%n%n", input);
		return false;
	}
	
	// method printAvaliableLetters prints a list of the currently allowed letter to input
	public void printAvailableLetters() {
		System.out.printf("%nAvailable letters:");
		int i=0;
		for (LetterSet letter : letters) {
			if (!letter.getUsedState()) {
				// letter is still available for use
				if (i % 10 == 0) {
					System.out.println(); // move to the next line after a set amount of letters
				}
				System.out.printf("%4s", letter.getLetter()); // print an available letter
				i++;
			}
		}
		// end of available letters output
		// separate from the next input
		System.out.println(); 
		System.out.println();
	}
	
	// reset the used state of every letter
	public void resetUsedState() {
		for (int i = 0; i < letters.length; i++) {
			letters[i].resetUsedState();
		}
	}
}
