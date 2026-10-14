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

   public PerfCounterQuery(Class<T> propertyEnum, String perfObject, String perfWmiClass) {
      this(propertyEnum, perfObject, perfWmiClass, perfObject);
   }

   public PerfCounterQuery(Class<T> propertyEnum, String perfObject, String perfWmiClass, String queryKey) {
      this.propertyEnum = propertyEnum;
      this.perfObject = perfObject;
      this.perfWmiClass = perfWmiClass;
      this.queryKey = queryKey;
      this.pdhQueryHandler = new PerfCounterQueryHandler();
      this.source = PerfCounterQuery.CounterDataSource.PDH;
   }

   public boolean setDataSource(PerfCounterQuery.CounterDataSource source) {
      this.source = source;
      switch (source) {
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

      for (Enum enum2 : (Enum[])this.propertyEnum.getEnumConstants()) {
         PerfDataUtil.PerfCounter perfCounter = PerfDataUtil.createCounter(
            this.perfObject, ((PerfCounterQuery.PdhCounterProperty)enum2).getInstance(), ((PerfCounterQuery.PdhCounterProperty)enum2).getCounter()
         );
         this.counterMap.put((T)enum2, perfCounter);
         if (!this.pdhQueryHandler.addCounterToQuery(perfCounter, this.queryKey)) {
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
      EnumMap enumMap = new EnumMap<>(this.propertyEnum);
      Enum[] enums = this.propertyEnum.getEnumConstants();
      if (this.source.equals(PerfCounterQuery.CounterDataSource.PDH)) {
         if (this.initPdhCounters() && this.queryPdh(enumMap, (T[])enums)) {
            this.unInitPdhCounters();
         } else {
            this.setDataSource(PerfCounterQuery.CounterDataSource.WMI);
         }
      }

      if (this.source.equals(PerfCounterQuery.CounterDataSource.WMI)) {
         this.queryWmi(enumMap, (T[])enums);
      }

      return enumMap;
   }

   private boolean queryPdh(Map<T, Long> valueMap, T[] props) {
      if (this.counterMap != null && 0L < this.pdhQueryHandler.updateQuery(this.queryKey)) {
         for (Enum enum2 : props) {
            valueMap.put((T)(enum2), this.pdhQueryHandler.queryCounter(this.counterMap.get(enum2)));
         }

         return true;
      } else {
         return false;
      }
   }

   private void queryWmi(Map<T, Long> valueMap, T[] props) {
      WmiResult wmiResult = WmiQueryHandler.createInstance().queryWMI(this.counterQuery);
      if (wmiResult.getResultCount() > 0) {
         for (Enum enum2 : props) {
            switch (wmiResult.getCIMType((T)enum2)) {
               case 18:
                  valueMap.put((T)(enum2), (long)WmiUtil.getUint16(wmiResult, (T)enum2, 0));
                  break;
               case 19:
                  valueMap.put((T)(enum2), WmiUtil.getUint32asLong(wmiResult, (T)enum2, 0));
                  break;
               case 20:
               default:
                  throw new ClassCastException("Unimplemented CIM Type Mapping.");
               case 21:
                  valueMap.put((T)(enum2), WmiUtil.getUint64(wmiResult, (T)enum2, 0));
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
