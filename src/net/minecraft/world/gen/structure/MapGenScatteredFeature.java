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

   public MapGenScatteredFeature() {
      this.scatteredFeatureSpawnList.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 1, 1, 1));
   }

   public MapGenScatteredFeature(Map<String, String> map) {
      this();

      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("distance")) {
            this.maxDistanceBetweenScatteredFeatures = MathHelper.parseIntWithDefaultAndMax(
               (String)entry.getValue(), this.maxDistanceBetweenScatteredFeatures, this.minDistanceBetweenScatteredFeatures + 1
            );
         }
      }
   }

   @Override
   public String getStructureName() {
      return "Temple";
   }

   @Override
   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int i = chunkX;
      int j = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
      }

      int k = chunkX / this.maxDistanceBetweenScatteredFeatures;
      int l = chunkZ / this.maxDistanceBetweenScatteredFeatures;
      Random random = this.worldObj.setRandomSeed(k, l, 14357617);
      k *= this.maxDistanceBetweenScatteredFeatures;
      l *= this.maxDistanceBetweenScatteredFeatures;
      k += random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
      l += random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
      if (i == k && j == l) {
         BiomeGenBase biomeGenBase = this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(i * 16 + 8, 0, j * 16 + 8));
         if (biomeGenBase == null) {
            return false;
         }

         for (BiomeGenBase biomeGenBase2 : biomelist) {
            if (biomeGenBase == biomeGenBase2) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new MapGenScatteredFeature.Start(this.worldObj, this.rand, chunkX, chunkZ);
   }

   public boolean func_175798_a(BlockPos blockPos) {
      StructureStart structureStart = this.func_175797_c(blockPos);
      if (structureStart != null && structureStart instanceof MapGenScatteredFeature.Start && !structureStart.components.isEmpty()) {
         StructureComponent structureComponent = structureStart.components.getFirst();
         return structureComponent instanceof ComponentScatteredFeaturePieces.SwampHut;
      } else {
         return false;
      }
   }

   public List<BiomeGenBase.SpawnListEntry> getScatteredFeatureSpawnList() {
      return this.scatteredFeatureSpawnList;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(World worldIn, Random random2, int i, int j) {
         super(i, j);
         BiomeGenBase biomeGenBase = worldIn.getBiomeGenForCoords(new BlockPos(i * 16 + 8, 0, j * 16 + 8));
         if (biomeGenBase == BiomeGenBase.jungle || biomeGenBase == BiomeGenBase.jungleHills) {
            ComponentScatteredFeaturePieces.JunglePyramid junglePyramid = new ComponentScatteredFeaturePieces.JunglePyramid(random2, i * 16, j * 16);
            this.components.add(junglePyramid);
         } else if (biomeGenBase == BiomeGenBase.swampland) {
            ComponentScatteredFeaturePieces.SwampHut swampHut = new ComponentScatteredFeaturePieces.SwampHut(random2, i * 16, j * 16);
            this.components.add(swampHut);
         } else if (biomeGenBase == BiomeGenBase.desert || biomeGenBase == BiomeGenBase.desertHills) {
            ComponentScatteredFeaturePieces.DesertPyramid desertPyramid = new ComponentScatteredFeaturePieces.DesertPyramid(random2, i * 16, j * 16);
            this.components.add(desertPyramid);
         }

         this.updateBoundingBox();
      }
   }
}
