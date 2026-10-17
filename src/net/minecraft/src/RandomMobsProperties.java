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

   public RandomMobsProperties(String path, ResourceLocation[] variants) {
      ConnectedParser connectedParser = new ConnectedParser("RandomMobs");
      this.name = connectedParser.parseName(path);
      this.basePath = connectedParser.parseBasePath(path);
      this.resourceLocations = variants;
   }

   public RandomMobsProperties(Properties props, String path, ResourceLocation baseResLoc) {
      ConnectedParser connectedParser = new ConnectedParser("RandomMobs");
      this.name = connectedParser.parseName(path);
      this.basePath = connectedParser.parseBasePath(path);
      this.rules = this.parseRules(props, baseResLoc, connectedParser);
   }

   public ResourceLocation getTextureLocation(ResourceLocation loc, EntityLiving el) {
      if (this.rules != null) {
         for (int i = 0; i < this.rules.length; i++) {
            RandomMobsRule randomMobsRule = this.rules[i];
            if (randomMobsRule.matches(el)) {
               return randomMobsRule.getTextureLocation(loc, el.randomMobsId);
            }
         }
      }

      if (this.resourceLocations != null) {
         int j = el.randomMobsId;
         int k = j % this.resourceLocations.length;
         return this.resourceLocations[k];
      } else {
         return loc;
      }
   }

   private RandomMobsRule[] parseRules(Properties properties, ResourceLocation resourceLocation, ConnectedParser connectedParser) {
      ArrayList arrayList = new ArrayList();
      int i = properties.size();

      for (int j = 0; j < i; j++) {
         int k = j + 1;
         String string = properties.getProperty("skins." + k);
         if (string != null) {
            int[] ints = connectedParser.parseIntList(string);
            int[] ints2 = connectedParser.parseIntList(properties.getProperty("weights." + k));
            BiomeGenBase[] biomeGenBases = connectedParser.y(properties.getProperty("biomes." + k));
            RangeListInt rangeListInt = connectedParser.parseRangeListInt(properties.getProperty("heights." + k));
            if (rangeListInt == null) {
               rangeListInt = this.parseMinMaxHeight(properties, k);
            }

            RandomMobsRule randomMobsRule = new RandomMobsRule(resourceLocation, ints, ints2, biomeGenBases, rangeListInt);
            arrayList.add(randomMobsRule);
         }
      }

      return (net.minecraft.src.RandomMobsRule[])(arrayList.toArray(new RandomMobsRule[arrayList.size()]));
   }

   private RangeListInt parseMinMaxHeight(Properties props, int index) {
      String string = props.getProperty("minHeight." + index);
      String string2 = props.getProperty("maxHeight." + index);
      if (string == null && string2 == null) {
         return null;
      }

      int i = 0;
      if (string != null) {
         i = Config.parseInt(string, -1);
         if (i < 0) {
            Config.warn("Invalid minHeight: " + string);
            return null;
         }
      }

      int j = 256;
      if (string2 != null) {
         j = Config.parseInt(string2, -1);
         if (j < 0) {
            Config.warn("Invalid maxHeight: " + string2);
            return null;
         }
      }

      if (j < 0) {
         Config.warn("Invalid minHeight, maxHeight: " + string + ", " + string2);
         return null;
      } else {
         RangeListInt rangeListInt = new RangeListInt();
         rangeListInt.addRange(new RangeInt(i, j));
         return rangeListInt;
      }
   }

   public boolean isValid(String path) {
      if (this.resourceLocations == null && this.rules == null) {
         Config.warn("No skins specified: " + path);
         return false;
      }

      if (this.rules != null) {
         for (int i = 0; i < this.rules.length; i++) {
            RandomMobsRule randomMobsRule = this.rules[i];
            if (!randomMobsRule.isValid(path)) {
               return false;
            }
         }
      }

      if (this.resourceLocations != null) {
         for (int j = 0; j < this.resourceLocations.length; j++) {
            ResourceLocation resourceLocation = this.resourceLocations[j];
            if (!Config.hasResource(resourceLocation)) {
               Config.warn("Texture not found: " + resourceLocation.getResourcePath());
               return false;
            }
         }
      }

      return true;
   }
}
