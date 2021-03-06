package student;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Stack;

import game.EscapeState;
import game.ExplorationState;
import game.NodeStatus;
import game.Node;

public class Explorer {

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
    Stack<Integer> visited = new Stack<Integer>();
    ArrayList<NodeStatus> route = new ArrayList<NodeStatus>();

    while (state.getDistanceToTarget() != 0) {
      List<NodeStatus> options = state.getNeighbours().stream()
        .filter(node -> !route.contains(node))
        .sorted((n1, n2) -> n1.compareTo(n2))
        .collect(Collectors.toList());

      if (options.size() == 0) {
        visited.pop();
      } else {
        NodeStatus node = options.get(0);
        route.add(node);
        visited.push((int) (long) node.getId());
      }

      state.moveTo(visited.peek());
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
    List<Crawler> crawlers = new ArrayList<Crawler>();

    int threadCount = 1000;

    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    for (int i = 0; i < threadCount; i++) { 
      Crawler crawler = new Crawler(state.getExit(), state.getCurrentNode());
      crawlers.add(crawler);
      executor.execute(crawler);
    }

    executor.shutdown();

    while (!executor.isTerminated()) {}

    crawlers = crawlers.stream()
      .filter(crawler -> {
        return crawler.getRoute() != null && crawler.getDistance() <= state.getTimeRemaining();
      })
      .sorted((c1, c2) -> c2.getGold()  - c1.getGold())
      .collect(Collectors.toList());

    if (crawlers.size() == 0) {
      return;
    }

    for (Node node : crawlers.get(0).getRoute()) {
      if (state.getCurrentNode().getTile().getGold() > 0) {
        state.pickUpGold();
      }
      state.moveTo(node);
    }
  }
}
