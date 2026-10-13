package bypass.loader.check;

import bypass.util.Holder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import net.minecraft.entity.passive.EntityPig;

public class ProcessCheck {
   private final StringListHolder L;
   private boolean q;
   private static final long a = 34062357179795L;

   public ProcessCheck(StringListHolder var1) {
      this.L = var1;
   }

   private boolean k(String var1) throws Exception {
      Process var5 = Runtime.getRuntime().exec(ProcessNamesStrings.c("q/k9EC4NS+k="));
      BufferedReader var6 = new BufferedReader(new InputStreamReader(var5.getInputStream()));

      String var7;
      while ((var7 = var6.readLine()) != null) {
         if (var7.toLowerCase().contains(var1 + ProcessNamesStrings.c("8f02Hg=="))) {
            return true;
         }
      }

      return false;
   }

   public Holder C() {
      Holder var4 = new Holder();
      var4.value = new EntityPig(2147483646);
      this.u(true);

      for (String var6 : this.L.A().L().getValue()) {
         try {
            if (this.k(var6) && (var4.value = new EntityPig(135278)) != null) {
            }
         } catch (Exception var8) {
         }
      }

      return var4;
   }

   public void u(boolean var1) {
      this.q = var1;
   }

   public boolean P() {
      return this.q;
   }
}
