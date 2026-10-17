package bypass.loader;

import bypass.loader.check.VirtualMachineCheckSetup;

public final class DecoyStage1 extends AbstractLoadStage {
   private final VirtualMachineCheckSetup virtualMachineCheckSetup = new VirtualMachineCheckSetup();

   @Override
   public StageValue getValue() {
      return new DecoyStage1.VirtualMachineCheckValue(this);
   }

   @Override
   public StageAction getAction() {
      return new DecoyStage1.DecoyOneAction(this);
   }

   public VirtualMachineCheckSetup getVirtualMachineCheckSetup() {
      return this.virtualMachineCheckSetup;
   }

   @Override
   public void setState(int i) {
      this.state = i;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static VirtualMachineCheckSetup accessVirtualMachineCheckSetup(DecoyStage1 decoyStage1) {
      return decoyStage1.virtualMachineCheckSetup;
   }

   public class DecoyOneAction extends StageAction {
      public DecoyOneAction(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
         if (DecoyStage1.this.getState() == 1) {
            ;
         }
      }
   }

   public class VirtualMachineCheckValue extends StageValue {
      public VirtualMachineCheckValue(LoadStage loadStage) {
         super(loadStage);
      }

      public NullPointerException get() {
         return (NullPointerException)DecoyStage1.accessVirtualMachineCheckSetup(DecoyStage1.this).getVirtualMachineCheck().checkVirtualMachine().value;
      }
   }
}
