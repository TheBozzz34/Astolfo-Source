package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.ResourceLocation;

public class RenderWitherSkull extends Render<EntityWitherSkull> {
   private static final ResourceLocation invulnerableWitherTextures = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation witherTextures = new ResourceLocation("textures/entity/wither/wither.png");
   private final ModelSkeletonHead skeletonHeadModel = new ModelSkeletonHead();
   private static final long a = 113568273568508L;

   public RenderWitherSkull(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   private float func_82400_a(float f, float g, float h) {
      float f2 = g - f;

      while (f2 < -180.0F) {
         f2 += 360.0F;
      }

      while (f2 >= 180.0F) {
         f2 -= 360.0F;
      }

      return f + h * f2;
   }

   public void doRender(EntityWitherSkull entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      float f = this.func_82400_a(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
      float g = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
      GlStateManager.translate((float)x, (float)y, (float)z);
      float h = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlpha();
      this.bindEntityTexture(entity);
      this.skeletonHeadModel.render(entity, 0.0F, 0.0F, 0.0F, f, g, h);
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(EntityWitherSkull entity) {
      return entity.isInvulnerable() ? invulnerableWitherTextures : witherTextures;
   }
}
