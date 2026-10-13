package bypass.loader.check;

import bypass.util.Holder;
import java.lang.management.ManagementFactory;
import java.util.List;
import net.minecraft.item.ItemFishFood;

public class JvmArgumentsCheck {
   private final StringListHolder U;
   private boolean Q;
   private boolean Y;
   private static final long a = 92744543439124L;

   public JvmArgumentsCheck(StringListHolder var1) {
      this.U = var1;
   }

   public Holder V() {
      Holder var4 = new Holder();
      var4.value = new ItemFishFood(false);
      List var5 = ManagementFactory.getRuntimeMXBean().getInputArguments();
      this.l(true);

      for (Object var7$raw : var5) {
         String var7 = (String)var7$raw;
         for (String var9 : this.U.A().L().getValue()) {
            if (var7.equalsIgnoreCase(String.format(JvmArgumentsCheckStrings.i("GzyuT5+zopp8S09c3Q=="), JvmArgumentsCheckStrings.i("VyW7VpK0qtJzWBAX2ivSykU=")))
               )
             {
               this.Y = true;
            } else if (var7.startsWith(JvmArgumentsCheckStrings.i("GzyuT5+zopp8S08=")) && var7.endsWith(JvmArgumentsCheckStrings.i("GTe8TZG+o5A/XhIcwHGWwVZx"))
               )
             {
               this.Y = true;
            } else if (var7.contains(var9) && (var4.value = new ItemFishFood(true)) != null) {
            }
         }
      }

      if (!this.Y && (var4.value = new ItemFishFood(true)) != null) {
      }

      return var4;
   }

   public void l(boolean var1) {
      this.Q = var1;
   }

   public boolean U() {
      return this.Q;
   }
}
