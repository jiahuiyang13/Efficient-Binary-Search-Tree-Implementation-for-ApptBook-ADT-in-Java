// This is an assignment for students to complete after reading Chapter 3 of
// "Data Structures and Other Objects Using Java" by Michael Main.

package edu.uwm.cs351;

import java.util.function.Consumer;

import edu.uwm.cs.junit.LockedTestCase;


//Jiahui Yang, Comp SCI 351, Homework 8
//Worked through helper methods with tutors in the tutor room
//Colloborated with Christian Oropeza on nextInTree
/******************************************************************************
 * This class is a homework assignment;
 * An ApptBook ("book" for short) is a sequence of Appointment objects in sorted order.
 * The book can have a special "current element," which is specified and 
 * accessed through four methods that are available in the this class 
 * (start, getCurrent, advance and isCurrent).
 ******************************************************************************/
public class ApptBook implements Cloneable {
	// TODO: Declare the private static Node class.
	// It should have a constructor but no methods.
	// The constructor should take an Appointment.
	// The fields of Node should have "default" access (neither public, nor private)
	// and should not start with underscores.
	//Node class that declares appointment data
	private static class Node{
		Appointment data;
		Node right, left;
			 //Node specifying constructor that initializes data
			 public Node(Appointment d){
				 data = d;
			 }
	}
	// TODO: Declare the private fields of ApptBook needed for sequences
	// using a binary search tree.
	//private fields declared
	private Node cursor;
	private Node root;
	private int manyItems;
	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };
	
	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}
	
	/**
	 * Return true if the given subtree has height no more than a given bound.
	 * In particular if the "tree" has a cycle, then false will be returned
	 * since it has unbounded height.
	 * @param r root of subtree to check, may be null
	 * @param max maximum permitted height (null has height 0)
	 * @return whether the subtree has at most tgis height
	 */
	//returns whether given Binary Search Tree 
	private boolean checkHeight(Node r, int max) {
		//if max is less than 0 return false height of a subtree is more than the max
		if (max < 0) return false;
		//if r is null given subtree has height no more than a given bound so return true
		if (r == null) return true;
		//calling itself decrementing max checking left subtree and right subtree, decrement max
		return checkHeight(r.left, max - 1) && checkHeight(r.right, max - 1); //TODO
	}
	
	
	/**
	 * Return the number of nodes in a subtree that has no cycles.
	 * @param r root of the subtree to count nodes in, may be null
	 * @return number of nodes in subtree
	 */
	private int countNodes(Node r) {
		//base case for if r is null just return 0 because there is nothing in the tree
		if (r == null) return 0;
		//return the amount of nodes on the right side added by the amound of nodes on the left side then add one to account for root
		return countNodes(r.right) + countNodes(r.left) + 1; // TODO
	}
	
	/**
	 * Return whether all the nodes in the subtree are in the given range,
	 * and also in their respective subranges.
	 * @param r root of subtree to check, may be null
	 * @param lo inclusive lower bound, may be null (no lower bound)
	 * @param hi exclusive upper bound, may be null (no upper bound)
	 * @return
	 */
	private boolean allInRange(Node r, Appointment lo, Appointment hi) {
		//if node is null then it is in range because null is unbound
		if (r == null) return true;
		//return a report if nodes data is null
		if (r.data == null) return report("Node data is null");
		//if lo isn't null and nodes data is before the given range, return report
		if (lo != null && r.data.compareTo(lo)<0) return report("Node before range");
		//if hi isn't null and nodes data is after the given range or exactly the hi, then report
		if (hi != null && r.data.compareTo(hi)>=0) return report("Node after range");
		//recursive call on itself with different node being passed in each time going through the left and right of tree
		return allInRange(r.left, lo, r.data) && allInRange(r.right, r.data, hi);// TODO
	}
	
	/**
	 * Return whether the cursor was found in the tree.
	 * If the cursor is null, it should always be found since 
	 * a binary search tree has many null pointers in it.
	 * This method doesn't examine any of the data elements;
	 * it works fine even on trees that are badly structured, as long as
	 * they are height-bounded.
	 * @param r subtree to check, may be null, but must have bounded height
	 * @return true if the cursor was found in the subtree
	 */
	
	private boolean foundCursor(Node r) {
		//if r is cursor return true
		if (r == cursor) return true;
		//if r is null there is no cursor
		if (r == null) return false;
		//return recursive call checking the right side of the tree to see if the cursor is on that side
		//then check if the cursor is on the left side
		return foundCursor(r.right) || foundCursor(r.left);
		 // TODO
	}

	
	private boolean wellFormed() {
		// Check the invariant.
		// Invariant:
		// 1. The tree must have height bounded by the number of items
		// 2. The number of nodes must match manyItems
		// 3. Every node's data must not be null and be in range.
		// 4. The cursor must be null or in the tree.
		
		// Implementation:
		// Do multiple checks: each time returning false if a problem is found.
		// (Use "report" to give a descriptive report while returning false.)
		// TODO: Use helper methods to do all the work.
		
		//if the checkHeight doesn't return true return report
		if (checkHeight(root, manyItems) != true) return report("height not bounded by number of items");
		//if the countNodes return does not equal manyItems return report
		if (countNodes(root) != manyItems) return report("number of nodes not equal to manyItems");
		//if any root is not in range return report
		if (!allInRange(root, null, null)) return false;
		//if there is no cursor found, return report
		if (foundCursor(root) != true)return report("no cursor found");
		// If no problems found, then return true:
		return true;
	}
	
	// This is only for testing the invariant.  Do not change!
	private ApptBook(boolean testInvariant) { }

	/**
	 * Initialize an empty book. 
	 **/   
	//default constructor
	public ApptBook( )
	{
		// TODO: Implemented by student.
		manyItems = 0;
		cursor = null;
		root = null;
		assert wellFormed() : "invariant failed at end of constructor";
	}
	/**
	 * Determine the number of elements in this book.
	 * @return
	 *   the number of elements in this book
	 **/ 
	public int size( )
	{
		//return manyitems
		assert wellFormed() : "invariant failed at start of size";
		return manyItems;
		// TODO: Implemented by student.
	
	
	}

	/**
	 * Return the first node in a non-empty subtree.
	 * It doesn't examine the data in teh nodes; 
	 * it just uses the structure.
	 * @param r subtree, must not be null
	 * @return first node in the subtree
	 */
	//https://stackoverflow.com/questions/36629442/how-to-get-the-first-element-when-going-through-a-binary-search-tree-with-inorde
	private Node firstInTree(Node r) {
		//base case if r is null then return null
		if (r == null) return null;
		//if there is a left subtree return the node most left
		if (r.left != null) return firstInTree(r.left);
		//return r r.left is null
		return r;
		 // TODO: non-recursive is fine
	}
	
	/**
	 * Set the current element at the front of this book.
	 * @postcondition
	 *   The front element of this book is now the current element (but 
	 *   if this book has no elements at all, then there is no current 
	 *   element).
	 **/ 
	public void start( )
	{
		//set cursor to the node most left
		assert wellFormed() : "invariant failed at start of start";
		cursor = firstInTree(root);
		// TODO: Implemented by student.
		assert wellFormed() : "invariant failed at end of start";
	}

	/**
	 * Accessor method to determine whether this book has a specified 
	 * current element that can be retrieved with the 
	 * getCurrent method. 
	 * @return
	 *   true (there is a current element) or false (there is no current element at the moment)
	 **/
	public boolean isCurrent( )
	{
		//return is cursor is null or not
		assert wellFormed() : "invariant failed at start of isCurrent";
		return cursor != null;
		// TODO: Implemented by student.
		
	}

	/**
	 * Accessor method to get the current element of this book. 
	 * @precondition
	 *   isCurrent() returns true.
	 * @return
	 *   the current element of this book
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   getCurrent may not be called.
	 **/
	public Appointment getCurrent( )
	{
		//returns the cursors data if there is one, throw exception if there isn't one
		assert wellFormed() : "invariant failed at start of getCurrent";
		if (!isCurrent()) throw new IllegalStateException("no current");
		return cursor.data;
		// TODO: Implemented by student.
	}

	/**
	 * Find the node that has the appt (if acceptEquivalent) or the first thing
	 * after it.  Return that node.  Return the alternate if everything in the subtree
	 * comes before the given appt.
	 * @param r subtree to look into, may be null
	 * @param appt appointment to look for, must not be null
	 * @param acceptEquivalent whether we accept something equivalent.  Otherwise, only
	 * appointments after the appt are accepted.
	 * @param alt what to return if no node in subtree is acceptable.
	 * @return node that has the first element equal (if acceptEquivalent) or after
	 * the appt.
	 */
	//https://www.techiedelight.com/find-inorder-successor-given-key-bst/
	private Node nextInTree(Node r, Appointment appt, boolean acceptEquivalent, Node alt) {
		//if node is null return the alternate node
		if (r == null) return alt;
		//if node data is the same as the appt we are looking and acceptEquivalent is true, return that node
		if (r.data.equals(appt) && acceptEquivalent) return r;
		//if the nodes data comes before the appt, use recursion to call itself going to the left subtree changing alt's data as it goes
		else if (r.data.compareTo(appt)>0) return nextInTree(r.left, appt,acceptEquivalent, alt = r);
		//if nodes data comes after the appt, use recursion to call itself going to the right subtree
		else return nextInTree(r.right, appt,acceptEquivalent, alt);		
		// TODO: recursion not required, but is simpler
	}
	
	/**
	 * Move forward, so that the current element will be the next element in
	 * this book.
	 * @precondition
	 *   isCurrent() returns true. 
	 * @postcondition
	 *   If the current element was already the end element of this book 
	 *   (with nothing after it), then there is no longer any current element. 
	 *   Otherwise, the new element is the element immediately after the 
	 *   original current element.
	 * @exception IllegalStateException
	 *   Indicates that there is no current element, so 
	 *   advance may not be called.
	 **/
	public void advance( )
	{
		assert wellFormed() : "invariant failed at start of advance";
		//fail fast: if there is no current throw exception
		if (!isCurrent()) throw new IllegalStateException();
		//if there is a right subtree from cursor, set the cursor to the node that is equivalent or to the right of cursor
		if (cursor.right != null) cursor = nextInTree(cursor.right, cursor.data, true, null);	
		//if cursor doesn't have a right subtree, then set cursor to the ancestor that comes after it
		else cursor = nextInTree(root, cursor.data, false, null);
		// TODO: See homework description.
		// firstInTree and nextInTree are useful.
		assert wellFormed() : "invariant failed at end of advance";
	}
	/**
	 * Remove the current element from this book.
	 * NB: Not supported in Homework #8
	 **/
	public void removeCurrent( )
	{
		assert wellFormed() : "invariant failed at start of removeCurrent";
		throw new UnsupportedOperationException("remove is not implemented");
	}
	
	/**
	 * Set the current element to the first element that is equal
	 * or greater than the guide.  This operation will be efficient
	 * if the tree is balanced.
	 * @param guide element to compare against, must not be null.
	 */
	public void setCurrent(Appointment guide) {
		assert wellFormed() : "invariant failed at start of setCurrent";
		//fail fast
		if (guide == null) throw new NullPointerException();
		//set the cursor to the node that has the same data as guide appointment or the one closest
		cursor = nextInTree(root, guide, true, null);
		// TODO: Use nextInTree helper method
		assert wellFormed() : "invariant failed at end of setCurrent";
	}

	// OPTIONAL: You may define a helper method for insert.  The solution does
	//insert helper that returns a node and takes a node and an appointment as it's parameters
	private Node insertH(Node r, Appointment element) {
		//base case
		if (r == null) return new Node(element);
		//node of element should go BEFORE r
		if (element.compareTo(r.data)<0)r.left = insertH(r.left, element);
		//node of element should go AFTER r
		else r.right = insertH(r.right, element);
		return r;
	}
	/**
	 * Add a new element to this book, in order.  If an equal appointment is already
	 * in the book, it is inserted after the last of these. 
	 * The current element (if any) is not affected.
	 * @param element
	 *   the new element that is being added, must not be null
	 * @postcondition
	 *   element (whether or not is exists) is not changed.
	 * @exception IllegalArgumentException
	 *   indicates the parameter is null
	 **/
	public void insert(Appointment element)
	{
		assert wellFormed() : "invariant failed at start of insert";
		if (element == null) throw new IllegalArgumentException();
		//call helper and set root
		root = insertH(root, element);
		//increment manyItems
		manyItems++;
		// TODO: Implemented by student.
		assert wellFormed() : "invariant failed at end of insert";
	}

	// TODO: recursive helper method for insertAll.  
	// - Must be recursive.
	// - Must add in "pre-order"
	//takes node as a paramater and returns void
	private void insertAllH(Node r) {
		//base case
		if (r == null) return;
		//uses insert method to insert the argument passed
		insert(r.data);
		//recursion call that inserts nodes that need to go to left subtree to maintain order
		insertAllH(r.left);
		//recursion call that inserts nodes that need to go to left subtree to maintain order
		insertAllH(r.right);
	}
	/**
	 * Place all the appointments of another book (which may be the
	 * same book as this!) into this book in order as in {@link #insert}.
	 * The elements should added one by one.
	 * @param addend
	 *   a book whose contents will be placed into this book
	 * @precondition
	 *   The parameter, addend, is not null. 
	 * @postcondition
	 *   The elements from addend have been placed into
	 *   this book. The current el;ement (if any) is
	 *   unchanged.
	 **/
	public void insertAll(ApptBook addend)
	{
		assert wellFormed() : "invariant failed at start of insertAll";
		//fail fast
		if (addend.manyItems == 0) return;
		//if addend is the same as the ApptBook calling the method, then clone the caller 
		if(addend == this) addend = addend.clone();
		//uses insert all helper to add to the BST
		insertAllH(addend.root);
		// TODO: Implemented by student.
		// Watch out for the this==addend case!
		// Cloning the addend is an easy way to avoid problems.
		assert wellFormed() : "invariant failed at end of insertAll";
		assert addend.wellFormed() : "invariant of addend broken in insertAll";
	}

	// TODO: private recursive helper method for clone.
	// - Must be recursive
	// - Take the answer as a parameter so you can set the cloned cursor
	//https://interview.hackingnote.com/en/problems/clone-binary-tree
	private Node cloneH(Node r, ApptBook answer) {
		//base case
		if (r == null) return null;
		//create a new node with the old data
		Node newNode = new Node(r.data);
		//if the nodes data that was created the root, set the cloned root to the new node
		if (r == root) answer.root = newNode;
		//if the nodes data that was created the cursor, set the cloned cursor to the new node
		if (r == cursor) answer.cursor = newNode;
		//set the newNodes left subtree to the new nodes created using recursion
		newNode.left = cloneH(r.left, answer);
		//set the newNodes right subtree to the new nodes created using recursion
		newNode.right = cloneH(r.right, answer);
		return newNode;
	}
	/**
	 * Generate a copy of this book.
	 * @return
	 *   The return value is a copy of this book. Subsequent changes to the
	 *   copy will not affect the original, nor vice versa.
	 **/ 
	public ApptBook clone( ) { 
		assert wellFormed() : "invariant failed at start of clone";
		ApptBook answer;
	
		try
		{
			answer = (ApptBook) super.clone( );
		}
		catch (CloneNotSupportedException e)
		{  // This exception should not occur. But if it does, it would probably
			// indicate a programming error that made super.clone unavailable.
			// The most common error would be forgetting the "Implements Cloneable"
			// clause at the start of this class.
			throw new RuntimeException
			("This class does not implement Cloneable");
		}
	
		// TODO: copy the structure (use helper method)
		//call helper method that starts at root
		cloneH(root, answer);
		assert wellFormed() : "invariant failed at end of clone";
		assert answer.wellFormed() : "invariant on answer failed at end of clone";
		return answer;
	}

	// don't change this nested class:
	public static class TestInvariantChecker extends LockedTestCase {
		protected ApptBook self;

		protected Consumer<String> getReporter() {
			return reporter;
		}
		
		protected void setReporter(Consumer<String> c) {
			reporter = c;
		}
		
		private static Appointment a = new Appointment(new Period(new Time(), Duration.HOUR), "default");
		
		protected class Node extends ApptBook.Node {
			public Node(Appointment d, Node n1, Node n2) {
				super(a);
				data = d;
				left = n1;
				right = n2;
			}
			public void setLeft(Node l) {
				left = l;
			}
			public void setRight(Node r) {
				right = r;
			}
		}
		
		protected Node newNode(Appointment a, Node l, Node r) {
			return new Node(a, l, r);
		}
		
		protected void setRoot(Node n) {
			self.root = n;
		}
		
		protected void setManyItems(int mi) {
			self.manyItems = mi;
		}
		
		protected void setCursor(Node n) {
			self.cursor = n;
		}
		
		protected void setUp() {
			self = new ApptBook(false);
			self.root = self.cursor = null;
			self.manyItems = 0;
		}
		
		
		/// relay methods for helper methods:
		
		protected boolean checkHeight(Node r, int max) {
			return self.checkHeight(r, max);
		}
		
		protected int countNodes(Node r) {
			return self.countNodes(r);
		}
		
		protected boolean allInRange(Node r, Appointment lo, Appointment hi) {
			return self.allInRange(r, lo, hi);
		}
		
		protected boolean foundCursor(Node r) {
			return self.foundCursor(r);
		}
		
		protected boolean wellFormed() {
			return self.wellFormed();
		}
		
		protected Node firstInTree(Node r) {
			return (Node)self.firstInTree(r);
		}
		
		protected Node nextInTree(Node r, Appointment a, boolean acceptEquiv, Node alt) {
			return (Node)self.nextInTree(r, a, acceptEquiv, alt);
		}
		
		
		/// Prevent this test suite from running by itself
		
		public void test() {
			assertFalse("DOn't attempt to run this test", true);
		}
	}
}

