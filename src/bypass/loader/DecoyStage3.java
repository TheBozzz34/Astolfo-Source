package bypass.loader;

import bypass.loader.check.FileHashCheckSetup;
import net.minecraft.block.Block;

public final class DecoyStage3 extends AbstractLoadStage {
   private final FileHashCheckSetup Q = new FileHashCheckSetup();

   @Override
   public StageAction getAction() {
      return new DecoyStage3.azx(this);
   }

   @Override
   public StageValue getValue() {
      return new DecoyStage3.r9x(this);
   }

   @Override
   public void setState(int var1) {
      this.state = var1;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static FileHashCheckSetup N(DecoyStage3 var0) {
      return var0.Q;
   }

   public class azx extends StageAction {
      public azx(LoadStage var2) {
         super(var2);
      }

      @Override
      public void run() {
      }
   }

   public class r9x extends StageValue {
      public r9x(LoadStage var2) {
         super(var2);
      }

      public Block get() {
         return (Block)DecoyStage3.N(DecoyStage3.this).c().T().value;
      }
   }
}
