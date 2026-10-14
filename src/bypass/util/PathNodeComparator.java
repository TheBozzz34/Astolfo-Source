package bypass.util;

import java.util.Comparator;

public class PathNodeComparator implements Comparator<PathNode> {
   public int compare(PathNode pathNode, PathNode pathNode2) {
      return (int)(pathNode.M() + pathNode.d() - (pathNode2.M() + pathNode2.d()));
   }
}
