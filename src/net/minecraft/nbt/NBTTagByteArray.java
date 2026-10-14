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

   public NBTTagByteArray(byte[] data) {
      this.data = data;
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.data.length);
      output.write(this.data);
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(192L);
      int i = input.readInt();
      sizeTracker.read(8 * i);
      this.data = new byte[i];
      input.readFully(this.data);
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
      byte[] bytes = new byte[this.data.length];
      System.arraycopy(this.data, 0, bytes, 0, this.data.length);
      return new NBTTagByteArray(bytes);
   }

   @Override
   public boolean equals(Object object) {
      return super.equals(object) ? Arrays.equals(this.data, ((NBTTagByteArray)object).data) : false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.data);
   }

   public byte[] getByteArray() {
      return this.data;
   }
}
