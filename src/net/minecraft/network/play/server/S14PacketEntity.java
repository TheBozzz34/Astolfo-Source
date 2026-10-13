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
   private static final long a = 138393640637085L;

   public S14PacketEntity() {
   }

   public S14PacketEntity(int var1) {
      this.entityId = var1;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.entityId = var1.readVarIntFromBuffer();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeVarIntToBuffer(this.entityId);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleEntityMovement(this);
   }

   @Override
   public String toString() {
      return "Entity_" + super.toString();
   }

   public Entity getEntity(World var1) {
      return var1.getEntityByID(this.entityId);
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

      public S15PacketEntityRelMove(int var1, byte var2, byte var3, byte var4, boolean var5) {
         super(var1);
         this.posX = var2;
         this.posY = var3;
         this.posZ = var4;
         this.C = var5;
         this.E = true;
      }

      @Override
      public void readPacketData(PacketBuffer var1) throws IOException {
         super.readPacketData(var1);
         this.posX = var1.readByte();
         this.posY = var1.readByte();
         this.posZ = var1.readByte();
         this.C = var1.readBoolean();
      }

      @Override
      public void writePacketData(PacketBuffer var1) throws IOException {
         super.writePacketData(var1);
         var1.writeByte(this.posX);
         var1.writeByte(this.posY);
         var1.writeByte(this.posZ);
         var1.writeBoolean(this.C);
      }
   }

   public static class S16PacketEntityLook extends S14PacketEntity {
      public S16PacketEntityLook() {
         this.D = true;
      }

      public S16PacketEntityLook(int var1, byte var2, byte var3, boolean var4) {
         super(var1);
         this.yaw = var2;
         this.pitch = var3;
         this.D = true;
         this.C = var4;
      }

      @Override
      public void readPacketData(PacketBuffer var1) throws IOException {
         super.readPacketData(var1);
         this.yaw = var1.readByte();
         this.pitch = var1.readByte();
         this.C = var1.readBoolean();
      }

      @Override
      public void writePacketData(PacketBuffer var1) throws IOException {
         super.writePacketData(var1);
         var1.writeByte(this.yaw);
         var1.writeByte(this.pitch);
         var1.writeBoolean(this.C);
      }
   }

   public static class S17PacketEntityLookMove extends S14PacketEntity {
      public S17PacketEntityLookMove() {
         this.D = true;
      }

      public S17PacketEntityLookMove(int var1, byte var2, byte var3, byte var4, byte var5, byte var6, boolean var7) {
         super(var1);
         this.posX = var2;
         this.posY = var3;
         this.posZ = var4;
         this.yaw = var5;
         this.pitch = var6;
         this.C = var7;
         this.D = true;
         this.E = true;
      }

      @Override
      public void readPacketData(PacketBuffer var1) throws IOException {
         super.readPacketData(var1);
         this.posX = var1.readByte();
         this.posY = var1.readByte();
         this.posZ = var1.readByte();
         this.yaw = var1.readByte();
         this.pitch = var1.readByte();
         this.C = var1.readBoolean();
      }

      @Override
      public void writePacketData(PacketBuffer var1) throws IOException {
         super.writePacketData(var1);
         var1.writeByte(this.posX);
         var1.writeByte(this.posY);
         var1.writeByte(this.posZ);
         var1.writeByte(this.yaw);
         var1.writeByte(this.pitch);
         var1.writeBoolean(this.C);
      }
   }
}
