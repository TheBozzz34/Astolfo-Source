package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityOtherPlayerMP extends AbstractClientPlayer {
   private boolean isItemInUse;
   private int otherPlayerMPPosRotationIncrements;
   private double otherPlayerMPX;
   private double otherPlayerMPY;
   private double otherPlayerMPZ;
   private double otherPlayerMPYaw;
   private double otherPlayerMPPitch;
   private static final long bb = 116492934793263L;

   public EntityOtherPlayerMP(World var1, GameProfile var2) {
      super(var1, var2);
      this.stepHeight = 0.0F;
      this.noClip = true;
      this.renderOffsetY = 0.25F;
      this.renderDistanceWeight = 10.0;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      return true;
   }

   @Override
   public void setPositionAndRotation2(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.otherPlayerMPX = var1;
      this.otherPlayerMPY = var3;
      this.otherPlayerMPZ = var5;
      this.otherPlayerMPYaw = var7;
      this.otherPlayerMPPitch = var8;
      this.otherPlayerMPPosRotationIncrements = var9;
   }

   @Override
   public void onUpdate() {
      this.renderOffsetY = 0.0F;
      super.onUpdate();
      this.prevLimbSwingAmount = this.limbSwingAmount;
      double var4 = this.posX - this.prevPosX;
      double var6 = this.posZ - this.prevPosZ;
      float var8 = MathHelper.sqrt_double(var4 * var4 + var6 * var6) * 4.0F;
      if (var8 > 1.0F) {
         var8 = 1.0F;
      }

      this.limbSwingAmount = this.limbSwingAmount + (var8 - this.limbSwingAmount) * 0.4F;
      this.limbSwing = this.limbSwing + this.limbSwingAmount;
      if (!this.isItemInUse && this.m() && this.inventory.mainInventory[this.inventory.currentItem] != null) {
         ItemStack var9 = this.inventory.mainInventory[this.inventory.currentItem];
         this.setItemInUse(this.inventory.mainInventory[this.inventory.currentItem], var9.getItem().getMaxItemUseDuration(var9));
         this.isItemInUse = true;
      } else if (this.isItemInUse && !this.m()) {
         this.clearItemInUse();
         this.isItemInUse = false;
      }
   }

   @Override
   public void onLivingUpdate() {
      if (this.otherPlayerMPPosRotationIncrements > 0) {
         double var4 = this.posX + (this.otherPlayerMPX - this.posX) / this.otherPlayerMPPosRotationIncrements;
         double var6 = this.posY + (this.otherPlayerMPY - this.posY) / this.otherPlayerMPPosRotationIncrements;
         double var8 = this.posZ + (this.otherPlayerMPZ - this.posZ) / this.otherPlayerMPPosRotationIncrements;
         double var10 = this.otherPlayerMPYaw - this.rotationYaw;

         while (var10 < -180.0) {
            var10 += 360.0;
         }

         while (var10 >= 180.0) {
            var10 -= 360.0;
         }

         this.rotationYaw = (float)(this.rotationYaw + var10 / this.otherPlayerMPPosRotationIncrements);
         this.rotationPitch = (float)(this.rotationPitch + (this.otherPlayerMPPitch - this.rotationPitch) / this.otherPlayerMPPosRotationIncrements);
         this.otherPlayerMPPosRotationIncrements--;
         this.setPosition(var4, var6, var8);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      }

      this.prevCameraYaw = this.cameraYaw;
      this.L();
      float var12 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      float var5 = (float)Math.atan(-this.motionY * 0.2F) * 15.0F;
      if (var12 > 0.1F) {
         var12 = 0.1F;
      }

      if (!this.onGround || this.getHealth() <= 0.0F) {
         var12 = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         var5 = 0.0F;
      }

      this.cameraYaw = this.cameraYaw + (var12 - this.cameraYaw) * 0.4F;
      this.cameraPitch = this.cameraPitch + (var5 - this.cameraPitch) * 0.8F;
   }

   @Override
   public void setCurrentItemOrArmor(int var1, ItemStack var2) {
      if (var1 == 0) {
         this.inventory.mainInventory[this.inventory.currentItem] = var2;
      } else {
         this.inventory.armorInventory[var1 - 1] = var2;
      }
   }

   @Override
   public void addChatMessage(IChatComponent var1) {
      Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(var1);
   }

   @Override
   public boolean canCommandSenderUseCommand(int var1, String var2) {
      return false;
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
   }
}
