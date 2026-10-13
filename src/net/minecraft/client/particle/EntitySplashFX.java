package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntitySplashFX extends EntityRainFX {
   private static final long cb = 37142407898527L;

   public EntitySplashFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6);
      this.particleGravity = 0.04F;
      this.nextTextureIndexX();
      if (var10 == 0.0 && (var8 != 0.0 || var12 != 0.0)) {
         this.motionX = var8;
         this.motionY = var10 + 0.1;
         this.motionZ = var12;
      }
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntitySplashFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
