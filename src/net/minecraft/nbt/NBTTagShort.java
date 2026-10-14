package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase.NBTPrimitive {
   private short data;
   private static final long a = 83460912681668L;
   private static final long c = -4556152138534747905L;

   public NBTTagShort() {
   }

   public NBTTagShort(short data) {
      this.data = data;
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeShort(this.data);
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(80L);
      this.data = input.readShort();
   }

   @Override
   public byte getId() {
      return 2;
   }

   @Override
   public String toString() {
      return "" + this.data + "s";
   }

   @Override
   public NBTBase copy() {
      return new NBTTagShort(this.data);
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NBTTagShort nbtTagShort = (NBTTagShort)object;
         return this.data == nbtTagShort.data;
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
      return (byte)(this.data & (int)c);
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
