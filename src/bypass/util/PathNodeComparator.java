package bypass.util;

import java.util.Comparator;

public class PathNodeComparator implements Comparator<PathNode> {
   public int compare(PathNode var1, PathNode var2) {
      return (int)(var1.M() + var1.d() - (var2.M() + var2.d()));
   }
}
