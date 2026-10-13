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
   private static final long c = 96681278444354L;

   public RadarHud(float var1, float var2, int var3, int var4) {
      super("radar", var1, var2, var3, var4);
      this.F(true);
   }

   @Override
   public void l(int var1) {
   }

   @Override
   public void render(int var1, int var2) {
      float var6 = this.M();
      float var7 = this.E_float();
      float var8 = this.x();
      float var9 = this.Y();
      RenderUtil.drawRect(var6 - 0.5F, var7 - 0.5F, var6 + var8 + 0.5F, var7 + var9 + 0.5F, new Color(0, 0, 0, 150).getRGB());
      RenderUtil.drawThinBorderedRect(var6, var7, var6 + var8, var7 + var9, new Color(17, 17, 17, 50).getRGB(), new Color(17, 17, 17, 197).getRGB());
      RenderUtil.drawRect(var6, var7, var6 + var8, var7 + 0.5F, client.getColorSetting().getValue().getRGB());
      RenderUtil.drawRect(var6, var7 + 0.5F, var6 + var8, var7 + 1.0F, client.getColorSetting().getValue().darker().getRGB());
      RenderUtil.drawLine(1.0F, var6 + var8 / 2.0F, var7 + var9, var6 + var8 / 2.0F, var7 + 1.0F, -1);
      RenderUtil.drawLine(1.0F, var6, var7 + var9 / 2.0F, var6 + var8, var7 + var9 / 2.0F, -1);

      for (Entity var11 : mc.theWorld.loadedEntityList) {
         if (this.T(var11)) {
            float var12 = (float)Math.toRadians(mc.thePlayer.rotationYaw);
            float var13 = (float)(mc.thePlayer.posX - var11.posX);
            float var14 = (float)(mc.thePlayer.posZ - var11.posZ);
            float var15 = (float)(var14 * Math.cos(var12) - var13 * Math.sin(var12)) / this.V;
            float var16 = (float)(var13 * Math.cos(var12) + var14 * Math.sin(var12)) / this.V;
            float var17 = var6 + var8 / 2.0F + var16;
            float var18 = var7 + var9 / 2.0F + var15;
            if (var17 < var6) {
               var17 = var6;
            }

            if (var17 > var6 + var8 - 3.0F) {
               var17 = var6 + var8 - 3.0F;
            }

            if (var18 < var7 + 0.5F) {
               var18 = var7 + 1.5F;
            }

            if (var18 >= var7 + var9 - 3.0F) {
               var18 = var7 + var9 - 3.0F;
            }

            RenderUtil.drawRect(var17, var18, var17 + 2.0F, var18 + 2.0F, this.t(var11));
         }
      }
   }

   private boolean T(Entity var1) {
      if ((!(var1 instanceof EntityPlayer) || BotDetector.w.contains(var1) || var1 == mc.thePlayer)
         && (var1 != mc.thePlayer || mc.gameSettings.thirdPersonView <= 0 || !this.q.getSetting("Local").asBoolean())) {
         return var1 instanceof EntityMob && this.q.getSetting("Monsters").asBoolean()
            || var1 instanceof EntityAnimal && this.q.getSetting("Animals").asBoolean()
            || var1 instanceof EntityTameable && this.q.getSetting("Passives").asBoolean()
            || var1 instanceof EntityItem && this.q.getSetting("Items").asBoolean()
            || var1 instanceof EntityAgeable && this.q.getSetting("Passives").asBoolean();
      } else if (GameUtil.S(var1) && !this.q.getSetting("Invisibles").asBoolean()) {
         return false;
      } else {
         return GameUtil.isOnHypixel() && GameUtil.scoreboardTitleContains("PIT") && AutoHypixel.pitSpawnY != 0.0 && var1.posY > AutoHypixel.pitSpawnY
            ? false
            : this.q.getSetting("Players").asBoolean();
      }
   }

   private int t(Entity var1) {
      long var2 = c ^ 104664927551934L;
      long var10001 = var2 ^ 133659505719201L;
      int var4 = (int)((var2 ^ 133659505719201L) >>> 56);
      int var5 = (int)((var2 ^ 133659505719201L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      if (client.getRelationManager().D(var1)) {
         return this.f;
      } else if (client.getRelationManager().z(var1)) {
         return this.e;
      } else if (var1 instanceof EntityItem) {
         return Color.WHITE.getRGB();
      } else if (AutoHypixel.bountiedPlayers.contains(var1)) {
         return Color.YELLOW.getRGB();
      } else {
         return var1 == KillAura.target ? client.getColorSetting().getValue().getRGB() : this.X;
      }
   }

   public void E(MapSetting var1) {
      this.q = var1;
   }

   public void Z(int var1) {
      this.V = var1;
   }
}
