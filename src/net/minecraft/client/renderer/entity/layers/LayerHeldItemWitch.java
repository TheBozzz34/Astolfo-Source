package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldItemWitch implements LayerRenderer<EntityWitch> {
   private final RenderWitch witchRenderer;
   private static final long a = 2620876148614L;

   public LayerHeldItemWitch(RenderWitch var1) {
      this.witchRenderer = var1;
   }

   public void doRenderLayer(EntityWitch var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var12 = var1.getHeldItem();
      if (var12 != null) {
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         if (this.witchRenderer.getMainModel().isChild) {
            GlStateManager.translate(0.0F, 0.625F, 0.0F);
            GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
            float var13 = 0.5F;
            GlStateManager.scale(var13, var13, var13);
         }

         ((ModelWitch)this.witchRenderer.getMainModel()).villagerNose.postRender(0.0625F);
         GlStateManager.translate(-0.0625F, 0.53125F, 0.21875F);
         Item var16 = var12.getItem();
         Minecraft var14 = Minecraft.getMinecraft();
         if (var16 instanceof ItemBlock && var14.getBlockRendererDispatcher().isRenderTypeChest(Block.getBlockFromItem(var16), var12.getMetadata())) {
            GlStateManager.translate(0.0F, 0.0625F, -0.25F);
            GlStateManager.rotate(30.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-5.0F, 0.0F, 1.0F, 0.0F);
            float var19 = 0.375F;
            GlStateManager.scale(var19, -var19, var19);
         } else if (var16 == Items.bow) {
            GlStateManager.translate(0.0F, 0.125F, -0.125F);
            GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
            float var15 = 0.625F;
            GlStateManager.scale(var15, -var15, var15);
            GlStateManager.rotate(-100.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-20.0F, 0.0F, 1.0F, 0.0F);
         } else if (var16.isFull3D()) {
            if (var16.shouldRotateAroundWhenRendering()) {
               GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.translate(0.0F, -0.0625F, 0.0F);
            }

            this.witchRenderer.transformHeldFull3DItemLayer();
            GlStateManager.translate(0.0625F, -0.125F, 0.0F);
            float var17 = 0.625F;
            GlStateManager.scale(var17, -var17, var17);
            GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.translate(0.1875F, 0.1875F, 0.0F);
            float var18 = 0.875F;
            GlStateManager.scale(var18, var18, var18);
            GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-60.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-30.0F, 0.0F, 0.0F, 1.0F);
         }

         GlStateManager.rotate(-15.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(40.0F, 0.0F, 0.0F, 1.0F);
         var14.getItemRenderer().renderItem(var1, var12, ItemCameraTransforms.TransformType.THIRD_PERSON);
         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
