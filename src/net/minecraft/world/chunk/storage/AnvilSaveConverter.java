package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilSaveConverter extends SaveFormatOld {
   private static final Logger logger = LogManager.getLogger();

   public AnvilSaveConverter(File savesDirectoryIn) {
      super(savesDirectoryIn);
   }

   @Override
   public String getName() {
      return "Anvil";
   }

   @Override
   public List<SaveFormatComparator> getSaveList() throws AnvilConverterException {
      if (this.savesDirectory != null && this.savesDirectory.exists() && this.savesDirectory.isDirectory()) {
         ArrayList arrayList = Lists.newArrayList();
         File[] files = this.savesDirectory.listFiles();

         for (File file : files) {
            if (file.isDirectory()) {
               String string = file.getName();
               WorldInfo worldInfo = this.getWorldInfo(string);
               if (worldInfo != null && (worldInfo.getSaveVersion() == 19132 || worldInfo.getSaveVersion() == 19133)) {
                  boolean bl = worldInfo.getSaveVersion() != this.getSaveVersion();
                  String string2 = worldInfo.getWorldName();
                  if (StringUtils.isEmpty(string2)) {
                     string2 = string;
                  }

                  long l = 0L;
                  arrayList.add(
                     new SaveFormatComparator(
                        string, string2, worldInfo.getLastTimePlayed(), l, worldInfo.getGameType(), bl, worldInfo.isHardcoreModeEnabled(), worldInfo.areCommandsAllowed()
                     )
                  );
               }
            }
         }

         return arrayList;
      } else {
         throw new AnvilConverterException("Unable to read or access folder where game worlds are saved!");
      }
   }

   public int getSaveVersion() {
      return 19133;
   }

   @Override
   public void flushCache() {
      RegionFileCache.clearRegionFileReferences();
   }

   @Override
   public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata) {
      return new AnvilSaveHandler(this.savesDirectory, saveName, storePlayerdata);
   }

   @Override
   public boolean isConvertible(String saveName) {
      WorldInfo worldInfo = this.getWorldInfo(saveName);
      return worldInfo != null && worldInfo.getSaveVersion() == 19132;
   }

   @Override
   public boolean isOldMapFormat(String saveName) {
      WorldInfo worldInfo = this.getWorldInfo(saveName);
      return worldInfo != null && worldInfo.getSaveVersion() != this.getSaveVersion();
   }

   @Override
   public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
      progressCallback.setLoadingProgress(0);
      ArrayList arrayList = Lists.newArrayList();
      ArrayList arrayList2 = Lists.newArrayList();
      ArrayList arrayList3 = Lists.newArrayList();
      File file = new File(this.savesDirectory, filename);
      File file2 = new File(file, "DIM-1");
      File file3 = new File(file, "DIM1");
      logger.info("Scanning folders...");
      this.addRegionFilesToCollection(file, arrayList);
      if (file2.exists()) {
         this.addRegionFilesToCollection(file2, arrayList2);
      }

      if (file3.exists()) {
         this.addRegionFilesToCollection(file3, arrayList3);
      }

      int i = arrayList.size() + arrayList2.size() + arrayList3.size();
      logger.info("Total conversion count is " + i);
      WorldInfo worldInfo = this.getWorldInfo(filename);
      WorldChunkManager worldChunkManager = null;
      if (worldInfo.getTerrainType() == WorldType.FLAT) {
         worldChunkManager = new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F);
      } else {
         worldChunkManager = new WorldChunkManager(worldInfo.getSeed(), worldInfo.getTerrainType(), worldInfo.getGeneratorOptions());
      }

      this.convertFile(new File(file, "region"), arrayList, worldChunkManager, 0, i, progressCallback);
      this.convertFile(new File(file2, "region"), arrayList2, new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F), arrayList.size(), i, progressCallback);
      this.convertFile(new File(file3, "region"), arrayList3, new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F), arrayList.size() + arrayList2.size(), i, progressCallback);
      worldInfo.setSaveVersion(19133);
      if (worldInfo.getTerrainType() == WorldType.DEFAULT_1_1) {
         worldInfo.setTerrainType(WorldType.DEFAULT);
      }

      this.createFile(filename);
      ISaveHandler iSaveHandler = this.getSaveLoader(filename, false);
      iSaveHandler.saveWorldInfo(worldInfo);
      return true;
   }

   private void createFile(String filename) {
      File file = new File(this.savesDirectory, filename);
      if (!file.exists()) {
         logger.warn("Unable to create level.dat_mcr backup");
      } else {
         File file2 = new File(file, "level.dat");
         if (!file2.exists()) {
            logger.warn("Unable to create level.dat_mcr backup");
         } else {
            File file3 = new File(file, "level.dat_mcr");
            if (!file2.renameTo(file3)) {
               logger.warn("Unable to create level.dat_mcr backup");
            }
         }
      }
   }

   private void convertFile(File file, Iterable<File> iterable, WorldChunkManager worldChunkManager, int i, int j, IProgressUpdate iProgressUpdate) {
      for (File file2 : iterable) {
         this.convertChunks(file, file2, worldChunkManager, i, j, iProgressUpdate);
         i++;
         int k = (int)Math.round(100.0 * i / j);
         iProgressUpdate.setLoadingProgress(k);
      }
   }

   private void convertChunks(File file, File file2, WorldChunkManager worldChunkManager, int i, int j, IProgressUpdate progressCallback) {
      try {
         String string = file2.getName();
         RegionFile regionFile = new RegionFile(file2);
         RegionFile regionFile2 = new RegionFile(new File(file, string.substring(0, string.length() - ".mcr".length()) + ".mca"));

         for (int k = 0; k < 32; k++) {
            for (int l = 0; l < 32; l++) {
               if (regionFile.isChunkSaved(k, l) && !regionFile2.isChunkSaved(k, l)) {
                  DataInputStream dataInputStream = regionFile.getChunkDataInputStream(k, l);
                  if (dataInputStream == null) {
                     logger.warn("Failed to fetch input stream");
                  } else {
                     NBTTagCompound nbtTagCompound = CompressedStreamTools.read(dataInputStream);
                     dataInputStream.close();
                     NBTTagCompound nbtTagCompound2 = nbtTagCompound.getCompoundTag("Level");
                     ChunkLoader.AnvilConverterData anvilConverterData = ChunkLoader.load(nbtTagCompound2);
                     NBTTagCompound nbtTagCompound3 = new NBTTagCompound();
                     NBTTagCompound nbtTagCompound4 = new NBTTagCompound();
                     nbtTagCompound3.setTag("Level", nbtTagCompound4);
                     ChunkLoader.convertToAnvilFormat(anvilConverterData, nbtTagCompound4, worldChunkManager);
                     DataOutputStream dataOutputStream = regionFile2.getChunkDataOutputStream(k, l);
                     CompressedStreamTools.write(nbtTagCompound3, dataOutputStream);
                     dataOutputStream.close();
                  }
               }
            }

            int m = (int)Math.round(100.0 * (i * 1024) / (j * 1024));
            int n = (int)Math.round(100.0 * ((k + 1) * 32 + i * 1024) / (j * 1024));
            if (n > m) {
               progressCallback.setLoadingProgress(n);
            }
         }

         regionFile.close();
         regionFile2.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }

   private void addRegionFilesToCollection(File worldDir, Collection<File> collection) {
      File file2 = new File(worldDir, "region");
      File[] files = file2.listFiles(new FilenameFilter() {
         @Override
         public boolean accept(File file, String string) {
            return string.endsWith(".mcr");
         }
      });
      if (files != null) {
         Collections.addAll(collection, files);
      }
   }
}
