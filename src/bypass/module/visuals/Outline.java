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
   private Listener<RenderModelEvent> onRenderModel = var1x -> {
      long var2 = c ^ 114449926509162L;
      long var10001 = var2 ^ 35537221722439L;
      int var4 = (int)((var2 ^ 35537221722439L) >>> 56);
      int var5 = (int)((var2 ^ 35537221722439L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      if (var1x.getState() == EventState.PRE && this.r(var1x.getEntity())) {
         GL11.glPushMatrix();
         GlStateManager.depthMask(true);
         int var8 = this.color.getValue().getRGB();
         if (var1x.getEntity() instanceof EntityPlayer) {
            if (client.getRelationManager().z(var1x.getEntity())) {
               var8 = OutlineColor.BLUE.C().getRGB();
            } else if (client.getRelationManager().D(var1x.getEntity())) {
               var8 = OutlineColor.RED.C().getRGB();
            }
         }

         RenderUtil.setColor(var8);
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
         long var10000 = 4919695800598552673L ^ var2;
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
         RenderUtil.setColor(var8);
         var10000 = 7527467513157604236L ^ var2;
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
         GL11.glPolygonMode(1032, 6914);
         RenderUtil.setColor(var8);
         var10000 = 1414541032376484744L ^ var2;
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
         RenderUtil.setColor(var8);
         GL11.glStencilFunc(514, 1, 15);
         GL11.glStencilOp(7680, 7680, 7680);
         GL11.glPolygonMode(1032, 6913);
         RenderUtil.setColor(Color.WHITE.getRGB());
         var10000 = 4195649592761273385L ^ var2;
         GL11.glDepthMask(false);
         GL11.glDisable(2929);
         GL11.glEnable(10754);
         GL11.glPolygonOffset(1.0F, -2000000.0F);
         var10000 = 1892666837428165539L ^ var2;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
         RenderUtil.setColor(var8);
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
   private static final long c = 62716144703088L;

   public Outline(Category var1) {
      super(var1);
      this.setHidden(false);
      this.addSettings(this.entities, this.color, this.radius);
   }

   public static void P_void() {
      Framebuffer var3 = mc.getFramebuffer();
      if (var3 != null && var3.depthBuffer > -1) {
         f(var3);
         var3.depthBuffer = -1;
      }
   }

   public static void f(Framebuffer var0) {
      EXTFramebufferObject.glDeleteRenderbuffersEXT(var0.depthBuffer);
      int var3 = EXTFramebufferObject.glGenRenderbuffersEXT();
      EXTFramebufferObject.glBindRenderbufferEXT(36161, var3);
      EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, mc.displayWidth, mc.displayHeight);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, var3);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, var3);
   }

   private boolean r(Entity var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof EntityLivingBase)) {
         return false;
      } else if (var1.isInvisible()) {
         return false;
      } else if (var1.getEntityId() == -1337 || var1.getEntityId() == -420) {
         return false;
      } else {
         return var1 instanceof EntityPlayer && var1 != mc.thePlayer
            ? this.entities.getSetting("Players").asBoolean()
            : GameUtil.isHostile(var1) && this.entities.getSetting("Hostiles").asBoolean()
               || GameUtil.isNeutral(var1) && this.entities.getSetting("Neutrals").asBoolean()
               || GameUtil.isPassive(var1) && this.entities.getSetting("Passives").asBoolean()
               || GameUtil.isBoss(var1) && this.entities.getSetting("Bosses").asBoolean()
               || GameUtil.isVehicle(var1) && this.entities.getSetting("Vehicles").asBoolean()
               || GameUtil.isBoss(var1) && this.entities.getSetting("Bosses").asBoolean()
               || GameUtil.isObject(var1) && this.entities.getSetting("Others").asBoolean();
      }
   }
}
