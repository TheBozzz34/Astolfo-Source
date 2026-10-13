package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderPainting extends Render<EntityPainting> {
   private static final ResourceLocation KRISTOFFER_PAINTING_TEXTURE = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");
   private static final long a = 51539349536466L;

   public RenderPainting(RenderManager var1) {
      super(var1);
   }

   public void doRender(EntityPainting var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(var2, var4, var6);
      GlStateManager.rotate(180.0F - var8, 0.0F, 1.0F, 0.0F);
      GlStateManager.enableRescaleNormal();
      this.bindEntityTexture(var1);
      EntityPainting.EnumArt var10 = var1.art;
      float var11 = 0.0625F;
      GlStateManager.scale(var11, var11, var11);
      this.renderPainting(var1, var10.sizeX, var10.sizeY, var10.offsetX, var10.offsetY);
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(var1, var2, var4, var6, var8, var9);
   }

   public ResourceLocation getEntityTexture(EntityPainting var1) {
      return KRISTOFFER_PAINTING_TEXTURE;
   }

   private void renderPainting(EntityPainting var1, int var2, int var3, int var4, int var5) {
      float var9 = -var2 / 2.0F;
      float var10 = -var3 / 2.0F;
      float var11 = 0.5F;
      float var12 = 0.75F;
      float var13 = 0.8125F;
      float var14 = 0.0F;
      float var15 = 0.0625F;
      float var16 = 0.75F;
      float var17 = 0.8125F;
      float var18 = 0.001953125F;
      float var19 = 0.001953125F;
      float var20 = 0.7519531F;
      float var21 = 0.7519531F;
      float var22 = 0.0F;
      float var23 = 0.0625F;

      for (int var24 = 0; var24 < var2 / 16; var24++) {
         for (int var25 = 0; var25 < var3 / 16; var25++) {
            float var26 = var9 + (var24 + 1) * 16;
            float var27 = var9 + var24 * 16;
            float var28 = var10 + (var25 + 1) * 16;
            float var29 = var10 + var25 * 16;
            this.setLightmap(var1, (var26 + var27) / 2.0F, (var28 + var29) / 2.0F);
            float var30 = (var4 + var2 - var24 * 16) / 256.0F;
            float var31 = (var4 + var2 - (var24 + 1) * 16) / 256.0F;
            float var32 = (var5 + var3 - var25 * 16) / 256.0F;
            float var33 = (var5 + var3 - (var25 + 1) * 16) / 256.0F;
            Tessellator var34 = Tessellator.getInstance();
            WorldRenderer var35 = var34.getWorldRenderer();
            var35.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
            var35.pos(var26, var29, -var11).tex(var31, var32).normal(0.0F, 0.0F, -1.0F).endVertex();
            var35.pos(var27, var29, -var11).tex(var30, var32).normal(0.0F, 0.0F, -1.0F).endVertex();
            var35.pos(var27, var28, -var11).tex(var30, var33).normal(0.0F, 0.0F, -1.0F).endVertex();
            var35.pos(var26, var28, -var11).tex(var31, var33).normal(0.0F, 0.0F, -1.0F).endVertex();
            var35.pos(var26, var28, var11).tex(var12, var14).normal(0.0F, 0.0F, 1.0F).endVertex();
            var35.pos(var27, var28, var11).tex(var13, var14).normal(0.0F, 0.0F, 1.0F).endVertex();
            var35.pos(var27, var29, var11).tex(var13, var15).normal(0.0F, 0.0F, 1.0F).endVertex();
            var35.pos(var26, var29, var11).tex(var12, var15).normal(0.0F, 0.0F, 1.0F).endVertex();
            var35.pos(var26, var28, -var11).tex(var16, var18).normal(0.0F, 1.0F, 0.0F).endVertex();
            var35.pos(var27, var28, -var11).tex(var17, var18).normal(0.0F, 1.0F, 0.0F).endVertex();
            var35.pos(var27, var28, var11).tex(var17, var19).normal(0.0F, 1.0F, 0.0F).endVertex();
            var35.pos(var26, var28, var11).tex(var16, var19).normal(0.0F, 1.0F, 0.0F).endVertex();
            var35.pos(var26, var29, var11).tex(var16, var18).normal(0.0F, -1.0F, 0.0F).endVertex();
            var35.pos(var27, var29, var11).tex(var17, var18).normal(0.0F, -1.0F, 0.0F).endVertex();
            var35.pos(var27, var29, -var11).tex(var17, var19).normal(0.0F, -1.0F, 0.0F).endVertex();
            var35.pos(var26, var29, -var11).tex(var16, var19).normal(0.0F, -1.0F, 0.0F).endVertex();
            var35.pos(var26, var28, var11).tex(var21, var22).normal(-1.0F, 0.0F, 0.0F).endVertex();
            var35.pos(var26, var29, var11).tex(var21, var23).normal(-1.0F, 0.0F, 0.0F).endVertex();
            var35.pos(var26, var29, -var11).tex(var20, var23).normal(-1.0F, 0.0F, 0.0F).endVertex();
            var35.pos(var26, var28, -var11).tex(var20, var22).normal(-1.0F, 0.0F, 0.0F).endVertex();
            var35.pos(var27, var28, -var11).tex(var21, var22).normal(1.0F, 0.0F, 0.0F).endVertex();
            var35.pos(var27, var29, -var11).tex(var21, var23).normal(1.0F, 0.0F, 0.0F).endVertex();
            var35.pos(var27, var29, var11).tex(var20, var23).normal(1.0F, 0.0F, 0.0F).endVertex();
            var35.pos(var27, var28, var11).tex(var20, var22).normal(1.0F, 0.0F, 0.0F).endVertex();
            var34.draw();
         }
      }
   }

   private void setLightmap(EntityPainting var1, float var2, float var3) {
      int var7 = MathHelper.floor_double(var1.posX);
      int var8 = MathHelper.floor_double(var1.posY + var3 / 16.0F);
      int var9 = MathHelper.floor_double(var1.posZ);
      EnumFacing var10 = var1.facingDirection;
      if (var10 == EnumFacing.NORTH) {
         var7 = MathHelper.floor_double(var1.posX + var2 / 16.0F);
      }

      if (var10 == EnumFacing.WEST) {
         var9 = MathHelper.floor_double(var1.posZ - var2 / 16.0F);
      }

      if (var10 == EnumFacing.SOUTH) {
         var7 = MathHelper.floor_double(var1.posX - var2 / 16.0F);
      }

      if (var10 == EnumFacing.EAST) {
         var9 = MathHelper.floor_double(var1.posZ + var2 / 16.0F);
      }

      int var11 = this.renderManager.worldObj.getCombinedLight(new BlockPos(var7, var8, var9), 0);
      int var12 = var11 % 65536;
      int var13 = var11 / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var12, var13);
      GlStateManager.color(1.0F, 1.0F, 1.0F);
   }
}
