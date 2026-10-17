package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityHugeExplodeFX extends EntityFX {
   private int timeSinceStart;
   private int maximumTime = 8;

   public EntityHugeExplodeFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double e, double d2) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0, 0.0, 0.0);
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float rotationX,
      float rotationZ,
      float rotationYZ,
      float rotationXY,
      float rotationXZ
   ) {
   }

   @Override
   public void onUpdate() {
      for (int i = 0; i < 6; i++) {
         double d = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         double e = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         double d2 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d, e, d2, (float)this.timeSinceStart / this.maximumTime, 0.0, 0.0);
      }

      this.timeSinceStart++;
      if (this.timeSinceStart == this.maximumTime) {
         this.setDead();
      }
   }

   @Override
   public int getFXLayer() {
      return 1;
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityHugeExplodeFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }
}
