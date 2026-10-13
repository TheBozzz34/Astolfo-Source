package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagString extends NBTBase {
   private String data;
   private static final long a = 102521581367980L;

   public NBTTagString() {
      this.data = "";
   }

   public NBTTagString(String var1) {
      this.data = var1;
      if (var1 == null) {
         throw new IllegalArgumentException("Empty string not allowed");
      }
   }

   @Override
   public void write(DataOutput var1) throws IOException {
      var1.writeUTF(this.data);
   }

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(288L);
      this.data = var1.readUTF();
      var3.read(16 * this.data.length());
   }

   @Override
   public byte getId() {
      return 8;
   }

   @Override
   public String toString() {
      return "\"" + this.data.replace("\"", "\\\"") + "\"";
   }

   @Override
   public NBTBase copy() {
      return new NBTTagString(this.data);
   }

   @Override
   public boolean hasNoTags() {
      return this.data.isEmpty();
   }

   @Override
   public boolean equals(Object var1) {
      if (!super.equals(var1)) {
         return false;
      }

      NBTTagString var5 = (NBTTagString)var1;
      return this.data == null && var5.data == null || this.data != null && this.data.equals(var5.data);
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.data.hashCode();
   }

   @Override
   public String getString() {
      return this.data;
   }
}
