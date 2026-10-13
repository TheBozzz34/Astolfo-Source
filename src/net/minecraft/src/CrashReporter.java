package net.minecraft.src;

import java.util.HashMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import shadersmod.client.Shaders;

public class CrashReporter {
   private static final long a = 104921570701964L;
   private static final long e = 1195253711593537546L;

   public static void onCrashReport(CrashReport var0, CrashReportCategory var1) {
      try {
         GameSettings var5 = Config.getGameSettings();
         if (var5 == null) {
            return;
         }

         if (!var5.snooperEnabled) {
            return;
         }

         Throwable var6 = var0.getCrashCause();
         if (var6 == null) {
            return;
         }

         if (var6.getClass() == Throwable.class) {
            return;
         }

         if (var6.getClass().getName().contains(".fml.client.SplashProgress")) {
            return;
         }

         extendCrashReport(var1);
         String var7 = "http://optifine.net/crashReport";
         String var8 = makeReport(var0);
         byte[] var9 = var8.getBytes("ASCII");
         IFileUploadListener var10 = new IFileUploadListener() {
            @Override
            public void fileUploadFinished(String var1, byte[] var2, Throwable var3) {
            }
         };
         HashMap var11 = new HashMap();
         var11.put("OF-Version", Config.getVersion());
         var11.put("OF-Summary", makeSummary(var0));
         FileUploadThread var12 = new FileUploadThread(var7, var11, var9, var10);
         var12.setPriority((int)e);
         var12.start();
         Thread.sleep(1000L);
      } catch (Exception var13) {
         Config.dbg(var13.getClass().getName() + ": " + var13.getMessage());
      }
   }

   private static String makeReport(CrashReport var0) {
      StringBuffer var3 = new StringBuffer();
      var3.append("OptiFineVersion: " + Config.getVersion() + "\n");
      var3.append("Summary: " + makeSummary(var0) + "\n");
      var3.append("\n");
      var3.append(var0.getCompleteReport());
      var3.append("\n");
      return var3.toString();
   }

   private static String makeSummary(CrashReport var0) {
      Throwable var4 = var0.getCrashCause();
      if (var4 == null) {
         return "Unknown";
      }

      StackTraceElement[] var5 = var4.getStackTrace();
      String var6 = "unknown";
      if (var5.length > 0) {
         var6 = var5[0].toString().trim();
      }

      return var4.getClass().getName() + ": " + var4.getMessage() + " (" + var0.getDescription() + ") [" + var6 + "]";
   }

   public static void extendCrashReport(CrashReportCategory var0) {
      var0.addCrashSection("OptiFine Version", Config.getVersion());
      if (Config.getGameSettings() != null) {
         var0.addCrashSection("Render Distance Chunks", "" + Config.getChunkViewDistance());
         var0.addCrashSection("Mipmaps", "" + Config.getMipmapLevels());
         var0.addCrashSection("Anisotropic Filtering", "" + Config.getAnisotropicFilterLevel());
         var0.addCrashSection("Antialiasing", "" + Config.getAntialiasingLevel());
         var0.addCrashSection("Multitexture", "" + Config.isMultiTexture());
      }

      var0.addCrashSection("Shaders", "" + Shaders.getShaderPackName());
      var0.addCrashSection("OpenGlVersion", "" + Config.openGlVersion);
      var0.addCrashSection("OpenGlRenderer", "" + Config.openGlRenderer);
      var0.addCrashSection("OpenGlVendor", "" + Config.openGlVendor);
      var0.addCrashSection("CpuCount", "" + Config.getAvailableProcessors());
   }
}
