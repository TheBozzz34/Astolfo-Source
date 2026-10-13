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
   private static final long a = 73900974549985L;

   @Override
   public void write(DataOutput var1) throws IOException {
      for (String var6 : this.tagMap.keySet()) {
         NBTBase var7 = this.tagMap.get(var6);
         writeEntry(var6, var7, var1);
      }

      var1.writeByte(0);
   }

   @Override
   public void read(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.read(384L);
      if (var2 > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      }

      this.tagMap.clear();

      byte var7;
      while ((var7 = readType(var1, var3)) != 0) {
         String var8 = readKey(var1, var3);
         var3.read(224 + 16 * var8.length());
         NBTBase var9 = readNBT(var7, var8, var1, var2 + 1, var3);
         if (this.tagMap.put(var8, var9) != null) {
            var3.read(288L);
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

   public void setTag(String var1, NBTBase var2) {
      this.tagMap.put(var1, var2);
   }

   public void setByte(String var1, byte var2) {
      this.tagMap.put(var1, new NBTTagByte(var2));
   }

   public void setShort(String var1, short var2) {
      this.tagMap.put(var1, new NBTTagShort(var2));
   }

   public void setInteger(String var1, int var2) {
      this.tagMap.put(var1, new NBTTagInt(var2));
   }

   public void setLong(String var1, long var2) {
      this.tagMap.put(var1, new NBTTagLong(var2));
   }

   public void setFloat(String var1, float var2) {
      this.tagMap.put(var1, new NBTTagFloat(var2));
   }

   public void setDouble(String var1, double var2) {
      this.tagMap.put(var1, new NBTTagDouble(var2));
   }

   public void setString(String var1, String var2) {
      this.tagMap.put(var1, new NBTTagString(var2));
   }

   public void setByteArray(String var1, byte[] var2) {
      this.tagMap.put(var1, new NBTTagByteArray(var2));
   }

   public void setIntArray(String var1, int[] var2) {
      this.tagMap.put(var1, new NBTTagIntArray(var2));
   }

   public void setBoolean(String var1, boolean var2) {
      this.setByte(var1, (byte)(var2 ? 1 : 0));
   }

   public NBTBase getTag(String var1) {
      return this.tagMap.get(var1);
   }

   public byte getTagId(String var1) {
      NBTBase var5 = this.tagMap.get(var1);
      return var5 != null ? var5.getId() : 0;
   }

   public boolean hasKey(String var1) {
      return this.tagMap.containsKey(var1);
   }

   public boolean hasKey(String var1, int var2) {
      byte var6 = this.getTagId(var1);
      if (var6 == var2) {
         return true;
      } else {
         return var2 != 99 ? false : var6 == 1 || var6 == 2 || var6 == 3 || var6 == 4 || var6 == 5 || var6 == 6;
      }
   }

   public byte getByte(String var1) {
      try {
         return !this.hasKey(var1, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(var1)).getByte();
      } catch (ClassCastException var6) {
         return 0;
      }
   }

   public short getShort(String var1) {
      try {
         return !this.hasKey(var1, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(var1)).getShort();
      } catch (ClassCastException var6) {
         return 0;
      }
   }

   public int getInteger(String var1) {
      try {
         return !this.hasKey(var1, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(var1)).getInt();
      } catch (ClassCastException var6) {
         return 0;
      }
   }

   public long getLong(String var1) {
      try {
         return !this.hasKey(var1, 99) ? 0L : ((NBTBase.NBTPrimitive)this.tagMap.get(var1)).getLong();
      } catch (ClassCastException var6) {
         return 0L;
      }
   }

   public float getFloat(String var1) {
      try {
         return !this.hasKey(var1, 99) ? 0.0F : ((NBTBase.NBTPrimitive)this.tagMap.get(var1)).getFloat();
      } catch (ClassCastException var6) {
         return 0.0F;
      }
   }

   public double getDouble(String var1) {
      try {
         return !this.hasKey(var1, 99) ? 0.0 : ((NBTBase.NBTPrimitive)this.tagMap.get(var1)).getDouble();
      } catch (ClassCastException var6) {
         return 0.0;
      }
   }

   public String getString(String var1) {
      try {
         return !this.hasKey(var1, 8) ? "" : this.tagMap.get(var1).getString();
      } catch (ClassCastException var6) {
         return "";
      }
   }

   public byte[] getByteArray(String var1) {
      try {
         return !this.hasKey(var1, 7) ? new byte[0] : ((NBTTagByteArray)this.tagMap.get(var1)).getByteArray();
      } catch (ClassCastException var6) {
         throw new ReportedException(this.createCrashReport(var1, 7, var6));
      }
   }

   public int[] getIntArray(String var1) {
      try {
         return !this.hasKey(var1, 11) ? new int[0] : ((NBTTagIntArray)this.tagMap.get(var1)).getIntArray();
      } catch (ClassCastException var6) {
         throw new ReportedException(this.createCrashReport(var1, 11, var6));
      }
   }

   public NBTTagCompound getCompoundTag(String var1) {
      try {
         return !this.hasKey(var1, 10) ? new NBTTagCompound() : (NBTTagCompound)this.tagMap.get(var1);
      } catch (ClassCastException var6) {
         throw new ReportedException(this.createCrashReport(var1, 10, var6));
      }
   }

   public NBTTagList getTagList(String var1, int var2) {
      try {
         if (this.getTagId(var1) != 9) {
            return new NBTTagList();
         }

         NBTTagList var6 = (NBTTagList)this.tagMap.get(var1);
         return var6.tagCount() > 0 && var6.getTagType() != var2 ? new NBTTagList() : var6;
      } catch (ClassCastException var7) {
         throw new ReportedException(this.createCrashReport(var1, 9, var7));
      }
   }

   public boolean getBoolean(String var1) {
      return this.getByte(var1) != 0;
   }

   public void removeTag(String var1) {
      this.tagMap.remove(var1);
   }

   @Override
   public String toString() {
      StringBuilder var4 = new StringBuilder("{");

      for (Entry var6 : this.tagMap.entrySet()) {
         if (var4.length() != 1) {
            var4.append(',');
         }

         var4.append((String)var6.getKey()).append(':').append(var6.getValue());
      }

      return var4.append('}').toString();
   }

   @Override
   public boolean hasNoTags() {
      return this.tagMap.isEmpty();
   }

   private CrashReport createCrashReport(final String var1, final int var2, ClassCastException var3) {
      CrashReport var6 = CrashReport.makeCrashReport(var3, "Reading NBT data");
      CrashReportCategory var7 = var6.makeCategoryDepth("Corrupt NBT tag", 1);
      var7.addCrashSectionCallable("Tag type found", new Callable<String>() {
         public String call() throws Exception {
            return NBTBase.NBT_TYPES[NBTTagCompound.this.tagMap.get(var1).getId()];
         }
      });
      var7.addCrashSectionCallable("Tag type expected", new Callable<String>() {
         public String call() throws Exception {
            return NBTBase.NBT_TYPES[var2];
         }
      });
      var7.addCrashSection("Tag name", var1);
      return var6;
   }

   @Override
   public NBTBase copy() {
      NBTTagCompound var3 = new NBTTagCompound();

      for (String var5 : this.tagMap.keySet()) {
         var3.setTag(var5, this.tagMap.get(var5).copy());
      }

      return var3;
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagCompound var5 = (NBTTagCompound)var1;
         return this.tagMap.entrySet().equals(var5.tagMap.entrySet());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.tagMap.hashCode();
   }

   private static void writeEntry(String var0, NBTBase var1, DataOutput var2) throws IOException {
      var2.writeByte(var1.getId());
      if (var1.getId() != 0) {
         var2.writeUTF(var0);
         var1.write(var2);
      }
   }

   private static byte readType(DataInput var0, NBTSizeTracker var1) throws IOException {
      return var0.readByte();
   }

   private static String readKey(DataInput var0, NBTSizeTracker var1) throws IOException {
      return var0.readUTF();
   }

   public static NBTBase readNBT(byte var0, String var1, DataInput var2, int var3, NBTSizeTracker var4) throws IOException {
      NBTBase var7 = NBTBase.createNewByType(var0);

      try {
         var7.read(var2, var3, var4);
         return var7;
      } catch (IOException var11) {
         CrashReport var9 = CrashReport.makeCrashReport(var11, "Loading NBT data");
         CrashReportCategory var10 = var9.makeCategory("NBT Tag");
         var10.addCrashSection("Tag name", var1);
         var10.addCrashSection("Tag type", var0);
         throw new ReportedException(var9);
      }
   }

   public void merge(NBTTagCompound var1) {
      for (String var6 : var1.tagMap.keySet()) {
         NBTBase var7 = var1.tagMap.get(var6);
         if (var7.getId() == 10) {
            if (this.hasKey(var6, 10)) {
               NBTTagCompound var8 = this.getCompoundTag(var6);
               var8.merge((NBTTagCompound)var7);
            } else {
               this.setTag(var6, var7.copy());
            }
         } else {
            this.setTag(var6, var7.copy());
         }
      }
   }
}
