package bypass.loader.check;

import bypass.util.container.ListContainerOwner;
import java.io.File;

public class VirtualMachineFileListOwner {
   private final ListContainerOwner<File> fileContainer;

   public VirtualMachineFileListOwner() {
      VirtualMachineFiles virtualMachineFiles = new VirtualMachineFiles();
      this.fileContainer = new ListContainerOwner<>(virtualMachineFiles.getFiles());
   }

   public ListContainerOwner<File> getFileContainer() {
      return this.fileContainer;
   }
}
