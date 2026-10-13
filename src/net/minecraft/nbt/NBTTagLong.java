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

   public NBTTagLong(long var1) {
      this.data = var1;
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeLong(this.data);
   }

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(128L);
      this.data = var1.readLong();
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
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagLong var5 = (NBTTagLong)var1;
         return this.data == var5.data;
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
