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
   private static final long a = 46351381018946L;

   public LayerWolfCollar(RenderWolf var1) {
      this.wolfRenderer = var1;
   }

   public void doRenderLayer(EntityWolf var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var1.isTamed() && !var1.isInvisible()) {
         this.wolfRenderer.bindTexture(WOLF_COLLAR);
         EnumDyeColor var12 = EnumDyeColor.byMetadata(var1.getCollarColor().getMetadata());
         float[] var13 = EntitySheep.getDyeRgb(var12);
         if (Config.isCustomColors()) {
            var13 = CustomColors.getWolfCollarColors(var12, var13);
         }

         GlStateManager.color(var13[0], var13[1], var13[2]);
         this.wolfRenderer.getMainModel().render(var1, var2, var3, var5, var6, var7, var8);
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
