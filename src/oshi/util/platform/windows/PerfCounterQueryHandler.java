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
   private static final long a = 138244549769680L;

   public PerfCounterQueryHandler() {
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            PerfCounterQueryHandler.this.removeAllCounters();
         }
      });
   }

   public boolean a(PerfDataUtil.PerfCounter var1) {
      return this.addCounterToQuery(var1, var1.c());
   }

   public boolean addCounterToQuery(PerfDataUtil.PerfCounter var1, String var2) {
      HANDLEByReference var6 = this.getOrOpenQuery(var2);
      if (var6 == null) {
         return false;
      }

      HANDLEByReference var7 = new HANDLEByReference();
      if (PerfDataUtil.addCounter(var6, var1.getCounterPath(), var7)) {
         this.counterHandleMap.put(var1, var7);
         List var8 = this.queryCounterMap.get(var2);
         if (var8 != null) {
            var8.add(var1);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean O(PerfDataUtil.PerfCounter var1) {
      return this.removeCounterFromQuery(var1, var1.c());
   }

   public boolean removeCounterFromQuery(PerfDataUtil.PerfCounter var1, String var2) {
      HANDLEByReference var6 = this.counterHandleMap.remove(var1);
      boolean var7 = false;
      if (var6 != null) {
         var7 = PerfDataUtil.removeCounter(var6);
      }

      List var8 = this.queryCounterMap.get(var2);
      if (var8 != null && var8.remove(var1) && var8.isEmpty()) {
         this.queryCounterMap.remove(var2);
         PerfDataUtil.closeQuery(this.queryHandleMap.remove(var2));
      }

      return var7;
   }

   public void removeAllCountersFromQuery(String var1) {
      List var5 = this.queryCounterMap.remove(var1);
      if (var5 != null) {
         for (Object var7$raw : var5) {
            PerfDataUtil.PerfCounter var7 = (PerfDataUtil.PerfCounter)var7$raw;
            HANDLEByReference var8 = this.counterHandleMap.remove(var7);
            if (var8 != null) {
               PerfDataUtil.removeCounter(var8);
            }
         }

         HANDLEByReference var9 = this.queryHandleMap.remove(var1);
         if (var9 != null) {
            PerfDataUtil.closeQuery(var9);
         }
      }
   }

   public void removeAllCounters() {
      for (HANDLEByReference var5 : this.counterHandleMap.values()) {
         PerfDataUtil.removeCounter(var5);
      }

      this.counterHandleMap.clear();

      for (HANDLEByReference var7 : this.queryHandleMap.values()) {
         PerfDataUtil.closeQuery(var7);
      }

      this.queryHandleMap.clear();
      this.queryCounterMap.clear();
   }

   public long updateQuery(String var1) {
      return !this.queryHandleMap.containsKey(var1) ? 0L : PerfDataUtil.updateQueryTimestamp(this.queryHandleMap.get(var1));
   }

   public long queryCounter(PerfDataUtil.PerfCounter var1) {
      if (!this.counterHandleMap.containsKey(var1)) {
         return 0L;
      }

      long var5 = PerfDataUtil.queryCounter(this.counterHandleMap.get(var1));
      return var5 < 0L ? 0L : var5;
   }

   private HANDLEByReference getOrOpenQuery(String var1) {
      if (this.queryHandleMap.containsKey(var1)) {
         return this.queryHandleMap.get(var1);
      } else {
         HANDLEByReference var5 = new HANDLEByReference();
         if (PerfDataUtil.openQuery(var5)) {
            this.queryHandleMap.put(var1, var5);
            List var6 = Collections.synchronizedList(new ArrayList());
            this.queryCounterMap.put(var1, var6);
            return var5;
         } else {
            return null;
         }
      }
   }
}
