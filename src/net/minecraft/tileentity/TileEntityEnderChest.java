package net.minecraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ITickable;

public class TileEntityEnderChest extends TileEntity implements ITickable {
   public float lidAngle;
   public float prevLidAngle;
   public int numPlayersUsing;
   private int ticksSinceSync;

   @Override
   public void update() {
      if (++this.ticksSinceSync % 20 * 4 == 0) {
         this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
      }

      this.prevLidAngle = this.lidAngle;
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      float f = 0.1F;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
         double d = i + 0.5;
         double e = k + 0.5;
         this.worldObj.playSoundEffect(d, j + 0.5, e, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float g = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += f;
         } else {
            this.lidAngle -= f;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float h = 0.5F;
         if (this.lidAngle < h && g >= h) {
            double d2 = i + 0.5;
            double d3 = k + 0.5;
            this.worldObj.playSoundEffect(d2, j + 0.5, d3, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }
   }

   @Override
   public boolean receiveClientEvent(int id, int typeIn) {
      if (id == 1) {
         this.numPlayersUsing = typeIn;
         return true;
      } else {
         return super.receiveClientEvent(id, typeIn);
      }
   }

   @Override
   public void invalidate() {
      this.updateContainingBlockInfo();
      super.invalidate();
   }

   public void A() {
      this.numPlayersUsing++;
      this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
   }

   public void C() {
      this.numPlayersUsing--;
      this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
   }

   public boolean canBeUsed(EntityPlayer entityPlayer) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : entityPlayer.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }
}
