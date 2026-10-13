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

   public RenderMinecart(RenderManager var1) {
      super(var1);
      this.shadowSize = 0.5F;
   }

   public void doRender(T var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      this.bindEntityTexture((T)var1);
      long var13 = var1.getEntityId() * 493286711L;
      var13 = var13 * var13 * 4392167121L + var13 * 98761L;
      float var15 = (((float)(var13 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var16 = (((float)(var13 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float var17 = (((float)(var13 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      GlStateManager.translate(var15, var16, var17);
      double var18 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * var9;
      double var20 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * var9;
      double var22 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * var9;
      double var24 = 0.3F;
      Vec3 var26 = var1.func_70489_a(var18, var20, var22);
      float var27 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var9;
      if (var26 != null) {
         Vec3 var28 = var1.func_70495_a(var18, var20, var22, var24);
         Vec3 var29 = var1.func_70495_a(var18, var20, var22, -var24);
         if (var28 == null) {
            var28 = var26;
         }

         if (var29 == null) {
            var29 = var26;
         }

         var2 += var26.xCoord - var18;
         var4 += (var28.yCoord + var29.yCoord) / 2.0 - var20;
         var6 += var26.zCoord - var22;
         Vec3 var30 = var29.addVector(-var28.xCoord, -var28.yCoord, -var28.zCoord);
         if (var30.lengthVector() != 0.0) {
            var30 = var30.normalize();
            var8 = (float)(Math.atan2(var30.zCoord, var30.xCoord) * 180.0 / Math.PI);
            var27 = (float)(Math.atan(var30.yCoord) * 73.0);
         }
      }

      GlStateManager.translate((float)var2, (float)var4 + 0.375F, (float)var6);
      GlStateManager.rotate(180.0F - var8, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-var27, 0.0F, 0.0F, 1.0F);
      float var34 = var1.getRollingAmplitude() - var9;
      float var35 = var1.getDamage() - var9;
      if (var35 < 0.0F) {
         var35 = 0.0F;
      }

      if (var34 > 0.0F) {
         GlStateManager.rotate(MathHelper.sin(var34) * var34 * var35 / 10.0F * var1.getRollingDirection(), 1.0F, 0.0F, 0.0F);
      }

      int var37 = var1.getDisplayTileOffset();
      IBlockState var31 = var1.getDisplayTile();
      if (var31.getBlock().getRenderType() != -1) {
         GlStateManager.pushMatrix();
         this.bindTexture(TextureMap.locationBlocksTexture);
         float var32 = 0.75F;
         GlStateManager.scale(var32, var32, var32);
         GlStateManager.translate(-0.5F, (var37 - 8) / 16.0F, 0.5F);
         this.func_180560_a((T)var1, var9, var31);
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindEntityTexture((T)var1);
      }

      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      this.modelMinecart.render(var1, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      super.doRender((T)var1, var2, var4, var6, var8, var9);
   }

   public ResourceLocation getEntityTexture(T var1) {
      return minecartTextures;
   }

   public void func_180560_a(T var1, float var2, IBlockState var3) {
      GlStateManager.pushMatrix();
      Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(var3, var1.getBrightness(var2));
      GlStateManager.popMatrix();
   }
}
