package bypass.loader.check;

public class VirtualMachineCheckSetup {
   private final VirtualMachineFileListOwner fileListOwner = new VirtualMachineFileListOwner();
   private final FileListHolder fileListHolder = new FileListHolder(this.fileListOwner.getFileContainer());
   private final VirtualMachineCheck virtualMachineCheck = new VirtualMachineCheck(this.fileListHolder);

   public FileListHolder getFileListHolder() {
      return this.fileListHolder;
   }

   public VirtualMachineCheck getVirtualMachineCheck() {
      return this.virtualMachineCheck;
   }
}
