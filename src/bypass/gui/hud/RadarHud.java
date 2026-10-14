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
   private MapSetting q;
   private int V;
   private final int e = new Color(15, 176, 245, 255).getRGB();
   private final int X = new Color(200, 200, 200).getRGB();
   private final int f = new Color(200, 60, 71).getRGB();

   public RadarHud(float f, float g, int i, int j) {
      super("radar", f, g, i, j);
      this.F(true);
   }

   @Override
   public void l(int i) {
   }

   @Override
   public void render(int i, int j) {
      float f = this.M();
      float g = this.E_float();
      float h = this.x();
      float f2 = this.Y();
      RenderUtil.drawRect(f - 0.5F, g - 0.5F, f + h + 0.5F, g + f2 + 0.5F, new Color(0, 0, 0, 150).getRGB());
      RenderUtil.drawThinBorderedRect(f, g, f + h, g + f2, new Color(17, 17, 17, 50).getRGB(), new Color(17, 17, 17, 197).getRGB());
      RenderUtil.drawRect(f, g, f + h, g + 0.5F, client.getColorSetting().getValue().getRGB());
      RenderUtil.drawRect(f, g + 0.5F, f + h, g + 1.0F, client.getColorSetting().getValue().darker().getRGB());
      RenderUtil.drawLine(1.0F, f + h / 2.0F, g + f2, f + h / 2.0F, g + 1.0F, -1);
      RenderUtil.drawLine(1.0F, f, g + f2 / 2.0F, f + h, g + f2 / 2.0F, -1);

      for (Entity entity : mc.theWorld.loadedEntityList) {
         if (this.T(entity)) {
            float f3 = (float)Math.toRadians(mc.thePlayer.rotationYaw);
            float f4 = (float)(mc.thePlayer.posX - entity.posX);
            float f5 = (float)(mc.thePlayer.posZ - entity.posZ);
            float f6 = (float)(f5 * Math.cos(f3) - f4 * Math.sin(f3)) / this.V;
            float f7 = (float)(f4 * Math.cos(f3) + f5 * Math.sin(f3)) / this.V;
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

            RenderUtil.drawRect(f8, f9, f8 + 2.0F, f9 + 2.0F, this.t(entity));
         }
      }
   }

   private boolean T(Entity entity) {
      if ((!(entity instanceof EntityPlayer) || BotDetector.w.contains(entity) || entity == mc.thePlayer)
         && (entity != mc.thePlayer || mc.gameSettings.thirdPersonView <= 0 || !this.q.getSetting("Local").asBoolean())) {
         return entity instanceof EntityMob && this.q.getSetting("Monsters").asBoolean()
            || entity instanceof EntityAnimal && this.q.getSetting("Animals").asBoolean()
            || entity instanceof EntityTameable && this.q.getSetting("Passives").asBoolean()
            || entity instanceof EntityItem && this.q.getSetting("Items").asBoolean()
            || entity instanceof EntityAgeable && this.q.getSetting("Passives").asBoolean();
      } else if (GameUtil.S(entity) && !this.q.getSetting("Invisibles").asBoolean()) {
         return false;
      } else {
         return GameUtil.isOnHypixel() && GameUtil.scoreboardTitleContains("PIT") && AutoHypixel.pitSpawnY != 0.0 && entity.posY > AutoHypixel.pitSpawnY
            ? false
            : this.q.getSetting("Players").asBoolean();
      }
   }

   private int t(Entity entity) {
      if (client.getRelationManager().D(entity)) {
         return this.f;
      } else if (client.getRelationManager().z(entity)) {
         return this.e;
      } else if (entity instanceof EntityItem) {
         return Color.WHITE.getRGB();
      } else if (AutoHypixel.bountiedPlayers.contains(entity)) {
         return Color.YELLOW.getRGB();
      } else {
         return entity == KillAura.target ? client.getColorSetting().getValue().getRGB() : this.X;
      }
   }

   public void E(MapSetting mapSetting) {
      this.q = mapSetting;
   }

   public void Z(int i) {
      this.V = i;
   }
}
