package bypass.loader.check;

import java.util.Comparator;

public abstract class AbstractStageCheck implements StageCheck {
   private static final Comparator<StageCheck> A = Comparator.comparing(StageCheck::o);

   @Override
   public final int compareTo(StageCheck var1) {
      return A.compare(this, var1);
   }

   static {
      int[] var10000 = new int[3];
   }
}
