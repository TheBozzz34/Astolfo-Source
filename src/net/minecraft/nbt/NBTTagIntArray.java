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

   public NBTTagIntArray(int[] var1) {
      this.intArray = var1;
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeInt(this.intArray.length);

      for (int var5 = 0; var5 < this.intArray.length; var5++) {
         var1.writeInt(this.intArray[var5]);
      }
   }

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(192L);
      int var7 = var1.readInt();
      var3.read(32 * var7);
      this.intArray = new int[var7];

      for (int var8 = 0; var8 < var7; var8++) {
         this.intArray[var8] = var1.readInt();
      }
   }

   @Override
   public byte getId() {
      return 11;
   }

   @Override
   public String toString() {
      String var4 = "[";

      for (int var8 : this.intArray) {
         var4 = var4 + var8 + ",";
      }

      return var4 + "]";
   }

   @Override
   public NBTBase copy() {
      int[] var1 = new int[this.intArray.length];
      System.arraycopy(this.intArray, 0, var1, 0, this.intArray.length);
      return new NBTTagIntArray(var1);
   }

   @Override
   public boolean equals(Object var1) {
      return super.equals(var1) ? Arrays.equals(this.intArray, ((NBTTagIntArray)var1).intArray) : false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ Arrays.hashCode(this.intArray);
   }

   public int[] getIntArray() {
      return this.intArray;
   }
}
