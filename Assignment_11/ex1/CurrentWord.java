/*
 * Author: Hadar Tiferet
 * File: CurrentWord.java
 */

// CurrentWord handles the execution and presentation of the current chosen word within the guessing game
public class CurrentWord {
	private LegalInput inputControl; // object of the LegalInput class, holding information about the available letters and valid inputs
	private String[] revealedWord; // array containing each letter of the word
	private boolean[] isRevealed; // array containing the state of each letter in the word
	private String concealedLetter; // string to output in the place of a hidden letter
	private int guesses; // the amount of legal guesses the user had so far
	private int correctGuesses; // the amount of correct guesses the user had so far
	
	// empty constructor, relevant information is updated upon the input of a new word
	public CurrentWord() {
		inputControl = new LegalInput();
		concealedLetter = "_";
	}
	
	// setCurrentWord sets up the class object to contain a new word to guess
	public void setCurrentWord(String word) {
		int wordLength = word.length();
		revealedWord = new String[wordLength];
		isRevealed = new boolean[wordLength];
		
		for (int i=0; i < wordLength; i++) {
			revealedWord[i] = String.valueOf(word.charAt(i));
			isRevealed[i] = false;
		}
		guesses = 0;
		correctGuesses = 0;
		inputControl.resetUsedState();
		
		printCurrentWord();
		inputControl.printAvailableLetters();
	}
	
	// processNewInput receives input and checks it against the chosen word, returning feedback to the user
	public boolean processNewInput(String input) {
		if (inputControl.checkInput(input)) {
			// input is a valid letter
			guesses++;
			for (int i = 0; i < revealedWord.length; i++) {
				if (input.equalsIgnoreCase(revealedWord[i])) {
					// input is a letter from the chosen word
					isRevealed[i] = true;
					correctGuesses++;
				}
			}
		}
		
		printCurrentWord(); // print the current word after this guess
		
		// check if the entire word was revealed
		if (correctGuesses >= revealedWord.length) {
			// current word is revealed completely
			System.out.printf("Congratulations, you managed to reveal the entire word in %d guesses.%n%n", guesses);
			return true;
		}
		else {
			// the word is not yet revealed
			inputControl.printAvailableLetters(); // print the available letters for the next guess
			return false;
		}
	}
	
	// printCurrentWord prints the chosen word while revealing only the correctly guessed letters so far
	public void printCurrentWord() {
		System.out.println("The current word is:");
		
		for (int i = 0; i < revealedWord.length; i++) {
			// if the current letter in the chosen word was already discovered - show it, otherwise keep it hidden
			System.out.printf("%4s", (isRevealed[i] ? revealedWord[i] : concealedLetter));
		}
		System.out.println();
	}
	
}
