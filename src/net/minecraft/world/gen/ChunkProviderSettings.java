package net.minecraft.world.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.biome.BiomeGenBase;

public class ChunkProviderSettings {
   public final float coordinateScale;
   public final float heightScale;
   public final float upperLimitScale;
   public final float lowerLimitScale;
   public final float depthNoiseScaleX;
   public final float depthNoiseScaleZ;
   public final float depthNoiseScaleExponent;
   public final float mainNoiseScaleX;
   public final float mainNoiseScaleY;
   public final float mainNoiseScaleZ;
   public final float baseSize;
   public final float stretchY;
   public final float biomeDepthWeight;
   public final float biomeDepthOffSet;
   public final float biomeScaleWeight;
   public final float biomeScaleOffset;
   public final int seaLevel;
   public final boolean useCaves;
   public final boolean useDungeons;
   public final int dungeonChance;
   public final boolean useStrongholds;
   public final boolean useVillages;
   public final boolean useMineShafts;
   public final boolean useTemples;
   public final boolean useMonuments;
   public final boolean useRavines;
   public final boolean useWaterLakes;
   public final int waterLakeChance;
   public final boolean useLavaLakes;
   public final int lavaLakeChance;
   public final boolean useLavaOceans;
   public final int fixedBiome;
   public final int biomeSize;
   public final int riverSize;
   public final int dirtSize;
   public final int dirtCount;
   public final int dirtMinHeight;
   public final int dirtMaxHeight;
   public final int gravelSize;
   public final int gravelCount;
   public final int gravelMinHeight;
   public final int gravelMaxHeight;
   public final int graniteSize;
   public final int graniteCount;
   public final int graniteMinHeight;
   public final int graniteMaxHeight;
   public final int dioriteSize;
   public final int dioriteCount;
   public final int dioriteMinHeight;
   public final int dioriteMaxHeight;
   public final int andesiteSize;
   public final int andesiteCount;
   public final int andesiteMinHeight;
   public final int andesiteMaxHeight;
   public final int coalSize;
   public final int coalCount;
   public final int coalMinHeight;
   public final int coalMaxHeight;
   public final int ironSize;
   public final int ironCount;
   public final int ironMinHeight;
   public final int ironMaxHeight;
   public final int goldSize;
   public final int goldCount;
   public final int goldMinHeight;
   public final int goldMaxHeight;
   public final int redstoneSize;
   public final int redstoneCount;
   public final int redstoneMinHeight;
   public final int redstoneMaxHeight;
   public final int diamondSize;
   public final int diamondCount;
   public final int diamondMinHeight;
   public final int diamondMaxHeight;
   public final int lapisSize;
   public final int lapisCount;
   public final int lapisCenterHeight;
   public final int lapisSpread;

   public ChunkProviderSettings(ChunkProviderSettings.Factory var1) {
      this.coordinateScale = var1.coordinateScale;
      this.heightScale = var1.heightScale;
      this.upperLimitScale = var1.upperLimitScale;
      this.lowerLimitScale = var1.lowerLimitScale;
      this.depthNoiseScaleX = var1.depthNoiseScaleX;
      this.depthNoiseScaleZ = var1.depthNoiseScaleZ;
      this.depthNoiseScaleExponent = var1.depthNoiseScaleExponent;
      this.mainNoiseScaleX = var1.mainNoiseScaleX;
      this.mainNoiseScaleY = var1.mainNoiseScaleY;
      this.mainNoiseScaleZ = var1.mainNoiseScaleZ;
      this.baseSize = var1.baseSize;
      this.stretchY = var1.stretchY;
      this.biomeDepthWeight = var1.biomeDepthWeight;
      this.biomeDepthOffSet = var1.biomeDepthOffset;
      this.biomeScaleWeight = var1.biomeScaleWeight;
      this.biomeScaleOffset = var1.biomeScaleOffset;
      this.seaLevel = var1.seaLevel;
      this.useCaves = var1.useCaves;
      this.useDungeons = var1.useDungeons;
      this.dungeonChance = var1.dungeonChance;
      this.useStrongholds = var1.useStrongholds;
      this.useVillages = var1.useVillages;
      this.useMineShafts = var1.useMineShafts;
      this.useTemples = var1.useTemples;
      this.useMonuments = var1.useMonuments;
      this.useRavines = var1.useRavines;
      this.useWaterLakes = var1.useWaterLakes;
      this.waterLakeChance = var1.waterLakeChance;
      this.useLavaLakes = var1.useLavaLakes;
      this.lavaLakeChance = var1.lavaLakeChance;
      this.useLavaOceans = var1.useLavaOceans;
      this.fixedBiome = var1.fixedBiome;
      this.biomeSize = var1.biomeSize;
      this.riverSize = var1.riverSize;
      this.dirtSize = var1.dirtSize;
      this.dirtCount = var1.dirtCount;
      this.dirtMinHeight = var1.dirtMinHeight;
      this.dirtMaxHeight = var1.dirtMaxHeight;
      this.gravelSize = var1.gravelSize;
      this.gravelCount = var1.gravelCount;
      this.gravelMinHeight = var1.gravelMinHeight;
      this.gravelMaxHeight = var1.gravelMaxHeight;
      this.graniteSize = var1.graniteSize;
      this.graniteCount = var1.graniteCount;
      this.graniteMinHeight = var1.graniteMinHeight;
      this.graniteMaxHeight = var1.graniteMaxHeight;
      this.dioriteSize = var1.dioriteSize;
      this.dioriteCount = var1.dioriteCount;
      this.dioriteMinHeight = var1.dioriteMinHeight;
      this.dioriteMaxHeight = var1.dioriteMaxHeight;
      this.andesiteSize = var1.andesiteSize;
      this.andesiteCount = var1.andesiteCount;
      this.andesiteMinHeight = var1.andesiteMinHeight;
      this.andesiteMaxHeight = var1.andesiteMaxHeight;
      this.coalSize = var1.coalSize;
      this.coalCount = var1.coalCount;
      this.coalMinHeight = var1.coalMinHeight;
      this.coalMaxHeight = var1.coalMaxHeight;
      this.ironSize = var1.ironSize;
      this.ironCount = var1.ironCount;
      this.ironMinHeight = var1.ironMinHeight;
      this.ironMaxHeight = var1.ironMaxHeight;
      this.goldSize = var1.goldSize;
      this.goldCount = var1.goldCount;
      this.goldMinHeight = var1.goldMinHeight;
      this.goldMaxHeight = var1.goldMaxHeight;
      this.redstoneSize = var1.redstoneSize;
      this.redstoneCount = var1.redstoneCount;
      this.redstoneMinHeight = var1.redstoneMinHeight;
      this.redstoneMaxHeight = var1.redstoneMaxHeight;
      this.diamondSize = var1.diamondSize;
      this.diamondCount = var1.diamondCount;
      this.diamondMinHeight = var1.diamondMinHeight;
      this.diamondMaxHeight = var1.diamondMaxHeight;
      this.lapisSize = var1.lapisSize;
      this.lapisCount = var1.lapisCount;
      this.lapisCenterHeight = var1.lapisCenterHeight;
      this.lapisSpread = var1.lapisSpread;
   }

   public static class Factory {
      public static final Gson JSON_ADAPTER = new GsonBuilder()
         .registerTypeAdapter(ChunkProviderSettings.Factory.class, new ChunkProviderSettings.Serializer())
         .create();
      public float coordinateScale = 684.412F;
      public float heightScale = 684.412F;
      public float upperLimitScale = 512.0F;
      public float lowerLimitScale = 512.0F;
      public float depthNoiseScaleX = 200.0F;
      public float depthNoiseScaleZ = 200.0F;
      public float depthNoiseScaleExponent = 0.5F;
      public float mainNoiseScaleX = 80.0F;
      public float mainNoiseScaleY = 160.0F;
      public float mainNoiseScaleZ = 80.0F;
      public float baseSize = 8.5F;
      public float stretchY = 12.0F;
      public float biomeDepthWeight = 1.0F;
      public float biomeDepthOffset = 0.0F;
      public float biomeScaleWeight = 1.0F;
      public float biomeScaleOffset = 0.0F;
      public int seaLevel = 63;
      public boolean useCaves = true;
      public boolean useDungeons = true;
      public int dungeonChance = 8;
      public boolean useStrongholds = true;
      public boolean useVillages = true;
      public boolean useMineShafts = true;
      public boolean useTemples = true;
      public boolean useMonuments = true;
      public boolean useRavines = true;
      public boolean useWaterLakes = true;
      public int waterLakeChance = 4;
      public boolean useLavaLakes = true;
      public int lavaLakeChance = 80;
      public boolean useLavaOceans = false;
      public int fixedBiome = -1;
      public int biomeSize = 4;
      public int riverSize = 4;
      public int dirtSize = 33;
      public int dirtCount = 10;
      public int dirtMinHeight = 0;
      public int dirtMaxHeight = 256;
      public int gravelSize = 33;
      public int gravelCount = 8;
      public int gravelMinHeight = 0;
      public int gravelMaxHeight = 256;
      public int graniteSize = 33;
      public int graniteCount = 10;
      public int graniteMinHeight = 0;
      public int graniteMaxHeight = 80;
      public int dioriteSize = 33;
      public int dioriteCount = 10;
      public int dioriteMinHeight = 0;
      public int dioriteMaxHeight = 80;
      public int andesiteSize = 33;
      public int andesiteCount = 10;
      public int andesiteMinHeight = 0;
      public int andesiteMaxHeight = 80;
      public int coalSize = 17;
      public int coalCount = 20;
      public int coalMinHeight = 0;
      public int coalMaxHeight = 128;
      public int ironSize = 9;
      public int ironCount = 20;
      public int ironMinHeight = 0;
      public int ironMaxHeight = 64;
      public int goldSize = 9;
      public int goldCount = 2;
      public int goldMinHeight = 0;
      public int goldMaxHeight = 32;
      public int redstoneSize = 8;
      public int redstoneCount = 8;
      public int redstoneMinHeight = 0;
      public int redstoneMaxHeight = 16;
      public int diamondSize = 8;
      public int diamondCount = 1;
      public int diamondMinHeight = 0;
      public int diamondMaxHeight = 16;
      public int lapisSize = 7;
      public int lapisCount = 1;
      public int lapisCenterHeight = 16;
      public int lapisSpread = 16;
      private static final long ab = 76074268922177L;

      public static ChunkProviderSettings.Factory jsonToFactory(String var0) {
         if (var0.length() == 0) {
            return new ChunkProviderSettings.Factory();
         }

         try {
            return JSON_ADAPTER.fromJson(var0, ChunkProviderSettings.Factory.class);
         } catch (Exception var4) {
            return new ChunkProviderSettings.Factory();
         }
      }

      @Override
      public String toString() {
         return JSON_ADAPTER.toJson(this);
      }

      public Factory() {
         this.func_177863_a();
      }

      public void func_177863_a() {
         this.coordinateScale = 684.412F;
         this.heightScale = 684.412F;
         this.upperLimitScale = 512.0F;
         this.lowerLimitScale = 512.0F;
         this.depthNoiseScaleX = 200.0F;
         this.depthNoiseScaleZ = 200.0F;
         this.depthNoiseScaleExponent = 0.5F;
         this.mainNoiseScaleX = 80.0F;
         this.mainNoiseScaleY = 160.0F;
         this.mainNoiseScaleZ = 80.0F;
         this.baseSize = 8.5F;
         this.stretchY = 12.0F;
         this.biomeDepthWeight = 1.0F;
         this.biomeDepthOffset = 0.0F;
         this.biomeScaleWeight = 1.0F;
         this.biomeScaleOffset = 0.0F;
         this.seaLevel = 63;
         this.useCaves = true;
         this.useDungeons = true;
         this.dungeonChance = 8;
         this.useStrongholds = true;
         this.useVillages = true;
         this.useMineShafts = true;
         this.useTemples = true;
         this.useMonuments = true;
         this.useRavines = true;
         this.useWaterLakes = true;
         this.waterLakeChance = 4;
         this.useLavaLakes = true;
         this.lavaLakeChance = 80;
         this.useLavaOceans = false;
         this.fixedBiome = -1;
         this.biomeSize = 4;
         this.riverSize = 4;
         this.dirtSize = 33;
         this.dirtCount = 10;
         this.dirtMinHeight = 0;
         this.dirtMaxHeight = 256;
         this.gravelSize = 33;
         this.gravelCount = 8;
         this.gravelMinHeight = 0;
         this.gravelMaxHeight = 256;
         this.graniteSize = 33;
         this.graniteCount = 10;
         this.graniteMinHeight = 0;
         this.graniteMaxHeight = 80;
         this.dioriteSize = 33;
         this.dioriteCount = 10;
         this.dioriteMinHeight = 0;
         this.dioriteMaxHeight = 80;
         this.andesiteSize = 33;
         this.andesiteCount = 10;
         this.andesiteMinHeight = 0;
         this.andesiteMaxHeight = 80;
         this.coalSize = 17;
         this.coalCount = 20;
         this.coalMinHeight = 0;
         this.coalMaxHeight = 128;
         this.ironSize = 9;
         this.ironCount = 20;
         this.ironMinHeight = 0;
         this.ironMaxHeight = 64;
         this.goldSize = 9;
         this.goldCount = 2;
         this.goldMinHeight = 0;
         this.goldMaxHeight = 32;
         this.redstoneSize = 8;
         this.redstoneCount = 8;
         this.redstoneMinHeight = 0;
         this.redstoneMaxHeight = 16;
         this.diamondSize = 8;
         this.diamondCount = 1;
         this.diamondMinHeight = 0;
         this.diamondMaxHeight = 16;
         this.lapisSize = 7;
         this.lapisCount = 1;
         this.lapisCenterHeight = 16;
         this.lapisSpread = 16;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            ChunkProviderSettings.Factory var5 = (ChunkProviderSettings.Factory)var1;
            return this.andesiteCount != var5.andesiteCount
               ? false
               : (
                  this.andesiteMaxHeight != var5.andesiteMaxHeight
                     ? false
                     : (
                        this.andesiteMinHeight != var5.andesiteMinHeight
                           ? false
                           : (
                              this.andesiteSize != var5.andesiteSize
                                 ? false
                                 : (
                                    Float.compare(var5.baseSize, this.baseSize) != 0
                                       ? false
                                       : (
                                          Float.compare(var5.biomeDepthOffset, this.biomeDepthOffset) != 0
                                             ? false
                                             : (
                                                Float.compare(var5.biomeDepthWeight, this.biomeDepthWeight) != 0
                                                   ? false
                                                   : (
                                                      Float.compare(var5.biomeScaleOffset, this.biomeScaleOffset) != 0
                                                         ? false
                                                         : (
                                                            Float.compare(var5.biomeScaleWeight, this.biomeScaleWeight) != 0
                                                               ? false
                                                               : (
                                                                  this.biomeSize != var5.biomeSize
                                                                     ? false
                                                                     : (
                                                                        this.coalCount != var5.coalCount
                                                                           ? false
                                                                           : (
                                                                              this.coalMaxHeight != var5.coalMaxHeight
                                                                                 ? false
                                                                                 : (
                                                                                    this.coalMinHeight != var5.coalMinHeight
                                                                                       ? false
                                                                                       : (
                                                                                          this.coalSize != var5.coalSize
                                                                                             ? false
                                                                                             : (
                                                                                                Float.compare(var5.coordinateScale, this.coordinateScale) != 0
                                                                                                   ? false
                                                                                                   : (
                                                                                                      Float.compare(
                                                                                                               var5.depthNoiseScaleExponent,
                                                                                                               this.depthNoiseScaleExponent
                                                                                                            )
                                                                                                            != 0
                                                                                                         ? false
                                                                                                         : (
                                                                                                            Float.compare(
                                                                                                                     var5.depthNoiseScaleX,
                                                                                                                     this.depthNoiseScaleX
                                                                                                                  )
                                                                                                                  != 0
                                                                                                               ? false
                                                                                                               : (
                                                                                                                  Float.compare(
                                                                                                                           var5.depthNoiseScaleZ,
                                                                                                                           this.depthNoiseScaleZ
                                                                                                                        )
                                                                                                                        != 0
                                                                                                                     ? false
                                                                                                                     : (
                                                                                                                        this.diamondCount != var5.diamondCount
                                                                                                                           ? false
                                                                                                                           : (
                                                                                                                              this.diamondMaxHeight
                                                                                                                                    != var5.diamondMaxHeight
                                                                                                                                 ? false
                                                                                                                                 : (
                                                                                                                                    this.diamondMinHeight
                                                                                                                                          != var5.diamondMinHeight
                                                                                                                                       ? false
                                                                                                                                       : (
                                                                                                                                          this.diamondSize
                                                                                                                                                != var5.diamondSize
                                                                                                                                             ? false
                                                                                                                                             : (
                                                                                                                                                this.dioriteCount
                                                                                                                                                      != var5.dioriteCount
                                                                                                                                                   ? false
                                                                                                                                                   : (
                                                                                                                                                      this.dioriteMaxHeight
                                                                                                                                                            != var5.dioriteMaxHeight
                                                                                                                                                         ? false
                                                                                                                                                         : (
                                                                                                                                                            this.dioriteMinHeight
                                                                                                                                                                  != var5.dioriteMinHeight
                                                                                                                                                               ? false
                                                                                                                                                               : (
                                                                                                                                                                  this.dioriteSize
                                                                                                                                                                        != var5.dioriteSize
                                                                                                                                                                     ? false
                                                                                                                                                                     : (
                                                                                                                                                                        this.dirtCount
                                                                                                                                                                              != var5.dirtCount
                                                                                                                                                                           ? false
                                                                                                                                                                           : (
                                                                                                                                                                              this.dirtMaxHeight
                                                                                                                                                                                    != var5.dirtMaxHeight
                                                                                                                                                                                 ? false
                                                                                                                                                                                 : (
                                                                                                                                                                                    this.dirtMinHeight
                                                                                                                                                                                          != var5.dirtMinHeight
                                                                                                                                                                                       ? false
                                                                                                                                                                                       : (
                                                                                                                                                                                          this.dirtSize
                                                                                                                                                                                                != var5.dirtSize
                                                                                                                                                                                             ? false
                                                                                                                                                                                             : (
                                                                                                                                                                                                this.dungeonChance
                                                                                                                                                                                                      != var5.dungeonChance
                                                                                                                                                                                                   ? false
                                                                                                                                                                                                   : (
                                                                                                                                                                                                      this.fixedBiome
                                                                                                                                                                                                            != var5.fixedBiome
                                                                                                                                                                                                         ? false
                                                                                                                                                                                                         : (
                                                                                                                                                                                                            this.goldCount
                                                                                                                                                                                                                  != var5.goldCount
                                                                                                                                                                                                               ? false
                                                                                                                                                                                                               : (
                                                                                                                                                                                                                  this.goldMaxHeight
                                                                                                                                                                                                                        != var5.goldMaxHeight
                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                        this.goldMinHeight
                                                                                                                                                                                                                              != var5.goldMinHeight
                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                              this.goldSize
                                                                                                                                                                                                                                    != var5.goldSize
                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                    this.graniteCount
                                                                                                                                                                                                                                          != var5.graniteCount
                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                          this.graniteMaxHeight
                                                                                                                                                                                                                                                != var5.graniteMaxHeight
                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                this.graniteMinHeight
                                                                                                                                                                                                                                                      != var5.graniteMinHeight
                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                      this.graniteSize
                                                                                                                                                                                                                                                            != var5.graniteSize
                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                            this.gravelCount
                                                                                                                                                                                                                                                                  != var5.gravelCount
                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                  this.gravelMaxHeight
                                                                                                                                                                                                                                                                        != var5.gravelMaxHeight
                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                        this.gravelMinHeight
                                                                                                                                                                                                                                                                              != var5.gravelMinHeight
                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                              this.gravelSize
                                                                                                                                                                                                                                                                                    != var5.gravelSize
                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                    Float.compare(
                                                                                                                                                                                                                                                                                             var5.heightScale,
                                                                                                                                                                                                                                                                                             this.heightScale
                                                                                                                                                                                                                                                                                          )
                                                                                                                                                                                                                                                                                          != 0
                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                          this.ironCount
                                                                                                                                                                                                                                                                                                != var5.ironCount
                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                this.ironMaxHeight
                                                                                                                                                                                                                                                                                                      != var5.ironMaxHeight
                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                      this.ironMinHeight
                                                                                                                                                                                                                                                                                                            != var5.ironMinHeight
                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                            this.ironSize
                                                                                                                                                                                                                                                                                                                  != var5.ironSize
                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                  this.lapisCenterHeight
                                                                                                                                                                                                                                                                                                                        != var5.lapisCenterHeight
                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                        this.lapisCount
                                                                                                                                                                                                                                                                                                                              != var5.lapisCount
                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                              this.lapisSize
                                                                                                                                                                                                                                                                                                                                    != var5.lapisSize
                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                    this.lapisSpread
                                                                                                                                                                                                                                                                                                                                          != var5.lapisSpread
                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                                                                          this.lavaLakeChance
                                                                                                                                                                                                                                                                                                                                                != var5.lavaLakeChance
                                                                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                                                                Float.compare(
                                                                                                                                                                                                                                                                                                                                                         var5.lowerLimitScale,
                                                                                                                                                                                                                                                                                                                                                         this.lowerLimitScale
                                                                                                                                                                                                                                                                                                                                                      )
                                                                                                                                                                                                                                                                                                                                                      != 0
                                                                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                                                                      Float.compare(
                                                                                                                                                                                                                                                                                                                                                               var5.mainNoiseScaleX,
                                                                                                                                                                                                                                                                                                                                                               this.mainNoiseScaleX
                                                                                                                                                                                                                                                                                                                                                            )
                                                                                                                                                                                                                                                                                                                                                            != 0
                                                                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                                                                            Float.compare(
                                                                                                                                                                                                                                                                                                                                                                     var5.mainNoiseScaleY,
                                                                                                                                                                                                                                                                                                                                                                     this.mainNoiseScaleY
                                                                                                                                                                                                                                                                                                                                                                  )
                                                                                                                                                                                                                                                                                                                                                                  != 0
                                                                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                                                                  Float.compare(
                                                                                                                                                                                                                                                                                                                                                                           var5.mainNoiseScaleZ,
                                                                                                                                                                                                                                                                                                                                                                           this.mainNoiseScaleZ
                                                                                                                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                                                                                                                        != 0
                                                                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                                                                        this.redstoneCount
                                                                                                                                                                                                                                                                                                                                                                              != var5.redstoneCount
                                                                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                                                                              this.redstoneMaxHeight
                                                                                                                                                                                                                                                                                                                                                                                    != var5.redstoneMaxHeight
                                                                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                                                                    this.redstoneMinHeight
                                                                                                                                                                                                                                                                                                                                                                                          != var5.redstoneMinHeight
                                                                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                                                                                                                          this.redstoneSize
                                                                                                                                                                                                                                                                                                                                                                                                != var5.redstoneSize
                                                                                                                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                                                                                                                this.riverSize
                                                                                                                                                                                                                                                                                                                                                                                                      != var5.riverSize
                                                                                                                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                                                                                                                      this.seaLevel
                                                                                                                                                                                                                                                                                                                                                                                                            != var5.seaLevel
                                                                                                                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                                                                                                                            Float.compare(
                                                                                                                                                                                                                                                                                                                                                                                                                     var5.stretchY,
                                                                                                                                                                                                                                                                                                                                                                                                                     this.stretchY
                                                                                                                                                                                                                                                                                                                                                                                                                  )
                                                                                                                                                                                                                                                                                                                                                                                                                  != 0
                                                                                                                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                                                                                                                  Float.compare(
                                                                                                                                                                                                                                                                                                                                                                                                                           var5.upperLimitScale,
                                                                                                                                                                                                                                                                                                                                                                                                                           this.upperLimitScale
                                                                                                                                                                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                                                                                                                                                                        != 0
                                                                                                                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                                                                                                                        this.useCaves
                                                                                                                                                                                                                                                                                                                                                                                                                              != var5.useCaves
                                                                                                                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                                                                                                                              this.useDungeons
                                                                                                                                                                                                                                                                                                                                                                                                                                    != var5.useDungeons
                                                                                                                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                                                                                                                    this.useLavaLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                          != var5.useLavaLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                       : (
                                                                                                                                                                                                                                                                                                                                                                                                                                          this.useLavaOceans
                                                                                                                                                                                                                                                                                                                                                                                                                                                != var5.useLavaOceans
                                                                                                                                                                                                                                                                                                                                                                                                                                             ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                             : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                this.useMineShafts
                                                                                                                                                                                                                                                                                                                                                                                                                                                      != var5.useMineShafts
                                                                                                                                                                                                                                                                                                                                                                                                                                                   ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                   : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                      this.useRavines
                                                                                                                                                                                                                                                                                                                                                                                                                                                            != var5.useRavines
                                                                                                                                                                                                                                                                                                                                                                                                                                                         ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                         : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                            this.useStrongholds
                                                                                                                                                                                                                                                                                                                                                                                                                                                                  != var5.useStrongholds
                                                                                                                                                                                                                                                                                                                                                                                                                                                               ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                               : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                  this.useTemples
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        != var5.useTemples
                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                     : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        this.useMonuments
                                                                                                                                                                                                                                                                                                                                                                                                                                                                              != var5.useMonuments
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                              this.useVillages
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    != var5.useVillages
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 : (
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    this.useWaterLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          != var5.useWaterLakes
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ? false
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       : this.waterLakeChance
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          == var5.waterLakeChance
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                                                                           )
                                                                                                                                                                                                                                                                     )
                                                                                                                                                                                                                                                               )
                                                                                                                                                                                                                                                         )
                                                                                                                                                                                                                                                   )
                                                                                                                                                                                                                                             )
                                                                                                                                                                                                                                       )
                                                                                                                                                                                                                                 )
                                                                                                                                                                                                                           )
                                                                                                                                                                                                                     )
                                                                                                                                                                                                               )
                                                                                                                                                                                                         )
                                                                                                                                                                                                   )
                                                                                                                                                                                             )
                                                                                                                                                                                       )
                                                                                                                                                                                 )
                                                                                                                                                                           )
                                                                                                                                                                     )
                                                                                                                                                               )
                                                                                                                                                         )
                                                                                                                                                   )
                                                                                                                                             )
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                           )
                                                                                                                     )
                                                                                                               )
                                                                                                         )
                                                                                                   )
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           )
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               );
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int var5 = this.coordinateScale != 0.0F ? Float.floatToIntBits(this.coordinateScale) : 0;
         var5 = 31 * var5 + (this.heightScale != 0.0F ? Float.floatToIntBits(this.heightScale) : 0);
         var5 = 31 * var5 + (this.upperLimitScale != 0.0F ? Float.floatToIntBits(this.upperLimitScale) : 0);
         var5 = 31 * var5 + (this.lowerLimitScale != 0.0F ? Float.floatToIntBits(this.lowerLimitScale) : 0);
         var5 = 31 * var5 + (this.depthNoiseScaleX != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleX) : 0);
         var5 = 31 * var5 + (this.depthNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleZ) : 0);
         var5 = 31 * var5 + (this.depthNoiseScaleExponent != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleExponent) : 0);
         var5 = 31 * var5 + (this.mainNoiseScaleX != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleX) : 0);
         var5 = 31 * var5 + (this.mainNoiseScaleY != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleY) : 0);
         var5 = 31 * var5 + (this.mainNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleZ) : 0);
         var5 = 31 * var5 + (this.baseSize != 0.0F ? Float.floatToIntBits(this.baseSize) : 0);
         var5 = 31 * var5 + (this.stretchY != 0.0F ? Float.floatToIntBits(this.stretchY) : 0);
         var5 = 31 * var5 + (this.biomeDepthWeight != 0.0F ? Float.floatToIntBits(this.biomeDepthWeight) : 0);
         var5 = 31 * var5 + (this.biomeDepthOffset != 0.0F ? Float.floatToIntBits(this.biomeDepthOffset) : 0);
         var5 = 31 * var5 + (this.biomeScaleWeight != 0.0F ? Float.floatToIntBits(this.biomeScaleWeight) : 0);
         var5 = 31 * var5 + (this.biomeScaleOffset != 0.0F ? Float.floatToIntBits(this.biomeScaleOffset) : 0);
         var5 = 31 * var5 + this.seaLevel;
         var5 = 31 * var5 + (this.useCaves ? 1 : 0);
         var5 = 31 * var5 + (this.useDungeons ? 1 : 0);
         var5 = 31 * var5 + this.dungeonChance;
         var5 = 31 * var5 + (this.useStrongholds ? 1 : 0);
         var5 = 31 * var5 + (this.useVillages ? 1 : 0);
         var5 = 31 * var5 + (this.useMineShafts ? 1 : 0);
         var5 = 31 * var5 + (this.useTemples ? 1 : 0);
         var5 = 31 * var5 + (this.useMonuments ? 1 : 0);
         var5 = 31 * var5 + (this.useRavines ? 1 : 0);
         var5 = 31 * var5 + (this.useWaterLakes ? 1 : 0);
         var5 = 31 * var5 + this.waterLakeChance;
         var5 = 31 * var5 + (this.useLavaLakes ? 1 : 0);
         var5 = 31 * var5 + this.lavaLakeChance;
         var5 = 31 * var5 + (this.useLavaOceans ? 1 : 0);
         var5 = 31 * var5 + this.fixedBiome;
         var5 = 31 * var5 + this.biomeSize;
         var5 = 31 * var5 + this.riverSize;
         var5 = 31 * var5 + this.dirtSize;
         var5 = 31 * var5 + this.dirtCount;
         var5 = 31 * var5 + this.dirtMinHeight;
         var5 = 31 * var5 + this.dirtMaxHeight;
         var5 = 31 * var5 + this.gravelSize;
         var5 = 31 * var5 + this.gravelCount;
         var5 = 31 * var5 + this.gravelMinHeight;
         var5 = 31 * var5 + this.gravelMaxHeight;
         var5 = 31 * var5 + this.graniteSize;
         var5 = 31 * var5 + this.graniteCount;
         var5 = 31 * var5 + this.graniteMinHeight;
         var5 = 31 * var5 + this.graniteMaxHeight;
         var5 = 31 * var5 + this.dioriteSize;
         var5 = 31 * var5 + this.dioriteCount;
         var5 = 31 * var5 + this.dioriteMinHeight;
         var5 = 31 * var5 + this.dioriteMaxHeight;
         var5 = 31 * var5 + this.andesiteSize;
         var5 = 31 * var5 + this.andesiteCount;
         var5 = 31 * var5 + this.andesiteMinHeight;
         var5 = 31 * var5 + this.andesiteMaxHeight;
         var5 = 31 * var5 + this.coalSize;
         var5 = 31 * var5 + this.coalCount;
         var5 = 31 * var5 + this.coalMinHeight;
         var5 = 31 * var5 + this.coalMaxHeight;
         var5 = 31 * var5 + this.ironSize;
         var5 = 31 * var5 + this.ironCount;
         var5 = 31 * var5 + this.ironMinHeight;
         var5 = 31 * var5 + this.ironMaxHeight;
         var5 = 31 * var5 + this.goldSize;
         var5 = 31 * var5 + this.goldCount;
         var5 = 31 * var5 + this.goldMinHeight;
         var5 = 31 * var5 + this.goldMaxHeight;
         var5 = 31 * var5 + this.redstoneSize;
         var5 = 31 * var5 + this.redstoneCount;
         var5 = 31 * var5 + this.redstoneMinHeight;
         var5 = 31 * var5 + this.redstoneMaxHeight;
         var5 = 31 * var5 + this.diamondSize;
         var5 = 31 * var5 + this.diamondCount;
         var5 = 31 * var5 + this.diamondMinHeight;
         var5 = 31 * var5 + this.diamondMaxHeight;
         var5 = 31 * var5 + this.lapisSize;
         var5 = 31 * var5 + this.lapisCount;
         var5 = 31 * var5 + this.lapisCenterHeight;
         return 31 * var5 + this.lapisSpread;
      }

      public ChunkProviderSettings func_177864_b() {
         return new ChunkProviderSettings(this);
      }
   }

   public static class Serializer implements JsonDeserializer<ChunkProviderSettings.Factory>, JsonSerializer<ChunkProviderSettings.Factory> {
      private static final long a = 63486794459450L;
      private static final long e = 8959328030296113190L;

      public ChunkProviderSettings.Factory deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var7 = var1.getAsJsonObject();
         ChunkProviderSettings.Factory var8 = new ChunkProviderSettings.Factory();

         try {
            var8.coordinateScale = JsonUtils.getFloat(var7, "coordinateScale", var8.coordinateScale);
            var8.heightScale = JsonUtils.getFloat(var7, "heightScale", var8.heightScale);
            var8.lowerLimitScale = JsonUtils.getFloat(var7, "lowerLimitScale", var8.lowerLimitScale);
            var8.upperLimitScale = JsonUtils.getFloat(var7, "upperLimitScale", var8.upperLimitScale);
            var8.depthNoiseScaleX = JsonUtils.getFloat(var7, "depthNoiseScaleX", var8.depthNoiseScaleX);
            var8.depthNoiseScaleZ = JsonUtils.getFloat(var7, "depthNoiseScaleZ", var8.depthNoiseScaleZ);
            var8.depthNoiseScaleExponent = JsonUtils.getFloat(var7, "depthNoiseScaleExponent", var8.depthNoiseScaleExponent);
            var8.mainNoiseScaleX = JsonUtils.getFloat(var7, "mainNoiseScaleX", var8.mainNoiseScaleX);
            var8.mainNoiseScaleY = JsonUtils.getFloat(var7, "mainNoiseScaleY", var8.mainNoiseScaleY);
            var8.mainNoiseScaleZ = JsonUtils.getFloat(var7, "mainNoiseScaleZ", var8.mainNoiseScaleZ);
            var8.baseSize = JsonUtils.getFloat(var7, "baseSize", var8.baseSize);
            var8.stretchY = JsonUtils.getFloat(var7, "stretchY", var8.stretchY);
            var8.biomeDepthWeight = JsonUtils.getFloat(var7, "biomeDepthWeight", var8.biomeDepthWeight);
            var8.biomeDepthOffset = JsonUtils.getFloat(var7, "biomeDepthOffset", var8.biomeDepthOffset);
            var8.biomeScaleWeight = JsonUtils.getFloat(var7, "biomeScaleWeight", var8.biomeScaleWeight);
            var8.biomeScaleOffset = JsonUtils.getFloat(var7, "biomeScaleOffset", var8.biomeScaleOffset);
            var8.seaLevel = JsonUtils.getInt(var7, "seaLevel", var8.seaLevel);
            var8.useCaves = JsonUtils.getBoolean(var7, "useCaves", var8.useCaves);
            var8.useDungeons = JsonUtils.getBoolean(var7, "useDungeons", var8.useDungeons);
            var8.dungeonChance = JsonUtils.getInt(var7, "dungeonChance", var8.dungeonChance);
            var8.useStrongholds = JsonUtils.getBoolean(var7, "useStrongholds", var8.useStrongholds);
            var8.useVillages = JsonUtils.getBoolean(var7, "useVillages", var8.useVillages);
            var8.useMineShafts = JsonUtils.getBoolean(var7, "useMineShafts", var8.useMineShafts);
            var8.useTemples = JsonUtils.getBoolean(var7, "useTemples", var8.useTemples);
            var8.useMonuments = JsonUtils.getBoolean(var7, "useMonuments", var8.useMonuments);
            var8.useRavines = JsonUtils.getBoolean(var7, "useRavines", var8.useRavines);
            var8.useWaterLakes = JsonUtils.getBoolean(var7, "useWaterLakes", var8.useWaterLakes);
            var8.waterLakeChance = JsonUtils.getInt(var7, "waterLakeChance", var8.waterLakeChance);
            var8.useLavaLakes = JsonUtils.getBoolean(var7, "useLavaLakes", var8.useLavaLakes);
            var8.lavaLakeChance = JsonUtils.getInt(var7, "lavaLakeChance", var8.lavaLakeChance);
            var8.useLavaOceans = JsonUtils.getBoolean(var7, "useLavaOceans", var8.useLavaOceans);
            var8.fixedBiome = JsonUtils.getInt(var7, "fixedBiome", var8.fixedBiome);
            if (var8.fixedBiome >= (int)e || var8.fixedBiome < -1) {
               var8.fixedBiome = -1;
            } else if (var8.fixedBiome >= BiomeGenBase.hell.biomeID) {
               var8.fixedBiome += 2;
            }

            var8.biomeSize = JsonUtils.getInt(var7, "biomeSize", var8.biomeSize);
            var8.riverSize = JsonUtils.getInt(var7, "riverSize", var8.riverSize);
            var8.dirtSize = JsonUtils.getInt(var7, "dirtSize", var8.dirtSize);
            var8.dirtCount = JsonUtils.getInt(var7, "dirtCount", var8.dirtCount);
            var8.dirtMinHeight = JsonUtils.getInt(var7, "dirtMinHeight", var8.dirtMinHeight);
            var8.dirtMaxHeight = JsonUtils.getInt(var7, "dirtMaxHeight", var8.dirtMaxHeight);
            var8.gravelSize = JsonUtils.getInt(var7, "gravelSize", var8.gravelSize);
            var8.gravelCount = JsonUtils.getInt(var7, "gravelCount", var8.gravelCount);
            var8.gravelMinHeight = JsonUtils.getInt(var7, "gravelMinHeight", var8.gravelMinHeight);
            var8.gravelMaxHeight = JsonUtils.getInt(var7, "gravelMaxHeight", var8.gravelMaxHeight);
            var8.graniteSize = JsonUtils.getInt(var7, "graniteSize", var8.graniteSize);
            var8.graniteCount = JsonUtils.getInt(var7, "graniteCount", var8.graniteCount);
            var8.graniteMinHeight = JsonUtils.getInt(var7, "graniteMinHeight", var8.graniteMinHeight);
            var8.graniteMaxHeight = JsonUtils.getInt(var7, "graniteMaxHeight", var8.graniteMaxHeight);
            var8.dioriteSize = JsonUtils.getInt(var7, "dioriteSize", var8.dioriteSize);
            var8.dioriteCount = JsonUtils.getInt(var7, "dioriteCount", var8.dioriteCount);
            var8.dioriteMinHeight = JsonUtils.getInt(var7, "dioriteMinHeight", var8.dioriteMinHeight);
            var8.dioriteMaxHeight = JsonUtils.getInt(var7, "dioriteMaxHeight", var8.dioriteMaxHeight);
            var8.andesiteSize = JsonUtils.getInt(var7, "andesiteSize", var8.andesiteSize);
            var8.andesiteCount = JsonUtils.getInt(var7, "andesiteCount", var8.andesiteCount);
            var8.andesiteMinHeight = JsonUtils.getInt(var7, "andesiteMinHeight", var8.andesiteMinHeight);
            var8.andesiteMaxHeight = JsonUtils.getInt(var7, "andesiteMaxHeight", var8.andesiteMaxHeight);
            var8.coalSize = JsonUtils.getInt(var7, "coalSize", var8.coalSize);
            var8.coalCount = JsonUtils.getInt(var7, "coalCount", var8.coalCount);
            var8.coalMinHeight = JsonUtils.getInt(var7, "coalMinHeight", var8.coalMinHeight);
            var8.coalMaxHeight = JsonUtils.getInt(var7, "coalMaxHeight", var8.coalMaxHeight);
            var8.ironSize = JsonUtils.getInt(var7, "ironSize", var8.ironSize);
            var8.ironCount = JsonUtils.getInt(var7, "ironCount", var8.ironCount);
            var8.ironMinHeight = JsonUtils.getInt(var7, "ironMinHeight", var8.ironMinHeight);
            var8.ironMaxHeight = JsonUtils.getInt(var7, "ironMaxHeight", var8.ironMaxHeight);
            var8.goldSize = JsonUtils.getInt(var7, "goldSize", var8.goldSize);
            var8.goldCount = JsonUtils.getInt(var7, "goldCount", var8.goldCount);
            var8.goldMinHeight = JsonUtils.getInt(var7, "goldMinHeight", var8.goldMinHeight);
            var8.goldMaxHeight = JsonUtils.getInt(var7, "goldMaxHeight", var8.goldMaxHeight);
            var8.redstoneSize = JsonUtils.getInt(var7, "redstoneSize", var8.redstoneSize);
            var8.redstoneCount = JsonUtils.getInt(var7, "redstoneCount", var8.redstoneCount);
            var8.redstoneMinHeight = JsonUtils.getInt(var7, "redstoneMinHeight", var8.redstoneMinHeight);
            var8.redstoneMaxHeight = JsonUtils.getInt(var7, "redstoneMaxHeight", var8.redstoneMaxHeight);
            var8.diamondSize = JsonUtils.getInt(var7, "diamondSize", var8.diamondSize);
            var8.diamondCount = JsonUtils.getInt(var7, "diamondCount", var8.diamondCount);
            var8.diamondMinHeight = JsonUtils.getInt(var7, "diamondMinHeight", var8.diamondMinHeight);
            var8.diamondMaxHeight = JsonUtils.getInt(var7, "diamondMaxHeight", var8.diamondMaxHeight);
            var8.lapisSize = JsonUtils.getInt(var7, "lapisSize", var8.lapisSize);
            var8.lapisCount = JsonUtils.getInt(var7, "lapisCount", var8.lapisCount);
            var8.lapisCenterHeight = JsonUtils.getInt(var7, "lapisCenterHeight", var8.lapisCenterHeight);
            var8.lapisSpread = JsonUtils.getInt(var7, "lapisSpread", var8.lapisSpread);
         } catch (Exception var10) {
         }

         return var8;
      }

      public JsonElement serialize(ChunkProviderSettings.Factory var1, Type var2, JsonSerializationContext var3) {
         JsonObject var7 = new JsonObject();
         var7.addProperty("coordinateScale", var1.coordinateScale);
         var7.addProperty("heightScale", var1.heightScale);
         var7.addProperty("lowerLimitScale", var1.lowerLimitScale);
         var7.addProperty("upperLimitScale", var1.upperLimitScale);
         var7.addProperty("depthNoiseScaleX", var1.depthNoiseScaleX);
         var7.addProperty("depthNoiseScaleZ", var1.depthNoiseScaleZ);
         var7.addProperty("depthNoiseScaleExponent", var1.depthNoiseScaleExponent);
         var7.addProperty("mainNoiseScaleX", var1.mainNoiseScaleX);
         var7.addProperty("mainNoiseScaleY", var1.mainNoiseScaleY);
         var7.addProperty("mainNoiseScaleZ", var1.mainNoiseScaleZ);
         var7.addProperty("baseSize", var1.baseSize);
         var7.addProperty("stretchY", var1.stretchY);
         var7.addProperty("biomeDepthWeight", var1.biomeDepthWeight);
         var7.addProperty("biomeDepthOffset", var1.biomeDepthOffset);
         var7.addProperty("biomeScaleWeight", var1.biomeScaleWeight);
         var7.addProperty("biomeScaleOffset", var1.biomeScaleOffset);
         var7.addProperty("seaLevel", var1.seaLevel);
         var7.addProperty("useCaves", var1.useCaves);
         var7.addProperty("useDungeons", var1.useDungeons);
         var7.addProperty("dungeonChance", var1.dungeonChance);
         var7.addProperty("useStrongholds", var1.useStrongholds);
         var7.addProperty("useVillages", var1.useVillages);
         var7.addProperty("useMineShafts", var1.useMineShafts);
         var7.addProperty("useTemples", var1.useTemples);
         var7.addProperty("useMonuments", var1.useMonuments);
         var7.addProperty("useRavines", var1.useRavines);
         var7.addProperty("useWaterLakes", var1.useWaterLakes);
         var7.addProperty("waterLakeChance", var1.waterLakeChance);
         var7.addProperty("useLavaLakes", var1.useLavaLakes);
         var7.addProperty("lavaLakeChance", var1.lavaLakeChance);
         var7.addProperty("useLavaOceans", var1.useLavaOceans);
         var7.addProperty("fixedBiome", var1.fixedBiome);
         var7.addProperty("biomeSize", var1.biomeSize);
         var7.addProperty("riverSize", var1.riverSize);
         var7.addProperty("dirtSize", var1.dirtSize);
         var7.addProperty("dirtCount", var1.dirtCount);
         var7.addProperty("dirtMinHeight", var1.dirtMinHeight);
         var7.addProperty("dirtMaxHeight", var1.dirtMaxHeight);
         var7.addProperty("gravelSize", var1.gravelSize);
         var7.addProperty("gravelCount", var1.gravelCount);
         var7.addProperty("gravelMinHeight", var1.gravelMinHeight);
         var7.addProperty("gravelMaxHeight", var1.gravelMaxHeight);
         var7.addProperty("graniteSize", var1.graniteSize);
         var7.addProperty("graniteCount", var1.graniteCount);
         var7.addProperty("graniteMinHeight", var1.graniteMinHeight);
         var7.addProperty("graniteMaxHeight", var1.graniteMaxHeight);
         var7.addProperty("dioriteSize", var1.dioriteSize);
         var7.addProperty("dioriteCount", var1.dioriteCount);
         var7.addProperty("dioriteMinHeight", var1.dioriteMinHeight);
         var7.addProperty("dioriteMaxHeight", var1.dioriteMaxHeight);
         var7.addProperty("andesiteSize", var1.andesiteSize);
         var7.addProperty("andesiteCount", var1.andesiteCount);
         var7.addProperty("andesiteMinHeight", var1.andesiteMinHeight);
         var7.addProperty("andesiteMaxHeight", var1.andesiteMaxHeight);
         var7.addProperty("coalSize", var1.coalSize);
         var7.addProperty("coalCount", var1.coalCount);
         var7.addProperty("coalMinHeight", var1.coalMinHeight);
         var7.addProperty("coalMaxHeight", var1.coalMaxHeight);
         var7.addProperty("ironSize", var1.ironSize);
         var7.addProperty("ironCount", var1.ironCount);
         var7.addProperty("ironMinHeight", var1.ironMinHeight);
         var7.addProperty("ironMaxHeight", var1.ironMaxHeight);
         var7.addProperty("goldSize", var1.goldSize);
         var7.addProperty("goldCount", var1.goldCount);
         var7.addProperty("goldMinHeight", var1.goldMinHeight);
         var7.addProperty("goldMaxHeight", var1.goldMaxHeight);
         var7.addProperty("redstoneSize", var1.redstoneSize);
         var7.addProperty("redstoneCount", var1.redstoneCount);
         var7.addProperty("redstoneMinHeight", var1.redstoneMinHeight);
         var7.addProperty("redstoneMaxHeight", var1.redstoneMaxHeight);
         var7.addProperty("diamondSize", var1.diamondSize);
         var7.addProperty("diamondCount", var1.diamondCount);
         var7.addProperty("diamondMinHeight", var1.diamondMinHeight);
         var7.addProperty("diamondMaxHeight", var1.diamondMaxHeight);
         var7.addProperty("lapisSize", var1.lapisSize);
         var7.addProperty("lapisCount", var1.lapisCount);
         var7.addProperty("lapisCenterHeight", var1.lapisCenterHeight);
         var7.addProperty("lapisSpread", var1.lapisSpread);
         return var7;
      }
   }
}
