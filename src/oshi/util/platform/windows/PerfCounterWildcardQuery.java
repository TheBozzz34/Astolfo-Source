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

   public PerfCounterWildcardQuery(Class<T> propertyEnum, String perfObject, String perfWmiClass) {
      this(propertyEnum, perfObject, perfWmiClass, perfObject);
   }

   public PerfCounterWildcardQuery(Class<T> propertyEnum, String perfObject, String perfWmiClass, String queryKey) {
      super(propertyEnum, perfObject, perfWmiClass, queryKey);
      if (((Enum[])propertyEnum.getEnumConstants()).length < 2) {
         throw new IllegalArgumentException("Enum " + propertyEnum.getName() + " must have at least two elements, an instance filter and a counter.");
      }

      this.instanceFilter = ((PerfCounterWildcardQuery.PdhCounterWildcardProperty)((Enum[])propertyEnum.getEnumConstants())[0]).getCounter().toLowerCase();
      this.perfObjectLocalized = localize(this.perfObject);
   }

   private static String localize(String perfObject) {
      String string = null;

      try {
         string = PdhUtil.PdhLookupPerfNameByIndex(null, PdhUtil.PdhLookupPerfIndexByEnglishName(perfObject));
      } catch (Win32Exception win32Exception) {
      } catch (PdhException pdhException) {
      }

      return string != null && string.length() != 0 ? string : perfObject;
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
      EnumMap enumMap = new EnumMap<>(this.propertyEnum);
      this.instancesFromLastQuery.clear();
      Enum[] enums = this.propertyEnum.getEnumConstants();
      if (this.source.equals(PerfCounterQuery.CounterDataSource.PDH)) {
         if (this.initPdhCounters() && this.queryPdhWildcard(enumMap, (T[])enums)) {
            this.unInitPdhCounters();
         } else {
            this.setDataSource(PerfCounterQuery.CounterDataSource.WMI);
         }
      }

      if (this.source.equals(PerfCounterQuery.CounterDataSource.WMI)) {
         this.queryWmiWildcard(enumMap, (T[])enums);
      }

      return enumMap;
   }

   private boolean queryPdhWildcard(Map<T, List<Long>> valueMap, T[] props) {
      if (this.counterListMap != null && 0L < this.pdhQueryHandler.updateQuery(this.queryKey)) {
         for (int i = 1; i < props.length; i++) {
            Enum enum2 = props[i];
            ArrayList arrayList = new ArrayList();

            for (PerfDataUtil.PerfCounter perfCounter : this.counterListMap.get(enum2)) {
               arrayList.add(this.pdhQueryHandler.queryCounter(perfCounter));
               if (i == 1) {
                  this.instancesFromLastQuery.add(perfCounter.getInstance());
               }
            }

            valueMap.put((T)(enum2), arrayList);
         }

         return true;
      } else {
         return false;
      }
   }

   private void queryWmiWildcard(Map<T, List<Long>> valueMap, T[] props) {
      WmiResult wmiResult = WmiQueryHandler.createInstance().queryWMI(this.counterQuery);
      if (wmiResult.getResultCount() > 0) {
         for (int i = 0; i < wmiResult.getResultCount(); i++) {
            this.instancesFromLastQuery.add(WmiUtil.getString(wmiResult, (T)props[0], i));
         }

         for (int j = 1; j < props.length; j++) {
            Enum enum2 = props[j];
            ArrayList arrayList = new ArrayList();

            for (int k = 0; k < wmiResult.getResultCount(); k++) {
               switch (wmiResult.getCIMType((T)enum2)) {
                  case 18:
                     arrayList.add((long)WmiUtil.getUint16(wmiResult, (T)enum2, k));
                     break;
                  case 19:
                     arrayList.add(WmiUtil.getUint32asLong(wmiResult, (T)enum2, k));
                     break;
                  case 21:
                     arrayList.add(WmiUtil.getUint64(wmiResult, (T)enum2, k));
                     break;
                  case 101:
                     arrayList.add(WmiUtil.getDateTime(wmiResult, (T)enum2, k).toInstant().toEpochMilli());
                     break;
                  default:
                     throw new ClassCastException("Unimplemented CIM Type Mapping.");
               }
            }

            valueMap.put((T)(enum2), arrayList);
         }
      }
   }

   public List<String> getInstancesFromLastQuery() {
      return this.instancesFromLastQuery;
   }

   private boolean fillCounterListMap() {
      PdhEnumObjectItems pdhEnumObjectItems;
      try {
         pdhEnumObjectItems = PdhUtil.PdhEnumObjectItems(null, null, this.perfObjectLocalized, 100);
      } catch (PdhException pdhException) {
         return false;
      }

      List list = pdhEnumObjectItems.getInstances();
      list.removeIf(i -> !Util.wildcardMatch(((java.lang.String)i).toLowerCase(), this.instanceFilter));
      HashSet hashSet = new HashSet(list);
      this.counterListMap = new EnumMap<>(this.propertyEnum);

      for (int j = 1; j < ((Enum[])this.propertyEnum.getEnumConstants()).length; j++) {
         Enum enum2 = ((Enum[])this.propertyEnum.getEnumConstants())[j];
         ArrayList arrayList = new ArrayList(list.size());

         for (Object object : hashSet) {
            String string = (String)object;
            PerfDataUtil.PerfCounter perfCounter = PerfDataUtil.createCounter(
               this.perfObject, string, ((PerfCounterWildcardQuery.PdhCounterWildcardProperty)enum2).getCounter()
            );
            if (!this.pdhQueryHandler.addCounterToQuery(perfCounter, this.queryKey)) {
               this.unInitPdhCounters();
               return false;
            }

            arrayList.add(perfCounter);
         }

         this.counterListMap.put((T)enum2, arrayList);
      }

      return this.counterListMap.size() > 0;
   }

   public interface PdhCounterWildcardProperty {
      String getCounter();
   }
}
