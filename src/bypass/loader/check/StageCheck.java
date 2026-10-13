package bypass.loader.check;

import bypass.event.EventPriority;

public interface StageCheck extends Comparable<StageCheck> {
   CheckResult H(CheckRequest var1);

   default EventPriority o() {
      return EventPriority.NORMAL;
   }

   int compareTo(StageCheck var1);
}
