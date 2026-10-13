package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TileEntityPistonRenderer extends TileEntitySpecialRenderer<TileEntityPiston> {
   private final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
   private static final long a = 26593547584956L;

   public void renderTileEntityAt(TileEntityPiston var1, double var2, double var4, double var6, float var8, int var9) {
      long var10 = a ^ 69372101033390L;
      BlockPos var13 = var1.getPos();
      IBlockState var14 = var1.getPistonState();
      Block var15 = var14.getBlock();
      if (var15.getMaterial() != Material.air && var1.getProgress(var8) < 1.0F) {
         Tessellator var16 = Tessellator.getInstance();
         WorldRenderer var17 = var16.getWorldRenderer();
         this.bindTexture(TextureMap.locationBlocksTexture);
         RenderHelper.disableStandardItemLighting();
         long var10000 = 3992647339877128244L ^ var10;
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableBlend();
         GlStateManager.disableCull();
         if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
         } else {
            GlStateManager.shadeModel(7424);
         }

         var17.begin(7, DefaultVertexFormats.BLOCK);
         var17.setTranslation(
            (float)var2 - var13.getX() + var1.getOffsetX(var8),
            (float)var4 - var13.getY() + var1.getOffsetY(var8),
            (float)var6 - var13.getZ() + var1.getOffsetZ(var8)
         );
         World var18 = this.getWorld();
         if (var15 == Blocks.piston_head && var1.getProgress(var8) < 0.5F) {
            var14 = var14.withProperty(BlockPistonExtension.SHORT, true);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(var18, this.blockRenderer.getModelFromBlockState(var14, var18, var13), var14, var13, var17, true);
         } else if (var1.shouldPistonHeadBeRendered() && !var1.isExtending()) {
            BlockPistonExtension.EnumPistonType var19 = var15 == Blocks.sticky_piston
               ? BlockPistonExtension.EnumPistonType.STICKY
               : BlockPistonExtension.EnumPistonType.DEFAULT;
            IBlockState var20 = Blocks.piston_head
               .getDefaultState()
               .withProperty(BlockPistonExtension.TYPE, var19)
               .withProperty(BlockPistonExtension.FACING, var14.getValue(BlockPistonBase.FACING));
            var20 = var20.withProperty(BlockPistonExtension.SHORT, var1.getProgress(var8) >= 0.5F);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(var18, this.blockRenderer.getModelFromBlockState(var20, var18, var13), var20, var13, var17, true);
            var17.setTranslation((float)var2 - var13.getX(), (float)var4 - var13.getY(), (float)var6 - var13.getZ());
            var14.withProperty(BlockPistonBase.EXTENDED, true);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(var18, this.blockRenderer.getModelFromBlockState(var14, var18, var13), var14, var13, var17, true);
         } else {
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(var18, this.blockRenderer.getModelFromBlockState(var14, var18, var13), var14, var13, var17, false);
         }

         var17.setTranslation(0.0, 0.0, 0.0);
         var16.draw();
         RenderHelper.enableStandardItemLighting();
      }
   }
}
