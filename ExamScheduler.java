/* Taylor Beebe
 * CS 136 Lab 11
 * Exam scheduler creates an undirected graph where vertexs represent classes
 * and the edges represent students taking both classes the edge connects
 * 
 * an algoritm chooses classes based on wheter or not they are connected to other
 * classes in a timeslot to create an exam schedule that doesn't cause a conflict
 * for any student
 * 
 * Extensions 1, 2, and 3 were done for this assignment
 */ 

import structure5.*;
import java.util.Iterator;
import java.util.Scanner;

class ExamScheduler{
  
  /* parses a file where the first line is the student name and the next fourl lines
   * are the classes being taken by that student. such a file is parsed and a vector of
   * students is created
   * 
   * @Pre: filename is a valid file
   * @Post returns a vector containing Student objects
   */ 
  private Vector<Student> parseSchedules(String filename){
    //Scanner to read in file
    Scanner in = new Scanner(new FileStream(filename));
    Vector<Student> vs = new Vector<Student>(10);
    
    while(in.hasNextLine()){
      vs.add(new Student(in.nextLine(), in.nextLine(), in.nextLine(), in.nextLine(), in.nextLine()));
    }
    return vs;
  }
  
  /* creates a graph where the verticies are classes and the edges between verticies
   * are the number of students taking both of those classes
   * 
   * @Pre: v is not empty
   * @Post: returns an undirected graph that contains the schedules of the students
   */ 
  private GraphListUndirected<String, Integer> createGraph(Vector<Student> v){
    
    GraphListUndirected<String, Integer> g = 
      new GraphListUndirected<String,Integer>();
    String [] c;
    
    for (Student s : v){
      
      //get students classes
      c = s.getClasses();
      
      for (String st : c) g.add(st);
      //connects all the added classes to eachother in graph
      for (int x = 0; x < c.length; x++){
        for (int y = 0; y < c.length; y++){
          if (y != x) g.addEdge(g.get(c[x]), g.get(c[y]), 1);
        } } }
    
    //returns the created graph
    return g;
  }
  
  /* prints the graph as an adjacency matrix
   * 
   * @Pre: The graph is not empty
   * @Post: prints an adjacency matrix where each line is
   */ 
  public void printGraph(GraphListUndirected<String, Integer> g){
    
    Iterator<String> i = g.iterator();
    
    while (i.hasNext()){
      String vertex = i.next();
      String line = vertex + "->";
      Iterator<String> n = g.neighbors(vertex);
      while (n.hasNext()){
        line += n.next() + " ";
      }
      //prints each class -> neighbors
      System.out.println(line);
    } }
  
  
  /* prints the schedule generated by the getSchedule method
   * 
   * @Pre: vector is not empty
   * @Post: prints each association as a line with the slot -> class
   */ 
  public void printSchedule(Vector<Association<Integer, String>> v){
    
    int slot = 1;
    for(Association a : v){
      System.out.println("Slot " + a.getKey() + " -> " + a.getValue());
    } }
  
  /*creates a schedule using a graph
   * 
   * @Param: a graph whose vertexes are classes and edges are the number of students taking
   * both classes the edge connects
   * @Pre: graph is not blank
   * @Post: returns a vector of associations contining an integer representing a timeslot
   * and the name of the class in that timeslot as the key
   */ 
  private Vector<Association<Integer, String>> getSchedule(GraphListUndirected<String,Integer> g){
    Assert.pre(!g.isEmpty(), "Graph is empty, cannot generate schedule");
    
    //the slot number to store in association
    int slot = 1;
    
    /*next is the next class added to graph
    *initialized to be the smallest degree
    *vertex in the graph*/
    String next = getSmallestDegree(g);
    
    //holds the schedule generated
    Vector<Association<Integer, String>> v =
      new Vector<Association<Integer, String>>();
    
    //holds the classes in the current time slot
    Vector<String> currentSlotVector = new Vector<String>(5);
    
    //sets the starting vertex as visited
    g.visit(g.get(next));
    
    //adds the starting vertex to the classes in current
    //time slot
    currentSlotVector.add(next);
    
    //associates slot number with starting class
    v.add(new Association<Integer, String>(slot, next));
    
    while(true){
      next = getNextVertex(g, currentSlotVector);
      if (next!= ""){
        v.add(new Association<Integer, String>(slot, next));
        g.visit(g.get(next));
        currentSlotVector.add(next);
      }
      //breaks the loop
      else if (next == "" && currentSlotVector.isEmpty()) break;
      else {
        currentSlotVector.clear();
        slot++;
      } }
    //reset the visited flags in graph
    g.reset();
    if(g.size() > v.size()) Assert.fail("Cannot generate schedule for graph");
    return v;
  }
  
  /* fetches a vertex label for the getSchedule method which represents a class that doesn't conflict
   * with any classes in the current timeslot
   * 
   * @Param: a graph of classes, and a vector containing all the classes in the current timeslot
   * @Pre: the graph is not blank
   * @Post: returns a string label that doesn't conflict with any of the classes in currentClasses
   */ 
  private String getNextVertex(GraphListUndirected<String, Integer> g, Vector<String> currentClasses){
    Assert.pre(!g.isEmpty(), "Graph is empty, cannot generate schedule");
    Iterator<String> i = g.iterator();
    
    String nextVertex = "";
    String testVertex;
    //finds the starting vertex to ensure starting vertex isn't already visited
    while(i.hasNext() && nextVertex == ""){
      testVertex = i.next();
      if(!g.isVisited(testVertex) && 
         getNextVertexHelper(g, currentClasses, testVertex)) nextVertex = testVertex;
    }
    //gets the next vertex with the smallest degree
    while (i.hasNext()){
      testVertex = i.next();
      if(!g.isVisited(testVertex) && getNextVertexHelper(g, currentClasses, testVertex) 
           && g.degree(nextVertex) > g.degree(testVertex)) nextVertex = testVertex;
    }
    return nextVertex;
  }
  
  /* helper method for getNextVertex. returns true if testVertex doesn't 
   * have any neighbors present in currentClasses
   * 
   * @Param: graph containing classes, vector containing classes in the 
   * current timeslot, string label of the vertex being considered
   * @Pre: textVertex is a valid vertex, graph is not blank
   * @Post: returns true if testVertex doesn't conflict with the classes in currentClasses
   */ 
  private Boolean getNextVertexHelper(GraphListUndirected<String, Integer> g, Vector<String> currentClasses, String testVertex){
    Assert.pre(!g.isEmpty(), "Graph is empty, cannot generate schedule");
    if(!g.contains(testVertex)) return false;
    Iterator<String> n = g.neighbors(testVertex);
    while(n.hasNext()){
      if(currentClasses.contains(n.next())) return false;
    } return true;
  }
  
  /* returns the smallest degree vertex to start the scheduling process
   * 
   * @Param: a graph contining classes
   * @Pre: the graph is not blank
   * @Post: returns the label of the smallest degree vertex in the graph
   */ 
  private String getSmallestDegree(GraphListUndirected<String, Integer> g){
    Assert.pre(!g.isEmpty(), "Graph is empty, cannot generate schedule");
    Iterator<String> i = g.iterator();
    String smallestDegree = i.next();
    
    while (i.hasNext()){
      String testVertex = i.next();
      if(g.degree(smallestDegree) > g.degree(testVertex)) smallestDegree = testVertex;
    } return smallestDegree;
  }
  
  /* prints the best possible exam schedule
   * 
   * note: this creates an iterator that iterates through all possible permutations
   * of the vector of classes. the number of permutations is the factorial of the vector
   * size. any vector over 18 should not be attempted 
   * 
   * @Param: a graph of the classes
   * @Pre: the graph is not blank
   * @Post: returns a vector of associations containing the best possible schedule
   */ 
  private Vector<Association<Integer, String>> getBestSchedule(GraphListUndirected<String,Integer> g){
    Assert.pre(!g.isEmpty(), "Graph is empty, cannot generate schedule");
    
    //if there are more than 18 classes, just return a blank vector
    if (g.size() > 18) return new Vector<Association<Integer, String>>();
    
    Iterator<Vector<String>> iter = new Permute<String>(g.iterator());
    
    //the best permutation of the vector
    Vector<String> bestSchedule = iter.next();
    
    //the vector used to test against the best vector
    Vector<String> testSchedule;
    
    //classes in the current timeslot
    Vector<String> runningSchedule = new Vector<String>(5);
    
    //schedule being returned by method
    Vector<Association<Integer, String>> returnSchedule =
      new Vector<Association<Integer, String>>(g.size());
    
    //current timeslot
    int slot = 1;
    
    //the lowest number of timeslots, initialized to worst case: one class
    //per timeslot
    int bestSlot = g.size();
    
    //iterates through all permutations to find best
    while (iter.hasNext()){
      //fetches next possible optimal schedule 
      testSchedule = iter.next();
      runningSchedule.clear();
      //figures out how many slots the permutation requires
      for(String s : testSchedule){
        
        g.visit(g.get(s));
        
        if (getNextVertexHelper(g, runningSchedule, s)) runningSchedule.add(s);
        else {
          runningSchedule.clear();
          runningSchedule.add(s);
          slot++;
        } }
      //tests if this permutation requires fewer slots than running best
      if (slot < bestSlot){
        bestSchedule = new Vector<String>(testSchedule);
        bestSlot = slot;
      }
      g.reset();
      slot = 1;
    }
    
    runningSchedule.clear();
    //creates a vector of associatoins out of best schedule found
    for (String str : bestSchedule){
      g.visit(g.get(str));
      if (getNextVertexHelper(g, runningSchedule, str)){
        returnSchedule.add(new Association<Integer, String>(slot, str));
        runningSchedule.add(str);
      }
      else{
        slot++;
        returnSchedule.add(new Association<Integer, String>(slot, str));
        runningSchedule.clear();
        runningSchedule.add(str);
      }
    }
    g.reset();
    return returnSchedule;
  }
  
  /* Prints out the exam schedule in alphabetical order
   * 
   * @Param: vector of associations with the time slot number as the key and class as value
   * @Pre: vector is not empty
   * @Post: prints the exam schedule in alphabetical order
   */ 
  public void printOrderedSchedule(Vector<Association<Integer, String>> v){
    
    Vector<Association<Integer, String>> returnVector = new Vector<Association<Integer, String>>(v.size());
    OrderedVector<String> ordV = new OrderedVector<String>();
    int order = 1;
    
    for(Association<Integer, String> a : v) ordV.add(a.getValue());
    for(String s : ordV){
      
      for(Association<Integer, String> ass : v){
        if (ass.getValue().equals(s)){
          returnVector.add(new Association<Integer, String>(ass.getKey(), ass.getValue()));
          break;
        } } }
    printSchedule(returnVector);
  }
  
  private void printScheduleByStudent(Vector<Association<Integer, String>> v, Vector<Student> s){
    
    //ordered vector sorts students by name using comparable
    OrderedVector<Student> ov = new OrderedVector<Student>();
    for (Student student : s) ov.add(student);
    //prints each student in vector's schedule
    for(Student stud : ov){
      String[] classes = stud.getClasses();
      System.out.println(stud.getName());
      for(String str : classes){
        for (Association a : v){
          if (a.getValue().equals(str)){
            System.out.println(str + " -> " + "Slot " + a.getKey());
          } } } } }
  
  public static void main(String[] args){
    
    ExamScheduler e = new ExamScheduler();
    
    Vector<Student> students = e.parseSchedules(args[0]);
    GraphListUndirected<String, Integer> gr = e.createGraph(students);
    
    //e.printGraph(gr);
    System.out.println("\nPrinting Normal Scheduling Algorthm\n");
    e.printSchedule(e.getSchedule(gr));
    System.out.println("\nPrinting Optimal Scheduling Algorithm(Extension #3)\n");
    Vector<Association<Integer, String>> best = e.getBestSchedule(gr);
    if (best.size() == 0) System.out.println("TOO MANY CLASSES TO FIND OPTIMAL SCHEDULE!!!");
    else e.printSchedule(best);
    System.out.println("\nPrinting Schedule 'Normal Algo' In Alphabetical Order(Extension #1)\n");
    e.printOrderedSchedule(e.getSchedule(gr));
    System.out.println("\nPrinting Schedule 'Normal Algo' By Student In Alphabetical Order(Extension #2)\n");
    e.printScheduleByStudent(e.getSchedule(gr), students);
    
    
  }
  
}