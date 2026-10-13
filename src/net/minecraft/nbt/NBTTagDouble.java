package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.MathHelper;

public class NBTTagDouble extends NBTBase.NBTPrimitive {
   private double data;
   private static final long a = 71763514063471L;

   public NBTTagDouble() {
   }

   public NBTTagDouble(double var1) {
      this.data = var1;
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeDouble(this.data);
   }

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(128L);
      this.data = var1.readDouble();
   }

   @Override
   public byte getId() {
      return 6;
   }

   @Override
   public String toString() {
      return "" + this.data + "d";
   }

   @Override
   public NBTBase copy() {
      return new NBTTagDouble(this.data);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagDouble var5 = (NBTTagDouble)var1;
         return this.data == var5.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      long var3 = Double.doubleToLongBits(this.data);
      return super.hashCode() ^ (int)(var3 ^ var3 >>> 32);
   }

   @Override
   public long getLong() {
      return (long)Math.floor(this.data);
   }

   @Override
   public int getInt() {
      return MathHelper.floor_double(this.data);
   }

   @Override
   public short getShort() {
      return (short)(MathHelper.floor_double(this.data) & 65535);
   }

   @Override
   public byte getByte() {
      return (byte)(MathHelper.floor_double(this.data) & 0xFF);
   }

   @Override
   public double getDouble() {
      return this.data;
   }

   @Override
   public float getFloat() {
      return (float)this.data;
   }
}
