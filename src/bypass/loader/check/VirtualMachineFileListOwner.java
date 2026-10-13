package bypass.loader.check;

import bypass.util.container.ListContainerOwner;
import java.io.File;

public class VirtualMachineFileListOwner {
   private final ListContainerOwner<File> w;

   public VirtualMachineFileListOwner() {
      VirtualMachineFiles var1 = new VirtualMachineFiles();
      this.w = new ListContainerOwner<>(var1.i());
   }

   public ListContainerOwner<File> A() {
      return this.w;
   }
}
