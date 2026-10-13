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

   public StructureStart(int var1, int var2) {
      this.chunkPosX = var1;
      this.chunkPosZ = var2;
   }

   public StructureBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public LinkedList<StructureComponent> getComponents() {
      return this.components;
   }

   public void generateStructure(World var1, Random var2, StructureBoundingBox var3) {
      Iterator var7 = this.components.iterator();

      while (var7.hasNext()) {
         StructureComponent var8 = (StructureComponent)var7.next();
         if (var8.getBoundingBox().intersectsWith(var3) && !var8.addComponentParts(var1, var2, var3)) {
            var7.remove();
         }
      }
   }

   public void updateBoundingBox() {
      this.boundingBox = StructureBoundingBox.getNewBoundingBox();

      for (StructureComponent var5 : this.components) {
         this.boundingBox.expandTo(var5.getBoundingBox());
      }
   }

   public NBTTagCompound writeStructureComponentsToNBT(int var1, int var2) {
      NBTTagCompound var6 = new NBTTagCompound();
      var6.setString("id", MapGenStructureIO.getStructureStartName(this));
      var6.setInteger("ChunkX", var1);
      var6.setInteger("ChunkZ", var2);
      var6.setTag("BB", this.boundingBox.toNBTTagIntArray());
      NBTTagList var7 = new NBTTagList();

      for (StructureComponent var9 : this.components) {
         var7.appendTag(var9.createStructureBaseNBT());
      }

      var6.setTag("Children", var7);
      this.writeToNBT(var6);
      return var6;
   }

   public void writeToNBT(NBTTagCompound var1) {
   }

   public void readStructureComponentsFromNBT(World var1, NBTTagCompound var2) {
      this.chunkPosX = var2.getInteger("ChunkX");
      this.chunkPosZ = var2.getInteger("ChunkZ");
      if (var2.hasKey("BB")) {
         this.boundingBox = new StructureBoundingBox(var2.getIntArray("BB"));
      }

      NBTTagList var6 = var2.getTagList("Children", (int)i);

      for (int var7 = 0; var7 < var6.tagCount(); var7++) {
         this.components.add(MapGenStructureIO.getStructureComponent(var6.getCompoundTagAt(var7), var1));
      }

      this.readFromNBT(var2);
   }

   public void readFromNBT(NBTTagCompound var1) {
   }

   public void markAvailableHeight(World var1, Random var2, int var3) {
      int var7 = var1.getSeaLevel() - var3;
      int var8 = this.boundingBox.getYSize() + 1;
      if (var8 < var7) {
         var8 += var2.nextInt(var7 - var8);
      }

      int var9 = var8 - this.boundingBox.maxY;
      this.boundingBox.offset(0, var9, 0);

      for (StructureComponent var11 : this.components) {
         var11.func_181138_a(0, var9, 0);
      }
   }

   public void setRandomHeight(World var1, Random var2, int var3, int var4) {
      int var8 = var4 - var3 + 1 - this.boundingBox.getYSize();
      int var9 = 1;
      if (var8 > 1) {
         var9 = var3 + var2.nextInt(var8);
      } else {
         var9 = var3;
      }

      int var10 = var9 - this.boundingBox.minY;
      this.boundingBox.offset(0, var10, 0);

      for (StructureComponent var12 : this.components) {
         var12.func_181138_a(0, var10, 0);
      }
   }

   public boolean isSizeableStructure() {
      return true;
   }

   public boolean func_175788_a(ChunkCoordIntPair var1) {
      return true;
   }

   public void func_175787_b(ChunkCoordIntPair var1) {
   }

   public int getChunkPosX() {
      return this.chunkPosX;
   }

   public int getChunkPosZ() {
      return this.chunkPosZ;
   }
}
