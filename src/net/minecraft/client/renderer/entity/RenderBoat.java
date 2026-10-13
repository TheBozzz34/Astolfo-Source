package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderBoat extends Render<EntityBoat> {
   private static final ResourceLocation boatTextures = new ResourceLocation("textures/entity/boat.png");
   public ModelBase modelBoat = new ModelBoat();
   private static final long a = 777745356384L;

   public RenderBoat(RenderManager var1) {
      super(var1);
      this.shadowSize = 0.5F;
   }

   public void doRender(EntityBoat var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2, (float)var4 + 0.25F, (float)var6);
      GlStateManager.rotate(180.0F - var8, 0.0F, 1.0F, 0.0F);
      float var13 = var1.getTimeSinceHit() - var9;
      float var14 = var1.getDamageTaken() - var9;
      if (var14 < 0.0F) {
         var14 = 0.0F;
      }

      if (var13 > 0.0F) {
         GlStateManager.rotate(MathHelper.sin(var13) * var13 * var14 / 10.0F * var1.getForwardDirection(), 1.0F, 0.0F, 0.0F);
      }

      float var15 = 0.75F;
      GlStateManager.scale(var15, var15, var15);
      GlStateManager.scale(1.0F / var15, 1.0F / var15, 1.0F / var15);
      this.bindEntityTexture(var1);
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      this.modelBoat.render(var1, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      super.doRender(var1, var2, var4, var6, var8, var9);
   }

   public ResourceLocation getEntityTexture(EntityBoat var1) {
      return boatTextures;
   }
}
