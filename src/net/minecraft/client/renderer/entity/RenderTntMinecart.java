package net.minecraft.client.renderer.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;

public class RenderTntMinecart extends RenderMinecart<EntityMinecartTNT> {
   private static final long c = 98470757018527L;

   public RenderTntMinecart(RenderManager var1) {
      super(var1);
   }

   public void func_180560_a(EntityMinecartTNT var1, float var2, IBlockState var3) {
      long var4 = c ^ 55617037861937L;
      int var7 = var1.getFuseTicks();
      if (var7 > -1 && var7 - var2 + 1.0F < 10.0F) {
         float var8 = 1.0F - (var7 - var2 + 1.0F) / 10.0F;
         var8 = MathHelper.clamp_float(var8, 0.0F, 1.0F);
         var8 *= var8;
         var8 *= var8;
         float var9 = 1.0F + var8 * 0.3F;
         GlStateManager.scale(var9, var9, var9);
      }

      super.func_180560_a(var1, var2, var3);
      if (var7 > -1 && var7 / 5 % 2 == 0) {
         BlockRendererDispatcher var13 = Minecraft.getMinecraft().getBlockRendererDispatcher();
         GlStateManager.disableTexture2D();
         long var10000 = 6879750879246821350L ^ var4;
         GlStateManager.disableLighting();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 772);
         GlStateManager.color(1.0F, 1.0F, 1.0F, (1.0F - (var7 - var2 + 1.0F) / 100.0F) * 0.8F);
         GlStateManager.pushMatrix();
         var13.renderBlockBrightness(Blocks.tnt.getDefaultState(), 1.0F);
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
      }
   }
}
