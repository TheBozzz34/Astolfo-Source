package net.minecraft.client.renderer.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderMinecart<T extends EntityMinecart> extends Render<T> {
   private static final ResourceLocation minecartTextures = new ResourceLocation("textures/entity/minecart.png");
   public ModelBase modelMinecart = new ModelMinecart();
   private static final long a = 45975228521223L;

   public RenderMinecart(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.5F;
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      this.bindEntityTexture((T)entity);
      long l = entity.getEntityId() * 493286711L;
      l = l * l * 4392167121L + l * 98761L;
      float f = (((float)(l >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float g = (((float)(l >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float h = (((float)(l >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      GlStateManager.translate(f, g, h);
      double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
      double e = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
      double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
      double d3 = 0.3F;
      Vec3 vec3 = entity.func_70489_a(d, e, d2);
      float f2 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
      if (vec3 != null) {
         Vec3 vec32 = entity.func_70495_a(d, e, d2, d3);
         Vec3 vec33 = entity.func_70495_a(d, e, d2, -d3);
         if (vec32 == null) {
            vec32 = vec3;
         }

         if (vec33 == null) {
            vec33 = vec3;
         }

         x += vec3.xCoord - d;
         y += (vec32.yCoord + vec33.yCoord) / 2.0 - e;
         z += vec3.zCoord - d2;
         Vec3 vec34 = vec33.addVector(-vec32.xCoord, -vec32.yCoord, -vec32.zCoord);
         if (vec34.lengthVector() != 0.0) {
            vec34 = vec34.normalize();
            entityYaw = (float)(Math.atan2(vec34.zCoord, vec34.xCoord) * 180.0 / Math.PI);
            f2 = (float)(Math.atan(vec34.yCoord) * 73.0);
         }
      }

      GlStateManager.translate((float)x, (float)y + 0.375F, (float)z);
      GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-f2, 0.0F, 0.0F, 1.0F);
      float f3 = entity.getRollingAmplitude() - partialTicks;
      float f4 = entity.getDamage() - partialTicks;
      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      if (f3 > 0.0F) {
         GlStateManager.rotate(MathHelper.sin(f3) * f3 * f4 / 10.0F * entity.getRollingDirection(), 1.0F, 0.0F, 0.0F);
      }

      int i = entity.getDisplayTileOffset();
      IBlockState iBlockState = entity.getDisplayTile();
      if (iBlockState.getBlock().getRenderType() != -1) {
         GlStateManager.pushMatrix();
         this.bindTexture(TextureMap.locationBlocksTexture);
         float f5 = 0.75F;
         GlStateManager.scale(f5, f5, f5);
         GlStateManager.translate(-0.5F, (i - 8) / 16.0F, 0.5F);
         this.func_180560_a((T)entity, partialTicks, iBlockState);
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindEntityTexture((T)entity);
      }

      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      this.modelMinecart.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      super.doRender((T)entity, x, y, z, entityYaw, partialTicks);
   }

   public ResourceLocation getEntityTexture(T entity) {
      return minecartTextures;
   }

   public void func_180560_a(T minecart, float partialTicks, IBlockState state) {
      GlStateManager.pushMatrix();
      Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(state, minecart.getBrightness(partialTicks));
      GlStateManager.popMatrix();
   }
}
