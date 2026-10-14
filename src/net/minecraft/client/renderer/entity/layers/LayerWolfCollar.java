package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.src.Config;
import net.minecraft.src.CustomColors;
import net.minecraft.util.ResourceLocation;

public class LayerWolfCollar implements LayerRenderer<EntityWolf> {
   private static final ResourceLocation WOLF_COLLAR = new ResourceLocation("textures/entity/wolf/wolf_collar.png");
   private final RenderWolf wolfRenderer;

   public LayerWolfCollar(RenderWolf wolfRendererIn) {
      this.wolfRenderer = wolfRendererIn;
   }

   public void doRenderLayer(EntityWolf entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      if (entitylivingbaseIn.isTamed() && !entitylivingbaseIn.isInvisible()) {
         this.wolfRenderer.bindTexture(WOLF_COLLAR);
         EnumDyeColor enumDyeColor = EnumDyeColor.byMetadata(entitylivingbaseIn.getCollarColor().getMetadata());
         float[] floats = EntitySheep.getDyeRgb(enumDyeColor);
         if (Config.isCustomColors()) {
            floats = CustomColors.getWolfCollarColors(enumDyeColor, floats);
         }

         GlStateManager.color(floats[0], floats[1], floats[2]);
         this.wolfRenderer.getMainModel().render(entitylivingbaseIn, f, g, h, f2, f3, scale);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
