package oshi.driver.windows.registry;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinPerf.PERF_COUNTER_BLOCK;
import com.sun.jna.platform.win32.WinPerf.PERF_COUNTER_DEFINITION;
import com.sun.jna.platform.win32.WinPerf.PERF_DATA_BLOCK;
import com.sun.jna.platform.win32.WinPerf.PERF_INSTANCE_DEFINITION;
import com.sun.jna.platform.win32.WinPerf.PERF_OBJECT_TYPE;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class HkeyPerformanceDataUtil {
   private static final Map<String, Integer> COUNTER_INDEX_MAP = mapCounterIndicesFromRegistry();
   private static final long b = 51218759850169L;

   private HkeyPerformanceDataUtil() {
   }

   public static <T extends Enum<T> & PerfCounterWildcardQuery.PdhCounterWildcardProperty> Triplet<List<Map<T, Object>>, Long, Long> readPerfDataFromRegistry(
      String var0, Class<T> var1
   ) {
      Pair var5 = getCounterIndices(var0, var1);
      if (var5 == null) {
         return null;
      }

      Memory var6 = readPerfDataBuffer(var0);
      if (var6 == null) {
         return null;
      }

      PERF_DATA_BLOCK var7 = new PERF_DATA_BLOCK(var6.share(0L));
      long var8 = var7.PerfTime100nSec.getValue();
      long var10 = System.currentTimeMillis();
      long var12 = var7.HeaderLength;

      for (int var14 = 0; var14 < var7.NumObjectTypes; var14++) {
         PERF_OBJECT_TYPE var15 = new PERF_OBJECT_TYPE(var6.share(var12));
         if (var15.ObjectNameTitleIndex == COUNTER_INDEX_MAP.get(var0)) {
            long var16 = var12 + var15.HeaderLength;
            HashMap var18 = new HashMap();
            HashMap var19 = new HashMap();

            for (int var20 = 0; var20 < var15.NumCounters; var20++) {
               PERF_COUNTER_DEFINITION var21 = new PERF_COUNTER_DEFINITION(var6.share(var16));
               var18.put(var21.CounterNameTitleIndex, var21.CounterOffset);
               var19.put(var21.CounterNameTitleIndex, var21.CounterSize);
               var16 += var21.ByteLength;
            }

            long var33 = var12 + var15.DefinitionLength;
            ArrayList var22 = new ArrayList(var15.NumInstances);

            for (int var23 = 0; var23 < var15.NumInstances; var23++) {
               PERF_INSTANCE_DEFINITION var24 = new PERF_INSTANCE_DEFINITION(var6.share(var33));
               long var25 = var33 + var24.ByteLength;
               EnumMap var27 = new EnumMap(var1);
               Enum[] var28 = (Enum[])var1.getEnumConstants();
               var27.put(var28[0], var6.getWideString(var33 + var24.NameOffset));

               for (int var29 = 1; var29 < var28.length; var29++) {
                  Enum var30 = var28[var29];
                  int var31 = COUNTER_INDEX_MAP.get(((PerfCounterWildcardQuery.PdhCounterWildcardProperty)var30).getCounter());
                  int var32 = (int)(var19.getOrDefault(var31, 0));
                  if (var32 == 4) {
                     var27.put(var30, var6.getInt(var25 + ((Integer)var18.get(var31)).intValue()));
                  } else if (var32 == 8) {
                     var27.put(var30, var6.getLong(var25 + ((Integer)var18.get(var31)).intValue()));
                  } else {
                     var27.put(var30, null);
                  }
               }

               var22.add(var27);
               var33 = var25 + (new PERF_COUNTER_BLOCK(var6.share(var25))).ByteLength;
            }

            return new Triplet<>(var22, var8, var10);
         }

         var12 += var15.TotalByteLength;
      }

      return null;
   }

   private static <T extends Enum<T> & PerfCounterWildcardQuery.PdhCounterWildcardProperty> Pair<Integer, EnumMap<T, Integer>> getCounterIndices(
      String var0, Class<T> var1
   ) {
      if (!COUNTER_INDEX_MAP.containsKey(var0)) {
         return null;
      }

      int var5 = COUNTER_INDEX_MAP.get(var0);
      Enum[] var6 = (Enum[])var1.getEnumConstants();
      EnumMap var7 = new EnumMap(var1);

      for (int var8 = 1; var8 < var6.length; var8++) {
         Enum var9 = var6[var8];
         String var10 = ((PerfCounterWildcardQuery.PdhCounterWildcardProperty)var9).getCounter();
         if (!COUNTER_INDEX_MAP.containsKey(var10)) {
            return null;
         }

         var7.put(var9, COUNTER_INDEX_MAP.get(var10));
      }

      return new Pair<>(var5, var7);
   }

   private static Memory readPerfDataBuffer(String var0) {
      String var4 = Integer.toString(COUNTER_INDEX_MAP.get(var0));
      short var5 = 4096;
      IntByReference var6 = new IntByReference(var5);
      Memory var7 = new Memory(var5);
      int var8 = Advapi32.INSTANCE.RegQueryValueEx(WinReg.HKEY_PERFORMANCE_DATA, var4, 0, null, var7, var6);
      if (var8 != 0 && var8 != 234) {
         return null;
      }

      while (var8 == 234) {
         var5 += 4096;
         var6.setValue(var5);
         var7 = new Memory(var5);
         var8 = Advapi32.INSTANCE.RegQueryValueEx(WinReg.HKEY_PERFORMANCE_DATA, var4, 0, null, var7, var6);
      }

      return var7;
   }

   private static Map<String, Integer> mapCounterIndicesFromRegistry() {
      HashMap var2 = new HashMap();

      try {
         String[] var3 = Advapi32Util.registryGetStringArray(
            WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009", "Counter"
         );

         for (int var4 = 1; var4 < var3.length; var4 += 2) {
            var2.put(var3[var4], Integer.parseInt(var3[var4 - 1]));
         }
      } catch (Win32Exception var5) {
      } catch (NumberFormatException var6) {
      }

      return Collections.unmodifiableMap(var2);
   }
}
