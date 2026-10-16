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

   public CrashReport(String descriptionIn, Throwable causeThrowable) {
      this.description = descriptionIn;
      this.cause = causeThrowable;
      this.populateEnvironment();
   }

   private void populateEnvironment() {
      this.theReportCategory.addCrashSectionCallable("Minecraft Version", new Callable<String>() {
         public String call() {
            return "1.8.8";
         }
      });
      this.theReportCategory.addCrashSectionCallable("Operating System", new Callable<String>() {
         public String call() {
            return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
         }
      });
      this.theReportCategory.addCrashSectionCallable("CPU", new CrashReportCpu());
      this.theReportCategory.addCrashSectionCallable("Java Version", new Callable<String>() {
         public String call() {
            return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
         }
      });
      this.theReportCategory.addCrashSectionCallable("Java VM Version", new Callable<String>() {
         public String call() {
            return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
         }
      });
      this.theReportCategory.addCrashSectionCallable("Memory", new Callable<String>() {
         public String call() {
            Runtime runtime = Runtime.getRuntime();
            long l = runtime.maxMemory();
            long m = runtime.totalMemory();
            long n = runtime.freeMemory();
            long l2 = l / 1024L / 1024L;
            long l3 = m / 1024L / 1024L;
            long l4 = n / 1024L / 1024L;
            return n + " bytes (" + l4 + " MB) / " + m + " bytes (" + l3 + " MB) up to " + l + " bytes (" + l2 + " MB)";
         }
      });
      this.theReportCategory.addCrashSectionCallable("JVM Flags", new Callable<String>() {
         public String call() {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            List list = runtimeMXBean.getInputArguments();
            int i = 0;
            StringBuilder stringBuilder = new StringBuilder();

            for (Object object : list) {
               if (((String)object).startsWith("-X")) {
                  if (i++ > 0) {
                     stringBuilder.append(" ");
                  }

                  stringBuilder.append(object);
               }
            }

            return String.format("%d total; %s", i, stringBuilder.toString());
         }
      });
      this.theReportCategory.addCrashSectionCallable("IntCache", new Callable<String>() {
         public String call() throws Exception {
            return IntCache.getCacheSizes();
         }
      });
      if (Reflector.FMLCommonHandler_enhanceCrashReport.exists()) {
         Object object2 = Reflector.call(Reflector.FMLCommonHandler_instance);
         Reflector.callString(object2, Reflector.FMLCommonHandler_enhanceCrashReport, this, this.theReportCategory);
      }
   }

   public String getDescription() {
      return this.description;
   }

   public Throwable getCrashCause() {
      return this.cause;
   }

   public void getSectionsInStringBuilder(StringBuilder builder) {
      if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0) {
         this.stacktrace = ArrayUtils.subarray(this.crashReportSections.get(0).getStackTrace(), 0, 1);
      }

      if (this.stacktrace != null && this.stacktrace.length > 0) {
         builder.append("-- Head --\n");
         builder.append("Stacktrace:\n");

         for (StackTraceElement stackTraceElement : this.stacktrace) {
            builder.append("\t").append("at ").append(stackTraceElement.toString());
            builder.append("\n");
         }

         builder.append("\n");
      }

      for (Object object : this.crashReportSections) {
         ((CrashReportCategory)object).appendToStringBuilder(builder);
         builder.append("\n\n");
      }

      this.theReportCategory.appendToStringBuilder(builder);
   }

   public String getCauseStackTraceOrString() {
      StringWriter stringWriter = null;
      PrintWriter printWriter = null;
      Throwable throwable = this.cause;
      if (throwable.getMessage() == null) {
         if (throwable instanceof NullPointerException) {
            throwable = new NullPointerException(this.description);
         } else if (throwable instanceof StackOverflowError) {
            throwable = new StackOverflowError(this.description);
         } else if (throwable instanceof OutOfMemoryError) {
            throwable = new OutOfMemoryError(this.description);
         }

         throwable.setStackTrace(this.cause.getStackTrace());
      }

      String string = throwable.toString();

      try {
         stringWriter = new StringWriter();
         printWriter = new PrintWriter(stringWriter);
         throwable.printStackTrace(printWriter);
         string = stringWriter.toString();
      } finally {
         IOUtils.closeQuietly(stringWriter);
         IOUtils.closeQuietly(printWriter);
      }

      return string;
   }

   public String getCompleteReport() {
      if (!this.reported) {
         this.reported = true;
         CrashReporter.onCrashReport(this, this.theReportCategory);
      }

      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("---- Minecraft Crash Report ----\n");
      Reflector.call(Reflector.BlamingTransformer_onCrash, stringBuilder);
      Reflector.call(Reflector.CoreModManager_onCrash, stringBuilder);
      stringBuilder.append("// ");
      stringBuilder.append(getWittyComment());
      stringBuilder.append("\n\n");
      stringBuilder.append("Time: ");
      stringBuilder.append(new SimpleDateFormat().format(new Date()));
      stringBuilder.append("\n");
      stringBuilder.append("Description: ");
      stringBuilder.append(this.description);
      stringBuilder.append("\n\n");
      stringBuilder.append(this.getCauseStackTraceOrString());
      stringBuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for (int i = 0; i < 87; i++) {
         stringBuilder.append("-");
      }

      stringBuilder.append("\n\n");
      this.getSectionsInStringBuilder(stringBuilder);
      return stringBuilder.toString();
   }

   public File getFile() {
      return this.crashReportFile;
   }

   public boolean saveToFile(File toFile) {
      if (this.crashReportFile != null) {
         return false;
      }

      if (toFile.getParentFile() != null) {
         toFile.getParentFile().mkdirs();
      }

      try {
         FileWriter fileWriter = new FileWriter(toFile);
         fileWriter.write(CrashReportEncryption.encrypt(this.getCompleteReport()));
         fileWriter.close();
         this.crashReportFile = toFile;
         return true;
      } catch (Throwable throwable) {
         logger.error("Could not save crash report to " + toFile, throwable);
         return false;
      }
   }

   public CrashReportCategory getCategory() {
      return this.theReportCategory;
   }

   public CrashReportCategory makeCategory(String name) {
      return this.makeCategoryDepth(name, 1);
   }

   public CrashReportCategory makeCategoryDepth(String categoryName, int stacktraceLength) {
      CrashReportCategory crashReportCategory = new CrashReportCategory(this, categoryName);
      if (this.firstCategoryInCrashReport) {
         int i = crashReportCategory.getPrunedStackTrace(stacktraceLength);
         StackTraceElement[] stackTraceElements = this.cause.getStackTrace();
         StackTraceElement stackTraceElement = null;
         StackTraceElement stackTraceElement2 = null;
         int j = stackTraceElements.length - i;
         if (j < 0) {
            System.out.println("Negative index in crash report handler (" + stackTraceElements.length + "/" + i + ")");
         }

         if (stackTraceElements != null && 0 <= j && j < stackTraceElements.length) {
            stackTraceElement = stackTraceElements[j];
            if (stackTraceElements.length + 1 - i < stackTraceElements.length) {
               stackTraceElement2 = stackTraceElements[stackTraceElements.length + 1 - i];
            }
         }

         this.firstCategoryInCrashReport = crashReportCategory.firstTwoElementsOfStackTraceMatch(stackTraceElement, stackTraceElement2);
         if (i > 0 && !this.crashReportSections.isEmpty()) {
            CrashReportCategory crashReportCategory2 = this.crashReportSections.get(this.crashReportSections.size() - 1);
            crashReportCategory2.trimStackTraceEntriesFromBottom(i);
         } else if (stackTraceElements != null && stackTraceElements.length >= i && 0 <= j && j < stackTraceElements.length) {
            this.stacktrace = new StackTraceElement[j];
            System.arraycopy(stackTraceElements, 0, this.stacktrace, 0, this.stacktrace.length);
         } else {
            this.firstCategoryInCrashReport = false;
         }
      }

      this.crashReportSections.add(crashReportCategory);
      return crashReportCategory;
   }

   private static String getWittyComment() {
      String[] strings = new String[]{
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
         return strings[(int)(System.nanoTime() % strings.length)];
      } catch (Throwable throwable) {
         return "Witty comment unavailable :(";
      }
   }

   public static CrashReport makeCrashReport(Throwable causeIn, String descriptionIn) {
      CrashReport crashReport;
      if (causeIn instanceof ReportedException) {
         crashReport = ((ReportedException)causeIn).getCrashReport();
      } else {
         crashReport = new CrashReport(descriptionIn, causeIn);
      }

      return crashReport;
   }
}
