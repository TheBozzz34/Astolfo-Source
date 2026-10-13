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

   public NBTTagShort(short var1) {
      this.data = var1;
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeShort(this.data);
   }

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(80L);
      this.data = var1.readShort();
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
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagShort var5 = (NBTTagShort)var1;
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
