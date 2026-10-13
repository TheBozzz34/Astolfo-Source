package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenScatteredFeature extends MapGenStructure {
   private static final List<BiomeGenBase> biomelist = Arrays.asList(
      BiomeGenBase.desert, BiomeGenBase.desertHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.swampland
   );
   private List<BiomeGenBase.SpawnListEntry> scatteredFeatureSpawnList = Lists.newArrayList();
   private int maxDistanceBetweenScatteredFeatures = 32;
   private int minDistanceBetweenScatteredFeatures = 8;
   private static final long c = 76350284654576L;

   public MapGenScatteredFeature() {
      this.scatteredFeatureSpawnList.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 1, 1, 1));
   }

   public MapGenScatteredFeature(Map<String, String> var1) {
      this();

      for (Entry var6 : var1.entrySet()) {
         if (((String)var6.getKey()).equals("distance")) {
            this.maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax(
               (String)var6.getValue(), this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures + 1
            );
         }
      }
   }

   @Override
   public String getStructureName() {
      return "Temple";
   }

   @Override
   public boolean canSpawnStructureAtCoords(int var1, int var2) {
      int var6 = var1;
      int var7 = var2;
      if (var1 < 0) {
         var1 -= this.maxDistanceBetweenScatteredFeatures - 1;
      }

      if (var2 < 0) {
         var2 -= this.maxDistanceBetweenScatteredFeatures - 1;
      }

      int var8 = var1 / this.maxDistanceBetweenScatteredFeatures;
      int var9 = var2 / this.maxDistanceBetweenScatteredFeatures;
      Random var10 = this.worldObj.setRandomSeed(var8, var9, 14357617);
      var8 *= this.maxDistanceBetweenScatteredFeatures;
      var9 *= this.maxDistanceBetweenScatteredFeatures;
      var8 += var10.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
      var9 += var10.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
      if (var6 == var8 && var7 == var9) {
         BiomeGenBase var11 = this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(var6 * 16 + 8, 0, var7 * 16 + 8));
         if (var11 == null) {
            return false;
         }

         for (BiomeGenBase var13 : biomelist) {
            if (var11 == var13) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public StructureStart getStructureStart(int var1, int var2) {
      return new MapGenScatteredFeature.Start(this.worldObj, this.rand, var1, var2);
   }

   public boolean func_175798_a(BlockPos var1) {
      StructureStart var5 = this.func_175797_c(var1);
      if (var5 != null && var5 instanceof MapGenScatteredFeature.Start && !var5.components.isEmpty()) {
         StructureComponent var6 = var5.components.getFirst();
         return var6 instanceof ComponentScatteredFeaturePieces.SwampHut;
      } else {
         return false;
      }
   }

   public List<BiomeGenBase.SpawnListEntry> getScatteredFeatureSpawnList() {
      return this.scatteredFeatureSpawnList;
   }

   public static class Start extends StructureStart {
      private static final long a = 101033698915414L;

      public Start() {
      }

      public Start(World var1, Random var2, int var3, int var4) {
         super(var3, var4);
         BiomeGenBase var8 = var1.getBiomeGenForCoords(new BlockPos(var3 * 16 + 8, 0, var4 * 16 + 8));
         if (var8 == BiomeGenBase.jungle || var8 == BiomeGenBase.jungleHills) {
            ComponentScatteredFeaturePieces.JunglePyramid var11 = new ComponentScatteredFeaturePieces.JunglePyramid(var2, var3 * 16, var4 * 16);
            this.components.add(var11);
         } else if (var8 == BiomeGenBase.swampland) {
            ComponentScatteredFeaturePieces.SwampHut var9 = new ComponentScatteredFeaturePieces.SwampHut(var2, var3 * 16, var4 * 16);
            this.components.add(var9);
         } else if (var8 == BiomeGenBase.desert || var8 == BiomeGenBase.desertHills) {
            ComponentScatteredFeaturePieces.DesertPyramid var10 = new ComponentScatteredFeaturePieces.DesertPyramid(var2, var3 * 16, var4 * 16);
            this.components.add(var10);
         }

         this.updateBoundingBox();
      }
   }
}
