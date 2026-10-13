package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

public class FlatGeneratorInfo {
   private final List<FlatLayerInfo> flatLayers = Lists.newArrayList();
   private final Map<String, Map<String, String>> worldFeatures = Maps.newHashMap();
   private int biomeToUse;
   private static final long a = 28651824603549L;

   public int getBiome() {
      return this.biomeToUse;
   }

   public void setBiome(int var1) {
      this.biomeToUse = var1;
   }

   public Map<String, Map<String, String>> getWorldFeatures() {
      return this.worldFeatures;
   }

   public List<FlatLayerInfo> getFlatLayers() {
      return this.flatLayers;
   }

   public void func_82645_d() {
      int var3 = 0;

      for (FlatLayerInfo var5 : this.flatLayers) {
         var5.setMinY(var3);
         var3 += var5.getLayerCount();
      }
   }

   @Override
   public String toString() {
      StringBuilder var3 = new StringBuilder();
      var3.append(3);
      var3.append(";");

      for (int var4 = 0; var4 < this.flatLayers.size(); var4++) {
         if (var4 > 0) {
            var3.append(",");
         }

         var3.append(this.flatLayers.get(var4).toString());
      }

      var3.append(";");
      var3.append(this.biomeToUse);
      if (!this.worldFeatures.isEmpty()) {
         var3.append(";");
         int var11 = 0;

         for (Entry var6 : this.worldFeatures.entrySet()) {
            if (var11++ > 0) {
               var3.append(",");
            }

            var3.append(((String)var6.getKey()).toLowerCase());
            Map var7 = (Map)var6.getValue();
            if (!var7.isEmpty()) {
               var3.append("(");
               int var8 = 0;

               for (Object var10$raw : var7.entrySet()) {
                  Entry var10 = (Entry)var10$raw;
                  if (var8++ > 0) {
                     var3.append(" ");
                  }

                  var3.append((String)var10.getKey());
                  var3.append("=");
                  var3.append((String)var10.getValue());
               }

               var3.append(")");
            }
         }
      } else {
         var3.append(";");
      }

      return var3.toString();
   }

   private static FlatLayerInfo func_180715_a(int var0, String var1, int var2) {
      String[] var6 = var0 >= 3 ? var1.split("\\*", 2) : var1.split("x", 2);
      int var7 = 1;
      int var8 = 0;
      if (var6.length == 2) {
         try {
            var7 = Integer.parseInt(var6[0]);
            if (var2 + var7 >= 256) {
               var7 = 256 - var2;
            }

            if (var7 < 0) {
               var7 = 0;
            }
         } catch (Throwable var11) {
            return null;
         }
      }

      Block var9 = null;

      try {
         String var10 = var6[var6.length - 1];
         if (var0 < 3) {
            var6 = var10.split(":", 2);
            if (var6.length > 1) {
               var8 = Integer.parseInt(var6[1]);
            }

            var9 = Block.getBlockById(Integer.parseInt(var6[0]));
         } else {
            var6 = var10.split(":", 3);
            var9 = var6.length > 1 ? Block.getBlockFromName(var6[0] + ":" + var6[1]) : null;
            if (var9 != null) {
               var8 = var6.length > 2 ? Integer.parseInt(var6[2]) : 0;
            } else {
               var9 = Block.getBlockFromName(var6[0]);
               if (var9 != null) {
                  var8 = var6.length > 1 ? Integer.parseInt(var6[1]) : 0;
               }
            }

            if (var9 == null) {
               return null;
            }
         }

         if (var9 == Blocks.air) {
            var8 = 0;
         }

         if (var8 < 0 || var8 > 15) {
            var8 = 0;
         }
      } catch (Throwable var12) {
         return null;
      }

      FlatLayerInfo var16 = new FlatLayerInfo(var0, var7, var9, var8);
      var16.setMinY(var2);
      return var16;
   }

   private static List<FlatLayerInfo> func_180716_a(int var0, String var1) {
      if (var1 != null && var1.length() >= 1) {
         ArrayList var5 = Lists.newArrayList();
         String[] var6 = var1.split(",");
         int var7 = 0;

         for (String var11 : var6) {
            FlatLayerInfo var12 = func_180715_a(var0, var11, var7);
            if (var12 == null) {
               return null;
            }

            var5.add(var12);
            var7 += var12.getLayerCount();
         }

         return var5;
      } else {
         return null;
      }
   }

   public static FlatGeneratorInfo createFlatGeneratorFromString(String var0) {
      if (var0 == null) {
         return getDefaultFlatGenerator();
      }

      String[] var4 = var0.split(";", -1);
      int var5 = var4.length == 1 ? 0 : MathHelper.parseIntWithDefault(var4[0], 0);
      if (var5 >= 0 && var5 <= 3) {
         FlatGeneratorInfo var6 = new FlatGeneratorInfo();
         int var7 = var4.length == 1 ? 0 : 1;
         List var8 = func_180716_a(var5, var4[var7++]);
         if (var8 != null && !var8.isEmpty()) {
            var6.getFlatLayers().addAll(var8);
            var6.func_82645_d();
            int var9 = BiomeGenBase.plains.biomeID;
            if (var5 > 0 && var4.length > var7) {
               var9 = MathHelper.parseIntWithDefault(var4[var7++], var9);
            }

            var6.setBiome(var9);
            if (var5 > 0 && var4.length > var7) {
               String[] var10 = var4[var7++].toLowerCase().split(",");

               for (String var14 : var10) {
                  String[] var15 = var14.split("\\(", 2);
                  HashMap var16 = Maps.newHashMap();
                  if (var15[0].length() > 0) {
                     var6.getWorldFeatures().put(var15[0], var16);
                     if (var15.length > 1 && var15[1].endsWith(")") && var15[1].length() > 1) {
                        String[] var17 = var15[1].substring(0, var15[1].length() - 1).split(" ");

                        for (int var18 = 0; var18 < var17.length; var18++) {
                           String[] var19 = var17[var18].split("=", 2);
                           if (var19.length == 2) {
                              var16.put(var19[0], var19[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               var6.getWorldFeatures().put("village", Maps.newHashMap());
            }

            return var6;
         } else {
            return getDefaultFlatGenerator();
         }
      } else {
         return getDefaultFlatGenerator();
      }
   }

   public static FlatGeneratorInfo getDefaultFlatGenerator() {
      FlatGeneratorInfo var2 = new FlatGeneratorInfo();
      var2.setBiome(BiomeGenBase.plains.biomeID);
      var2.getFlatLayers().add(new FlatLayerInfo(1, Blocks.bedrock));
      var2.getFlatLayers().add(new FlatLayerInfo(2, Blocks.dirt));
      var2.getFlatLayers().add(new FlatLayerInfo(1, Blocks.grass));
      var2.func_82645_d();
      var2.getWorldFeatures().put("village", Maps.newHashMap());
      return var2;
   }
}
