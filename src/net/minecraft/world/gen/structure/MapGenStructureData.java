package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class MapGenStructureData extends WorldSavedData {
   private NBTTagCompound tagCompound = new NBTTagCompound();
   private static final long a = 96120073789176L;

   public MapGenStructureData(String name) {
      super(name);
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt) {
      this.tagCompound = nbt.getCompoundTag("Features");
   }

   @Override
   public void writeToNBT(NBTTagCompound nbt) {
      nbt.setTag("Features", this.tagCompound);
   }

   public void writeInstance(NBTTagCompound tagCompoundIn, int chunkX, int chunkZ) {
      this.tagCompound.setTag(formatChunkCoords(chunkX, chunkZ), tagCompoundIn);
   }

   public static String formatChunkCoords(int chunkX, int chunkZ) {
      return "[" + chunkX + "," + chunkZ + "]";
   }

   public NBTTagCompound getTagCompound() {
      return this.tagCompound;
   }
}
