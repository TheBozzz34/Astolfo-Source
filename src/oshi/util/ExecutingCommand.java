package oshi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class ExecutingCommand {
   private static final long a = 8186991338389L;

   private ExecutingCommand() {
   }

   public static List<String> runNative(String var0) {
      String[] var1 = var0.split(" ");
      return runNative(var1);
   }

   public static List<String> runNative(String[] var0) {
      Process var1 = null;

      try {
         var1 = Runtime.getRuntime().exec(var0);
      } catch (SecurityException | IOException var16) {
         return new ArrayList<>(0);
      }

      ArrayList var2 = new ArrayList();

      try (BufferedReader var3 = new BufferedReader(new InputStreamReader(var1.getInputStream(), Charset.defaultCharset()))) {
         String var5;
         while ((var5 = var3.readLine()) != null) {
            var2.add(var5);
         }

         var1.waitFor();
      } catch (IOException var19) {
         return new ArrayList<>(0);
      } catch (InterruptedException var20) {
         Thread.currentThread().interrupt();
      }

      return var2;
   }

   public static String getFirstAnswer(String var0) {
      return getAnswerAt(var0, 0);
   }

   public static String getAnswerAt(String var0, int var1) {
      List var4 = runNative(var0);
      return var1 >= 0 && var1 < var4.size() ? (String)var4.get(var1) : "";
   }
}
