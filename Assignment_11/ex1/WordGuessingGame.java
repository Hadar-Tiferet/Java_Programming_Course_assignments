/*
 * Author: Hadar Tiferet
 * File: WordGuessingGame.java
 */

import java.util.Scanner;

public class WordGuessingGame {
	private static boolean activeWord = false; // flag, true if a word is currently being guessed
	private static final String NEW_WORD = ".n"; // user input for setting up a new word to guess
	private static final String QUIT_GAME = ".q"; // user input for quitting the game and terminating the program
	
	// main method, running the program
	public static void main(String[] args) {
		// add a new scanner to get input from the user
		Scanner in = new Scanner(System.in);
		System.out.printf("%nWelcome to The word guessing game%n");
		inactiveWordInputMessage();
		
		String input; // string containing the input currently being processed

		// object containing a list of the available words to guess and a method to get a random word from the list
		WordBank wordBank = new WordBank();
		// only one object of class CurrentWord is created to maintain the current word the user tries to guess
		// to prevent waiting for garbage collection to clear dereferenced objects after every word,
		// the same object is being updated with a new word with every NEW_WORD request
		CurrentWord wordToGuess = new CurrentWord();
		
		// while loop running as long as input is available and user did not request to quit
		while (in.hasNextLine()) {
			// get the new line of input, trim the blank spaces
			input = in.nextLine().trim();
			
			if (input.equals(QUIT_GAME)) {
				//user requested program termination
				break; // exit the while loop to end the program
			}
			else if (input.equals(NEW_WORD)) {
				// set the new word to guess as a random word from object wordBank
				wordToGuess.setCurrentWord(wordBank.getRandomWord());
				activeWord = true; // flag that the current word is not finished
				activeWordInputMessage();
			}
			else if (activeWord) {
				// current word is not done, process the current input according to the current word
				if (wordToGuess.processNewInput(input)) {
					// flag that the current word is finished and the user has to request a new one
					activeWord = false;
					inactiveWordInputMessage();
				}
				else {
					// current word is not fully revealed, request input accordingly
					activeWordInputMessage();
				}
			}
			else {
				// unrecognized input with no active word
				unrecognizedInputMessage();
			}
		}
		in.close(); // close the input stream
	}
	
	// methods for specific output cases
	public static void unrecognizedInputMessage() {
		System.out.printf("Unrecognized input, please enter: %-4sto get a new word or: %-4sto quit.%n",NEW_WORD, QUIT_GAME);
	}
	
	public static void inactiveWordInputMessage() {
		System.out.printf("Please enter: %-4sto get a new word or: %-4sto quit.%n",NEW_WORD, QUIT_GAME);
	}
	
	public static void activeWordInputMessage() {
		System.out.printf("Please enter a valid letter as a guess, %-4sto get a new word or: %-4sto quit.%n",NEW_WORD, QUIT_GAME);
	}
}
