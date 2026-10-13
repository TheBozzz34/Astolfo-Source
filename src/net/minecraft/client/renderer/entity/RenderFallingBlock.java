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

   public RenderFallingBlock(RenderManager var1) {
      super(var1);
      this.shadowSize = 0.5F;
   }

   public void doRender(EntityFallingBlock var1, double var2, double var4, double var6, float var8, float var9) {
      if (var1.getBlock() != null) {
         this.bindTexture(TextureMap.locationBlocksTexture);
         IBlockState var13 = var1.getBlock();
         Block var14 = var13.getBlock();
         BlockPos var15 = new BlockPos(var1);
         World var16 = var1.getWorldObj();
         if (var13 != var16.getBlockState(var15) && var14.getRenderType() != -1 && var14.getRenderType() == 3) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)var2, (float)var4, (float)var6);
            GlStateManager.disableLighting();
            Tessellator var17 = Tessellator.getInstance();
            WorldRenderer var18 = var17.getWorldRenderer();
            var18.begin((int)c, DefaultVertexFormats.BLOCK);
            int var19 = var15.getX();
            int var20 = var15.getY();
            int var21 = var15.getZ();
            var18.setTranslation(-var19 - 0.5F, -var20, -var21 - 0.5F);
            BlockRendererDispatcher var22 = Minecraft.getMinecraft().getBlockRendererDispatcher();
            IBakedModel var23 = var22.getModelFromBlockState(var13, var16, (BlockPos)null);
            var22.getBlockModelRenderer().renderModel(var16, var23, var13, var15, var18, false);
            var18.setTranslation(0.0, 0.0, 0.0);
            var17.draw();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            super.doRender(var1, var2, var4, var6, var8, var9);
         }
      }
   }

   public ResourceLocation getEntityTexture(EntityFallingBlock var1) {
      return TextureMap.locationBlocksTexture;
   }
}
