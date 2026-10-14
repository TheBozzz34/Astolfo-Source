package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public abstract class StructureStart {
   public LinkedList<StructureComponent> components = new LinkedList<>();
   public StructureBoundingBox boundingBox;
   private int chunkPosX;
   private int chunkPosZ;
   private static final long b = 139517476950036L;
   private static final long i = -4891874910130929654L;

   public StructureStart() {
   }

   public StructureStart(int chunkX, int chunkZ) {
      this.chunkPosX = chunkX;
      this.chunkPosZ = chunkZ;
   }

   public StructureBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public LinkedList<StructureComponent> getComponents() {
      return this.components;
   }

   public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb) {
      Iterator iterator2 = this.components.iterator();

      while (iterator2.hasNext()) {
         StructureComponent structureComponent = (StructureComponent)iterator2.next();
         if (structureComponent.getBoundingBox().intersectsWith(structurebb) && !structureComponent.addComponentParts(worldIn, rand, structurebb)) {
            iterator2.remove();
         }
      }
   }

   public void updateBoundingBox() {
      this.boundingBox = StructureBoundingBox.getNewBoundingBox();

      for (StructureComponent structureComponent : this.components) {
         this.boundingBox.expandTo(structureComponent.getBoundingBox());
      }
   }

   public NBTTagCompound writeStructureComponentsToNBT(int chunkX, int chunkZ) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      nbtTagCompound.setString("id", MapGenStructureIO.getStructureStartName(this));
      nbtTagCompound.setInteger("ChunkX", chunkX);
      nbtTagCompound.setInteger("ChunkZ", chunkZ);
      nbtTagCompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
      NBTTagList nbtTagList = new NBTTagList();

      for (StructureComponent structureComponent : this.components) {
         nbtTagList.appendTag(structureComponent.createStructureBaseNBT());
      }

      nbtTagCompound.setTag("Children", nbtTagList);
      this.writeToNBT(nbtTagCompound);
      return nbtTagCompound;
   }

   public void writeToNBT(NBTTagCompound tagCompound) {
   }

   public void readStructureComponentsFromNBT(World worldIn, NBTTagCompound tagCompound) {
      this.chunkPosX = tagCompound.getInteger("ChunkX");
      this.chunkPosZ = tagCompound.getInteger("ChunkZ");
      if (tagCompound.hasKey("BB")) {
         this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
      }

      NBTTagList nbtTagList = tagCompound.getTagList("Children", (int)i);

      for (int j = 0; j < nbtTagList.tagCount(); j++) {
         this.components.add(MapGenStructureIO.getStructureComponent(nbtTagList.getCompoundTagAt(j), worldIn));
      }

      this.readFromNBT(tagCompound);
   }

   public void readFromNBT(NBTTagCompound tagCompound) {
   }

   public void markAvailableHeight(World worldIn, Random rand, int j) {
      int i = worldIn.getSeaLevel() - j;
      int k = this.boundingBox.getYSize() + 1;
      if (k < i) {
         k += rand.nextInt(i - k);
      }

      int l = k - this.boundingBox.maxY;
      this.boundingBox.offset(0, l, 0);

      for (StructureComponent structureComponent : this.components) {
         structureComponent.func_181138_a(0, l, 0);
      }
   }

   public void setRandomHeight(World worldIn, Random rand, int j, int k) {
      int i = k - j + 1 - this.boundingBox.getYSize();
      int l = 1;
      if (i > 1) {
         l = j + rand.nextInt(i);
      } else {
         l = j;
      }

      int m = l - this.boundingBox.minY;
      this.boundingBox.offset(0, m, 0);

      for (StructureComponent structureComponent : this.components) {
         structureComponent.func_181138_a(0, m, 0);
      }
   }

   public boolean isSizeableStructure() {
      return true;
   }

   public boolean func_175788_a(ChunkCoordIntPair pair) {
      return true;
   }

   public void func_175787_b(ChunkCoordIntPair pair) {
   }

   public int getChunkPosX() {
      return this.chunkPosX;
   }

   public int getChunkPosZ() {
      return this.chunkPosZ;
   }
}
