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

   public Pet(Map<String, Object> stats) {
      this.stats = stats;
      if (stats != null) {
         if (stats.get("experience") != null) {
            this.experience = ((Number)stats.get("experience")).intValue();
         }

         if (stats.get("name") != null) {
            this.name = (String)stats.get("name");
         }
      }

      this.updateLevel();
   }

   public String getName() {
      return this.name;
   }

   public double getAverageHappiness() {
      double d = 0.0;

      for (PetAttribute petAttribute : PetAttribute.values()) {
         d += this.getAttribute(petAttribute);
      }

      return d / PetAttribute.values().length;
   }

   public int getAttribute(PetAttribute attribute) {
      Map map = (Map)this.stats.get(attribute.name());
      if (map == null) {
         return 1;
      } else {
         Object object = map.get("timestamp");
         Object object2 = map.get("value");
         if (object instanceof Number && object2 instanceof Number) {
            long l = System.currentTimeMillis();
            long m = ((Number)object).longValue();
            int i = ((Number)object2).intValue();
            long n = l - m;
            long l2 = n / 60000L;
            long l3 = (long)Math.floor(l2 / 5L);
            return Math.max(0, Math.round((float)(i - l3 * attribute.getDecay())));
         } else {
            return 1;
         }
      }
   }

   public boolean updateLevel() {
      int i = this.experience;
      int j = 1;

      for (int k : EXPERIENCE_LEVELS) {
         if (i < k) {
            break;
         }

         i -= k;
         j++;
      }

      this.level = j;
      return false;
   }

   public int K() {
      return this.level;
   }

   public int getExperienceUntilLevel(int level) {
      int i = 0;

      for (int j = 0; j < Math.min(level - 1, 100); j++) {
         i += EXPERIENCE_LEVELS[j];
      }

      return i;
   }

   public int J() {
      return this.experience - this.getExperienceUntilLevel(this.level);
   }

   @Override
   public String toString() {
      return "Pet{stats=" + this.stats + ", level=" + this.level + ", experience=" + this.experience + ", name='" + this.name + '\'' + '}';
   }
}
