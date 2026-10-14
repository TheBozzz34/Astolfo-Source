package bypass.auth.fingerprint;

import java.io.File;
import net.minecraft.util.Util;

public final class UserDirectoryValidator implements FingerprintValidator<String> {
   private static final long a = 122351099187892L;

   @Override
   public boolean test(FingerprintEntry fingerprintEntry) {
      if (!Util.getOSType().equals(Util.EnumOS.WINDOWS)) {
         return true;
      }

      boolean bl = false;
      File file = new File("C:/Users/");
      File[] files = file.listFiles();
      if (files != null || files.length != 0) {
         for (File file2 : files) {
            if (file2.isDirectory() && ((String)fingerprintEntry.z()).contains(file2.getName())) {
               bl = true;
               break;
            }
         }
      }

      return bl;
   }
}
