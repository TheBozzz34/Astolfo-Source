package net.minecraft.src;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionCheckThread extends Thread {
   private static final long a = 116778161396671L;

   @Override
   public void run() {
      HttpURLConnection var4 = null;

      try {
         Config.dbg("Checking for new version");
         URL var5 = new URL("http://optifine.net/version/1.8.8/HD_U.txt");
         var4 = (HttpURLConnection)var5.openConnection();
         var4.setDoInput(true);
         var4.setDoOutput(false);
         var4.connect();

         try {
            InputStream var6 = var4.getInputStream();
            String var7 = Config.readInputStream(var6);
            var6.close();
            String[] var8 = Config.tokenize(var7, "\n\r");
            if (var8.length < 1) {
               return;
            }

            String var9 = var8[0].trim();
            Config.dbg("Version found: " + var9);
            if (Config.compareRelease(var9, "H8") > 0) {
               Config.G(var9);
               return;
            }
         } finally {
            if (var4 != null) {
               var4.disconnect();
            }
         }
      } catch (Exception var14) {
         Config.dbg(var14.getClass().getName() + ": " + var14.getMessage());
      }
   }
}
