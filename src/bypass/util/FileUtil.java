package bypass.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FileUtil {
   public static final ExecutorService w = Executors.newSingleThreadExecutor();
   private static final long a = 112447251599516L;

   public static JsonElement readJson(File var0) {
      JsonElement var4 = null;
      FileReader var5 = null;

      try {
         var5 = new FileReader(var0);
         var4 = new JsonParser().parse(var5);
      } catch (FileNotFoundException | JsonIOException | JsonSyntaxException var15) {
         var15.printStackTrace();
      } finally {
         if (var5 != null) {
            try {
               var5.close();
            } catch (IOException var14) {
               var14.printStackTrace();
            }
         }
      }

      return var4;
   }

   public static void writeJson(File var0, JsonElement var1) {
      FileWriter var5 = null;

      try {
         var5 = new FileWriter(var0);
         var5.write(new GsonBuilder().setPrettyPrinting().create().toJson(var1));
      } catch (IOException var15) {
         var15.printStackTrace();
      } finally {
         if (var5 != null) {
            try {
               var5.close();
            } catch (IOException var14) {
               var14.printStackTrace();
            }
         }
      }
   }

   public static void writeLines(File var0, Collection<String> var1) {
      BufferedReader var5 = null;

      try {
         var5 = new BufferedReader(new InputStreamReader(new FileInputStream(var0)));

         String var6;
         while ((var6 = var5.readLine()) != null) {
            var1.add(var6);
         }
      } catch (IOException var15) {
         var15.printStackTrace();
      } finally {
         try {
            if (var5 != null) {
               var5.close();
            }
         } catch (IOException var14) {
            var14.printStackTrace();
         }
      }
   }

   public static File chooseFile(File var0) {
      File var4 = null;
      TopFileChooser var5 = new TopFileChooser(var0);
      if (var5.showOpenDialog(var5) == 0) {
         var4 = var5.getSelectedFile();
      }

      return var4;
   }
}
