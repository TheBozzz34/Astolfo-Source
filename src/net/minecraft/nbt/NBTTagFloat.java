package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.MathHelper;

public class NBTTagFloat extends NBTBase.NBTPrimitive {
   private float data;
   private static final long a = 102628788829531L;

   public NBTTagFloat() {
   }

   public NBTTagFloat(float var1) {
      this.data = var1;
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeFloat(this.data);
   }

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(96L);
      this.data = var1.readFloat();
   }

   @Override
   public byte getId() {
      return 5;
   }

   @Override
   public String toString() {
      return "" + this.data + "f";
   }

   @Override
   public NBTBase copy() {
      return new NBTTagFloat(this.data);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagFloat var5 = (NBTTagFloat)var1;
         return this.data == var5.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Float.floatToIntBits(this.data);
   }

   @Override
   public long getLong() {
      return (long)this.data;
   }

   @Override
   public int getInt() {
      return MathHelper.floor_float(this.data);
   }

   @Override
   public short getShort() {
      return (short)(MathHelper.floor_float(this.data) & 65535);
   }

   @Override
   public byte getByte() {
      return (byte)(MathHelper.floor_float(this.data) & 0xFF);
   }

   @Override
   public double getDouble() {
      return this.data;
   }

   @Override
   public float getFloat() {
      return this.data;
   }
}
