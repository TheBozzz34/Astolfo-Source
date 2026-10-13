package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S39PacketPlayerAbilities implements Packet<INetHandlerPlayClient> {
   private boolean invulnerable;
   private boolean flying;
   private boolean allowFlying;
   private boolean creativeMode;
   private float flySpeed;
   private float walkSpeed;
   private static final long a = 114054864197643L;

   public S39PacketPlayerAbilities() {
   }

   public S39PacketPlayerAbilities(PlayerCapabilities var1) {
      this.setInvulnerable(var1.disableDamage);
      this.setFlying(var1.isFlying);
      this.setAllowFlying(var1.allowFlying);
      this.setCreativeMode(var1.isCreativeMode);
      this.setFlySpeed(var1.getFlySpeed());
      this.setWalkSpeed(var1.getWalkSpeed());
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      byte var5 = var1.readByte();
      this.setInvulnerable((var5 & 1) > 0);
      this.setFlying((var5 & 2) > 0);
      this.setAllowFlying((var5 & 4) > 0);
      this.setCreativeMode((var5 & 8) > 0);
      this.setFlySpeed(var1.readFloat());
      this.setWalkSpeed(var1.readFloat());
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      byte var5 = 0;
      if (this.isInvulnerable()) {
         var5 = (byte)(var5 | 1);
      }

      if (this.isFlying()) {
         var5 = (byte)(var5 | 2);
      }

      if (this.isAllowFlying()) {
         var5 = (byte)(var5 | 4);
      }

      if (this.isCreativeMode()) {
         var5 = (byte)(var5 | 8);
      }

      var1.writeByte(var5);
      var1.writeFloat(this.flySpeed);
      var1.writeFloat(this.walkSpeed);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handlePlayerAbilities(this);
   }

   public boolean isInvulnerable() {
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

   public boolean isAllowFlying() {
      return this.allowFlying;
   }

   public void setAllowFlying(boolean var1) {
      this.allowFlying = var1;
   }

   public boolean isCreativeMode() {
      return this.creativeMode;
   }

   public void setCreativeMode(boolean var1) {
      this.creativeMode = var1;
   }

   public float getFlySpeed() {
      return this.flySpeed;
   }

   public void setFlySpeed(float var1) {
      this.flySpeed = var1;
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   public void setWalkSpeed(float var1) {
      this.walkSpeed = var1;
   }
}
