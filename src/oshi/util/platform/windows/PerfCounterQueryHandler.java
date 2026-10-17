package oshi.util.platform.windows;

import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class PerfCounterQueryHandler {
   private Map<PerfDataUtil.PerfCounter, HANDLEByReference> counterHandleMap = new ConcurrentHashMap<>();
   private Map<String, HANDLEByReference> queryHandleMap = new ConcurrentHashMap<>();
   private Map<String, List<PerfDataUtil.PerfCounter>> queryCounterMap = new ConcurrentHashMap<>();

   public PerfCounterQueryHandler() {
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            PerfCounterQueryHandler.this.removeAllCounters();
         }
      });
   }

   public boolean addCounterToObjectQuery(PerfDataUtil.PerfCounter perfCounter) {
      return this.addCounterToQuery(perfCounter, perfCounter.getObject());
   }

   public boolean addCounterToQuery(PerfDataUtil.PerfCounter counter, String key) {
      HANDLEByReference handleByReference = this.getOrOpenQuery(key);
      if (handleByReference == null) {
         return false;
      }

      HANDLEByReference handleByReference2 = new HANDLEByReference();
      if (PerfDataUtil.addCounter(handleByReference, counter.getCounterPath(), handleByReference2)) {
         this.counterHandleMap.put(counter, handleByReference2);
         List list = this.queryCounterMap.get(key);
         if (list != null) {
            list.add(counter);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean removeCounterFromObjectQuery(PerfDataUtil.PerfCounter perfCounter) {
      return this.removeCounterFromQuery(perfCounter, perfCounter.getObject());
   }

   public boolean removeCounterFromQuery(PerfDataUtil.PerfCounter counter, String key) {
      HANDLEByReference handleByReference = this.counterHandleMap.remove(counter);
      boolean bl = false;
      if (handleByReference != null) {
         bl = PerfDataUtil.removeCounter(handleByReference);
      }

      List list = this.queryCounterMap.get(key);
      if (list != null && list.remove(counter) && list.isEmpty()) {
         this.queryCounterMap.remove(key);
         PerfDataUtil.closeQuery(this.queryHandleMap.remove(key));
      }

      return bl;
   }

   public void removeAllCountersFromQuery(String queryKey) {
      List list = this.queryCounterMap.remove(queryKey);
      if (list != null) {
         for (Object object : list) {
            PerfDataUtil.PerfCounter perfCounter = (PerfDataUtil.PerfCounter)object;
            HANDLEByReference handleByReference = this.counterHandleMap.remove(perfCounter);
            if (handleByReference != null) {
               PerfDataUtil.removeCounter(handleByReference);
            }
         }

         HANDLEByReference handleByReference2 = this.queryHandleMap.remove(queryKey);
         if (handleByReference2 != null) {
            PerfDataUtil.closeQuery(handleByReference2);
         }
      }
   }

   public void removeAllCounters() {
      for (HANDLEByReference handleByReference : this.counterHandleMap.values()) {
         PerfDataUtil.removeCounter(handleByReference);
      }

      this.counterHandleMap.clear();

      for (HANDLEByReference handleByReference2 : this.queryHandleMap.values()) {
         PerfDataUtil.closeQuery(handleByReference2);
      }

      this.queryHandleMap.clear();
      this.queryCounterMap.clear();
   }

   public long updateQuery(String key) {
      return !this.queryHandleMap.containsKey(key) ? 0L : PerfDataUtil.updateQueryTimestamp(this.queryHandleMap.get(key));
   }

   public long queryCounter(PerfDataUtil.PerfCounter counter) {
      if (!this.counterHandleMap.containsKey(counter)) {
         return 0L;
      }

      long l = PerfDataUtil.queryCounter(this.counterHandleMap.get(counter));
      return l < 0L ? 0L : l;
   }

   private HANDLEByReference getOrOpenQuery(String key) {
      if (this.queryHandleMap.containsKey(key)) {
         return this.queryHandleMap.get(key);
      } else {
         HANDLEByReference handleByReference = new HANDLEByReference();
         if (PerfDataUtil.openQuery(handleByReference)) {
            this.queryHandleMap.put(key, handleByReference);
            List list = Collections.synchronizedList(new ArrayList());
            this.queryCounterMap.put(key, list);
            return handleByReference;
         } else {
            return null;
         }
      }
   }
}
