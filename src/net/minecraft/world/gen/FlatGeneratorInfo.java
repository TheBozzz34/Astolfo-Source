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

   public int getBiome() {
      return this.biomeToUse;
   }

   public void setBiome(int biome) {
      this.biomeToUse = biome;
   }

   public Map<String, Map<String, String>> getWorldFeatures() {
      return this.worldFeatures;
   }

   public List<FlatLayerInfo> getFlatLayers() {
      return this.flatLayers;
   }

   public void func_82645_d() {
      int i = 0;

      for (FlatLayerInfo flatLayerInfo : this.flatLayers) {
         flatLayerInfo.setMinY(i);
         i += flatLayerInfo.getLayerCount();
      }
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(3);
      stringBuilder.append(";");

      for (int i = 0; i < this.flatLayers.size(); i++) {
         if (i > 0) {
            stringBuilder.append(",");
         }

         stringBuilder.append(this.flatLayers.get(i).toString());
      }

      stringBuilder.append(";");
      stringBuilder.append(this.biomeToUse);
      if (!this.worldFeatures.isEmpty()) {
         stringBuilder.append(";");
         int j = 0;

         for (Entry entry : this.worldFeatures.entrySet()) {
            if (j++ > 0) {
               stringBuilder.append(",");
            }

            stringBuilder.append(((String)entry.getKey()).toLowerCase());
            Map map = (Map)entry.getValue();
            if (!map.isEmpty()) {
               stringBuilder.append("(");
               int k = 0;

               for (Object object : map.entrySet()) {
                  Entry entry2 = (Entry)object;
                  if (k++ > 0) {
                     stringBuilder.append(" ");
                  }

                  stringBuilder.append((String)entry2.getKey());
                  stringBuilder.append("=");
                  stringBuilder.append((String)entry2.getValue());
               }

               stringBuilder.append(")");
            }
         }
      } else {
         stringBuilder.append(";");
      }

      return stringBuilder.toString();
   }

   private static FlatLayerInfo func_180715_a(int i, String string, int j) {
      String[] strings = i >= 3 ? string.split("\\*", 2) : string.split("x", 2);
      int k = 1;
      int l = 0;
      if (strings.length == 2) {
         try {
            k = Integer.parseInt(strings[0]);
            if (j + k >= 256) {
               k = 256 - j;
            }

            if (k < 0) {
               k = 0;
            }
         } catch (Throwable throwable) {
            return null;
         }
      }

      Block block = null;

      try {
         String string2 = strings[strings.length - 1];
         if (i < 3) {
            strings = string2.split(":", 2);
            if (strings.length > 1) {
               l = Integer.parseInt(strings[1]);
            }

            block = Block.getBlockById(Integer.parseInt(strings[0]));
         } else {
            strings = string2.split(":", 3);
            block = strings.length > 1 ? Block.getBlockFromName(strings[0] + ":" + strings[1]) : null;
            if (block != null) {
               l = strings.length > 2 ? Integer.parseInt(strings[2]) : 0;
            } else {
               block = Block.getBlockFromName(strings[0]);
               if (block != null) {
                  l = strings.length > 1 ? Integer.parseInt(strings[1]) : 0;
               }
            }

            if (block == null) {
               return null;
            }
         }

         if (block == Blocks.air) {
            l = 0;
         }

         if (l < 0 || l > 15) {
            l = 0;
         }
      } catch (Throwable throwable2) {
         return null;
      }

      FlatLayerInfo flatLayerInfo = new FlatLayerInfo(i, k, block, l);
      flatLayerInfo.setMinY(j);
      return flatLayerInfo;
   }

   private static List<FlatLayerInfo> func_180716_a(int i, String string) {
      if (string != null && string.length() >= 1) {
         ArrayList arrayList = Lists.newArrayList();
         String[] strings = string.split(",");
         int j = 0;

         for (String string2 : strings) {
            FlatLayerInfo flatLayerInfo = func_180715_a(i, string2, j);
            if (flatLayerInfo == null) {
               return null;
            }

            arrayList.add(flatLayerInfo);
            j += flatLayerInfo.getLayerCount();
         }

         return arrayList;
      } else {
         return null;
      }
   }

   public static FlatGeneratorInfo createFlatGeneratorFromString(String flatGeneratorSettings) {
      if (flatGeneratorSettings == null) {
         return getDefaultFlatGenerator();
      }

      String[] strings = flatGeneratorSettings.split(";", -1);
      int i = strings.length == 1 ? 0 : MathHelper.parseIntWithDefault(strings[0], 0);
      if (i >= 0 && i <= 3) {
         FlatGeneratorInfo flatGeneratorInfo = new FlatGeneratorInfo();
         int j = strings.length == 1 ? 0 : 1;
         List list = func_180716_a(i, strings[j++]);
         if (list != null && !list.isEmpty()) {
            flatGeneratorInfo.getFlatLayers().addAll(list);
            flatGeneratorInfo.func_82645_d();
            int k = BiomeGenBase.plains.biomeID;
            if (i > 0 && strings.length > j) {
               k = MathHelper.parseIntWithDefault(strings[j++], k);
            }

            flatGeneratorInfo.setBiome(k);
            if (i > 0 && strings.length > j) {
               String[] strings2 = strings[j++].toLowerCase().split(",");

               for (String string : strings2) {
                  String[] strings3 = string.split("\\(", 2);
                  HashMap hashMap = Maps.newHashMap();
                  if (strings3[0].length() > 0) {
                     flatGeneratorInfo.getWorldFeatures().put(strings3[0], hashMap);
                     if (strings3.length > 1 && strings3[1].endsWith(")") && strings3[1].length() > 1) {
                        String[] strings4 = strings3[1].substring(0, strings3[1].length() - 1).split(" ");

                        for (int l = 0; l < strings4.length; l++) {
                           String[] strings5 = strings4[l].split("=", 2);
                           if (strings5.length == 2) {
                              hashMap.put(strings5[0], strings5[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               flatGeneratorInfo.getWorldFeatures().put("village", Maps.newHashMap());
            }

            return flatGeneratorInfo;
         } else {
            return getDefaultFlatGenerator();
         }
      } else {
         return getDefaultFlatGenerator();
      }
   }

   public static FlatGeneratorInfo getDefaultFlatGenerator() {
      FlatGeneratorInfo flatGeneratorInfo = new FlatGeneratorInfo();
      flatGeneratorInfo.setBiome(BiomeGenBase.plains.biomeID);
      flatGeneratorInfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.bedrock));
      flatGeneratorInfo.getFlatLayers().add(new FlatLayerInfo(2, Blocks.dirt));
      flatGeneratorInfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.grass));
      flatGeneratorInfo.func_82645_d();
      flatGeneratorInfo.getWorldFeatures().put("village", Maps.newHashMap());
      return flatGeneratorInfo;
   }
}
