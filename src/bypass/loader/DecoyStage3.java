package bypass.loader;

import bypass.loader.check.FileHashCheckSetup;
import net.minecraft.block.Block;

public final class DecoyStage3 extends AbstractLoadStage {
   private final FileHashCheckSetup fileHashCheckSetup = new FileHashCheckSetup();

   @Override
   public StageAction getAction() {
      return new DecoyStage3.DecoyThreeAction(this);
   }

   @Override
   public StageValue getValue() {
      return new DecoyStage3.FileHashCheckValue(this);
   }

   @Override
   public void setState(int i) {
      this.state = i;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static FileHashCheckSetup accessFileHashCheckSetup(DecoyStage3 decoyStage3) {
      return decoyStage3.fileHashCheckSetup;
   }

   public class DecoyThreeAction extends StageAction {
      public DecoyThreeAction(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
      }
   }

   public class FileHashCheckValue extends StageValue {
      public FileHashCheckValue(LoadStage loadStage) {
         super(loadStage);
      }

      public Block get() {
         return (Block)DecoyStage3.accessFileHashCheckSetup(DecoyStage3.this).getFileHashCheck().checkFileHash().value;
      }
   }
}
