package student;

import java.util.List;
import game.Node;

public interface Route {
  int getDistance();

  int getGold();

  List<Node> getNodes();

  List<Node> getUnvisitedNodes();

  boolean hasVisitedNode(Node node);

  void visitNode(Node node);
}
