package net.minecraft.client.gui;

import bypass.Astolfo;
import bypass.render.font.CustomFontRenderer;
import bypass.util.Stopwatch;
import java.io.IOException;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.ResourceLocation;

public class GuiIngameMenu extends GuiScreen {
   private int field_146445_a;
   private int field_146444_f;
   private static final Stopwatch i = new Stopwatch();
   private static final int w = 2500;
   private static final long a = 112426152169406L;

   @Override
   public void initGui() {
      this.field_146445_a = 0;
      this.buttonList.clear();
      byte var4 = -16;
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + var4, I18n.format("menu.returnToMenu")));
      if (!this.mc.isIntegratedServerRunning()) {
         this.buttonList.get(0).displayString = I18n.format("menu.disconnect");
      }

      this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + var4, I18n.format("menu.returnToGame")));
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + var4, 98, 20, I18n.format("menu.options")));
      GuiButton var6;
      this.buttonList.add(var6 = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + var4, 98, 20, I18n.format("menu.shareToLan")));
      this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + var4, 98, 20, I18n.format("gui.achievements")));
      this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + var4, 98, 20, I18n.format("gui.stats")));
      var6.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      switch (var1.id) {
         case 0:
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            break;
         case 1:
            boolean var5 = this.mc.isIntegratedServerRunning();
            boolean var6 = this.mc.isConnectedToRealms();
            var1.enabled = false;
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld((WorldClient)null);
            if (var5) {
               this.mc.displayGuiScreen(new GuiMainMenu());
            } else if (var6) {
               RealmsBridge var7 = new RealmsBridge();
               var7.switchToRealms(new GuiMainMenu());
            } else {
               this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
            }
         case 2:
         case 3:
         default:
            break;
         case 4:
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
            break;
         case 5:
            this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
            break;
         case 6:
            this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
            break;
         case 7:
            this.mc.displayGuiScreen(new GuiShareToLan(this));
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.field_146444_f++;
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      ScaledResolution var7 = new ScaledResolution(this.mc);
      this.drawDefaultBackground();
      float var8 = var7.getScaledWidth() - 10.0F;
      float var9 = 5.0F;
      float var10 = 8.0F;

      for (int var11 = 0; var11 < 2; var11++) {
         if (var11 == 0) {
            String var12 = "Sense";
            ResourceLocation var13 = Astolfo.INSTANCE.getSenseSetting().getValue() ? GuiMainMenu.V : GuiMainMenu.K;
            float var14 = GuiMainMenu.R.getStringWidth(var12);
            float var15 = var14 + var9 * 2.0F + var10 * 2.0F;
            if (var1 > var8 - var15 && var1 < var8 && var2 > 7.5F && var2 < 35.0F) {
               boolean var17 = true;
            } else {
               boolean var10000 = false;
            }

            var8 -= var15 + 10.0F;
         }
      }

      this.d(this.fontRendererObj, I18n.format("menu.game"), this.width / 2, 40, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      super.mouseClicked(var1, var2, var3);
      if (var3 == 0 && i.hasElapsed(2500.0F)) {
         ScaledResolution var7 = new ScaledResolution(this.mc);
         float var8 = 5.0F;
         float var9 = 9.0F;
         float var10 = var7.getScaledWidth() - 10.0F;
         CustomFontRenderer var11 = GuiMainMenu.R;

         for (int var12 = 0; var12 < 2; var12++) {
            if (var12 == 0) {
               String var13 = "Sense";
               float var14 = var11.getStringWidth(var13);
               float var15 = var14 + var8 * 2.0F + var9 * 2.0F;
               boolean var16 = var1 > var10 - var15 && var1 < var10 && var2 > 7.5F && var2 < 35.0F;
               if (var16) {
                  Astolfo.INSTANCE.getSenseSetting().set(!Astolfo.INSTANCE.getSenseSetting().getValue());
                  i.reset();
               }
            }
         }
      }
   }

   public void H(CustomFontRenderer var1, String var2, float var3, float var4, int var5, boolean var6) {
      int var10 = var1.getStringWidth(var2);
      int var11 = var1.getStringHeight(var2);
      float var12 = var3 - var10 / 2.0F;
      float var13 = var4 - var11 / 2.0F;
      if (var6) {
         var1.drawStringWithShadow(var2, (int)var12, (int)var13, var5);
      } else {
         var1.drawString(var2, (int)var12, (int)var13, var5);
      }
   }

   static {
      long var11 = a ^ 43405373162718L;
      long var10000 = var11 ^ 1705155665226939249L;
   }
}
