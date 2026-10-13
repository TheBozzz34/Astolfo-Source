package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;
import shadersmod.client.ShadersTex;

public class RenderItemFrame extends Render<EntityItemFrame> {
   private static final ResourceLocation mapBackgroundTextures = new ResourceLocation("textures/map/map_background.png");
   private final Minecraft mc = Minecraft.getMinecraft();
   private final ModelResourceLocation itemFrameModel = new ModelResourceLocation("item_frame", "normal");
   private final ModelResourceLocation mapModel = new ModelResourceLocation("item_frame", "map");
   private RenderItem itemRenderer;
   private static final long c = 137620321673647L;

   public RenderItemFrame(RenderManager var1, RenderItem var2) {
      super(var1);
      this.itemRenderer = var2;
   }

   public void doRender(EntityItemFrame var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      BlockPos var13 = var1.getHangingPosition();
      double var14 = var13.getX() - var1.posX + var2;
      double var16 = var13.getY() - var1.posY + var4;
      double var18 = var13.getZ() - var1.posZ + var6;
      GlStateManager.translate(var14 + 0.5, var16 + 0.5, var18 + 0.5);
      GlStateManager.rotate(180.0F - var1.rotationYaw, 0.0F, 1.0F, 0.0F);
      this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      BlockRendererDispatcher var20 = this.mc.getBlockRendererDispatcher();
      ModelManager var21 = var20.getBlockModelShapes().getModelManager();
      IBakedModel var22;
      if (var1.getDisplayedItem() != null && var1.getDisplayedItem().getItem() == Items.filled_map) {
         var22 = var21.getModel(this.mapModel);
      } else {
         var22 = var21.getModel(this.itemFrameModel);
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      var20.getBlockModelRenderer().renderModelBrightnessColor(var22, 1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      GlStateManager.translate(0.0F, 0.0F, 0.4375F);
      this.renderItem(var1);
      GlStateManager.popMatrix();
      this.renderName(var1, var2 + var1.facingDirection.getFrontOffsetX() * 0.3F, var4 - 0.25, var6 + var1.facingDirection.getFrontOffsetZ() * 0.3F);
   }

   public ResourceLocation getEntityTexture(EntityItemFrame var1) {
      return null;
   }

   private void renderItem(EntityItemFrame var1) {
      ItemStack var5 = var1.getDisplayedItem();
      if (var5 != null) {
         EntityItem var6 = new EntityItem(var1.worldObj, 0.0, 0.0, 0.0, var5);
         Item var7 = var6.getEntityItem().getItem();
         var6.getEntityItem().stackSize = 1;
         var6.hoverStart = 0.0F;
         GlStateManager.pushMatrix();
         GlStateManager.disableLighting();
         int var8 = var1.getRotation();
         if (var7 instanceof ItemMap) {
            var8 = var8 % 4 * 2;
         }

         GlStateManager.rotate(var8 * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
         if (!Reflector.postForgeBusEvent(Reflector.RenderItemInFrameEvent_Constructor, var1, this)) {
            if (var7 instanceof ItemMap) {
               this.renderManager.renderEngine.bindTexture(mapBackgroundTextures);
               GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
               float var15 = 0.0078125F;
               GlStateManager.scale(var15, var15, var15);
               GlStateManager.translate(-64.0F, -64.0F, 0.0F);
               MapData var16 = Items.filled_map.getMapData(var6.getEntityItem(), var1.worldObj);
               GlStateManager.translate(0.0F, 0.0F, -1.0F);
               if (var16 != null) {
                  this.mc.entityRenderer.getMapItemRenderer().renderMap(var16, true);
               }
            } else {
               TextureAtlasSprite var9 = null;
               if (var7 == Items.compass) {
                  var9 = this.mc.getTextureMapBlocks().getAtlasSprite(TextureCompass.locationSprite);
                  if (Config.isShaders()) {
                     ShadersTex.bindTextureMapForUpdateAndRender(this.mc.getTextureManager(), TextureMap.locationBlocksTexture);
                  } else {
                     this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                  }

                  if (var9 instanceof TextureCompass) {
                     TextureCompass var10 = (TextureCompass)var9;
                     double var11 = var10.currentAngle;
                     double var13 = var10.angleDelta;
                     var10.currentAngle = 0.0;
                     var10.angleDelta = 0.0;
                     var10.updateCompass(
                        var1.worldObj, var1.posX, var1.posZ, MathHelper.wrapAngleTo180_float(180 + var1.facingDirection.getHorizontalIndex() * 90), false, true
                     );
                     var10.currentAngle = var11;
                     var10.angleDelta = var13;
                  } else {
                     var9 = null;
                  }
               }

               GlStateManager.scale(0.5F, 0.5F, 0.5F);
               if (!this.itemRenderer.shouldRenderItemIn3D(var6.getEntityItem()) || var7 instanceof ItemSkull) {
                  GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               }

               GlStateManager.pushAttrib();
               RenderHelper.enableStandardItemLighting();
               this.itemRenderer.renderItem(var6.getEntityItem(), ItemCameraTransforms.TransformType.FIXED);
               RenderHelper.disableStandardItemLighting();
               GlStateManager.popAttrib();
               if (var9 != null && var9.getFrameCount() > 0) {
                  var9.updateAnimation();
               }
            }
         }

         GlStateManager.enableLighting();
         GlStateManager.popMatrix();
      }
   }

   public void renderName(EntityItemFrame var1, double var2, double var4, double var6) {
      long var8 = c ^ 128971832605170L;
      if (Minecraft.isGuiEnabled() && var1.getDisplayedItem() != null && var1.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == var1) {
         float var11 = 1.6F;
         float var12 = 0.016666668F * var11;
         double var13 = var1.getDistanceSqToEntity(this.renderManager.livingPlayer);
         float var15 = var1.isSneaking() ? 32.0F : 64.0F;
         if (var13 < var15 * var15) {
            String var16 = var1.getDisplayedItem().getDisplayName();
            if (var1.isSneaking()) {
               FontRenderer var17 = this.getFontRendererFromRenderManager();
               GlStateManager.pushMatrix();
               long var10000 = 6658562332429050768L ^ var8;
               GlStateManager.translate((float)var2 + 0.0F, (float)var4 + var1.height + 0.5F, (float)var6);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
               GlStateManager.scale(-var12, -var12, var12);
               GlStateManager.disableLighting();
               GlStateManager.translate(0.0F, 0.25F / var12, 0.0F);
               GlStateManager.depthMask(false);
               GlStateManager.enableBlend();
               GlStateManager.blendFunc(770, 771);
               Tessellator var18 = Tessellator.getInstance();
               WorldRenderer var19 = var18.getWorldRenderer();
               int var20 = var17.getStringWidth(var16) / 2;
               GlStateManager.disableTexture2D();
               var19.begin(7, DefaultVertexFormats.POSITION_COLOR);
               var19.pos(-var20 - 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               var19.pos(-var20 - 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               var19.pos(var20 + 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               var19.pos(var20 + 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               var18.draw();
               GlStateManager.enableTexture2D();
               GlStateManager.depthMask(true);
               var17.D(var16, -var17.getStringWidth(var16) / 2, 0.0F, 553648127);
               GlStateManager.enableLighting();
               GlStateManager.disableBlend();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.popMatrix();
            } else {
               this.renderLivingLabel(var1, var16, var2, var4, var6, 64);
            }
         }
      }
   }
}
