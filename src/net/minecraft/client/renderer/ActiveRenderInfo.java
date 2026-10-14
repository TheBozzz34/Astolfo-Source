package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ActiveRenderInfo {
   private static final IntBuffer VIEWPORT = GLAllocation.createDirectIntBuffer(16);
   private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
   private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
   private static final FloatBuffer OBJECTCOORDS = GLAllocation.createDirectFloatBuffer(3);
   private static Vec3 position = new Vec3(0.0, 0.0, 0.0);
   private static float rotationX;
   private static float rotationXZ;
   private static float rotationZ;
   private static float rotationYZ;
   private static float rotationXY;
   private static final long a = 49062560652069L;

   public static void updateRenderInfo(EntityPlayer entityplayerIn, boolean bl) {
      GlStateManager.getFloat(EntityRenderer.ZE, MODELVIEW);
      GlStateManager.getFloat(EntityRenderer.ZE + 1, PROJECTION);
      GL11.glGetInteger(2978, VIEWPORT);
      float f = (VIEWPORT.get(0) + VIEWPORT.get(2)) / 2;
      float g = (VIEWPORT.get(1) + VIEWPORT.get(3)) / 2;
      GLU.gluUnProject(f, g, 0.0F, MODELVIEW, PROJECTION, VIEWPORT, OBJECTCOORDS);
      position = new Vec3(OBJECTCOORDS.get(0), OBJECTCOORDS.get(1), OBJECTCOORDS.get(2));
      int i = bl ? 1 : 0;
      float h = entityplayerIn.rotationPitch;
      float f2 = entityplayerIn.rotationYaw;
      rotationX = MathHelper.cos(f2 * (float) Math.PI / 180.0F) * (1 - i * 2);
      rotationZ = MathHelper.sin(f2 * (float) Math.PI / 180.0F) * (1 - i * 2);
      rotationYZ = -rotationZ * MathHelper.sin(h * (float) Math.PI / 180.0F) * (1 - i * 2);
      rotationXY = rotationX * MathHelper.sin(h * (float) Math.PI / 180.0F) * (1 - i * 2);
      rotationXZ = MathHelper.cos(h * (float) Math.PI / 180.0F);
   }

   public static Vec3 projectViewFromEntity(Entity entity, double d) {
      double e = entity.prevPosX + (entity.posX - entity.prevPosX) * d;
      double d2 = entity.prevPosY + (entity.posY - entity.prevPosY) * d;
      double d3 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * d;
      double d4 = e + position.xCoord;
      double d5 = d2 + position.yCoord;
      double d6 = d3 + position.zCoord;
      return new Vec3(d4, d5, d6);
   }

   public static Block getBlockAtEntityViewpoint(World worldIn, Entity entity, float f) {
      Vec3 vec3 = projectViewFromEntity(entity, f);
      BlockPos blockPos = new BlockPos(vec3);
      IBlockState iBlockState = worldIn.getBlockState(blockPos);
      Block block = iBlockState.getBlock();
      if (block.getMaterial().isLiquid()) {
         float g = 0.0F;
         if (iBlockState.getBlock() instanceof BlockLiquid) {
            g = BlockLiquid.getLiquidHeightPercent(iBlockState.getValue(BlockLiquid.LEVEL)) - 0.11111111F;
         }

         float h = blockPos.getY() + 1 - g;
         if (vec3.yCoord >= h) {
            block = worldIn.getBlockState(blockPos.up()).getBlock();
         }
      }

      return block;
   }

   public static Vec3 getPosition() {
      return position;
   }

   public static float S() {
      return rotationX;
   }

   public static float w() {
      return rotationXZ;
   }

   public static float N() {
      return rotationZ;
   }

   public static float T() {
      return rotationYZ;
   }

   public static float U() {
      return rotationXY;
   }
}
