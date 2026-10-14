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

   public JvmArgumentsCheck(StringListHolder stringListHolder) {
      this.U = stringListHolder;
   }

   public Holder V() {
      Holder holder = new Holder();
      holder.value = new ItemFishFood(false);
      List list = ManagementFactory.getRuntimeMXBean().getInputArguments();
      this.l(true);

      for (Object object : list) {
         String string = (String)object;
         for (String string2 : this.U.A().L().getValue()) {
            if (string.equalsIgnoreCase(String.format(JvmArgumentsCheckStrings.i("GzyuT5+zopp8S09c3Q=="), JvmArgumentsCheckStrings.i("VyW7VpK0qtJzWBAX2ivSykU=")))
               )
             {
               this.Y = true;
            } else if (string.startsWith(JvmArgumentsCheckStrings.i("GzyuT5+zopp8S08=")) && string.endsWith(JvmArgumentsCheckStrings.i("GTe8TZG+o5A/XhIcwHGWwVZx"))
               )
             {
               this.Y = true;
            } else if (string.contains(string2) && (holder.value = new ItemFishFood(true)) != null) {
            }
         }
      }

      if (!this.Y && (holder.value = new ItemFishFood(true)) != null) {
      }

      return holder;
   }

   public void l(boolean bl) {
      this.Q = bl;
   }

   public boolean U() {
      return this.Q;
   }
}
