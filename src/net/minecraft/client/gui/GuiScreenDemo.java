package net.minecraft.client.gui;

import java.io.IOException;
import java.net.URI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiScreenDemo extends GuiScreen {
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation field_146348_f = new ResourceLocation("textures/gui/demo_background.png");
   private static final long a = 11316962758479L;

   @Override
   public void initGui() {
      this.buttonList.clear();
      byte var3 = -16;
      this.buttonList.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 + 62 + var3, 114, 20, I18n.format("demo.help.buy")));
      this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height / 2 + 62 + var3, 114, 20, I18n.format("demo.help.later")));
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      switch (var1.id) {
         case 1:
            var1.enabled = false;

            try {
               Class var5 = Class.forName("java.awt.Desktop");
               Object var6 = var5.getMethod("getDesktop").invoke(null);
               var5.getMethod("browse", URI.class).invoke(var6, new URI("http://www.minecraft.net/store?source=demo"));
            } catch (Throwable var7) {
               logger.error("Couldn't open link", var7);
            }
            break;
         case 2:
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
   }

   @Override
   public void drawDefaultBackground() {
      super.drawDefaultBackground();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(field_146348_f);
      int var3 = (this.width - 248) / 2;
      int var4 = (this.height - 166) / 2;
      this.drawTexturedModalRect(var3, var4, 0, 0, 248, 166);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      int var6 = (this.width - 248) / 2 + 10;
      int var7 = (this.height - 166) / 2 + 8;
      this.fontRendererObj.D(I18n.format("demo.help.title"), var6, var7, 2039583);
      var7 += 12;
      GameSettings var8 = this.mc.gameSettings;
      this.fontRendererObj
         .D(
            I18n.format(
               "demo.help.movementShort",
               GameSettings.getKeyDisplayString(var8.keyBindForward.getKeyCode()),
               GameSettings.getKeyDisplayString(var8.keyBindLeft.getKeyCode()),
               GameSettings.getKeyDisplayString(var8.keyBindBack.getKeyCode()),
               GameSettings.getKeyDisplayString(var8.keyBindRight.getKeyCode())
            ),
            var6,
            var7,
            5197647
         );
      this.fontRendererObj.D(I18n.format("demo.help.movementMouse"), var6, var7 + 12, 5197647);
      this.fontRendererObj.D(I18n.format("demo.help.jump", GameSettings.getKeyDisplayString(var8.keyBindJump.getKeyCode())), var6, var7 + 24, 5197647);
      this.fontRendererObj
         .D(I18n.format("demo.help.inventory", GameSettings.getKeyDisplayString(var8.keyBindInventory.getKeyCode())), var6, var7 + 36, 5197647);
      this.fontRendererObj.drawSplitString(I18n.format("demo.help.fullWrapped"), var6, var7 + 68, 218, 2039583);
      super.drawScreen(var1, var2, var3);
   }
}
