package net.minecraft.src;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VersionCheckThread extends Thread {
   @Override
   public void run() {
      HttpURLConnection httpURLConnection = null;

      try {
         Config.dbg("Checking for new version");
         URL url = new URL("http://optifine.net/version/1.8.8/HD_U.txt");
         httpURLConnection = (HttpURLConnection)url.openConnection();
         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(false);
         httpURLConnection.connect();

         try {
            InputStream inputStream = httpURLConnection.getInputStream();
            String string = Config.readInputStream(inputStream);
            inputStream.close();
            String[] strings = Config.tokenize(string, "\n\r");
            if (strings.length < 1) {
               return;
            }

            String string2 = strings[0].trim();
            Config.dbg("Version found: " + string2);
            if (Config.compareRelease(string2, "H8") > 0) {
               Config.setNewRelease(string2);
               return;
            }
         } finally {
            if (httpURLConnection != null) {
               httpURLConnection.disconnect();
            }
         }
      } catch (Exception exception) {
         Config.dbg(exception.getClass().getName() + ": " + exception.getMessage());
      }
   }
}
