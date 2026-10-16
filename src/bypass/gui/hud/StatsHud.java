package bypass.gui.hud;

import bypass.gui.component.Component;
import bypass.hypixel.PlayerStatsEntry;
import bypass.module.hidden.PlayerListTracker;
import bypass.module.other.AutoHypixelAutoCheckerGameMode;
import bypass.render.RenderUtil;
import bypass.util.MathUtil;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;

public class StatsHud extends Component {
   private AutoHypixelAutoCheckerGameMode gameMode;
   private int maxPlayers = 10;

   public StatsHud(float f, float g, int i, int j) {
      super("Stats", f, g, i, j);
      this.setDraggable(true);
   }

   @Override
   public void render(int i, int j) {
      if (this.gameMode != null) {
         float f = this.getX();
         float g = this.getYPos();
         float h = this.getWidth();
         int k = (int)(h / 12.0F);
         int l = 1;
         byte b = 25;

         for (PlayerStatsEntry playerStatsEntry : PlayerListTracker.playerStats) {
            if (l > this.maxPlayers) {
               break;
            }

            if (PlayerListTracker.tabListNames.contains(playerStatsEntry.name)) {
               b += 12;
               l++;
            }
         }

         int m = b + 5;
         float f2 = mc.fontRendererObj.getStringWidth("PLAYERS");
         float f3 = mc.fontRendererObj.getStringWidth("30000");
         float f4 = this.getLongestNameWidth();
         float f5 = f4 + f2 + f3 * this.gameMode.columnAmount + 15.0F;
         RenderUtil.drawRect(f, g, f + f5, g + 15.0F, new Color(1, 1, 1, 180).getRGB());
         RenderUtil.drawRect(f, g + 15.0F, f + f5, g + m, new Color(1, 1, 1, 120).getRGB());
         switch (this.gameMode) {
            case BW:
               PlayerListTracker.playerStats.sort((playerStatsEntry2, playerStatsEntry3) -> Float.compare(playerStatsEntry3.bedwarsWinstreak, playerStatsEntry2.bedwarsWinstreak));
               break;
            case SW:
               PlayerListTracker.playerStats.sort((playerStatsEntry4, playerStatsEntry5) -> Float.compare(playerStatsEntry5.skywarsKdr, playerStatsEntry4.skywarsKdr));
               break;
            case UHC:
               PlayerListTracker.playerStats.sort((playerStatsEntry6, playerStatsEntry7) -> Float.compare(playerStatsEntry7.uhcTotalKdr, playerStatsEntry6.uhcTotalKdr));
               break;
            case MW:
               PlayerListTracker.playerStats.sort((playerStatsEntry8, playerStatsEntry9) -> Float.compare(playerStatsEntry9.megaWallsKdr, playerStatsEntry8.megaWallsKdr));
         }

         mc.fontRendererObj.drawStringWithShadow("PLAYERS", this.getX() + 11.0F, this.getYPos() + 4.0F, -1);
         byte b2 = 25;
         int n = 1;

         for (PlayerStatsEntry playerStatsEntry10 : PlayerListTracker.playerStats) {
            if (n > this.maxPlayers) {
               break;
            }

            if (PlayerListTracker.tabListNames.contains(playerStatsEntry10.name)) {
               if (playerStatsEntry10.playerInfo != null) {
                  mc.getTextureManager().bindTexture(playerStatsEntry10.playerInfo.c());
                  Gui.drawScaledCustomSizeModalRect((int)(this.getX() + k - 12.0F), (int)(this.getYPos() + b2), 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
               }

               switch (this.gameMode) {
                  case BW:
                     mc.fontRendererObj.drawStringWithShadow("WS", this.getX() + f4 + f2, this.getYPos() + 4.0F, -1);
                     mc.fontRendererObj.drawStringWithShadow(String.valueOf(playerStatsEntry10.bedwarsWinstreak), this.getX() + f4 + f2, this.getYPos() + b2, -1);
                     RenderUtil.drawArrow(this.getX() + f4 + f2 + 14.0F, this.getYPos() + 10.0F, 6.0, false, Color.WHITE.getRGB());
                     mc.fontRendererObj.drawStringWithShadow("KD", this.getX() + f4 + f2 + f3, this.getYPos() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.bedwarsKdr), this.getX() + f4 + f3 + f2, this.getYPos() + b2, -1);
                     mc.fontRendererObj.drawStringWithShadow("FKD", this.getX() + f4 + f2 + f3 * 2.0F, this.getYPos() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(
                           MathUtil.ONE_DECIMAL.format(playerStatsEntry10.bedwarsFkdr), this.getX() + f4 + f3 * 2.0F + f2, this.getYPos() + b2, -1
                        );
                     mc.fontRendererObj.drawStringWithShadow("STARS", this.getX() + f4 + f2 + f3 * 3.0F, this.getYPos() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(String.valueOf(playerStatsEntry10.bedwarsLevel), this.getX() + f4 + f3 * 3.0F + f2, this.getYPos() + b2, -1);
                     break;
                  case SW:
                     mc.fontRendererObj.drawStringWithShadow("KD", this.getX() + f4 + f2, this.getYPos() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.skywarsKdr), this.getX() + f4 + f2, this.getYPos() + b2, -1);
                     RenderUtil.drawArrow(this.getX() + f4 + f2 + 14.0F, this.getYPos() + 10.0F, 6.0, false, Color.WHITE.getRGB());
                     mc.fontRendererObj.drawStringWithShadow("WL", this.getX() + f4 + f2 + f3, this.getYPos() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.skywarsWlr), this.getX() + f4 + f3 + f2, this.getYPos() + b2, -1);
                     break;
                  case UHC:
                     mc.fontRendererObj.drawStringWithShadow("SKD", this.getX() + f4 + f2, this.getYPos() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.uhcSoloKdr), this.getX() + f4 + f2, this.getYPos() + b2, -1);
                     mc.fontRendererObj.drawStringWithShadow("TKD", this.getX() + f4 + f2 + f3, this.getYPos() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.uhcKdr), this.getX() + f4 + f3 + f2, this.getYPos() + b2, -1);
                     mc.fontRendererObj.drawStringWithShadow("STARS", this.getX() + f4 + f2 + f3 * 2.0F, this.getYPos() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(String.valueOf(playerStatsEntry10.uhcStars), this.getX() + f4 + f3 * 2.0F + f2, this.getYPos() + b2, -1);
                     break;
                  case MW:
                     mc.fontRendererObj.drawStringWithShadow("KD", this.getX() + f4 + f2, this.getYPos() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.megaWallsKdr), this.getX() + f4 + f2, this.getYPos() + b2, -1);
                     RenderUtil.drawArrow(this.getX() + f4 + f2 + 14.0F, this.getYPos() + 10.0F, 6.0, false, Color.WHITE.getRGB());
                     mc.fontRendererObj.drawStringWithShadow("FKD", this.getX() + f4 + f2 + f3, this.getYPos() + 4.0F, -1);
                     mc.fontRendererObj
                        .drawStringWithShadow(MathUtil.ONE_DECIMAL.format(playerStatsEntry10.megaWallsFkdr), this.getX() + f4 + f3 + f2, this.getYPos() + b2, -1);
               }

               mc.fontRendererObj
                  .drawStringWithShadow(
                     playerStatsEntry10.realName != null ? EnumChatFormatting.GOLD + playerStatsEntry10.realName : playerStatsEntry10.name, this.getX() + k, this.getYPos() + b2, -1
                  );
               b2 += 12;
               n++;
            }
         }

         this.setHeight(m);
      }
   }

   @Override
   public void onClick(int i) {
   }

   private float getLongestNameWidth() {
      float f = 30.0F;

      for (PlayerStatsEntry playerStatsEntry : PlayerListTracker.playerStats) {
         String string = playerStatsEntry.name;
         float g = mc.fontRendererObj.getStringWidth(string);
         if (g > f) {
            f = g;
         }
      }

      return f;
   }

   public void setMaxPlayers(int i) {
      this.maxPlayers = i;
   }

   public void setGameMode(AutoHypixelAutoCheckerGameMode autoHypixelAutoCheckerGameMode) {
      this.gameMode = autoHypixelAutoCheckerGameMode;
   }
}
