/*Taylor Beebe
 * CS 136 Lab 11
 * Custom vector to store and sort students based on a comparator
 */ 

import structure5.*;
import java.util.Comparator;

public class StudentVector<E> extends Vector<E> {
 
    public StudentVector(int initialCapacity){
      super();
    }
    

    // @Pre: c is a valid comparator
    // @Post: sort this vector in order determined by c
    public void sort(Comparator<E> c){
      int n = this.size();
 
      for (int i = 0; i < n-1; i++) {
        // Find the minimum element in unsorted array
        int minIndex = i;
        for (int j = i+1; j < n; j++)
          if (c.compare(this.get(minIndex), this.get(j)) >= 1){
          minIndex = j;
        }
        // Swap the found minimum element with the first
        // element
        E temp = this.get(minIndex);
        this.setElementAt(this.get(i),minIndex);
        this.setElementAt(temp, i);
      }
    }

}
