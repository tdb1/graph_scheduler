/*Taylor Beebe
 * CS 136 Lab 11
 * Compares students based on their name
 */ 
import java.util.Comparator;

public class StudentNameComparator implements Comparator<Student>{
  public int compare(Student i, Student j){
    return i.getName().compareTo(j.getName());
  }
}