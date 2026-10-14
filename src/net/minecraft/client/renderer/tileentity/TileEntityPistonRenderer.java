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

   public void renderTileEntityAt(TileEntityPiston te, double x, double y, double z, float partialTicks, int destroyStage) {
      long l = a ^ 69372101033390L;
      BlockPos blockPos = te.getPos();
      IBlockState iBlockState = te.getPistonState();
      Block block = iBlockState.getBlock();
      if (block.getMaterial() != Material.air && te.getProgress(partialTicks) < 1.0F) {
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         this.bindTexture(TextureMap.locationBlocksTexture);
         RenderHelper.disableStandardItemLighting();
         long m = 3992647339877128244L ^ l;
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableBlend();
         GlStateManager.disableCull();
         if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
         } else {
            GlStateManager.shadeModel(7424);
         }

         worldRenderer.begin(7, DefaultVertexFormats.BLOCK);
         worldRenderer.setTranslation(
            (float)x - blockPos.getX() + te.getOffsetX(partialTicks),
            (float)y - blockPos.getY() + te.getOffsetY(partialTicks),
            (float)z - blockPos.getZ() + te.getOffsetZ(partialTicks)
         );
         World world = this.getWorld();
         if (block == Blocks.piston_head && te.getProgress(partialTicks) < 0.5F) {
            iBlockState = iBlockState.withProperty(BlockPistonExtension.SHORT, true);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world, this.blockRenderer.getModelFromBlockState(iBlockState, world, blockPos), iBlockState, blockPos, worldRenderer, true);
         } else if (te.shouldPistonHeadBeRendered() && !te.isExtending()) {
            BlockPistonExtension.EnumPistonType enumPistonType = block == Blocks.sticky_piston
               ? BlockPistonExtension.EnumPistonType.STICKY
               : BlockPistonExtension.EnumPistonType.DEFAULT;
            IBlockState iBlockState2 = Blocks.piston_head
               .getDefaultState()
               .withProperty(BlockPistonExtension.TYPE, enumPistonType)
               .withProperty(BlockPistonExtension.FACING, iBlockState.getValue(BlockPistonBase.FACING));
            iBlockState2 = iBlockState2.withProperty(BlockPistonExtension.SHORT, te.getProgress(partialTicks) >= 0.5F);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world, this.blockRenderer.getModelFromBlockState(iBlockState2, world, blockPos), iBlockState2, blockPos, worldRenderer, true);
            worldRenderer.setTranslation((float)x - blockPos.getX(), (float)y - blockPos.getY(), (float)z - blockPos.getZ());
            iBlockState.withProperty(BlockPistonBase.EXTENDED, true);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world, this.blockRenderer.getModelFromBlockState(iBlockState, world, blockPos), iBlockState, blockPos, worldRenderer, true);
         } else {
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world, this.blockRenderer.getModelFromBlockState(iBlockState, world, blockPos), iBlockState, blockPos, worldRenderer, false);
         }

         worldRenderer.setTranslation(0.0, 0.0, 0.0);
         tessellator.draw();
         RenderHelper.enableStandardItemLighting();
      }
   }
}
