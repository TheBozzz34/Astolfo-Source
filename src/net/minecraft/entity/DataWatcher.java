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

   public DataWatcher(Entity owner) {
      this.owner = owner;
   }

   public <T> void addObject(int id, T object) {
      Integer integer = dataTypes.get(object.getClass());
      if (integer == null) {
         throw new IllegalArgumentException("Unknown data type: " + object.getClass());
      }

      if (id > 31) {
         throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
      }

      if (this.watchedObjects.containsKey(id)) {
         throw new IllegalArgumentException("Duplicate id value for " + id + "!");
      }

      DataWatcher.WatchableObject watchableObject = new DataWatcher.WatchableObject(integer, id, object);
      this.lock.writeLock().lock();
      this.watchedObjects.put(id, watchableObject);
      this.lock.writeLock().unlock();
      this.isBlank = false;
   }

   public void addObjectByDataType(int id, int typeIn) {
      DataWatcher.WatchableObject watchableObject = new DataWatcher.WatchableObject(typeIn, id, null);
      this.lock.writeLock().lock();
      this.watchedObjects.put(id, watchableObject);
      this.lock.writeLock().unlock();
      this.isBlank = false;
   }

   public byte getWatchableObjectByte(int id) {
      return (Byte)this.getWatchedObject(id).getObject();
   }

   public short getWatchableObjectShort(int id) {
      return (Short)this.getWatchedObject(id).getObject();
   }

   public int getWatchableObjectInt(int id) {
      return (Integer)this.getWatchedObject(id).getObject();
   }

   public float getWatchableObjectFloat(int id) {
      return (Float)this.getWatchedObject(id).getObject();
   }

   public String getWatchableObjectString(int id) {
      return (String)this.getWatchedObject(id).getObject();
   }

   public ItemStack getWatchableObjectItemStack(int id) {
      return (ItemStack)this.getWatchedObject(id).getObject();
   }

   private DataWatcher.WatchableObject getWatchedObject(int id) {
      this.lock.readLock().lock();

      DataWatcher.WatchableObject watchableObject;
      try {
         watchableObject = this.watchedObjects.get(id);
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Getting synched entity data");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Synched entity data");
         crashReportCategory.addCrashSection("Data ID", id);
         throw new ReportedException(crashReport);
      }

      this.lock.readLock().unlock();
      return watchableObject;
   }

   public Rotations getWatchableObjectRotations(int id) {
      return (Rotations)this.getWatchedObject(id).getObject();
   }

   public <T> void updateObject(int id, T newData) {
      DataWatcher.WatchableObject watchableObject = this.getWatchedObject(id);
      if (ObjectUtils.notEqual(newData, watchableObject.getObject())) {
         watchableObject.setObject(newData);
         this.owner.onDataWatcherUpdate(id);
         watchableObject.setWatched(true);
         this.objectChanged = true;
      }
   }

   public void setObjectWatched(int id) {
      this.getWatchedObject(id).watched = true;
      this.objectChanged = true;
   }

   public boolean hasObjectChanged() {
      return this.objectChanged;
   }

   public static void writeWatchedListToPacketBuffer(List<DataWatcher.WatchableObject> objectsList, PacketBuffer buffer) throws IOException {
      if (objectsList != null) {
         for (DataWatcher.WatchableObject watchableObject : objectsList) {
            writeWatchableObjectToPacketBuffer(buffer, watchableObject);
         }
      }

      buffer.writeByte(127);
   }

   public List<DataWatcher.WatchableObject> getChanged() {
      ArrayList arrayList = null;
      if (this.objectChanged) {
         this.lock.readLock().lock();

         for (DataWatcher.WatchableObject watchableObject : this.watchedObjects.values()) {
            if (watchableObject.isWatched()) {
               watchableObject.setWatched(false);
               if (arrayList == null) {
                  arrayList = Lists.newArrayList();
               }

               arrayList.add(watchableObject);
            }
         }

         this.lock.readLock().unlock();
      }

      this.objectChanged = false;
      return arrayList;
   }

   public void writeTo(PacketBuffer buffer) throws IOException {
      this.lock.readLock().lock();

      for (DataWatcher.WatchableObject watchableObject : this.watchedObjects.values()) {
         writeWatchableObjectToPacketBuffer(buffer, watchableObject);
      }

      this.lock.readLock().unlock();
      buffer.writeByte(127);
   }

   public List<DataWatcher.WatchableObject> getAllWatched() {
      ArrayList arrayList = null;
      this.lock.readLock().lock();

      for (DataWatcher.WatchableObject watchableObject : this.watchedObjects.values()) {
         if (arrayList == null) {
            arrayList = Lists.newArrayList();
         }

         arrayList.add(watchableObject);
      }

      this.lock.readLock().unlock();
      return arrayList;
   }

   private static void writeWatchableObjectToPacketBuffer(PacketBuffer buffer, DataWatcher.WatchableObject object) throws IOException {
      int i = (object.getObjectType() << 5 | object.getDataValueId() & 31) & 0xFF;
      buffer.writeByte(i);
      switch (object.getObjectType()) {
         case 0:
            buffer.writeByte((Byte)object.getObject());
            break;
         case 1:
            buffer.writeShort((Short)object.getObject());
            break;
         case 2:
            buffer.writeInt((Integer)object.getObject());
            break;
         case 3:
            buffer.writeFloat((Float)object.getObject());
            break;
         case 4:
            buffer.writeString((String)object.getObject());
            break;
         case 5:
            ItemStack itemStack = (ItemStack)object.getObject();
            buffer.writeItemStackToBuffer(itemStack);
            break;
         case 6:
            BlockPos blockPos = (BlockPos)object.getObject();
            buffer.writeInt(blockPos.getX());
            buffer.writeInt(blockPos.getY());
            buffer.writeInt(blockPos.getZ());
            break;
         case 7:
            Rotations rotations = (Rotations)object.getObject();
            buffer.writeFloat(rotations.getX());
            buffer.writeFloat(rotations.getY());
            buffer.writeFloat(rotations.getZ());
      }
   }

   public static List<DataWatcher.WatchableObject> readWatchedListFromPacketBuffer(PacketBuffer buffer) throws IOException {
      ArrayList arrayList = null;

      for (int i = buffer.readByte(); i != 127; i = buffer.readByte()) {
         if (arrayList == null) {
            arrayList = Lists.newArrayList();
         }

         int j = (i & 224) >> 5;
         int k = i & 31;
         DataWatcher.WatchableObject watchableObject = null;
         switch (j) {
            case 0:
               watchableObject = new DataWatcher.WatchableObject(j, k, buffer.readByte());
               break;
            case 1:
               watchableObject = new DataWatcher.WatchableObject(j, k, buffer.readShort());
               break;
            case 2:
               watchableObject = new DataWatcher.WatchableObject(j, k, buffer.readInt());
               break;
            case 3:
               watchableObject = new DataWatcher.WatchableObject(j, k, buffer.readFloat());
               break;
            case 4:
               watchableObject = new DataWatcher.WatchableObject(j, k, buffer.readStringFromBuffer(32767));
               break;
            case 5:
               watchableObject = new DataWatcher.WatchableObject(j, k, buffer.readItemStackFromBuffer());
               break;
            case 6:
               int l = buffer.readInt();
               int m = buffer.readInt();
               int n = buffer.readInt();
               watchableObject = new DataWatcher.WatchableObject(j, k, new BlockPos(l, m, n));
               break;
            case 7:
               float f = buffer.readFloat();
               float g = buffer.readFloat();
               float h = buffer.readFloat();
               watchableObject = new DataWatcher.WatchableObject(j, k, new Rotations(f, g, h));
         }

         arrayList.add(watchableObject);
      }

      return arrayList;
   }

   public void updateWatchedObjectsFromList(List<DataWatcher.WatchableObject> list) {
      this.lock.writeLock().lock();

      for (DataWatcher.WatchableObject watchableObject : list) {
         DataWatcher.WatchableObject watchableObject2 = this.watchedObjects.get(watchableObject.getDataValueId());
         if (watchableObject2 != null) {
            watchableObject2.setObject(watchableObject.getObject());
            this.owner.onDataWatcherUpdate(watchableObject.getDataValueId());
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

      public WatchableObject(int typeIn, int id, Object object) {
         this.dataValueId = id;
         this.watchedObject = object;
         this.objectType = typeIn;
         this.watched = true;
      }

      public int getDataValueId() {
         return this.dataValueId;
      }

      public void setObject(Object object) {
         this.watchedObject = object;
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

      public void setWatched(boolean watched) {
         this.watched = watched;
      }
   }
}
