package bypass.loader.check;

import bypass.util.Holder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import net.minecraft.entity.passive.EntityPig;

public class ProcessCheck {
   private final StringListHolder L;
   private boolean q;
   private static final long a = 34062357179795L;

   public ProcessCheck(StringListHolder stringListHolder) {
      this.L = stringListHolder;
   }

   private boolean k(String string) throws Exception {
      Process process = Runtime.getRuntime().exec(ProcessNamesStrings.c("q/k9EC4NS+k="));
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String string2;
      while ((string2 = bufferedReader.readLine()) != null) {
         if (string2.toLowerCase().contains(string + ProcessNamesStrings.c("8f02Hg=="))) {
            return true;
         }
      }

      return false;
   }

   public Holder C() {
      Holder holder = new Holder();
      holder.value = new EntityPig(2147483646);
      this.u(true);

      for (String string : this.L.A().L().getValue()) {
         try {
            if (this.k(string) && (holder.value = new EntityPig(135278)) != null) {
            }
         } catch (Exception exception) {
         }
      }

      return holder;
   }

   public void u(boolean bl) {
      this.q = bl;
   }

   public boolean P() {
      return this.q;
   }
}
