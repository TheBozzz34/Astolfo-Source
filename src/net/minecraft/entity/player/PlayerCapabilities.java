package net.minecraft.entity.player;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities {
   public boolean disableDamage;
   public boolean isFlying;
   public boolean allowFlying;
   public boolean isCreativeMode;
   public boolean allowEdit = true;
   private float flySpeed = 0.05F;
   private float walkSpeed = 0.1F;
   private static final long a = 93822400042065L;

   public void writeCapabilitiesToNBT(NBTTagCompound tagCompound) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      nbtTagCompound.setBoolean("invulnerable", this.disableDamage);
      nbtTagCompound.setBoolean("flying", this.isFlying);
      nbtTagCompound.setBoolean("mayfly", this.allowFlying);
      nbtTagCompound.setBoolean("instabuild", this.isCreativeMode);
      nbtTagCompound.setBoolean("mayBuild", this.allowEdit);
      nbtTagCompound.setFloat("flySpeed", this.flySpeed);
      nbtTagCompound.setFloat("walkSpeed", this.walkSpeed);
      tagCompound.setTag("abilities", nbtTagCompound);
   }

   public void readCapabilitiesFromNBT(NBTTagCompound tagCompound) {
      if (tagCompound.hasKey("abilities", 10)) {
         NBTTagCompound nbtTagCompound = tagCompound.getCompoundTag("abilities");
         this.disableDamage = nbtTagCompound.getBoolean("invulnerable");
         this.isFlying = nbtTagCompound.getBoolean("flying");
         this.allowFlying = nbtTagCompound.getBoolean("mayfly");
         this.isCreativeMode = nbtTagCompound.getBoolean("instabuild");
         if (nbtTagCompound.hasKey("flySpeed", 99)) {
            this.flySpeed = nbtTagCompound.getFloat("flySpeed");
            this.walkSpeed = nbtTagCompound.getFloat("walkSpeed");
         }

         if (nbtTagCompound.hasKey("mayBuild", 1)) {
            this.allowEdit = nbtTagCompound.getBoolean("mayBuild");
         }
      }
   }

   public float getFlySpeed() {
      return this.flySpeed;
   }

   public void setFlySpeed(float speed) {
      this.flySpeed = speed;
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   public void setPlayerWalkSpeed(float speed) {
      this.walkSpeed = speed;
   }
}
