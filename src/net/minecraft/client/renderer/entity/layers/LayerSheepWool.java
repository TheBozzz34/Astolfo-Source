package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.util.ResourceLocation;

public class LayerSheepWool implements LayerRenderer<EntitySheep> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sheep/sheep_fur.png");
   private final RenderSheep sheepRenderer;
   private final ModelSheep1 sheepModel = new ModelSheep1();
   private static final long a = 74811154794581L;

   public LayerSheepWool(RenderSheep var1) {
      this.sheepRenderer = var1;
   }

   public void doRenderLayer(EntitySheep var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (!var1.getSheared() && !var1.isInvisible()) {
         this.sheepRenderer.bindTexture(TEXTURE);
         if (var1.hasCustomName() && "jeb_".equals(var1.getCustomNameTag())) {
            boolean var20 = true;
            int var13 = var1.ticksExisted / 25 + var1.getEntityId();
            int var14 = EnumDyeColor.values().length;
            int var15 = var13 % var14;
            int var16 = (var13 + 1) % var14;
            float var17 = (var1.ticksExisted % 25 + var4) / 25.0F;
            float[] var18 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(var15));
            float[] var19 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(var16));
            if (Config.isCustomColors()) {
               var18 = CustomColors.getSheepColors(EnumDyeColor.byMetadata(var15), var18);
               var19 = CustomColors.getSheepColors(EnumDyeColor.byMetadata(var16), var19);
            }

            GlStateManager.color(
               var18[0] * (1.0F - var17) + var19[0] * var17, var18[1] * (1.0F - var17) + var19[1] * var17, var18[2] * (1.0F - var17) + var19[2] * var17
            );
         } else {
            float[] var12 = EntitySheep.getDyeRgb(var1.getFleeceColor());
            if (Config.isCustomColors()) {
               var12 = CustomColors.getSheepColors(var1.getFleeceColor(), var12);
            }

            GlStateManager.color(var12[0], var12[1], var12[2]);
         }

         this.sheepModel.setModelAttributes(this.sheepRenderer.getMainModel());
         this.sheepModel.setLivingAnimations(var1, var2, var3, var4);
         this.sheepModel.render(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
