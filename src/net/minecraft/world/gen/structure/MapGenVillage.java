package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenVillage extends MapGenStructure {
   public static final List<BiomeGenBase> villageSpawnBiomes = Arrays.asList(BiomeGenBase.plains, BiomeGenBase.desert, BiomeGenBase.savanna);
   private int terrainType;
   private int field_82665_g = 32;
   private int field_82666_h = 8;

   public MapGenVillage() {
   }

   public MapGenVillage(Map<String, String> map) {
      this();

      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("size")) {
            this.terrainType = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.terrainType, 0);
         } else if (((String)entry.getKey()).equals("distance")) {
            this.field_82665_g = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.field_82665_g, this.field_82666_h + 1);
         }
      }
   }

   @Override
   public String getStructureName() {
      return "Village";
   }

   @Override
   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int i = chunkX;
      int j = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.field_82665_g - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.field_82665_g - 1;
      }

      int k = chunkX / this.field_82665_g;
      int l = chunkZ / this.field_82665_g;
      Random random = this.worldObj.setRandomSeed(k, l, 10387312);
      k *= this.field_82665_g;
      l *= this.field_82665_g;
      k += random.nextInt(this.field_82665_g - this.field_82666_h);
      l += random.nextInt(this.field_82665_g - this.field_82666_h);
      if (i == k && j == l) {
         boolean bl = this.worldObj.getWorldChunkManager().areBiomesViable(i * 16 + 8, j * 16 + 8, 0, villageSpawnBiomes);
         if (bl) {
            return true;
         }
      }

      return false;
   }

   @Override
   public StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new MapGenVillage.Start(this.worldObj, this.rand, chunkX, chunkZ, this.terrainType);
   }

   public static class Start extends StructureStart {
      private boolean hasMoreThanTwoComponents;

      public Start() {
      }

      public Start(World worldIn, Random randIn, int x, int z, int size) {
         super(x, z);
         List list = StructureVillagePieces.getStructureVillageWeightedPieceList(randIn, size);
         StructureVillagePieces.Start start = new StructureVillagePieces.Start(
            worldIn.getWorldChunkManager(), 0, randIn, (x << 4) + 2, (z << 4) + 2, list, size
         );
         this.components.add(start);
         start.buildComponent(start, this.components, randIn);
         List list2 = start.field_74930_j;
         List list3 = start.field_74932_i;

         while (!list2.isEmpty() || !list3.isEmpty()) {
            if (list2.isEmpty()) {
               int i = randIn.nextInt(list3.size());
               StructureComponent structureComponent = (StructureComponent)list3.remove(i);
               structureComponent.buildComponent(start, this.components, randIn);
            } else {
               int j = randIn.nextInt(list2.size());
               StructureComponent structureComponent2 = (StructureComponent)list2.remove(j);
               structureComponent2.buildComponent(start, this.components, randIn);
            }
         }

         this.updateBoundingBox();
         int k = 0;

         for (StructureComponent structureComponent3 : this.components) {
            if (!(structureComponent3 instanceof StructureVillagePieces.Road)) {
               k++;
            }
         }

         this.hasMoreThanTwoComponents = k > 2;
      }

      @Override
      public boolean isSizeableStructure() {
         return this.hasMoreThanTwoComponents;
      }

      @Override
      public void writeToNBT(NBTTagCompound tagCompound) {
         super.writeToNBT(tagCompound);
         tagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
      }

      @Override
      public void readFromNBT(NBTTagCompound tagCompound) {
         super.readFromNBT(tagCompound);
         this.hasMoreThanTwoComponents = tagCompound.getBoolean("Valid");
      }
   }
}
