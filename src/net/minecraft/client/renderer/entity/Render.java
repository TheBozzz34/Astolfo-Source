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

   public Render(RenderManager renderManager) {
      this.renderManager = renderManager;
   }

   public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
      AxisAlignedBB axisAlignedBB = livingEntity.getEntityBoundingBox();
      if (axisAlignedBB.hasNaN() || axisAlignedBB.getAverageEdgeLength() == 0.0) {
         axisAlignedBB = new AxisAlignedBB(
            livingEntity.posX - 2.0,
            livingEntity.posY - 2.0,
            livingEntity.posZ - 2.0,
            livingEntity.posX + 2.0,
            livingEntity.posY + 2.0,
            livingEntity.posZ + 2.0
         );
      }

      return livingEntity.isInRangeToRender3d(camX, camY, camZ) && (livingEntity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(axisAlignedBB));
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.renderName((T)entity, x, y, z);
   }

   public void renderName(T entity, double x, double y, double z) {
      if (this.canRenderName((T)entity)) {
         this.renderLivingLabel((T)entity, entity.getDisplayName().getFormattedText(), x, y, z, 64);
      }
   }

   public boolean canRenderName(T entity) {
      return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();
   }

   public void renderOffsetLivingLabel(T entityIn, double x, double y, double z, String str, float f, double d) {
      this.renderLivingLabel((T)entityIn, str, x, y, z, 64);
   }

   public abstract ResourceLocation getEntityTexture(T entity);

   public boolean bindEntityTexture(T entity) {
      ResourceLocation resourceLocation = this.getEntityTexture((T)entity);
      if (resourceLocation == null) {
         return false;
      }

      this.bindTexture(resourceLocation);
      return true;
   }

   public void bindTexture(ResourceLocation location) {
      this.renderManager.renderEngine.bindTexture(location);
   }

   private void renderEntityOnFire(Entity entity, double x, double y, double z, float partialTicks) {
      GlStateManager.disableLighting();
      TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
      TextureAtlasSprite textureAtlasSprite = textureMap.getAtlasSprite("minecraft:blocks/fire_layer_0");
      TextureAtlasSprite textureAtlasSprite2 = textureMap.getAtlasSprite("minecraft:blocks/fire_layer_1");
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      float f = entity.width * 1.4F;
      GlStateManager.scale(f, f, f);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      float g = 0.5F;
      float h = 0.0F;
      float f2 = 1.8F;
      float f3 = (float)(entity.posY - entity.getEntityBoundingBox().minY);
      GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(0.0F, 0.0F, -0.3F + (int)f2 * 0.02F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f4 = 0.0F;
      int i = 0;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      while (f2 > 0.0F) {
         TextureAtlasSprite textureAtlasSprite3 = i % 2 == 0 ? textureAtlasSprite : textureAtlasSprite2;
         this.bindTexture(TextureMap.locationBlocksTexture);
         float f5 = textureAtlasSprite3.getMinU();
         float f6 = textureAtlasSprite3.getMinV();
         float f7 = textureAtlasSprite3.getMaxU();
         float f8 = textureAtlasSprite3.getMaxV();
         if (i / 2 % 2 == 0) {
            float f9 = f7;
            f7 = f5;
            f5 = f9;
         }

         worldRenderer.pos(g - h, 0.0F - f3, f4).tex(f7, f8).endVertex();
         worldRenderer.pos(-g - h, 0.0F - f3, f4).tex(f5, f8).endVertex();
         worldRenderer.pos(-g - h, 1.4F - f3, f4).tex(f5, f6).endVertex();
         worldRenderer.pos(g - h, 1.4F - f3, f4).tex(f7, f6).endVertex();
         f2 -= 0.45F;
         f3 -= 0.45F;
         g *= 0.9F;
         f4 += 0.03F;
         i++;
      }

      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
   }

   private void renderShadow(Entity entityIn, double x, double y, double z, float shadowAlpha, float partialTicks) {
      if (!Config.isShaders() || !Shaders.shouldSkipDefaultShadow) {
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         this.renderManager.renderEngine.bindTexture(shadowTextures);
         World world = this.getWorldFromRenderManager();
         GlStateManager.depthMask(false);
         float f = this.shadowSize;
         if (entityIn instanceof EntityLiving) {
            EntityLiving entityLiving = (EntityLiving)entityIn;
            f *= entityLiving.getRenderSizeModifier();
            if (entityLiving.isChild()) {
               f *= 0.5F;
            }
         }

         double d = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
         double e = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
         double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
         int i = MathHelper.floor_double(d - f);
         int j = MathHelper.floor_double(d + f);
         int k = MathHelper.floor_double(e - f);
         int l = MathHelper.floor_double(e);
         int m = MathHelper.floor_double(d2 - f);
         int n = MathHelper.floor_double(d2 + f);
         double d3 = x - d;
         double d4 = y - e;
         double d5 = z - d2;
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

         for (BlockPos blockPos : BlockPos.getAllInBoxMutable(new BlockPos(i, k, m), new BlockPos(j, l, n))) {
            Block block = world.getBlockState(blockPos.down()).getBlock();
            if (block.getRenderType() != -1 && world.getLightFromNeighbors(blockPos) > 3) {
               this.renderShadowBlock(block, x, y, z, blockPos, shadowAlpha, f, d3, d4, d5);
            }
         }

         tessellator.draw();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }
   }

   private World getWorldFromRenderManager() {
      return this.renderManager.worldObj;
   }

   private void renderShadowBlock(Block blockIn, double d, double d2, double d3, BlockPos pos, float f, float g, double d4, double d5, double d6) {
      if (blockIn.isFullCube()) {
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         double e = (f - (d2 - (pos.getY() + d5)) / 2.0) * 0.5 * this.getWorldFromRenderManager().getLightBrightness(pos);
         if (e >= 0.0) {
            if (e > 1.0) {
               e = 1.0;
            }

            double d7 = pos.getX() + blockIn.getBlockBoundsMinX() + d4;
            double d8 = pos.getX() + blockIn.getBlockBoundsMaxX() + d4;
            double d9 = pos.getY() + blockIn.getBlockBoundsMinY() + d5 + 0.015625;
            double d10 = pos.getZ() + blockIn.getBlockBoundsMinZ() + d6;
            double d11 = pos.getZ() + blockIn.getBlockBoundsMaxZ() + d6;
            float h = (float)((d - d7) / 2.0 / g + 0.5);
            float f2 = (float)((d - d8) / 2.0 / g + 0.5);
            float f3 = (float)((d3 - d10) / 2.0 / g + 0.5);
            float f4 = (float)((d3 - d11) / 2.0 / g + 0.5);
            worldRenderer.pos(d7, d9, d10).tex(h, f3).color(1.0F, 1.0F, 1.0F, (float)e).endVertex();
            worldRenderer.pos(d7, d9, d11).tex(h, f4).color(1.0F, 1.0F, 1.0F, (float)e).endVertex();
            worldRenderer.pos(d8, d9, d11).tex(f2, f4).color(1.0F, 1.0F, 1.0F, (float)e).endVertex();
            worldRenderer.pos(d8, d9, d10).tex(f2, f3).color(1.0F, 1.0F, 1.0F, (float)e).endVertex();
         }
      }
   }

   public static void renderOffsetAABB(AxisAlignedBB boundingBox, double x, double y, double z) {
      GlStateManager.disableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      worldRenderer.setTranslation(x, y, z);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_NORMAL);
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      tessellator.draw();
      worldRenderer.setTranslation(0.0, 0.0, 0.0);
      GlStateManager.enableTexture2D();
   }

   public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
      if (this.renderManager.options != null) {
         if (this.renderManager.options.entityShadows && this.shadowSize > 0.0F && !entityIn.isInvisible() && this.renderManager.isRenderShadow()) {
            double d = this.renderManager.getDistanceToCamera(entityIn.posX, entityIn.posY, entityIn.posZ);
            float f = (float)((1.0 - d / 256.0) * this.shadowOpaque);
            if (f > 0.0F) {
               this.renderShadow(entityIn, x, y, z, f, partialTicks);
            }
         }

         if (entityIn.canRenderOnFire() && (!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).isSpectator())) {
            this.renderEntityOnFire(entityIn, x, y, z, partialTicks);
         }
      }
   }

   public FontRenderer getFontRendererFromRenderManager() {
      return this.renderManager.getFontRenderer();
   }

   public void renderLivingLabel(T entityIn, String str, double x, double y, double z, int maxDistance) {
      long l = u ^ 60540761083249L;
      double d = entityIn.getDistanceSqToEntity(this.renderManager.livingPlayer);
      if (d <= maxDistance * maxDistance) {
         FontRenderer fontRenderer = this.getFontRendererFromRenderManager();
         float f = 1.6F;
         long m = 2654402046154640604L ^ l;
         float g = 0.016666668F * f;
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)x + 0.0F, (float)y + entityIn.height + 0.5F, (float)z);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
         GlStateManager.scale(-g, -g, g);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(false);
         GlStateManager.disableDepth();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         byte b = 0;
         if (str.equals("deadmau5")) {
            b = -10;
         }

         int i = fontRenderer.getStringWidth(str) / 2;
         GlStateManager.disableTexture2D();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
         worldRenderer.pos(-i - 1, -1 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         worldRenderer.pos(-i - 1, 8 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         worldRenderer.pos(i + 1, 8 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         worldRenderer.pos(i + 1, -1 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         tessellator.draw();
         GlStateManager.enableTexture2D();
         fontRenderer.D(str, -fontRenderer.getStringWidth(str) / 2, b, 553648127);
         GlStateManager.enableDepth();
         GlStateManager.depthMask(true);
         fontRenderer.D(str, -fontRenderer.getStringWidth(str) / 2, b, -1);
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
