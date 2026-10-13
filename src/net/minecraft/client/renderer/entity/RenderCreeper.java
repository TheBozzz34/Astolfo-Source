package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCreeperCharge;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderCreeper extends RenderLiving<EntityCreeper> {
   private static final ResourceLocation creeperTextures = new ResourceLocation("textures/entity/creeper/creeper.png");
   private static final long d = 37138073737958L;

   public RenderCreeper(RenderManager var1) {
      super(var1, new ModelCreeper(), 0.5F);
      this.addLayer(new LayerCreeperCharge(this));
   }

   public void preRenderCallback(EntityCreeper var1, float var2) {
      float var3 = var1.getCreeperFlashIntensity(var2);
      float var4 = 1.0F + MathHelper.sin(var3 * 100.0F) * var3 * 0.01F;
      var3 = MathHelper.clamp_float(var3, 0.0F, 1.0F);
      var3 *= var3;
      var3 *= var3;
      float var5 = (1.0F + var3 * 0.4F) * var4;
      float var6 = (1.0F + var3 * 0.1F) / var4;
      GlStateManager.scale(var5, var6, var5);
   }

   public int getColorMultiplier(EntityCreeper var1, float var2, float var3) {
      float var7 = var1.getCreeperFlashIntensity(var3);
      if ((int)(var7 * 10.0F) % 2 == 0) {
         return 0;
      }

      int var8 = (int)(var7 * 0.2F * 255.0F);
      var8 = MathHelper.clamp_int(var8, 0, 255);
      return var8 << 24 | 16777215;
   }

   public ResourceLocation getEntityTexture(EntityCreeper var1) {
      return creeperTextures;
   }
}
