// Merger.java
import java.util.LinkedList;

// merger with a run method that merges two sorted integer arrays into one
public class Merger implements Runnable {
	// collection shared between all concurrent tasks
	private final SynchronizedCollection synchronizedCollection;
	// list containing elements to merge in the current merge sort iteration
	private final LinkedList<Integer[]> toMerge;
	// array containing the result of the current merge sort iteration
	private Integer[] resultArray;
	// indexes to be used by the merge sort algorithm
	private int firstIndex;
	private int secondIndex;
	private int resultIndex;
	
	// constructor
	public Merger(SynchronizedCollection synchronizedCollection) {
		this.synchronizedCollection = synchronizedCollection;
		toMerge = new LinkedList<Integer[]>();
		// any other instance variable is initialized to the default value and
		// will be changed on every loop
	}
	
	// removes two sorted arrays from the shared collection and insert
	// a single array of the sorted contents of both arrays
	@Override
	public void run() {
		try {
			// while the collection has more than one arrays in total:
			// available to be taken and sorted or occupied in other tasks,
			// take and remove two arrays from the collection
			while (synchronizedCollection.checkAndTakeTwoElements(toMerge)) {
				// initialize the result array to hold all the elements of both arrays
				resultArray = new Integer[toMerge.getFirst().length + toMerge.getLast().length];
				
				// initialize array traversal indexes
				firstIndex = 0;
				secondIndex = 0;
				resultIndex = 0;
				
				// perform merge sort algorithm
				while (firstIndex < toMerge.getFirst().length && secondIndex < toMerge.getLast().length) {
					// both input arrays still contain elements
					// insert the smallest element from both arrays into the result array
					// increment the index of the result array and the array containing the smaller element
					if (toMerge.getFirst()[firstIndex] <= toMerge.getLast()[secondIndex]) {
						resultArray[resultIndex++] = toMerge.getFirst()[firstIndex++];
					}
					else {
						resultArray[resultIndex++] = toMerge.getLast()[secondIndex++];
					}
				}
				
				// while only the first array contains additional elements
				while (firstIndex < toMerge.getFirst().length) {
					resultArray[resultIndex++] = toMerge.getFirst()[firstIndex++];
				}
				
				// while only the second array contains additional elements
				while (secondIndex < toMerge.getLast().length) {
					resultArray[resultIndex++] = toMerge.getLast()[secondIndex++];
				}
				
				// clear the linked list for future use
				toMerge.clear();
				// insert the sorted array back into the collection
				synchronizedCollection.insertElement(resultArray);
			}
		}
		catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
		}
	}
}
