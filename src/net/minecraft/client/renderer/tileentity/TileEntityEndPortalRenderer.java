package net.minecraft.client.renderer.tileentity;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;

public class TileEntityEndPortalRenderer extends TileEntitySpecialRenderer<TileEntityEndPortal> {
   private static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
   private static final Random field_147527_e = new Random(31100L);
   public FloatBuffer field_147528_b = GLAllocation.createDirectFloatBuffer(16);
   private static final long a = 107406912118175L;

   public void renderTileEntityAt(TileEntityEndPortal var1, double var2, double var4, double var6, float var8, int var9) {
      long var10 = a ^ 49161711138997L;
      float var13 = (float)this.rendererDispatcher.entityX;
      float var14 = (float)this.rendererDispatcher.entityY;
      float var15 = (float)this.rendererDispatcher.entityZ;
      GlStateManager.disableLighting();
      field_147527_e.setSeed(31100L);
      float var16 = 0.75F;

      for (int var17 = 0; var17 < 16; var17++) {
         GlStateManager.pushMatrix();
         float var18 = 16 - var17;
         float var19 = 0.0625F;
         float var20 = 1.0F / (var18 + 1.0F);
         if (var17 == 0) {
            this.bindTexture(END_SKY_TEXTURE);
            var20 = 0.1F;
            long var10000 = 1286629455563104611L ^ var10;
            var18 = 65.0F;
            var19 = 0.125F;
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
         }

         if (var17 >= 1) {
            this.bindTexture(END_PORTAL_TEXTURE);
         }

         if (var17 == 1) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(1, 1);
            var19 = 0.5F;
         }

         float var21 = (float)(-(var4 + var16));
         float var22 = var21 + (float)ActiveRenderInfo.getPosition().yCoord;
         float var23 = var21 + var18 + (float)ActiveRenderInfo.getPosition().yCoord;
         float var24 = var22 / var23;
         var24 = (float)(var4 + var16) + var24;
         GlStateManager.translate(var13, var24, var15);
         GlStateManager.texGen(GlStateManager.TexGen.S, 9217);
         GlStateManager.texGen(GlStateManager.TexGen.T, 9217);
         GlStateManager.texGen(GlStateManager.TexGen.R, 9217);
         GlStateManager.texGen(GlStateManager.TexGen.Q, 9216);
         GlStateManager.texGen(GlStateManager.TexGen.S, 9473, this.func_147525_a(1.0F, 0.0F, 0.0F, 0.0F));
         GlStateManager.texGen(GlStateManager.TexGen.T, 9473, this.func_147525_a(0.0F, 0.0F, 1.0F, 0.0F));
         GlStateManager.texGen(GlStateManager.TexGen.R, 9473, this.func_147525_a(0.0F, 0.0F, 0.0F, 1.0F));
         GlStateManager.texGen(GlStateManager.TexGen.Q, 9474, this.func_147525_a(0.0F, 1.0F, 0.0F, 0.0F));
         GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
         long var32 = 5038568056052339966L ^ var10;
         GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
         GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
         GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, (float)(Minecraft.getSystemTime() % 700000L) / 700000.0F, 0.0F);
         GlStateManager.scale(var19, var19, var19);
         GlStateManager.translate(0.5F, 0.5F, 0.0F);
         GlStateManager.rotate((var17 * var17 * 4321 + var17 * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.translate(-0.5F, -0.5F, 0.0F);
         GlStateManager.translate(-var13, -var15, -var14);
         var22 = var21 + (float)ActiveRenderInfo.getPosition().yCoord;
         GlStateManager.translate(
            (float)ActiveRenderInfo.getPosition().xCoord * var18 / var22, (float)ActiveRenderInfo.getPosition().zCoord * var18 / var22, -var14
         );
         Tessellator var25 = Tessellator.getInstance();
         WorldRenderer var26 = var25.getWorldRenderer();
         var26.begin(7, DefaultVertexFormats.POSITION_COLOR);
         float var27 = (field_147527_e.nextFloat() * 0.5F + 0.1F) * var20;
         float var28 = (field_147527_e.nextFloat() * 0.5F + 0.4F) * var20;
         float var29 = (field_147527_e.nextFloat() * 0.5F + 0.5F) * var20;
         if (var17 == 0) {
            var27 = var28 = var29 = 1.0F * var20;
         }

         var26.pos(var2, var4 + var16, var6).color(var27, var28, var29, 1.0F).endVertex();
         var26.pos(var2, var4 + var16, var6 + 1.0).color(var27, var28, var29, 1.0F).endVertex();
         var32 = 8401031176255678822L ^ var10;
         var26.pos(var2 + 1.0, var4 + var16, var6 + 1.0).color(var27, var28, var29, 1.0F).endVertex();
         var26.pos(var2 + 1.0, var4 + var16, var6).color(var27, var28, var29, 1.0F).endVertex();
         var25.draw();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
         this.bindTexture(END_SKY_TEXTURE);
      }

      GlStateManager.disableBlend();
      GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
      GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
      GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
      GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
      GlStateManager.enableLighting();
   }

   private FloatBuffer func_147525_a(float var1, float var2, float var3, float var4) {
      ((Buffer)this.field_147528_b).clear();
      this.field_147528_b.put(var1).put(var2).put(var3).put(var4);
      ((Buffer)this.field_147528_b).flip();
      return this.field_147528_b;
   }
}
