package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {
   private int[] intArray;
   private static final long a = 68654914900947L;

   public NBTTagIntArray() {
   }

   public NBTTagIntArray(int[] ints) {
      this.intArray = ints;
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.intArray.length);

      for (int i = 0; i < this.intArray.length; i++) {
         output.writeInt(this.intArray[i]);
      }
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(192L);
      int i = input.readInt();
      sizeTracker.read(32 * i);
      this.intArray = new int[i];

      for (int j = 0; j < i; j++) {
         this.intArray[j] = input.readInt();
      }
   }

   @Override
   public byte getId() {
      return 11;
   }

   @Override
   public String toString() {
      String string = "[";

      for (int i : this.intArray) {
         string = string + i + ",";
      }

      return string + "]";
   }

   @Override
   public NBTBase copy() {
      int[] ints = new int[this.intArray.length];
      System.arraycopy(this.intArray, 0, ints, 0, this.intArray.length);
      return new NBTTagIntArray(ints);
   }

   @Override
   public boolean equals(Object object) {
      return super.equals(object) ? Arrays.equals(this.intArray, ((NBTTagIntArray)object).intArray) : false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.intArray);
   }

   public int[] getIntArray() {
      return this.intArray;
   }
}
