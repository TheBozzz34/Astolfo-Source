package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenStronghold extends MapGenStructure {
   private List<BiomeGenBase> field_151546_e;
   private boolean ranBiomeCheck;
   private ChunkCoordIntPair[] structureCoords = new ChunkCoordIntPair[3];
   private double field_82671_h = 32.0;
   private int field_82672_i = 3;

   public MapGenStronghold() {
      this.field_151546_e = Lists.newArrayList();

      for (BiomeGenBase biomeGenBase : BiomeGenBase.getBiomeGenArray()) {
         if (biomeGenBase != null && biomeGenBase.minHeight > 0.0F) {
            this.field_151546_e.add(biomeGenBase);
         }
      }
   }

   public MapGenStronghold(Map<String, String> map) {
      this();

      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("distance")) {
            this.field_82671_h = MathHelper.parseDoubleWithDefaultAndMax((String)entry.getValue(), this.field_82671_h, 1.0);
         } else if (((String)entry.getKey()).equals("count")) {
            this.structureCoords = new ChunkCoordIntPair[MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.structureCoords.length, 1)];
         } else if (((String)entry.getKey()).equals("spread")) {
            this.field_82672_i = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.field_82672_i, 1);
         }
      }
   }

   @Override
   public String getStructureName() {
      return "Stronghold";
   }

   @Override
   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      if (!this.ranBiomeCheck) {
         Random random = new Random();
         random.setSeed(this.worldObj.getSeed());
         double d = random.nextDouble() * Math.PI * 2.0;
         int i = 1;

         for (int j = 0; j < this.structureCoords.length; j++) {
            double e = (1.25 * i + random.nextDouble()) * this.field_82671_h * i;
            int k = (int)Math.round(Math.cos(d) * e);
            int l = (int)Math.round(Math.sin(d) * e);
            BlockPos blockPos = this.worldObj.getWorldChunkManager().findBiomePosition((k << 4) + 8, (l << 4) + 8, 112, this.field_151546_e, random);
            if (blockPos != null) {
               k = blockPos.getX() >> 4;
               l = blockPos.getZ() >> 4;
            }

            this.structureCoords[j] = new ChunkCoordIntPair(k, l);
            d += (Math.PI * 2) * i / this.field_82672_i;
            if (j == this.field_82672_i) {
               i += 2 + random.nextInt(5);
               this.field_82672_i = this.field_82672_i + 1 + random.nextInt(2);
            }
         }

         this.ranBiomeCheck = true;
      }

      for (ChunkCoordIntPair chunkCoordIntPair : this.structureCoords) {
         if (chunkX == chunkCoordIntPair.chunkXPos && chunkZ == chunkCoordIntPair.chunkZPos) {
            return true;
         }
      }

      return false;
   }

   @Override
   public List<BlockPos> getCoordList() {
      ArrayList arrayList = Lists.newArrayList();

      for (ChunkCoordIntPair chunkCoordIntPair : this.structureCoords) {
         if (chunkCoordIntPair != null) {
            arrayList.add(chunkCoordIntPair.getCenterBlock(64));
         }
      }

      return arrayList;
   }

   @Override
   public StructureStart getStructureStart(int chunkX, int chunkZ) {
      MapGenStronghold.Start start = new MapGenStronghold.Start(this.worldObj, this.rand, chunkX, chunkZ);

      while (start.getComponents().isEmpty() || ((StructureStrongholdPieces.Stairs2)start.getComponents().get(0)).strongholdPortalRoom == null) {
         start = new MapGenStronghold.Start(this.worldObj, this.rand, chunkX, chunkZ);
      }

      return start;
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(World worldIn, Random random2, int i, int j) {
         super(i, j);
         StructureStrongholdPieces.prepareStructurePieces();
         StructureStrongholdPieces.Stairs2 stairs2 = new StructureStrongholdPieces.Stairs2(0, random2, (i << 4) + 2, (j << 4) + 2);
         this.components.add(stairs2);
         stairs2.buildComponent(stairs2, this.components, random2);
         List list = stairs2.field_75026_c;

         while (!list.isEmpty()) {
            int k = random2.nextInt(list.size());
            StructureComponent structureComponent = (StructureComponent)list.remove(k);
            structureComponent.buildComponent(stairs2, this.components, random2);
         }

         this.updateBoundingBox();
         this.markAvailableHeight(worldIn, random2, 10);
      }
   }
}
