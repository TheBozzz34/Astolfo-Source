package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0FPacketSpawnMob implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private int type;
   private int x;
   private int y;
   private int z;
   private int velocityX;
   private int velocityY;
   private int velocityZ;
   private byte yaw;
   private byte pitch;
   private byte headPitch;
   private DataWatcher field_149043_l;
   private List<DataWatcher.WatchableObject> watcher;
   private static final long a = 75819819801177L;

   public S0FPacketSpawnMob() {
   }

   public S0FPacketSpawnMob(EntityLivingBase var1) {
      this.entityId = var1.getEntityId();
      this.type = (byte)EntityList.getEntityID(var1);
      this.x = MathHelper.floor_double(var1.posX * 32.0);
      this.y = MathHelper.floor_double(var1.posY * 32.0);
      this.z = MathHelper.floor_double(var1.posZ * 32.0);
      this.yaw = (byte)(var1.rotationYaw * 256.0F / 360.0F);
      this.pitch = (byte)(var1.rotationPitch * 256.0F / 360.0F);
      this.headPitch = (byte)(var1.rotationYawHead * 256.0F / 360.0F);
      double var6 = 3.9;
      double var8 = var1.motionX;
      double var10 = var1.motionY;
      double var12 = var1.motionZ;
      if (var8 < -var6) {
         var8 = -var6;
      }

      if (var10 < -var6) {
         var10 = -var6;
      }

      if (var12 < -var6) {
         var12 = -var6;
      }

      if (var8 > var6) {
         var8 = var6;
      }

      if (var10 > var6) {
         var10 = var6;
      }

      if (var12 > var6) {
         var12 = var6;
      }

      this.velocityX = (int)(var8 * 8000.0);
      this.velocityY = (int)(var10 * 8000.0);
      this.velocityZ = (int)(var12 * 8000.0);
      this.field_149043_l = var1.getDataWatcher();
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.entityId = var1.readVarIntFromBuffer();
      this.type = var1.readByte() & 255;
      this.x = var1.readInt();
      this.y = var1.readInt();
      this.z = var1.readInt();
      this.yaw = var1.readByte();
      this.pitch = var1.readByte();
      this.headPitch = var1.readByte();
      this.velocityX = var1.readShort();
      this.velocityY = var1.readShort();
      this.velocityZ = var1.readShort();
      this.watcher = DataWatcher.readWatchedListFromPacketBuffer(var1);
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeVarIntToBuffer(this.entityId);
      var1.writeByte(this.type & 0xFF);
      var1.writeInt(this.x);
      var1.writeInt(this.y);
      var1.writeInt(this.z);
      var1.writeByte(this.yaw);
      var1.writeByte(this.pitch);
      var1.writeByte(this.headPitch);
      var1.writeShort(this.velocityX);
      var1.writeShort(this.velocityY);
      var1.writeShort(this.velocityZ);
      this.field_149043_l.writeTo(var1);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleSpawnMob(this);
   }

   public List<DataWatcher.WatchableObject> func_149027_c() {
      if (this.watcher == null && this.field_149043_l != null) {
         this.watcher = this.field_149043_l.getAllWatched();
      }

      return this.watcher;
   }

   public int getEntityID() {
      return this.entityId;
   }

   public int getEntityType() {
      return this.type;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public int getVelocityX() {
      return this.velocityX;
   }

   public int getVelocityY() {
      return this.velocityY;
   }

   public int getVelocityZ() {
      return this.velocityZ;
   }

   public byte getYaw() {
      return this.yaw;
   }

   public byte getPitch() {
      return this.pitch;
   }

   public byte getHeadPitch() {
      return this.headPitch;
   }
}
