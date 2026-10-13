package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagEnd extends NBTBase {
   private static final long a = 103789956510460L;

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(64L);
   }

   @Override
   public void write(DataOutput var1) throws IOException {
   }

   @Override
   public byte getId() {
      return 0;
   }

   @Override
   public String toString() {
      return "END";
   }

   @Override
   public NBTBase copy() {
      return new NBTTagEnd();
   }
}
