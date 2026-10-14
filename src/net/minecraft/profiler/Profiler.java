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

   public void clearProfiling() {
      this.profilingMap.clear();
      this.profilingSection = "";
      this.sectionList.clear();
      this.profilerLocalEnabled = this.profilerGlobalEnabled;
   }

   public void startSection(String name) {
      if (Lagometer.isActive()) {
         int i = name.hashCode();
         if (i == HASH_SCHEDULED_EXECUTABLES && name.equals("scheduledExecutables")) {
            Lagometer.timerScheduledExecutables.start();
         } else if (i == HASH_TICK && name.equals("tick") && Config.isMinecraftThread()) {
            Lagometer.timerScheduledExecutables.end();
            Lagometer.timerTick.start();
         } else if (i == HASH_PRE_RENDER_ERRORS && name.equals("preRenderErrors")) {
            Lagometer.timerTick.end();
         }
      }

      if (Config.isFastRender()) {
         int j = name.hashCode();
         if (j == HASH_RENDER && name.equals("render")) {
            GlStateManager.clearEnabled = false;
         } else if (j == HASH_DISPLAY && name.equals("display")) {
            GlStateManager.clearEnabled = true;
         }
      }

      if (this.profilerLocalEnabled && this.profilingEnabled) {
         if (this.profilingSection.length() > 0) {
            this.profilingSection = this.profilingSection + ".";
         }

         this.profilingSection = this.profilingSection + name;
         this.sectionList.add(this.profilingSection);
         this.timestampList.add(System.nanoTime());
      }
   }

   public void endSection() {
      if (this.profilerLocalEnabled && this.profilingEnabled) {
         long l = System.nanoTime();
         long m = this.timestampList.remove(this.timestampList.size() - 1);
         this.sectionList.remove(this.sectionList.size() - 1);
         long n = l - m;
         if (this.profilingMap.containsKey(this.profilingSection)) {
            this.profilingMap.put(this.profilingSection, this.profilingMap.get(this.profilingSection) + n);
         } else {
            this.profilingMap.put(this.profilingSection, n);
         }

         if (n > 100000000L) {
            logger.warn("Something's taking too long! '" + this.profilingSection + "' took aprox " + n / 1000000.0 + " ms");
         }

         this.profilingSection = !this.sectionList.isEmpty() ? this.sectionList.get(this.sectionList.size() - 1) : "";
      }
   }

   public List<Profiler.Result> getProfilingData(String profilerName) {
      this.profilerLocalEnabled = this.profilerGlobalEnabled;
      if (!this.profilerLocalEnabled) {
         return new ArrayList<>(Arrays.asList(new Profiler.Result("root", 0.0, 0.0)));
      }

      if (!this.profilingEnabled) {
         return null;
      }

      long l = this.profilingMap.containsKey("root") ? this.profilingMap.get("root") : 0L;
      long m = this.profilingMap.containsKey(profilerName) ? this.profilingMap.get(profilerName) : -1L;
      ArrayList arrayList = Lists.newArrayList();
      if (profilerName.length() > 0) {
         profilerName = profilerName + ".";
      }

      long n = 0L;

      for (Object object : this.profilingMap.keySet()) {
         if (((String)object).length() > profilerName.length()
            && ((String)object).startsWith(profilerName)
            && ((String)object).indexOf(".", profilerName.length() + 1) < 0) {
            n += this.profilingMap.get(object);
         }
      }

      float f = (float)n;
      if (n < m) {
         n = m;
      }

      if (l < n) {
         l = n;
      }

      for (Object object2 : this.profilingMap.keySet()) {
         String string = (String)object2;
         if (string.length() > profilerName.length() && string.startsWith(profilerName) && string.indexOf(".", profilerName.length() + 1) < 0) {
            long l2 = this.profilingMap.get(string);
            double d = l2 * 100.0 / n;
            double e = l2 * 100.0 / l;
            String string2 = string.substring(profilerName.length());
            arrayList.add(new Profiler.Result(string2, d, e));
         }
      }

      for (Object object3 : this.profilingMap.keySet()) {
         this.profilingMap.put((String)object3, this.profilingMap.get(object3) * 950L / 1000L);
      }

      if ((float)n > f) {
         arrayList.add(new Profiler.Result("unspecified", ((float)n - f) * 100.0 / n, ((float)n - f) * 100.0 / l));
      }

      Collections.sort(arrayList);
      arrayList.add(0, new Profiler.Result(profilerName, 100.0, n * 100.0 / l));
      return arrayList;
   }

   public void endStartSection(String name) {
      if (this.profilerLocalEnabled) {
         this.endSection();
         this.startSection(name);
      }
   }

   public String getNameOfLastSection() {
      return this.sectionList.size() == 0 ? "[UNKNOWN]" : this.sectionList.get(this.sectionList.size() - 1);
   }

   public static final class Result implements Comparable<Profiler.Result> {
      public double field_76332_a;
      public double field_76330_b;
      public String field_76331_c;

      public Result(String profilerName, double usePercentage, double totalUsePercentage) {
         this.field_76331_c = profilerName;
         this.field_76332_a = usePercentage;
         this.field_76330_b = totalUsePercentage;
      }

      public int compareTo(Profiler.Result result) {
         return result.field_76332_a < this.field_76332_a
            ? -1
            : (result.field_76332_a > this.field_76332_a ? 1 : result.field_76331_c.compareTo(this.field_76331_c));
      }

      public int getColor() {
         return (this.field_76331_c.hashCode() & 11184810) + 4473924;
      }
   }
}
