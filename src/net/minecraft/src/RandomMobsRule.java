package net.minecraft.src;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class RandomMobsRule {
   private ResourceLocation baseResLoc = null;
   private int[] skins = null;
   private ResourceLocation[] resourceLocations = null;
   private int[] weights = null;
   private BiomeGenBase[] biomes = null;
   private RangeListInt heights = null;
   public int[] sumWeights = null;
   public int sumAllWeights = 1;

   public RandomMobsRule(ResourceLocation resourceLocation, int[] ints, int[] ints2, BiomeGenBase[] biomeGenBases, RangeListInt rangeListInt) {
      this.baseResLoc = resourceLocation;
      this.skins = ints;
      this.weights = ints2;
      this.biomes = biomeGenBases;
      this.heights = rangeListInt;
   }

   public boolean isValid(String path) {
      this.resourceLocations = new ResourceLocation[this.skins.length];
      ResourceLocation resourceLocation = RandomMobs.getMcpatcherLocation(this.baseResLoc);
      if (resourceLocation == null) {
         Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
         return false;
      }

      for (int i = 0; i < this.resourceLocations.length; i++) {
         int j = this.skins[i];
         if (j <= 1) {
            this.resourceLocations[i] = this.baseResLoc;
         } else {
            ResourceLocation resourceLocation2 = RandomMobs.getLocationIndexed(resourceLocation, j);
            if (resourceLocation2 == null) {
               Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
               return false;
            }

            if (!Config.hasResource(resourceLocation2)) {
               Config.warn("Texture not found: " + resourceLocation2.getResourcePath());
               return false;
            }

            this.resourceLocations[i] = resourceLocation2;
         }
      }

      if (this.weights != null) {
         if (this.weights.length > this.resourceLocations.length) {
            Config.warn("More weights defined than skins, trimming weights: " + path);
            int[] ints = new int[this.resourceLocations.length];
            System.arraycopy(this.weights, 0, ints, 0, ints.length);
            this.weights = ints;
         }

         if (this.weights.length < this.resourceLocations.length) {
            Config.warn("Less weights defined than skins, expanding weights: " + path);
            int[] ints2 = new int[this.resourceLocations.length];
            System.arraycopy(this.weights, 0, ints2, 0, this.weights.length);
            int k = MathUtils.getAverage(this.weights);

            for (int l = this.weights.length; l < ints2.length; l++) {
               ints2[l] = k;
            }

            this.weights = ints2;
         }

         this.sumWeights = new int[this.weights.length];
         int m = 0;

         for (int n = 0; n < this.weights.length; n++) {
            if (this.weights[n] < 0) {
               Config.warn("Invalid weight: " + this.weights[n]);
               return false;
            }

            m += this.weights[n];
            this.sumWeights[n] = m;
         }

         this.sumAllWeights = m;
         if (this.sumAllWeights <= 0) {
            Config.warn("Invalid sum of all weights: " + m);
            this.sumAllWeights = 1;
         }
      }

      return true;
   }

   public boolean matches(EntityLiving el) {
      return !Matches.biome(el.spawnBiome, this.biomes)
         ? false
         : (this.heights != null && el.spawnPosition != null ? this.heights.isInRange(el.spawnPosition.getY()) : true);
   }

   public ResourceLocation getTextureLocation(ResourceLocation loc, int randomId) {
      int i = 0;
      if (this.weights == null) {
         i = randomId % this.resourceLocations.length;
      } else {
         int j = randomId % this.sumAllWeights;

         for (int k = 0; k < this.sumWeights.length; k++) {
            if (this.sumWeights[k] > j) {
               i = k;
               break;
            }
         }
      }

      return this.resourceLocations[i];
   }
}
