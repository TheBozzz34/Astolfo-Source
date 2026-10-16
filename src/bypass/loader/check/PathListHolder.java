package bypass.loader.check;

import bypass.util.container.ListContainerOwner;
import java.nio.file.Path;

public class PathListHolder {
   private final ListContainerOwner<Path> pathContainer;

   public PathListHolder(ListContainerOwner<Path> listContainerOwner) {
      this.pathContainer = listContainerOwner;
   }

   public ListContainerOwner<Path> getPathContainer() {
      return this.pathContainer;
   }
}
