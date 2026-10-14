package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S14PacketEntity implements Packet<INetHandlerPlayClient> {
   public int entityId;
   public byte posX;
   public byte posY;
   public byte posZ;
   public byte yaw;
   public byte pitch;
   public boolean C;
   public boolean D;
   public boolean E;

   public S14PacketEntity() {
   }

   public S14PacketEntity(int entityIdIn) {
      this.entityId = entityIdIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityMovement(this);
   }

   @Override
   public String toString() {
      return "Entity_" + super.toString();
   }

   public Entity getEntity(World worldIn) {
      return worldIn.getEntityByID(this.entityId);
   }

   public byte func_149062_c() {
      return this.posX;
   }

   public byte func_149061_d() {
      return this.posY;
   }

   public byte func_149064_e() {
      return this.posZ;
   }

   public byte func_149066_f() {
      return this.yaw;
   }

   public byte func_149063_g() {
      return this.pitch;
   }

   public boolean func_149060_h() {
      return this.D;
   }

   public boolean t() {
      return this.E;
   }

   public boolean getOnGround() {
      return this.C;
   }

   public static class S15PacketEntityRelMove extends S14PacketEntity {
      public S15PacketEntityRelMove() {
      }

      public S15PacketEntityRelMove(int entityIdIn, byte x, byte y, byte z, boolean onGroundIn) {
         super(entityIdIn);
         this.posX = x;
         this.posY = y;
         this.posZ = z;
         this.C = onGroundIn;
         this.E = true;
      }

      @Override
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.posX = buf.readByte();
         this.posY = buf.readByte();
         this.posZ = buf.readByte();
         this.C = buf.readBoolean();
      }

      @Override
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeByte(this.posX);
         buf.writeByte(this.posY);
         buf.writeByte(this.posZ);
         buf.writeBoolean(this.C);
      }
   }

   public static class S16PacketEntityLook extends S14PacketEntity {
      public S16PacketEntityLook() {
         this.D = true;
      }

      public S16PacketEntityLook(int entityIdIn, byte yawIn, byte pitchIn, boolean onGroundIn) {
         super(entityIdIn);
         this.yaw = yawIn;
         this.pitch = pitchIn;
         this.D = true;
         this.C = onGroundIn;
      }

      @Override
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.yaw = buf.readByte();
         this.pitch = buf.readByte();
         this.C = buf.readBoolean();
      }

      @Override
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeByte(this.yaw);
         buf.writeByte(this.pitch);
         buf.writeBoolean(this.C);
      }
   }

   public static class S17PacketEntityLookMove extends S14PacketEntity {
      public S17PacketEntityLookMove() {
         this.D = true;
      }

      public S17PacketEntityLookMove(int i, byte b, byte b2, byte b3, byte b4, byte b5, boolean bl) {
         super(i);
         this.posX = b;
         this.posY = b2;
         this.posZ = b3;
         this.yaw = b4;
         this.pitch = b5;
         this.C = bl;
         this.D = true;
         this.E = true;
      }

      @Override
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.posX = buf.readByte();
         this.posY = buf.readByte();
         this.posZ = buf.readByte();
         this.yaw = buf.readByte();
         this.pitch = buf.readByte();
         this.C = buf.readBoolean();
      }

      @Override
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeByte(this.posX);
         buf.writeByte(this.posY);
         buf.writeByte(this.posZ);
         buf.writeByte(this.yaw);
         buf.writeByte(this.pitch);
         buf.writeBoolean(this.C);
      }
   }
}
