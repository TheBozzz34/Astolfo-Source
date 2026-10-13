package oshi.util.platform.windows;

import com.sun.jna.platform.win32.PdhUtil;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.sun.jna.platform.win32.PdhUtil.PdhEnumObjectItems;
import com.sun.jna.platform.win32.PdhUtil.PdhException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.NotThreadSafe;
import oshi.util.Util;

@NotThreadSafe
public class PerfCounterWildcardQuery<T extends Enum<T>> extends PerfCounterQuery<T> {
   private EnumMap<T, List<PerfDataUtil.PerfCounter>> counterListMap = null;
   private List<String> instancesFromLastQuery = new ArrayList<>();
   private final String perfObjectLocalized;
   private final String instanceFilter;
   private static final long c = 39668346806085L;
   private static final long m = 911481185077559396L;

   public PerfCounterWildcardQuery(Class<T> var1, String var2, String var3) {
      this(var1, var2, var3, var2);
   }

   public PerfCounterWildcardQuery(Class<T> var1, String var2, String var3, String var4) {
      super(var1, var2, var3, var4);
      if (((Enum[])var1.getEnumConstants()).length < 2) {
         throw new IllegalArgumentException("Enum " + var1.getName() + " must have at least two elements, an instance filter and a counter.");
      }

      this.instanceFilter = ((PerfCounterWildcardQuery.PdhCounterWildcardProperty)((Enum[])var1.getEnumConstants())[0]).getCounter().toLowerCase();
      this.perfObjectLocalized = localize(this.perfObject);
   }

   private static String localize(String var0) {
      String var4 = null;

      try {
         var4 = PdhUtil.PdhLookupPerfNameByIndex(null, PdhUtil.PdhLookupPerfIndexByEnglishName(var0));
      } catch (Win32Exception var6) {
      } catch (PdhException var7) {
      }

      return var4 != null && var4.length() != 0 ? var4 : var0;
   }

   @Override
   public boolean initPdhCounters() {
      return this.fillCounterListMap();
   }

   @Override
   public void unInitPdhCounters() {
      this.pdhQueryHandler.removeAllCountersFromQuery(this.queryKey);
      this.counterListMap = null;
   }

   @Override
   public Map<T, Long> queryValues() {
      throw new UnsupportedOperationException("Use queryValuesWildcard() on this class.");
   }

   public Map<T, List<Long>> queryValuesWildcard() {
      EnumMap var4 = new EnumMap<>(this.propertyEnum);
      this.instancesFromLastQuery.clear();
      Enum[] var5 = this.propertyEnum.getEnumConstants();
      if (this.source.equals(PerfCounterQuery.CounterDataSource.PDH)) {
         if (this.initPdhCounters() && this.queryPdhWildcard(var4, (T[])var5)) {
            this.unInitPdhCounters();
         } else {
            this.setDataSource(PerfCounterQuery.CounterDataSource.WMI);
         }
      }

      if (this.source.equals(PerfCounterQuery.CounterDataSource.WMI)) {
         this.queryWmiWildcard(var4, (T[])var5);
      }

      return var4;
   }

   private boolean queryPdhWildcard(Map<T, List<Long>> var1, T[] var2) {
      if (this.counterListMap != null && 0L < this.pdhQueryHandler.updateQuery(this.queryKey)) {
         for (int var6 = 1; var6 < var2.length; var6++) {
            Enum var7 = var2[var6];
            ArrayList var8 = new ArrayList();

            for (PerfDataUtil.PerfCounter var10 : this.counterListMap.get(var7)) {
               var8.add(this.pdhQueryHandler.queryCounter(var10));
               if (var6 == 1) {
                  this.instancesFromLastQuery.add(var10.getInstance());
               }
            }

            var1.put((T)(var7), var8);
         }

         return true;
      } else {
         return false;
      }
   }

   private void queryWmiWildcard(Map<T, List<Long>> var1, T[] var2) {
      WmiResult var6 = WmiQueryHandler.createInstance().queryWMI(this.counterQuery);
      if (var6.getResultCount() > 0) {
         for (int var7 = 0; var7 < var6.getResultCount(); var7++) {
            this.instancesFromLastQuery.add(WmiUtil.getString(var6, (T)var2[0], var7));
         }

         for (int var11 = 1; var11 < var2.length; var11++) {
            Enum var8 = var2[var11];
            ArrayList var9 = new ArrayList();

            for (int var10 = 0; var10 < var6.getResultCount(); var10++) {
               switch (var6.getCIMType((T)var8)) {
                  case 18:
                     var9.add((long)WmiUtil.getUint16(var6, (T)var8, var10));
                     break;
                  case 19:
                     var9.add(WmiUtil.getUint32asLong(var6, (T)var8, var10));
                     break;
                  case 21:
                     var9.add(WmiUtil.getUint64(var6, (T)var8, var10));
                     break;
                  case 101:
                     var9.add(WmiUtil.getDateTime(var6, (T)var8, var10).toInstant().toEpochMilli());
                     break;
                  default:
                     throw new ClassCastException("Unimplemented CIM Type Mapping.");
               }
            }

            var1.put((T)(var8), var9);
         }
      }
   }

   public List<String> getInstancesFromLastQuery() {
      return this.instancesFromLastQuery;
   }

   private boolean fillCounterListMap() {
      PdhEnumObjectItems var4;
      try {
         var4 = PdhUtil.PdhEnumObjectItems(null, null, this.perfObjectLocalized, (int)m);
      } catch (PdhException var13) {
         return false;
      }

      List var5 = var4.getInstances();
      var5.removeIf(var1 -> !Util.wildcardMatch(((java.lang.String)var1).toLowerCase(), this.instanceFilter));
      HashSet var6 = new HashSet(var5);
      this.counterListMap = new EnumMap<>(this.propertyEnum);

      for (int var7 = 1; var7 < ((Enum[])this.propertyEnum.getEnumConstants()).length; var7++) {
         Enum var8 = ((Enum[])this.propertyEnum.getEnumConstants())[var7];
         ArrayList var9 = new ArrayList(var5.size());

         for (Object var11$raw : var6) {
            String var11 = (String)var11$raw;
            PerfDataUtil.PerfCounter var12 = PerfDataUtil.createCounter(
               this.perfObject, var11, ((PerfCounterWildcardQuery.PdhCounterWildcardProperty)var8).getCounter()
            );
            if (!this.pdhQueryHandler.addCounterToQuery(var12, this.queryKey)) {
               this.unInitPdhCounters();
               return false;
            }

            var9.add(var12);
         }

         this.counterListMap.put((T)var8, var9);
      }

      return this.counterListMap.size() > 0;
   }

   public interface PdhCounterWildcardProperty {
      String getCounter();
   }
}
