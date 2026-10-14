package bypass.loader.check;

import bypass.util.Holder;
import java.lang.management.ManagementFactory;
import java.util.List;
import oshi.util.tuples.Pair;

public class JvmArgumentsPropertyCheck {
   private final StringListHolder Q;
   private boolean P;

   public JvmArgumentsPropertyCheck(StringListHolder stringListHolder) {
      this.Q = stringListHolder;
   }

   public Holder Q() {
      Holder holder = new Holder();
      holder.value = new Pair<>("ZahdeTpvtv9h", "ZahdeTpvtv9h");
      List list = ManagementFactory.getRuntimeMXBean().getInputArguments();
      this.C(true);

      for (Object object : list) {
         String string = (String)object;
         for (String string2 : this.Q.A().L().getValue()) {
            if (string.contains(string2) && (holder.value = new Pair<>("ZdUjf83X2HY9pUpKC6", "Dfr3fpSAmNrYAB6BDs")) != null) {
               return holder;
            }
         }
      }

      System.setProperty("java.net.useSystemProxies", "false");
      return holder;
   }

   public void C(boolean bl) {
      this.P = bl;
   }

   public boolean w() {
      return this.P;
   }
}
