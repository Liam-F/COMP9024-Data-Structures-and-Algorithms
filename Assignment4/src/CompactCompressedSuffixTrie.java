import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;


public class CompactCompressedSuffixTrie {
	private Node root; // The root of the compressed suffix tree.
	private static final int LETTERS = 5; // Number of letters in the DNA alphabet + special character.
	private static final Character UN = '$'; // Special character to denote suffix. 
	private static final Character DEL = '#'; // Special character 1 to simplify code.
	private static final Character DEL2 = '€'; // Special character 2 to simplify code. 
	private Node active; // Active Node, where we start from.
	private int actEdge; // Active Edge, to choose edge from Active Node.
	private int actLen; // Active Length, how far we go on Active Edge.
	private End end; // Global end.
	private int remS; // Remaining suffixes.
	private char[] s; // The char array of string stored in Suffix-trie.
	private String seq; // The String stored in Suffix-trie.

	/**
	 * Creates a compact representation of the compressed suffix trie from an input text file f that stores a DNA sequence using Ukkonen's algorithm.
	 * Also use Edge-label compression.
	 * 
	 * References: http://web.stanford.edu/~mjkay/gusfield.pdf 
	 * 			   http://www.geeksforgeeks.org/ukkonens-suffix-tree-construction-part-6/
	 * 
	 * Rules:
	 * Let B = S[j...i] be a suffix of S[1...i].
	 * Rule 1: In phase i+1 if B ends at a leaf then add S[i+1] at 
	 * the end.
	 * Rule 2: In phase i+1 if B ends in the middle of an edge and the next character is
	 * not S[i+1] -> a new leaf edge starting from the end of B with label S[i+1] is created.
	 * Rule 3: In phase i+1 if B ends in the middle of an edge and next character is
	 * S[i+1] then do nothing since BS[i+1] already in trie.
	 * 
	 * To speed up, 3 tricks are used:
	 * Trick 1: When walking down from node s(v) to leaf we directly skip to the end of the edge 
	 * if the number of characters on the edge is less than the number of characters we need to traverse.
	 * If number of characters on the edge is more than the number of characters 
	 * we need to travel, we directly skip to the last character on that edge.
	 * Trick 2: Stop phase as soon as rule 3 applies since all further extensions are already present in tree implicitly.
	 * Trick 3: Use a global end.
	 *  
	 * @throws FileNotFoundException
	 * 
	 * Time complexity analysis:
	 * The reading from the file and conversions takes linear time.
	 * 
	 * We automatically update all leaves using the global end, so it takes constant time.
	 * 
	 * remS states how many additional inserts that has to be made, it's bounded by the global end (maximum number of suffixes left).
	 * 
	 * Each insert takes constant time since the active variables tells us where to insert and the number characters in the DNA-alphabet is constant.
	 * 
	 * After insertions remS is decremented and we follow the chain suffix links or go/stay to root 
	 * (If we are at root already, we modify the active point) so it takes constant time.
	 * As we follow down the chain of suffix links to make the remaining inserts, 
	 * active_length can only decrease, and the number of adjustments can't be larger than active_length. 
	 * Since active_length<=remS, the total sum of increments ever made to remainder for of the entire process is O(n) 
	 * and so the number of active point adjustments is also bounded by O(n).
	 *
	 * The indexing of the trie takes linear time.
	 * 
	 * So the total worst case time-complexity of the constructor is O(n) where n is the size of the input DNA sequence.
	 */
	public CompactCompressedSuffixTrie(String f) throws FileNotFoundException{
		root = new Node(1, new End(0)); // Create root
		remS = 0;	// Initiate fields.
		actEdge = -1;
		actLen = 0;
		root.index = -1;

		// Read from file.
		try{
			File read = new File(f);
			Scanner sc = new Scanner(read);
			StringBuilder builder = new StringBuilder();
			while(sc.hasNext()){
				builder.append(sc.next());
			}
			sc.close();
			builder.append(UN); //Add special symbol to end.
			char input[] = new char[builder.length()]; 
			for (int j=0;j < builder.length();j++){ //Convert to char array.
				input[j] = builder.charAt(j);
			}
			this.s = input;
			seq = new String(s); //Convert to String.
			active = root; //Active node is root.
			this.end = new End(-1); //Global end is -1.

			for (int i =0; i < builder.length();i++){ //Loop through all phases.
				Node lastcreated = null; //Last created internal node is null.
				this.end.end++; //Rule 1 through trick 3.
				remS++; //The number of suffixes that needs to be created.

				while(remS > 0){ //While there are suffixes left.
					if(actLen == 0){ //If active length is 0, look for character from root.
						if(active.child[convert(input[i])] != null){ //If there is a matching character from root.
							actEdge = active.child[convert(input[i])].start; //Active edge is that start index.
							actLen++; //Increase active length.
							break; // End phase by rule 3.
						}
						else{ // If no match.
							root.child[convert(input[i])] = new Node(i, end); //Create leaf node.
							remS--; // Decrement remaining suffixes.
						}
					}
					else{ //Active length not 0, so we are somewhere in the middle.
						char c = DEL; //Initiate c to special character.
						do{ // Find next character after the active point.
							Node node = active.child[convert(input[actEdge])]; //First point on active edge.
							if((node.end.end - node.start) >= actLen){ //If true, traverse to point after active point.
								c = input[(active.child[convert(input[actEdge])]).start + actLen]; //character at next point.
							}
							else if((node.end.end - node.start) + 1 == actLen){ // Else if at end of edge.
								c = DEL2; // Set c equal to special character.
								if(node.child[convert(input[i])] != null){ // If there is a child with input character.
									c = input[i]; // set c equal to the input character.
								}
							}
							else{ // Else keep traversing
								active = node;
								actLen = actLen - (node.end.end - node.start)  -1;
								actEdge = actEdge + (node.end.end - node.start)  +1;
							}
						}while(c == DEL && c != DEL2);
						if(c == DEL2){ //There was not a child with input character.
							Node node = active.child[convert(input[actEdge])]; //Get node.
							node.child[convert(input[i])] = new Node(i, end); //Create child with input character.
							if (lastcreated != null) { //If last created external node is not null.
								lastcreated.link = node; //Link last created external node to new node.
							}
							lastcreated = node; //Last created external node is now the new node.
							if(active != root){ //If the active node is not the root.
								active = active.link; //Update active.
							}
							else{ //Else
								actEdge++; //Increase active edge. 
								actLen--; //Decrease active length.
							}
							remS--; //Decrease remaining suffixes.
						}
						else{
							if(c == input[i]){ //There was a point with input character.
								if(lastcreated != null){ //Last external node is not null.
									lastcreated.link = active.child[convert(input[actEdge])]; //Link to input character node.
								}
								//Move to new point.
								Node node = active.child[convert(input[actEdge])]; //Input character edge.
								if((node.end.end - node.start) < actLen){ //If active length is greater then path edge length.
									active = node; //Change active point to new edge.
									actLen = actLen - (node.end.end - node.start); //New active length.
									actEdge = node.child[convert(input[i])].start; //New active edge.
								}else{
									actLen++; //Else just increment active length.
								}
								break; //End of phase. 
							}
							else{ //Next character on the edge does not match the input character.
								Node node = active.child[convert(input[actEdge])]; //Make to leaf node.
								int oldStart = node.start; 
								node.start = node.start + actLen; 
								Node newInternalNode = new Node(oldStart, new End(oldStart + actLen - 1)); //New internal node.
								Node newLeafNode = new Node(i, this.end); //New leaf node.

								//Set internal nodes child as old node and new leaf node.
								newInternalNode.child[convert(input[newInternalNode.start + actLen])] = node; 
								newInternalNode.child[convert(input[i])] = newLeafNode;
								newInternalNode.index = -1;
								active.child[convert(input[newInternalNode.start])] = newInternalNode;

								// Link if another internal node was created in this phase.
								if (lastcreated != null) {
									lastcreated.link = newInternalNode;
								}
								// Update lastcreated internal node.
								lastcreated = newInternalNode;
								// Link to root.
								newInternalNode.link = root;

								// If active not root, follow suffix link.
								if(active != root){
									active = active.link;
								}
								// Else active node is root, increase active edge and decrease length.
								else{
									actEdge++;
									actLen--;
								}
								remS--; //Decrease remaining suffixes.
							}
						}
					}
				}
			}
		}

		catch(FileNotFoundException e){ //File not found, throw exception.
			throw new FileNotFoundException(f + "does not exist.");
		}
		setindex(this.root, s.length,0); //Set the indexes of the trie.


	}

	/**
	 * Returns the index of the first occurrence of a pattern s in the DNA sequence represented by the trie, otherwise returns -1.
	 * length(s) = n
	 * Since i increases by one every time we move, a constant number of operations is done between each move and the method
	 * terminates at a match or no match the worst case time complexity is:
	 * Time-complexity: O(n) where n is the length of the pattern.  
	 */
	public int findString( String s ){
		Node p = root;
		boolean found = false;
		int i = 0;
		int k = 0;
		int l = -1;
		while(i < s.length()){ 
			char c = s.charAt(i); // Character to find.
			p = findchar(p,0,c); //Find direction to move in.
			if(p == null){return -1;} //If no match, no occurrence of string.
			l = p.start; //start edge-index for search. 
			i++; //increment i.
			while(p.start+k < p.end.end && i < s.length()){ //while not at the end of edge and i < n
				c = s.charAt(i); // Go to next character if s.
				k = k+1; //increment k
				if(c != this.s[p.start+k]){return -1;} //If c not equal to next character on edge, no match.
				i++; //Increment i
			}
			int g = k;
			if(i == s.length()){found = true;g=0;} //We found the pattern, else move to new edge.
			k = k - g; 
		}
		if(found){
			return l+k-i+1; //Index of first occurrence.
		}
		return -1; // No match.

	}

	/**
	 * Returns the child of p (edge) that that contains character c and otherwise null.
	 * Since the number of children are bounded by the size of the DNA-alphabet worst case
	 * Time complexity:  O(1)
	 */
	private Node findchar(Node p,int l, char c) {
		for(Node n: p.child){ //For each child of p do.
			if(n != null){ // If not null
				if(c == this.s[n.start+l]){return n;} //If we found a match, return that child.
			}

		}
		return null; // No match
	}

	/**
	 * Computes the longest common subsequence of the two DNA sequences stored 
	 * in the text files f1 and f2, respectively, and writing it to the file f3.
	 * 
	 * Return the degree of similarity of the two DNA sequences stored in the text files f1 and f2
	 * 
	 * Reading and writing from files takes linear time.
	 * Solving all the subproblems we loop size(f1.DNA)*size(f2.DNA) = n*m times.
	 * Recover the subsequence takes worst case O(n+m).
	 * 
	 * In total the worst case
	 * Time-complexity is: O(nm)
	 */
	public static float similarityAnalyser(String f1, String f2, String f3) throws FileNotFoundException{
		Scanner sc1;
		Scanner sc2;
		String check;
		try{ 
			File read1 = new File(f1);
			sc1 = new Scanner(read1);
		}
		catch(FileNotFoundException e){
			throw new FileNotFoundException(f1 + "does not exist.");
		}
		try{ 
			File read2 = new File(f2);
			sc2 = new Scanner(read2);
		}
		catch(FileNotFoundException e){
			sc1.close();
			throw new FileNotFoundException(f2 + "does not exist.");
		}
		StringBuilder builder1 = new StringBuilder();
		StringBuilder builder2 = new StringBuilder();
		while(sc1.hasNext()){ //Read from file1
			check = sc1.next();
			builder1.append(check);
		}
		while(sc2.hasNext()){ //Read from file 2
			check = sc2.next();
			builder2.append(check);		
		}
		sc1.close();
		sc2.close();
		int M = builder1.length();
		int N = builder2.length();
		//Dynamic programming solution.
		int[][] L = new int[M + 1][N + 1]; //Initiate L to 0, the longest common subsequence array.

		//Solve all subproblems.
		for (int i = M - 1; i >= 0; i--) {
			for (int j = N - 1; j >= 0; j--) {
				if (builder1.charAt(i) == builder2.charAt(j)){ //If match.
					L[i][j] = L[i + 1][j + 1] + 1; //Increase current longest common subsequence by 1.
				}
				else{
					L[i][j] = Math.max(L[i + 1][j], L[i][j + 1]); //Else - max of previous two possibilities.
				}
			}
		}

		//Recover subsequence. 
		String ret = "";
		int i = 0, j = 0;
		while (i < M && j < N) {
			if (builder1.charAt(i) == builder2.charAt(j)) { //If match, add and increase both indexes.
				ret += builder1.charAt(i);
				i++;
				j++;
			} 
			else if (L[i + 1][j] >= L[i][j + 1]) { //No match and longest common subsequence of S1[0,i+1]&S2[0,j] >= S1[0,i]&S2[0,j+1]
				i++; //Increase i
			} 
			else {
				j++; //Otherwise increase j.
			}
		}

		BufferedWriter bufferedWriter = null; 
		try {	//Try to write to file. Otherwise throw exception.
			File myFile = new File(f3); 
			if (!myFile.exists()) { //If file does not exists, create it. Otherwise the existing file will be overwritten.
				myFile.createNewFile();
			}
			Writer writer = new FileWriter(myFile);
			bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(ret); //Write output to file.
		} catch (IOException e) {
			e.printStackTrace();
		} 

		if (builder1.length()==0 || builder2.length()==0) { //Empty strings, LCS is 0. 
			return (float) 0.0;
		}
		return (float) ret.length() / Math.max(builder1.length(),builder2.length()); //Otherwise return The degree of similarity |LCS(S1,S2)|/max{|S1|,|S2|}
	}


	/**
	 * Sets the indexes of current trie recursively starting with Node el.
	 * Size is the length of the string whos suffixes is represented by the trie.
	 * The compact suffix trie has at most size = n leaves (can have at most n suffixes). Every inner node must eventually lead to a suffix
	 * and hence if there is K inner nodes there must be at least K+1 leaves. The leaves are bounded by n so the internal nodes is bounded by n-2.
	 * Hence the maximum number of nodes is n - 2+ n + 1 = 2n-1 (The 1 comes from the root.)
	 * 
	 * Since each node is visited one time and a constant number of operations is done the time-complexity is O(n) where n is the size of the string the trie represents.    
	 */
	public void setindex(Node el, int size, int value){
		if(el == null){ //If null return.
			return;
		}

		value += el.end.end - el.start + 1; // Length of character sequence. 
		if(el.index != -1){ //If leaf.
			el.index = size - value; //Start index relative to array is size of string minus length of character sequence.
			return;
		}

		for(Node node : el.child){ //Recursively do it for all children.
			setindex(node, size, value);
		}
	}

	/**
	 *Helper function to convert char in the DNA alphabet + the special character to indixes.
	 */
	public static int convert(char c){
		switch(c){
		case 'A': return 0;
		case 'C': return 1;
		case 'G': return 2;
		case 'T': return 3;
		case '$': return 4;
		default: throw new  IllegalArgumentException("Input file does not contain an DNA sequence");
		}
	}



	/**
	 * Helper class to represent the nodes.
	 */
	class Node{
		End end; //End
		Node[] child; //Children
		int start; //Start index.
		int index; //End index
		Node link; //Suffix link

		public Node(){	    	
		}

		public Node(int start, End end){
			child = new Node[LETTERS];
			this.start = start;
			this.end = end;
		}
	}


	/**
	 * Helper class to represent the global end.
	 */
	class End{
		int end;
		public End(int end){
			this.end = end;
		}
		public int getEnd(){
			return this.end;
		}
		public void setEnd(int end){
			this.end = end;
		}

	}
}