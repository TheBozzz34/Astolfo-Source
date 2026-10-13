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
   public int n = 1;
   private static final long a = 82811361601521L;

   public RandomMobsRule(ResourceLocation var1, int[] var2, int[] var3, BiomeGenBase[] var4, RangeListInt var5) {
      this.baseResLoc = var1;
      this.skins = var2;
      this.weights = var3;
      this.biomes = var4;
      this.heights = var5;
   }

   public boolean isValid(String var1) {
      this.resourceLocations = new ResourceLocation[this.skins.length];
      ResourceLocation var5 = RandomMobs.getMcpatcherLocation(this.baseResLoc);
      if (var5 == null) {
         Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
         return false;
      }

      for (int var6 = 0; var6 < this.resourceLocations.length; var6++) {
         int var7 = this.skins[var6];
         if (var7 <= 1) {
            this.resourceLocations[var6] = this.baseResLoc;
         } else {
            ResourceLocation var8 = RandomMobs.getLocationIndexed(var5, var7);
            if (var8 == null) {
               Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
               return false;
            }

            if (!Config.hasResource(var8)) {
               Config.warn("Texture not found: " + var8.getResourcePath());
               return false;
            }

            this.resourceLocations[var6] = var8;
         }
      }

      if (this.weights != null) {
         if (this.weights.length > this.resourceLocations.length) {
            Config.warn("More weights defined than skins, trimming weights: " + var1);
            int[] var9 = new int[this.resourceLocations.length];
            System.arraycopy(this.weights, 0, var9, 0, var9.length);
            this.weights = var9;
         }

         if (this.weights.length < this.resourceLocations.length) {
            Config.warn("Less weights defined than skins, expanding weights: " + var1);
            int[] var10 = new int[this.resourceLocations.length];
            System.arraycopy(this.weights, 0, var10, 0, this.weights.length);
            int var12 = MathUtils.o(this.weights);

            for (int var14 = this.weights.length; var14 < var10.length; var14++) {
               var10[var14] = var12;
            }

            this.weights = var10;
         }

         this.sumWeights = new int[this.weights.length];
         int var11 = 0;

         for (int var13 = 0; var13 < this.weights.length; var13++) {
            if (this.weights[var13] < 0) {
               Config.warn("Invalid weight: " + this.weights[var13]);
               return false;
            }

            var11 += this.weights[var13];
            this.sumWeights[var13] = var11;
         }

         this.n = var11;
         if (this.n <= 0) {
            Config.warn("Invalid sum of all weights: " + var11);
            this.n = 1;
         }
      }

      return true;
   }

   public boolean matches(EntityLiving var1) {
      return !Matches.biome(var1.spawnBiome, this.biomes)
         ? false
         : (this.heights != null && var1.spawnPosition != null ? this.heights.isInRange(var1.spawnPosition.getY()) : true);
   }

   public ResourceLocation getTextureLocation(ResourceLocation var1, int var2) {
      int var6 = 0;
      if (this.weights == null) {
         var6 = var2 % this.resourceLocations.length;
      } else {
         int var7 = var2 % this.n;

         for (int var8 = 0; var8 < this.sumWeights.length; var8++) {
            if (this.sumWeights[var8] > var7) {
               var6 = var8;
               break;
            }
         }
      }

      return this.resourceLocations[var6];
   }
}
