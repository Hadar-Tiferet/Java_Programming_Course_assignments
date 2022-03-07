// ConcurrentMergeSort.java
// main class for an application showcasing a merge sort algorithm on integer values done concurrently
// imports for concurrent program control
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
// import for user input
import java.util.Scanner;

import java.security.SecureRandom;


public class ConcurrentMergeSort {
	public static void main(String[] args) throws InterruptedException {
		Integer[] intArray;
		// values serve as default to prevent usage of empty or negative size values
		// number of integers to store in the array
		int arraySize = 50;
		// number of tasks to create
		int numberOfTasks = 10;
		
		// add a new scanner to get input from the user
		Scanner in = new Scanner(System.in);
		
		System.out.printf("%s%n%s%n", "Please enter a non zero positive integer",
				"for the number of values in the array");
		arraySize = getPositiveInt(in, arraySize);
		
		System.out.printf("%s%n%s%n", "Please enter a non zero positive integer",
				"for the number of tasks to execute the merge sort concurrently");
		numberOfTasks = getPositiveInt(in, numberOfTasks);
		
		// close the scanner
		in.close();
		
		intArray = getRandomizedArray(arraySize, 100);
		// print the array before sorting
		printArray(intArray, "Array before merge sort");
		
		// initialize the collection to be used by multiple tasks concurrently
		SynchronizedCollection sharedCollection = new SynchronizedCollection(intArray);
		
		// create a new fixed thread pool
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfTasks);
		
		// create m tasks of class merger 
		for (int i = 0; i < numberOfTasks; i++) {
			executorService.execute(new Merger(sharedCollection));
		}
		
		// prevent addition of new threads
		executorService.shutdown();
		// wait for tasks to complete before executing the reminder of the main thread
		// with a time limit of 5 minutes for extreme cases
		if (executorService.awaitTermination(5, TimeUnit.MINUTES)) {
			// calculations completed successfully, print results
			intArray = sharedCollection.getFirstArray();
			printArray(intArray, "Array after merge sort");
		}
		else {
			// calculations are taking too long, terminate program
			System.out.println("Program took too long, shutting down");
			System.exit(1);
		}
		
		
	}
	
	// create an array of random integers of size n
	private static Integer[] getRandomizedArray(int size, int maxValue) {
		SecureRandom generator = new SecureRandom();
		Integer[] array = new Integer[size];
		
		for (int i = 0; i < size; i++) {
			array[i] = generator.nextInt(maxValue) + 1;
		}
		
		return array;
	}
	
	// print a given Integer array to the standard output
	private static void printArray(Integer[] array, String title) {
		System.out.printf("%n%s", title);
		
		for (int i = 0; i < array.length; i++) {
			if (i % 15 == 0) {
				// move to a new line
				System.out.println();
			}
			System.out.printf("%3d ", array[i]);
		}
		
		System.out.println();
	}
	
	// get a non zero positive integer from user input
	private static int getPositiveInt(Scanner in, int defaultValue) {
		while (in.hasNext()) {
			// input is still given
			if (in.hasNextInt()) {
				// input contains an integer
				int value = in.nextInt();
				
				if (value > 0) {
					// input is a valid positive integer
					return value;
				}
				else {
					// line contains an integer that does not fit the criteria
					System.out.printf("Input must be an integer larger than zero%n");
				}
			}
			else {
				// line does not contain an integer, move to the next one
				if (!in.nextLine().isEmpty()) {
					System.out.printf("Input must be an integer%n");
				}
			}
		}
		// end of file reached - use the default value instead
		System.out.printf("No input given.%nUsing default value of: %d instead%n", defaultValue);
		return defaultValue;
	}
}