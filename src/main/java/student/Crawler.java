package student;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;

import game.Node;

public class Crawler implements Runnable {
  private Node exitNode;
  private Node startNode;

  private int distance = 0;
  private int gold = 0;
  private Stack<Node> route = new Stack<Node>();

  public Crawler(Node exitNode, Node startNode) {
    this.exitNode = exitNode;
    this.startNode = startNode;
  }

  /**
   * Traverses the route and calculates the total gold found and the total
   * distance travelled taking weighted edges into account.
   */
  private void calculateDistanceAndGold() {
    Node previousNode = this.startNode;
    for (Node node : this.route) {
      this.gold += node.getTile().getOriginalGold();
      this.distance += previousNode.getEdge(node).length();
      previousNode = node;
    }
  }

  /**
   * Returns the distance of the route.
   */
  public int getDistance() {
    return this.distance;
  }

  /**
   * Returns the gold found on the route.
   */
  public int getGold() {
    return this.gold;
  }

  /**
   * Returns the route.
   */
  public List<Node> getRoute() {
    return this.route;
  }

  /**
   * Added to implement the Runnable interface. At each fork, an unvisited
   * neighouring node is explored until the exit node is found. Upon
   * completion, the distance and gold for the route is calculated.
   */
  public void run() {
    ArrayList<Node> visitedNodes = new ArrayList<Node>();

    Node node = this.startNode;

    while (node != this.exitNode) {
      List<Node> options = node.getNeighbours().stream()
        .filter(option -> !visitedNodes.contains(option))
        .collect(Collectors.toList());

      if (options.size() == 0) {
        this.route.pop();
      } else {
        Random r = new Random();
        Node nextNode = options.get(r.nextInt(options.size()));
        visitedNodes.add(nextNode);
        this.route.push(nextNode);
      }

      node = this.route.peek();
    }

    this.calculateDistanceAndGold();
  }
}
