package net.minecraft.client.renderer;

import bypass.Astolfo;
import bypass.event.EventState;
import bypass.event.render.RenderArmEvent;
import bypass.event.render.RenderItemEvent;
import bypass.event.render.RenderOverlaysEvent;
import bypass.module.visuals.Animations;
import bypass.module.visuals.AnimationsMode;
import bypass.module.visuals.Camera;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.src.Config;
import net.minecraft.src.DynamicLights;
import net.minecraft.src.Reflector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class ItemRenderer {
   private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
   private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
   private final Minecraft mc;
   private ItemStack itemToRender;
   private float j;
   private float I;
   private final RenderManager renderManager;
   private final RenderItem itemRenderer;
   private int equippedItemSlot = -1;
   public static boolean J;
   private float p = 0.0F;
   private static final long b = 116949165088460L;

   public ItemRenderer(Minecraft var1) {
      this.mc = var1;
      this.renderManager = var1.getRenderManager();
      this.itemRenderer = var1.getRenderItem();
   }

   public void renderItem(EntityLivingBase var1, ItemStack var2, ItemCameraTransforms.TransformType var3) {
      if (var2 != null) {
         RenderItemEvent var7 = new RenderItemEvent(var1, var2);
         Astolfo.INSTANCE.getEventBus().post(var7);
         if (!var7.isCancelled()) {
            Item var8 = var2.getItem();
            Block var9 = Block.getBlockFromItem(var8);
            GlStateManager.pushMatrix();
            if (this.itemRenderer.shouldRenderItemIn3D(var2)) {
               GlStateManager.scale(2.0F, 2.0F, 2.0F);
               if (this.isBlockTranslucent(var9) && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask)) {
                  GlStateManager.depthMask(false);
               }
            }

            this.itemRenderer.renderItemModelForEntity(var2, var1, var3);
            if (this.isBlockTranslucent(var9)) {
               GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
         }
      }
   }

   private boolean isBlockTranslucent(Block var1) {
      return var1 != null && var1.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
   }

   private void R(float var1, float var2) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(var1, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(var2, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   private void setLightMapFromPlayer(AbstractClientPlayer var1) {
      int var5 = this.mc.theWorld.getCombinedLight(new BlockPos(var1.posX, var1.posY + var1.getEyeHeight(), var1.posZ), 0);
      if (Config.isDynamicLights()) {
         var5 = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), var5);
      }

      float var6 = var5 & 65535;
      float var7 = var5 >> 16;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var6, var7);
   }

   private void rotateWithPlayerRotations(EntityPlayerSP var1, float var2) {
      float var3 = var1.prevRenderArmPitch + (var1.renderArmPitch - var1.prevRenderArmPitch) * var2;
      float var4 = var1.prevRenderArmYaw + (var1.renderArmYaw - var1.prevRenderArmYaw) * var2;
      GlStateManager.rotate((var1.rotationPitch - var3) * 0.1F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate((var1.rotationYaw - var4) * 0.1F, 0.0F, 1.0F, 0.0F);
   }

   private float getMapAngleFromPitch(float var1) {
      float var2 = 1.0F - var1 / 45.0F + 0.1F;
      var2 = MathHelper.clamp_float(var2, 0.0F, 1.0F);
      return -MathHelper.cos(var2 * (float) Math.PI) * 0.5F + 0.5F;
   }

   private void renderRightArm(RenderPlayer var1) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(0.25F, -0.85F, 0.75F);
      var1.renderRightArm(this.mc.thePlayer);
      GlStateManager.popMatrix();
   }

   private void renderLeftArm(RenderPlayer var1) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-0.3F, -1.1F, 0.45F);
      var1.renderLeftArm(this.mc.thePlayer);
      GlStateManager.popMatrix();
   }

   private void renderPlayerArms(AbstractClientPlayer var1) {
      this.mc.getTextureManager().bindTexture(var1.getLocationSkin());
      Render var5 = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
      RenderPlayer var6 = (RenderPlayer)var5;
      if (!var1.isInvisible()) {
         GlStateManager.disableCull();
         this.renderRightArm(var6);
         this.renderLeftArm(var6);
         GlStateManager.enableCull();
      }
   }

   private void renderItemMap(AbstractClientPlayer var1, float var2, float var3, float var4) {
      float var7 = -0.4F * MathHelper.sin(MathHelper.sqrt_float(var4) * (float) Math.PI);
      float var8 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(var4) * (float) Math.PI * 2.0F);
      float var9 = -0.2F * MathHelper.sin(var4 * (float) Math.PI);
      GlStateManager.translate(var7, var8, var9);
      float var10 = this.getMapAngleFromPitch(var2);
      GlStateManager.translate(0.0F, 0.04F, -0.72F);
      GlStateManager.translate(0.0F, var3 * -1.2F, 0.0F);
      GlStateManager.translate(0.0F, var10 * -0.5F, 0.0F);
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(var10 * -85.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
      this.renderPlayerArms(var1);
      float var11 = MathHelper.sin(var4 * var4 * (float) Math.PI);
      float var12 = MathHelper.sin(MathHelper.sqrt_float(var4) * (float) Math.PI);
      GlStateManager.rotate(var11 * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(var12 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(var12 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.38F, 0.38F, 0.38F);
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-1.0F, -1.0F, 0.0F);
      GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
      this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
      Tessellator var13 = Tessellator.getInstance();
      WorldRenderer var14 = var13.getWorldRenderer();
      GL11.glNormal3f(0.0F, 0.0F, -1.0F);
      var14.begin(7, DefaultVertexFormats.POSITION_TEX);
      var14.pos(-7.0, 135.0, 0.0).tex(0.0, 1.0).endVertex();
      var14.pos(135.0, 135.0, 0.0).tex(1.0, 1.0).endVertex();
      var14.pos(135.0, -7.0, 0.0).tex(1.0, 0.0).endVertex();
      var14.pos(-7.0, -7.0, 0.0).tex(0.0, 0.0).endVertex();
      var13.draw();
      MapData var15 = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);
      if (var15 != null) {
         this.mc.entityRenderer.getMapItemRenderer().renderMap(var15, false);
      }
   }

   private void renderPlayerArm(AbstractClientPlayer var1, float var2, float var3) {
      Astolfo.INSTANCE.getEventBus().post(new RenderArmEvent(EventState.PRE));
      float var6 = -0.3F * MathHelper.sin(MathHelper.sqrt_float(var3) * (float) Math.PI);
      float var7 = 0.4F * MathHelper.sin(MathHelper.sqrt_float(var3) * (float) Math.PI * 2.0F);
      float var8 = -0.4F * MathHelper.sin(var3 * (float) Math.PI);
      GlStateManager.translate(var6, var7, var8);
      GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
      GlStateManager.translate(0.0F, var2 * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float var9 = MathHelper.sin(var3 * var3 * (float) Math.PI);
      float var10 = MathHelper.sin(MathHelper.sqrt_float(var3) * (float) Math.PI);
      GlStateManager.rotate(var10 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(var9 * -20.0F, 0.0F, 0.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(var1.getLocationSkin());
      GlStateManager.translate(-1.0F, 3.6F, 3.5F);
      GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      GlStateManager.translate(5.6F, 0.0F, 0.0F);
      Render var11 = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
      GlStateManager.disableCull();
      RenderPlayer var12 = (RenderPlayer)var11;
      var12.renderRightArm(this.mc.thePlayer);
      GlStateManager.enableCull();
      Astolfo.INSTANCE.getEventBus().post(new RenderArmEvent(EventState.POST));
   }

   private void doItemUsedTransformations(float var1) {
      float var2 = -0.4F * MathHelper.sin(MathHelper.sqrt_float(var1) * (float) Math.PI);
      float var3 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(var1) * (float) Math.PI * 2.0F);
      float var4 = -0.2F * MathHelper.sin(var1 * (float) Math.PI);
      GlStateManager.translate(var2, var3, var4);
   }

   private void performDrinking(AbstractClientPlayer var1, float var2) {
      float var6 = var1.p() - var2 + 1.0F;
      float var7 = var6 / this.itemToRender.getMaxItemUseDuration();
      float var8 = MathHelper.abs(MathHelper.cos(var6 / 4.0F * (float) Math.PI) * 0.1F);
      if (var7 >= 0.8F) {
         var8 = 0.0F;
      }

      GlStateManager.translate(0.0F, var8, 0.0F);
      float var9 = 1.0F - (float)Math.pow(var7, 27.0);
      GlStateManager.translate(var9 * 0.6F, var9 * -0.5F, var9 * 0.0F);
      GlStateManager.rotate(var9 * 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(var9 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(var9 * 30.0F, 0.0F, 0.0F, 1.0F);
   }

   private void L(float var1, float var2) {
      long var3 = b ^ 31825681895783L;
      long var5 = var3 ^ 72410545959448L;
      GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
      if (Astolfo.INSTANCE.getModuleManager().get(Camera.class).isEnabled()) {
         GlStateManager.translate(
            Camera.itemPosition.getSetting("X").asFloat(), Camera.itemPosition.getSetting("Y").asFloat(), Camera.itemPosition.getSetting("Z").asFloat()
         );
      }

      GlStateManager.translate(0.0F, var1 * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float var8 = MathHelper.sin((float)(var2 * var2 * Math.PI));
      float var9 = MathHelper.sin((float)(MathHelper.sqrt_float(var2) * Math.PI));
      GlStateManager.rotate(var8 * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(var9 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(var9 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.4F, 0.4F, 0.4F);
   }

   private void e(float var1, float var2) {
      GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
      GlStateManager.translate(0.0F, var1 * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float var3 = MathHelper.sin(var2 * var2 * (float) Math.PI);
      float var4 = MathHelper.sin(MathHelper.sqrt_float(var2) * (float) Math.PI);
      GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(var4 * -1.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(var4 * -45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.4F, 0.4F, 0.4F);
   }

   private void doBowTransformations(float var1, AbstractClientPlayer var2) {
      GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-0.9F, 0.2F, 0.0F);
      float var6 = this.itemToRender.getMaxItemUseDuration() - (var2.p() - var1 + 1.0F);
      float var7 = var6 / 20.0F;
      var7 = (var7 * var7 + var7 * 2.0F) / 3.0F;
      if (var7 > 1.0F) {
         var7 = 1.0F;
      }

      if (var7 > 0.1F) {
         float var8 = MathHelper.sin((var6 - 0.1F) * 1.3F);
         float var9 = var7 - 0.1F;
         float var10 = var8 * var9;
         GlStateManager.translate(var10 * 0.0F, var10 * 0.01F, var10 * 0.0F);
      }

      GlStateManager.translate(var7 * 0.0F, var7 * 0.0F, var7 * 0.1F);
      GlStateManager.scale(1.0F, 1.0F, 1.0F + var7 * 0.2F);
   }

   private void doBlockTransformations() {
      GlStateManager.translate(-0.5F, 0.2F, 0.0F);
      GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
   }

   private void i() {
      GlStateManager.translate(0.5F, 0.1F, 0.0F);
      GlStateManager.rotate(10.0F, -2.0F, 1.0F, 0.0F);
      GlStateManager.rotate(this.t(Animations.spin.getValue()), 0.0F, 1.0F, 0.0F);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void renderItemInFirstPerson(float var1) {
      long var2 = b ^ 51051569062736L;
      long var4 = var2 ^ 126739184074799L;
      float var7 = 1.0F - (this.I + (this.j - this.I) * var1);
      EntityPlayerSP var8 = this.mc.thePlayer;
      float var9 = var8.getSwingProgress(var1);
      float var10 = var8.prevRotationPitch + (var8.rotationPitch - var8.prevRotationPitch) * var1;
      float var11 = var8.prevRotationYaw + (var8.rotationYaw - var8.prevRotationYaw) * var1;
      this.R(var10, var11);
      this.setLightMapFromPlayer(var8);
      this.rotateWithPlayerRotations(var8, var1);
      GlStateManager.enableRescaleNormal();
      GlStateManager.pushMatrix();
      if (this.itemToRender != null) {
         if (!(this.itemToRender.getItem() instanceof ItemSword) && J) {
            J = false;
         }

         if (this.itemToRender.getItem() instanceof ItemMap) {
            this.renderItemMap(var8, var10, var7, var9);
         } else if (var8.p() <= 0 && !J) {
            this.doItemUsedTransformations(var9);
            this.L(var7, var9);
         } else {
            EnumAction var12 = this.itemToRender.getItemUseAction();
            if (J) {
               var12 = EnumAction.BLOCK;
            }

            label46:
            switch (var12) {
               case NONE:
                  this.L(var7, 0.0F);
                  break;
               case EAT:
               case DRINK:
                  this.performDrinking(var8, var1);
                  this.L(var7, 0.0F);
                  break;
               case BLOCK:
                  if (Astolfo.INSTANCE.getModuleManager().get(Animations.class).isEnabled()) {
                     float var13 = MathHelper.sin((float)(MathHelper.sqrt_float(var9) * 3.1));
                     float var14 = MathHelper.sin((float)(MathHelper.sqrt_float(var9) * Math.PI));
                     GL11.glTranslatef(
                        Animations.position.getSetting("X").asFloat(),
                        Animations.position.getSetting("Y").asFloat(),
                        Animations.position.getSetting("Z").asFloat()
                     );
                     float var16 = var9;
                     float var17 = var7;
                     switch ((AnimationsMode)Animations.mode.getValue()) {
                        case DEFAULT:
                           this.L(var7, var9);
                           this.doBlockTransformations();
                           break label46;
                        case ASTOLFO:
                           this.e(var7, var9);
                           GlStateManager.rotate(-var14 * 25.0F, -8.0F, -0.2F, 9.0F);
                           GlStateManager.rotate(-var13 * 30.0F, 1.0F, var13 / 2.0F, -0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case POINT:
                           this.L(var17 / 4.0F, 0.0F);
                           GlStateManager.rotate(-var14 * 40.0F / 2.0F, var14 / 2.0F, -0.0F, 9.0F);
                           GlStateManager.rotate(-var14 * 30.0F, 1.0F, var14 / 2.0F, -0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case DOWN:
                           GlStateManager.translate(0.0, 0.15F, 0.0);
                           this.L(0.1F, 0.0F);
                           this.doBlockTransformations();
                           GlStateManager.translate(-0.1F, 0.2F, 0.0F);
                           GlStateManager.rotate(-var14 * 80.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                           GlStateManager.rotate(-var14 * 80.0F, 1.0F, -0.4F, -0.0F);
                           break label46;
                        case OLD:
                           GlStateManager.translate(0.08F, -0.027F, -0.33F);
                           this.L(var7, var9);
                           this.doBlockTransformations();
                           break label46;
                        case WINGY:
                           GlStateManager.translate(0.1F, 0.09F, 0.1F);
                           GlStateManager.rotate(-var14 * 25.0F, -8.0F, -0.2F, 9.0F);
                           this.L(var7, var9);
                           this.doBlockTransformations();
                           break label46;
                        case PLAIN:
                           GlStateManager.translate(0.0F, 0.1F, 0.0F);
                           this.L(var7, 0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case SPIN:
                           GlStateManager.rotate(this.t(Animations.spin.getValue()), 0.0F, 1.0F, 5.0F);
                           this.L(var7, 0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case SPINNY:
                           this.L(var7, 0.0F);
                           this.i();
                           break label46;
                        case BLICKY:
                           this.L(var17 / 2.0F, var16);
                           float var19 = MathHelper.sin(MathHelper.sqrt_float(var9) * (float) Math.PI);
                           GlStateManager.rotate(var19 * 30.0F / 2.0F, -var19, -0.0F, 9.0F);
                           GlStateManager.rotate(var19 * 40.0F, 1.0F, -var19 / 2.0F, -0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case FRONT:
                           this.L(var17 / 2.0F, 0.0F);
                           float var18 = MathHelper.sin(var16 * var16 * (float) Math.PI);
                           GlStateManager.rotate(-var18 * 40.0F / 2.0F, var18 / 2.0F, -0.0F, 9.0F);
                           GlStateManager.rotate(-var18 * 30.0F, 1.0F, var18 / 2.0F, -0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case DOWN_2:
                           this.L(var17, 0.0F);
                           this.doBlockTransformations();
                           GlStateManager.rotate(-var14 * 70.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                           GlStateManager.rotate(-var14 * 70.0F, 1.5F, -0.4F, -0.0F);
                           break label46;
                        case DASH:
                           this.L(var17 / 2.0F, 0.0F);
                           GlStateManager.rotate(-var14 * 30.0F, 9.0F, -0.2F, 9.0F);
                           GlStateManager.rotate(-var14 * 45.0F / 2.0F, var14, -0.0F, 9.0F);
                           GlStateManager.rotate(-var14 * 30.0F, var14, var14 / 2.0F, -0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case EDIT:
                           this.L(var17 / 2.0F, var16);
                           float var15 = MathHelper.sin(MathHelper.sqrt_float(var16) * (float) Math.PI);
                           GlStateManager.rotate(var15 * 30.0F, -var15, -0.0F, 9.0F);
                           GlStateManager.rotate(var15 * 40.0F, 1.0F, -var15, -0.0F);
                           this.doBlockTransformations();
                     }
                  } else {
                     this.L(var7, var9);
                     this.doBlockTransformations();
                  }
                  break;
               case BOW:
                  this.L(var7, 0.0F);
                  this.doBowTransformations(var1, var8);
            }
         }

         this.renderItem(var8, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
      } else if (!var8.isInvisible()) {
         this.renderPlayerArm(var8, var7, var9);
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
   }

   public float t(float var1) {
      this.p += var1;
      if (this.p > 360.0F) {
         this.p -= 360.0F;
      }

      return this.p;
   }

   public void renderOverlays(float var1) {
      RenderOverlaysEvent var5 = new RenderOverlaysEvent();
      Astolfo.INSTANCE.getEventBus().post(var5);
      if (!var5.isCancelled()) {
         GlStateManager.disableAlpha();
         if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            IBlockState var6 = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            BlockPos var7 = new BlockPos(this.mc.thePlayer);
            EntityPlayerSP var8 = this.mc.thePlayer;

            for (int var9 = 0; var9 < 8; var9++) {
               double var10 = var8.posX + ((var9 >> 0) % 2 - 0.5F) * var8.width * 0.8F;
               double var12 = var8.posY + ((var9 >> 1) % 2 - 0.5F) * 0.1F;
               double var14 = var8.posZ + ((var9 >> 2) % 2 - 0.5F) * var8.width * 0.8F;
               BlockPos var16 = new BlockPos(var10, var12 + var8.getEyeHeight(), var14);
               IBlockState var17 = this.mc.theWorld.getBlockState(var16);
               if (var17.getBlock().isVisuallyOpaque()) {
                  var6 = var17;
                  var7 = var16;
               }
            }

            if (var6.getBlock().getRenderType() != -1) {
               Object var18 = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);
               if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, this.mc.thePlayer, var1, var18, var6, var7)) {
                  this.renderBlockInHand(var1, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(var6));
               }
            }
         }

         if (!this.mc.thePlayer.isSpectator()) {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water)
               && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, this.mc.thePlayer, var1)) {
               this.renderWaterOverlayTexture(var1);
            }

            if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, this.mc.thePlayer, var1)) {
               this.renderFireInFirstPerson(var1);
            }
         }

         GlStateManager.enableAlpha();
      }
   }

   private void renderBlockInHand(float var1, TextureAtlasSprite var2) {
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      Tessellator var5 = Tessellator.getInstance();
      WorldRenderer var6 = var5.getWorldRenderer();
      GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
      GlStateManager.pushMatrix();
      float var13 = var2.getMinU();
      float var14 = var2.getMaxU();
      float var15 = var2.getMinV();
      float var16 = var2.getMaxV();
      var6.begin(7, DefaultVertexFormats.POSITION_TEX);
      var6.pos(-1.0, -1.0, -0.5).tex(var14, var16).endVertex();
      var6.pos(1.0, -1.0, -0.5).tex(var13, var16).endVertex();
      var6.pos(1.0, 1.0, -0.5).tex(var13, var15).endVertex();
      var6.pos(-1.0, 1.0, -0.5).tex(var14, var15).endVertex();
      var5.draw();
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderWaterOverlayTexture(float var1) {
      long var2 = b ^ 75536758868115L;
      if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
         this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
         Tessellator var5 = Tessellator.getInstance();
         WorldRenderer var6 = var5.getWorldRenderer();
         long var10000 = 4759463353789441697L ^ var2;
         float var7 = this.mc.thePlayer.getBrightness(var1);
         GlStateManager.color(var7, var7, var7, 0.5F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.pushMatrix();
         float var14 = -this.mc.thePlayer.rotationYaw / 64.0F;
         float var15 = this.mc.thePlayer.rotationPitch / 64.0F;
         var6.begin(7, DefaultVertexFormats.POSITION_TEX);
         var6.pos(-1.0, -1.0, -0.5).tex(4.0F + var14, 4.0F + var15).endVertex();
         var6.pos(1.0, -1.0, -0.5).tex(0.0F + var14, 4.0F + var15).endVertex();
         var6.pos(1.0, 1.0, -0.5).tex(0.0F + var14, 0.0F + var15).endVertex();
         var6.pos(-1.0, 1.0, -0.5).tex(4.0F + var14, 0.0F + var15).endVertex();
         var5.draw();
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
      }
   }

   private void renderFireInFirstPerson(float var1) {
      long var2 = b ^ 127489816598533L;
      Tessellator var5 = Tessellator.getInstance();
      WorldRenderer var6 = var5.getWorldRenderer();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
      GlStateManager.depthFunc(519);
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      float var7 = 1.0F;

      for (int var8 = 0; var8 < 2; var8++) {
         GlStateManager.pushMatrix();
         TextureAtlasSprite var9 = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
         this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         float var10 = var9.getMinU();
         float var11 = var9.getMaxU();
         float var12 = var9.getMinV();
         float var13 = var9.getMaxV();
         float var14 = (0.0F - var7) / 2.0F;
         float var15 = var14 + var7;
         float var16 = 0.0F - var7 / 2.0F;
         float var17 = var16 + var7;
         float var18 = -0.5F;
         GlStateManager.translate(-(var8 * 2 - 1) * 0.24F, -0.3F, 0.0F);
         GlStateManager.rotate((var8 * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
         var6.begin(7, DefaultVertexFormats.POSITION_TEX);
         var6.pos(var14, var16, var18).tex(var11, var13).endVertex();
         var6.pos(var15, var16, var18).tex(var10, var13).endVertex();
         var6.pos(var15, var17, var18).tex(var10, var12).endVertex();
         var6.pos(var14, var17, var18).tex(var11, var12).endVertex();
         var5.draw();
         GlStateManager.popMatrix();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      long var10000 = 6639977977637520773L ^ var2;
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
   }

   public void updateEquippedItem() {
      this.I = this.j;
      EntityPlayerSP var4 = this.mc.thePlayer;
      ItemStack var5 = var4.inventory.getCurrentItem();
      boolean var6 = false;
      if (this.itemToRender != null && var5 != null) {
         if (!this.itemToRender.getIsItemStackEqual(var5)) {
            if (Reflector.ForgeItem_shouldCauseReequipAnimation.exists()) {
               boolean var7 = Reflector.callBoolean(
                  this.itemToRender.getItem(),
                  Reflector.ForgeItem_shouldCauseReequipAnimation,
                  this.itemToRender,
                  var5,
                  this.equippedItemSlot != var4.inventory.currentItem
               );
               if (!var7) {
                  this.itemToRender = var5;
                  this.equippedItemSlot = var4.inventory.currentItem;
                  return;
               }
            }

            var6 = true;
         }
      } else if (this.itemToRender == null && var5 == null) {
         var6 = false;
      } else {
         var6 = true;
      }

      float var10 = 0.4F;
      float var8 = var6 ? 0.0F : 1.0F;
      float var9 = MathHelper.clamp_float(var8 - this.j, -var10, var10);
      this.j += var9;
      if (this.j < 0.1F) {
         if (Config.isShaders()) {
            Shaders.setItemToRenderMain(var5);
         }

         this.itemToRender = var5;
         this.equippedItemSlot = var4.inventory.currentItem;
      }
   }

   public void resetEquippedProgress() {
      this.j = 0.0F;
   }

   public void resetEquippedProgress2() {
      this.j = 0.0F;
   }
}
