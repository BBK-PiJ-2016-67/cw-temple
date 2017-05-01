package student;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Stack;

import game.EscapeState;
import game.ExplorationState;
import game.NodeStatus;
import game.Node;

public class Explorer {

  private Stack<Integer> visitedIds = new Stack<Integer>();
  private ArrayList<NodeStatus> visited = new ArrayList<NodeStatus>();

  private List<NodeStatus> getOptions(ExplorationState state) {
    return state.getNeighbours().stream().filter(node -> {
      return !this.visited.contains(node);
    }).sorted((n1, n2) -> {
      return n1.compareTo(n2);
    }).collect(Collectors.toList());
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
      List<NodeStatus> options = this.getOptions(state);
      if (options.size() == 0) {
        this.visitedIds.pop();
      } else {
        NodeStatus node = options.get(0);
        this.visited.add(node);
        this.visitedIds.push((int) (long) node.getId());
      }
      state.moveTo(this.visitedIds.peek());
    }
  }

  private Route getOptimalRoute(long timeout, int limit, Node exit, Route route) {
    // Timeout
    if (System.currentTimeMillis() >= timeout) {
      return null;
    }

    List<Node> unvisitedNodes = route.getUnvisitedNodes();

    // Dead end
    if (unvisitedNodes.size() == 0) {
      return null;
    }

    List<Route> routes = new ArrayList<Route>();

    // DO threads here

    for (Node neighbour : unvisitedNodes) {
      Route newRoute = new RouteImpl(route);
      newRoute.visitNode(neighbour);
      if (newRoute.getDistance() > limit) {
        continue;
      } else if (newRoute.hasVisitedNode(exit)) {
        routes.add(newRoute);
        continue;
      }
      routes.add(this.getOptimalRoute(timeout, limit, exit, newRoute));
    }

    // Filter out dead ends, sort by most gold
    routes = routes.stream().filter(r -> {
      return r != null && r.hasVisitedNode(exit);
    }).sorted((r1, r2) -> {
      return Integer.compare(r2.getGold(), r1.getGold());
    }).collect(Collectors.toList());

    if (routes.size() == 0) {
      return null;
    }

    return routes.get(0);
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
    Route route = new RouteImpl();
    route.visitNode(state.getCurrentNode());

    long timeout = System.currentTimeMillis() + 10000L;

    Route optimalRoute = this.getOptimalRoute(timeout, state.getTimeRemaining(), state.getExit(), route);

    if (optimalRoute == null) {
      System.out.println("COULD NOT FIND ROUTE");
      return;
    }

    for (Node node : optimalRoute.getNodes()) {
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
