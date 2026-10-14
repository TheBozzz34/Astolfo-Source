package bypass.loader;

import bypass.loader.check.JvmArgumentsCheckSetup;
import net.minecraft.item.ItemFishFood;

public final class DecoyStage2 extends AbstractLoadStage {
   private final JvmArgumentsCheckSetup n = new JvmArgumentsCheckSetup();

   @Override
   public StageAction getAction() {
      return new DecoyStage2.azg(this);
   }

   @Override
   public StageValue getValue() {
      return new DecoyStage2.r9g(this);
   }

   @Override
   public void setState(int i) {
      this.state = i;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static JvmArgumentsCheckSetup h(DecoyStage2 decoyStage2) {
      return decoyStage2.n;
   }

   public class azg extends StageAction {
      public azg(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
      }
   }

   public class r9g extends StageValue {
      public r9g(LoadStage loadStage) {
         super(loadStage);
      }

      public ItemFishFood get() {
         return (ItemFishFood)DecoyStage2.h(DecoyStage2.this).Y().V().value;
      }
   }
}
