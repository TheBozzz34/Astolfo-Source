package net.hypixel.api.pets;

import java.util.Map;

public class Pet {
   private static final int[] EXPERIENCE_LEVELS = new int[]{
      200,
      210,
      230,
      250,
      280,
      310,
      350,
      390,
      450,
      500,
      570,
      640,
      710,
      800,
      880,
      980,
      1080,
      1190,
      1300,
      1420,
      1540,
      1670,
      1810,
      1950,
      2100,
      2260,
      2420,
      2580,
      2760,
      2940,
      3120,
      3310,
      3510,
      3710,
      3920,
      4140,
      4360,
      4590,
      4820,
      5060,
      5310,
      5560,
      5820,
      6090,
      6360,
      6630,
      6920,
      7210,
      7500,
      7800,
      8110,
      8420,
      8740,
      9070,
      9400,
      9740,
      10080,
      10430,
      10780,
      11150,
      11510,
      11890,
      12270,
      12650,
      13050,
      13440,
      13850,
      14260,
      14680,
      15100,
      15530,
      15960,
      16400,
      16850,
      17300,
      17760,
      18230,
      18700,
      19180,
      19660,
      20150,
      20640,
      21150,
      21650,
      22170,
      22690,
      23210,
      23750,
      24280,
      24830,
      25380,
      25930,
      26500,
      27070,
      27640,
      28220,
      28810,
      29400,
      30000
   };
   private Map<String, Object> stats;
   private int level;
   private int experience;
   private String name;
   private static final long a = 72919244296480L;

   public Pet(Map<String, Object> var1) {
      this.stats = var1;
      if (var1 != null) {
         if (var1.get("experience") != null) {
            this.experience = ((Number)var1.get("experience")).intValue();
         }

         if (var1.get("name") != null) {
            this.name = (String)var1.get("name");
         }
      }

      this.updateLevel();
   }

   public String getName() {
      return this.name;
   }

   public double getAverageHappiness() {
      double var4 = 0.0;

      for (PetAttribute var9 : PetAttribute.values()) {
         var4 += this.getAttribute(var9);
      }

      return var4 / PetAttribute.values().length;
   }

   public int getAttribute(PetAttribute var1) {
      Map var5 = (Map)this.stats.get(var1.name());
      if (var5 == null) {
         return 1;
      } else {
         Object var6 = var5.get("timestamp");
         Object var7 = var5.get("value");
         if (var6 instanceof Number && var7 instanceof Number) {
            long var8 = System.currentTimeMillis();
            long var10 = ((Number)var6).longValue();
            int var12 = ((Number)var7).intValue();
            long var13 = var8 - var10;
            long var15 = var13 / 60000L;
            long var17 = (long)Math.floor(var15 / 5L);
            return Math.max(0, Math.round((float)(var12 - var17 * var1.getDecay())));
         } else {
            return 1;
         }
      }
   }

   public boolean updateLevel() {
      int var4 = this.experience;
      int var5 = 1;

      for (int var9 : EXPERIENCE_LEVELS) {
         if (var4 < var9) {
            break;
         }

         var4 -= var9;
         var5++;
      }

      this.level = var5;
      return false;
   }

   public int K() {
      return this.level;
   }

   public int getExperienceUntilLevel(int var1) {
      int var5 = 0;

      for (int var6 = 0; var6 < Math.min(var1 - 1, 100); var6++) {
         var5 += EXPERIENCE_LEVELS[var6];
      }

      return var5;
   }

   public int J() {
      return this.experience - this.getExperienceUntilLevel(this.level);
   }

   @Override
   public String toString() {
      return "Pet{stats=" + this.stats + ", level=" + this.level + ", experience=" + this.experience + ", name='" + this.name + '\'' + '}';
   }
}
