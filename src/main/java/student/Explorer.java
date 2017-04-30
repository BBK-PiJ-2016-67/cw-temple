package student;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Stack;
import java.util.Random;
import java.util.Set;

import game.EscapeState;
import game.ExplorationState;
import game.NodeStatus;
import game.Node;
import game.Tile;

public class Explorer {

  private Stack<Integer> visitedIds = new Stack<Integer>();
  private ArrayList<NodeStatus> visited = new ArrayList<NodeStatus>();

  private List<NodeStatus> getUnvisited(ExplorationState state) {
    return state.getNeighbours().stream().filter(node -> {
      return !this.visited.contains(node);
    }).sorted((n1, n2) -> {
      return Long.compare(n1.getDistanceToTarget(), n2.getDistanceToTarget());
    }).collect(Collectors.toList());
  }

  private void retraceSteps(ExplorationState state) {
    this.visitedIds.pop();
    while (true) {
      if (this.getUnvisited(state).size() > 0) {
        this.visitedIds.push((int) (long) state.getCurrentLocation());
        return;
      }
      state.moveTo(this.visitedIds.pop());
    }
  }

  /**
   * Explore the cavern, trying to find the orb in as few steps as possible.
   * Once you find the orb, you must return from the function in order to pick
   * it up. If you continue to move after finding the orb rather
   * than returning, it will not count.
   * If you return from this function while not standing on top of the orb,
   * it will count as a failure.
   *   
   * <p>There is no limit to how many steps you can take, but you will receive
   * a score bonus multiplier for finding the orb in fewer steps.</p>
   * 
   * <p>At every step, you only know your current tile's ID and the ID of all
   * open neighbor tiles, as well as the distance to the orb at each of these tiles
   * (ignoring walls and obstacles).</p>
   * 
   * <p>To get information about the current state, use functions
   * getCurrentLocation(),
   * getNeighbours(), and
   * getDistanceToTarget()
   * in ExplorationState.
   * You know you are standing on the orb when getDistanceToTarget() is 0.</p>
   *
   * <p>Use function moveTo(long id) in ExplorationState to move to a neighboring
   * tile by its ID. Doing this will change state to reflect your new position.</p>
   *
   * <p>A suggested first implementation that will always find the orb, but likely won't
   * receive a large bonus multiplier, is a depth-first search.</p>
   *
   * @param state the information available at the current state
   */
  public void explore(ExplorationState state) {
    while (true) {
      if (state.getDistanceToTarget() == 0) {
        return;
      }
      List<NodeStatus> nodes = this.getUnvisited(state);
      if (nodes.size() == 0) {
        this.retraceSteps(state);
        continue;
      }
      NodeStatus node = nodes.get(0);
      long id = node.getId();
      this.visited.add(node);
      this.visitedIds.push((int) (long) id);
      state.moveTo(id);
    }
  }

  private ArrayList<Node> optimalRoute = new ArrayList<Node>();
  private int optimalRouteTotalGold = 0;

  private void fork(long timeout, int limit, Node exit, Node node, ArrayList<Node> route, int gold, int distance) {
    if (System.currentTimeMillis() >= timeout || distance > limit) {
      return;
    }
    // Add node to route
    route.add(node);
    // Check if Terry has reached the exit
    if (node.equals(exit)) {
      if (optimalRouteTotalGold == 0 || gold > optimalRouteTotalGold) {
        this.optimalRouteTotalGold = gold;
        this.optimalRoute = route;
      }
      return;
    }
    // Get neighbours that haven't already been visited
    // Sort by most gold
    List<Node> neighbours = node.getNeighbours().stream().filter(n -> {
      return !route.contains(n);
    }).sorted((n1, n2) -> {
      return Integer.compare(n2.getTile().getGold(), n1.getTile().getGold());
    }).collect(Collectors.toList());

    // Dead end
    if (neighbours.size() == 0) {
      return;
    }

    // Add gold to total
    gold += node.getTile().getGold();

    for (Node neighbour : neighbours) {
      int weight = node.getEdge(neighbour).length();
      ArrayList<Node> newRoute = new ArrayList<Node>(route);
      this.fork(timeout, limit, exit, neighbour, newRoute, gold, distance + weight);
    }
  }

  /**
   * Escape from the cavern before the ceiling collapses, trying to collect as much
   * gold as possible along the way. Your solution must ALWAYS escape before time runs
   * out, and this should be prioritized above collecting gold.
   *
   * <p>You now have access to the entire underlying graph, which can be accessed 
   * through EscapeState.
   * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
   * will return a collection of all nodes on the graph.</p>
   * 
   * <p>Note that time is measured entirely in the number of steps taken, and for each step
   * the time remaining is decremented by the weight of the edge taken. You can use
   * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
   * on your current tile (this will fail if no such gold exists), and moveTo() to move
   * to a destination node adjacent to your current node.</p>
   * 
   * <p>You must return from this function while standing at the exit. Failing to do so before time
   * runs out or returning from the wrong location will be considered a failed run.</p>
   * 
   * <p>You will always have enough time to escape using the shortest path from the starting
   * position to the exit, although this will not collect much gold.</p>
   *
   * @param state the information available at the current state
   */
  public void escape(EscapeState state) {
    long timeout = System.currentTimeMillis() + 10000L;

    this.fork(timeout, state.getTimeRemaining(), state.getExit(), state.getCurrentNode(), new ArrayList<Node>(), 0, 0);

    for (Node node : this.optimalRoute) {
      Node current = state.getCurrentNode();
      if (current.getTile().getGold() > 0) {
        state.pickUpGold();
      }
      if (node.equals(current)) {
        continue;
      }
      state.moveTo(node);
    }
  }
}
