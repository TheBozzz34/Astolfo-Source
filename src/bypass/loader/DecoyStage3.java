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
   public void setState(int i) {
      this.state = i;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static FileHashCheckSetup N(DecoyStage3 decoyStage3) {
      return decoyStage3.Q;
   }

   public class azx extends StageAction {
      public azx(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
      }
   }

   public class r9x extends StageValue {
      public r9x(LoadStage loadStage) {
         super(loadStage);
      }

      public Block get() {
         return (Block)DecoyStage3.N(DecoyStage3.this).c().T().value;
      }
   }
}
