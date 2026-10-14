package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat {
   private static final Logger logger = LogManager.getLogger();
   public final File savesDirectory;
   private static final long b = 22921181179171L;

   public SaveFormatOld(File savesDirectoryIn) {
      if (!savesDirectoryIn.exists()) {
         savesDirectoryIn.mkdirs();
      }

      this.savesDirectory = savesDirectoryIn;
   }

   @Override
   public String getName() {
      return "Old Format";
   }

   @Override
   public List<SaveFormatComparator> getSaveList() throws AnvilConverterException {
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < 5; i++) {
         String string = "World" + (i + 1);
         WorldInfo worldInfo = this.getWorldInfo(string);
         if (worldInfo != null) {
            arrayList.add(
               new SaveFormatComparator(
                  string, "", worldInfo.getLastTimePlayed(), worldInfo.getSizeOnDisk(), worldInfo.getGameType(), false, worldInfo.isHardcoreModeEnabled(), worldInfo.areCommandsAllowed()
               )
            );
         }
      }

      return arrayList;
   }

   @Override
   public void flushCache() {
   }

   @Override
   public WorldInfo getWorldInfo(String saveName) {
      File file = new File(this.savesDirectory, saveName);
      if (!file.exists()) {
         return null;
      }

      File file2 = new File(file, "level.dat");
      if (file2.exists()) {
         try {
            NBTTagCompound nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
            NBTTagCompound nbtTagCompound2 = nbtTagCompound.getCompoundTag("Data");
            return new WorldInfo(nbtTagCompound2);
         } catch (Exception exception) {
            logger.error("Exception reading " + file2, exception);
         }
      }

      file2 = new File(file, "level.dat_old");
      if (file2.exists()) {
         try {
            NBTTagCompound nbtTagCompound3 = CompressedStreamTools.readCompressed(new FileInputStream(file2));
            NBTTagCompound nbtTagCompound4 = nbtTagCompound3.getCompoundTag("Data");
            return new WorldInfo(nbtTagCompound4);
         } catch (Exception exception2) {
            logger.error("Exception reading " + file2, exception2);
         }
      }

      return null;
   }

   @Override
   public void renameWorld(String dirName, String newName) {
      File file = new File(this.savesDirectory, dirName);
      if (file.exists()) {
         File file2 = new File(file, "level.dat");
         if (file2.exists()) {
            try {
               NBTTagCompound nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(file2));
               NBTTagCompound nbtTagCompound2 = nbtTagCompound.getCompoundTag("Data");
               nbtTagCompound2.setString("LevelName", newName);
               CompressedStreamTools.writeCompressed(nbtTagCompound, new FileOutputStream(file2));
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }
      }
   }

   @Override
   public boolean isNewLevelIdAcceptable(String saveName) {
      File file = new File(this.savesDirectory, saveName);
      if (file.exists()) {
         return false;
      }

      try {
         file.mkdir();
         file.delete();
         return true;
      } catch (Throwable throwable) {
         logger.warn("Couldn't make new level", throwable);
         return false;
      }
   }

   @Override
   public boolean deleteWorldDirectory(String saveName) {
      File file = new File(this.savesDirectory, saveName);
      if (!file.exists()) {
         return true;
      }

      logger.info("Deleting level " + saveName);

      for (int i = 1; i <= 5; i++) {
         logger.info("Attempt " + i + "...");
         if (deleteFiles(file.listFiles())) {
            break;
         }

         logger.warn("Unsuccessful in deleting contents.");
         if (i < 5) {
            try {
               Thread.sleep(500L);
            } catch (InterruptedException interruptedException) {
            }
         }
      }

      return file.delete();
   }

   public static boolean deleteFiles(File[] files) {
      for (int i = 0; i < files.length; i++) {
         File file2 = files[i];
         logger.debug("Deleting " + file2);
         if (file2.isDirectory() && !deleteFiles(file2.listFiles())) {
            logger.warn("Couldn't delete directory " + file2);
            return false;
         }

         if (!file2.delete()) {
            logger.warn("Couldn't delete file " + file2);
            return false;
         }
      }

      return true;
   }

   @Override
   public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata) {
      return new SaveHandler(this.savesDirectory, saveName, storePlayerdata);
   }

   @Override
   public boolean isConvertible(String saveName) {
      return false;
   }

   @Override
   public boolean isOldMapFormat(String saveName) {
      return false;
   }

   @Override
   public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
      return false;
   }

   @Override
   public boolean canLoadWorld(String saveName) {
      File file = new File(this.savesDirectory, saveName);
      return file.isDirectory();
   }
}
