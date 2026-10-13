package net.minecraft.profiler;

import com.google.common.collect.Maps;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.util.HttpUtil;

public class PlayerUsageSnooper {
   private final Map<String, Object> snooperStats = Maps.newHashMap();
   private final Map<String, Object> clientStats = Maps.newHashMap();
   private final String uniqueID = UUID.randomUUID().toString();
   private final URL serverUrl;
   private final IPlayerUsage playerStatsCollector;
   private final Timer threadTrigger = new Timer("Snooper Timer", true);
   private final Object syncLock = new Object();
   private final long minecraftStartTimeMilis;
   private boolean isRunning;
   private int selfCounter;
   private static final long b = 6362374568035L;

   public PlayerUsageSnooper(String var1, IPlayerUsage var2, long var3) {
      try {
         this.serverUrl = new URL("http://snoop.minecraft.net/" + var1 + "?version=" + 2);
      } catch (MalformedURLException var10) {
         throw new IllegalArgumentException();
      }

      this.playerStatsCollector = var2;
      this.minecraftStartTimeMilis = var3;
   }

   public void startSnooper() {
      if (!this.isRunning) {
         this.isRunning = true;
         this.addOSData();
         this.threadTrigger.schedule(new TimerTask() {
            private static final long a = 102021098559778L;

            @Override
            public void run() {
               if (PlayerUsageSnooper.this.playerStatsCollector.isSnooperEnabled()) {
                  HashMap var4;
                  synchronized (PlayerUsageSnooper.this.syncLock) {
                     var4 = Maps.newHashMap(PlayerUsageSnooper.this.clientStats);
                     if (PlayerUsageSnooper.this.selfCounter == 0) {
                        var4.putAll(PlayerUsageSnooper.this.snooperStats);
                     }

                     var4.put("snooper_count", PlayerUsageSnooper.this.selfCounter++);
                     var4.put("snooper_token", PlayerUsageSnooper.this.uniqueID);
                  }

                  HttpUtil.postMap(PlayerUsageSnooper.this.serverUrl, var4, true);
               }
            }
         }, 0L, 900000L);
      }
   }

   private void addOSData() {
      this.addJvmArgsToSnooper();
      this.addClientStat("snooper_token", this.uniqueID);
      this.addStatToSnooper("snooper_token", this.uniqueID);
      this.addStatToSnooper("os_name", System.getProperty("os.name"));
      this.addStatToSnooper("os_version", System.getProperty("os.version"));
      this.addStatToSnooper("os_architecture", System.getProperty("os.arch"));
      this.addStatToSnooper("java_version", System.getProperty("java.version"));
      this.addClientStat("version", "1.8.8");
      this.playerStatsCollector.addServerTypeToSnooper(this);
   }

   private void addJvmArgsToSnooper() {
      RuntimeMXBean var4 = ManagementFactory.getRuntimeMXBean();
      List var5 = var4.getInputArguments();
      int var6 = 0;

      for (Object var8$raw : var5) {
         String var8 = (String)var8$raw;
         if (var8.startsWith("-X")) {
            this.addClientStat("jvm_arg[" + var6++ + "]", var8);
         }
      }

      this.addClientStat("jvm_args", var6);
   }

   public void addMemoryStatsToSnooper() {
      this.addStatToSnooper("memory_total", Runtime.getRuntime().totalMemory());
      this.addStatToSnooper("memory_max", Runtime.getRuntime().maxMemory());
      this.addStatToSnooper("memory_free", Runtime.getRuntime().freeMemory());
      this.addStatToSnooper("cpu_cores", Runtime.getRuntime().availableProcessors());
      this.playerStatsCollector.addServerStatsToSnooper(this);
   }

   public void addClientStat(String var1, Object var2) {
      synchronized (this.syncLock) {
         this.clientStats.put(var1, var2);
      }
   }

   public void addStatToSnooper(String var1, Object var2) {
      synchronized (this.syncLock) {
         this.snooperStats.put(var1, var2);
      }
   }

   public Map<String, String> getCurrentStats() {
      LinkedHashMap var4 = Maps.newLinkedHashMap();
      synchronized (this.syncLock) {
         this.addMemoryStatsToSnooper();

         for (Entry var7 : this.snooperStats.entrySet()) {
            var4.put(var7.getKey(), var7.getValue().toString());
         }

         for (Entry var11 : this.clientStats.entrySet()) {
            var4.put(var11.getKey(), var11.getValue().toString());
         }

         return var4;
      }
   }

   public boolean isSnooperRunning() {
      return this.isRunning;
   }

   public void stopSnooper() {
      this.threadTrigger.cancel();
   }

   public String getUniqueID() {
      return this.uniqueID;
   }

   public long getMinecraftStartTimeMillis() {
      return this.minecraftStartTimeMilis;
   }
}
