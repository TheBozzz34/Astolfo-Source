package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class CrashReportCategory {
   private final CrashReport crashReport;
   private final String name;
   private final List<CrashReportCategory.Entry> children = Lists.newArrayList();
   private StackTraceElement[] stackTrace = new StackTraceElement[0];
   private static final long a = 35604262996765L;

   public CrashReportCategory(CrashReport var1, String var2) {
      this.crashReport = var1;
      this.name = var2;
   }

   public static String getCoordinateInfo(double var0, double var2, double var4) {
      return String.format("%.2f,%.2f,%.2f - %s", var0, var2, var4, getCoordinateInfo(new BlockPos(var0, var2, var4)));
   }

   public static String getCoordinateInfo(BlockPos var0) {
      int var3 = var0.getX();
      int var4 = var0.getY();
      int var5 = var0.getZ();
      StringBuilder var6 = new StringBuilder();

      try {
         var6.append(String.format("World: (%d,%d,%d)", var3, var4, var5));
      } catch (Throwable var19) {
         var6.append("(Error finding world loc)");
      }

      var6.append(", ");

      try {
         int var7 = var3 >> 4;
         int var8 = var5 >> 4;
         int var9 = var3 & 15;
         int var10 = var4 >> 4;
         int var11 = var5 & 15;
         int var12 = var7 << 4;
         int var13 = var8 << 4;
         int var14 = (var7 + 1 << 4) - 1;
         int var15 = (var8 + 1 << 4) - 1;
         var6.append(
            String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", var9, var10, var11, var7, var8, var12, var13, var14, var15)
         );
      } catch (Throwable var18) {
         var6.append("(Error finding chunk loc)");
      }

      var6.append(", ");

      try {
         int var20 = var3 >> 9;
         int var21 = var5 >> 9;
         int var22 = var20 << 5;
         int var23 = var21 << 5;
         int var24 = (var20 + 1 << 5) - 1;
         int var25 = (var21 + 1 << 5) - 1;
         int var26 = var20 << 9;
         int var27 = var21 << 9;
         int var28 = (var20 + 1 << 9) - 1;
         int var16 = (var21 + 1 << 9) - 1;
         var6.append(
            String.format(
               "Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)",
               var20,
               var21,
               var22,
               var23,
               var24,
               var25,
               var26,
               var27,
               var28,
               var16
            )
         );
      } catch (Throwable var17) {
         var6.append("(Error finding world loc)");
      }

      return var6.toString();
   }

   public void addCrashSectionCallable(String var1, Callable<String> var2) {
      try {
         this.addCrashSection(var1, var2.call());
      } catch (Throwable var4) {
         this.addCrashSectionThrowable(var1, var4);
      }
   }

   public void addCrashSection(String var1, Object var2) {
      this.children.add(new CrashReportCategory.Entry(var1, var2));
   }

   public void addCrashSectionThrowable(String var1, Throwable var2) {
      this.addCrashSection(var1, var2);
   }

   public int getPrunedStackTrace(int var1) {
      StackTraceElement[] var4 = Thread.currentThread().getStackTrace();
      if (var4.length <= 0) {
         return 0;
      }

      this.stackTrace = new StackTraceElement[var4.length - 3 - var1];
      System.arraycopy(var4, 3 + var1, this.stackTrace, 0, this.stackTrace.length);
      return this.stackTrace.length;
   }

   public boolean firstTwoElementsOfStackTraceMatch(StackTraceElement var1, StackTraceElement var2) {
      if (this.stackTrace.length != 0 && var1 != null) {
         StackTraceElement var5 = this.stackTrace[0];
         if (var5.isNativeMethod() == var1.isNativeMethod()
            && var5.getClassName().equals(var1.getClassName())
            && var5.getFileName().equals(var1.getFileName())
            && var5.getMethodName().equals(var1.getMethodName())) {
            if (var2 != null != this.stackTrace.length > 1) {
               return false;
            }

            if (var2 != null && !this.stackTrace[1].equals(var2)) {
               return false;
            }

            this.stackTrace[0] = var1;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void trimStackTraceEntriesFromBottom(int var1) {
      StackTraceElement[] var2 = new StackTraceElement[this.stackTrace.length - var1];
      System.arraycopy(this.stackTrace, 0, var2, 0, var2.length);
      this.stackTrace = var2;
   }

   public void appendToStringBuilder(StringBuilder var1) {
      var1.append("-- ").append(this.name).append(" --\n");
      var1.append("Details:");

      for (CrashReportCategory.Entry var6 : this.children) {
         var1.append("\n\t");
         var1.append(var6.getKey());
         var1.append(": ");
         var1.append(var6.getValue());
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         var1.append("\nStacktrace:");

         for (StackTraceElement var8 : this.stackTrace) {
            var1.append("\n\tat ");
            var1.append(var8.toString());
         }
      }
   }

   public StackTraceElement[] getStackTrace() {
      return this.stackTrace;
   }

   public static void addBlockInfo(CrashReportCategory var0, final BlockPos var1, final Block var2, final int var3) {
      final int var6 = Block.getIdFromBlock(var2);
      var0.addCrashSectionCallable("Block type", new Callable<String>() {
         private static final long a = 117100938683758L;

         public String call() throws Exception {
            try {
               return String.format("ID #%d (%s // %s)", var6, var2.getUnlocalizedName(), var2.getClass().getCanonicalName());
            } catch (Throwable var4) {
               return "ID #" + var6;
            }
         }
      });
      var0.addCrashSectionCallable("Block data value", new Callable<String>() {
         private static final long a = 84308596675139L;

         public String call() throws Exception {
            if (var3 < 0) {
               return "Unknown? (Got " + var3 + ")";
            }

            String var3x = String.format("%4s", Integer.toBinaryString(var3)).replace(" ", "0");
            return String.format("%1$d / 0x%1$X / 0b%2$s", var3, var3x);
         }
      });
      var0.addCrashSectionCallable("Block location", new Callable<String>() {
         public String call() throws Exception {
            return CrashReportCategory.getCoordinateInfo(var1);
         }
      });
   }

   public static void addBlockInfo(CrashReportCategory var0, final BlockPos var1, final IBlockState var2) {
      var0.addCrashSectionCallable("Block", new Callable<String>() {
         public String call() throws Exception {
            return var2.toString();
         }
      });
      var0.addCrashSectionCallable("Block location", new Callable<String>() {
         public String call() throws Exception {
            return CrashReportCategory.getCoordinateInfo(var1);
         }
      });
   }

   public static class Entry {
      private final String key;
      private final String value;
      private static final long b = 9952055425890L;

      public Entry(String var1, Object var2) {
         this.key = var1;
         if (var2 == null) {
            this.value = "~~NULL~~";
         } else if (var2 instanceof Throwable) {
            Throwable var5 = (Throwable)var2;
            this.value = "~~ERROR~~ " + var5.getClass().getSimpleName() + ": " + var5.getMessage();
         } else {
            this.value = var2.toString();
         }
      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }
   }
}
