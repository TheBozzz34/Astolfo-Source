package bypass.loader.check;

import bypass.util.Holder;
import java.lang.management.ManagementFactory;
import java.util.List;
import net.minecraft.item.ItemFishFood;

public class JvmArgumentsCheck {
   private final StringListHolder patternList;
   private boolean flag1;
   private boolean agentFound;

   public JvmArgumentsCheck(StringListHolder stringListHolder) {
      this.patternList = stringListHolder;
   }

   public Holder checkJvmArguments() {
      Holder holder = new Holder();
      holder.value = new ItemFishFood(false);
      List list = ManagementFactory.getRuntimeMXBean().getInputArguments();
      this.setFlag1(true);

      for (Object object : list) {
         String string = (String)object;
         for (String string2 : this.patternList.getStringContainer().getOwnedContainer().getValue()) {
            if (string.equalsIgnoreCase(String.format("-javaagent:%s", "astolfo-agent.jar"))) {
               this.agentFound = true;
            } else if (string.startsWith("-javaagent:") && string.endsWith("/astolfo-agent.jar")) {
               this.agentFound = true;
            } else if (string.contains(string2) && (holder.value = new ItemFishFood(true)) != null) {
            }
         }
      }

      if (!this.agentFound && (holder.value = new ItemFishFood(true)) != null) {
      }

      return holder;
   }

   public void setFlag1(boolean bl) {
      this.flag1 = bl;
   }

   public boolean isFlag1() {
      return this.flag1;
   }
}
