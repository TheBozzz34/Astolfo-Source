package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S12PacketEntityVelocity implements Packet<INetHandlerPlayClient> {
   private int entityID;
   public int motionX;
   public int motionY;
   public int motionZ;
   private static final long a = 100064723962836L;

   public S12PacketEntityVelocity() {
   }

   public S12PacketEntityVelocity(Entity var1) {
      this(var1.getEntityId(), var1.motionX, var1.motionY, var1.motionZ);
   }

   public S12PacketEntityVelocity(int var1, double var2, double var4, double var6) {
      this.entityID = var1;
      double var11 = 3.9;
      if (var2 < -var11) {
         var2 = -var11;
      }

      if (var4 < -var11) {
         var4 = -var11;
      }

      if (var6 < -var11) {
         var6 = -var11;
      }

      if (var2 > var11) {
         var2 = var11;
      }

      if (var4 > var11) {
         var4 = var11;
      }

      if (var6 > var11) {
         var6 = var11;
      }

      this.motionX = (int)(var2 * 8000.0);
      this.motionY = (int)(var4 * 8000.0);
      this.motionZ = (int)(var6 * 8000.0);
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.entityID = var1.readVarIntFromBuffer();
      this.motionX = var1.readShort();
      this.motionY = var1.readShort();
      this.motionZ = var1.readShort();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeVarIntToBuffer(this.entityID);
      var1.writeShort(this.motionX);
      var1.writeShort(this.motionY);
      var1.writeShort(this.motionZ);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleEntityVelocity(this);
   }

   public int getEntityID() {
      return this.entityID;
   }

   public int getMotionX() {
      return this.motionX;
   }

   public int getMotionY() {
      return this.motionY;
   }

   public int getMotionZ() {
      return this.motionZ;
   }
}
