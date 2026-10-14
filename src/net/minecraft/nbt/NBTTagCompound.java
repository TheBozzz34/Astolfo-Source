package net.minecraft.nbt;

import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class NBTTagCompound extends NBTBase {
   private Map<String, NBTBase> tagMap = Maps.newHashMap();

   @Override
   public void write(DataOutput output) throws IOException {
      for (String string : this.tagMap.keySet()) {
         NBTBase nbtBase = this.tagMap.get(string);
         writeEntry(string, nbtBase, output);
      }

      output.writeByte(0);
   }

   @Override
   public void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(384L);
      if (depth > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      }

      this.tagMap.clear();

      byte b;
      while ((b = readType(input, sizeTracker)) != 0) {
         String string = readKey(input, sizeTracker);
         sizeTracker.read(224 + 16 * string.length());
         NBTBase nbtBase = readNBT(b, string, input, depth + 1, sizeTracker);
         if (this.tagMap.put(string, nbtBase) != null) {
            sizeTracker.read(288L);
         }
      }
   }

   public Set<String> getKeySet() {
      return this.tagMap.keySet();
   }

   @Override
   public byte getId() {
      return 10;
   }

   public void setTag(String key, NBTBase value) {
      this.tagMap.put(key, value);
   }

   public void setByte(String key, byte value) {
      this.tagMap.put(key, new NBTTagByte(value));
   }

   public void setShort(String key, short value) {
      this.tagMap.put(key, new NBTTagShort(value));
   }

   public void setInteger(String key, int value) {
      this.tagMap.put(key, new NBTTagInt(value));
   }

   public void setLong(String key, long value) {
      this.tagMap.put(key, new NBTTagLong(value));
   }

   public void setFloat(String key, float value) {
      this.tagMap.put(key, new NBTTagFloat(value));
   }

   public void setDouble(String key, double value) {
      this.tagMap.put(key, new NBTTagDouble(value));
   }

   public void setString(String key, String value) {
      this.tagMap.put(key, new NBTTagString(value));
   }

   public void setByteArray(String key, byte[] value) {
      this.tagMap.put(key, new NBTTagByteArray(value));
   }

   public void setIntArray(String key, int[] value) {
      this.tagMap.put(key, new NBTTagIntArray(value));
   }

   public void setBoolean(String key, boolean value) {
      this.setByte(key, (byte)(value ? 1 : 0));
   }

   public NBTBase getTag(String key) {
      return this.tagMap.get(key);
   }

   public byte getTagId(String key) {
      NBTBase nbtBase = this.tagMap.get(key);
      return nbtBase != null ? nbtBase.getId() : 0;
   }

   public boolean hasKey(String key) {
      return this.tagMap.containsKey(key);
   }

   public boolean hasKey(String key, int typeIn) {
      byte b = this.getTagId(key);
      if (b == typeIn) {
         return true;
      } else {
         return typeIn != 99 ? false : b == 1 || b == 2 || b == 3 || b == 4 || b == 5 || b == 6;
      }
   }

   public byte getByte(String key) {
      try {
         return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getByte();
      } catch (ClassCastException classCastException) {
         return 0;
      }
   }

   public short getShort(String key) {
      try {
         return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getShort();
      } catch (ClassCastException classCastException) {
         return 0;
      }
   }

   public int getInteger(String key) {
      try {
         return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getInt();
      } catch (ClassCastException classCastException) {
         return 0;
      }
   }

   public long getLong(String key) {
      try {
         return !this.hasKey(key, 99) ? 0L : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getLong();
      } catch (ClassCastException classCastException) {
         return 0L;
      }
   }

   public float getFloat(String key) {
      try {
         return !this.hasKey(key, 99) ? 0.0F : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getFloat();
      } catch (ClassCastException classCastException) {
         return 0.0F;
      }
   }

   public double getDouble(String key) {
      try {
         return !this.hasKey(key, 99) ? 0.0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getDouble();
      } catch (ClassCastException classCastException) {
         return 0.0;
      }
   }

   public String getString(String key) {
      try {
         return !this.hasKey(key, 8) ? "" : this.tagMap.get(key).getString();
      } catch (ClassCastException classCastException) {
         return "";
      }
   }

   public byte[] getByteArray(String key) {
      try {
         return !this.hasKey(key, 7) ? new byte[0] : ((NBTTagByteArray)this.tagMap.get(key)).getByteArray();
      } catch (ClassCastException classCastException) {
         throw new ReportedException(this.createCrashReport(key, 7, classCastException));
      }
   }

   public int[] getIntArray(String key) {
      try {
         return !this.hasKey(key, 11) ? new int[0] : ((NBTTagIntArray)this.tagMap.get(key)).getIntArray();
      } catch (ClassCastException classCastException) {
         throw new ReportedException(this.createCrashReport(key, 11, classCastException));
      }
   }

   public NBTTagCompound getCompoundTag(String key) {
      try {
         return !this.hasKey(key, 10) ? new NBTTagCompound() : (NBTTagCompound)this.tagMap.get(key);
      } catch (ClassCastException classCastException) {
         throw new ReportedException(this.createCrashReport(key, 10, classCastException));
      }
   }

   public NBTTagList getTagList(String key, int typeIn) {
      try {
         if (this.getTagId(key) != 9) {
            return new NBTTagList();
         }

         NBTTagList nbtTagList = (NBTTagList)this.tagMap.get(key);
         return nbtTagList.tagCount() > 0 && nbtTagList.getTagType() != typeIn ? new NBTTagList() : nbtTagList;
      } catch (ClassCastException classCastException) {
         throw new ReportedException(this.createCrashReport(key, 9, classCastException));
      }
   }

   public boolean getBoolean(String key) {
      return this.getByte(key) != 0;
   }

   public void removeTag(String key) {
      this.tagMap.remove(key);
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder("{");

      for (Entry entry : this.tagMap.entrySet()) {
         if (stringBuilder.length() != 1) {
            stringBuilder.append(',');
         }

         stringBuilder.append((String)entry.getKey()).append(':').append(entry.getValue());
      }

      return stringBuilder.append('}').toString();
   }

   @Override
   public boolean hasNoTags() {
      return this.tagMap.isEmpty();
   }

   private CrashReport createCrashReport(final String key, final int expectedType, ClassCastException ex) {
      CrashReport crashReport = CrashReport.makeCrashReport(ex, "Reading NBT data");
      CrashReportCategory crashReportCategory = crashReport.makeCategoryDepth("Corrupt NBT tag", 1);
      crashReportCategory.addCrashSectionCallable("Tag type found", new Callable<String>() {
         public String call() throws Exception {
            return NBTBase.NBT_TYPES[NBTTagCompound.this.tagMap.get(key).getId()];
         }
      });
      crashReportCategory.addCrashSectionCallable("Tag type expected", new Callable<String>() {
         public String call() throws Exception {
            return NBTBase.NBT_TYPES[expectedType];
         }
      });
      crashReportCategory.addCrashSection("Tag name", key);
      return crashReport;
   }

   @Override
   public NBTBase copy() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();

      for (String string : this.tagMap.keySet()) {
         nbtTagCompound.setTag(string, this.tagMap.get(string).copy());
      }

      return nbtTagCompound;
   }

   @Override
   public boolean equals(Object object) {
      if (super.equals(object)) {
         NBTTagCompound nbtTagCompound = (NBTTagCompound)object;
         return this.tagMap.entrySet().equals(nbtTagCompound.tagMap.entrySet());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.tagMap.hashCode();
   }

   private static void writeEntry(String name, NBTBase data, DataOutput output) throws IOException {
      output.writeByte(data.getId());
      if (data.getId() != 0) {
         output.writeUTF(name);
         data.write(output);
      }
   }

   private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
      return input.readByte();
   }

   private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
      return input.readUTF();
   }

   public static NBTBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      NBTBase nbtBase = NBTBase.createNewByType(id);

      try {
         nbtBase.read(input, depth, sizeTracker);
         return nbtBase;
      } catch (IOException ioException) {
         CrashReport crashReport = CrashReport.makeCrashReport(ioException, "Loading NBT data");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("NBT Tag");
         crashReportCategory.addCrashSection("Tag name", key);
         crashReportCategory.addCrashSection("Tag type", id);
         throw new ReportedException(crashReport);
      }
   }

   public void merge(NBTTagCompound other) {
      for (String string : other.tagMap.keySet()) {
         NBTBase nbtBase = other.tagMap.get(string);
         if (nbtBase.getId() == 10) {
            if (this.hasKey(string, 10)) {
               NBTTagCompound nbtTagCompound = this.getCompoundTag(string);
               nbtTagCompound.merge((NBTTagCompound)nbtBase);
            } else {
               this.setTag(string, nbtBase.copy());
            }
         } else {
            this.setTag(string, nbtBase.copy());
         }
      }
   }
}
