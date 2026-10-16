package bypass.util;

import java.util.Comparator;

public class PathNodeComparator implements Comparator<PathNode> {
   public int compare(PathNode pathNode, PathNode pathNode2) {
      return (int)(pathNode.getDistanceToGoalSq() + pathNode.getTotalCost() - (pathNode2.getDistanceToGoalSq() + pathNode2.getTotalCost()));
   }
}
