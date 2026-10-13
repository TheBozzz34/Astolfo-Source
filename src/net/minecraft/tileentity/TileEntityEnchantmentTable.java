package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IInteractionObject;

public class TileEntityEnchantmentTable extends TileEntity implements ITickable, IInteractionObject {
   public int tickCount;
   public float pageFlip;
   public float pageFlipPrev;
   public float field_145932_k;
   public float field_145929_l;
   public float bookSpread;
   public float bookSpreadPrev;
   public float bookRotation;
   public float bookRotationPrev;
   public float field_145924_q;
   private static Random rand = new Random();
   private String customName;
   private static final long a = 1091837686886L;

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);
      if (this.hasCustomName()) {
         var1.setString("CustomName", this.customName);
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      super.readFromNBT(var1);
      if (var1.hasKey("CustomName", 8)) {
         this.customName = var1.getString("CustomName");
      }
   }

   @Override
   public void update() {
      this.bookSpreadPrev = this.bookSpread;
      this.bookRotationPrev = this.bookRotation;
      EntityPlayer var5 = this.worldObj.getClosestPlayer(this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F, 3.0);
      if (var5 != null) {
         double var6 = var5.posX - (this.pos.getX() + 0.5F);
         double var8 = var5.posZ - (this.pos.getZ() + 0.5F);
         this.field_145924_q = (float)MathHelper.atan2(var8, var6);
         this.bookSpread += 0.1F;
         if (this.bookSpread < 0.5F || rand.nextInt(40) == 0) {
            float var10 = this.field_145932_k;

            do {
               this.field_145932_k = this.field_145932_k + (rand.nextInt(4) - rand.nextInt(4));
            } while (var10 == this.field_145932_k);
         }
      } else {
         this.field_145924_q += 0.02F;
         this.bookSpread -= 0.1F;
      }

      while (this.bookRotation >= (float) Math.PI) {
         this.bookRotation -= (float) (Math.PI * 2);
      }

      while (this.bookRotation < (float) -Math.PI) {
         this.bookRotation += (float) (Math.PI * 2);
      }

      while (this.field_145924_q >= (float) Math.PI) {
         this.field_145924_q -= (float) (Math.PI * 2);
      }

      while (this.field_145924_q < (float) -Math.PI) {
         this.field_145924_q += (float) (Math.PI * 2);
      }

      float var11 = this.field_145924_q - this.bookRotation;

      while (var11 >= (float) Math.PI) {
         var11 -= (float) (Math.PI * 2);
      }

      while (var11 < (float) -Math.PI) {
         var11 += (float) (Math.PI * 2);
      }

      this.bookRotation += var11 * 0.4F;
      this.bookSpread = MathHelper.clamp_float(this.bookSpread, 0.0F, 1.0F);
      this.tickCount++;
      this.pageFlipPrev = this.pageFlip;
      float var7 = (this.field_145932_k - this.pageFlip) * 0.4F;
      float var13 = 0.2F;
      var7 = MathHelper.clamp_float(var7, -var13, var13);
      this.field_145929_l = this.field_145929_l + (var7 - this.field_145929_l) * 0.9F;
      this.pageFlip = this.pageFlip + this.field_145929_l;
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.enchant";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setCustomName(String var1) {
      this.customName = var1;
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerEnchantment(var1, this.worldObj, this.pos);
   }

   @Override
   public String getGuiID() {
      return "minecraft:enchanting_table";
   }
}
