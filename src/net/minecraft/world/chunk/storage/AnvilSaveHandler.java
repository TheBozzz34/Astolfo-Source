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
   private static final long a = 8701770818447L;
   private static final long k = -1655002361170474307L;

   public AnvilSaveHandler(File var1, String var2, boolean var3) {
      super(var1, var2, var3);
   }

   @Override
   public IChunkLoader getChunkLoader(WorldProvider var1) {
      File var5 = this.getWorldDirectory();
      if (var1 instanceof WorldProviderHell) {
         File var7 = new File(var5, "DIM-1");
         var7.mkdirs();
         return new AnvilChunkLoader(var7);
      } else if (var1 instanceof WorldProviderEnd) {
         File var6 = new File(var5, "DIM1");
         var6.mkdirs();
         return new AnvilChunkLoader(var6);
      } else {
         return new AnvilChunkLoader(var5);
      }
   }

   @Override
   public void saveWorldInfoWithPlayer(WorldInfo var1, NBTTagCompound var2) {
      var1.setSaveVersion((int)k);
      super.saveWorldInfoWithPlayer(var1, var2);
   }

   @Override
   public void flush() {
      try {
         ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }

      RegionFileCache.clearRegionFileReferences();
   }
}
