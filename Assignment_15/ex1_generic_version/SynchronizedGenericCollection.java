// SynchronizedGenericCollection.java
import java.util.LinkedList;
import java.util.ArrayList;

// Class managing a collection of generic ArrayLists, allowing for thread safe removal
// and insertion of arrays in the collection
public class SynchronizedGenericCollection <C extends Comparable<C>> {
	private final LinkedList<ArrayList<C>> sharedCollection = new LinkedList<ArrayList<C>>();
	private int numberOfArrays;
	
	public SynchronizedGenericCollection(ArrayList<C> array) {
		ArrayList<C> temp; //= new ArrayList<C>();
		// populate the collection with arrays of size 1
		for (int i = 0; i < array.size(); i++) {
			temp = new ArrayList<C>();
			temp.add(array.get(i));
			sharedCollection.add(temp);
		}
		
		numberOfArrays = sharedCollection.size();
	}
	
	// allow one thread at a time to take out and remove two elements from the collection
	// return indication of true on successful removal or false if the collection contains only
	// one element, which is a sign of completion
	public synchronized boolean checkAndTakeTwoElements(LinkedList<ArrayList<C>> arraysToMerge)
			throws InterruptedException {
		// while there are no two available elements in the collection - wait
		while (sharedCollection.size() < 2) {
			// return false if the collection is sorted, to finish the task
			if (numberOfArrays == 1) {
				return false;
			}
			else {
				// wait until woken up by a task which results in the collection 
				// containing 2 or more elements
				wait();
			}
		}
		
		// two or more elements are available in the collection
		arraysToMerge.add(sharedCollection.remove());
		arraysToMerge.add(sharedCollection.remove());
		
		return true;
	}
	
	// allow one thread at a time to insert one element into the collection
	// notify the task waiting the longest time if the collection contains two
	// or more elements (and thus another merge can occur)
	// notify all the tasks waiting if only one element is left as part of the entire work load
	// which indicates merge sort completion, and thus requires tasks to run and finish
	public synchronized void insertElement(ArrayList<C> merged) {
		// insert the array into the collection and update the number of arrays in total
		sharedCollection.add(merged);
		--numberOfArrays;
		
		if (sharedCollection.size() > 1) {
			// at least one task can take two elements
			// wake up one task, since if threads have been put to sleep
			// then most of the elements in the collection are busy in other tasks
			// and waking up every sleeping task would lead to potentially many
			// tasks taking the lock just to wait on the lock again, wasting processing time
			notify();
		}
		else if (numberOfArrays == 1) {
			// the collection is sorted, wake up any sleeping task to clean up
			notifyAll();
		}
	}
	
	// return the first array within the collection
	public ArrayList<C> getFirstArray() {
		return sharedCollection.getFirst();
	}
}
