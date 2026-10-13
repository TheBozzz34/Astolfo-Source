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

   private void f(AbstractCheck var1) {
      this.w.add(var1);
   }

   public static List P(PlayerChecks var0) {
      return var0.w;
   }

   public static void p(PlayerChecks var0, AbstractCheck var1) {
      var0.f(var1);
   }
}
