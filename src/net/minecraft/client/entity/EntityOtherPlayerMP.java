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

   public EntityOtherPlayerMP(World worldIn, GameProfile gameProfileIn) {
      super(worldIn, gameProfileIn);
      this.stepHeight = 0.0F;
      this.noClip = true;
      this.renderOffsetY = 0.25F;
      this.renderDistanceWeight = 10.0;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return true;
   }

   @Override
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean bl) {
      this.otherPlayerMPX = x;
      this.otherPlayerMPY = y;
      this.otherPlayerMPZ = z;
      this.otherPlayerMPYaw = yaw;
      this.otherPlayerMPPitch = pitch;
      this.otherPlayerMPPosRotationIncrements = posRotationIncrements;
   }

   @Override
   public void onUpdate() {
      this.renderOffsetY = 0.0F;
      super.onUpdate();
      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d = this.posX - this.prevPosX;
      double e = this.posZ - this.prevPosZ;
      float f = MathHelper.sqrt_double(d * d + e * e) * 4.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      this.limbSwingAmount = this.limbSwingAmount + (f - this.limbSwingAmount) * 0.4F;
      this.limbSwing = this.limbSwing + this.limbSwingAmount;
      if (!this.isItemInUse && this.isEating() && this.inventory.mainInventory[this.inventory.currentItem] != null) {
         ItemStack itemStack = this.inventory.mainInventory[this.inventory.currentItem];
         this.setItemInUse(this.inventory.mainInventory[this.inventory.currentItem], itemStack.getItem().getMaxItemUseDuration(itemStack));
         this.isItemInUse = true;
      } else if (this.isItemInUse && !this.isEating()) {
         this.clearItemInUse();
         this.isItemInUse = false;
      }
   }

   @Override
   public void onLivingUpdate() {
      if (this.otherPlayerMPPosRotationIncrements > 0) {
         double d = this.posX + (this.otherPlayerMPX - this.posX) / this.otherPlayerMPPosRotationIncrements;
         double e = this.posY + (this.otherPlayerMPY - this.posY) / this.otherPlayerMPPosRotationIncrements;
         double d2 = this.posZ + (this.otherPlayerMPZ - this.posZ) / this.otherPlayerMPPosRotationIncrements;
         double d3 = this.otherPlayerMPYaw - this.rotationYaw;

         while (d3 < -180.0) {
            d3 += 360.0;
         }

         while (d3 >= 180.0) {
            d3 -= 360.0;
         }

         this.rotationYaw = (float)(this.rotationYaw + d3 / this.otherPlayerMPPosRotationIncrements);
         this.rotationPitch = (float)(this.rotationPitch + (this.otherPlayerMPPitch - this.rotationPitch) / this.otherPlayerMPPosRotationIncrements);
         this.otherPlayerMPPosRotationIncrements--;
         this.setPosition(d, e, d2);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      }

      this.prevCameraYaw = this.cameraYaw;
      this.updateArmSwingProgress();
      float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      float g = (float)Math.atan(-this.motionY * 0.2F) * 15.0F;
      if (f > 0.1F) {
         f = 0.1F;
      }

      if (!this.onGround || this.getHealth() <= 0.0F) {
         f = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         g = 0.0F;
      }

      this.cameraYaw = this.cameraYaw + (f - this.cameraYaw) * 0.4F;
      this.cameraPitch = this.cameraPitch + (g - this.cameraPitch) * 0.8F;
   }

   @Override
   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
      if (slotIn == 0) {
         this.inventory.mainInventory[this.inventory.currentItem] = stack;
      } else {
         this.inventory.armorInventory[slotIn - 1] = stack;
      }
   }

   @Override
   public void addChatMessage(IChatComponent component) {
      Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(component);
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      return false;
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
   }
}
