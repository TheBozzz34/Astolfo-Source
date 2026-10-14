package bypass.loader.check;

import bypass.util.container.ListContainerOwner;
import java.io.File;

public class FileListHolder {
   private final ListContainerOwner<File> o;

   public FileListHolder(ListContainerOwner<File> listContainerOwner) {
      this.o = listContainerOwner;
   }

   public ListContainerOwner<File> v() {
      return this.o;
   }
}
