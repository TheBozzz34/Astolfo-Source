package bypass.loader.check;

import bypass.util.container.ListContainerOwner;
import java.io.File;

public class FileListHolder {
   private final ListContainerOwner<File> fileContainer;

   public FileListHolder(ListContainerOwner<File> listContainerOwner) {
      this.fileContainer = listContainerOwner;
   }

   public ListContainerOwner<File> getFileContainer() {
      return this.fileContainer;
   }
}
