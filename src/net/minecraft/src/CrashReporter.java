package net.minecraft.src;

import java.util.HashMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import shadersmod.client.Shaders;

public class CrashReporter {
   public static void onCrashReport(CrashReport crashReport, CrashReportCategory category) {
      try {
         GameSettings gameSettings = Config.getGameSettings();
         if (gameSettings == null) {
            return;
         }

         if (!gameSettings.snooperEnabled) {
            return;
         }

         Throwable throwable = crashReport.getCrashCause();
         if (throwable == null) {
            return;
         }

         if (throwable.getClass() == Throwable.class) {
            return;
         }

         if (throwable.getClass().getName().contains(".fml.client.SplashProgress")) {
            return;
         }

         extendCrashReport(category);
         String string = "http://optifine.net/crashReport";
         String string2 = makeReport(crashReport);
         byte[] bytes = string2.getBytes("ASCII");
         IFileUploadListener iFileUploadListener = new IFileUploadListener() {
            @Override
            public void fileUploadFinished(String url, byte[] content, Throwable exception) {
            }
         };
         HashMap hashMap = new HashMap();
         hashMap.put("OF-Version", Config.getVersion());
         hashMap.put("OF-Summary", makeSummary(crashReport));
         FileUploadThread fileUploadThread = new FileUploadThread(string, hashMap, bytes, iFileUploadListener);
         fileUploadThread.setPriority(10);
         fileUploadThread.start();
         Thread.sleep(1000L);
      } catch (Exception exception2) {
         Config.dbg(exception2.getClass().getName() + ": " + exception2.getMessage());
      }
   }

   private static String makeReport(CrashReport crashReport) {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("OptiFineVersion: " + Config.getVersion() + "\n");
      stringBuffer.append("Summary: " + makeSummary(crashReport) + "\n");
      stringBuffer.append("\n");
      stringBuffer.append(crashReport.getCompleteReport());
      stringBuffer.append("\n");
      return stringBuffer.toString();
   }

   private static String makeSummary(CrashReport crashReport) {
      Throwable throwable = crashReport.getCrashCause();
      if (throwable == null) {
         return "Unknown";
      }

      StackTraceElement[] stackTraceElements = throwable.getStackTrace();
      String string = "unknown";
      if (stackTraceElements.length > 0) {
         string = stackTraceElements[0].toString().trim();
      }

      return throwable.getClass().getName() + ": " + throwable.getMessage() + " (" + crashReport.getDescription() + ") [" + string + "]";
   }

   public static void extendCrashReport(CrashReportCategory cat) {
      cat.addCrashSection("OptiFine Version", Config.getVersion());
      if (Config.getGameSettings() != null) {
         cat.addCrashSection("Render Distance Chunks", "" + Config.getChunkViewDistance());
         cat.addCrashSection("Mipmaps", "" + Config.getMipmapLevels());
         cat.addCrashSection("Anisotropic Filtering", "" + Config.getAnisotropicFilterLevel());
         cat.addCrashSection("Antialiasing", "" + Config.getAntialiasingLevel());
         cat.addCrashSection("Multitexture", "" + Config.isMultiTexture());
      }

      cat.addCrashSection("Shaders", "" + Shaders.getShaderPackName());
      cat.addCrashSection("OpenGlVersion", "" + Config.openGlVersion);
      cat.addCrashSection("OpenGlRenderer", "" + Config.openGlRenderer);
      cat.addCrashSection("OpenGlVendor", "" + Config.openGlVendor);
      cat.addCrashSection("CpuCount", "" + Config.getAvailableProcessors());
   }
}
