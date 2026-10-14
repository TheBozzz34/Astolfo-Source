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
   private final Listener<RenderModelEvent> onRenderModel = renderModelEvent -> {
      long l = e ^ 52652937633881L;
      if (this.shouldRender(renderModelEvent.getEntity())) {
         switch (renderModelEvent.getState()) {
            case PRE:
               boolean bl = renderModelEvent.getEntity().hurtTime > 0;
               Color color = bl ? this.hTimeColor.getValue() : this.getColor(false);
               Color color2 = bl ? this.hTimeColor.getValue() : this.getColor(true);
               switch ((ChamsMode)this.mode.getValue()) {
                  case COLOR:
                     renderModelEvent.cancel();
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

                     RenderUtil.setColor(color.getRGB());
                     renderModelEvent.getRenderer()
                        .renderModel(
                           renderModelEvent.getEntity(),
                           renderModelEvent.getLimbSwing(),
                           renderModelEvent.getLimbSwingAmount(),
                           renderModelEvent.getAgeInTicks(),
                           renderModelEvent.getNetHeadYaw(),
                           renderModelEvent.getHeadPitch(),
                           renderModelEvent.getScale()
                        );
                     long m = 2244473403450929002L ^ l;
                     GL11.glEnable(2929);
                     if (this.solid.getValue()) {
                        GL11.glEnable(3553);
                     }

                     RenderUtil.setColor(color2.getRGB());
                     if (this.solid.getValue()) {
                        GL11.glDisable(3553);
                     }

                     renderModelEvent.getRenderer()
                        .renderModel(
                           renderModelEvent.getEntity(),
                           renderModelEvent.getLimbSwing(),
                           renderModelEvent.getLimbSwingAmount(),
                           renderModelEvent.getAgeInTicks(),
                           renderModelEvent.getNetHeadYaw(),
                           renderModelEvent.getHeadPitch(),
                           renderModelEvent.getScale()
                        );
                     if (this.solid.getValue()) {
                        GL11.glEnable(3553);
                     }

                     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                     break;
                  case OUTLINE:
                     renderModelEvent.getRenderer()
                        .renderModel(
                           renderModelEvent.getEntity(),
                           renderModelEvent.getLimbSwing(),
                           renderModelEvent.getLimbSwingAmount(),
                           renderModelEvent.getAgeInTicks(),
                           renderModelEvent.getNetHeadYaw(),
                           renderModelEvent.getHeadPitch(),
                           renderModelEvent.getScale()
                        );
                     GL11.glPushAttrib(1048575);
                     GL11.glDisable(3553);
                     GL11.glDisable(2929);
                     GL11.glDisable(2896);
                     GL11.glEnable(3042);
                     GL11.glBlendFunc(770, 771);
                     GL11.glLineWidth(1.1F);
                     long n = 1197606005611931831L ^ l;
                     GL11.glEnable(2960);
                     GL11.glStencilFunc(512, 1, 15);
                     GL11.glStencilOp(7681, 7681, 7681);
                     GL11.glPolygonMode(1028, 6913);
                     RenderUtil.setColor(color2.getRGB());
                     n = 911193093238641288L ^ l;
                     renderModelEvent.getRenderer()
                        .renderModel(
                           renderModelEvent.getEntity(),
                           renderModelEvent.getLimbSwing(),
                           renderModelEvent.getLimbSwingAmount(),
                           renderModelEvent.getAgeInTicks(),
                           renderModelEvent.getNetHeadYaw(),
                           renderModelEvent.getHeadPitch(),
                           renderModelEvent.getScale()
                        );
                     GL11.glStencilFunc(512, 0, 15);
                     GL11.glStencilOp(7681, 7681, 7681);
                     GL11.glPolygonMode(1028, 6914);
                     renderModelEvent.getRenderer()
                        .renderModel(
                           renderModelEvent.getEntity(),
                           renderModelEvent.getLimbSwing(),
                           renderModelEvent.getLimbSwingAmount(),
                           renderModelEvent.getAgeInTicks(),
                           renderModelEvent.getNetHeadYaw(),
                           renderModelEvent.getHeadPitch(),
                           renderModelEvent.getScale()
                        );
                     n = 6380339839772114269L ^ l;
                     GL11.glStencilFunc(514, 1, 15);
                     GL11.glStencilOp(7680, 7680, 7680);
                     GL11.glPolygonMode(1028, 6913);
                     GL11.glEnable(10754);
                     OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
                     n = 1197606005611931831L ^ l;
                     GlStateManager.doPolygonOffset(1.0F, -1.0E7F);
                     renderModelEvent.getRenderer()
                        .renderModel(
                           renderModelEvent.getEntity(),
                           renderModelEvent.getLimbSwing(),
                           renderModelEvent.getLimbSwingAmount(),
                           renderModelEvent.getAgeInTicks(),
                           renderModelEvent.getNetHeadYaw(),
                           renderModelEvent.getHeadPitch(),
                           renderModelEvent.getScale()
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
   private final Listener<RenderTileEntityEvent> onRenderTileEntity = renderTileEntityEvent -> {
      long l = e ^ 25534541032515L;
      if (this.shouldRenderTileEntity(renderTileEntityEvent.getTileEntity())) {
         switch (renderTileEntityEvent.getState()) {
            case PRE:
               Color color = this.getColor(false);
               Color color2 = this.getColor(true);
               switch ((ChamsMode)this.mode.getValue()) {
                  case COLOR:
                     renderTileEntityEvent.cancel();
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

                     RenderUtil.setColor(color.getRGB());
                     renderTileEntityEvent.getDispatcher()
                        .renderTileEntityAt(
                           renderTileEntityEvent.getTileEntity(),
                           renderTileEntityEvent.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           renderTileEntityEvent.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           renderTileEntityEvent.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           renderTileEntityEvent.getPartialTicks(),
                           renderTileEntityEvent.getDestroyStage()
                        );
                     long m = 2244411581919830896L ^ l;
                     GL11.glEnable(2929);
                     if (this.solid.getValue()) {
                        GL11.glEnable(3553);
                     }

                     RenderUtil.setColor(color2.getRGB());
                     if (this.solid.getValue()) {
                        GL11.glDisable(3553);
                     }

                     renderTileEntityEvent.getDispatcher()
                        .renderTileEntityAt(
                           renderTileEntityEvent.getTileEntity(),
                           renderTileEntityEvent.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           renderTileEntityEvent.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           renderTileEntityEvent.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           renderTileEntityEvent.getPartialTicks(),
                           renderTileEntityEvent.getDestroyStage()
                        );
                     if (this.solid.getValue()) {
                        GL11.glEnable(3553);
                     }

                     GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                     break;
                  case NORMAL:
                     boolean bl = mc.gameSettings.viewBobbing;
                     mc.gameSettings.viewBobbing = false;
                     renderTileEntityEvent.cancel();
                     long n = 3359551688184540551L ^ l;
                     GL11.glEnable(32823);
                     GlStateManager.enablePolygonOffset();
                     n = 7496973731016948420L ^ l;
                     GlStateManager.doPolygonOffset(1.0F, -1.0E7F);
                     renderTileEntityEvent.getDispatcher()
                        .renderTileEntityAt(
                           renderTileEntityEvent.getTileEntity(),
                           renderTileEntityEvent.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           renderTileEntityEvent.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           renderTileEntityEvent.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           renderTileEntityEvent.getPartialTicks(),
                           renderTileEntityEvent.getDestroyStage()
                        );
                     GL11.glDisable(32823);
                     GlStateManager.doPolygonOffset(1.0F, 1.0E7F);
                     GlStateManager.disablePolygonOffset();
                     mc.gameSettings.viewBobbing = bl;
                     break;
                  case OUTLINE:
                     renderTileEntityEvent.getDispatcher()
                        .renderTileEntityAt(
                           renderTileEntityEvent.getTileEntity(),
                           renderTileEntityEvent.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           renderTileEntityEvent.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           renderTileEntityEvent.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           renderTileEntityEvent.getPartialTicks(),
                           renderTileEntityEvent.getDestroyStage()
                        );
                     GL11.glPushAttrib(1048575);
                     GL11.glDisable(3553);
                     GL11.glDisable(2929);
                     GL11.glDisable(2896);
                     GL11.glEnable(3042);
                     GL11.glBlendFunc(770, 771);
                     GL11.glLineWidth(1.0F);
                     long l2 = 3570051404827713652L ^ l;
                     GL11.glEnable(2960);
                     GL11.glStencilFunc(512, 1, 15);
                     GL11.glStencilOp(7681, 7681, 7681);
                     GL11.glPolygonMode(1028, 6913);
                     RenderUtil.setColor(color2.getRGB());
                     l2 = 911149413531704978L ^ l;
                     renderTileEntityEvent.getDispatcher()
                        .renderTileEntityAt(
                           renderTileEntityEvent.getTileEntity(),
                           renderTileEntityEvent.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           renderTileEntityEvent.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           renderTileEntityEvent.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           renderTileEntityEvent.getPartialTicks(),
                           renderTileEntityEvent.getDestroyStage()
                        );
                     GL11.glStencilFunc(512, 0, 15);
                     GL11.glStencilOp(7681, 7681, 7681);
                     GL11.glPolygonMode(1028, 6914);
                     renderTileEntityEvent.getDispatcher()
                        .renderTileEntityAt(
                           renderTileEntityEvent.getTileEntity(),
                           renderTileEntityEvent.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           renderTileEntityEvent.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           renderTileEntityEvent.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           renderTileEntityEvent.getPartialTicks(),
                           renderTileEntityEvent.getDestroyStage()
                        );
                     l2 = 4231120209782579225L ^ l;
                     GL11.glStencilFunc(514, 1, 15);
                     GL11.glStencilOp(7680, 7680, 7680);
                     GL11.glPolygonMode(1028, 6913);
                     GL11.glEnable(10754);
                     OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
                     l2 = 1197667827612656813L ^ l;
                     GlStateManager.doPolygonOffset(1.0F, -1.0E7F);
                     renderTileEntityEvent.getDispatcher()
                        .renderTileEntityAt(
                           renderTileEntityEvent.getTileEntity(),
                           renderTileEntityEvent.getTileEntity().getPos().getX() - TileEntityRendererDispatcher.staticPlayerX,
                           renderTileEntityEvent.getTileEntity().getPos().getY() - TileEntityRendererDispatcher.staticPlayerY,
                           renderTileEntityEvent.getTileEntity().getPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ,
                           renderTileEntityEvent.getPartialTicks(),
                           renderTileEntityEvent.getDestroyStage()
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
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.WORLD && this.mode.getValue() == ChamsMode.NORMAL) {
         GlStateManager.alphaFunc(516, 0.001F);
         GlStateManager.clear(256);
         RenderHelper.enableStandardItemLighting();

         for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity != null
               && mc.thePlayer.ticksExisted >= 10
               && (entity != mc.getRenderViewEntity() || mc.gameSettings.thirdPersonView != 0)
               && this.shouldRender(entity)) {
               if (!this.lightning.getValue()) {
                  mc.entityRenderer.disableLightmap();
               }

               mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, false);
               if (!this.lightning.getValue()) {
                  mc.entityRenderer.enableLightmap();
               }
            }
         }

         GlStateManager.alphaFunc(516, 0.1F);
      }
   };
   @EventHandler(value = RenderArmEvent.class, priority = EventPriority.LOWEST)
   private final Listener<RenderArmEvent> onRenderArm = renderArmEvent -> {
      if (this.hands.getValue()) {
         switch (renderArmEvent.getState()) {
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

   public Chams(Category category) {
      super(category);
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

   private boolean shouldRender(Entity entity) {
      if (entity == null) {
         return false;
      } else if (!(entity instanceof EntityLivingBase)) {
         return false;
      } else if (entity.isInvisible()) {
         return false;
      } else if (entity.getEntityId() == -1337 || entity.getEntityId() == -420) {
         return false;
      } else {
         return (!(entity instanceof EntityPlayer) || entity == mc.thePlayer)
               && (entity != mc.thePlayer || mc.gameSettings.thirdPersonView <= 0 || !this.entities.getSetting("Local").asBoolean())
            ? GameUtil.isHostile(entity) && this.entities.getSetting("Hostiles").asBoolean()
               || GameUtil.isNeutral(entity) && this.entities.getSetting("Neutrals").asBoolean()
               || GameUtil.isPassive(entity) && this.entities.getSetting("Passives").asBoolean()
               || GameUtil.isBoss(entity) && this.entities.getSetting("Bosses").asBoolean()
            : this.entities.getSetting("Players").asBoolean();
      }
   }

   private Color getColor(boolean bl) {
      float f = 1.0F - MathHelper.abs(MathHelper.sin((float)Minecraft.getSystemTime() % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.6F);
      if (bl) {
         switch ((ChamsColorMode)this.vColorMode.getValue()) {
            case SOLID:
               return this.vColor.getValue();
            case BREATH:
               return new Color(
                  MathHelper.clamp_int((int)(this.vColor.getValue().getRed() * f), 0, 255),
                  MathHelper.clamp_int((int)(this.vColor.getValue().getGreen() * f), 0, 255),
                  MathHelper.clamp_int((int)(this.vColor.getValue().getBlue() * f), 0, 255),
                  this.vColor.getValue().getAlpha()
               );
         }
      } else {
         switch ((ChamsColorMode)this.hColorMode.getValue()) {
            case SOLID:
               return this.hColor.getValue();
            case BREATH:
               return new Color(
                  MathHelper.clamp_int((int)(this.hColor.getValue().getRed() * f), 0, 255),
                  MathHelper.clamp_int((int)(this.hColor.getValue().getGreen() * f), 0, 255),
                  MathHelper.clamp_int((int)(this.hColor.getValue().getBlue() * f), 0, 255),
                  this.hColor.getValue().getAlpha()
               );
         }
      }

      return Color.RED;
   }

   private boolean shouldRenderTileEntity(TileEntity tileEntity) {
      return tileEntity == null
         ? false
         : tileEntity instanceof TileEntityEnderChest && this.blocks.getSetting("Ender Chest").asBoolean()
            || tileEntity instanceof TileEntityChest && this.blocks.getSetting("Chest").asBoolean()
            || tileEntity instanceof TileEntityHopper && this.blocks.getSetting("Hopper").asBoolean();
   }
}
