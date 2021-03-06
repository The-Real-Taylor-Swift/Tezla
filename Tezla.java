/* [Tezla.java]
 * A program that, given a map for input, outputs fastest route and the route that takes the least battery.
 * Author: Kailas Moon
 * Date: November 6th, 2017
 * */

//Import statements
import java.io.*;
import java.util.Scanner;
import java.util.*;

public class Tezla {
  
  public static String[][] originalMap; //2D arry of original Map
  public static String[][] temporaryMapShortestPath; //2D arrays of maps I will use and modify to calculate the paths
  public static String[][] temporaryMapMostBattery;
  
  //MAIN METHOD
  public static void main(String[] args) throws Exception {
    
    //Declaring variables
    Scanner userInput = new Scanner(System.in);
    System.out.println("Enter the name of the map's file.");
    String inputFileName = userInput.next();
    File inputFile = new File(inputFileName);
    File outputFile = new File("SHORTEST ROUTE.txt");
    File outputFile2 = new File("MOST BATTERY.txt");
    Scanner input = new Scanner(inputFile);
    PrintWriter output = new PrintWriter(outputFile);
    PrintWriter output2 = new PrintWriter(outputFile2);
    int rows = Integer.parseInt(input.nextLine());
    int columns = Integer.parseInt(input.nextLine());
    int battery = Integer.parseInt(input.nextLine());
    originalMap = new String[rows][columns];
    temporaryMapShortestPath = new String[rows][columns];
    temporaryMapMostBattery = new String[rows][columns];
    
    //Updating my three 2D arrays with the values from the file
    String nextLine;
    for ( int i = 0; i < originalMap.length; i ++) {
      nextLine = input.nextLine();   
      for ( int c = 0; c < originalMap[i].length; c++ ) {
        originalMap[i][c] = temporaryMapShortestPath[i][c] = temporaryMapMostBattery[i][c] = nextLine.substring(c,c+1);
      }
    }
    //displayMap(originalMap); // Would not recommend printing large maps to console :)
    //displayMap(temporaryMapShortestPath);
    
    /* EXTENSION 1*/
    Point p = shortestPath(0,1, battery); //Find the shortest path
    int shortestPathBattery = p.battery;
    System.out.println("Battery remaining after shortest path: " + shortestPathBattery);
    
    //Backtrack and add stars to the 2D array
    while(p.getPreviousPoint() != null) {
      originalMap[p.pointDepth][p.pointWidth] = "*";
      p = p.getPreviousPoint();
    }
    //Write to file
    for ( int i = 0; i < originalMap.length; i ++) {  
      for ( int c = 0; c < originalMap[i].length; c++ ) {
        output.print(originalMap[i][c]);
      }
      output.println();
    }
    
    /* EXTENSION 2*/
    queue.clear(); //Clear queue
    p = mostBattery(0,1,battery,shortestPathBattery);
    System.out.println("Battery remaining after path with the most battery: " + p.battery);
    
    //Backtrack and add stars to the 2D array
    while(p.getPreviousPoint() != null) {
      temporaryMapMostBattery[p.pointDepth][p.pointWidth] = "*";
      p = p.getPreviousPoint();
    }
    //Write to file
    for ( int i = 0; i < temporaryMapMostBattery.length; i ++) {  
      for ( int c = 0; c < temporaryMapMostBattery[i].length; c++ ) {
        output2.print(temporaryMapMostBattery[i][c]);
      }
      output2.println();
    }
    
    System.out.println("Done!");
    
    //Closing scanners and printwriters
    userInput.close();
    input.close();
    output.close();
    output2.close();
  } //End of main
  
  //METHOD to display map
  public static void displayMap(String[][] inputMap) {
    for (int i = 0; i < temporaryMapShortestPath.length; i++) {
      for (int j = 0; j < temporaryMapShortestPath[0].length; j++) {
        System.out.print(inputMap[i][j]);
      }
      System.out.println();
    }
  }
  
  //CONSTRUCTOR CLASS    
  public static class Point {
    int pointDepth;
    int pointWidth;
    Point previousPoint;
    int battery;
    
    public Point(int pointDepth, int pointWidth, Point previousPoint, int battery) {
      this.pointDepth = pointDepth;
      this.pointWidth = pointWidth;
      this.previousPoint = previousPoint;
      this.battery = battery;
    }
    
    public Point getPreviousPoint() {
      return this.previousPoint;
    }
  } //End of class
  
  public static Queue<Point> queue = new LinkedList<Point>(); // Creates a queue of points
  
  //METHOD to find shortest path
  public static Point shortestPath(int x, int y, int battery) {
    
    queue.add(new Point(x,y, null, battery));
    
    while(!queue.isEmpty()) {
      Point p = queue.remove(); //This retrieves the point at the top of the queue and removes it from the queue
      
      if (temporaryMapShortestPath[p.pointDepth][p.pointWidth].equals("D")) { //Once a path reached the end, return that path. The one that reaches the end the earliest is the fastest.
        return p;
      }
      //If adjacent spots are free, add those points to the queue.
      if(isOpen(p.pointDepth+1,p.pointWidth,temporaryMapShortestPath)) { 
        Point nextP = new Point(p.pointDepth+1,p.pointWidth, p, p.battery-1);
        queue.add(nextP);
      }
      if(isOpen(p.pointDepth-1,p.pointWidth,temporaryMapShortestPath)) {
        Point nextP = new Point(p.pointDepth-1,p.pointWidth, p, p.battery-1);
        queue.add(nextP);
      }
      if(isOpen(p.pointDepth,p.pointWidth+1,temporaryMapShortestPath)) {
        Point nextP = new Point(p.pointDepth,p.pointWidth+1, p, p.battery-1);
        queue.add(nextP);
      }
      if(isOpen(p.pointDepth,p.pointWidth-1,temporaryMapShortestPath)) {
        Point nextP = new Point(p.pointDepth,p.pointWidth-1, p, p.battery-1);
        queue.add(nextP);
      }             
      temporaryMapShortestPath[p.pointDepth][p.pointWidth] = "#"; // Blocks off the visited point for efficiency. The reasoning: If a point has been reached, then any other path that gets to the same point at a later time is inefficient and does not need to be calculated.
    }
    return null;
  } //End of method
  
  //METHOD that finds the path that leaves you with the most battery
  public static Point mostBattery(int x, int y, int battery, int shortestPathBattery) {
    
    queue.add(new Point(x,y, null, battery));
    Point output = new Point(1,1,null,shortestPathBattery); //Start with the battery that was remaining after the shortest path, because that is the greatest guranteed amount of battery left
    
    while(!queue.isEmpty()) {
      Point p = queue.remove();
      
      if (p.battery >= output.battery) { //Once a path has spent more battery than the point output, it discards it.
        if (temporaryMapMostBattery[p.pointDepth][p.pointWidth].equals("C")) { //If a point is on a charging station, increment battery by 5 and remove charging station.
          p.battery +=5;
          temporaryMapMostBattery[p.pointDepth][p.pointWidth] = " ";
        }
        if (temporaryMapMostBattery[p.pointDepth][p.pointWidth].equals("D")) { //If a point hits the end with more battery, that is the new output for this method.
          output = p;
        }
        //If there is an adjacent spot, move into it.
        if(isOpen(p.pointDepth+1,p.pointWidth,temporaryMapMostBattery)) { 
          Point nextP = new Point(p.pointDepth+1,p.pointWidth, p, p.battery-1);
          queue.add(nextP);
        }
        if(isOpen(p.pointDepth-1,p.pointWidth,temporaryMapMostBattery)) {
          Point nextP = new Point(p.pointDepth-1,p.pointWidth, p, p.battery-1);
          queue.add(nextP);
        }
        if(isOpen(p.pointDepth,p.pointWidth+1,temporaryMapMostBattery)) {
          Point nextP = new Point(p.pointDepth,p.pointWidth+1, p, p.battery-1);
          queue.add(nextP);
        }
        if(isOpen(p.pointDepth,p.pointWidth-1,temporaryMapMostBattery)) {
          Point nextP = new Point(p.pointDepth,p.pointWidth-1, p, p.battery-1);
          queue.add(nextP);
        }
      }
    }
    return output;
  } //End of method
  
  //METHOD to check if map[pointDepth][pointWidth] is unoccupied
  public static boolean isOpen(int pointDepth, int pointWidth,String[][] inputMap) {
    if((pointDepth >= 0 && pointDepth < inputMap.length) && (pointWidth >= 0 && pointWidth < inputMap[pointDepth].length) && (inputMap[pointDepth][pointWidth].equals(" ") || inputMap[pointDepth][pointWidth].equals("D") || inputMap[pointDepth][pointWidth].equals("C") )) {
      return true;
    }
    return false;
  } //End of method
  
} //End of class