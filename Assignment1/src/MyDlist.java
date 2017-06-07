import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MyDlist extends DList{
	private ArrayList<String> elementlist; // Arraylist containing the elements of the nodes in the correct order.

	/**
	 * Creates an empty doubly linked list.
	 */
	public MyDlist(){
		super();	//Create empty doubly linked list.
	}
	
	/**
	 * Creates an doubly linked list from the argument f.  
	 * @param f String specifying where the elements of the list is contained, if f= "stdin"
	 * the strings are read from the standard input. Otherwise the strings are read from the file
	 * with path f.
	 * @throws FileNotFoundException
	 */
	public MyDlist(String f) throws FileNotFoundException{
		this(); //Create empty doubly linked list by calling no-argument constructor.
		this.elementlist = read(f); //Read from file or user input.
		createList(this.elementlist); //Create the doubly linked list.
	}

	/**
	 * Prints every element in the doubly linked list in the correct order.
	 */
	public void printList(){
		for(String element : this.elementlist){ // Print every element in list.
			System.out.println(element);
		}
	}
	
	
	/**
	 * Clones and returns the doubly linked list specified by u. 
	 * @param u The doubly linked list to be cloned.
	 * @return The cloned list (deep copy).
	 */
	public static MyDlist cloneList(MyDlist u){
		ArrayList<String> elementlist = u.elementlist; // Get the elements from u.
		MyDlist copy = new MyDlist();
		copy.createList(elementlist);	// Create new list with the same elements and order as u.
		return copy;
	}
	
	
	/**
	 * Creates and returns the union of two doubly linked lists specified by u and v.
	 * @param u One of the doubly linked lists to be used in the union.
	 * @param v The other of the doubly linked lists to be used in the union.
	 * @return The union of u and v.
	 * TimeComplexity (Worst case scenario):
	 * The operations before the for loop all run in constant time independent of the size n.
	 * The for loop is executed n times.
	 * The contains method run in O(n). 
	 * The add method runs in constant time.
	 * The createlist method runs in O(n).
	 * The total TimeComplexity of the method is O(1) + O(n^2) + O(n) = O(n^2).
	 */
	public static MyDlist union(MyDlist u, MyDlist v){
		MyDlist unionList = new MyDlist();	//Initiate the new list.
		ArrayList<String> elementU = u.elementlist; //Get the elements in u.
		ArrayList<String> union = v.elementlist; //Initiate element-union as the elements in v.
		for(String element: elementU){ //Loop through all the elements of u, assumed to be larger than v. 
			if(!union.contains(element)){ //If the element-union do not contain an element from u, add it.
				union.add(element);
			}
		}
		unionList.createList(union); //Create the doubly linked list from the element-union.
		return unionList;
	}
	
	/**
	 * Creates and returns the intersection of two doubly linked lists specified by u and v.
	 * @param u One of the doubly linked lists to be used in the intersection.
	 * @param v The other of the doubly linked lists to be used in the intersection.
	 * @return The intersection of u and v.
	 * TimeComplexity (Worst case scenario):
	 * The operations before the for loop all run in constant time independent of the size n.
	 * The for loop is executed n times.
	 * The contains method run in O(n). 
	 * The add method runs in constant time.
	 * The createlist method runs in O(n).
	 * The total TimeComplexity of the method is O(1) + O(n^2) + O(n) = O(n^2).
	 */
	public static MyDlist intersection(MyDlist u, MyDlist v){
		MyDlist intersectList = new MyDlist(); //Initiate the new list.
		ArrayList<String> elementU = u.elementlist; //Get the elements in u.
		ArrayList<String> elementV = v.elementlist; //Get the elements in v.
		ArrayList<String> intersect = new ArrayList<>(); //Initiate the element-intersection.
		for(String element: elementU){ //Loop through  all the elements of u, assumed to be larger then v.
			if(elementV.contains(element)){ //If the element in u is in v, add to intersect.
				intersect.add(element);
			}
		}
		
		intersectList.createList(intersect); //Create the doubly linked list from the element-intersection.
		return intersectList;
	}
	
	
	/**
	 * Reads the strings provided from user input or from an file. Throws FileNotFoundException if file
	 * not found.
	 * @param f  String which specifies from were to read. If f = "stdin" read from system.in. 
	 * Otherwise it's assumed that f is a filepath.
	 * @return ArrayList<String> of all the strings read.
	 * @throws FileNotFoundException
	 */
	private static ArrayList<String> read(String f) throws FileNotFoundException{
		ArrayList<String> elementlist = new ArrayList<String>();
		boolean exit = false;
		Scanner sc = null;
		if(f.equals("stdin")){	// Read from user input.
			sc = new Scanner(System.in);
			while(!exit){	// While there is input do. 
				String temp = sc.nextLine();	// Read next string.
				if (temp.isEmpty()){	// If empty line.
					exit = true; // We are done.
				}
				else{
				elementlist.add(temp); // Else, Add the string to elementlist.
				}
			}
		}
		else{	// Read from file
			try {	// Try to read from file, throw exception if file not found.
				File fl = new File(f); // Create file.
			    sc = new Scanner(fl);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			while(sc.hasNext()){ // Add the strings to the elementlist.
				elementlist.add(sc.next());
			}
		}
		return elementlist;
	}
	
	/**
	 * Creates the doubly linked list from an ArrayList.
	 * TimeComplexity: O(n)
	 * @param elementlist ArrayList of strings containing the elements of the nodes.
	 */
	private void createList(ArrayList<String> elementlist) throws IllegalArgumentException{
		if (elementlist instanceof ArrayList<?>){
			this.elementlist = ((ArrayList<String>) elementlist.clone()); // Assign field elementlist.
		}
		super.addFirst(new DNode(elementlist.get(0),null,null)); // Add first node separately to avoid error.
		for(String element : elementlist){
			super.addLast(new DNode(element,null,null)); // Add the rest.
		}
	}
	
	
	
	public static void main(String[] args) throws Exception{
		 
		   System.out.println("please type some strings, one string each line and an empty line for the end of input:");
		    /** Create the first doubly linked list
		    by reading all the strings from the standard input. */
		    MyDlist firstList = new MyDlist("stdin");
		    
		   /** Print all elememts in firstList */
		    System.out.println("------------First List---------");
		    firstList.printList();
		   
		   /** Create the second doubly linked list                         
		    by reading all the strings from the file myfile that contains some strings. */
		  
		   /** Replace the argument by the full path name of the text file */  
		    MyDlist secondList=new MyDlist("/Users/Tranheden/Documents/COMP9024/Assignments/Assignment1/myfile.txt");

		   /** Print all elememts in secondList */   
		    System.out.println("------------Second List---------");
		    secondList.printList();

		   /** Clone firstList */
		    System.out.println("------------Third = clone of first---------");
		    MyDlist thirdList = cloneList(firstList);

		   /** Print all elements in thirdList. */
		    thirdList.printList();
		    System.out.println("\nShallow-copy: " + (thirdList == firstList));

		  /** Clone secondList */
		    System.out.println("------------Fourth = clone of Second---------");
		    MyDlist fourthList = cloneList(secondList);

		   /** Print all elements in fourthList. */
		    fourthList.printList();
		    System.out.println("\nShallow-copy: " + (fourthList == secondList));

		    
		   /** Compute the union of firstList and secondList */
		    System.out.println("------------Union(first, Second)---------");
		    MyDlist fifthList = union(firstList, secondList);

		   /** Print all elements in thirdList. */ 
		    fifthList.printList(); 

		   /** Compute the intersection of thirdList and fourthList */
		    System.out.println("------------Intersection(Third, Fourth)---------");
		    MyDlist sixthList = intersection(thirdList, fourthList);
		   /** Print all elements in fourthList. */
		    sixthList.printList();
		  }
}
