package bypass.loader.check;

import bypass.util.Holder;
import java.lang.management.ManagementFactory;
import java.util.List;
import oshi.util.tuples.Pair;

public class JvmArgumentsPropertyCheck {
   private final StringListHolder Q;
   private boolean P;
   private static final long a = 116872602638098L;

   public JvmArgumentsPropertyCheck(StringListHolder var1) {
      this.Q = var1;
   }

   public Holder Q() {
      Holder var4 = new Holder();
      var4.value = new Pair<>("ZahdeTpvtv9h", "ZahdeTpvtv9h");
      List var5 = ManagementFactory.getRuntimeMXBean().getInputArguments();
      this.C(true);

      for (Object var7$raw : var5) {
         String var7 = (String)var7$raw;
         for (String var9 : this.Q.A().L().getValue()) {
            if (var7.contains(var9) && (var4.value = new Pair<>("ZdUjf83X2HY9pUpKC6", "Dfr3fpSAmNrYAB6BDs")) != null) {
               return var4;
            }
         }
      }

      System.setProperty(JvmArgumentsPropertyStrings.J("/jJmqaAJ/A7HI2uxLkozi5uENXs2tiLrBA=="), JvmArgumentsPropertyStrings.J("8jJ8u+s="));
      return var4;
   }

   public void C(boolean var1) {
      this.P = var1;
   }

   public boolean w() {
      return this.P;
   }
}
