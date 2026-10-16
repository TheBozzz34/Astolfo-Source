package bypass.loader.check;

import bypass.util.Holder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import net.minecraft.entity.passive.EntityPig;

public class ProcessCheck {
   private final StringListHolder processListHolder;
   private boolean flag1;

   public ProcessCheck(StringListHolder stringListHolder) {
      this.processListHolder = stringListHolder;
   }

   private boolean isProcessRunning(String string) throws Exception {
      Process process = Runtime.getRuntime().exec("tasklist");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String string2;
      while ((string2 = bufferedReader.readLine()) != null) {
         if (string2.toLowerCase().contains(string + ".exe")) {
            return true;
         }
      }

      return false;
   }

   public Holder checkProcesses() {
      Holder holder = new Holder();
      holder.value = new EntityPig(2147483646);
      this.setFlag1(true);

      for (String string : this.processListHolder.getStringContainer().getOwnedContainer().getValue()) {
         try {
            if (this.isProcessRunning(string) && (holder.value = new EntityPig(135278)) != null) {
            }
         } catch (Exception exception) {
         }
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
