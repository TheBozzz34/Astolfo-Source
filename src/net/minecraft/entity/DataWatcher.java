package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Rotations;
import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher {
   private final Entity owner;
   private boolean isBlank = true;
   private static final Map<Class<?>, Integer> dataTypes = Maps.newHashMap();
   private final Map<Integer, DataWatcher.WatchableObject> watchedObjects = Maps.newHashMap();
   private boolean objectChanged;
   private ReadWriteLock lock = new ReentrantReadWriteLock();
   private static final long b = 16941455846499L;

   public DataWatcher(Entity var1) {
      this.owner = var1;
   }

   public <T> void addObject(int var1, T var2) {
      Integer var6 = dataTypes.get(var2.getClass());
      if (var6 == null) {
         throw new IllegalArgumentException("Unknown data type: " + var2.getClass());
      }

      if (var1 > 31) {
         throw new IllegalArgumentException("Data value id is too big with " + var1 + "! (Max is " + 31 + ")");
      }

      if (this.watchedObjects.containsKey(var1)) {
         throw new IllegalArgumentException("Duplicate id value for " + var1 + "!");
      }

      DataWatcher.WatchableObject var7 = new DataWatcher.WatchableObject(var6, var1, var2);
      this.lock.writeLock().lock();
      this.watchedObjects.put(var1, var7);
      this.lock.writeLock().unlock();
      this.isBlank = false;
   }

   public void addObjectByDataType(int var1, int var2) {
      DataWatcher.WatchableObject var3 = new DataWatcher.WatchableObject(var2, var1, null);
      this.lock.writeLock().lock();
      this.watchedObjects.put(var1, var3);
      this.lock.writeLock().unlock();
      this.isBlank = false;
   }

   public byte getWatchableObjectByte(int var1) {
      return (Byte)this.getWatchedObject(var1).getObject();
   }

   public short getWatchableObjectShort(int var1) {
      return (Short)this.getWatchedObject(var1).getObject();
   }

   public int getWatchableObjectInt(int var1) {
      return (Integer)this.getWatchedObject(var1).getObject();
   }

   public float getWatchableObjectFloat(int var1) {
      return (Float)this.getWatchedObject(var1).getObject();
   }

   public String getWatchableObjectString(int var1) {
      return (String)this.getWatchedObject(var1).getObject();
   }

   public ItemStack getWatchableObjectItemStack(int var1) {
      return (ItemStack)this.getWatchedObject(var1).getObject();
   }

   private DataWatcher.WatchableObject getWatchedObject(int var1) {
      this.lock.readLock().lock();

      DataWatcher.WatchableObject var4;
      try {
         var4 = this.watchedObjects.get(var1);
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.makeCrashReport(var8, "Getting synched entity data");
         CrashReportCategory var7 = var6.makeCategory("Synched entity data");
         var7.addCrashSection("Data ID", var1);
         throw new ReportedException(var6);
      }

      this.lock.readLock().unlock();
      return var4;
   }

   public Rotations getWatchableObjectRotations(int var1) {
      return (Rotations)this.getWatchedObject(var1).getObject();
   }

   public <T> void updateObject(int var1, T var2) {
      DataWatcher.WatchableObject var6 = this.getWatchedObject(var1);
      if (ObjectUtils.notEqual(var2, var6.getObject())) {
         var6.setObject(var2);
         this.owner.onDataWatcherUpdate(var1);
         var6.setWatched(true);
         this.objectChanged = true;
      }
   }

   public void setObjectWatched(int var1) {
      this.getWatchedObject(var1).watched = true;
      this.objectChanged = true;
   }

   public boolean hasObjectChanged() {
      return this.objectChanged;
   }

   public static void writeWatchedListToPacketBuffer(List<DataWatcher.WatchableObject> var0, PacketBuffer var1) throws IOException {
      if (var0 != null) {
         for (DataWatcher.WatchableObject var6 : var0) {
            writeWatchableObjectToPacketBuffer(var1, var6);
         }
      }

      var1.writeByte(127);
   }

   public List<DataWatcher.WatchableObject> getChanged() {
      ArrayList var4 = null;
      if (this.objectChanged) {
         this.lock.readLock().lock();

         for (DataWatcher.WatchableObject var6 : this.watchedObjects.values()) {
            if (var6.isWatched()) {
               var6.setWatched(false);
               if (var4 == null) {
                  var4 = Lists.newArrayList();
               }

               var4.add(var6);
            }
         }

         this.lock.readLock().unlock();
      }

      this.objectChanged = false;
      return var4;
   }

   public void writeTo(PacketBuffer var1) throws IOException {
      this.lock.readLock().lock();

      for (DataWatcher.WatchableObject var6 : this.watchedObjects.values()) {
         writeWatchableObjectToPacketBuffer(var1, var6);
      }

      this.lock.readLock().unlock();
      var1.writeByte(127);
   }

   public List<DataWatcher.WatchableObject> getAllWatched() {
      ArrayList var4 = null;
      this.lock.readLock().lock();

      for (DataWatcher.WatchableObject var6 : this.watchedObjects.values()) {
         if (var4 == null) {
            var4 = Lists.newArrayList();
         }

         var4.add(var6);
      }

      this.lock.readLock().unlock();
      return var4;
   }

   private static void writeWatchableObjectToPacketBuffer(PacketBuffer var0, DataWatcher.WatchableObject var1) throws IOException {
      int var5 = (var1.getObjectType() << 5 | var1.getDataValueId() & 31) & 0xFF;
      var0.writeByte(var5);
      switch (var1.getObjectType()) {
         case 0:
            var0.writeByte((Byte)var1.getObject());
            break;
         case 1:
            var0.writeShort((Short)var1.getObject());
            break;
         case 2:
            var0.writeInt((Integer)var1.getObject());
            break;
         case 3:
            var0.writeFloat((Float)var1.getObject());
            break;
         case 4:
            var0.writeString((String)var1.getObject());
            break;
         case 5:
            ItemStack var6 = (ItemStack)var1.getObject();
            var0.writeItemStackToBuffer(var6);
            break;
         case 6:
            BlockPos var7 = (BlockPos)var1.getObject();
            var0.writeInt(var7.getX());
            var0.writeInt(var7.getY());
            var0.writeInt(var7.getZ());
            break;
         case 7:
            Rotations var8 = (Rotations)var1.getObject();
            var0.writeFloat(var8.getX());
            var0.writeFloat(var8.getY());
            var0.writeFloat(var8.getZ());
      }
   }

   public static List<DataWatcher.WatchableObject> readWatchedListFromPacketBuffer(PacketBuffer var0) throws IOException {
      ArrayList var4 = null;

      for (int var5 = var0.readByte(); var5 != 127; var5 = var0.readByte()) {
         if (var4 == null) {
            var4 = Lists.newArrayList();
         }

         int var6 = (var5 & 224) >> 5;
         int var7 = var5 & 31;
         DataWatcher.WatchableObject var8 = null;
         switch (var6) {
            case 0:
               var8 = new DataWatcher.WatchableObject(var6, var7, var0.readByte());
               break;
            case 1:
               var8 = new DataWatcher.WatchableObject(var6, var7, var0.readShort());
               break;
            case 2:
               var8 = new DataWatcher.WatchableObject(var6, var7, var0.readInt());
               break;
            case 3:
               var8 = new DataWatcher.WatchableObject(var6, var7, var0.readFloat());
               break;
            case 4:
               var8 = new DataWatcher.WatchableObject(var6, var7, var0.readStringFromBuffer(32767));
               break;
            case 5:
               var8 = new DataWatcher.WatchableObject(var6, var7, var0.readItemStackFromBuffer());
               break;
            case 6:
               int var9 = var0.readInt();
               int var10 = var0.readInt();
               int var11 = var0.readInt();
               var8 = new DataWatcher.WatchableObject(var6, var7, new BlockPos(var9, var10, var11));
               break;
            case 7:
               float var12 = var0.readFloat();
               float var13 = var0.readFloat();
               float var14 = var0.readFloat();
               var8 = new DataWatcher.WatchableObject(var6, var7, new Rotations(var12, var13, var14));
         }

         var4.add(var8);
      }

      return var4;
   }

   public void updateWatchedObjectsFromList(List<DataWatcher.WatchableObject> var1) {
      this.lock.writeLock().lock();

      for (DataWatcher.WatchableObject var6 : var1) {
         DataWatcher.WatchableObject var7 = this.watchedObjects.get(var6.getDataValueId());
         if (var7 != null) {
            var7.setObject(var6.getObject());
            this.owner.onDataWatcherUpdate(var6.getDataValueId());
         }
      }

      this.lock.writeLock().unlock();
      this.objectChanged = true;
   }

   public boolean getIsBlank() {
      return this.isBlank;
   }

   public void func_111144_e() {
      this.objectChanged = false;
   }

   static {
      dataTypes.put(Byte.class, 0);
      dataTypes.put(Short.class, 1);
      dataTypes.put(Integer.class, 2);
      dataTypes.put(Float.class, 3);
      dataTypes.put(String.class, 4);
      dataTypes.put(ItemStack.class, 5);
      dataTypes.put(BlockPos.class, 6);
      dataTypes.put(Rotations.class, 7);
   }

   public static class WatchableObject {
      private final int objectType;
      private final int dataValueId;
      private Object watchedObject;
      private boolean watched;

      public WatchableObject(int var1, int var2, Object var3) {
         this.dataValueId = var2;
         this.watchedObject = var3;
         this.objectType = var1;
         this.watched = true;
      }

      public int getDataValueId() {
         return this.dataValueId;
      }

      public void setObject(Object var1) {
         this.watchedObject = var1;
      }

      public Object getObject() {
         return this.watchedObject;
      }

      public int getObjectType() {
         return this.objectType;
      }

      public boolean isWatched() {
         return this.watched;
      }

      public void setWatched(boolean var1) {
         this.watched = var1;
      }
   }
}
