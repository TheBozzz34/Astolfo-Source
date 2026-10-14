package bypass.loader;

import bypass.loader.check.VirtualMachineCheckSetup;

public final class DecoyStage1 extends AbstractLoadStage {
   private final VirtualMachineCheckSetup L = new VirtualMachineCheckSetup();

   @Override
   public StageValue getValue() {
      return new DecoyStage1.r9i(this);
   }

   @Override
   public StageAction getAction() {
      return new DecoyStage1.azi(this);
   }

   public VirtualMachineCheckSetup y() {
      return this.L;
   }

   @Override
   public void setState(int i) {
      this.state = i;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static VirtualMachineCheckSetup J(DecoyStage1 decoyStage1) {
      return decoyStage1.L;
   }

   public class azi extends StageAction {
      private static final long c = 44304318223068L;

      public azi(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
         if (DecoyStage1.this.getState() == 1) {
            ;
         }
      }
   }

   public class r9i extends StageValue {
      public r9i(LoadStage loadStage) {
         super(loadStage);
      }

      public NullPointerException get() {
         return (NullPointerException)DecoyStage1.J(DecoyStage1.this).u().U().value;
      }
   }
}
