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

   public static void updateRenderInfo(EntityPlayer var0, boolean var1) {
      GlStateManager.getFloat(EntityRenderer.ZE, MODELVIEW);
      GlStateManager.getFloat(EntityRenderer.ZE + 1, PROJECTION);
      GL11.glGetInteger(2978, VIEWPORT);
      float var5 = (VIEWPORT.get(0) + VIEWPORT.get(2)) / 2;
      float var6 = (VIEWPORT.get(1) + VIEWPORT.get(3)) / 2;
      GLU.gluUnProject(var5, var6, 0.0F, MODELVIEW, PROJECTION, VIEWPORT, OBJECTCOORDS);
      position = new Vec3(OBJECTCOORDS.get(0), OBJECTCOORDS.get(1), OBJECTCOORDS.get(2));
      int var7 = var1 ? 1 : 0;
      float var8 = var0.rotationPitch;
      float var9 = var0.rotationYaw;
      rotationX = MathHelper.cos(var9 * (float) Math.PI / 180.0F) * (1 - var7 * 2);
      rotationZ = MathHelper.sin(var9 * (float) Math.PI / 180.0F) * (1 - var7 * 2);
      rotationYZ = -rotationZ * MathHelper.sin(var8 * (float) Math.PI / 180.0F) * (1 - var7 * 2);
      rotationXY = rotationX * MathHelper.sin(var8 * (float) Math.PI / 180.0F) * (1 - var7 * 2);
      rotationXZ = MathHelper.cos(var8 * (float) Math.PI / 180.0F);
   }

   public static Vec3 projectViewFromEntity(Entity var0, double var1) {
      double var3 = var0.prevPosX + (var0.posX - var0.prevPosX) * var1;
      double var5 = var0.prevPosY + (var0.posY - var0.prevPosY) * var1;
      double var7 = var0.prevPosZ + (var0.posZ - var0.prevPosZ) * var1;
      double var9 = var3 + position.xCoord;
      double var11 = var5 + position.yCoord;
      double var13 = var7 + position.zCoord;
      return new Vec3(var9, var11, var13);
   }

   public static Block getBlockAtEntityViewpoint(World var0, Entity var1, float var2) {
      Vec3 var6 = projectViewFromEntity(var1, var2);
      BlockPos var7 = new BlockPos(var6);
      IBlockState var8 = var0.getBlockState(var7);
      Block var9 = var8.getBlock();
      if (var9.getMaterial().isLiquid()) {
         float var10 = 0.0F;
         if (var8.getBlock() instanceof BlockLiquid) {
            var10 = BlockLiquid.getLiquidHeightPercent(var8.getValue(BlockLiquid.LEVEL)) - 0.11111111F;
         }

         float var11 = var7.getY() + 1 - var10;
         if (var6.yCoord >= var11) {
            var9 = var0.getBlockState(var7.up()).getBlock();
         }
      }

      return var9;
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
