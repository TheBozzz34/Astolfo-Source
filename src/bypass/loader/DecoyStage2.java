package bypass.loader;

import bypass.loader.check.JvmArgumentsCheckSetup;
import net.minecraft.item.ItemFishFood;

public final class DecoyStage2 extends AbstractLoadStage {
   private final JvmArgumentsCheckSetup jvmArgumentsCheckSetup = new JvmArgumentsCheckSetup();

   @Override
   public StageAction getAction() {
      return new DecoyStage2.DecoyTwoAction(this);
   }

   @Override
   public StageValue getValue() {
      return new DecoyStage2.JvmArgumentsCheckValue(this);
   }

   @Override
   public void setState(int i) {
      this.state = i;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static JvmArgumentsCheckSetup getJvmArgumentsCheckSetup(DecoyStage2 decoyStage2) {
      return decoyStage2.jvmArgumentsCheckSetup;
   }

   public class DecoyTwoAction extends StageAction {
      public DecoyTwoAction(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
      }
   }

   public class JvmArgumentsCheckValue extends StageValue {
      public JvmArgumentsCheckValue(LoadStage loadStage) {
         super(loadStage);
      }

      public ItemFishFood get() {
         return (ItemFishFood)DecoyStage2.getJvmArgumentsCheckSetup(DecoyStage2.this).getJvmArgumentsCheck().checkJvmArguments().value;
      }
   }
}
