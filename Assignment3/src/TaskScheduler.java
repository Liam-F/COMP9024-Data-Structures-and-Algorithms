import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import javax.management.InvalidAttributeValueException;
import net.datastructures.ArrayListCompleteBinaryTree;
import net.datastructures.EmptyPriorityQueueException;
import net.datastructures.Position;

/**
 * @author Ludwig Tranheden 5129849.
 *
 */
public class TaskScheduler {
	/**
	 * Constructs from file1 a feasible schedule for the task set on a processor with m identical
	 * cores by using the EDF strategy and write the feasible schedule to file2.
	 * 			
	 * Time-complexity:
	 * Reading all the tasks from the file takes linear time. 
	 * The insertion into the first queue is O(log(n)) time and n insertions will be made. 
	 * The removal from the first queue is O(log(n)) time and n removals will be made.
	 * The insertion into the second queue is O(log(n)) time and n insertions will be made.
	 * The removal from the second queue is O(log(n)) time and n removals will be made.
	 * Creating and writing the output to the file takes linear time.
	 * Hence the worst case time-complexity is O(n*log(n)).
	 */
	static void scheduler(String file1, String file2, int m) throws InvalidAttributeValueException, FileNotFoundException {
		MyHeapPriorityQueue<Integer, Task> que1 = new MyHeapPriorityQueue<Integer, Task>(); //Priority heap queue for the release-times.
		MyHeapPriorityQueue<Integer, Task> que2 = new MyHeapPriorityQueue<Integer, Task>(); //Priority heap queue for the deadline-times.
		File read = new File(file1); //Read from file1.
		Scanner sc;
		Task task;
		try { //Try to read from file. Otherwise throw FileNotFoundException.
			sc = new Scanner(read);
			String name;
			int deadlinet, releaset;
			while (sc.hasNext()) { //While there is input.

				if (!sc.hasNextInt()) {    //If the next token is not an Int, it's the task name. Otherwise throw InvalidAttributeValueException.
					name = sc.next(); 

					if (sc.hasNextInt()) {      //If the next token is an Int, it's the release-time. Otherwise throw InvalidAttributeValueException.
						releaset = sc.nextInt();

						if (!sc.hasNextInt()) { //If the next token is not an Int throw InvalidAttributeValueException. Otherwise it's
							sc.close(); 		//the deadline-time.
							throw new InvalidAttributeValueException("Input error when reading the attributes of the task " + "''" + name + "''");
						} 
						else {   
							deadlinet = sc.nextInt();
							task = new Task(name, releaset, deadlinet);
						}
					} 
					else {     
						sc.close();
						throw new InvalidAttributeValueException("Input error when reading the attributes of the task " + "''" + name + "''");

					}
					
				} 
				else {
					String temp = sc.next();
					sc.close();
					throw new InvalidAttributeValueException("Input error when reading the attributes of the task " + "''" + temp + "''");
				}
				que1.insertTask(task.releaset(), task); //Insert the registered Task to the priority queue sorted by the release-time (The key).

			} 
			sc.close();  // Close scanner

		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(file1 + " does not exist.");
		}

		int time = 0; //Initiate the time.
		String output = ""; //String to store the output.
		String entry = "";
		int coresused = 0; //Initiate the number of cores currently used.
		while(!que1.isEmpty()){ //While there still is tasks.
			while(!que1.isEmpty() && que1.minEntry().releaset() == time){ //While there still is tasks and the release-time corresponds to the current time.
				que2.insertTask(que1.minEntry().deadlinet(),que1.removeMinTask()); //Insert into the dynamic deadline sorted by the deadline-times (The key). 	
			}

			coresused = 0; //The number of cores currently used.
			while(!que2.isEmpty() && coresused < m){ //While there still is tasks that have been released and there is cores available.
				if(time >= que2.minEntry().deadlinet()){ //If the time has exceeded the deadline, there is no feasible schedule.
					System.out.println("No feasible schedule exists.");
					System.exit(0);
				}
				entry = que2.removeMinTask().task() + " " + Integer.toString(time) + " "; 
				output = output.concat(entry); //Add the task and the start time to the output.
				coresused++; //Using one more core. 
			}
			time++; //Next time step.
		}
		BufferedWriter bufferedWriter = null; 
		try {	//Try to write to file. Otherwise throw exception.
			File myFile = new File(file2 + ".txt"); 
			if (!myFile.exists()) { //If file does not exists, create it. Otherwise the existing file will be overwritten.
				myFile.createNewFile();
			}
			Writer writer = new FileWriter(myFile);
			bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(output); //Write output to file.
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try{
				if(bufferedWriter != null) bufferedWriter.close(); //Try to close BufferedWriter. Otherwise throw exception.
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
}




/**
 * Helper class for representing the tasks.
 */
class Task {
	private String task;
	private Integer releaset;
	private Integer deadlinet;

	// Constructor.
	public Task(String task, Integer releaset, Integer deadlinet) { 
		this.task = task;
		this.releaset= releaset;
		this.deadlinet = deadlinet;
	}

	// Get methods.
	public String task() {
		return task;
	}
	public Integer releaset() {
		return releaset;
	}

	public Integer deadlinet() {
		return deadlinet;
	}
}

/**
 * The heap priority queue to store the tasks and the keys.
 */
class MyHeapPriorityQueue<key, Task> {
	/**
	 * nested static class to represent the entries.
	 */
	static class  MyEntry<key,Task>{
		protected int key;
		protected Task task;
		
		// Constructor.
		public MyEntry(int k, Task task) { 
			this.key = k; this.task = task; 
		}
		//Get methods.
		public Integer getKey() { 
			return key; 
		}
		public Task getTask() { 
			return task; 
		}
	}

	//Binary tree by means of an arraylist to represent the heap.
	protected ArrayListCompleteBinaryTree<MyEntry<key,Task>> heap;
	
	/**
	 * Constructor
	 */
	public MyHeapPriorityQueue(){
		heap = new ArrayListCompleteBinaryTree<MyEntry<key,Task>>(); //Initiate heap. 
	}
	
	/**
	 * Size of heap
	 * Time-complexity: O(1)
	 */
	public int size() { 
		return heap.size(); 
	} 
	
	/**
	 * Checks if heap is empty.
	 * Time-complexity: O(1)
	 */
	public boolean isEmpty() { 
		return heap.size() == 0; 
	}
	
	/**
	 * Returns the Task with the smallest key.
	 * Time-complexity: O(1)
	 */
	public Task minEntry() throws EmptyPriorityQueueException {
		if (isEmpty()) 
			throw new EmptyPriorityQueueException("Queue empty");
		return heap.root().element().getTask(); //Smallest key contained at the root.
	}
	
	/**
	 * Insertion-method.
	 * Time-complexity: O(log(n))
	 */
	public MyEntry<key,Task> insertTask(int k, Task task) {
		MyEntry<key,Task> entry = new MyEntry<key,Task>(k,task); //Create new entry from specified task and key.
		Position<MyEntry<key,Task>> pos = heap.add(entry); //Add entry at the end of the heap.
		upHeap(pos); //Restore heap properties through upheap.
		return entry;
	}
	
	/**
	 * Removes and returns the Task with the smallest key.
	 * Time-complexity: O(log(n))
	 */
	public Task removeMinTask() throws EmptyPriorityQueueException {
		if (isEmpty()) 
			throw new EmptyPriorityQueueException("Priority queue is empty");
		Task min = heap.root().element().getTask(); //Smallest key contained at the root.
		if (size() == 1) //If root the only entry, just remove entry.
			heap.remove();
		else { 
			heap.replace(heap.root(), heap.remove()); //Otherwise replace the root with the last entry.
			downHeap(heap.root()); //Restore heap properties through downHeap.
		}
		return min;
	}
	
	/**
	 * UpHeaping method.
	 * Time-complexity:
	 * Performs a constant time operations at every level of the heap. Worst case scenario the method goes all the way to the root.
	 * The height is of a heap is O(log(n)) and hence the time complexity is O(log(n)).
	 */
	protected void upHeap(Position<MyEntry<key,Task>> t) {
		Position<MyEntry<key,Task>> u;
		boolean done = false;
		while (!heap.isRoot(t) && !done) { //While not at root or encountered a node with a key smaller than k.
			u = heap.parent(t); //Go upwards.
			if(u.element().getKey() < t.element().getKey()){done = true;}
			else{
			MyEntry<key,Task> temp = u.element(); 
			heap.replace(u, t.element()); //Swap.
			heap.replace(t, temp);
			t = u;
			}
		}
	}
	
	/**
	 * DownHeaping method.
	 * Time-complexity:
	 * Performs a constant time operations at every level of the heap. Worst case scenario the method goes all the way to the end-node.
	 * The height is of a heap is O(log(n)) and hence the time complexity is O(log(n)).	 
	 */
	protected void downHeap(Position<MyEntry<key,Task>> t) {
		Position<MyEntry<key,Task>> u;	
		boolean done = false;
		while (heap.isInternal(t) && !done) { //While not at external node or encountered a node with a key smaller than k.
			if (!heap.hasRight(t)) //If t does not have right child, move left.
				u = heap.left(t);
			else if (heap.left(t).element().getKey() <= heap.right(t).element().getKey()) //else move to the smallest child (or left if tie).
				u = heap.left(t);
			else
				u = heap.right(t);
			if (u.element().getKey() < t.element().getKey()) { //If key still larger.
				MyEntry<key,Task> temp = t.element(); //Swap.
				heap.replace(t, u.element());
				heap.replace(u, temp);
				t = u;
			}
			else  //Else we are done.
				done = true;
		}
	}
}

