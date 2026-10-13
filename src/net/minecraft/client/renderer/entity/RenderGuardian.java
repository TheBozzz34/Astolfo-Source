package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderGuardian extends RenderLiving<EntityGuardian> {
   private static final ResourceLocation GUARDIAN_TEXTURE = new ResourceLocation("textures/entity/guardian.png");
   private static final ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");
   private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = new ResourceLocation("textures/entity/guardian_beam.png");
   public int field_177115_a = ((ModelGuardian)this.mainModel).func_178706_a();
   private static final long e = 134730947953884L;

   public RenderGuardian(RenderManager var1) {
      super(var1, new ModelGuardian(), 0.5F);
   }

   public boolean shouldRender(EntityGuardian var1, ICamera var2, double var3, double var5, double var7) {
      if (super.shouldRender(var1, var2, var3, var5, var7)) {
         return true;
      }

      if (var1.hasTargetedEntity()) {
         EntityLivingBase var12 = var1.getTargetedEntity();
         if (var12 != null) {
            Vec3 var13 = this.func_177110_a(var12, var12.height * 0.5, 1.0F);
            Vec3 var14 = this.func_177110_a(var1, var1.getEyeHeight(), 1.0F);
            if (var2.isBoundingBoxInFrustum(AxisAlignedBB.fromBounds(var14.xCoord, var14.yCoord, var14.zCoord, var13.xCoord, var13.yCoord, var13.zCoord))) {
               return true;
            }
         }
      }

      return false;
   }

   private Vec3 func_177110_a(EntityLivingBase var1, double var2, float var4) {
      double var5 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * var4;
      double var7 = var2 + var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * var4;
      double var9 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * var4;
      return new Vec3(var5, var7, var9);
   }

   public void doRender(EntityGuardian var1, double var2, double var4, double var6, float var8, float var9) {
      long var10 = e ^ 15411579158919L;
      if (this.field_177115_a != ((ModelGuardian)this.mainModel).func_178706_a()) {
         this.mainModel = new ModelGuardian();
         this.field_177115_a = ((ModelGuardian)this.mainModel).func_178706_a();
      }

      super.doRender(var1, var2, var4, var6, var8, var9);
      EntityLivingBase var13 = var1.getTargetedEntity();
      if (var13 != null) {
         float var14 = var1.func_175477_p(var9);
         Tessellator var15 = Tessellator.getInstance();
         long var10000 = 747033026848950311L ^ var10;
         WorldRenderer var16 = var15.getWorldRenderer();
         this.bindTexture(GUARDIAN_BEAM_TEXTURE);
         GL11.glTexParameterf(3553, 10242, 10497.0F);
         GL11.glTexParameterf(3553, 10243, 10497.0F);
         GlStateManager.disableLighting();
         var10000 = 9131928542262324030L ^ var10;
         GlStateManager.disableCull();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         float var17 = 240.0F;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var17, var17);
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         float var18 = (float)var1.worldObj.getTotalWorldTime() + var9;
         float var19 = var18 * 0.5F % 1.0F;
         float var20 = var1.getEyeHeight();
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)var2, (float)var4 + var20, (float)var6);
         Vec3 var21 = this.func_177110_a(var13, var13.height * 0.5, var9);
         Vec3 var22 = this.func_177110_a(var1, var20, var9);
         Vec3 var23 = var21.subtract(var22);
         double var24 = var23.lengthVector() + 1.0;
         var23 = var23.normalize();
         float var26 = (float)Math.acos(var23.yCoord);
         float var27 = (float)Math.atan2(var23.zCoord, var23.xCoord);
         GlStateManager.rotate(((float) (Math.PI / 2) + -var27) * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(var26 * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         byte var28 = 1;
         double var29 = var18 * 0.05 * (1.0 - (var28 & 1) * 2.5);
         var16.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         float var31 = var14 * var14;
         var10000 = 4435857273330059672L ^ var10;
         int var32 = 64 + (int)(var31 * 240.0F);
         int var33 = 32 + (int)(var31 * 192.0F);
         int var34 = 128 - (int)(var31 * 64.0F);
         double var35 = var28 * 0.2;
         double var37 = var35 * 1.41;
         double var39 = 0.0 + Math.cos(var29 + (Math.PI * 3.0 / 4.0)) * var37;
         double var41 = 0.0 + Math.sin(var29 + (Math.PI * 3.0 / 4.0)) * var37;
         double var43 = 0.0 + Math.cos(var29 + (Math.PI / 4)) * var37;
         double var45 = 0.0 + Math.sin(var29 + (Math.PI / 4)) * var37;
         double var47 = 0.0 + Math.cos(var29 + (Math.PI * 5.0 / 4.0)) * var37;
         double var49 = 0.0 + Math.sin(var29 + (Math.PI * 5.0 / 4.0)) * var37;
         double var51 = 0.0 + Math.cos(var29 + (Math.PI * 7.0 / 4.0)) * var37;
         double var53 = 0.0 + Math.sin(var29 + (Math.PI * 7.0 / 4.0)) * var37;
         double var55 = 0.0 + Math.cos(var29 + Math.PI) * var35;
         double var57 = 0.0 + Math.sin(var29 + Math.PI) * var35;
         double var59 = 0.0 + Math.cos(var29 + 0.0) * var35;
         double var61 = 0.0 + Math.sin(var29 + 0.0) * var35;
         double var63 = 0.0 + Math.cos(var29 + (Math.PI / 2)) * var35;
         double var65 = 0.0 + Math.sin(var29 + (Math.PI / 2)) * var35;
         double var67 = 0.0 + Math.cos(var29 + (Math.PI * 3.0 / 2.0)) * var35;
         double var69 = 0.0 + Math.sin(var29 + (Math.PI * 3.0 / 2.0)) * var35;
         double var75 = -1.0F + var19;
         double var77 = var24 * (0.5 / var35) + var75;
         var16.pos(var55, var24, var57).tex(0.4999, var77).color(var32, var33, var34, 255).endVertex();
         var16.pos(var55, 0.0, var57).tex(0.4999, var75).color(var32, var33, var34, 255).endVertex();
         var16.pos(var59, 0.0, var61).tex(0.0, var75).color(var32, var33, var34, 255).endVertex();
         var16.pos(var59, var24, var61).tex(0.0, var77).color(var32, var33, var34, 255).endVertex();
         var16.pos(var63, var24, var65).tex(0.4999, var77).color(var32, var33, var34, 255).endVertex();
         var16.pos(var63, 0.0, var65).tex(0.4999, var75).color(var32, var33, var34, 255).endVertex();
         var16.pos(var67, 0.0, var69).tex(0.0, var75).color(var32, var33, var34, 255).endVertex();
         var16.pos(var67, var24, var69).tex(0.0, var77).color(var32, var33, var34, 255).endVertex();
         double var79 = 0.0;
         if (var1.ticksExisted % 2 == 0) {
            var79 = 0.5;
         }

         var16.pos(var39, var24, var41).tex(0.5, var79 + 0.5).color(var32, var33, var34, 255).endVertex();
         var16.pos(var43, var24, var45).tex(1.0, var79 + 0.5).color(var32, var33, var34, 255).endVertex();
         var16.pos(var51, var24, var53).tex(1.0, var79).color(var32, var33, var34, 255).endVertex();
         var16.pos(var47, var24, var49).tex(0.5, var79).color(var32, var33, var34, 255).endVertex();
         var15.draw();
         GlStateManager.popMatrix();
      }
   }

   public void preRenderCallback(EntityGuardian var1, float var2) {
      if (var1.isElder()) {
         GlStateManager.scale(2.35F, 2.35F, 2.35F);
      }
   }

   public ResourceLocation getEntityTexture(EntityGuardian var1) {
      return var1.isElder() ? GUARDIAN_ELDER_TEXTURE : GUARDIAN_TEXTURE;
   }
}
