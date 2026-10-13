package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;

public class RealmsAnvilLevelStorageSource {
   private ISaveFormat levelStorageSource;
   private static final long a = 132936317656314L;

   public RealmsAnvilLevelStorageSource(ISaveFormat var1) {
      this.levelStorageSource = var1;
   }

   public String getName() {
      return this.levelStorageSource.getName();
   }

   public boolean a(String var1) {
      return this.levelStorageSource.canLoadWorld(var1);
   }

   public boolean convertLevel(String var1, IProgressUpdate var2) {
      return this.levelStorageSource.convertMapFormat(var1, var2);
   }

   public boolean e(String var1) {
      return this.levelStorageSource.isOldMapFormat(var1);
   }

   public boolean G(String var1) {
      return this.levelStorageSource.isNewLevelIdAcceptable(var1);
   }

   public boolean H(String var1) {
      return this.levelStorageSource.deleteWorldDirectory(var1);
   }

   public boolean n(String var1) {
      return this.levelStorageSource.isConvertible(var1);
   }

   public void renameLevel(String var1, String var2) {
      this.levelStorageSource.renameWorld(var1, var2);
   }

   public void clearAll() {
      this.levelStorageSource.flushCache();
   }

   public List<RealmsLevelSummary> getLevelList() throws AnvilConverterException {
      ArrayList var4 = Lists.newArrayList();

      for (SaveFormatComparator var6 : this.levelStorageSource.getSaveList()) {
         var4.add(new RealmsLevelSummary(var6));
      }

      return var4;
   }
}
