package net.datastructures;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * 
 * @author Ludwig Tranheden 5129849.
 * I put the running time analysis (worst case time-complexity) in the beginning of all methods.
 * It made more sense since methods uses private help-methods.
 */

public class ExtendedAVLTree<K,V> extends AVLTree<K,V>{

	
    /**
     * This class method creates an identical copy of the AVL tree specified by the parameter
     * and returns a reference to the new AVL tree.
     * Time-complexity: All operations run in constant time (O(1)) except the call to Clonesub
     * 					which run in O(n). Hence the time-complexity is O(n) where n is the size of the input tree.
     */
    public static <K, V> AVLTree<K, V> clone(AVLTree<K,V> tree) {
        AVLTree<K, V> clone = new AVLTree<K, V>();  // Initiate new tree.
        clone.insert(tree.root.element().getKey(), tree.root.element().getValue()); // Copy root.
        cloneSub(clone.root, tree.root, tree); // Clone the subtree starting at the root (I.e the entire tree).
        clone.size = tree.size;	// Copy size field.
        clone.numEntries = tree.numEntries; //Copy numEntries field.
        clone.setHeight(clone.root()); //Set height of root, will be correct since all other heights are updated in "Clonesub".
        return clone;
    }
    
    /**
     * Clones the subtree starting at node "original" to the node "clone".
     * Time-complexity: Performs a constant number of operations in each recursion.
     * 					Is called recursively n + c times (c a constant) where n is the size of the subtree rooted at original.
     * 					Hence the time-complexity is O(n).
     */
    private static <K, V> void cloneSub(BTPosition<Entry<K,V>> clone, BTPosition<Entry<K,V>> original, AVLTree<K,V> tree) {
        
        if(original.getLeft() != null){ // While not at a leaf, recursively keep copying the left children.
        	BTPosition<Entry<K,V>> left = cloneNode(clone, original, "left",tree); // Clone and link the left children.
        	cloneSub(left, original.getLeft(),tree);
        }
        
        if(original.getLeft() != null){ // While not at a leaf, recursively keep copying the right children.
        	BTPosition<Entry<K,V>> right = cloneNode(clone, original, "right",tree); // Clone and link the right children.
        	cloneSub(right, original.getRight(),tree);
        }
    }
    
    /**
     * Clones and links the children specified by the string "link" from "node" to "copy". Returns the children copied. 
     * @throws IllegalArgumentException
     * Time-complexity: Performs a constant number of operations, hence the time-complexity is O(1).
     */
    private static <K,V> BTPosition<Entry<K,V>> cloneNode(BTPosition<Entry<K,V>> copy, BTPosition<Entry<K,V>> node, String link, AVLTree<K,V> tree) throws IllegalArgumentException {
    	if (!link.toLowerCase().equals("right") && !link.equals("left")){ // If link not right or left, throw exception.
    		throw new IllegalArgumentException("The specification of left or right child is missing.");
    	}
    	
        if (link.toLowerCase().equals("left")){ // Copy left children.
            AVLNode<K,V> left = new AVLNode<K,V>(node.getLeft().element(),copy,null,null);
        	left.setHeight(tree.height(node.getLeft()));
            copy.setLeft(left); 
            return left;
        }
        else{	// Copy right children.
        	AVLNode<K,V> right = new AVLNode<K,V>(node.getRight().element(), copy, null,null);
        	right.setHeight(tree.height(node.getRight()));
            copy.setRight(right);
            return right;
        }

    }
    
    
	/**
	 * Merges two AVL trees, tree1 and tree2 into a new AVL tree, and returns the merged tree.
	 * m = size tree1, n = size tree2.
	 * Time-complexity: The creation of inorderPositionslists runs in O(n+m).
	 * The merging of the lists runs in O(n+m).
	 * The creation of the tree by list2tree runs in O(size of list) = O(n+m).
	 * Hence the total time-complexity is O(n+m).
	 */
	public static <K, V> AVLTree<K, V> merge(AVLTree<K,V> tree1, AVLTree<K,V> tree2 ){
        PositionList<Position<Entry<K,V>>> listtree1 = new NodePositionList<Position<Entry<K,V>>>();
        PositionList<Position<Entry<K,V>>> listtree2 = new NodePositionList<Position<Entry<K,V>>>();
        PositionList<Position<Entry<K,V>>> list = new NodePositionList<Position<Entry<K,V>>>();
        
        tree1.inorderPositions(tree1.root(), listtree1); // Create sorted list from tree1.
        tree2.inorderPositions(tree2.root(), listtree2); // Create sorted list from tree2.
        
        boolean stopcompare = false;
        int i = 0;
        while(!listtree1.isEmpty() && !listtree2.isEmpty()){	// Add all elements of lists to the new list.
        	if(listtree1.first().element().element() == null){listtree1.remove(listtree1.first());} //Do not add null elements.
        	if(listtree2.first().element().element() == null){listtree2.remove(listtree2.first());}
        	else{
        		if ((listtree1.isEmpty() ||  listtree2.isEmpty()) && !stopcompare){ //If one list becomes empty.
        			stopcompare = true;
        			if (listtree1.isEmpty()){i = 1;}	//Keep adding elements from the other.
        			else{i = -1;}
        		}
        		if(!stopcompare){
        			i = compare(listtree1.first().element(),listtree2.first().element()); //Compare the keys.
        		}
        		if (i < 0){
        			list.addLast(listtree1.remove(listtree1.first()));	//If key1<key2 add from list1 to end of merged list.
        		}
        		else{
        			list.addLast(listtree2.remove(listtree2.first()));  //If key1>=key2 add from list1 to end of merged list.
        		}
        	}
        }
        
        AVLTree<K, V> mergedtree = new AVLTree<K, V>();  // Initiate new tree.
        mergedtree.size = tree1.size + tree2.size;
        mergedtree.numEntries = tree1.numEntries + tree2.numEntries;
        mergedtree.root = list2tree(0,list.size()-1,list);
        return mergedtree;
	}
	
	/**
	 * Compares two keys, returns -1 if the first key is smaller and 1 if the second is (or tie).
	 * Time-complexity: O(1).
	 */
	private static<K,V> int compare(Position<Entry<K, V>> var1, Position<Entry<K, V>> var2){
		if(Integer.parseInt(var1.element().getKey().toString()) < Integer.parseInt(var2.element().getKey().toString())){return -1;}
		else{return 1;}
	}
	
	
	/**
	 * Recursively builds AVL-subtrees "bottom-up" starting from the middle of a sorted list, returns the root of the tree.
	 * Time-complexity: Performs a constant number of operations except the recursive calls. n + c recursive calls are made where 
	 * n is the size of the list and c is an constant. Hence the time complexity is O(n).
	 */
	private static <K,V> BTPosition<Entry<K,V>> list2tree(int start, int stop, PositionList<Position<Entry<K,V>>> list){
		if (start > stop){return null;}	//Base case, at end of list.
		int midpoint = start + (stop-start)/2; //Update midpoint.
		BTPosition<Entry<K,V>> parent = new AVLNode<K, V>(); 
		BTPosition<Entry<K,V>> left = list2tree(start,midpoint-1,list); // Start building left subtree recursively. (first call)
		if (left != null){	
			left.setParent(parent); 
		}
		parent.setLeft(left);
		parent.setElement(list.remove(list.first()).element()); 
		BTPosition<Entry<K,V>> right = list2tree(midpoint+1,stop,list); // Start building right subtree recursively. (first call)
		if(right != null){	
			right.setParent(parent);
		}
		parent.setRight(right);
		return parent;
	}
		

	
	/**
	 * Prints the AVL tree specified by the parameter on a new window. 
	 * Each internal node is displayed by a circle containing its key and each external node is displayed by a rectangle. 
	 * Size of circles and rectangles ensures method never prints a tree with crossing edges.
	 */
	public static <K, V> void print(AVLTree<K, V> tree){
        JFrame frame = new JFrame();
        int rad = 300; // Radius circles.
        int w1 = 2*rad; //Dimensions rectangles.
        int h1 = 2*rad;
        int gridWidth = 1200;	//Size of window (fixt).
        int gridHeight = 800;
        
        double heightbound = 2*(Math.log(tree.size) / Math.log(2)) + 2; //A bound for the height.
        double scale = tree.size; //Size of tree.
        int exnodes = tree.size - tree.numEntries; //Number of external nodes.
        int r = (int) (rad/scale); //Scale down dimension, to fit.
        int w = (int) (w1/scale);
        int h = (int) (h1/scale);
        int verticalOffset = (int) (exnodes*h); //Vertical offset determined by height of rectangles/circles and number of external nodes .
        int horizontalOffset = exnodes*w; //Horizontal offset determined by number of external nodes and their width.
        
        frame.setSize(gridWidth, gridHeight); 
        frame.setResizable(true); 
        frame.setTitle("AVLTree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BTPosition<Entry<K,V>> position = tree.root;
        drawTree(position,frame,r,w,h,gridWidth/2,2*r,verticalOffset,horizontalOffset); //Recursively draw tree.
	}
	
	/**
	 * Recursively draws a subtree starting from the specified node.
	 */
	private static <K,V> void drawTree(BTPosition<Entry<K,V>> node, JFrame frame, int rad, int width, int height, int x, int y, int vO, int hO){
		if(node.getLeft() != null && node.getLeft().element() != null){ //Recurse down to left external node.
			drawTree(node.getLeft(), frame, rad, width, height, x-hO, y+vO,vO/2,hO/2);
		}
		else{
			frame.getContentPane().add(new Rect(x-hO,y+vO,height,width,"")); // Draw rectangle
			frame.setVisible(true);
		}
		if(node.getRight() != null && node.getRight().element() != null){	// //Recurse down to right external node.
			drawTree(node.getRight(), frame, rad, width, height, x+hO, y+vO, vO/2, hO/2); 
		}
		else{
			frame.getContentPane().add(new Rect(x + hO,y+vO,height,width,"")); // Draw rectangle
			frame.setVisible(true);
		}
		if (node.element() != null){
        frame.getContentPane().add(new Circle(x, y, rad, node.element().getKey().toString())); // If we reach this point, draw internal node.
        frame.setVisible(true);
		}
        frame.getContentPane().add(new Line(x,y+rad,x-hO,y+vO-rad)); // Connect the internal node to it's children with lines.
        frame.setVisible(true);

        frame.getContentPane().add(new Line(x,y+rad,x+hO,y+vO-rad));
        frame.setVisible(true);
		
	}
	
	/**
	 * Help class to draw circles with text inside.
	 *
	 */
	private static class Circle extends JComponent {

		private int x, y;
		private int radius;
		private String element;

		Circle( int x, int y, int r, String text) {
		    this.x = x;
		    this.y = y;
		    this.radius = r;
		    this.element = text;
		}

		protected void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    int fontSize = radius+2+2; // For visibility.
		    int l = element.length();
		    g.setFont(new Font("TimesRoman", Font.ITALIC, fontSize));
		    g.drawString(element, x-radius/2 - l%2, y+radius/2); // Draw string.
		    Graphics2D g2 = (Graphics2D) g;

		    Ellipse2D circle = new Ellipse2D.Double();
		    circle.setFrameFromCenter(x, y, x + radius, y + radius); //Draw circle.

		    g2.draw(circle);
		}
	}
	/**
	 * Help class to draw lines connecting the nodes.
	 */
	private static class Line extends JComponent {

		private int x1,x2,y1,y2;

		Line(int x1, int y1, int x2, int y2) {
		    this.x1 = x1;
		    this.y1 = y1;
		    this.x2 = x2;
		    this.y2 = y2;
		}

		protected void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    g.drawLine(x1, y1, x2, y2); // Draw line between coordinates.
		}
	}
	/**
	 * Help class to draw rectangles with text inside (We will just send an empty string).
	 *
	 */
	private static class Rect extends JComponent {

		private int x,y;
		private int h,w;
		private String element;

		Rect(int x, int y, int h, int w, String element) {
		    this.x = x;
		    this.y = y;
		    this.h = h;
		    this.w = w;
		    this.element = element;
		}

		protected void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    
		    g.drawRect(x-w/2, y-h/2, w, h); // Draw rectangle.
		    g.drawString(element, x, y-h/2); // Draw string.
		}
	}
	
}