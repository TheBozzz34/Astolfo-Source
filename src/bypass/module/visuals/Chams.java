package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.render.RenderArmEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderModelEvent;
import bypass.event.render.RenderTileEntityEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.util.GameUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public final class Chams extends Module {
   private final EnumSetting<ChamsMode> mode = new EnumSetting<>("Mode", "Chams mode", ChamsMode.NORMAL);
   private final MapSetting entities = new MapSettingBuilder("Entities")
      .ordered()
      .description("chams")
      .add("Players", new BooleanSetting("Players", "Add players to entities", true))
      .add("Hostiles", new BooleanSetting("Hostiles", "Add monsters to Entities", false))
      .add("Passives", new BooleanSetting("Passives", "Add passives to Entities", false))
      .add("Neutrals", new BooleanSetting("Neutrals", "Add neutrals to Entities", false))
      .add("Bosses", new BooleanSetting("Bosses", "Add bosses to Entities", false))
      .add("Invisibles", new BooleanSetting("Invisibles", "Add invisibles to entities", false))
      .add("Local", new BooleanSetting("Local", "Add local to entities", false))
      .build();
   private final MapSetting blocks = new MapSettingBuilder("Blocks")
      .ordered()
      .add("Chest", new BooleanSetting("Chest", "Add chests to blocks", true))
      .add("Hopper", new BooleanSetting("Hopper", "Add hoppers to blocks", true))
      .add("Ender Chest", new BooleanSetting("Ender-Chest", "Add ender chests to blocks", true))
      .build();
   private final EnumSetting<ChamsColorMode> vColorMode = new EnumSetting<>("V-Color-Mode", "color mode", ChamsColorMode.SOLID);
   private final EnumSetting<ChamsColorMode> hColorMode = new EnumSetting<>("H-Color-Mode", "color mode", ChamsColorMode.SOLID);
   private final ColorSetting vColor = new ColorSetting("V-Color", "Visible color", ChamsColor.PINK.d());
   private final ColorSetting hColor = new ColorSetting("H-Color", "Hidden color", ChamsColor.BLUE.d());
   private final ColorSetting hTimeColor = new ColorSetting("HTime-Color", "poop", ChamsColor.RED.d());
   private final BooleanSetting lightning = new BooleanSetting("Lightning", "Disable lighting", false);
   private final BooleanSetting solid = new BooleanSetting("Solid", "solid chams", true);
   private final BooleanSetting hands = new BooleanSetting("Hands", "Color hands", true);
   @EventHandler(value = RenderModelEvent.class, priority = EventPriority.LOWEST)
   private final Listener<RenderModelEvent> onRenderModel = var1x -> {
      long var2 = e ^ 52652937633881L;
      if (this.shouldRender(var1x.getEntity())) {
         switch (var1x.getState()) {
            case PRE:
               boolean var5 = var1x.getEntity().hurtTime > 0;
               Color var6 = var5 ? this.hTimeColor.getValue() : this.getColor(false);
               Color var7 = var5 ? this.hTimeColor.getValue() : this.getColor(true);
               switch ((ChamsMode)this.mode.getValue()) {
                  case COLOR:
                     var1x.cancel();
                     OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
                     if (!this.lightning.getValue()) {
                        GL11.glDisable(2896);
                     }

                     GL11.glDisable(2929);
                     GL11.glEnable(3042);
                     GL11.glBlendFunc(770, 771);
                     if (this.solid.getValue()) {
                        GL11.glDisable(3553);
                     }

                     RenderUtil.setColor(var6.getRGB());
                     var1x.getRenderer()
                        .renderModel(
                           var1x.getEntity(),
                           var1x.getLimbSwing(),
                           var1x.getLimbSwingAmount(),
                           var1x.getAgeInTicks(),
                           var1x.getNetHeadYaw(),
                           var1x.getHeadPitch(),
                           var1x.getScale()
                        );
                     long var11 = 2244473403450929002L ^ var2;
                     GL11.glEnable(2929);
                     if (this.solid.getValue()) {
                        GL11.glEnable(3553);
                     }

                     RenderUtil.setColor(var7.getRGB());
                     if (this.solid.getValue()) {
                        GL11.glDisable(3553);
                     }

                     var1x.getRenderer()
                        .renderModel(
                           var1x.getEntity(),
                           var1x.getLimbSwing(),
                           var1x.getLimbSwingAmount(),
                           var1x.getAgeInTicks(),
                           var1x.getNetHeadYaw(),
                           var1x.getHeadPitch(),
                           var1x.getScale()
                        );
                     if (this.solid.getValue()) {
                        GL11.glEnable(3553);
                     }

                     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                     break;
                  case OUTLINE:
                     var1x.getRenderer()
                        .renderModel(
                           var1x.getEntity(),
                           var1x.getLimbSwing(),
                           var1x.getLimbSwingAmount(),
                           var1x.getAgeInTicks(),
                           var1x.getNetHeadYaw(),
                           var1x.getHeadPitch(),
                           var1x.getScale()
                        );
                     GL11.glPushAttrib(1048575);
                     GL11.glDisable(3553);
                     GL11.glDisable(2929);
                     GL11.glDisable(2896);
                     GL11.glEnable(3042);
                     GL11.glBlendFunc(770, 771);
                     GL11.glLineWidth(1.1F);
                     long var10000 = 1197606005611931831L ^ var2;
                     GL11.glEnable(2960);
                     GL11.glStencilFunc(512, 1, 15);
                     GL11.glStencilOp(7681, 7681, 7681);
                     GL11.glPolygonMode(1028, 6913);
                     RenderUtil.setColor(var7.getRGB());
                     var10000 = 911193093238641288L ^ var2;
                     var1x.getRenderer()
                        .renderModel(
                           var1x.getEntity(),
                           var1x.getLimbSwing(),
                           var1x.getLimbSwingAmount(),
                           var1x.getAgeInTicks(),
                           var1x.getNetHeadYaw(),
                           var1x.getHeadPitch(),
                           var1x.getScale()
                        );
                     GL11.glStencilFunc(512, 0, 15);
                     GL11.glStencilOp(7681, 7681, 7681);
                     GL11.glPolygonMode(1028, 6914);
                     var1x.getRenderer()
                        .renderModel(
                           var1x.getEntity(),
                           var1x.getLimbSwing(),
                           var1x.getLimbSwingAmount(),
                           var1x.getAgeInTicks(),
                           var1x.getNetHeadYaw(),
                           var1x.getHeadPitch(),
                           var1x.getScale()
                        );
                     var10000 = 6380339839772114269L ^ var2;
                     GL11.glStencilFunc(514, 1, 15);
                     GL11.glStencilOp(7680, 7680, 7680);
                     GL11.glPolygonMode(1028, 6913);
                     GL11.glEnable(10754);
                     OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
                     var10000 = 1197606005611931831L ^ var2;
                     GlStateManager.doPolygonOffset(1.0F, -1.0E7F);
                     var1x.getRenderer()
                        .renderModel(
                           var1x.getEntity(),
                           var1x.getLimbSwing(),
                           var1x.getLimbSwingAmount(),
                           var1x.getAgeInTicks(),
                           var1x.getNetHeadYaw(),
                           var1x.getHeadPitch(),
                           var1x.getScale()
                        );
                     GlStateManager.doPolygonOffset(1.0F, 1.0E7F);
                     GL11.glDisable(2960);
                     GL11.glEnable(3553);
                     GL11.glEnable(2896);
                     GL11.glDisable(3042);
                     GL11.glPopAttrib();
               }
            case POST:
         }
      }
   };
   @EventHandler(value = RenderTileEntityEvent.class, priority = EventPriority.LOWEST)
   private final Listener<RenderTileEntityEvent> onRenderTileEntity = var1x -> {
      long var2 = e ^ 25534541032515L;
      if (this.shouldRenderTileEntity(var1x.getTileEntity())) {
         switch (var1x.getState()) {
            case PRE:
               Color var5 = this.getColor(false);
               Color var6 = this.getColor(true);
               switch ((ChamsMode)this.mode.getValue()) {
                  case COLOR:
                     var1x.cancel();
                     OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
                     if (!this.lightning.getValue()) {
                        GL11.glDisable(2896);
                     }

                     GL11.glDisable(2929);
                     GL11.glEnable(3042);
                     GL11.glBlendFunc(770, 771);
                     if (this.solid.getValue()) {
                        GL11.glDisable(3553);
                     }

                     RenderUtil.setColor(var5.getRGB());
                     var1x.getDispatcher()
                        .renderTileEntityAt(
                           var1x.getTileEntity(),
                           var1x.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           var1x.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           var1x.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           var1x.getPartialTicks(),
                           var1x.getDestroyStage()
                        );
                     long var13 = 2244411581919830896L ^ var2;
                     GL11.glEnable(2929);
                     if (this.solid.getValue()) {
                        GL11.glEnable(3553);
                     }

                     RenderUtil.setColor(var6.getRGB());
                     if (this.solid.getValue()) {
                        GL11.glDisable(3553);
                     }

                     var1x.getDispatcher()
                        .renderTileEntityAt(
                           var1x.getTileEntity(),
                           var1x.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           var1x.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           var1x.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           var1x.getPartialTicks(),
                           var1x.getDestroyStage()
                        );
                     if (this.solid.getValue()) {
                        GL11.glEnable(3553);
                     }

                     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                     break;
                  case NORMAL:
                     boolean var7 = mc.gameSettings.viewBobbing;
                     mc.gameSettings.viewBobbing = false;
                     var1x.cancel();
                     long var11 = 3359551688184540551L ^ var2;
                     GL11.glEnable(32823);
                     GlStateManager.enablePolygonOffset();
                     var11 = 7496973731016948420L ^ var2;
                     GlStateManager.doPolygonOffset(1.0F, -1.0E7F);
                     var1x.getDispatcher()
                        .renderTileEntityAt(
                           var1x.getTileEntity(),
                           var1x.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           var1x.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           var1x.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           var1x.getPartialTicks(),
                           var1x.getDestroyStage()
                        );
                     GL11.glDisable(32823);
                     GlStateManager.doPolygonOffset(1.0F, 1.0E7F);
                     GlStateManager.disablePolygonOffset();
                     mc.gameSettings.viewBobbing = var7;
                     break;
                  case OUTLINE:
                     var1x.getDispatcher()
                        .renderTileEntityAt(
                           var1x.getTileEntity(),
                           var1x.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           var1x.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           var1x.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           var1x.getPartialTicks(),
                           var1x.getDestroyStage()
                        );
                     GL11.glPushAttrib(1048575);
                     GL11.glDisable(3553);
                     GL11.glDisable(2929);
                     GL11.glDisable(2896);
                     GL11.glEnable(3042);
                     GL11.glBlendFunc(770, 771);
                     GL11.glLineWidth(1.0F);
                     long var10000 = 3570051404827713652L ^ var2;
                     GL11.glEnable(2960);
                     GL11.glStencilFunc(512, 1, 15);
                     GL11.glStencilOp(7681, 7681, 7681);
                     GL11.glPolygonMode(1028, 6913);
                     RenderUtil.setColor(var6.getRGB());
                     var10000 = 911149413531704978L ^ var2;
                     var1x.getDispatcher()
                        .renderTileEntityAt(
                           var1x.getTileEntity(),
                           var1x.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           var1x.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           var1x.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           var1x.getPartialTicks(),
                           var1x.getDestroyStage()
                        );
                     GL11.glStencilFunc(512, 0, 15);
                     GL11.glStencilOp(7681, 7681, 7681);
                     GL11.glPolygonMode(1028, 6914);
                     var1x.getDispatcher()
                        .renderTileEntityAt(
                           var1x.getTileEntity(),
                           var1x.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           var1x.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           var1x.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           var1x.getPartialTicks(),
                           var1x.getDestroyStage()
                        );
                     var10000 = 4231120209782579225L ^ var2;
                     GL11.glStencilFunc(514, 1, 15);
                     GL11.glStencilOp(7680, 7680, 7680);
                     GL11.glPolygonMode(1028, 6913);
                     GL11.glEnable(10754);
                     OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
                     var10000 = 1197667827612656813L ^ var2;
                     GlStateManager.doPolygonOffset(1.0F, -1.0E7F);
                     var1x.getDispatcher()
                        .renderTileEntityAt(
                           var1x.getTileEntity(),
                           var1x.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           var1x.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           var1x.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           var1x.getPartialTicks(),
                           var1x.getDestroyStage()
                        );
                     GlStateManager.doPolygonOffset(1.0F, 1.0E7F);
                     GL11.glDisable(2960);
                     GL11.glEnable(3553);
                     GL11.glEnable(2896);
                     GL11.glDisable(3042);
                     GL11.glPopAttrib();
               }
         }
      }
   };
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private final Listener<RenderEvent> onRender = var1x -> {
      if (var1x.getType() == RenderType.WORLD && this.mode.getValue() == ChamsMode.NORMAL) {
         GlStateManager.alphaFunc(516, 0.001F);
         GlStateManager.clear(256);
         RenderHelper.enableStandardItemLighting();

         for (Entity var6 : mc.theWorld.getLoadedEntityList()) {
            if (var6 != null
               && mc.thePlayer.ticksExisted >= 10
               && (var6 != mc.getRenderViewEntity() || mc.gameSettings.thirdPersonView != 0)
               && this.shouldRender(var6)) {
               if (!this.lightning.getValue()) {
                  mc.entityRenderer.disableLightmap();
               }

               mc.getRenderManager().renderEntityStatic(var6, mc.timer.renderPartialTicks, false);
               if (!this.lightning.getValue()) {
                  mc.entityRenderer.enableLightmap();
               }
            }
         }

         GlStateManager.alphaFunc(516, 0.1F);
      }
   };
   @EventHandler(value = RenderArmEvent.class, priority = EventPriority.LOWEST)
   private final Listener<RenderArmEvent> onRenderArm = var1x -> {
      if (this.hands.getValue()) {
         switch (var1x.getState()) {
            case PRE:
               RenderUtil.setColor(this.vColor.getValue().getRGB());
               GL11.glDisable(3553);
               if (!this.lightning.getValue()) {
                  GL11.glDisable(2896);
               }

               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               break;
            case POST:
               GL11.glEnable(3553);
               if (!this.lightning.getValue()) {
                  GL11.glEnable(2896);
               }

               GL11.glDisable(3042);
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   };
   private static final long e = 103934299307939L;

   public Chams(Category var1) {
      super(var1);
      this.addSettings(
         this.mode,
         this.entities,
         this.blocks,
         this.vColor,
         this.hColor,
         this.hTimeColor,
         this.vColorMode,
         this.hColorMode,
         this.lightning,
         this.solid,
         this.hands
      );
      this.setHidden(false);
   }

   private boolean shouldRender(Entity var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof EntityLivingBase)) {
         return false;
      } else if (var1.isInvisible()) {
         return false;
      } else if (var1.getEntityId() == -1337 || var1.getEntityId() == -420) {
         return false;
      } else {
         return (!(var1 instanceof EntityPlayer) || var1 == mc.thePlayer)
               && (var1 != mc.thePlayer || mc.gameSettings.thirdPersonView <= 0 || !this.entities.getSetting("Local").asBoolean())
            ? GameUtil.isHostile(var1) && this.entities.getSetting("Hostiles").asBoolean()
               || GameUtil.isNeutral(var1) && this.entities.getSetting("Neutrals").asBoolean()
               || GameUtil.isPassive(var1) && this.entities.getSetting("Passives").asBoolean()
               || GameUtil.isBoss(var1) && this.entities.getSetting("Bosses").asBoolean()
            : this.entities.getSetting("Players").asBoolean();
      }
   }

   private Color getColor(boolean var1) {
      float var5 = 1.0F - MathHelper.abs(MathHelper.sin((float)Minecraft.getSystemTime() % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.6F);
      if (var1) {
         switch ((ChamsColorMode)this.vColorMode.getValue()) {
            case SOLID:
               return this.vColor.getValue();
            case BREATH:
               return new Color(
                  MathHelper.clamp_int((int)(this.vColor.getValue().getRed() * var5), 0, 255),
                  MathHelper.clamp_int((int)(this.vColor.getValue().getGreen() * var5), 0, 255),
                  MathHelper.clamp_int((int)(this.vColor.getValue().getBlue() * var5), 0, 255),
                  this.vColor.getValue().getAlpha()
               );
         }
      } else {
         switch ((ChamsColorMode)this.hColorMode.getValue()) {
            case SOLID:
               return this.hColor.getValue();
            case BREATH:
               return new Color(
                  MathHelper.clamp_int((int)(this.hColor.getValue().getRed() * var5), 0, 255),
                  MathHelper.clamp_int((int)(this.hColor.getValue().getGreen() * var5), 0, 255),
                  MathHelper.clamp_int((int)(this.hColor.getValue().getBlue() * var5), 0, 255),
                  this.hColor.getValue().getAlpha()
               );
         }
      }

      return Color.RED;
   }

   private boolean shouldRenderTileEntity(TileEntity var1) {
      return var1 == null
         ? false
         : var1 instanceof TileEntityEnderChest && this.blocks.getSetting("Ender Chest").asBoolean()
            || var1 instanceof TileEntityChest && this.blocks.getSetting("Chest").asBoolean()
            || var1 instanceof TileEntityHopper && this.blocks.getSetting("Hopper").asBoolean();
   }
}
