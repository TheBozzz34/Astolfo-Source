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

   @Override
   public void write(DataOutput output) throws IOException {
      if (!this.tagList.isEmpty()) {
         this.tagType = this.tagList.get(0).getId();
      } else {
         this.tagType = 0;
      }

      output.writeByte(this.tagType);
      output.writeInt(this.tagList.size());

      for (int i = 0; i < this.tagList.size(); i++) {
         this.tagList.get(i).write(output);
      }
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(296L);
      if (depth > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      }

      this.tagType = input.readByte();
      int i = input.readInt();
      if (this.tagType == 0 && i > 0) {
         throw new RuntimeException("Missing type on ListTag");
      }

      sizeTracker.read(32L * i);
      this.tagList = Lists.newArrayListWithCapacity(i);

      for (int j = 0; j < i; j++) {
         NBTBase nbtBase = NBTBase.createNewByType(this.tagType);
         nbtBase.read(input, depth + 1, sizeTracker);
         this.tagList.add(nbtBase);
      }
   }

   @Override
   public byte getId() {
      return 9;
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder("[");

      for (int i = 0; i < this.tagList.size(); i++) {
         if (i != 0) {
            stringBuilder.append(',');
         }

         stringBuilder.append(i).append(':').append(this.tagList.get(i));
      }

      return stringBuilder.append(']').toString();
   }

   public void appendTag(NBTBase nbt) {
      if (nbt.getId() == 0) {
         LOGGER.warn("Invalid TagEnd added to ListTag");
      } else {
         if (this.tagType == 0) {
            this.tagType = nbt.getId();
         } else if (this.tagType != nbt.getId()) {
            LOGGER.warn("Adding mismatching tag types to tag list");
            return;
         }

         this.tagList.add(nbt);
      }
   }

   public void set(int idx, NBTBase nbt) {
      if (nbt.getId() == 0) {
         LOGGER.warn("Invalid TagEnd added to ListTag");
      } else if (idx >= 0 && idx < this.tagList.size()) {
         if (this.tagType == 0) {
            this.tagType = nbt.getId();
         } else if (this.tagType != nbt.getId()) {
            LOGGER.warn("Adding mismatching tag types to tag list");
            return;
         }

         this.tagList.set(idx, nbt);
      } else {
         LOGGER.warn("index out of bounds to set tag in tag list");
      }
   }

   public NBTBase removeTag(int i) {
      return this.tagList.remove(i);
   }

   @Override
   public boolean hasNoTags() {
      return this.tagList.isEmpty();
   }

   public NBTTagCompound getCompoundTagAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nbtBase = this.tagList.get(i);
         return nbtBase.getId() == 10 ? (NBTTagCompound)nbtBase : new NBTTagCompound();
      } else {
         return new NBTTagCompound();
      }
   }

   public int[] getIntArrayAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nbtBase = this.tagList.get(i);
         return nbtBase.getId() == 11 ? ((NBTTagIntArray)nbtBase).getIntArray() : new int[0];
      } else {
         return new int[0];
      }
   }

   public double getDoubleAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nbtBase = this.tagList.get(i);
         return nbtBase.getId() == 6 ? ((NBTTagDouble)nbtBase).getDouble() : 0.0;
      } else {
         return 0.0;
      }
   }

   public float getFloatAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nbtBase = this.tagList.get(i);
         return nbtBase.getId() == 5 ? ((NBTTagFloat)nbtBase).getFloat() : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public String getStringTagAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nbtBase = this.tagList.get(i);
         return nbtBase.getId() == 8 ? nbtBase.getString() : nbtBase.toString();
      } else {
         return "";
      }
   }

   public NBTBase get(int idx) {
      return idx >= 0 && idx < this.tagList.size() ? this.tagList.get(idx) : new NBTTagEnd();
   }

   public int tagCount() {
      return this.tagList.size();
   }

   @Override
   public NBTBase copy() {
      NBTTagList nbtTagList = new NBTTagList();
      nbtTagList.tagType = this.tagType;

      for (NBTBase nbtBase : this.tagList) {
         NBTBase nbtBase2 = nbtBase.copy();
         nbtTagList.tagList.add(nbtBase2);
      }

      return nbtTagList;
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NBTTagList nbtTagList = (NBTTagList)object;
         if (this.tagType == nbtTagList.tagType) {
            return this.tagList.equals(nbtTagList.tagList);
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
