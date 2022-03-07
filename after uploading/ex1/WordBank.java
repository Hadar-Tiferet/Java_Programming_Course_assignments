/*
 * Author: Hadar Tiferet
 * File: WordBank.java
 */

import java.security.SecureRandom;

// WordBank contains and manages the words to be used in the game
public class WordBank {
	// random number generator
	private static final SecureRandom randomNumber = new SecureRandom();
		
	// array list containing the words in the bank
	private String[] words;
	
	// Constructor, setting up the String array words with predefined strings
	public WordBank() {
		String[] presetWords = {"White", "Black", "Blue", "Green", "Yellow", "Red",
				"Java", "File", "Class", "Method", "Import", "Constructor", "Package",
				"Private", "Public", "Protected", "Static", "Final",
				"Integer", "Float", "String", "Double", "Short", "Long",
				"Encapsulation", "Object", "Orientation", "Array"};
		this.words = presetWords;
	}
	
	// return a randomly chosen word from array words
	public String getRandomWord() {
		// set index to a random integer between 0 and the last array index in words
		int index = randomNumber.nextInt(words.length);
		// return the randomly chosen word
		return words[index];
	}
}
