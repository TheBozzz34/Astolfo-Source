package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.render.RenderModelEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.render.ClientColor;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

public final class Outline extends Module {
   private final MapSetting entities = new MapSettingBuilder("Entities")
      .ordered()
      .description("esp blocky")
      .add("Players", new BooleanSetting("Players", "Renders players", true))
      .add("Hostiles", new BooleanSetting("Hostiles", "Renders monsters", false))
      .add("Passives", new BooleanSetting("Passives", "Renders passives", false))
      .add("Neutrals", new BooleanSetting("Neutrals", "Renders neutrals", false))
      .add("Bosses", new BooleanSetting("Bosses", "Renders bosses", false))
      .add("Vehicles", new BooleanSetting("Vehicles", "Renders vehicles", false))
      .add("Others", new BooleanSetting("Others", "Renders others", false))
      .add("Invisibles", new BooleanSetting("Invisibles", "Renders invisibles", false))
      .build();
   private final NumberSetting<Float> radius = new NumberSetting<>("Radius", "Radius of outline", 1.0F, 0.1F, 3.0F);
   private final ColorSetting color = new ColorSetting("Color", "Color color", ClientColor.PURPLE.getColor());
   @EventHandler(value = RenderModelEvent.class, priority = EventPriority.LOW)
   private Listener<RenderModelEvent> onRenderModel = renderModelEvent -> {
      if (renderModelEvent.getState() == EventState.PRE && this.shouldOutline(renderModelEvent.getEntity())) {
         GL11.glPushMatrix();
         GlStateManager.depthMask(true);
         int i = this.color.getValue().getRGB();
         if (renderModelEvent.getEntity() instanceof EntityPlayer) {
            if (client.getRelationManager().isFriend(renderModelEvent.getEntity())) {
               i = OutlineColor.BLUE.getColor().getRGB();
            } else if (client.getRelationManager().isEnemy(renderModelEvent.getEntity())) {
               i = OutlineColor.RED.getColor().getRGB();
            }
         }

         RenderUtil.setColor(i);
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
         P_void();
         GL11.glPushAttrib(1048575);
         GL11.glDisable(3008);
         GL11.glDisable(3553);
         GL11.glDisable(2896);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glLineWidth(this.radius.getValue());
         GL11.glEnable(2848);
         GL11.glEnable(2960);
         GL11.glClear(1024);
         GL11.glClearStencil(15);
         GL11.glStencilFunc(512, 1, 15);
         GL11.glStencilOp(7681, 7681, 7681);
         GL11.glPolygonMode(1032, 6913);
         RenderUtil.setColor(i);
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
         GL11.glPolygonMode(1032, 6914);
         RenderUtil.setColor(i);
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
         RenderUtil.setColor(i);
         GL11.glStencilFunc(514, 1, 15);
         GL11.glStencilOp(7680, 7680, 7680);
         GL11.glPolygonMode(1032, 6913);
         RenderUtil.setColor(Color.WHITE.getRGB());
         GL11.glDepthMask(false);
         GL11.glDisable(2929);
         GL11.glEnable(10754);
         GL11.glPolygonOffset(1.0F, -2000000.0F);
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
         RenderUtil.setColor(i);
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
         GL11.glPolygonOffset(1.0F, 2000000.0F);
         GL11.glDisable(10754);
         GL11.glEnable(2929);
         GL11.glDepthMask(true);
         GL11.glDisable(2960);
         GL11.glDisable(2848);
         GL11.glHint(3154, 4352);
         GL11.glEnable(3042);
         GL11.glEnable(2896);
         GL11.glEnable(3553);
         GL11.glEnable(3008);
         GL11.glPopAttrib();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glPopMatrix();
      }
   };

   public Outline(Category category) {
      super(category);
      this.setHidden(false);
      this.addSettings(this.entities, this.color, this.radius);
   }

   public static void P_void() {
      Framebuffer framebuffer = mc.getFramebuffer();
      if (framebuffer != null && framebuffer.depthBuffer > -1) {
         setupStencilBuffer(framebuffer);
         framebuffer.depthBuffer = -1;
      }
   }

   public static void setupStencilBuffer(Framebuffer framebuffer) {
      EXTFramebufferObject.glDeleteRenderbuffersEXT(framebuffer.depthBuffer);
      int i = EXTFramebufferObject.glGenRenderbuffersEXT();
      EXTFramebufferObject.glBindRenderbufferEXT(36161, i);
      EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, mc.displayWidth, mc.displayHeight);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, i);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, i);
   }

   private boolean shouldOutline(Entity entity) {
      if (entity == null) {
         return false;
      } else if (!(entity instanceof EntityLivingBase)) {
         return false;
      } else if (entity.isInvisible()) {
         return false;
      } else if (entity.getEntityId() == -1337 || entity.getEntityId() == -420) {
         return false;
      } else {
         return entity instanceof EntityPlayer && entity != mc.thePlayer
            ? this.entities.getSetting("Players").asBoolean()
            : GameUtil.isHostile(entity) && this.entities.getSetting("Hostiles").asBoolean()
               || GameUtil.isNeutral(entity) && this.entities.getSetting("Neutrals").asBoolean()
               || GameUtil.isPassive(entity) && this.entities.getSetting("Passives").asBoolean()
               || GameUtil.isBoss(entity) && this.entities.getSetting("Bosses").asBoolean()
               || GameUtil.isVehicle(entity) && this.entities.getSetting("Vehicles").asBoolean()
               || GameUtil.isBoss(entity) && this.entities.getSetting("Bosses").asBoolean()
               || GameUtil.isObject(entity) && this.entities.getSetting("Others").asBoolean();
      }
   }
}
