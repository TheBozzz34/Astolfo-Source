package net.minecraft.util;

import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;

public class Rotations {
   public final float x;
   public final float y;
   public final float z;
   private static final long a = 28461729343014L;

   public Rotations(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public Rotations(NBTTagList var1) {
      this.x = var1.getFloatAt(0);
      this.y = var1.getFloatAt(1);
      this.z = var1.getFloatAt(2);
   }

   public NBTTagList writeToNBT() {
      NBTTagList var1 = new NBTTagList();
      var1.appendTag(new NBTTagFloat(this.x));
      var1.appendTag(new NBTTagFloat(this.y));
      var1.appendTag(new NBTTagFloat(this.z));
      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof Rotations)) {
         return false;
      }

      Rotations var5 = (Rotations)var1;
      return this.x == var5.x && this.y == var5.y && this.z == var5.z;
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
