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
      String objectName, Class<T> counterEnum
   ) {
      Pair pair = getCounterIndices(objectName, counterEnum);
      if (pair == null) {
         return null;
      }

      Memory memory = readPerfDataBuffer(objectName);
      if (memory == null) {
         return null;
      }

      PERF_DATA_BLOCK perF_DATA_BLOCK = new PERF_DATA_BLOCK(memory.share(0L));
      long l = perF_DATA_BLOCK.PerfTime100nSec.getValue();
      long m = System.currentTimeMillis();
      long n = perF_DATA_BLOCK.HeaderLength;

      for (int i = 0; i < perF_DATA_BLOCK.NumObjectTypes; i++) {
         PERF_OBJECT_TYPE perF_OBJECT_TYPE = new PERF_OBJECT_TYPE(memory.share(n));
         if (perF_OBJECT_TYPE.ObjectNameTitleIndex == COUNTER_INDEX_MAP.get(objectName)) {
            long l2 = n + perF_OBJECT_TYPE.HeaderLength;
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();

            for (int j = 0; j < perF_OBJECT_TYPE.NumCounters; j++) {
               PERF_COUNTER_DEFINITION perF_COUNTER_DEFINITION = new PERF_COUNTER_DEFINITION(memory.share(l2));
               hashMap.put(perF_COUNTER_DEFINITION.CounterNameTitleIndex, perF_COUNTER_DEFINITION.CounterOffset);
               hashMap2.put(perF_COUNTER_DEFINITION.CounterNameTitleIndex, perF_COUNTER_DEFINITION.CounterSize);
               l2 += perF_COUNTER_DEFINITION.ByteLength;
            }

            long l3 = n + perF_OBJECT_TYPE.DefinitionLength;
            ArrayList arrayList = new ArrayList(perF_OBJECT_TYPE.NumInstances);

            for (int k = 0; k < perF_OBJECT_TYPE.NumInstances; k++) {
               PERF_INSTANCE_DEFINITION perF_INSTANCE_DEFINITION = new PERF_INSTANCE_DEFINITION(memory.share(l3));
               long l4 = l3 + perF_INSTANCE_DEFINITION.ByteLength;
               EnumMap enumMap = new EnumMap(counterEnum);
               Enum[] enums = (Enum[])counterEnum.getEnumConstants();
               enumMap.put(enums[0], memory.getWideString(l3 + perF_INSTANCE_DEFINITION.NameOffset));

               for (int i2 = 1; i2 < enums.length; i2++) {
                  Enum enum2 = enums[i2];
                  int i3 = COUNTER_INDEX_MAP.get(((PerfCounterWildcardQuery.PdhCounterWildcardProperty)enum2).getCounter());
                  int i4 = (int)(hashMap2.getOrDefault(i3, 0));
                  if (i4 == 4) {
                     enumMap.put(enum2, memory.getInt(l4 + ((Integer)hashMap.get(i3)).intValue()));
                  } else if (i4 == 8) {
                     enumMap.put(enum2, memory.getLong(l4 + ((Integer)hashMap.get(i3)).intValue()));
                  } else {
                     enumMap.put(enum2, null);
                  }
               }

               arrayList.add(enumMap);
               l3 = l4 + (new PERF_COUNTER_BLOCK(memory.share(l4))).ByteLength;
            }

            return new Triplet<>(arrayList, l, m);
         }

         n += perF_OBJECT_TYPE.TotalByteLength;
      }

      return null;
   }

   private static <T extends Enum<T> & PerfCounterWildcardQuery.PdhCounterWildcardProperty> Pair<Integer, EnumMap<T, Integer>> getCounterIndices(
      String objectName, Class<T> counterEnum
   ) {
      if (!COUNTER_INDEX_MAP.containsKey(objectName)) {
         return null;
      }

      int i = COUNTER_INDEX_MAP.get(objectName);
      Enum[] enums = (Enum[])counterEnum.getEnumConstants();
      EnumMap enumMap = new EnumMap(counterEnum);

      for (int j = 1; j < enums.length; j++) {
         Enum enum2 = enums[j];
         String string = ((PerfCounterWildcardQuery.PdhCounterWildcardProperty)enum2).getCounter();
         if (!COUNTER_INDEX_MAP.containsKey(string)) {
            return null;
         }

         enumMap.put(enum2, COUNTER_INDEX_MAP.get(string));
      }

      return new Pair<>(i, enumMap);
   }

   private static Memory readPerfDataBuffer(String objectName) {
      String string = Integer.toString(COUNTER_INDEX_MAP.get(objectName));
      short s = 4096;
      IntByReference intByReference = new IntByReference(s);
      Memory memory = new Memory(s);
      int i = Advapi32.INSTANCE.RegQueryValueEx(WinReg.HKEY_PERFORMANCE_DATA, string, 0, null, memory, intByReference);
      if (i != 0 && i != 234) {
         return null;
      }

      while (i == 234) {
         s += 4096;
         intByReference.setValue(s);
         memory = new Memory(s);
         i = Advapi32.INSTANCE.RegQueryValueEx(WinReg.HKEY_PERFORMANCE_DATA, string, 0, null, memory, intByReference);
      }

      return memory;
   }

   private static Map<String, Integer> mapCounterIndicesFromRegistry() {
      HashMap hashMap = new HashMap();

      try {
         String[] strings = Advapi32Util.registryGetStringArray(
            WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009", "Counter"
         );

         for (int i = 1; i < strings.length; i += 2) {
            hashMap.put(strings[i], Integer.parseInt(strings[i - 1]));
         }
      } catch (Win32Exception win32Exception) {
      } catch (NumberFormatException numberFormatException) {
      }

      return Collections.unmodifiableMap(hashMap);
   }
}
