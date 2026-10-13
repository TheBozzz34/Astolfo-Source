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
   private Listener<RenderEvent> onRender = var1x -> {
      long var2 = c ^ 140645934746722L;
      long var10001 = var2 ^ 26630693634239L;
      int var4 = (int)((var2 ^ 26630693634239L) >>> 56);
      int var5 = (int)((var2 ^ 26630693634239L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      switch (var1x.getType()) {
         case WORLD:
            boolean var8 = mc.gameSettings.viewBobbing;
            float var9 = mc.thePlayer.timeInPortal;
            long var10000 = 4194270026570116024L ^ var2;
            float var10 = mc.thePlayer.prevTimeInPortal;
            mc.gameSettings.viewBobbing = false;
            mc.thePlayer.timeInPortal = 0.0F;
            mc.thePlayer.prevTimeInPortal = 0.0F;
            mc.entityRenderer.setupCameraTransform(var1x.getPartialTicks(), 0);
            GL11.glPushMatrix();
            GL11.glDisable(2929);
            GL11.glEnable(3042);

            for (Entity var12 : mc.theWorld.loadedEntityList) {
               if (this.W(var12)) {
                  double var13 = mc.thePlayer.getDistanceToEntity(var12);
                  GL11.glDisable(3553);
                  mc.entityRenderer.disableLightmap();
                  GL11.glEnable(2848);
                  GL11.glLineWidth(1.0F);
                  GL11.glEnable(2848);
                  float var15 = (float)this.Z(var12.posX, var12.lastTickPosX, var1x.getPartialTicks()) - (float)mc.getRenderManager().viewerPosX;
                  float var16 = (float)this.Z(var12.posY, var12.lastTickPosY, var1x.getPartialTicks()) - (float)mc.getRenderManager().viewerPosY;
                  float var17 = (float)this.Z(var12.posZ, var12.lastTickPosZ, var1x.getPartialTicks()) - (float)mc.getRenderManager().viewerPosZ;
                  if (client.getRelationManager().z(var12)) {
                     RenderUtil.setColor(BaseColor.BLUE.z().getRGB());
                  } else if (client.getRelationManager().D(var12)) {
                     RenderUtil.setColor(ClientColor.RED.getColor().getRGB());
                  } else if (AutoHypixel.bountiedPlayers.contains(var12)) {
                     RenderUtil.setColor(Color.YELLOW.getRGB());
                  } else if (var13 < 15.0) {
                     GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5F);
                  } else if (var13 < 20.0) {
                     GL11.glColor4f(1.0F, (float)((var13 - 15.0) / 5.0), 0.0F, 0.5F);
                  } else if (var13 < 45.0) {
                     GL11.glColor4f(1.0F, 1.0F, 0.0F, 0.5F);
                  } else if (var13 < 50.0) {
                     GL11.glColor4f((float)(1.0 - (var13 - 45.0) / 5.0), 1.0F, 0.0F, 0.5F);
                  } else if (var13 >= 50.0) {
                     GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.5F);
                  }

                  GL11.glLineWidth(this.width.getValue());
                  GL11.glBegin(1);
                  GL11.glVertex3d(0.0, mc.thePlayer.getEyeHeight(), 0.0);
                  GL11.glVertex3d(var15, var16 + var12.getEyeHeight() * 0.5F, var17);
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
            mc.gameSettings.viewBobbing = var8;
            mc.thePlayer.timeInPortal = var9;
            mc.thePlayer.prevTimeInPortal = var10;
      }
   };
   private static final long c = 24866622810496L;

   public Tracers(Category var1) {
      super(var1);
      this.setHidden(false);
      this.addSettings(this.entities, this.highlight, this.width);
   }

   private double Z(double var1, double var3, double var5) {
      return var3 + (var1 - var3) * var5;
   }

   private boolean W(Entity var1) {
      long var2 = c ^ 82360404310483L;
      long var10001 = var2 ^ 49730777675022L;
      int var4 = (int)((var2 ^ 49730777675022L) >>> 56);
      int var5 = (int)((var2 ^ 49730777675022L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      if ((!(var1 instanceof EntityPlayer) || var1 == mc.thePlayer)
         && (var1 != mc.thePlayer || mc.gameSettings.thirdPersonView <= 0 || !this.entities.getSetting("Local").asBoolean())) {
         return var1 instanceof EntityMob && this.entities.getSetting("Monsters").asBoolean()
            || var1 instanceof EntityAnimal && this.entities.getSetting("Animals").asBoolean()
            || var1 instanceof EntityTameable && this.entities.getSetting("Passives").asBoolean()
            || var1 instanceof EntityItem && this.entities.getSetting("Items").asBoolean()
            || var1 instanceof EntityAgeable && this.entities.getSetting("Passives").asBoolean();
      } else if (GameUtil.S((EntityLivingBase)var1) && !this.entities.getSetting("Invisibles").asBoolean()) {
         return false;
      } else if (GameUtil.isOnHypixel() && GameUtil.scoreboardTitleContains("PIT")) {
         return AutoHypixel.pitSpawnY != 0.0 && var1.posY > AutoHypixel.pitSpawnY
            ? false
            : AutoHypixel.bountiedPlayers.contains(var1) || client.getRelationManager().D(var1) || client.getRelationManager().z(var1);
      } else {
         return !this.highlight.getValue()
            ? this.entities.getSetting("Players").asBoolean()
            : this.entities.getSetting("Players").asBoolean()
               && (client.getRelationManager().z(var1) || client.getRelationManager().D(var1) || AutoHypixel.bountiedPlayers.contains(var1));
      }
   }
}
