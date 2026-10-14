package bypass.loader.check;

import bypass.util.container.ListContainerOwner;
import java.io.File;

public class VirtualMachineFileListOwner {
   private final ListContainerOwner<File> w;

   public VirtualMachineFileListOwner() {
      VirtualMachineFiles virtualMachineFiles = new VirtualMachineFiles();
      this.w = new ListContainerOwner<>(virtualMachineFiles.i());
   }

   public ListContainerOwner<File> A() {
      return this.w;
   }
}
