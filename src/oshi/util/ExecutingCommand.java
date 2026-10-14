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
   private ExecutingCommand() {
   }

   public static List<String> runNative(String cmdToRun) {
      String[] cmd = cmdToRun.split(" ");
      return runNative(cmd);
   }

   public static List<String> runNative(String[] cmdToRunWithArgs) {
      Process process = null;

      try {
         process = Runtime.getRuntime().exec(cmdToRunWithArgs);
      } catch (SecurityException | IOException value) {
         return new ArrayList<>(0);
      }

      ArrayList arrayList = new ArrayList();

      try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
         String string;
         while ((string = bufferedReader.readLine()) != null) {
            arrayList.add(string);
         }

         process.waitFor();
      } catch (IOException ioException) {
         return new ArrayList<>(0);
      } catch (InterruptedException interruptedException) {
         Thread.currentThread().interrupt();
      }

      return arrayList;
   }

   public static String getFirstAnswer(String cmd2launch) {
      return getAnswerAt(cmd2launch, 0);
   }

   public static String getAnswerAt(String cmd2launch, int answerIdx) {
      List list = runNative(cmd2launch);
      return answerIdx >= 0 && answerIdx < list.size() ? (String)list.get(answerIdx) : "";
   }
}
