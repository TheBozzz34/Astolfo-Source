package oshi.util.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import java.util.EnumMap;
import java.util.Map;
import oshi.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PerfCounterQuery<T extends Enum<T>> {
   public final Class<T> propertyEnum;
   public final String perfObject;
   public final String perfWmiClass;
   public final String queryKey;
   public PerfCounterQuery.CounterDataSource source;
   public PerfCounterQueryHandler pdhQueryHandler;
   private EnumMap<T, PerfDataUtil.PerfCounter> counterMap = null;
   public WmiQuery<T> counterQuery = null;
   public static final String TOTAL_INSTANCE = "_Total";
   public static final String TOTAL_INSTANCES = "*_Total";
   public static final String NOT_TOTAL_INSTANCE = "^_Total";
   public static final String NOT_TOTAL_INSTANCES = "^*_Total";
   private static final long a = 12682909698198L;

   public PerfCounterQuery(Class<T> var1, String var2, String var3) {
      this(var1, var2, var3, var2);
   }

   public PerfCounterQuery(Class<T> var1, String var2, String var3, String var4) {
      this.propertyEnum = var1;
      this.perfObject = var2;
      this.perfWmiClass = var3;
      this.queryKey = var4;
      this.pdhQueryHandler = new PerfCounterQueryHandler();
      this.source = PerfCounterQuery.CounterDataSource.PDH;
   }

   public boolean setDataSource(PerfCounterQuery.CounterDataSource var1) {
      this.source = var1;
      switch (var1) {
         case PDH:
            this.unInitWmiCounters();
            return this.initPdhCounters();
         case WMI:
            this.unInitPdhCounters();
            this.initWmiCounters();
            return true;
         default:
            throw new IllegalArgumentException("Invalid Data Source specified.");
      }
   }

   public boolean initPdhCounters() {
      this.counterMap = new EnumMap<>(this.propertyEnum);

      for (Enum var7 : (Enum[])this.propertyEnum.getEnumConstants()) {
         PerfDataUtil.PerfCounter var8 = PerfDataUtil.createCounter(
            this.perfObject, ((PerfCounterQuery.PdhCounterProperty)var7).getInstance(), ((PerfCounterQuery.PdhCounterProperty)var7).getCounter()
         );
         this.counterMap.put((T)var7, var8);
         if (!this.pdhQueryHandler.addCounterToQuery(var8, this.queryKey)) {
            this.unInitPdhCounters();
            return false;
         }
      }

      return true;
   }

   public void unInitPdhCounters() {
      this.pdhQueryHandler.removeAllCountersFromQuery(this.queryKey);
      this.counterMap = null;
   }

   public void initWmiCounters() {
      this.counterQuery = new WmiQuery<>(this.perfWmiClass, this.propertyEnum);
   }

   public void unInitWmiCounters() {
      this.counterQuery = null;
   }

   public Map<T, Long> queryValues() {
      EnumMap var4 = new EnumMap<>(this.propertyEnum);
      Enum[] var5 = this.propertyEnum.getEnumConstants();
      if (this.source.equals(PerfCounterQuery.CounterDataSource.PDH)) {
         if (this.initPdhCounters() && this.queryPdh(var4, (T[])var5)) {
            this.unInitPdhCounters();
         } else {
            this.setDataSource(PerfCounterQuery.CounterDataSource.WMI);
         }
      }

      if (this.source.equals(PerfCounterQuery.CounterDataSource.WMI)) {
         this.queryWmi(var4, (T[])var5);
      }

      return var4;
   }

   private boolean queryPdh(Map<T, Long> var1, T[] var2) {
      if (this.counterMap != null && 0L < this.pdhQueryHandler.updateQuery(this.queryKey)) {
         for (Enum var9 : var2) {
            var1.put((T)(var9), this.pdhQueryHandler.queryCounter(this.counterMap.get(var9)));
         }

         return true;
      } else {
         return false;
      }
   }

   private void queryWmi(Map<T, Long> var1, T[] var2) {
      WmiResult var6 = WmiQueryHandler.createInstance().queryWMI(this.counterQuery);
      if (var6.getResultCount() > 0) {
         for (Enum var10 : var2) {
            switch (var6.getCIMType((T)var10)) {
               case 18:
                  var1.put((T)(var10), (long)WmiUtil.getUint16(var6, (T)var10, 0));
                  break;
               case 19:
                  var1.put((T)(var10), WmiUtil.getUint32asLong(var6, (T)var10, 0));
                  break;
               case 20:
               default:
                  throw new ClassCastException("Unimplemented CIM Type Mapping.");
               case 21:
                  var1.put((T)(var10), WmiUtil.getUint64(var6, (T)var10, 0));
            }
         }
      }
   }

   public enum CounterDataSource {
      PDH,
      WMI;
   }

   public interface PdhCounterProperty {
      String getInstance();

      String getCounter();
   }
}
