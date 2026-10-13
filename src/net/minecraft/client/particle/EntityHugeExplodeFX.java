package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityHugeExplodeFX extends EntityFX {
   private int timeSinceStart;
   private int maximumTime = 8;
   private static final long ab = 67215327749180L;

   public EntityHugeExplodeFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
   }

   @Override
   public void renderParticle(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
   }

   @Override
   public void onUpdate() {
      for (int var4 = 0; var4 < 6; var4++) {
         double var5 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         double var7 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         double var9 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
         this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, var5, var7, var9, (float)this.timeSinceStart / this.maximumTime, 0.0, 0.0);
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
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityHugeExplodeFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
