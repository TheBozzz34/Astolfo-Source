package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagLong extends NBTBase.NBTPrimitive {
   private long data;
   private static final long a = 44759814816039L;
   private static final long c = -1578727250981814240L;

   public NBTTagLong() {
   }

   public NBTTagLong(long data) {
      this.data = data;
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeLong(this.data);
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(128L);
      this.data = input.readLong();
   }

   @Override
   public byte getId() {
      return 4;
   }

   @Override
   public String toString() {
      return "" + this.data + "L";
   }

   @Override
   public NBTBase copy() {
      return new NBTTagLong(this.data);
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NBTTagLong nbtTagLong = (NBTTagLong)object;
         return this.data == nbtTagLong.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ (int)(this.data ^ this.data >>> (int)c);
   }

   @Override
   public long getLong() {
      return this.data;
   }

   @Override
   public int getInt() {
      return (int)(this.data & -4632617565651542433L);
   }

   @Override
   public short getShort() {
      return (short)(this.data & 65535L);
   }

   @Override
   public byte getByte() {
      return (byte)(this.data & 255L);
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
