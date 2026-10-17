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
   private float equippedProgress;
   private float prevEquippedProgress;
   private final RenderManager renderManager;
   private final RenderItem itemRenderer;
   private int equippedItemSlot = -1;
   public static boolean fakeBlocking;
   private float spinAngle = 0.0F;

   public ItemRenderer(Minecraft mcIn) {
      this.mc = mcIn;
      this.renderManager = mcIn.getRenderManager();
      this.itemRenderer = mcIn.getRenderItem();
   }

   public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
      if (heldStack != null) {
         RenderItemEvent renderItemEvent = new RenderItemEvent(entityIn, heldStack);
         Astolfo.INSTANCE.getEventBus().post(renderItemEvent);
         if (!renderItemEvent.isCancelled()) {
            Item item = heldStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();
            if (this.itemRenderer.shouldRenderItemIn3D(heldStack)) {
               GlStateManager.scale(2.0F, 2.0F, 2.0F);
               if (this.isBlockTranslucent(block) && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask)) {
                  GlStateManager.depthMask(false);
               }
            }

            this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);
            if (this.isBlockTranslucent(block)) {
               GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
         }
      }
   }

   private boolean isBlockTranslucent(Block blockIn) {
      return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
   }

   private void rotateArroundXAndY(float angle, float angleY) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   private void setLightMapFromPlayer(AbstractClientPlayer clientPlayer) {
      int i = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
      if (Config.isDynamicLights()) {
         i = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), i);
      }

      float f = i & 65535;
      float g = i >> 16;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, g);
   }

   private void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks) {
      float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
      float g = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
      GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate((entityplayerspIn.rotationYaw - g) * 0.1F, 0.0F, 1.0F, 0.0F);
   }

   private float getMapAngleFromPitch(float pitch) {
      float f = 1.0F - pitch / 45.0F + 0.1F;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      return -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
   }

   private void renderRightArm(RenderPlayer renderPlayerIn) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(0.25F, -0.85F, 0.75F);
      renderPlayerIn.renderRightArm(this.mc.thePlayer);
      GlStateManager.popMatrix();
   }

   private void renderLeftArm(RenderPlayer renderPlayerIn) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-0.3F, -1.1F, 0.45F);
      renderPlayerIn.renderLeftArm(this.mc.thePlayer);
      GlStateManager.popMatrix();
   }

   private void renderPlayerArms(AbstractClientPlayer clientPlayer) {
      this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
      Render render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
      RenderPlayer renderPlayer = (RenderPlayer)render;
      if (!clientPlayer.isInvisible()) {
         GlStateManager.disableCull();
         this.renderRightArm(renderPlayer);
         this.renderLeftArm(renderPlayer);
         GlStateManager.enableCull();
      }
   }

   private void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress) {
      float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      float g = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F);
      float h = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
      GlStateManager.translate(f, g, h);
      float f2 = this.getMapAngleFromPitch(pitch);
      GlStateManager.translate(0.0F, 0.04F, -0.72F);
      GlStateManager.translate(0.0F, equipmentProgress * -1.2F, 0.0F);
      GlStateManager.translate(0.0F, f2 * -0.5F, 0.0F);
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f2 * -85.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
      this.renderPlayerArms(clientPlayer);
      float f3 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float f4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(f3 * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f4 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(f4 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.38F, 0.38F, 0.38F);
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-1.0F, -1.0F, 0.0F);
      GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
      this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GL11.glNormal3f(0.0F, 0.0F, -1.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(-7.0, 135.0, 0.0).tex(0.0, 1.0).endVertex();
      worldRenderer.pos(135.0, 135.0, 0.0).tex(1.0, 1.0).endVertex();
      worldRenderer.pos(135.0, -7.0, 0.0).tex(1.0, 0.0).endVertex();
      worldRenderer.pos(-7.0, -7.0, 0.0).tex(0.0, 0.0).endVertex();
      tessellator.draw();
      MapData mapData = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);
      if (mapData != null) {
         this.mc.entityRenderer.getMapItemRenderer().renderMap(mapData, false);
      }
   }

   private void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress) {
      Astolfo.INSTANCE.getEventBus().post(new RenderArmEvent(EventState.PRE));
      float f = -0.3F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      float g = 0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F);
      float h = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);
      GlStateManager.translate(f, g, h);
      GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
      GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float f2 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float f3 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(f3 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f2 * -20.0F, 0.0F, 0.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
      GlStateManager.translate(-1.0F, 3.6F, 3.5F);
      GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      GlStateManager.translate(5.6F, 0.0F, 0.0F);
      Render render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
      GlStateManager.disableCull();
      RenderPlayer renderPlayer = (RenderPlayer)render;
      renderPlayer.renderRightArm(this.mc.thePlayer);
      GlStateManager.enableCull();
      Astolfo.INSTANCE.getEventBus().post(new RenderArmEvent(EventState.POST));
   }

   private void doItemUsedTransformations(float swingProgress) {
      float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      float g = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F);
      float h = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
      GlStateManager.translate(f, g, h);
   }

   private void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks) {
      float f = clientPlayer.getItemInUseCount() - partialTicks + 1.0F;
      float g = f / this.itemToRender.getMaxItemUseDuration();
      float h = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
      if (g >= 0.8F) {
         h = 0.0F;
      }

      GlStateManager.translate(0.0F, h, 0.0F);
      float f2 = 1.0F - (float)Math.pow(g, 27.0);
      GlStateManager.translate(f2 * 0.6F, f2 * -0.5F, f2 * 0.0F);
      GlStateManager.rotate(f2 * 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f2 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(f2 * 30.0F, 0.0F, 0.0F, 1.0F);
   }

   private void transformFirstPersonItem(float equipProgress, float swingProgress) {
      GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
      if (Astolfo.INSTANCE.getModuleManager().get(Camera.class).isEnabled()) {
         GlStateManager.translate(
            Camera.itemPosition.getSetting("X").asFloat(), Camera.itemPosition.getSetting("Y").asFloat(), Camera.itemPosition.getSetting("Z").asFloat()
         );
      }

      GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float f = MathHelper.sin((float)(swingProgress * swingProgress * Math.PI));
      float g = MathHelper.sin((float)(MathHelper.sqrt_float(swingProgress) * Math.PI));
      GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(g * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(g * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.4F, 0.4F, 0.4F);
   }

   private void transformFirstPersonItemAstolfo(float f, float g) {
      GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
      GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float h = MathHelper.sin(g * g * (float) Math.PI);
      float f2 = MathHelper.sin(MathHelper.sqrt_float(g) * (float) Math.PI);
      GlStateManager.rotate(h * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f2 * -1.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.4F, 0.4F, 0.4F);
   }

   private void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer) {
      GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-0.9F, 0.2F, 0.0F);
      float f = this.itemToRender.getMaxItemUseDuration() - (clientPlayer.getItemInUseCount() - partialTicks + 1.0F);
      float g = f / 20.0F;
      g = (g * g + g * 2.0F) / 3.0F;
      if (g > 1.0F) {
         g = 1.0F;
      }

      if (g > 0.1F) {
         float h = MathHelper.sin((f - 0.1F) * 1.3F);
         float f2 = g - 0.1F;
         float f3 = h * f2;
         GlStateManager.translate(f3 * 0.0F, f3 * 0.01F, f3 * 0.0F);
      }

      GlStateManager.translate(g * 0.0F, g * 0.0F, g * 0.1F);
      GlStateManager.scale(1.0F, 1.0F, 1.0F + g * 0.2F);
   }

   private void doBlockTransformations() {
      GlStateManager.translate(-0.5F, 0.2F, 0.0F);
      GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
   }

   private void doSpinTransformations() {
      GlStateManager.translate(0.5F, 0.1F, 0.0F);
      GlStateManager.rotate(10.0F, -2.0F, 1.0F, 0.0F);
      GlStateManager.rotate(this.updateSpinAngle(Animations.spin.getValue()), 0.0F, 1.0F, 0.0F);
   }

   public void renderItemInFirstPerson(float partialTicks) {
      float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
      EntityPlayerSP entityPlayerSP = this.mc.thePlayer;
      float g = entityPlayerSP.getSwingProgress(partialTicks);
      float h = entityPlayerSP.prevRotationPitch + (entityPlayerSP.rotationPitch - entityPlayerSP.prevRotationPitch) * partialTicks;
      float f2 = entityPlayerSP.prevRotationYaw + (entityPlayerSP.rotationYaw - entityPlayerSP.prevRotationYaw) * partialTicks;
      this.rotateArroundXAndY(h, f2);
      this.setLightMapFromPlayer(entityPlayerSP);
      this.rotateWithPlayerRotations(entityPlayerSP, partialTicks);
      GlStateManager.enableRescaleNormal();
      GlStateManager.pushMatrix();
      if (this.itemToRender != null) {
         if (!(this.itemToRender.getItem() instanceof ItemSword) && fakeBlocking) {
            fakeBlocking = false;
         }

         if (this.itemToRender.getItem() instanceof ItemMap) {
            this.renderItemMap(entityPlayerSP, h, f, g);
         } else if (entityPlayerSP.getItemInUseCount() <= 0 && !fakeBlocking) {
            this.doItemUsedTransformations(g);
            this.transformFirstPersonItem(f, g);
         } else {
            EnumAction enumAction = this.itemToRender.getItemUseAction();
            if (fakeBlocking) {
               enumAction = EnumAction.BLOCK;
            }

            label46:
            switch (enumAction) {
               case NONE:
                  this.transformFirstPersonItem(f, 0.0F);
                  break;
               case EAT:
               case DRINK:
                  this.performDrinking(entityPlayerSP, partialTicks);
                  this.transformFirstPersonItem(f, 0.0F);
                  break;
               case BLOCK:
                  if (Astolfo.INSTANCE.getModuleManager().get(Animations.class).isEnabled()) {
                     float f3 = MathHelper.sin((float)(MathHelper.sqrt_float(g) * 3.1));
                     float f4 = MathHelper.sin((float)(MathHelper.sqrt_float(g) * Math.PI));
                     GL11.glTranslatef(
                        Animations.position.getSetting("X").asFloat(),
                        Animations.position.getSetting("Y").asFloat(),
                        Animations.position.getSetting("Z").asFloat()
                     );
                     float f5 = g;
                     float f6 = f;
                     switch ((AnimationsMode)Animations.mode.getValue()) {
                        case DEFAULT:
                           this.transformFirstPersonItem(f, g);
                           this.doBlockTransformations();
                           break label46;
                        case ASTOLFO:
                           this.transformFirstPersonItemAstolfo(f, g);
                           GlStateManager.rotate(-f4 * 25.0F, -8.0F, -0.2F, 9.0F);
                           GlStateManager.rotate(-f3 * 30.0F, 1.0F, f3 / 2.0F, -0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case POINT:
                           this.transformFirstPersonItem(f6 / 4.0F, 0.0F);
                           GlStateManager.rotate(-f4 * 40.0F / 2.0F, f4 / 2.0F, -0.0F, 9.0F);
                           GlStateManager.rotate(-f4 * 30.0F, 1.0F, f4 / 2.0F, -0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case DOWN:
                           GlStateManager.translate(0.0, 0.15F, 0.0);
                           this.transformFirstPersonItem(0.1F, 0.0F);
                           this.doBlockTransformations();
                           GlStateManager.translate(-0.1F, 0.2F, 0.0F);
                           GlStateManager.rotate(-f4 * 80.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                           GlStateManager.rotate(-f4 * 80.0F, 1.0F, -0.4F, -0.0F);
                           break label46;
                        case OLD:
                           GlStateManager.translate(0.08F, -0.027F, -0.33F);
                           this.transformFirstPersonItem(f, g);
                           this.doBlockTransformations();
                           break label46;
                        case WINGY:
                           GlStateManager.translate(0.1F, 0.09F, 0.1F);
                           GlStateManager.rotate(-f4 * 25.0F, -8.0F, -0.2F, 9.0F);
                           this.transformFirstPersonItem(f, g);
                           this.doBlockTransformations();
                           break label46;
                        case PLAIN:
                           GlStateManager.translate(0.0F, 0.1F, 0.0F);
                           this.transformFirstPersonItem(f, 0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case SPIN:
                           GlStateManager.rotate(this.updateSpinAngle(Animations.spin.getValue()), 0.0F, 1.0F, 5.0F);
                           this.transformFirstPersonItem(f, 0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case SPINNY:
                           this.transformFirstPersonItem(f, 0.0F);
                           this.doSpinTransformations();
                           break label46;
                        case BLICKY:
                           this.transformFirstPersonItem(f6 / 2.0F, f5);
                           float f7 = MathHelper.sin(MathHelper.sqrt_float(g) * (float) Math.PI);
                           GlStateManager.rotate(f7 * 30.0F / 2.0F, -f7, -0.0F, 9.0F);
                           GlStateManager.rotate(f7 * 40.0F, 1.0F, -f7 / 2.0F, -0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case FRONT:
                           this.transformFirstPersonItem(f6 / 2.0F, 0.0F);
                           float f8 = MathHelper.sin(f5 * f5 * (float) Math.PI);
                           GlStateManager.rotate(-f8 * 40.0F / 2.0F, f8 / 2.0F, -0.0F, 9.0F);
                           GlStateManager.rotate(-f8 * 30.0F, 1.0F, f8 / 2.0F, -0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case DOWN_2:
                           this.transformFirstPersonItem(f6, 0.0F);
                           this.doBlockTransformations();
                           GlStateManager.rotate(-f4 * 70.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                           GlStateManager.rotate(-f4 * 70.0F, 1.5F, -0.4F, -0.0F);
                           break label46;
                        case DASH:
                           this.transformFirstPersonItem(f6 / 2.0F, 0.0F);
                           GlStateManager.rotate(-f4 * 30.0F, 9.0F, -0.2F, 9.0F);
                           GlStateManager.rotate(-f4 * 45.0F / 2.0F, f4, -0.0F, 9.0F);
                           GlStateManager.rotate(-f4 * 30.0F, f4, f4 / 2.0F, -0.0F);
                           this.doBlockTransformations();
                           break label46;
                        case EDIT:
                           this.transformFirstPersonItem(f6 / 2.0F, f5);
                           float f9 = MathHelper.sin(MathHelper.sqrt_float(f5) * (float) Math.PI);
                           GlStateManager.rotate(f9 * 30.0F, -f9, -0.0F, 9.0F);
                           GlStateManager.rotate(f9 * 40.0F, 1.0F, -f9, -0.0F);
                           this.doBlockTransformations();
                     }
                  } else {
                     this.transformFirstPersonItem(f, g);
                     this.doBlockTransformations();
                  }
                  break;
               case BOW:
                  this.transformFirstPersonItem(f, 0.0F);
                  this.doBowTransformations(partialTicks, entityPlayerSP);
            }
         }

         this.renderItem(entityPlayerSP, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
      } else if (!entityPlayerSP.isInvisible()) {
         this.renderPlayerArm(entityPlayerSP, f, g);
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
   }

   public float updateSpinAngle(float f) {
      this.spinAngle += f;
      if (this.spinAngle > 360.0F) {
         this.spinAngle -= 360.0F;
      }

      return this.spinAngle;
   }

   public void renderOverlays(float partialTicks) {
      RenderOverlaysEvent renderOverlaysEvent = new RenderOverlaysEvent();
      Astolfo.INSTANCE.getEventBus().post(renderOverlaysEvent);
      if (!renderOverlaysEvent.isCancelled()) {
         GlStateManager.disableAlpha();
         if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            IBlockState iBlockState = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            BlockPos blockPos = new BlockPos(this.mc.thePlayer);
            EntityPlayerSP entityPlayerSP = this.mc.thePlayer;

            for (int i = 0; i < 8; i++) {
               double d = entityPlayerSP.posX + ((i >> 0) % 2 - 0.5F) * entityPlayerSP.width * 0.8F;
               double e = entityPlayerSP.posY + ((i >> 1) % 2 - 0.5F) * 0.1F;
               double d2 = entityPlayerSP.posZ + ((i >> 2) % 2 - 0.5F) * entityPlayerSP.width * 0.8F;
               BlockPos blockPos2 = new BlockPos(d, e + entityPlayerSP.getEyeHeight(), d2);
               IBlockState iBlockState2 = this.mc.theWorld.getBlockState(blockPos2);
               if (iBlockState2.getBlock().isVisuallyOpaque()) {
                  iBlockState = iBlockState2;
                  blockPos = blockPos2;
               }
            }

            if (iBlockState.getBlock().getRenderType() != -1) {
               Object object = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);
               if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, this.mc.thePlayer, partialTicks, object, iBlockState, blockPos)) {
                  this.renderBlockInHand(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iBlockState));
               }
            }
         }

         if (!this.mc.thePlayer.isSpectator()) {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water)
               && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, this.mc.thePlayer, partialTicks)) {
               this.renderWaterOverlayTexture(partialTicks);
            }

            if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, this.mc.thePlayer, partialTicks)) {
               this.renderFireInFirstPerson(partialTicks);
            }
         }

         GlStateManager.enableAlpha();
      }
   }

   private void renderBlockInHand(float partialTicks, TextureAtlasSprite atlas) {
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
      GlStateManager.pushMatrix();
      float f = atlas.getMinU();
      float g = atlas.getMaxU();
      float h = atlas.getMinV();
      float f2 = atlas.getMaxV();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(-1.0, -1.0, -0.5).tex(g, f2).endVertex();
      worldRenderer.pos(1.0, -1.0, -0.5).tex(f, f2).endVertex();
      worldRenderer.pos(1.0, 1.0, -0.5).tex(f, h).endVertex();
      worldRenderer.pos(-1.0, 1.0, -0.5).tex(g, h).endVertex();
      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderWaterOverlayTexture(float partialTicks) {
      if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
         this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         float f = this.mc.thePlayer.getBrightness(partialTicks);
         GlStateManager.color(f, f, f, 0.5F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.pushMatrix();
         float g = -this.mc.thePlayer.rotationYaw / 64.0F;
         float h = this.mc.thePlayer.rotationPitch / 64.0F;
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(-1.0, -1.0, -0.5).tex(4.0F + g, 4.0F + h).endVertex();
         worldRenderer.pos(1.0, -1.0, -0.5).tex(0.0F + g, 4.0F + h).endVertex();
         worldRenderer.pos(1.0, 1.0, -0.5).tex(0.0F + g, 0.0F + h).endVertex();
         worldRenderer.pos(-1.0, 1.0, -0.5).tex(4.0F + g, 0.0F + h).endVertex();
         tessellator.draw();
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
      }
   }

   private void renderFireInFirstPerson(float partialTicks) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
      GlStateManager.depthFunc(519);
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      float f = 1.0F;

      for (int i = 0; i < 2; i++) {
         GlStateManager.pushMatrix();
         TextureAtlasSprite textureAtlasSprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
         this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         float g = textureAtlasSprite.getMinU();
         float h = textureAtlasSprite.getMaxU();
         float f2 = textureAtlasSprite.getMinV();
         float f3 = textureAtlasSprite.getMaxV();
         float f4 = (0.0F - f) / 2.0F;
         float f5 = f4 + f;
         float f6 = 0.0F - f / 2.0F;
         float f7 = f6 + f;
         float f8 = -0.5F;
         GlStateManager.translate(-(i * 2 - 1) * 0.24F, -0.3F, 0.0F);
         GlStateManager.rotate((i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(f4, f6, f8).tex(h, f3).endVertex();
         worldRenderer.pos(f5, f6, f8).tex(g, f3).endVertex();
         worldRenderer.pos(f5, f7, f8).tex(g, f2).endVertex();
         worldRenderer.pos(f4, f7, f8).tex(h, f2).endVertex();
         tessellator.draw();
         GlStateManager.popMatrix();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
   }

   public void updateEquippedItem() {
      this.prevEquippedProgress = this.equippedProgress;
      EntityPlayerSP entityPlayerSP = this.mc.thePlayer;
      ItemStack itemStack = entityPlayerSP.inventory.getCurrentItem();
      boolean bl = false;
      if (this.itemToRender != null && itemStack != null) {
         if (!this.itemToRender.getIsItemStackEqual(itemStack)) {
            if (Reflector.ForgeItem_shouldCauseReequipAnimation.exists()) {
               boolean bl2 = Reflector.callBoolean(
                  this.itemToRender.getItem(),
                  Reflector.ForgeItem_shouldCauseReequipAnimation,
                  this.itemToRender,
                  itemStack,
                  this.equippedItemSlot != entityPlayerSP.inventory.currentItem
               );
               if (!bl2) {
                  this.itemToRender = itemStack;
                  this.equippedItemSlot = entityPlayerSP.inventory.currentItem;
                  return;
               }
            }

            bl = true;
         }
      } else if (this.itemToRender == null && itemStack == null) {
         bl = false;
      } else {
         bl = true;
      }

      float f = 0.4F;
      float g = bl ? 0.0F : 1.0F;
      float h = MathHelper.clamp_float(g - this.equippedProgress, -f, f);
      this.equippedProgress += h;
      if (this.equippedProgress < 0.1F) {
         if (Config.isShaders()) {
            Shaders.setItemToRenderMain(itemStack);
         }

         this.itemToRender = itemStack;
         this.equippedItemSlot = entityPlayerSP.inventory.currentItem;
      }
   }

   public void resetEquippedProgress() {
      this.equippedProgress = 0.0F;
   }

   public void resetEquippedProgress2() {
      this.equippedProgress = 0.0F;
   }
}
