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
   private static final long a = 82135059536711L;

   public AnvilSaveConverter(File var1) {
      super(var1);
   }

   @Override
   public String getName() {
      return "Anvil";
   }

   @Override
   public List<SaveFormatComparator> getSaveList() throws AnvilConverterException {
      if (this.savesDirectory != null && this.savesDirectory.exists() && this.savesDirectory.isDirectory()) {
         ArrayList var4 = Lists.newArrayList();
         File[] var5 = this.savesDirectory.listFiles();

         for (File var9 : var5) {
            if (var9.isDirectory()) {
               String var10 = var9.getName();
               WorldInfo var11 = this.getWorldInfo(var10);
               if (var11 != null && (var11.getSaveVersion() == 19132 || var11.getSaveVersion() == 19133)) {
                  boolean var12 = var11.getSaveVersion() != this.getSaveVersion();
                  String var13 = var11.getWorldName();
                  if (StringUtils.isEmpty(var13)) {
                     var13 = var10;
                  }

                  long var14 = 0L;
                  var4.add(
                     new SaveFormatComparator(
                        var10, var13, var11.getLastTimePlayed(), var14, var11.getGameType(), var12, var11.isHardcoreModeEnabled(), var11.areCommandsAllowed()
                     )
                  );
               }
            }
         }

         return var4;
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
   public ISaveHandler getSaveLoader(String var1, boolean var2) {
      return new AnvilSaveHandler(this.savesDirectory, var1, var2);
   }

   @Override
   public boolean isConvertible(String var1) {
      WorldInfo var5 = this.getWorldInfo(var1);
      return var5 != null && var5.getSaveVersion() == 19132;
   }

   @Override
   public boolean isOldMapFormat(String var1) {
      WorldInfo var5 = this.getWorldInfo(var1);
      return var5 != null && var5.getSaveVersion() != this.getSaveVersion();
   }

   @Override
   public boolean convertMapFormat(String var1, IProgressUpdate var2) {
      var2.setLoadingProgress(0);
      ArrayList var7 = Lists.newArrayList();
      ArrayList var8 = Lists.newArrayList();
      ArrayList var9 = Lists.newArrayList();
      File var10 = new File(this.savesDirectory, var1);
      File var11 = new File(var10, "DIM-1");
      File var12 = new File(var10, "DIM1");
      logger.info("Scanning folders...");
      this.addRegionFilesToCollection(var10, var7);
      if (var11.exists()) {
         this.addRegionFilesToCollection(var11, var8);
      }

      if (var12.exists()) {
         this.addRegionFilesToCollection(var12, var9);
      }

      int var13 = var7.size() + var8.size() + var9.size();
      logger.info("Total conversion count is " + var13);
      WorldInfo var14 = this.getWorldInfo(var1);
      WorldChunkManager var15 = null;
      if (var14.getTerrainType() == WorldType.FLAT) {
         var15 = new WorldChunkManagerHell(BiomeGenBase.plains, 0.5F);
      } else {
         var15 = new WorldChunkManager(var14.getSeed(), var14.getTerrainType(), var14.getGeneratorOptions());
      }

      this.convertFile(new File(var10, "region"), var7, var15, 0, var13, var2);
      this.convertFile(new File(var11, "region"), var8, new WorldChunkManagerHell(BiomeGenBase.hell, 0.0F), var7.size(), var13, var2);
      this.convertFile(new File(var12, "region"), var9, new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F), var7.size() + var8.size(), var13, var2);
      var14.setSaveVersion(19133);
      if (var14.getTerrainType() == WorldType.DEFAULT_1_1) {
         var14.setTerrainType(WorldType.DEFAULT);
      }

      this.createFile(var1);
      ISaveHandler var16 = this.getSaveLoader(var1, false);
      var16.saveWorldInfo(var14);
      return true;
   }

   private void createFile(String var1) {
      File var5 = new File(this.savesDirectory, var1);
      if (!var5.exists()) {
         logger.warn("Unable to create level.dat_mcr backup");
      } else {
         File var6 = new File(var5, "level.dat");
         if (!var6.exists()) {
            logger.warn("Unable to create level.dat_mcr backup");
         } else {
            File var7 = new File(var5, "level.dat_mcr");
            if (!var6.renameTo(var7)) {
               logger.warn("Unable to create level.dat_mcr backup");
            }
         }
      }
   }

   private void convertFile(File var1, Iterable<File> var2, WorldChunkManager var3, int var4, int var5, IProgressUpdate var6) {
      for (File var11 : var2) {
         this.convertChunks(var1, var11, var3, var4, var5, var6);
         var4++;
         int var12 = (int)Math.round(100.0 * var4 / var5);
         var6.setLoadingProgress(var12);
      }
   }

   private void convertChunks(File var1, File var2, WorldChunkManager var3, int var4, int var5, IProgressUpdate var6) {
      try {
         String var10 = var2.getName();
         RegionFile var11 = new RegionFile(var2);
         RegionFile var12 = new RegionFile(new File(var1, var10.substring(0, var10.length() - ".mcr".length()) + ".mca"));

         for (int var13 = 0; var13 < 32; var13++) {
            for (int var14 = 0; var14 < 32; var14++) {
               if (var11.isChunkSaved(var13, var14) && !var12.isChunkSaved(var13, var14)) {
                  DataInputStream var15 = var11.getChunkDataInputStream(var13, var14);
                  if (var15 == null) {
                     logger.warn("Failed to fetch input stream");
                  } else {
                     NBTTagCompound var16 = CompressedStreamTools.read(var15);
                     var15.close();
                     NBTTagCompound var17 = var16.getCompoundTag("Level");
                     ChunkLoader.AnvilConverterData var18 = ChunkLoader.load(var17);
                     NBTTagCompound var19 = new NBTTagCompound();
                     NBTTagCompound var20 = new NBTTagCompound();
                     var19.setTag("Level", var20);
                     ChunkLoader.convertToAnvilFormat(var18, var20, var3);
                     DataOutputStream var21 = var12.getChunkDataOutputStream(var13, var14);
                     CompressedStreamTools.write(var19, var21);
                     var21.close();
                  }
               }
            }

            int var23 = (int)Math.round(100.0 * (var4 * 1024) / (var5 * 1024));
            int var24 = (int)Math.round(100.0 * ((var13 + 1) * 32 + var4 * 1024) / (var5 * 1024));
            if (var24 > var23) {
               var6.setLoadingProgress(var24);
            }
         }

         var11.close();
         var12.close();
      } catch (IOException var22) {
         var22.printStackTrace();
      }
   }

   private void addRegionFilesToCollection(File var1, Collection<File> var2) {
      File var5 = new File(var1, "region");
      File[] var6 = var5.listFiles(new FilenameFilter() {
         private static final long a = 85280828702001L;

         @Override
         public boolean accept(File var1, String var2x) {
            return var2x.endsWith(".mcr");
         }
      });
      if (var6 != null) {
         Collections.addAll(var2, var6);
      }
   }
}
