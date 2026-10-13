package bypass.auth.fingerprint;

import java.io.File;
import net.minecraft.util.Util;

public final class UserDirectoryValidator implements FingerprintValidator<String> {
   private static final long a = 122351099187892L;

   @Override
   public boolean test(FingerprintEntry var1) {
      if (!Util.getOSType().equals(Util.EnumOS.WINDOWS)) {
         return true;
      }

      boolean var5 = false;
      File var6 = new File("C:/Users/");
      File[] var7 = var6.listFiles();
      if (var7 != null || var7.length != 0) {
         for (File var11 : var7) {
            if (var11.isDirectory() && ((String)var1.z()).contains(var11.getName())) {
               var5 = true;
               break;
            }
         }
      }

      return var5;
   }
}
