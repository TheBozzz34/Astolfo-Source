package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.world.WorldSavedData;

public class MapStorage {
   private ISaveHandler saveHandler;
   public Map<String, WorldSavedData> loadedDataMap = Maps.newHashMap();
   private List<WorldSavedData> loadedDataList = Lists.newArrayList();
   private Map<String, Short> idCounts = Maps.newHashMap();
   private static final long a = 23655112726176L;

   public MapStorage(ISaveHandler var1) {
      this.saveHandler = var1;
      this.loadIdCounts();
   }

   public WorldSavedData loadData(Class<? extends WorldSavedData> var1, String var2) {
      WorldSavedData var6 = this.loadedDataMap.get(var2);
      if (var6 != null) {
         return var6;
      }

      if (this.saveHandler != null) {
         try {
            File var7 = this.saveHandler.getMapFileFromName(var2);
            if (var7 != null && var7.exists()) {
               try {
                  var6 = (WorldSavedData)var1.getConstructor(String.class).newInstance(var2);
               } catch (Exception var10) {
                  throw new RuntimeException("Failed to instantiate " + var1.toString(), var10);
               }

               FileInputStream var8 = new FileInputStream(var7);
               NBTTagCompound var9 = CompressedStreamTools.readCompressed(var8);
               var8.close();
               var6.readFromNBT(var9.getCompoundTag("data"));
            }
         } catch (Exception var11) {
            var11.printStackTrace();
         }
      }

      if (var6 != null) {
         this.loadedDataMap.put(var2, var6);
         this.loadedDataList.add(var6);
      }

      return var6;
   }

   public void setData(String var1, WorldSavedData var2) {
      if (this.loadedDataMap.containsKey(var1)) {
         this.loadedDataList.remove(this.loadedDataMap.remove(var1));
      }

      this.loadedDataMap.put(var1, var2);
      this.loadedDataList.add(var2);
   }

   public void saveAllData() {
      for (int var4 = 0; var4 < this.loadedDataList.size(); var4++) {
         WorldSavedData var5 = this.loadedDataList.get(var4);
         if (var5.isDirty()) {
            this.saveData(var5);
            var5.setDirty(false);
         }
      }
   }

   private void saveData(WorldSavedData var1) {
      if (this.saveHandler != null) {
         try {
            File var5 = this.saveHandler.getMapFileFromName(var1.mapName);
            if (var5 != null) {
               NBTTagCompound var6 = new NBTTagCompound();
               var1.writeToNBT(var6);
               NBTTagCompound var7 = new NBTTagCompound();
               var7.setTag("data", var6);
               FileOutputStream var8 = new FileOutputStream(var5);
               CompressedStreamTools.writeCompressed(var7, var8);
               var8.close();
            }
         } catch (Exception var9) {
            var9.printStackTrace();
         }
      }
   }

   private void loadIdCounts() {
      try {
         this.idCounts.clear();
         if (this.saveHandler == null) {
            return;
         }

         File var4 = this.saveHandler.getMapFileFromName("idcounts");
         if (var4 != null && var4.exists()) {
            DataInputStream var5 = new DataInputStream(new FileInputStream(var4));
            NBTTagCompound var6 = CompressedStreamTools.read(var5);
            var5.close();

            for (String var8 : var6.getKeySet()) {
               NBTBase var9 = var6.getTag(var8);
               if (var9 instanceof NBTTagShort) {
                  NBTTagShort var10 = (NBTTagShort)var9;
                  short var11 = var10.getShort();
                  this.idCounts.put(var8, var11);
               }
            }
         }
      } catch (Exception var12) {
         var12.printStackTrace();
      }
   }

   public int getUniqueDataId(String var1) {
      Short var5 = this.idCounts.get(var1);
      if (var5 == null) {
         var5 = (short)0;
      } else {
         var5 = (short)(var5 + 1);
      }

      this.idCounts.put(var1, var5);
      if (this.saveHandler == null) {
         return var5;
      }

      try {
         File var6 = this.saveHandler.getMapFileFromName("idcounts");
         if (var6 != null) {
            NBTTagCompound var7 = new NBTTagCompound();

            for (String var9 : this.idCounts.keySet()) {
               short var10 = this.idCounts.get(var9);
               var7.setShort(var9, var10);
            }

            DataOutputStream var13 = new DataOutputStream(new FileOutputStream(var6));
            CompressedStreamTools.write(var7, var13);
            var13.close();
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      return var5;
   }
}
