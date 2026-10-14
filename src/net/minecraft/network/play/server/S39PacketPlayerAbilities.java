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

   public S39PacketPlayerAbilities() {
   }

   public S39PacketPlayerAbilities(PlayerCapabilities capabilities) {
      this.setInvulnerable(capabilities.disableDamage);
      this.setFlying(capabilities.isFlying);
      this.setAllowFlying(capabilities.allowFlying);
      this.setCreativeMode(capabilities.isCreativeMode);
      this.setFlySpeed(capabilities.getFlySpeed());
      this.setWalkSpeed(capabilities.getWalkSpeed());
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      byte b = buf.readByte();
      this.setInvulnerable((b & 1) > 0);
      this.setFlying((b & 2) > 0);
      this.setAllowFlying((b & 4) > 0);
      this.setCreativeMode((b & 8) > 0);
      this.setFlySpeed(buf.readFloat());
      this.setWalkSpeed(buf.readFloat());
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      byte b = 0;
      if (this.isInvulnerable()) {
         b = (byte)(b | 1);
      }

      if (this.isFlying()) {
         b = (byte)(b | 2);
      }

      if (this.isAllowFlying()) {
         b = (byte)(b | 4);
      }

      if (this.isCreativeMode()) {
         b = (byte)(b | 8);
      }

      buf.writeByte(b);
      buf.writeFloat(this.flySpeed);
      buf.writeFloat(this.walkSpeed);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handlePlayerAbilities(this);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean isInvulnerable) {
      this.invulnerable = isInvulnerable;
   }

   public boolean isFlying() {
      return this.flying;
   }

   public void setFlying(boolean isFlying) {
      this.flying = isFlying;
   }

   public boolean isAllowFlying() {
      return this.allowFlying;
   }

   public void setAllowFlying(boolean isAllowFlying) {
      this.allowFlying = isAllowFlying;
   }

   public boolean isCreativeMode() {
      return this.creativeMode;
   }

   public void setCreativeMode(boolean isCreativeMode) {
      this.creativeMode = isCreativeMode;
   }

   public float getFlySpeed() {
      return this.flySpeed;
   }

   public void setFlySpeed(float flySpeedIn) {
      this.flySpeed = flySpeedIn;
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   public void setWalkSpeed(float walkSpeedIn) {
      this.walkSpeed = walkSpeedIn;
   }
}
