package net.minecraft.world.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveHandler implements ISaveHandler, IPlayerFileData {
   private static final Logger logger = LogManager.getLogger();
   private final File worldDirectory;
   private final File playersDirectory;
   private final File mapDataDir;
   private final long initializationTime = MinecraftServer.getCurrentTimeMillis();
   private final String saveDirectoryName;

   public SaveHandler(File savesDirectory, String directoryName, boolean playersDirectoryIn) {
      this.worldDirectory = new File(savesDirectory, directoryName);
      this.worldDirectory.mkdirs();
      this.playersDirectory = new File(this.worldDirectory, "playerdata");
      this.mapDataDir = new File(this.worldDirectory, "data");
      this.mapDataDir.mkdirs();
      this.saveDirectoryName = directoryName;
      if (playersDirectoryIn) {
         this.playersDirectory.mkdirs();
      }

      this.setSessionLock();
   }

   private void setSessionLock() {
      try {
         File file = new File(this.worldDirectory, "session.lock");
         DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

         try {
            dataOutputStream.writeLong(this.initializationTime);
         } finally {
            dataOutputStream.close();
         }
      } catch (IOException ioException) {
         ioException.printStackTrace();
         throw new RuntimeException("Failed to check session lock, aborting");
      }
   }

   @Override
   public File getWorldDirectory() {
      return this.worldDirectory;
   }

   @Override
   public void checkSessionLock() throws MinecraftException {
      try {
         File file = new File(this.worldDirectory, "session.lock");
         DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

         try {
            if (dataInputStream.readLong() != this.initializationTime) {
               throw new MinecraftException("The save is being accessed from another location, aborting");
            }
         } finally {
            dataInputStream.close();
         }
      } catch (IOException ioException) {
         throw new MinecraftException("Failed to check session lock, aborting");
      }
   }

   @Override
   public IChunkLoader getChunkLoader(WorldProvider provider) {
      throw new RuntimeException("Old Chunk Storage is no longer supported.");
   }

   @Override
   public WorldInfo loadWorldInfo() {
      File file = new File(this.worldDirectory, "level.dat");
      if (file.exists()) {
         try {
            NBTTagCompound nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(file));
            NBTTagCompound nbtTagCompound2 = nbtTagCompound.getCompoundTag("Data");
            return new WorldInfo(nbtTagCompound2);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

      file = new File(this.worldDirectory, "level.dat_old");
      if (file.exists()) {
         try {
            NBTTagCompound nbtTagCompound3 = CompressedStreamTools.readCompressed(new FileInputStream(file));
            NBTTagCompound nbtTagCompound4 = nbtTagCompound3.getCompoundTag("Data");
            return new WorldInfo(nbtTagCompound4);
         } catch (Exception exception2) {
            exception2.printStackTrace();
         }
      }

      return null;
   }

   @Override
   public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
      NBTTagCompound nbtTagCompound = worldInformation.cloneNBTCompound(tagCompound);
      NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
      nbtTagCompound2.setTag("Data", nbtTagCompound);

      try {
         File file = new File(this.worldDirectory, "level.dat_new");
         File file2 = new File(this.worldDirectory, "level.dat_old");
         File file3 = new File(this.worldDirectory, "level.dat");
         CompressedStreamTools.writeCompressed(nbtTagCompound2, new FileOutputStream(file));
         if (file2.exists()) {
            file2.delete();
         }

         file3.renameTo(file2);
         if (file3.exists()) {
            file3.delete();
         }

         file.renameTo(file3);
         if (file.exists()) {
            file.delete();
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Override
   public void saveWorldInfo(WorldInfo worldInformation) {
      NBTTagCompound nbtTagCompound = worldInformation.getNBTTagCompound();
      NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
      nbtTagCompound2.setTag("Data", nbtTagCompound);

      try {
         File file = new File(this.worldDirectory, "level.dat_new");
         File file2 = new File(this.worldDirectory, "level.dat_old");
         File file3 = new File(this.worldDirectory, "level.dat");
         CompressedStreamTools.writeCompressed(nbtTagCompound2, new FileOutputStream(file));
         if (file2.exists()) {
            file2.delete();
         }

         file3.renameTo(file2);
         if (file3.exists()) {
            file3.delete();
         }

         file.renameTo(file3);
         if (file.exists()) {
            file.delete();
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Override
   public void writePlayerData(EntityPlayer player) {
      try {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         player.writeToNBT(nbtTagCompound);
         File file = new File(this.playersDirectory, player.getUniqueID().toString() + ".dat.tmp");
         File file2 = new File(this.playersDirectory, player.getUniqueID().toString() + ".dat");
         CompressedStreamTools.writeCompressed(nbtTagCompound, new FileOutputStream(file));
         if (file2.exists()) {
            file2.delete();
         }

         file.renameTo(file2);
      } catch (Exception exception) {
         logger.warn("Failed to save player data for " + player.getName());
      }
   }

   @Override
   public NBTTagCompound readPlayerData(EntityPlayer player) {
      NBTTagCompound nbtTagCompound = null;

      try {
         File file = new File(this.playersDirectory, player.getUniqueID().toString() + ".dat");
         if (file.exists() && file.isFile()) {
            nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(file));
         }
      } catch (Exception exception) {
         logger.warn("Failed to load player data for " + player.getName());
      }

      if (nbtTagCompound != null) {
         player.readFromNBT(nbtTagCompound);
      }

      return nbtTagCompound;
   }

   @Override
   public IPlayerFileData getPlayerNBTManager() {
      return this;
   }

   @Override
   public String[] getAvailablePlayerDat() {
      String[] strings = this.playersDirectory.list();
      if (strings == null) {
         strings = new String[0];
      }

      for (int i = 0; i < strings.length; i++) {
         if (strings[i].endsWith(".dat")) {
            strings[i] = strings[i].substring(0, strings[i].length() - 4);
         }
      }

      return strings;
   }

   @Override
   public void flush() {
   }

   @Override
   public File getMapFileFromName(String mapName) {
      return new File(this.mapDataDir, mapName + ".dat");
   }

   @Override
   public String getWorldDirectoryName() {
      return this.saveDirectoryName;
   }
}
