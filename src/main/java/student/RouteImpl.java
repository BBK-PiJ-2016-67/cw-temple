package student;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import game.Node;

public class RouteImpl implements Route {
  private int distance = 0;
  private int gold = 0;
  private List<Node> nodes = new ArrayList<Node>();

  public RouteImpl() {}

  public RouteImpl(Route route) {
    this.distance = route.getDistance();
    this.gold = route.getGold();  
    this.nodes = new ArrayList<Node>(route.getNodes());
  }

  public int getDistance() {
    return this.distance;
  }

  public int getGold() {
    return this.gold;
  }

  public List<Node> getNodes() {
    return this.nodes;
  }

  private Node getLastNode() {
    if (this.nodes.size() == 0) {
      return null;
    }
    return this.nodes.get(this.nodes.size() - 1);
  }

  public List<Node> getUnvisitedNodes() {
    return this.getLastNode().getNeighbours().stream().filter(node -> {
      return !this.nodes.contains(node);
    }).collect(Collectors.toList());
  }

  public boolean hasVisitedNode(Node node) {
    return this.nodes.contains(node);
  }

  public void visitNode(Node node) {
    Node lastVisitedNode = this.getLastNode();
    if (lastVisitedNode != null) {
      this.distance += lastVisitedNode.getEdge(node).length();
    }
    this.gold += node.getTile().getGold();
    this.nodes.add(node);
  }
}
