package bypass.loader.check;

import bypass.event.EventPriority;

public interface StageCheck extends Comparable<StageCheck> {
   CheckResult check(CheckRequest checkRequest);

   default EventPriority getPriority() {
      return EventPriority.NORMAL;
   }

   int compareTo(StageCheck stageCheck);
}
