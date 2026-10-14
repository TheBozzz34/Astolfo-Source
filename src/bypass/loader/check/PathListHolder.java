package bypass.loader.check;

import bypass.util.container.ListContainerOwner;
import java.nio.file.Path;

public class PathListHolder {
   private final ListContainerOwner<Path> I;

   public PathListHolder(ListContainerOwner<Path> listContainerOwner) {
      this.I = listContainerOwner;
   }

   public ListContainerOwner<Path> W() {
      return this.I;
   }
}
