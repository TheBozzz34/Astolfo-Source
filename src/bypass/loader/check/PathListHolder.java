package bypass.loader.check;

import bypass.util.container.ListContainerOwner;
import java.nio.file.Path;

public class PathListHolder {
   private final ListContainerOwner<Path> I;

   public PathListHolder(ListContainerOwner<Path> var1) {
      this.I = var1;
   }

   public ListContainerOwner<Path> W() {
      return this.I;
   }
}
