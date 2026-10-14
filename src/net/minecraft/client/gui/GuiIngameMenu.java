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
      byte b = -16;
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + b, I18n.format("menu.returnToMenu")));
      if (!this.mc.isIntegratedServerRunning()) {
         this.buttonList.get(0).displayString = I18n.format("menu.disconnect");
      }

      this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + b, I18n.format("menu.returnToGame")));
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + b, 98, 20, I18n.format("menu.options")));
      GuiButton guiButton;
      this.buttonList.add(guiButton = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + b, 98, 20, I18n.format("menu.shareToLan")));
      this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + b, 98, 20, I18n.format("gui.achievements")));
      this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + b, 98, 20, I18n.format("gui.stats")));
      guiButton.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      switch (button.id) {
         case 0:
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            break;
         case 1:
            boolean bl = this.mc.isIntegratedServerRunning();
            boolean bl2 = this.mc.isConnectedToRealms();
            button.enabled = false;
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld((WorldClient)null);
            if (bl) {
               this.mc.displayGuiScreen(new GuiMainMenu());
            } else if (bl2) {
               RealmsBridge realmsBridge = new RealmsBridge();
               realmsBridge.switchToRealms(new GuiMainMenu());
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
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      this.drawDefaultBackground();
      float f = scaledResolution.getScaledWidth() - 10.0F;
      float g = 5.0F;
      float h = 8.0F;

      for (int i = 0; i < 2; i++) {
         if (i == 0) {
            String string = "Sense";
            ResourceLocation resourceLocation = Astolfo.INSTANCE.getSenseSetting().getValue() ? GuiMainMenu.V : GuiMainMenu.K;
            float f2 = GuiMainMenu.R.getStringWidth(string);
            float f3 = f2 + g * 2.0F + h * 2.0F;
            if (mouseX > f - f3 && mouseX < f && mouseY > 7.5F && mouseY < 35.0F) {
               boolean bl = true;
            } else {
               boolean bl2 = false;
            }

            f -= f3 + 10.0F;
         }
      }

      this.d(this.fontRendererObj, I18n.format("menu.game"), this.width / 2, 40, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (mouseButton == 0 && i.hasElapsed(2500.0F)) {
         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         float f = 5.0F;
         float g = 9.0F;
         float h = scaledResolution.getScaledWidth() - 10.0F;
         CustomFontRenderer customFontRenderer = GuiMainMenu.R;

         for (int j = 0; j < 2; j++) {
            if (j == 0) {
               String string = "Sense";
               float f2 = customFontRenderer.getStringWidth(string);
               float f3 = f2 + f * 2.0F + g * 2.0F;
               boolean bl = mouseX > h - f3 && mouseX < h && mouseY > 7.5F && mouseY < 35.0F;
               if (bl) {
                  Astolfo.INSTANCE.getSenseSetting().set(!Astolfo.INSTANCE.getSenseSetting().getValue());
                  i.reset();
               }
            }
         }
      }
   }

   public void H(CustomFontRenderer customFontRenderer, String string, float f, float g, int j, boolean bl) {
      int i = customFontRenderer.getStringWidth(string);
      int k = customFontRenderer.getStringHeight(string);
      float h = f - i / 2.0F;
      float f2 = g - k / 2.0F;
      if (bl) {
         customFontRenderer.drawStringWithShadow(string, (int)h, (int)f2, j);
      } else {
         customFontRenderer.drawString(string, (int)h, (int)f2, j);
      }
   }

   static {
      long l = a ^ 43405373162718L;
      long m = l ^ 1705155665226939249L;
   }
}
