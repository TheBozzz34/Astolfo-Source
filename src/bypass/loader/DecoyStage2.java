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
   public void setState(int var1) {
      this.state = var1;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static JvmArgumentsCheckSetup h(DecoyStage2 var0) {
      return var0.n;
   }

   public class azg extends StageAction {
      public azg(LoadStage var2) {
         super(var2);
      }

      @Override
      public void run() {
      }
   }

   public class r9g extends StageValue {
      public r9g(LoadStage var2) {
         super(var2);
      }

      public ItemFishFood get() {
         return (ItemFishFood)DecoyStage2.h(DecoyStage2.this).Y().V().value;
      }
   }
}
