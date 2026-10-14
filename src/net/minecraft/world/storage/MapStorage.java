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

   public MapStorage(ISaveHandler saveHandlerIn) {
      this.saveHandler = saveHandlerIn;
      this.loadIdCounts();
   }

   public WorldSavedData loadData(Class<? extends WorldSavedData> clazz, String dataIdentifier) {
      WorldSavedData worldSavedData = this.loadedDataMap.get(dataIdentifier);
      if (worldSavedData != null) {
         return worldSavedData;
      }

      if (this.saveHandler != null) {
         try {
            File file = this.saveHandler.getMapFileFromName(dataIdentifier);
            if (file != null && file.exists()) {
               try {
                  worldSavedData = (WorldSavedData)clazz.getConstructor(String.class).newInstance(dataIdentifier);
               } catch (Exception exception) {
                  throw new RuntimeException("Failed to instantiate " + clazz.toString(), exception);
               }

               FileInputStream fileInputStream = new FileInputStream(file);
               NBTTagCompound nbtTagCompound = CompressedStreamTools.readCompressed(fileInputStream);
               fileInputStream.close();
               worldSavedData.readFromNBT(nbtTagCompound.getCompoundTag("data"));
            }
         } catch (Exception exception2) {
            exception2.printStackTrace();
         }
      }

      if (worldSavedData != null) {
         this.loadedDataMap.put(dataIdentifier, worldSavedData);
         this.loadedDataList.add(worldSavedData);
      }

      return worldSavedData;
   }

   public void setData(String dataIdentifier, WorldSavedData data) {
      if (this.loadedDataMap.containsKey(dataIdentifier)) {
         this.loadedDataList.remove(this.loadedDataMap.remove(dataIdentifier));
      }

      this.loadedDataMap.put(dataIdentifier, data);
      this.loadedDataList.add(data);
   }

   public void saveAllData() {
      for (int i = 0; i < this.loadedDataList.size(); i++) {
         WorldSavedData worldSavedData = this.loadedDataList.get(i);
         if (worldSavedData.isDirty()) {
            this.saveData(worldSavedData);
            worldSavedData.setDirty(false);
         }
      }
   }

   private void saveData(WorldSavedData worldSavedData) {
      if (this.saveHandler != null) {
         try {
            File file = this.saveHandler.getMapFileFromName(worldSavedData.mapName);
            if (file != null) {
               NBTTagCompound nbtTagCompound = new NBTTagCompound();
               worldSavedData.writeToNBT(nbtTagCompound);
               NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
               nbtTagCompound2.setTag("data", nbtTagCompound);
               FileOutputStream fileOutputStream = new FileOutputStream(file);
               CompressedStreamTools.writeCompressed(nbtTagCompound2, fileOutputStream);
               fileOutputStream.close();
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   private void loadIdCounts() {
      try {
         this.idCounts.clear();
         if (this.saveHandler == null) {
            return;
         }

         File file = this.saveHandler.getMapFileFromName("idcounts");
         if (file != null && file.exists()) {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            NBTTagCompound nbtTagCompound = CompressedStreamTools.read(dataInputStream);
            dataInputStream.close();

            for (String string : nbtTagCompound.getKeySet()) {
               NBTBase nbtBase = nbtTagCompound.getTag(string);
               if (nbtBase instanceof NBTTagShort) {
                  NBTTagShort nbtTagShort = (NBTTagShort)nbtBase;
                  short s = nbtTagShort.getShort();
                  this.idCounts.put(string, s);
               }
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public int getUniqueDataId(String key) {
      Short short2 = this.idCounts.get(key);
      if (short2 == null) {
         short2 = (short)0;
      } else {
         short2 = (short)(short2 + 1);
      }

      this.idCounts.put(key, short2);
      if (this.saveHandler == null) {
         return short2;
      }

      try {
         File file = this.saveHandler.getMapFileFromName("idcounts");
         if (file != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();

            for (String string : this.idCounts.keySet()) {
               short s = this.idCounts.get(string);
               nbtTagCompound.setShort(string, s);
            }

            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
            CompressedStreamTools.write(nbtTagCompound, dataOutputStream);
            dataOutputStream.close();
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      return short2;
   }
}
