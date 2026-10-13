package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;

public class TileEntityMobSpawnerRenderer extends TileEntitySpecialRenderer<TileEntityMobSpawner> {
   private static final long a = 88181695827065L;

   public void renderTileEntityAt(TileEntityMobSpawner var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4, (float)var6 + 0.5F);
      renderMob(var1.getSpawnerBaseLogic(), var2, var4, var6, var8);
      GlStateManager.popMatrix();
   }

   public static void renderMob(MobSpawnerBaseLogic var0, double var1, double var3, double var5, float var7) {
      Entity var10 = var0.func_180612_a(var0.getSpawnerWorld());
      if (var10 != null) {
         float var11 = 0.4375F;
         GlStateManager.translate(0.0F, 0.4F, 0.0F);
         GlStateManager.rotate((float)(var0.getPrevMobRotation() + (var0.getMobRotation() - var0.getPrevMobRotation()) * var7) * 10.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.4F, 0.0F);
         GlStateManager.scale(var11, var11, var11);
         var10.setLocationAndAngles(var1, var3, var5, 0.0F, 0.0F);
         Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(var10, 0.0, 0.0, 0.0, 0.0F, var7);
      }
   }
}
