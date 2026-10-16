package bypass.gui.hud;

import bypass.gui.component.Component;
import bypass.module.combat.KillAura;
import bypass.module.hidden.BotDetector;
import bypass.module.other.AutoHypixel;
import bypass.render.RenderUtil;
import bypass.setting.MapSetting;
import bypass.util.GameUtil;
import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;

public class RadarHud extends Component {
   private MapSetting entities;
   private int spacing;
   private final int friendColor = new Color(15, 176, 245, 255).getRGB();
   private final int defaultColor = new Color(200, 200, 200).getRGB();
   private final int enemyColor = new Color(200, 60, 71).getRGB();

   public RadarHud(float f, float g, int i, int j) {
      super("radar", f, g, i, j);
      this.setDraggable(true);
   }

   @Override
   public void onClick(int i) {
   }

   @Override
   public void render(int i, int j) {
      float f = this.getX();
      float g = this.getYPos();
      float h = this.getWidth();
      float f2 = this.getHeight();
      RenderUtil.drawRect(f - 0.5F, g - 0.5F, f + h + 0.5F, g + f2 + 0.5F, new Color(0, 0, 0, 150).getRGB());
      RenderUtil.drawThinBorderedRect(f, g, f + h, g + f2, new Color(17, 17, 17, 50).getRGB(), new Color(17, 17, 17, 197).getRGB());
      RenderUtil.drawRect(f, g, f + h, g + 0.5F, client.getColorSetting().getValue().getRGB());
      RenderUtil.drawRect(f, g + 0.5F, f + h, g + 1.0F, client.getColorSetting().getValue().darker().getRGB());
      RenderUtil.drawLine(1.0F, f + h / 2.0F, g + f2, f + h / 2.0F, g + 1.0F, -1);
      RenderUtil.drawLine(1.0F, f, g + f2 / 2.0F, f + h, g + f2 / 2.0F, -1);

      for (Entity entity : mc.theWorld.loadedEntityList) {
         if (this.shouldDisplay(entity)) {
            float f3 = (float)Math.toRadians(mc.thePlayer.rotationYaw);
            float f4 = (float)(mc.thePlayer.posX - entity.posX);
            float f5 = (float)(mc.thePlayer.posZ - entity.posZ);
            float f6 = (float)(f5 * Math.cos(f3) - f4 * Math.sin(f3)) / this.spacing;
            float f7 = (float)(f4 * Math.cos(f3) + f5 * Math.sin(f3)) / this.spacing;
            float f8 = f + h / 2.0F + f7;
            float f9 = g + f2 / 2.0F + f6;
            if (f8 < f) {
               f8 = f;
            }

            if (f8 > f + h - 3.0F) {
               f8 = f + h - 3.0F;
            }

            if (f9 < g + 0.5F) {
               f9 = g + 1.5F;
            }

            if (f9 >= g + f2 - 3.0F) {
               f9 = g + f2 - 3.0F;
            }

            RenderUtil.drawRect(f8, f9, f8 + 2.0F, f9 + 2.0F, this.getEntityColor(entity));
         }
      }
   }

   private boolean shouldDisplay(Entity entity) {
      if ((!(entity instanceof EntityPlayer) || BotDetector.bots.contains(entity) || entity == mc.thePlayer)
         && (entity != mc.thePlayer || mc.gameSettings.thirdPersonView <= 0 || !this.entities.getSetting("Local").asBoolean())) {
         return entity instanceof EntityMob && this.entities.getSetting("Monsters").asBoolean()
            || entity instanceof EntityAnimal && this.entities.getSetting("Animals").asBoolean()
            || entity instanceof EntityTameable && this.entities.getSetting("Passives").asBoolean()
            || entity instanceof EntityItem && this.entities.getSetting("Items").asBoolean()
            || entity instanceof EntityAgeable && this.entities.getSetting("Passives").asBoolean();
      } else if (GameUtil.isFullyInvisible(entity) && !this.entities.getSetting("Invisibles").asBoolean()) {
         return false;
      } else {
         return GameUtil.isOnHypixel() && GameUtil.scoreboardTitleContains("PIT") && AutoHypixel.pitSpawnY != 0.0 && entity.posY > AutoHypixel.pitSpawnY
            ? false
            : this.entities.getSetting("Players").asBoolean();
      }
   }

   private int getEntityColor(Entity entity) {
      if (client.getRelationManager().isEnemy(entity)) {
         return this.enemyColor;
      } else if (client.getRelationManager().isFriend(entity)) {
         return this.friendColor;
      } else if (entity instanceof EntityItem) {
         return Color.WHITE.getRGB();
      } else if (AutoHypixel.bountiedPlayers.contains(entity)) {
         return Color.YELLOW.getRGB();
      } else {
         return entity == KillAura.target ? client.getColorSetting().getValue().getRGB() : this.defaultColor;
      }
   }

   public void setEntities(MapSetting mapSetting) {
      this.entities = mapSetting;
   }

   public void setSpacing(int i) {
      this.spacing = i;
   }
}
