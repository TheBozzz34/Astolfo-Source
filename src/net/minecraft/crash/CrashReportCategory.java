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

   public CrashReportCategory(CrashReport report, String name) {
      this.crashReport = report;
      this.name = name;
   }

   public static String getCoordinateInfo(double x, double y, double z) {
      return String.format("%.2f,%.2f,%.2f - %s", x, y, z, getCoordinateInfo(new BlockPos(x, y, z)));
   }

   public static String getCoordinateInfo(BlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      StringBuilder stringBuilder = new StringBuilder();

      try {
         stringBuilder.append(String.format("World: (%d,%d,%d)", i, j, k));
      } catch (Throwable throwable) {
         stringBuilder.append("(Error finding world loc)");
      }

      stringBuilder.append(", ");

      try {
         int l = i >> 4;
         int m = k >> 4;
         int n = i & 15;
         int i2 = j >> 4;
         int i3 = k & 15;
         int i4 = l << 4;
         int i5 = m << 4;
         int i6 = (l + 1 << 4) - 1;
         int i7 = (m + 1 << 4) - 1;
         stringBuilder.append(
            String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", n, i2, i3, l, m, i4, i5, i6, i7)
         );
      } catch (Throwable throwable2) {
         stringBuilder.append("(Error finding chunk loc)");
      }

      stringBuilder.append(", ");

      try {
         int i8 = i >> 9;
         int i9 = k >> 9;
         int i10 = i8 << 5;
         int i11 = i9 << 5;
         int i12 = (i8 + 1 << 5) - 1;
         int i13 = (i9 + 1 << 5) - 1;
         int i14 = i8 << 9;
         int i15 = i9 << 9;
         int i16 = (i8 + 1 << 9) - 1;
         int i17 = (i9 + 1 << 9) - 1;
         stringBuilder.append(
            String.format(
               "Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)",
               i8,
               i9,
               i10,
               i11,
               i12,
               i13,
               i14,
               i15,
               i16,
               i17
            )
         );
      } catch (Throwable throwable3) {
         stringBuilder.append("(Error finding world loc)");
      }

      return stringBuilder.toString();
   }

   public void addCrashSectionCallable(String sectionName, Callable<String> callable) {
      try {
         this.addCrashSection(sectionName, callable.call());
      } catch (Throwable throwable) {
         this.addCrashSectionThrowable(sectionName, throwable);
      }
   }

   public void addCrashSection(String sectionName, Object value) {
      this.children.add(new CrashReportCategory.Entry(sectionName, value));
   }

   public void addCrashSectionThrowable(String sectionName, Throwable throwable) {
      this.addCrashSection(sectionName, throwable);
   }

   public int getPrunedStackTrace(int size) {
      StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
      if (stackTraceElements.length <= 0) {
         return 0;
      }

      this.stackTrace = new StackTraceElement[stackTraceElements.length - 3 - size];
      System.arraycopy(stackTraceElements, 3 + size, this.stackTrace, 0, this.stackTrace.length);
      return this.stackTrace.length;
   }

   public boolean firstTwoElementsOfStackTraceMatch(StackTraceElement s1, StackTraceElement s2) {
      if (this.stackTrace.length != 0 && s1 != null) {
         StackTraceElement stackTraceElement = this.stackTrace[0];
         if (stackTraceElement.isNativeMethod() == s1.isNativeMethod()
            && stackTraceElement.getClassName().equals(s1.getClassName())
            && stackTraceElement.getFileName().equals(s1.getFileName())
            && stackTraceElement.getMethodName().equals(s1.getMethodName())) {
            if (s2 != null != this.stackTrace.length > 1) {
               return false;
            }

            if (s2 != null && !this.stackTrace[1].equals(s2)) {
               return false;
            }

            this.stackTrace[0] = s1;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void trimStackTraceEntriesFromBottom(int amount) {
      StackTraceElement[] stackTraceElements = new StackTraceElement[this.stackTrace.length - amount];
      System.arraycopy(this.stackTrace, 0, stackTraceElements, 0, stackTraceElements.length);
      this.stackTrace = stackTraceElements;
   }

   public void appendToStringBuilder(StringBuilder builder) {
      builder.append("-- ").append(this.name).append(" --\n");
      builder.append("Details:");

      for (CrashReportCategory.Entry entry : this.children) {
         builder.append("\n\t");
         builder.append(entry.getKey());
         builder.append(": ");
         builder.append(entry.getValue());
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         builder.append("\nStacktrace:");

         for (StackTraceElement stackTraceElement : this.stackTrace) {
            builder.append("\n\tat ");
            builder.append(stackTraceElement.toString());
         }
      }
   }

   public StackTraceElement[] getStackTrace() {
      return this.stackTrace;
   }

   public static void addBlockInfo(CrashReportCategory category, final BlockPos pos, final Block blockIn, final int blockData) {
      final int i = Block.getIdFromBlock(blockIn);
      category.addCrashSectionCallable("Block type", new Callable<String>() {
         private static final long a = 117100938683758L;

         public String call() throws Exception {
            try {
               return String.format("ID #%d (%s // %s)", i, blockIn.getUnlocalizedName(), blockIn.getClass().getCanonicalName());
            } catch (Throwable throwable) {
               return "ID #" + i;
            }
         }
      });
      category.addCrashSectionCallable("Block data value", new Callable<String>() {
         private static final long a = 84308596675139L;

         public String call() throws Exception {
            if (blockData < 0) {
               return "Unknown? (Got " + blockData + ")";
            }

            String string = String.format("%4s", Integer.toBinaryString(blockData)).replace(" ", "0");
            return String.format("%1$d / 0x%1$X / 0b%2$s", blockData, string);
         }
      });
      category.addCrashSectionCallable("Block location", new Callable<String>() {
         public String call() throws Exception {
            return CrashReportCategory.getCoordinateInfo(pos);
         }
      });
   }

   public static void addBlockInfo(CrashReportCategory category, final BlockPos pos, final IBlockState state) {
      category.addCrashSectionCallable("Block", new Callable<String>() {
         public String call() throws Exception {
            return state.toString();
         }
      });
      category.addCrashSectionCallable("Block location", new Callable<String>() {
         public String call() throws Exception {
            return CrashReportCategory.getCoordinateInfo(pos);
         }
      });
   }

   public static class Entry {
      private final String key;
      private final String value;
      private static final long b = 9952055425890L;

      public Entry(String key, Object value) {
         this.key = key;
         if (value == null) {
            this.value = "~~NULL~~";
         } else if (value instanceof Throwable) {
            Throwable throwable = (Throwable)value;
            this.value = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
         } else {
            this.value = value.toString();
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
