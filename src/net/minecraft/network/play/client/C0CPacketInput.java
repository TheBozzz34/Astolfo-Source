package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0CPacketInput implements Packet<INetHandlerPlayServer> {
   private float strafeSpeed;
   private float forwardSpeed;
   private boolean jumping;
   private boolean sneaking;
   private static final long a = 12547239050296L;

   public C0CPacketInput() {
   }

   public C0CPacketInput(float var1, float var2, boolean var3, boolean var4) {
      this.strafeSpeed = var1;
      this.forwardSpeed = var2;
      this.jumping = var3;
      this.sneaking = var4;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.strafeSpeed = var1.readFloat();
      this.forwardSpeed = var1.readFloat();
      byte var5 = var1.readByte();
      this.jumping = (var5 & 1) > 0;
      this.sneaking = (var5 & 2) > 0;
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeFloat(this.strafeSpeed);
      var1.writeFloat(this.forwardSpeed);
      byte var5 = 0;
      if (this.jumping) {
         var5 = (byte)(var5 | 1);
      }

      if (this.sneaking) {
         var5 = (byte)(var5 | 2);
      }

      var1.writeByte(var5);
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processInput(this);
   }

   public float getStrafeSpeed() {
      return this.strafeSpeed;
   }

   public float getForwardSpeed() {
      return this.forwardSpeed;
   }

   public boolean isJumping() {
      return this.jumping;
   }

   public void d(boolean var1) {
      this.sneaking = var1;
   }

   public boolean isSneaking() {
      return this.sneaking;
   }
}
