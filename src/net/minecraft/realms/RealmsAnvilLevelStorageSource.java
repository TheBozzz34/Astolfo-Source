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

   public RealmsAnvilLevelStorageSource(ISaveFormat levelStorageSourceIn) {
      this.levelStorageSource = levelStorageSourceIn;
   }

   public String getName() {
      return this.levelStorageSource.getName();
   }

   public boolean a(String string) {
      return this.levelStorageSource.canLoadWorld(string);
   }

   public boolean convertLevel(String string, IProgressUpdate iProgressUpdate) {
      return this.levelStorageSource.convertMapFormat(string, iProgressUpdate);
   }

   public boolean e(String string) {
      return this.levelStorageSource.isOldMapFormat(string);
   }

   public boolean G(String string) {
      return this.levelStorageSource.isNewLevelIdAcceptable(string);
   }

   public boolean H(String string) {
      return this.levelStorageSource.deleteWorldDirectory(string);
   }

   public boolean n(String string) {
      return this.levelStorageSource.isConvertible(string);
   }

   public void renameLevel(String string, String string2) {
      this.levelStorageSource.renameWorld(string, string2);
   }

   public void clearAll() {
      this.levelStorageSource.flushCache();
   }

   public List<RealmsLevelSummary> getLevelList() throws AnvilConverterException {
      ArrayList arrayList = Lists.newArrayList();

      for (SaveFormatComparator saveFormatComparator : this.levelStorageSource.getSaveList()) {
         arrayList.add(new RealmsLevelSummary(saveFormatComparator));
      }

      return arrayList;
   }
}
