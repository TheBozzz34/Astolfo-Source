package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase {
   private byte[] data;
   private static final long a = 134345923447267L;

   public NBTTagByteArray() {
   }

   public NBTTagByteArray(byte[] var1) {
      this.data = var1;
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.data.length);
      var1.write(this.data);
   }

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(192L);
      int var6 = var1.readInt();
      var3.read(8 * var6);
      this.data = new byte[var6];
      var1.readFully(this.data);
   }

   @Override
   public byte getId() {
      return 7;
   }

   @Override
   public String toString() {
      return "[" + this.data.length + " bytes]";
   }

   @Override
   public NBTBase copy() {
      byte[] var1 = new byte[this.data.length];
      System.arraycopy(this.data, 0, var1, 0, this.data.length);
      return new NBTTagByteArray(var1);
   }

   @Override
   public boolean equals(Object var1) {
      return super.equals(var1) ? Arrays.equals(this.data, ((NBTTagByteArray)var1).data) : false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.data);
   }

   public byte[] getByteArray() {
      return this.data;
   }
}
