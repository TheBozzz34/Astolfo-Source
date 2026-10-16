package bypass.anticheat;

import java.util.ArrayList;
import java.util.List;

public class PlayerChecks {
   private final List<AbstractCheck> checks = new ArrayList<>(10);

   private void addCheck(AbstractCheck abstractCheck) {
      this.checks.add(abstractCheck);
   }

   public static List getChecks(PlayerChecks playerChecks) {
      return playerChecks.checks;
   }

   public static void addCheckTo(PlayerChecks playerChecks, AbstractCheck abstractCheck) {
      playerChecks.addCheck(abstractCheck);
   }
}
