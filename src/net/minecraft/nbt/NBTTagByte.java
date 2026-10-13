package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagByte extends NBTBase.NBTPrimitive {
   private byte data;
   private static final long a = 63726145863093L;

   public NBTTagByte() {
   }

   public NBTTagByte(byte var1) {
      this.data = var1;
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeByte(this.data);
   }

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(72L);
      this.data = var1.readByte();
   }

   @Override
   public byte getId() {
      return 1;
   }

   @Override
   public String toString() {
      return "" + this.data + "b";
   }

   @Override
   public NBTBase copy() {
      return new NBTTagByte(this.data);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagByte var5 = (NBTTagByte)var1;
         return this.data == var5.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.data;
   }

   @Override
   public long getLong() {
      return this.data;
   }

   @Override
   public int getInt() {
      return this.data;
   }

   @Override
   public short getShort() {
      return this.data;
   }

   @Override
   public byte getByte() {
      return this.data;
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
