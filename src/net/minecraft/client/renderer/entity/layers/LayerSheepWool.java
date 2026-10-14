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

   public LayerSheepWool(RenderSheep sheepRendererIn) {
      this.sheepRenderer = sheepRendererIn;
   }

   public void doRenderLayer(EntitySheep entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      if (!entitylivingbaseIn.getSheared() && !entitylivingbaseIn.isInvisible()) {
         this.sheepRenderer.bindTexture(TEXTURE);
         if (entitylivingbaseIn.hasCustomName() && "jeb_".equals(entitylivingbaseIn.getCustomNameTag())) {
            boolean bl = true;
            int i = entitylivingbaseIn.ticksExisted / 25 + entitylivingbaseIn.getEntityId();
            int j = EnumDyeColor.values().length;
            int k = i % j;
            int l = (i + 1) % j;
            float f4 = (entitylivingbaseIn.ticksExisted % 25 + partialTicks) / 25.0F;
            float[] floats = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(k));
            float[] floats2 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(l));
            if (Config.isCustomColors()) {
               floats = CustomColors.getSheepColors(EnumDyeColor.byMetadata(k), floats);
               floats2 = CustomColors.getSheepColors(EnumDyeColor.byMetadata(l), floats2);
            }

            GlStateManager.color(
               floats[0] * (1.0F - f4) + floats2[0] * f4, floats[1] * (1.0F - f4) + floats2[1] * f4, floats[2] * (1.0F - f4) + floats2[2] * f4
            );
         } else {
            float[] floats3 = EntitySheep.getDyeRgb(entitylivingbaseIn.getFleeceColor());
            if (Config.isCustomColors()) {
               floats3 = CustomColors.getSheepColors(entitylivingbaseIn.getFleeceColor(), floats3);
            }

            GlStateManager.color(floats3[0], floats3[1], floats3[2]);
         }

         this.sheepModel.setModelAttributes(this.sheepRenderer.getMainModel());
         this.sheepModel.setLivingAnimations(entitylivingbaseIn, f, g, partialTicks);
         this.sheepModel.render(entitylivingbaseIn, f, g, h, f2, f3, scale);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
