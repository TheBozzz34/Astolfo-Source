package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C13PacketPlayerAbilities implements Packet<INetHandlerPlayServer> {
   private boolean invulnerable;
   private boolean flying;
   private boolean allowFlying;
   private boolean creativeMode;
   private float flySpeed;
   private float walkSpeed;
   private static final long a = 136571740783073L;

   public C13PacketPlayerAbilities() {
   }

   public C13PacketPlayerAbilities(PlayerCapabilities var1) {
      this.setInvulnerable(var1.disableDamage);
      this.setFlying(var1.isFlying);
      this.setAllowFlying(var1.allowFlying);
      this.setCreativeMode(var1.isCreativeMode);
      this.setFlySpeed(var1.getFlySpeed());
      this.setWalkSpeed(var1.getWalkSpeed());
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      byte var6 = var1.readByte();
      this.setInvulnerable((var6 & 1) > 0);
      this.setFlying((var6 & 2) > 0);
      this.setAllowFlying((var6 & 4) > 0);
      this.setCreativeMode((var6 & 8) > 0);
      this.setFlySpeed(var1.readFloat());
      this.setWalkSpeed(var1.readFloat());
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      byte var5 = 0;
      if (this.o()) {
         var5 = (byte)(var5 | 1);
      }

      if (this.isFlying()) {
         var5 = (byte)(var5 | 2);
      }

      if (this.H()) {
         var5 = (byte)(var5 | 4);
      }

      if (this.D()) {
         var5 = (byte)(var5 | 8);
      }

      var1.writeByte(var5);
      var1.writeFloat(this.flySpeed);
      var1.writeFloat(this.walkSpeed);
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processPlayerAbilities(this);
   }

   public boolean o() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean var1) {
      this.invulnerable = var1;
   }

   public boolean isFlying() {
      return this.flying;
   }

   public void setFlying(boolean var1) {
      this.flying = var1;
   }

   public float Q() {
      return this.flySpeed;
   }

   public boolean H() {
      return this.allowFlying;
   }

   public void setAllowFlying(boolean var1) {
      this.allowFlying = var1;
   }

   public boolean D() {
      return this.creativeMode;
   }

   public void setCreativeMode(boolean var1) {
      this.creativeMode = var1;
   }

   public void setFlySpeed(float var1) {
      this.flySpeed = var1;
   }

   public void setWalkSpeed(float var1) {
      this.walkSpeed = var1;
   }
}
