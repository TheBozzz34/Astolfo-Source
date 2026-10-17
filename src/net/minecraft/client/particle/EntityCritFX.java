package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityCritFX extends EntitySmokeFX {
   public EntityCritFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double e, double d2) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, d, e, d2, 2.5F);
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityCritFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }
}
