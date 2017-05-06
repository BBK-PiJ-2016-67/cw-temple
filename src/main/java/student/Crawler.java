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

  public int gold = 0;
  public int distance = 0;
  public List<Node> route;

  public Crawler(Node exitNode, Node startNode) {
    this.exitNode = exitNode;
    this.startNode = startNode;
  }

  public void run() {
    Stack<Node> visited = new Stack<Node>();
    ArrayList<Node> route = new ArrayList<Node>();
    Node node = this.startNode;
    while (node != this.exitNode) {
      List<Node> options = node.getNeighbours().stream()
        .filter(option -> !route.contains(option))
        .collect(Collectors.toList());

      if (options.size() == 0) {
        visited.pop();
      } else {
        Random r = new Random();
        Node nextNode = options.get(r.nextInt(options.size()));
        route.add(nextNode);
        visited.push(nextNode);
      }

      node = visited.peek();
    }

    for (int i = 0; i < visited.size(); i++) {
      Node visitedNode = visited.get(i);
      this.gold += visitedNode.getTile().getOriginalGold();
      if (i == 0) {
        this.distance += this.startNode.getEdge(visitedNode).length();
        continue;
      }
      this.distance += visited.get(i - 1).getEdge(visitedNode).length();
    }

    this.route = visited;
  }
}
