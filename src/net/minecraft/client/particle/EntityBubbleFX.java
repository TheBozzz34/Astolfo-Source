package net.minecraft.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityBubbleFX extends EntityFX {
   private static final long ab = 81809813968949L;
   private static final long cb = 3945149631444287520L;

   public EntityBubbleFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.particleRed = 1.0F;
      this.particleGreen = 1.0F;
      this.particleBlue = 1.0F;
      this.setParticleTextureIndex((int)cb);
      this.setSize(0.02F, 0.02F);
      this.particleScale = this.particleScale * (this.rand.nextFloat() * 0.6F + 0.2F);
      this.motionX = var8 * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
      this.motionY = var10 * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
      this.motionZ = var12 * 0.2F + (Math.random() * 2.0 - 1.0) * 0.02F;
      this.particleMaxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY += 0.002;
      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.85F;
      this.motionY *= 0.85F;
      this.motionZ *= 0.85F;
      if (this.worldObj.getBlockState(new BlockPos(this)).getBlock().getMaterial() != Material.water) {
         this.setDead();
      }

      if (this.particleMaxAge-- <= 0) {
         this.setDead();
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityBubbleFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
