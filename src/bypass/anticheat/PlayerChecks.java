package bypass.anticheat;

import java.util.ArrayList;
import java.util.List;

public class PlayerChecks {
   private final List<AbstractCheck> w;
   private static final long a = 37307800938274L;
   private static final long b = -3988267448078958582L;

   public PlayerChecks() {
      this.w = new ArrayList<>((int)b);
   }

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
