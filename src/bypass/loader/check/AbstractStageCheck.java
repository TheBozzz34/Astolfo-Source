package bypass.loader.check;

import java.util.Comparator;

public abstract class AbstractStageCheck implements StageCheck {
   private static final Comparator<StageCheck> priorityComparator = Comparator.comparing(StageCheck::getPriority);

   @Override
   public final int compareTo(StageCheck stageCheck) {
      return priorityComparator.compare(this, stageCheck);
   }

   static {
      int[] ints = new int[3];
   }
}
