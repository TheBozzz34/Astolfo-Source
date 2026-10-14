package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.render.RenderEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.other.AutoHypixel;
import bypass.render.BaseColor;
import bypass.render.ClientColor;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public final class Tracers extends Module {
   private final MapSetting entities = new MapSettingBuilder("Entities")
      .ordered()
      .description("tracer")
      .add("Players", new BooleanSetting("Players", "Renders players", true))
      .add("Animals", new BooleanSetting("Animals", "Renders animals", true))
      .add("Monsters", new BooleanSetting("Monsters", "Renders monsters", false))
      .add("Passives", new BooleanSetting("Passives", "Renders passives", false))
      .add("Invisibles", new BooleanSetting("Invisibles", "Renders invisibles", false))
      .add("Local", new BooleanSetting("Local", "Renders local player", false))
      .add("Items", new BooleanSetting("Items", "Renders items", false))
      .build();
   private final BooleanSetting highlight = new BooleanSetting("Highlight", "Highlighted only", false);
   private final NumberSetting<Float> width = new NumberSetting<>("Width", "line width", 1.0F, 0.1F, 3.0F);
   @EventHandler(value = RenderEvent.class, priority = EventPriority.LOWEST)
   private Listener<RenderEvent> onRender = renderEvent -> {
      long l = c ^ 140645934746722L;
      long m = l ^ 26630693634239L;
      int i = (int)((l ^ 26630693634239L) >>> 56);
      int j = (int)((l ^ 26630693634239L) << 8 >>> 32);
      int k = (int)(m << 40 >>> 40);
      switch (renderEvent.getType()) {
         case WORLD:
            boolean bl = mc.gameSettings.viewBobbing;
            float f = mc.thePlayer.timeInPortal;
            long n = 4194270026570116024L ^ l;
            float g = mc.thePlayer.prevTimeInPortal;
            mc.gameSettings.viewBobbing = false;
            mc.thePlayer.timeInPortal = 0.0F;
            mc.thePlayer.prevTimeInPortal = 0.0F;
            mc.entityRenderer.setupCameraTransform(renderEvent.getPartialTicks(), 0);
            GL11.glPushMatrix();
            GL11.glDisable(2929);
            GL11.glEnable(3042);

            for (Entity entity : mc.theWorld.loadedEntityList) {
               if (this.W(entity)) {
                  double d = mc.thePlayer.getDistanceToEntity(entity);
                  GL11.glDisable(3553);
                  mc.entityRenderer.disableLightmap();
                  GL11.glEnable(2848);
                  GL11.glLineWidth(1.0F);
                  GL11.glEnable(2848);
                  float h = (float)this.Z(entity.posX, entity.lastTickPosX, renderEvent.getPartialTicks()) - (float)mc.getRenderManager().viewerPosX;
                  float f2 = (float)this.Z(entity.posY, entity.lastTickPosY, renderEvent.getPartialTicks()) - (float)mc.getRenderManager().viewerPosY;
                  float f3 = (float)this.Z(entity.posZ, entity.lastTickPosZ, renderEvent.getPartialTicks()) - (float)mc.getRenderManager().viewerPosZ;
                  if (client.getRelationManager().z(entity)) {
                     RenderUtil.setColor(BaseColor.BLUE.z().getRGB());
                  } else if (client.getRelationManager().D(entity)) {
                     RenderUtil.setColor(ClientColor.RED.getColor().getRGB());
                  } else if (AutoHypixel.bountiedPlayers.contains(entity)) {
                     RenderUtil.setColor(Color.YELLOW.getRGB());
                  } else if (d < 15.0) {
                     GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5F);
                  } else if (d < 20.0) {
                     GL11.glColor4f(1.0F, (float)((d - 15.0) / 5.0), 0.0F, 0.5F);
                  } else if (d < 45.0) {
                     GL11.glColor4f(1.0F, 1.0F, 0.0F, 0.5F);
                  } else if (d < 50.0) {
                     GL11.glColor4f((float)(1.0 - (d - 45.0) / 5.0), 1.0F, 0.0F, 0.5F);
                  } else if (d >= 50.0) {
                     GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.5F);
                  }

                  GL11.glLineWidth(this.width.getValue());
                  GL11.glBegin(1);
                  GL11.glVertex3d(0.0, mc.thePlayer.getEyeHeight(), 0.0);
                  GL11.glVertex3d(h, f2 + entity.getEyeHeight() * 0.5F, f3);
                  GL11.glEnd();
               }
            }

            GL11.glDisable(2848);
            GL11.glEnable(3553);
            GL11.glDisable(2848);
            GL11.glDisable(3042);
            GL11.glEnable(2929);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
            mc.gameSettings.viewBobbing = bl;
            mc.thePlayer.timeInPortal = f;
            mc.thePlayer.prevTimeInPortal = g;
      }
   };
   private static final long c = 24866622810496L;

   public Tracers(Category category) {
      super(category);
      this.setHidden(false);
      this.addSettings(this.entities, this.highlight, this.width);
   }

   private double Z(double d, double d2, double d3) {
      return d2 + (d - d2) * d3;
   }

   private boolean W(Entity entity) {
      long l = c ^ 82360404310483L;
      long m = l ^ 49730777675022L;
      int i = (int)((l ^ 49730777675022L) >>> 56);
      int j = (int)((l ^ 49730777675022L) << 8 >>> 32);
      int k = (int)(m << 40 >>> 40);
      if ((!(entity instanceof EntityPlayer) || entity == mc.thePlayer)
         && (entity != mc.thePlayer || mc.gameSettings.thirdPersonView <= 0 || !this.entities.getSetting("Local").asBoolean())) {
         return entity instanceof EntityMob && this.entities.getSetting("Monsters").asBoolean()
            || entity instanceof EntityAnimal && this.entities.getSetting("Animals").asBoolean()
            || entity instanceof EntityTameable && this.entities.getSetting("Passives").asBoolean()
            || entity instanceof EntityItem && this.entities.getSetting("Items").asBoolean()
            || entity instanceof EntityAgeable && this.entities.getSetting("Passives").asBoolean();
      } else if (GameUtil.S((EntityLivingBase)entity) && !this.entities.getSetting("Invisibles").asBoolean()) {
         return false;
      } else if (GameUtil.isOnHypixel() && GameUtil.scoreboardTitleContains("PIT")) {
         return AutoHypixel.pitSpawnY != 0.0 && entity.posY > AutoHypixel.pitSpawnY
            ? false
            : AutoHypixel.bountiedPlayers.contains(entity) || client.getRelationManager().D(entity) || client.getRelationManager().z(entity);
      } else {
         return !this.highlight.getValue()
            ? this.entities.getSetting("Players").asBoolean()
            : this.entities.getSetting("Players").asBoolean()
               && (client.getRelationManager().z(entity) || client.getRelationManager().D(entity) || AutoHypixel.bountiedPlayers.contains(entity));
      }
   }
}
