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

   public SaveFormatOld(File var1) {
      if (!var1.exists()) {
         var1.mkdirs();
      }

      this.savesDirectory = var1;
   }

   @Override
   public String getName() {
      return "Old Format";
   }

   @Override
   public List<SaveFormatComparator> getSaveList() throws AnvilConverterException {
      ArrayList var4 = Lists.newArrayList();

      for (int var5 = 0; var5 < 5; var5++) {
         String var6 = "World" + (var5 + 1);
         WorldInfo var7 = this.getWorldInfo(var6);
         if (var7 != null) {
            var4.add(
               new SaveFormatComparator(
                  var6, "", var7.getLastTimePlayed(), var7.getSizeOnDisk(), var7.getGameType(), false, var7.isHardcoreModeEnabled(), var7.areCommandsAllowed()
               )
            );
         }
      }

      return var4;
   }

   @Override
   public void flushCache() {
   }

   @Override
   public WorldInfo getWorldInfo(String var1) {
      File var5 = new File(this.savesDirectory, var1);
      if (!var5.exists()) {
         return null;
      }

      File var6 = new File(var5, "level.dat");
      if (var6.exists()) {
         try {
            NBTTagCompound var12 = CompressedStreamTools.readCompressed(new FileInputStream(var6));
            NBTTagCompound var13 = var12.getCompoundTag("Data");
            return new WorldInfo(var13);
         } catch (Exception var10) {
            logger.error("Exception reading " + var6, var10);
         }
      }

      var6 = new File(var5, "level.dat_old");
      if (var6.exists()) {
         try {
            NBTTagCompound var7 = CompressedStreamTools.readCompressed(new FileInputStream(var6));
            NBTTagCompound var8 = var7.getCompoundTag("Data");
            return new WorldInfo(var8);
         } catch (Exception var9) {
            logger.error("Exception reading " + var6, var9);
         }
      }

      return null;
   }

   @Override
   public void renameWorld(String var1, String var2) {
      File var6 = new File(this.savesDirectory, var1);
      if (var6.exists()) {
         File var7 = new File(var6, "level.dat");
         if (var7.exists()) {
            try {
               NBTTagCompound var8 = CompressedStreamTools.readCompressed(new FileInputStream(var7));
               NBTTagCompound var9 = var8.getCompoundTag("Data");
               var9.setString("LevelName", var2);
               CompressedStreamTools.writeCompressed(var8, new FileOutputStream(var7));
            } catch (Exception var10) {
               var10.printStackTrace();
            }
         }
      }
   }

   @Override
   public boolean isNewLevelIdAcceptable(String var1) {
      File var5 = new File(this.savesDirectory, var1);
      if (var5.exists()) {
         return false;
      }

      try {
         var5.mkdir();
         var5.delete();
         return true;
      } catch (Throwable var7) {
         logger.warn("Couldn't make new level", var7);
         return false;
      }
   }

   @Override
   public boolean deleteWorldDirectory(String var1) {
      File var5 = new File(this.savesDirectory, var1);
      if (!var5.exists()) {
         return true;
      }

      logger.info("Deleting level " + var1);

      for (int var6 = 1; var6 <= 5; var6++) {
         logger.info("Attempt " + var6 + "...");
         if (deleteFiles(var5.listFiles())) {
            break;
         }

         logger.warn("Unsuccessful in deleting contents.");
         if (var6 < 5) {
            try {
               Thread.sleep(500L);
            } catch (InterruptedException var8) {
            }
         }
      }

      return var5.delete();
   }

   public static boolean deleteFiles(File[] var0) {
      for (int var4 = 0; var4 < var0.length; var4++) {
         File var5 = var0[var4];
         logger.debug("Deleting " + var5);
         if (var5.isDirectory() && !deleteFiles(var5.listFiles())) {
            logger.warn("Couldn't delete directory " + var5);
            return false;
         }

         if (!var5.delete()) {
            logger.warn("Couldn't delete file " + var5);
            return false;
         }
      }

      return true;
   }

   @Override
   public ISaveHandler getSaveLoader(String var1, boolean var2) {
      return new SaveHandler(this.savesDirectory, var1, var2);
   }

   @Override
   public boolean isConvertible(String var1) {
      return false;
   }

   @Override
   public boolean isOldMapFormat(String var1) {
      return false;
   }

   @Override
   public boolean convertMapFormat(String var1, IProgressUpdate var2) {
      return false;
   }

   @Override
   public boolean canLoadWorld(String var1) {
      File var2 = new File(this.savesDirectory, var1);
      return var2.isDirectory();
   }
}
