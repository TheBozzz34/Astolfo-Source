package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.Config;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public abstract class Render<T extends Entity> {
   private static final ResourceLocation shadowTextures = new ResourceLocation("textures/misc/shadow.png");
   public final RenderManager renderManager;
   public float shadowSize;
   public float shadowOpaque = 1.0F;
   private static final long u = 132541803960720L;

   public Render(RenderManager var1) {
      this.renderManager = var1;
   }

   public boolean shouldRender(T var1, ICamera var2, double var3, double var5, double var7) {
      AxisAlignedBB var12 = var1.getEntityBoundingBox();
      if (var12.hasNaN() || var12.getAverageEdgeLength() == 0.0) {
         var12 = new AxisAlignedBB(var1.posX - 2.0, var1.posY - 2.0, var1.posZ - 2.0, var1.posX + 2.0, var1.posY + 2.0, var1.posZ + 2.0);
      }

      return var1.isInRangeToRender3d(var3, var5, var7) && (var1.ignoreFrustumCheck || var2.isBoundingBoxInFrustum(var12));
   }

   public void doRender(T var1, double var2, double var4, double var6, float var8, float var9) {
      this.renderName((T)var1, var2, var4, var6);
   }

   public void renderName(T var1, double var2, double var4, double var6) {
      if (this.canRenderName((T)var1)) {
         this.renderLivingLabel((T)var1, var1.getDisplayName().getFormattedText(), var2, var4, var6, 64);
      }
   }

   public boolean canRenderName(T var1) {
      return var1.getAlwaysRenderNameTagForRender() && var1.hasCustomName();
   }

   public void renderOffsetLivingLabel(T var1, double var2, double var4, double var6, String var8, float var9, double var10) {
      this.renderLivingLabel((T)var1, var8, var2, var4, var6, 64);
   }

   public abstract ResourceLocation getEntityTexture(T var1);

   public boolean bindEntityTexture(T var1) {
      ResourceLocation var5 = this.getEntityTexture((T)var1);
      if (var5 == null) {
         return false;
      }

      this.bindTexture(var5);
      return true;
   }

   public void bindTexture(ResourceLocation var1) {
      this.renderManager.renderEngine.bindTexture(var1);
   }

   private void renderEntityOnFire(Entity var1, double var2, double var4, double var6, float var8) {
      GlStateManager.disableLighting();
      TextureMap var12 = Minecraft.getMinecraft().getTextureMapBlocks();
      TextureAtlasSprite var13 = var12.getAtlasSprite("minecraft:blocks/fire_layer_0");
      TextureAtlasSprite var14 = var12.getAtlasSprite("minecraft:blocks/fire_layer_1");
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2, (float)var4, (float)var6);
      float var15 = var1.width * 1.4F;
      GlStateManager.scale(var15, var15, var15);
      Tessellator var16 = Tessellator.getInstance();
      WorldRenderer var17 = var16.getWorldRenderer();
      float var18 = 0.5F;
      float var19 = 0.0F;
      float var20 = 1.8F;
      float var21 = (float)(var1.posY - var1.getEntityBoundingBox().minY);
      GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(0.0F, 0.0F, -0.3F + (int)var20 * 0.02F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float var22 = 0.0F;
      int var23 = 0;
      var17.begin(7, DefaultVertexFormats.POSITION_TEX);

      while (var20 > 0.0F) {
         TextureAtlasSprite var24 = var23 % 2 == 0 ? var13 : var14;
         this.bindTexture(TextureMap.locationBlocksTexture);
         float var25 = var24.getMinU();
         float var26 = var24.getMinV();
         float var27 = var24.getMaxU();
         float var28 = var24.getMaxV();
         if (var23 / 2 % 2 == 0) {
            float var29 = var27;
            var27 = var25;
            var25 = var29;
         }

         var17.pos(var18 - var19, 0.0F - var21, var22).tex(var27, var28).endVertex();
         var17.pos(-var18 - var19, 0.0F - var21, var22).tex(var25, var28).endVertex();
         var17.pos(-var18 - var19, 1.4F - var21, var22).tex(var25, var26).endVertex();
         var17.pos(var18 - var19, 1.4F - var21, var22).tex(var27, var26).endVertex();
         var20 -= 0.45F;
         var21 -= 0.45F;
         var18 *= 0.9F;
         var22 += 0.03F;
         var23++;
      }

      var16.draw();
      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
   }

   private void renderShadow(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      if (!Config.isShaders() || !Shaders.shouldSkipDefaultShadow) {
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         this.renderManager.renderEngine.bindTexture(shadowTextures);
         World var13 = this.getWorldFromRenderManager();
         GlStateManager.depthMask(false);
         float var14 = this.shadowSize;
         if (var1 instanceof EntityLiving) {
            EntityLiving var15 = (EntityLiving)var1;
            var14 *= var15.getRenderSizeModifier();
            if (var15.isChild()) {
               var14 *= 0.5F;
            }
         }

         double var38 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * var9;
         double var17 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * var9;
         double var19 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * var9;
         int var21 = MathHelper.floor_double(var38 - var14);
         int var22 = MathHelper.floor_double(var38 + var14);
         int var23 = MathHelper.floor_double(var17 - var14);
         int var24 = MathHelper.floor_double(var17);
         int var25 = MathHelper.floor_double(var19 - var14);
         int var26 = MathHelper.floor_double(var19 + var14);
         double var27 = var2 - var38;
         double var29 = var4 - var17;
         double var31 = var6 - var19;
         Tessellator var33 = Tessellator.getInstance();
         WorldRenderer var34 = var33.getWorldRenderer();
         var34.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

         for (BlockPos var36 : BlockPos.getAllInBoxMutable(new BlockPos(var21, var23, var25), new BlockPos(var22, var24, var26))) {
            Block var37 = var13.getBlockState(var36.down()).getBlock();
            if (var37.getRenderType() != -1 && var13.getLightFromNeighbors(var36) > 3) {
               this.renderShadowBlock(var37, var2, var4, var6, var36, var8, var14, var27, var29, var31);
            }
         }

         var33.draw();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }
   }

   private World getWorldFromRenderManager() {
      return this.renderManager.worldObj;
   }

   private void renderShadowBlock(
      Block var1, double var2, double var4, double var6, BlockPos var8, float var9, float var10, double var11, double var13, double var15
   ) {
      if (var1.isFullCube()) {
         Tessellator var20 = Tessellator.getInstance();
         WorldRenderer var21 = var20.getWorldRenderer();
         double var22 = (var9 - (var4 - (var8.getY() + var13)) / 2.0) * 0.5 * this.getWorldFromRenderManager().getLightBrightness(var8);
         if (var22 >= 0.0) {
            if (var22 > 1.0) {
               var22 = 1.0;
            }

            double var24 = var8.getX() + var1.getBlockBoundsMinX() + var11;
            double var26 = var8.getX() + var1.getBlockBoundsMaxX() + var11;
            double var28 = var8.getY() + var1.getBlockBoundsMinY() + var13 + 0.015625;
            double var30 = var8.getZ() + var1.getBlockBoundsMinZ() + var15;
            double var32 = var8.getZ() + var1.getBlockBoundsMaxZ() + var15;
            float var34 = (float)((var2 - var24) / 2.0 / var10 + 0.5);
            float var35 = (float)((var2 - var26) / 2.0 / var10 + 0.5);
            float var36 = (float)((var6 - var30) / 2.0 / var10 + 0.5);
            float var37 = (float)((var6 - var32) / 2.0 / var10 + 0.5);
            var21.pos(var24, var28, var30).tex(var34, var36).color(1.0F, 1.0F, 1.0F, (float)var22).endVertex();
            var21.pos(var24, var28, var32).tex(var34, var37).color(1.0F, 1.0F, 1.0F, (float)var22).endVertex();
            var21.pos(var26, var28, var32).tex(var35, var37).color(1.0F, 1.0F, 1.0F, (float)var22).endVertex();
            var21.pos(var26, var28, var30).tex(var35, var36).color(1.0F, 1.0F, 1.0F, (float)var22).endVertex();
         }
      }
   }

   public static void renderOffsetAABB(AxisAlignedBB var0, double var1, double var3, double var5) {
      GlStateManager.disableTexture2D();
      Tessellator var10 = Tessellator.getInstance();
      WorldRenderer var11 = var10.getWorldRenderer();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      var11.setTranslation(var1, var3, var5);
      var11.begin(7, DefaultVertexFormats.POSITION_NORMAL);
      var11.pos(var0.minX, var0.maxY, var0.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      var11.pos(var0.maxX, var0.maxY, var0.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      var11.pos(var0.maxX, var0.minY, var0.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      var11.pos(var0.minX, var0.minY, var0.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      var11.pos(var0.minX, var0.minY, var0.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      var11.pos(var0.maxX, var0.minY, var0.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      var11.pos(var0.maxX, var0.maxY, var0.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      var11.pos(var0.minX, var0.maxY, var0.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      var11.pos(var0.minX, var0.minY, var0.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      var11.pos(var0.maxX, var0.minY, var0.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      var11.pos(var0.maxX, var0.minY, var0.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      var11.pos(var0.minX, var0.minY, var0.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      var11.pos(var0.minX, var0.maxY, var0.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      var11.pos(var0.maxX, var0.maxY, var0.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      var11.pos(var0.maxX, var0.maxY, var0.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      var11.pos(var0.minX, var0.maxY, var0.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      var11.pos(var0.minX, var0.minY, var0.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      var11.pos(var0.minX, var0.maxY, var0.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      var11.pos(var0.minX, var0.maxY, var0.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      var11.pos(var0.minX, var0.minY, var0.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      var11.pos(var0.maxX, var0.minY, var0.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      var11.pos(var0.maxX, var0.maxY, var0.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      var11.pos(var0.maxX, var0.maxY, var0.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      var11.pos(var0.maxX, var0.minY, var0.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      var10.draw();
      var11.setTranslation(0.0, 0.0, 0.0);
      GlStateManager.enableTexture2D();
   }

   public void doRenderShadowAndFire(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      if (this.renderManager.options != null) {
         if (this.renderManager.options.entityShadows && this.shadowSize > 0.0F && !var1.isInvisible() && this.renderManager.isRenderShadow()) {
            double var13 = this.renderManager.getDistanceToCamera(var1.posX, var1.posY, var1.posZ);
            float var15 = (float)((1.0 - var13 / 256.0) * this.shadowOpaque);
            if (var15 > 0.0F) {
               this.renderShadow(var1, var2, var4, var6, var15, var9);
            }
         }

         if (var1.canRenderOnFire() && (!(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).isSpectator())) {
            this.renderEntityOnFire(var1, var2, var4, var6, var9);
         }
      }
   }

   public FontRenderer getFontRendererFromRenderManager() {
      return this.renderManager.getFontRenderer();
   }

   public void renderLivingLabel(T var1, String var2, double var3, double var5, double var7, int var9) {
      long var10 = u ^ 60540761083249L;
      double var13 = var1.getDistanceSqToEntity(this.renderManager.livingPlayer);
      if (var13 <= var9 * var9) {
         FontRenderer var15 = this.getFontRendererFromRenderManager();
         float var16 = 1.6F;
         long var10000 = 2654402046154640604L ^ var10;
         float var17 = 0.016666668F * var16;
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)var3 + 0.0F, (float)var5 + var1.height + 0.5F, (float)var7);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
         GlStateManager.scale(-var17, -var17, var17);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(false);
         GlStateManager.disableDepth();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         Tessellator var18 = Tessellator.getInstance();
         WorldRenderer var19 = var18.getWorldRenderer();
         byte var20 = 0;
         if (var2.equals("deadmau5")) {
            var20 = -10;
         }

         int var21 = var15.getStringWidth(var2) / 2;
         GlStateManager.disableTexture2D();
         var19.begin(7, DefaultVertexFormats.POSITION_COLOR);
         var19.pos(-var21 - 1, -1 + var20, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         var19.pos(-var21 - 1, 8 + var20, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         var19.pos(var21 + 1, 8 + var20, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         var19.pos(var21 + 1, -1 + var20, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         var18.draw();
         GlStateManager.enableTexture2D();
         var15.D(var2, -var15.getStringWidth(var2) / 2, var20, 553648127);
         GlStateManager.enableDepth();
         GlStateManager.depthMask(true);
         var15.D(var2, -var15.getStringWidth(var2) / 2, var20, -1);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   public RenderManager getRenderManager() {
      return this.renderManager;
   }
}
