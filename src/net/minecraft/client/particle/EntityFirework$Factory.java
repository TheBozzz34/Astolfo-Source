package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class EntityFirework$Factory implements IParticleFactory {
   @Override
   public EntityFX getEntityFX(
      int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
   ) {
      EntityFirework$SparkFX entityFirework$SparkFX = new EntityFirework$SparkFX(
         worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Minecraft.getMinecraft().effectRenderer
      );
      entityFirework$SparkFX.setAlphaF(0.99F);
      return entityFirework$SparkFX;
   }
}
