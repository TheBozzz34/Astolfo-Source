package net.minecraft.src;

import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class RandomMobsProperties {
   public String name = null;
   public String basePath = null;
   public ResourceLocation[] resourceLocations = null;
   public RandomMobsRule[] rules = null;
   private static final long a = 26889798730540L;
   private static final long e = -7664019069812604672L;

   public RandomMobsProperties(String var1, ResourceLocation[] var2) {
      ConnectedParser var5 = new ConnectedParser("RandomMobs");
      this.name = var5.parseName(var1);
      this.basePath = var5.parseBasePath(var1);
      this.resourceLocations = var2;
   }

   public RandomMobsProperties(Properties var1, String var2, ResourceLocation var3) {
      ConnectedParser var6 = new ConnectedParser("RandomMobs");
      this.name = var6.parseName(var2);
      this.basePath = var6.parseBasePath(var2);
      this.rules = this.b(var1, var3, var6);
   }

   public ResourceLocation getTextureLocation(ResourceLocation var1, EntityLiving var2) {
      if (this.rules != null) {
         for (int var6 = 0; var6 < this.rules.length; var6++) {
            RandomMobsRule var7 = this.rules[var6];
            if (var7.matches(var2)) {
               return var7.getTextureLocation(var1, var2.randomMobsId);
            }
         }
      }

      if (this.resourceLocations != null) {
         int var8 = var2.randomMobsId;
         int var9 = var8 % this.resourceLocations.length;
         return this.resourceLocations[var9];
      } else {
         return var1;
      }
   }

   private RandomMobsRule[] b(Properties var1, ResourceLocation var2, ConnectedParser var3) {
      ArrayList var7 = new ArrayList();
      int var8 = var1.size();

      for (int var9 = 0; var9 < var8; var9++) {
         int var10 = var9 + 1;
         String var11 = var1.getProperty("skins." + var10);
         if (var11 != null) {
            int[] var12 = var3.parseIntList(var11);
            int[] var13 = var3.parseIntList(var1.getProperty("weights." + var10));
            BiomeGenBase[] var14 = var3.y(var1.getProperty("biomes." + var10));
            RangeListInt var15 = var3.F(var1.getProperty("heights." + var10));
            if (var15 == null) {
               var15 = this.parseMinMaxHeight(var1, var10);
            }

            RandomMobsRule var16 = new RandomMobsRule(var2, var12, var13, var14, var15);
            var7.add(var16);
         }
      }

      return (net.minecraft.src.RandomMobsRule[])(var7.toArray(new RandomMobsRule[var7.size()]));
   }

   private RangeListInt parseMinMaxHeight(Properties var1, int var2) {
      String var6 = var1.getProperty("minHeight." + var2);
      String var7 = var1.getProperty("maxHeight." + var2);
      if (var6 == null && var7 == null) {
         return null;
      }

      int var8 = 0;
      if (var6 != null) {
         var8 = Config.parseInt(var6, -1);
         if (var8 < 0) {
            Config.warn("Invalid minHeight: " + var6);
            return null;
         }
      }

      int var9 = (int)e;
      if (var7 != null) {
         var9 = Config.parseInt(var7, -1);
         if (var9 < 0) {
            Config.warn("Invalid maxHeight: " + var7);
            return null;
         }
      }

      if (var9 < 0) {
         Config.warn("Invalid minHeight, maxHeight: " + var6 + ", " + var7);
         return null;
      } else {
         RangeListInt var10 = new RangeListInt();
         var10.addRange(new RangeInt(var8, var9));
         return var10;
      }
   }

   public boolean isValid(String var1) {
      if (this.resourceLocations == null && this.rules == null) {
         Config.warn("No skins specified: " + var1);
         return false;
      }

      if (this.rules != null) {
         for (int var5 = 0; var5 < this.rules.length; var5++) {
            RandomMobsRule var6 = this.rules[var5];
            if (!var6.isValid(var1)) {
               return false;
            }
         }
      }

      if (this.resourceLocations != null) {
         for (int var7 = 0; var7 < this.resourceLocations.length; var7++) {
            ResourceLocation var8 = this.resourceLocations[var7];
            if (!Config.hasResource(var8)) {
               Config.warn("Texture not found: " + var8.getResourcePath());
               return false;
            }
         }
      }

      return true;
   }
}
