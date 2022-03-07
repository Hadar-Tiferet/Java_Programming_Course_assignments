// GenericMerger.java
import java.util.LinkedList;
import java.util.ArrayList;

// merger with a run method that merges two sorted generic ArrayLists into one
public class GenericMerger <C extends Comparable<C>> implements Runnable {
	// generic collection shared between all concurrent tasks
	private final SynchronizedGenericCollection<C> synchronizedCollection;
	// generic list containing elements to merge in the current merge sort iteration
	private final LinkedList<ArrayList<C>> toMerge;
	// generic ArrayList containing the result of the current merge sort iteration
	private ArrayList<C> resultArray;
	
	// constructor
	public GenericMerger(SynchronizedGenericCollection<C> synchronizedCollection) {
		this.synchronizedCollection = synchronizedCollection;
		toMerge = new LinkedList<ArrayList<C>>();
		resultArray = new ArrayList<C>();
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
				
				// perform merge sort algorithm
				resultArray = GenericMergeSort(toMerge.getFirst(), toMerge.getLast());
				
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
	
	private ArrayList<C> GenericMergeSort(ArrayList<C> firstArray, ArrayList<C> secondArray) {
		ArrayList<C> result = new ArrayList<C>();
		// initialize arrayList traversal indexes
		int firstIndex = 0;
		int secondIndex = 0;
		
		// perform merge sort algorithm
		while (firstIndex < firstArray.size() && secondIndex < secondArray.size()) {
			// both input arrays still contain elements
			// insert the smallest element from both arrays into the result array
			// increment the index of the result array and the array containing the smaller element
			if (firstArray.get(firstIndex).compareTo(secondArray.get(secondIndex)) <= 0) {
				result.add(firstArray.get(firstIndex++));
			}
			else {
				result.add(secondArray.get(secondIndex++));
			}
		}
		
		// while only the first array contains additional elements
		while (firstIndex < firstArray.size()) {
			result.add(firstArray.get(firstIndex++));
		}
		
		// while only the second array contains additional elements
		while (secondIndex < secondArray.size()) {
			result.add(secondArray.get(secondIndex++));
		}
		
		return result;
	}
}
