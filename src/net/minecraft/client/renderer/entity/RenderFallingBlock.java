package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RenderFallingBlock extends Render<EntityFallingBlock> {
   private static final long a = 25545339382847L;
   private static final long c = 7440295791027027975L;

   public RenderFallingBlock(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.5F;
   }

   public void doRender(EntityFallingBlock entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (entity.getBlock() != null) {
         this.bindTexture(TextureMap.locationBlocksTexture);
         IBlockState iBlockState = entity.getBlock();
         Block block = iBlockState.getBlock();
         BlockPos blockPos = new BlockPos(entity);
         World world = entity.getWorldObj();
         if (iBlockState != world.getBlockState(blockPos) && block.getRenderType() != -1 && block.getRenderType() == 3) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y, (float)z);
            GlStateManager.disableLighting();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.begin((int)c, DefaultVertexFormats.BLOCK);
            int i = blockPos.getX();
            int j = blockPos.getY();
            int k = blockPos.getZ();
            worldRenderer.setTranslation(-i - 0.5F, -j, -k - 0.5F);
            BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            IBakedModel iBakedModel = blockRendererDispatcher.getModelFromBlockState(iBlockState, world, (BlockPos)null);
            blockRendererDispatcher.getBlockModelRenderer().renderModel(world, iBakedModel, iBlockState, blockPos, worldRenderer, false);
            worldRenderer.setTranslation(0.0, 0.0, 0.0);
            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
         }
      }
   }

   public ResourceLocation getEntityTexture(EntityFallingBlock entity) {
      return TextureMap.locationBlocksTexture;
   }
}
