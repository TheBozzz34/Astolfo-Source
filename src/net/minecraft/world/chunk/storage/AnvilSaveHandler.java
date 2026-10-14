package net.minecraft.world.chunk.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler extends SaveHandler {
   public AnvilSaveHandler(File savesDirectory, String directoryName, boolean storePlayerdata) {
      super(savesDirectory, directoryName, storePlayerdata);
   }

   @Override
   public IChunkLoader getChunkLoader(WorldProvider provider) {
      File file = this.getWorldDirectory();
      if (provider instanceof WorldProviderHell) {
         File file2 = new File(file, "DIM-1");
         file2.mkdirs();
         return new AnvilChunkLoader(file2);
      } else if (provider instanceof WorldProviderEnd) {
         File file3 = new File(file, "DIM1");
         file3.mkdirs();
         return new AnvilChunkLoader(file3);
      } else {
         return new AnvilChunkLoader(file);
      }
   }

   @Override
   public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
      worldInformation.setSaveVersion(19133);
      super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
   }

   @Override
   public void flush() {
      try {
         ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
      } catch (InterruptedException interruptedException) {
         interruptedException.printStackTrace();
      }

      RegionFileCache.clearRegionFileReferences();
   }
}
