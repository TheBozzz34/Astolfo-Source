package bypass.anticheat;

import java.util.ArrayList;
import java.util.List;

public class PlayerChecks {
   private final List<AbstractCheck> w = new ArrayList<>(10);

   private void f(AbstractCheck abstractCheck) {
      this.w.add(abstractCheck);
   }

   public static List P(PlayerChecks playerChecks) {
      return playerChecks.w;
   }

   public static void p(PlayerChecks playerChecks, AbstractCheck abstractCheck) {
      playerChecks.f(abstractCheck);
   }
}
