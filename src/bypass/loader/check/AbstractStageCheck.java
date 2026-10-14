package bypass.loader.check;

import java.util.Comparator;

public abstract class AbstractStageCheck implements StageCheck {
   private static final Comparator<StageCheck> A = Comparator.comparing(StageCheck::o);

   @Override
   public final int compareTo(StageCheck stageCheck) {
      return A.compare(this, stageCheck);
   }

   static {
      int[] ints = new int[3];
   }
}
