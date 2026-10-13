package net.minecraft.client.renderer.entity.layers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class LayerHeldItem implements LayerRenderer<EntityLivingBase> {
   private final RendererLivingEntity<?> livingEntityRenderer;
   private static final long a = 42374186165773L;

   public LayerHeldItem(RendererLivingEntity<?> var1) {
      this.livingEntityRenderer = var1;
   }

   @Override
   public void doRenderLayer(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var12 = var1.getHeldItem();
      if (var12 != null) {
         GlStateManager.pushMatrix();
         if (this.livingEntityRenderer.getMainModel().isChild) {
            float var13 = 0.5F;
            GlStateManager.translate(0.0F, 0.625F, 0.0F);
            GlStateManager.rotate(-20.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.scale(var13, var13, var13);
         }

         ((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F);
         GlStateManager.translate(-0.0625F, 0.4375F, 0.0625F);
         if (var1 instanceof EntityPlayer && ((EntityPlayer)var1).fishEntity != null) {
            var12 = new ItemStack(Items.fishing_rod, 0);
         }

         Item var16 = var12.getItem();
         Minecraft var14 = Minecraft.getMinecraft();
         if (var16 instanceof ItemBlock && Block.getBlockFromItem(var16).getRenderType() == 2) {
            GlStateManager.translate(0.0F, 0.1875F, -0.3125F);
            GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
            float var15 = 0.375F;
            GlStateManager.scale(-var15, -var15, var15);
         }

         if (var1.isSneaking()) {
            GlStateManager.translate(0.0F, 0.203125F, 0.0F);
         }

         var14.getItemRenderer().renderItem(var1, var12, ItemCameraTransforms.TransformType.THIRD_PERSON);
         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return false;
   }
}
