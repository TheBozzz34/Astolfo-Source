package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class StructureOceanMonument extends MapGenStructure {
   private int field_175800_f = 32;
   private int field_175801_g = 5;
   public static final List<BiomeGenBase> field_175802_d = Arrays.asList(
      BiomeGenBase.ocean, BiomeGenBase.deepOcean, BiomeGenBase.river, BiomeGenBase.frozenOcean, BiomeGenBase.frozenRiver
   );
   private static final List<BiomeGenBase.SpawnListEntry> field_175803_h = Lists.newArrayList();
   private static final long c = 49600259522505L;

   public StructureOceanMonument() {
   }

   public StructureOceanMonument(Map<String, String> map) {
      this();

      for (Entry entry : map.entrySet()) {
         if (((String)entry.getKey()).equals("spacing")) {
            this.field_175800_f = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.field_175800_f, 1);
         } else if (((String)entry.getKey()).equals("separation")) {
            this.field_175801_g = MathHelper.parseIntWithDefaultAndMax((String)entry.getValue(), this.field_175801_g, 1);
         }
      }
   }

   @Override
   public String getStructureName() {
      return "Monument";
   }

   @Override
   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      int i = chunkX;
      int j = chunkZ;
      if (chunkX < 0) {
         chunkX -= this.field_175800_f - 1;
      }

      if (chunkZ < 0) {
         chunkZ -= this.field_175800_f - 1;
      }

      int k = chunkX / this.field_175800_f;
      int l = chunkZ / this.field_175800_f;
      Random random = this.worldObj.setRandomSeed(k, l, 10387313);
      k *= this.field_175800_f;
      l *= this.field_175800_f;
      k += (random.nextInt(this.field_175800_f - this.field_175801_g) + random.nextInt(this.field_175800_f - this.field_175801_g)) / 2;
      l += (random.nextInt(this.field_175800_f - this.field_175801_g) + random.nextInt(this.field_175800_f - this.field_175801_g)) / 2;
      if (i == k && j == l) {
         if (this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(i * 16 + 8, 64, j * 16 + 8), (BiomeGenBase)null)
            != BiomeGenBase.deepOcean) {
            return false;
         }

         boolean bl = this.worldObj.getWorldChunkManager().areBiomesViable(i * 16 + 8, j * 16 + 8, 29, field_175802_d);
         if (bl) {
            return true;
         }
      }

      return false;
   }

   @Override
   public StructureStart getStructureStart(int chunkX, int chunkZ) {
      return new StructureOceanMonument.StartMonument(this.worldObj, this.rand, chunkX, chunkZ);
   }

   public List<BiomeGenBase.SpawnListEntry> getScatteredFeatureSpawnList() {
      return field_175803_h;
   }

   static {
      field_175803_h.add(new BiomeGenBase.SpawnListEntry(EntityGuardian.class, 1, 2, 4));
   }

   public static class StartMonument extends StructureStart {
      private Set<ChunkCoordIntPair> field_175791_c = Sets.newHashSet();
      private boolean field_175790_d;
      private static final long a = 16372114707539L;

      public StartMonument() {
      }

      public StartMonument(World worldIn, Random random2, int j, int k) {
         super(j, k);
         this.func_175789_b(worldIn, random2, j, k);
      }

      private void func_175789_b(World worldIn, Random random2, int j, int k) {
         random2.setSeed(worldIn.getSeed());
         long l = random2.nextLong();
         long m = random2.nextLong();
         long n = j * l;
         long l2 = k * m;
         random2.setSeed(n ^ l2 ^ worldIn.getSeed());
         int i = j * 16 + 8 - 29;
         int i2 = k * 16 + 8 - 29;
         EnumFacing enumFacing = EnumFacing.Plane.HORIZONTAL.random(random2);
         this.components.add(new StructureOceanMonumentPieces.MonumentBuilding(random2, i, i2, enumFacing));
         this.updateBoundingBox();
         this.field_175790_d = true;
      }

      @Override
      public void generateStructure(World worldIn, Random randIn, StructureBoundingBox structurebb) {
         if (!this.field_175790_d) {
            this.components.clear();
            this.func_175789_b(worldIn, randIn, this.getChunkPosX(), this.getChunkPosZ());
         }

         super.generateStructure(worldIn, randIn, structurebb);
      }

      @Override
      public boolean func_175788_a(ChunkCoordIntPair pair) {
         return this.field_175791_c.contains(pair) ? false : super.func_175788_a(pair);
      }

      @Override
      public void func_175787_b(ChunkCoordIntPair pair) {
         super.func_175787_b(pair);
         this.field_175791_c.add(pair);
      }

      @Override
      public void writeToNBT(NBTTagCompound tagCompound) {
         super.writeToNBT(tagCompound);
         NBTTagList nbtTagList = new NBTTagList();

         for (ChunkCoordIntPair chunkCoordIntPair : this.field_175791_c) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            nbtTagCompound.setInteger("X", chunkCoordIntPair.chunkXPos);
            nbtTagCompound.setInteger("Z", chunkCoordIntPair.chunkZPos);
            nbtTagList.appendTag(nbtTagCompound);
         }

         tagCompound.setTag("Processed", nbtTagList);
      }

      @Override
      public void readFromNBT(NBTTagCompound tagCompound) {
         super.readFromNBT(tagCompound);
         if (tagCompound.hasKey("Processed", 9)) {
            NBTTagList nbtTagList = tagCompound.getTagList("Processed", 10);

            for (int i = 0; i < nbtTagList.tagCount(); i++) {
               NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
               this.field_175791_c.add(new ChunkCoordIntPair(nbtTagCompound.getInteger("X"), nbtTagCompound.getInteger("Z")));
            }
         }
      }
   }
}
