package searchexample;

/* Finding the solution out of a maze.

   This problem illustrates searching using stacks (depth-first search)
   and queues (breadth-first search).
*/

import java.util.LinkedList;
import java.util.Stack;


public class Maze {

  static final char C = ' ';
  static final char X = 'x'; 
  static final char S = 's';
  static final char E = 'e'; 
  static final char V = '.';

  static final int START_I = 1;
  static final int START_J = 1;
  static final int END_I = 2;
  static final int END_J = 9;

  private static char[][] maze = {
    {X, X, X, X, X, X, X, X, X, X},
    {X, S, C, C, C, C, C, C, C, X},
    {X, C, C, C, X, C, X, X, C, E},
    {X, C, X, X, X, C, X, X, C, X},
    {X, C, C, C, C, X, X, X, C, X},
    {X, X, X, X, C, X, X, X, C, X},
    {X, X, X, X, C, X, C, C, C, X},
    {X, X, C, X, C, X, X, C, C, X},
    {X, X, C, C, C, C, C, C, C, X},
    {X, X, X, X, X, X, C, X, X, X}
  };

  public int size() {
    return maze.length;
  }
  
  /**
   * Print out the maze to stdout.
  */
  public void print() {
    for (int i = 0; i < size(); i++) {
      for (int j = 0; j < size(); j++) {
        System.out.print(maze[i][j]);
        System.out.print(' ');
      }
      System.out.println();
    }
  }

  /**
   * Mark the cell as "seen".
   */
  public char mark(int i, int j, char value) {
    assert isInMaze(i, j);
    
    char tmp = maze[i][j];
    maze[i][j] = value;
    return tmp;
  }

  public char mark(MazePos pos, char value) {
    return mark(pos.xcoord() , pos.ycoord() , value);
  }

  public boolean isMarked(int i, int j) {
    assert isInMaze(i, j);
    return (maze[i][j] == V);
  }

  public boolean isMarked(MazePos pos) {
    return isMarked(pos.xcoord(), pos.ycoord());
  }

  public boolean isClear(int i, int j) {
    assert isInMaze(i, j);
    return (maze[i][j] != X && maze[i][j] != V);
  }

  public boolean isClear(MazePos pos) {
    return isClear(pos.xcoord(), pos.ycoord());
  }

  //true if cell is within maze
  public boolean isInMaze(int i, int j) {
    return i >= 0 && i < size() && j >= 0 && j < size();
  }

  //true if cell is within maze
  public boolean isInMaze(MazePos pos) {
    return isInMaze(pos.xcoord(), pos.ycoord());
  }


  public boolean isFinal(int i, int j) {
    return i == Maze.END_I && j == Maze.END_J;
  }

  public boolean isFinal(MazePos pos) {
    return isFinal(pos.xcoord(), pos.ycoord());
  }

  /**
   * Provide a copy of the maze.
   */
  public char[][] clone() {
    char[][] mazeCopy = new char[size()][size()];
    for (int i = 0; i < size(); i++) {
      for (int j = 0; j < size(); j++) {
        mazeCopy[i][j] = maze[i][j];
      }
    }
    return mazeCopy;
  }

  /**
   * Restore the maze to a previous state.
   */
  public void restore(char[][] savedMaze) {
    for (int i = 0; i < size(); i++) {
      for (int j = 0; j < size(); j++) {
        maze[i][j] = savedMaze[i][j];
      }
    }
  }

  /**
   * Sample usage of the routines.
   */
  public static void main(String[] args) {

    Maze maze = new Maze();
    maze.print();

    System.out.println("\n\nFind a path using a stack: ");
    maze.solveStack();

    System.out.println("\n\nFind a path using a queue: ");
    maze.solveQueue();

    System.out.println("\n\nFind a path recursively: ");
    maze.solveRec();

  }


  //THE GOAL IS TO FIND A PATH FROM START TO END

  /**
   * This solution uses a stack to keep track of possible
   * states/positions to explore; it marks the maze to remember the
   * positions that it's already explored.
   */
  public void solveStack() {

    //save the maze
    char[][] savedMaze;

    //declare the locations stack
    Stack<MazePos> candidates = new Stack<>();

    MazePos crt;
    MazePos next;

    //insert the start
    candidates.push(new MazePos(START_I, START_J));

    savedMaze = clone();

    while (!candidates.empty()) {

      //get current position
      crt = candidates.pop();

      if (isFinal(crt)) {
        break;
      }

      //mark the current position
      mark(crt, V);

      //put its neighbors in the queue
      next = crt.north();
      if (isInMaze(next) && isClear(next)) {
        candidates.push(next);
      }
      next = crt.east();
      
      if (isInMaze(next) && isClear(next)) { 
        candidates.push(next);
      }
      next = crt.west();
      
      if (isInMaze(next) && isClear(next)) {
        candidates.push(next);
      }
      next = crt.south();
      
      if (isInMaze(next) && isClear(next)) {
        candidates.push(next);
      }
    }

    if (!candidates.empty()) {
      System.out.println("Found a solution: ");
    } else {
      System.out.println("You're stuck in the maze!");
    }
    print();

    //restore the maze
    restore(savedMaze);
  }

  /** 
   * This solution uses a QUEUE to keep track of possible
   * states/positions to explore; it marks the maze to remember the
   * positions that it's already explored.
   */
  public void solveQueue() {

    //save the maze
    char[][] savedMaze;

    //declare the locations stack
    LinkedList<MazePos> candidates = new LinkedList<>();

    MazePos crt;
    MazePos next;

    //insert the start
    candidates.add(new MazePos(START_I, START_J));


    savedMaze = clone();

    while (!candidates.isEmpty()) {

      //get current position
      crt = candidates.removeFirst();

      if (isFinal(crt)) {
        break;
      }

      //mark the current position
      mark(crt, V);

      //put its neighbors in the queue
      next = crt.north();
      if (isInMaze(next) && isClear(next)) {
        candidates.add(next);
      }
      next = crt.east();
      if (isInMaze(next) && isClear(next)) {
        candidates.add(next);
      }
      next = crt.west();
      if (isInMaze(next) && isClear(next)) {
        candidates.add(next);
      }
      next = crt.south();
      if (isInMaze(next) && isClear(next)) {
        candidates.add(next);
      }
    }

    if (!candidates.isEmpty()) {
      System.out.println("Found a solution: ");
    } else { 
      System.out.println("You're stuck in the maze!");
    }
    print();

    //restore the maze
    restore(savedMaze);
  }

  /**
   * Solve using recursion. 
   * Note: this solution unmarks the path upon reaching
   * dead ends, so in the end only the path is left marked. It is
   * possible to write a solution that does not un-mark its traces,
   * but instead it clones and restores the maze.
   */
  public void solveRec() {
    if (solve(new MazePos(START_I, START_J))) {
      System.out.println("Found a solution: ");
    } else {
      System.out.println("You're stuck in the maze.");
    }
    print();
  }


  /** 
   * Find a path to exit the maze from this position. Works
   * recursively, by advancing to a neighbor and continuing from
   * there. If a path is found, return true. Otherwise return false.
   */
  public boolean solve(MazePos pos) {
    //base case
    if (!isInMaze(pos)) {
      return false;
    }
    if (isFinal(pos)) {
      return true;
    }
    if (!isClear(pos)) {
      return false;
    }

    //current position must be clear
    assert isClear(pos);

    //recurse

    //first mark this  location
    mark(pos, V);

    //try to go south
    if (solve(pos.south())) {
      //we found a solution going south: if we want to leave the
      //maze clean, then unmark current cell and return; if we
      //want to mark the path in the maze, then don't unmark
      //mark(pos, C);
      return true;
    }

    //else west
    if (solve(pos.west())) {
      //we found a solution going west: if we want to leave the
      //maze clean, then unmark current cell and return; if we
      //want to mark the path in the maze, then don't unmark
      //return
      //mark(pos, C);
      return true;
    }

    //else north
    if (solve(pos.north())) {
      //we found a solution going north: if we want to leave the
      //maze clean, then unmark current cell and return; if we
      //want to mark the path in the maze, then don't unmark
      //return
      // mark(pos, C);
      return true;
    }

    //else east
    if (solve(pos.east())) {
      //we found a solution going east: if we want to leave the
      //maze clean, then unmark current cell and return; if we
      //want to mark the path in the maze, then don't unmark
      //return
      //mark(pos, C);
      return true;
    }

    //unmark all dead ends; since it was marked, the position must
    //have been clear
    mark(pos, C);

    //if none of the above returned, then there is no solution
    return false;
  }

  class MazePos {
    private int xcoord;
    private int ycoord;

    public MazePos(int i, int j) {
      xcoord = i;
      ycoord = j;
    }
 
    public int xcoord()  { 
      return xcoord;
    }

    public int ycoord()  { 
      return ycoord;
    }

    public void print() {
      System.out.println("(" + xcoord()  + "," + ycoord()  + ")");
    }

    public MazePos north() {
      return new MazePos(xcoord - 1, ycoord);
    }

    public MazePos south() {
      return new MazePos(xcoord + 1, ycoord);
    }

    public MazePos east() {
      return new MazePos(xcoord, ycoord + 1);
    }

    public MazePos west() {
      return new MazePos(xcoord, ycoord - 1);
    }
  }
}
