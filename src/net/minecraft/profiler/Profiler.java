package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.src.Config;
import net.minecraft.src.Lagometer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler {
   private static final Logger logger = LogManager.getLogger();
   private final List<String> sectionList = Lists.newArrayList();
   private final List<Long> timestampList = Lists.newArrayList();
   public boolean profilingEnabled;
   private String profilingSection = "";
   private final Map<String, Long> profilingMap = Maps.newHashMap();
   public boolean profilerGlobalEnabled = true;
   private boolean profilerLocalEnabled = this.profilerGlobalEnabled;
   private static final int HASH_SCHEDULED_EXECUTABLES = "scheduledExecutables".hashCode();
   private static final int HASH_TICK = "tick".hashCode();
   private static final int HASH_PRE_RENDER_ERRORS = "preRenderErrors".hashCode();
   private static final int HASH_RENDER = "render".hashCode();
   private static final int HASH_DISPLAY = "display".hashCode();
   private static final long a = 45091631055133L;

   public void clearProfiling() {
      this.profilingMap.clear();
      this.profilingSection = "";
      this.sectionList.clear();
      this.profilerLocalEnabled = this.profilerGlobalEnabled;
   }

   public void startSection(String var1) {
      if (Lagometer.isActive()) {
         int var5 = var1.hashCode();
         if (var5 == HASH_SCHEDULED_EXECUTABLES && var1.equals("scheduledExecutables")) {
            Lagometer.timerScheduledExecutables.start();
         } else if (var5 == HASH_TICK && var1.equals("tick") && Config.isMinecraftThread()) {
            Lagometer.timerScheduledExecutables.end();
            Lagometer.timerTick.start();
         } else if (var5 == HASH_PRE_RENDER_ERRORS && var1.equals("preRenderErrors")) {
            Lagometer.timerTick.end();
         }
      }

      if (Config.isFastRender()) {
         int var6 = var1.hashCode();
         if (var6 == HASH_RENDER && var1.equals("render")) {
            GlStateManager.clearEnabled = false;
         } else if (var6 == HASH_DISPLAY && var1.equals("display")) {
            GlStateManager.clearEnabled = true;
         }
      }

      if (this.profilerLocalEnabled && this.profilingEnabled) {
         if (this.profilingSection.length() > 0) {
            this.profilingSection = this.profilingSection + ".";
         }

         this.profilingSection = this.profilingSection + var1;
         this.sectionList.add(this.profilingSection);
         this.timestampList.add(System.nanoTime());
      }
   }

   public void endSection() {
      if (this.profilerLocalEnabled && this.profilingEnabled) {
         long var4 = System.nanoTime();
         long var6 = this.timestampList.remove(this.timestampList.size() - 1);
         this.sectionList.remove(this.sectionList.size() - 1);
         long var8 = var4 - var6;
         if (this.profilingMap.containsKey(this.profilingSection)) {
            this.profilingMap.put(this.profilingSection, this.profilingMap.get(this.profilingSection) + var8);
         } else {
            this.profilingMap.put(this.profilingSection, var8);
         }

         if (var8 > 100000000L) {
            logger.warn("Something's taking too long! '" + this.profilingSection + "' took aprox " + var8 / 1000000.0 + " ms");
         }

         this.profilingSection = !this.sectionList.isEmpty() ? this.sectionList.get(this.sectionList.size() - 1) : "";
      }
   }

   public List<Profiler.Result> getProfilingData(String var1) {
      this.profilerLocalEnabled = this.profilerGlobalEnabled;
      if (!this.profilerLocalEnabled) {
         return new ArrayList<>(Arrays.asList(new Profiler.Result("root", 0.0, 0.0)));
      }

      if (!this.profilingEnabled) {
         return null;
      }

      long var5 = this.profilingMap.containsKey("root") ? this.profilingMap.get("root") : 0L;
      long var7 = this.profilingMap.containsKey(var1) ? this.profilingMap.get(var1) : -1L;
      ArrayList var9 = Lists.newArrayList();
      if (var1.length() > 0) {
         var1 = var1 + ".";
      }

      long var10 = 0L;

      for (Object var13 : this.profilingMap.keySet()) {
         if (((String)var13).length() > var1.length() && ((String)var13).startsWith(var1) && ((String)var13).indexOf(".", var1.length() + 1) < 0) {
            var10 += this.profilingMap.get(var13);
         }
      }

      float var23 = (float)var10;
      if (var10 < var7) {
         var10 = var7;
      }

      if (var5 < var10) {
         var5 = var10;
      }

      for (Object var14 : this.profilingMap.keySet()) {
         String var15 = (String)var14;
         if (var15.length() > var1.length() && var15.startsWith(var1) && var15.indexOf(".", var1.length() + 1) < 0) {
            long var16 = this.profilingMap.get(var15);
            double var18 = var16 * 100.0 / var10;
            double var20 = var16 * 100.0 / var5;
            String var22 = var15.substring(var1.length());
            var9.add(new Profiler.Result(var22, var18, var20));
         }
      }

      for (Object var26 : this.profilingMap.keySet()) {
         this.profilingMap.put((String)var26, this.profilingMap.get(var26) * 950L / 1000L);
      }

      if ((float)var10 > var23) {
         var9.add(new Profiler.Result("unspecified", ((float)var10 - var23) * 100.0 / var10, ((float)var10 - var23) * 100.0 / var5));
      }

      Collections.sort(var9);
      var9.add(0, new Profiler.Result(var1, 100.0, var10 * 100.0 / var5));
      return var9;
   }

   public void endStartSection(String var1) {
      if (this.profilerLocalEnabled) {
         this.endSection();
         this.startSection(var1);
      }
   }

   public String getNameOfLastSection() {
      return this.sectionList.size() == 0 ? "[UNKNOWN]" : this.sectionList.get(this.sectionList.size() - 1);
   }

   public static final class Result implements Comparable<Profiler.Result> {
      public double field_76332_a;
      public double field_76330_b;
      public String field_76331_c;
      private static final long a = 43955187717787L;

      public Result(String var1, double var2, double var4) {
         this.field_76331_c = var1;
         this.field_76332_a = var2;
         this.field_76330_b = var4;
      }

      public int compareTo(Profiler.Result var1) {
         return var1.field_76332_a < this.field_76332_a ? -1 : (var1.field_76332_a > this.field_76332_a ? 1 : var1.field_76331_c.compareTo(this.field_76331_c));
      }

      public int getColor() {
         return (this.field_76331_c.hashCode() & 11184810) + 4473924;
      }
   }
}
