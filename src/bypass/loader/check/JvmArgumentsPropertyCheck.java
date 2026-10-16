package bypass.loader.check;

import bypass.util.Holder;
import java.lang.management.ManagementFactory;
import java.util.List;
import oshi.util.tuples.Pair;

public class JvmArgumentsPropertyCheck {
   private final StringListHolder patternList;
   private boolean flag1;

   public JvmArgumentsPropertyCheck(StringListHolder stringListHolder) {
      this.patternList = stringListHolder;
   }

   public Holder checkJvmArgumentProperties() {
      Holder holder = new Holder();
      holder.value = new Pair<>("ZahdeTpvtv9h", "ZahdeTpvtv9h");
      List list = ManagementFactory.getRuntimeMXBean().getInputArguments();
      this.setFlag1(true);

      for (Object object : list) {
         String string = (String)object;
         for (String string2 : this.patternList.getStringContainer().getOwnedContainer().getValue()) {
            if (string.contains(string2) && (holder.value = new Pair<>("ZdUjf83X2HY9pUpKC6", "Dfr3fpSAmNrYAB6BDs")) != null) {
               return holder;
            }
         }
      }

      System.setProperty("java.net.useSystemProxies", "false");
      return holder;
   }

   public void setFlag1(boolean bl) {
      this.flag1 = bl;
   }

   public boolean isFlag1() {
      return this.flag1;
   }
}
