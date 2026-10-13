package bypass.loader.check;

import bypass.util.container.ListContainerOwner;
import java.io.File;

public class FileListHolder {
   private final ListContainerOwner<File> o;

   public FileListHolder(ListContainerOwner<File> var1) {
      this.o = var1;
   }

   public ListContainerOwner<File> v() {
      return this.o;
   }
}
