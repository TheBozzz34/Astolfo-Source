package bypass.loader.check;

public class VirtualMachineCheckSetup {
   private final VirtualMachineFileListOwner B = new VirtualMachineFileListOwner();
   private final FileListHolder f = new FileListHolder(this.B.A());
   private final VirtualMachineCheck o = new VirtualMachineCheck(this.f);

   public FileListHolder Y() {
      return this.f;
   }

   public VirtualMachineCheck u() {
      return this.o;
   }
}
