package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTBase.NBTPrimitive {
   private int data;
   private static final long a = 76113283055537L;

   public NBTTagInt() {
   }

   public NBTTagInt(int var1) {
      this.data = var1;
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data);
   }

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(96L);
      this.data = var1.readInt();
   }

   @Override
   public byte getId() {
      return 3;
   }

   @Override
   public String toString() {
      return "" + this.data;
   }

   @Override
   public NBTBase copy() {
      return new NBTTagInt(this.data);
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagInt var5 = (NBTTagInt)var1;
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
      return (short)(this.data & 65535);
   }

   @Override
   public byte getByte() {
      return (byte)(this.data & 0xFF);
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
