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

   public void writeCapabilitiesToNBT(NBTTagCompound var1) {
      NBTTagCompound var4 = new NBTTagCompound();
      var4.setBoolean("invulnerable", this.disableDamage);
      var4.setBoolean("flying", this.isFlying);
      var4.setBoolean("mayfly", this.allowFlying);
      var4.setBoolean("instabuild", this.isCreativeMode);
      var4.setBoolean("mayBuild", this.allowEdit);
      var4.setFloat("flySpeed", this.flySpeed);
      var4.setFloat("walkSpeed", this.walkSpeed);
      var1.setTag("abilities", var4);
   }

   public void readCapabilitiesFromNBT(NBTTagCompound var1) {
      if (var1.hasKey("abilities", 10)) {
         NBTTagCompound var5 = var1.getCompoundTag("abilities");
         this.disableDamage = var5.getBoolean("invulnerable");
         this.isFlying = var5.getBoolean("flying");
         this.allowFlying = var5.getBoolean("mayfly");
         this.isCreativeMode = var5.getBoolean("instabuild");
         if (var5.hasKey("flySpeed", 99)) {
            this.flySpeed = var5.getFloat("flySpeed");
            this.walkSpeed = var5.getFloat("walkSpeed");
         }

         if (var5.hasKey("mayBuild", 1)) {
            this.allowEdit = var5.getBoolean("mayBuild");
         }
      }
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

   public void setPlayerWalkSpeed(float var1) {
      this.walkSpeed = var1;
   }
}
