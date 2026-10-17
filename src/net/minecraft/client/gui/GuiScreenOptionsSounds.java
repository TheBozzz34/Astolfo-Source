package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiScreenOptionsSounds extends GuiScreen {
   private final GuiScreen field_146505_f;
   private final GameSettings game_settings_4;
   public String field_146507_a = "Options";
   private String field_146508_h;

   public GuiScreenOptionsSounds(GuiScreen guiScreen, GameSettings gameSettings) {
      this.field_146505_f = guiScreen;
      this.game_settings_4 = gameSettings;
   }

   @Override
   public void initGui() {
      int i = 0;
      this.field_146507_a = I18n.format("options.sounds.title");
      this.field_146508_h = I18n.format("options.off");
      this.buttonList
         .add(
            new GuiScreenOptionsSounds.Button(
               SoundCategory.MASTER.getCategoryId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), SoundCategory.MASTER, true
            )
         );
      i += 2;

      for (SoundCategory soundCategory : SoundCategory.values()) {
         if (soundCategory != SoundCategory.MASTER) {
            this.buttonList
               .add(
                  new GuiScreenOptionsSounds.Button(
                     soundCategory.getCategoryId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), soundCategory, false
                  )
               );
            i++;
         }
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled && button.id == 200) {
         this.mc.gameSettings.saveOptions();
         this.mc.displayGuiScreen(this.field_146505_f);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.field_146507_a, this.width / 2, 15, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public String getSoundVolume(SoundCategory soundCategory) {
      float f = this.game_settings_4.getSoundLevel(soundCategory);
      return f == 0.0F ? this.field_146508_h : (int)(f * 100.0F) + "%";
   }

   public class Button extends GuiButton {
      private final SoundCategory field_146153_r;
      private final String field_146152_s;
      public float field_146156_o = 1.0F;
      public boolean field_146155_p;

      public Button(int i, int j, int k, SoundCategory soundCategory, boolean bl) {
         super(i, j, k, bl ? 310 : 150, 20, "");
         this.field_146153_r = soundCategory;
         this.field_146152_s = I18n.format("soundCategory." + soundCategory.getCategoryName());
         this.displayString = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.getSoundVolume(soundCategory);
         this.field_146156_o = GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(soundCategory);
      }

      @Override
      public int getHoverState(boolean mouseOver) {
         return 0;
      }

      @Override
      public void mouseDragged(Minecraft mcIn, int mouseX, int mouseY) {
         if (this.visible) {
            if (this.field_146155_p) {
               this.field_146156_o = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
               this.field_146156_o = MathHelper.clamp_float(this.field_146156_o, 0.0F, 1.0F);
               mcIn.gameSettings.setSoundLevel(this.field_146153_r, this.field_146156_o);
               mcIn.gameSettings.saveOptions();
               this.displayString = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.getSoundVolume(this.field_146153_r);
            }

            mcIn.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.field_146156_o * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.field_146156_o * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
         }
      }

      @Override
      public boolean mousePressed(Minecraft mcIn, int mouseX, int mouseY) {
         if (super.mousePressed(mcIn, mouseX, mouseY)) {
            this.field_146156_o = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
            this.field_146156_o = MathHelper.clamp_float(this.field_146156_o, 0.0F, 1.0F);
            mcIn.gameSettings.setSoundLevel(this.field_146153_r, this.field_146156_o);
            mcIn.gameSettings.saveOptions();
            this.displayString = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.getSoundVolume(this.field_146153_r);
            this.field_146155_p = true;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void playPressSound(SoundHandler soundHandlerIn) {
      }

      @Override
      public void mouseReleased(int mouseX, int mouseY) {
         if (this.field_146155_p) {
            if (this.field_146153_r != SoundCategory.MASTER) {
               GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(this.field_146153_r);
            }

            GuiScreenOptionsSounds.this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         }

         this.field_146155_p = false;
      }
   }
}
