package net.minecraft.crash;

import bypass.CrashReportEncryption;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.src.CrashReportCpu;
import net.minecraft.src.CrashReporter;
import net.minecraft.src.Reflector;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
   private static final Logger logger = LogManager.getLogger();
   private final String description;
   private final Throwable cause;
   private final CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");
   private final List<CrashReportCategory> crashReportSections = Lists.newArrayList();
   private File crashReportFile;
   private boolean firstCategoryInCrashReport = true;
   private StackTraceElement[] stacktrace = new StackTraceElement[0];
   private boolean reported = false;
   private static final long a = 62197970293659L;

   public CrashReport(String var1, Throwable var2) {
      this.description = var1;
      this.cause = var2;
      this.populateEnvironment();
   }

   private void populateEnvironment() {
      this.theReportCategory.addCrashSectionCallable("Minecraft Version", new Callable<String>() {
         private static final long a = 32713302720581L;

         public String call() {
            return "1.8.8";
         }
      });
      this.theReportCategory.addCrashSectionCallable("Operating System", new Callable<String>() {
         private static final long a = 9808470714706L;

         public String call() {
            return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
         }
      });
      this.theReportCategory.addCrashSectionCallable("CPU", new CrashReportCpu());
      this.theReportCategory.addCrashSectionCallable("Java Version", new Callable<String>() {
         private static final long a = 31594650772762L;

         public String call() {
            return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
         }
      });
      this.theReportCategory.addCrashSectionCallable("Java VM Version", new Callable<String>() {
         private static final long a = 27672619279378L;

         public String call() {
            return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
         }
      });
      this.theReportCategory.addCrashSectionCallable("Memory", new Callable<String>() {
         private static final long a = 138140615711711L;

         public String call() {
            Runtime var4 = Runtime.getRuntime();
            long var5 = var4.maxMemory();
            long var7 = var4.totalMemory();
            long var9 = var4.freeMemory();
            long var11 = var5 / 1024L / 1024L;
            long var13 = var7 / 1024L / 1024L;
            long var15 = var9 / 1024L / 1024L;
            return var9 + " bytes (" + var15 + " MB) / " + var7 + " bytes (" + var13 + " MB) up to " + var5 + " bytes (" + var11 + " MB)";
         }
      });
      this.theReportCategory.addCrashSectionCallable("JVM Flags", new Callable<String>() {
         private static final long a = 47523400934197L;

         public String call() {
            RuntimeMXBean var4 = ManagementFactory.getRuntimeMXBean();
            List var5 = var4.getInputArguments();
            int var6 = 0;
            StringBuilder var7 = new StringBuilder();

            for (Object var9 : var5) {
               if (((String)var9).startsWith("-X")) {
                  if (var6++ > 0) {
                     var7.append(" ");
                  }

                  var7.append(var9);
               }
            }

            return String.format("%d total; %s", var6, var7.toString());
         }
      });
      this.theReportCategory.addCrashSectionCallable("IntCache", new Callable<String>() {
         public String call() throws Exception {
            return IntCache.getCacheSizes();
         }
      });
      if (Reflector.FMLCommonHandler_enhanceCrashReport.exists()) {
         Object var3 = Reflector.call(Reflector.FMLCommonHandler_instance);
         Reflector.callString(var3, Reflector.FMLCommonHandler_enhanceCrashReport, this, this.theReportCategory);
      }
   }

   public String getDescription() {
      return this.description;
   }

   public Throwable getCrashCause() {
      return this.cause;
   }

   public void getSectionsInStringBuilder(StringBuilder var1) {
      if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0) {
         this.stacktrace = ArrayUtils.subarray(this.crashReportSections.get(0).getStackTrace(), 0, 1);
      }

      if (this.stacktrace != null && this.stacktrace.length > 0) {
         var1.append("-- Head --\n");
         var1.append("Stacktrace:\n");

         for (StackTraceElement var8 : this.stacktrace) {
            var1.append("\t").append("at ").append(var8.toString());
            var1.append("\n");
         }

         var1.append("\n");
      }

      for (Object var10 : this.crashReportSections) {
         ((CrashReportCategory)var10).appendToStringBuilder(var1);
         var1.append("\n\n");
      }

      this.theReportCategory.appendToStringBuilder(var1);
   }

   public String getCauseStackTraceOrString() {
      StringWriter var4 = null;
      PrintWriter var5 = null;
      Throwable var6 = this.cause;
      if (var6.getMessage() == null) {
         if (var6 instanceof NullPointerException) {
            var6 = new NullPointerException(this.description);
         } else if (var6 instanceof StackOverflowError) {
            var6 = new StackOverflowError(this.description);
         } else if (var6 instanceof OutOfMemoryError) {
            var6 = new OutOfMemoryError(this.description);
         }

         var6.setStackTrace(this.cause.getStackTrace());
      }

      String var7 = var6.toString();

      try {
         var4 = new StringWriter();
         var5 = new PrintWriter(var4);
         var6.printStackTrace(var5);
         var7 = var4.toString();
      } finally {
         IOUtils.closeQuietly(var4);
         IOUtils.closeQuietly(var5);
      }

      return var7;
   }

   public String getCompleteReport() {
      if (!this.reported) {
         this.reported = true;
         CrashReporter.onCrashReport(this, this.theReportCategory);
      }

      StringBuilder var5 = new StringBuilder();
      var5.append("---- Minecraft Crash Report ----\n");
      Reflector.call(Reflector.BlamingTransformer_onCrash, var5);
      Reflector.call(Reflector.CoreModManager_onCrash, var5);
      var5.append("// ");
      var5.append(getWittyComment());
      var5.append("\n\n");
      var5.append("Time: ");
      var5.append(new SimpleDateFormat().format(new Date()));
      var5.append("\n");
      var5.append("Description: ");
      var5.append(this.description);
      var5.append("\n\n");
      var5.append(this.getCauseStackTraceOrString());
      var5.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for (int var6 = 0; var6 < 87; var6++) {
         var5.append("-");
      }

      var5.append("\n\n");
      this.getSectionsInStringBuilder(var5);
      return var5.toString();
   }

   public File getFile() {
      return this.crashReportFile;
   }

   public boolean saveToFile(File var1) {
      if (this.crashReportFile != null) {
         return false;
      }

      if (var1.getParentFile() != null) {
         var1.getParentFile().mkdirs();
      }

      try {
         FileWriter var5 = new FileWriter(var1);
         var5.write(CrashReportEncryption.O(this.getCompleteReport()));
         var5.close();
         this.crashReportFile = var1;
         return true;
      } catch (Throwable var6) {
         logger.error("Could not save crash report to " + var1, var6);
         return false;
      }
   }

   public CrashReportCategory getCategory() {
      return this.theReportCategory;
   }

   public CrashReportCategory makeCategory(String var1) {
      return this.makeCategoryDepth(var1, 1);
   }

   public CrashReportCategory makeCategoryDepth(String var1, int var2) {
      CrashReportCategory var5 = new CrashReportCategory(this, var1);
      if (this.firstCategoryInCrashReport) {
         int var6 = var5.getPrunedStackTrace(var2);
         StackTraceElement[] var7 = this.cause.getStackTrace();
         StackTraceElement var8 = null;
         StackTraceElement var9 = null;
         int var10 = var7.length - var6;
         if (var10 < 0) {
            System.out.println("Negative index in crash report handler (" + var7.length + "/" + var6 + ")");
         }

         if (var7 != null && 0 <= var10 && var10 < var7.length) {
            var8 = var7[var10];
            if (var7.length + 1 - var6 < var7.length) {
               var9 = var7[var7.length + 1 - var6];
            }
         }

         this.firstCategoryInCrashReport = var5.firstTwoElementsOfStackTraceMatch(var8, var9);
         if (var6 > 0 && !this.crashReportSections.isEmpty()) {
            CrashReportCategory var11 = this.crashReportSections.get(this.crashReportSections.size() - 1);
            var11.trimStackTraceEntriesFromBottom(var6);
         } else if (var7 != null && var7.length >= var6 && 0 <= var10 && var10 < var7.length) {
            this.stacktrace = new StackTraceElement[var10];
            System.arraycopy(var7, 0, this.stacktrace, 0, this.stacktrace.length);
         } else {
            this.firstCategoryInCrashReport = false;
         }
      }

      this.crashReportSections.add(var5);
      return var5;
   }

   private static String getWittyComment() {
      String[] var2 = new String[]{
         "Who set us up the TNT?",
         "Everything's going to plan. No, really, that was supposed to happen.",
         "Uh... Did I do that?",
         "Oops.",
         "Why did you do that?",
         "I feel sad now :(",
         "My bad.",
         "I'm sorry, Dave.",
         "I let you down. Sorry :(",
         "On the bright side, I bought you a teddy bear!",
         "Daisy, daisy...",
         "Oh - I know what I did wrong!",
         "Hey, that tickles! Hehehe!",
         "I blame Dinnerbone.",
         "You should try our sister game, Minceraft!",
         "Don't be sad. I'll do better next time, I promise!",
         "Don't be sad, have a hug! <3",
         "I just don't know what went wrong :(",
         "Shall we play a game?",
         "Quite honestly, I wouldn't worry myself about that.",
         "I bet Cylons wouldn't have this problem.",
         "Sorry :(",
         "Surprise! Haha. Well, this is awkward.",
         "Would you like a cupcake?",
         "Hi. I'm Minecraft, and I'm a crashaholic.",
         "Ooh. Shiny.",
         "This doesn't make any sense!",
         "Why is it breaking :(",
         "Don't do that.",
         "Ouch. That hurt :(",
         "You're mean.",
         "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]",
         "There are four lights!",
         "But it works on my machine."
      };

      try {
         return var2[(int)(System.nanoTime() % var2.length)];
      } catch (Throwable var4) {
         return "Witty comment unavailable :(";
      }
   }

   public static CrashReport makeCrashReport(Throwable var0, String var1) {
      CrashReport var4;
      if (var0 instanceof ReportedException) {
         var4 = ((ReportedException)var0).getCrashReport();
      } else {
         var4 = new CrashReport(var1, var0);
      }

      return var4;
   }
}
