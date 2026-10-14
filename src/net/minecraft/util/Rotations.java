package net.minecraft.util;

import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

public class Rotations {
   public final float x;
   public final float y;
   public final float z;

   public Rotations(float x, float y, float z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Rotations(NBTTagList nbt) {
      this.x = nbt.getFloatAt(0);
      this.y = nbt.getFloatAt(1);
      this.z = nbt.getFloatAt(2);
   }

   public NBTTagList writeToNBT() {
      NBTTagList nbtTagList = new NBTTagList();
      nbtTagList.appendTag(new NBTTagFloat(this.x));
      nbtTagList.appendTag(new NBTTagFloat(this.y));
      nbtTagList.appendTag(new NBTTagFloat(this.z));
      return nbtTagList;
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof Rotations)) {
         return false;
      }

      Rotations rotations = (Rotations)object;
      return this.x == rotations.x && this.y == rotations.y && this.z == rotations.z;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }
}
