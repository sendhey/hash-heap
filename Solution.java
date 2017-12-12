import java.util.*;
import java.lang.Object;

public class Solution {
    private static Hashtable<String, Guy> hash = new Hashtable<String, Guy>();
    private static Heap heap = new Heap(hash);

    // update a guy
    private static void update(String name, long eval) {
      Guy dude = hash.get(name);
      if (dude == null) {
        return;
      }
      dude.increaseEval(eval);
      heap.fix(dude);
    }

    
    
    // enroll a new guy
    private static void enroll(String name, long score) {
      Guy dude = new Guy(name, score);
      hash.put(name, dude);
      heap.insert(dude);
    }


    // cutoff the guys
    private static void cut(long cutoff) {
      int left = heap.cut(cutoff);
      System.out.println(left);
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // how many dudes to be added
        long size = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < size; i++) {
            String line = sc.nextLine();
            String[] line_split = line.split(" ");
            String name = line_split[0];
            long score = Integer.valueOf(line_split[1]);
            enroll(name, score);
        }

        // Number of subsequent queries
        long newSize = Long.parseLong(sc.nextLine());
        for (int i = 0; i < newSize; i++) {
            String line = sc.nextLine();
            String[] line_split = line.split(" ");
            String option = line_split[0];
            switch (option) {
              case "1":
                String name = line_split[1];
                long score = Long.valueOf(line_split[2]);
                update(name, score);
                break;
              case "2":
                long cutoff = Long.valueOf(line_split[1]);
                cut(cutoff);
                break;
              default:
                System.out.println("Unexpected input for one of the 'm' entries");
                break;
            }
        }

    }
}

class Heap {
	  public ArrayList<Guy> heap;

	  private static int counter = 0;
	  private Hashtable<String, Guy> guys;
	  private int size;

	  Heap(Hashtable<String, Guy> guys) {
	    this.guys = guys;
	    this.heap = new ArrayList<Guy>();
	    this.size = 0;
	  }

	  
	  // delete min
	  private void deleteMin() {
	    Guy one = heap.get(0);
	    one.setPosition(-1);
	    Guy root = heap.get(size - 1);
	    root.setPosition(0);
	    heap.set(0, root);
	    size--;
	    pushDown(root);
	  }
	  
	  
	  //cut the people who dont make the cutoff
	  public int cut(long cutoff) {
	    while (size > 0) {
	      Guy min = heap.get(0);
	      if (min.getEval() < cutoff) deleteMin();
	      else return size;
	    }
	    return 0;
	  }
	  
	  
	  // insert + update
	  public void insert(Guy dude) {
	    heap.add(size, dude);
	    dude.setPosition(size);
	    size++;
	    bubbleUp(dude);
	  }

	  
	  
	  // fix the heap
	  public void fix(Guy dude) {
	    bubbleUp(dude);
	    pushDown(dude);
	  }


	  //get size of heap
	  public int getSize() {
	    return size;
	  }

	  // swap guys a and b
	  private void swap(Guy a, Guy b) {
	    int aPosition = a.getPosition();
	    int bPosition = b.getPosition();
	    heap.set(aPosition, b);
	    heap.set(bPosition, a);
	    a.setPosition(bPosition);
	    b.setPosition(aPosition);
	  }


	  private boolean nodeGreaterThanParent(int nodePosition, int parentPosition) {
		    if (nodePosition >= size){
		    	return true;
		    }
		    if (parentPosition < 0){
		    	return true;
		    }

		    long parentScore = heap.get(parentPosition).getEval();
		    Guy dude = heap.get(nodePosition);
		    if (dude.getEval() < parentScore) return false;

		    int leftPosition = 2 * (dude.getPosition() + 1) - 1;
		    int rightPosition = leftPosition + 1;
		    boolean left = nodeGreaterThanParent(leftPosition, dude.getPosition());
		    boolean right = nodeGreaterThanParent(rightPosition, dude.getPosition());
		    return (left && right);
		  }

	  private void bubbleUp(Guy dude) {
	    Guy current = dude;
	    
	    int fixguy = dude.getPosition() + 1; 
	    int parent = (fixguy / 2 - 1);
	    while (parent >= 0) {
	      Guy p = heap.get(parent);
	      if (p.getEval() > current.getEval()) {
	        swap(current, p);
	        parent = (((parent + 1) / 2) - 1);
	        continue;
	      }
	      break;
	    }
	  }

	  public boolean verifyHeap() {
		    if (size < 2) return true;
		    return nodeGreaterThanParent(1, 0);
		  }
	  
	  private void pushDown(Guy dude) {
	    counter++;
	    Guy current = dude;
	    int adjustedPosition = dude.getPosition() + 1;
	    int leftPosition = (2 * adjustedPosition) - 1; // 2i (minus one because 0 indexing)
	    int rightPosition = (2 * adjustedPosition); // 2i+1 (minus one because 0 indexing)

	    while (leftPosition < size) {
	      rightPosition = leftPosition + 1;
	      Guy leftChild = heap.get(leftPosition);
	      Guy rightChild = null;
	      if (rightPosition < size) { // if there is a right child
	        rightChild = heap.get(rightPosition);
	      }

	      Guy swapChild = null;
	      if (rightChild != null) {
	        if (current.getEval() > rightChild.getEval()) {
	          swapChild = rightChild;
	        }
	      }
	      if (current.getEval() > leftChild.getEval()) {
	        if (swapChild == null) swapChild = leftChild;
	        else if (leftChild.getEval() < swapChild.getEval()) {
	          swapChild = leftChild;
	        }
	      }

	      if (swapChild == null) break;
	      swap(current, swapChild);
	      leftPosition = (2 * (current.getPosition() + 1)) - 1;
	    }
	  }
}

class Guy {
    private String name;
    private long eval;
    private int position;

    Guy(String name, long eval) {
      this.name = name;
      this.eval = eval;
    }

    //getters and setters
    
    public String getName() {
        return this.name;
      }
    
    public int getPosition() {
        return position;
      }
    
    public void setPosition(int position) {
        this.position = position;
    }

    public long getEval() {
      return this.eval;
    }
    
    public void increaseEval(long eval) {
        this.eval += eval;
      }

    
}