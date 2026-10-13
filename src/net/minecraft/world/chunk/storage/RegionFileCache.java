package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RegionFileCache {
   private static final Map<File, RegionFile> regionsByFilename = Maps.newHashMap();
   private static final long a = 6363100465421L;

   public static synchronized RegionFile createOrLoadRegionFile(File var0, int var1, int var2) {
      File var6 = new File(var0, "region");
      File var7 = new File(var6, "r." + (var1 >> 5) + "." + (var2 >> 5) + ".mca");
      RegionFile var8 = regionsByFilename.get(var7);
      if (var8 != null) {
         return var8;
      }

      if (!var6.exists()) {
         var6.mkdirs();
      }

      if (regionsByFilename.size() >= 256) {
         clearRegionFileReferences();
      }

      RegionFile var9 = new RegionFile(var7);
      regionsByFilename.put(var7, var9);
      return var9;
   }

   public static synchronized void clearRegionFileReferences() {
      for (RegionFile var4 : regionsByFilename.values()) {
         try {
            if (var4 != null) {
               var4.close();
            }
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

      regionsByFilename.clear();
   }

   public static DataInputStream getChunkInputStream(File var0, int var1, int var2) {
      RegionFile var5 = createOrLoadRegionFile(var0, var1, var2);
      return var5.getChunkDataInputStream(var1 & 31, var2 & 31);
   }

   public static DataOutputStream getChunkOutputStream(File var0, int var1, int var2) {
      RegionFile var5 = createOrLoadRegionFile(var0, var1, var2);
      return var5.getChunkDataOutputStream(var1 & 31, var2 & 31);
   }
}
