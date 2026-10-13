package net.minecraft.nbt;

import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagList extends NBTBase {
   private static final Logger LOGGER = LogManager.getLogger();
   private List<NBTBase> tagList = Lists.newArrayList();
   private byte tagType = 0;
   private static final long c = 122194312244805L;

   @Override
   public void write(DataOutput var1) throws IOException {
      if (!this.tagList.isEmpty()) {
         this.tagType = this.tagList.get(0).getId();
      } else {
         this.tagType = 0;
      }

      var1.writeByte(this.tagType);
      var1.writeInt(this.tagList.size());

      for (int var5 = 0; var5 < this.tagList.size(); var5++) {
         this.tagList.get(var5).write(var1);
      }
   }

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(296L);
      if (var2 > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      }

      this.tagType = var1.readByte();
      int var8 = var1.readInt();
      if (this.tagType == 0 && var8 > 0) {
         throw new RuntimeException("Missing type on ListTag");
      }

      var3.read(32L * var8);
      this.tagList = Lists.newArrayListWithCapacity(var8);

      for (int var9 = 0; var9 < var8; var9++) {
         NBTBase var10 = NBTBase.createNewByType(this.tagType);
         var10.read(var1, var2 + 1, var3);
         this.tagList.add(var10);
      }
   }

   @Override
   public byte getId() {
      return 9;
   }

   @Override
   public String toString() {
      StringBuilder var4 = new StringBuilder("[");

      for (int var5 = 0; var5 < this.tagList.size(); var5++) {
         if (var5 != 0) {
            var4.append(',');
         }

         var4.append(var5).append(':').append(this.tagList.get(var5));
      }

      return var4.append(']').toString();
   }

   public void appendTag(NBTBase var1) {
      if (var1.getId() == 0) {
         LOGGER.warn("Invalid TagEnd added to ListTag");
      } else {
         if (this.tagType == 0) {
            this.tagType = var1.getId();
         } else if (this.tagType != var1.getId()) {
            LOGGER.warn("Adding mismatching tag types to tag list");
            return;
         }

         this.tagList.add(var1);
      }
   }

   public void set(int var1, NBTBase var2) {
      if (var2.getId() == 0) {
         LOGGER.warn("Invalid TagEnd added to ListTag");
      } else if (var1 >= 0 && var1 < this.tagList.size()) {
         if (this.tagType == 0) {
            this.tagType = var2.getId();
         } else if (this.tagType != var2.getId()) {
            LOGGER.warn("Adding mismatching tag types to tag list");
            return;
         }

         this.tagList.set(var1, var2);
      } else {
         LOGGER.warn("index out of bounds to set tag in tag list");
      }
   }

   public NBTBase removeTag(int var1) {
      return this.tagList.remove(var1);
   }

   @Override
   public boolean hasNoTags() {
      return this.tagList.isEmpty();
   }

   public NBTTagCompound getCompoundTagAt(int var1) {
      if (var1 >= 0 && var1 < this.tagList.size()) {
         NBTBase var5 = this.tagList.get(var1);
         return var5.getId() == 10 ? (NBTTagCompound)var5 : new NBTTagCompound();
      } else {
         return new NBTTagCompound();
      }
   }

   public int[] getIntArrayAt(int var1) {
      if (var1 >= 0 && var1 < this.tagList.size()) {
         NBTBase var5 = this.tagList.get(var1);
         return var5.getId() == 11 ? ((NBTTagIntArray)var5).getIntArray() : new int[0];
      } else {
         return new int[0];
      }
   }

   public double getDoubleAt(int var1) {
      if (var1 >= 0 && var1 < this.tagList.size()) {
         NBTBase var5 = this.tagList.get(var1);
         return var5.getId() == 6 ? ((NBTTagDouble)var5).getDouble() : 0.0;
      } else {
         return 0.0;
      }
   }

   public float getFloatAt(int var1) {
      if (var1 >= 0 && var1 < this.tagList.size()) {
         NBTBase var5 = this.tagList.get(var1);
         return var5.getId() == 5 ? ((NBTTagFloat)var5).getFloat() : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public String getStringTagAt(int var1) {
      if (var1 >= 0 && var1 < this.tagList.size()) {
         NBTBase var5 = this.tagList.get(var1);
         return var5.getId() == 8 ? var5.getString() : var5.toString();
      } else {
         return "";
      }
   }

   public NBTBase get(int var1) {
      return var1 >= 0 && var1 < this.tagList.size() ? this.tagList.get(var1) : new NBTTagEnd();
   }

   public int tagCount() {
      return this.tagList.size();
   }

   @Override
   public NBTBase copy() {
      NBTTagList var4 = new NBTTagList();
      var4.tagType = this.tagType;

      for (NBTBase var6 : this.tagList) {
         NBTBase var7 = var6.copy();
         var4.tagList.add(var7);
      }

      return var4;
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagList var5 = (NBTTagList)var1;
         if (this.tagType == var5.tagType) {
            return this.tagList.equals(var5.tagList);
         }
      }

      return false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.tagList.hashCode();
   }

   public int getTagType() {
      return this.tagType;
   }
}
