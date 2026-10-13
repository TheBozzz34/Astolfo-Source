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
   private static final long a = 87688598720564L;

   public GuiScreenOptionsSounds(GuiScreen var1, GameSettings var2) {
      this.field_146505_f = var1;
      this.game_settings_4 = var2;
   }

   @Override
   public void initGui() {
      int var4 = 0;
      this.field_146507_a = I18n.format("options.sounds.title");
      this.field_146508_h = I18n.format("options.off");
      this.buttonList
         .add(
            new GuiScreenOptionsSounds.Button(
               SoundCategory.MASTER.getCategoryId(), this.width / 2 - 155 + var4 % 2 * 160, this.height / 6 - 12 + 24 * (var4 >> 1), SoundCategory.MASTER, true
            )
         );
      var4 += 2;

      for (SoundCategory var8 : SoundCategory.values()) {
         if (var8 != SoundCategory.MASTER) {
            this.buttonList
               .add(
                  new GuiScreenOptionsSounds.Button(
                     var8.getCategoryId(), this.width / 2 - 155 + var4 % 2 * 160, this.height / 6 - 12 + 24 * (var4 >> 1), var8, false
                  )
               );
            var4++;
         }
      }

      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled && var1.id == 200) {
         this.mc.gameSettings.saveOptions();
         this.mc.displayGuiScreen(this.field_146505_f);
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, this.field_146507_a, this.width / 2, 15, 16777215);
      super.drawScreen(var1, var2, var3);
   }

   public String getSoundVolume(SoundCategory var1) {
      float var4 = this.game_settings_4.getSoundLevel(var1);
      return var4 == 0.0F ? this.field_146508_h : (int)(var4 * 100.0F) + "%";
   }

   public class Button extends GuiButton {
      private final SoundCategory field_146153_r;
      private final String field_146152_s;
      public float field_146156_o = 1.0F;
      public boolean field_146155_p;
      private static final long c = 44835809259567L;

      public Button(int var2, int var3, int var4, SoundCategory var5, boolean var6) {
         super(var2, var3, var4, var6 ? 310 : 150, 20, "");
         this.field_146153_r = var5;
         this.field_146152_s = I18n.format("soundCategory." + var5.getCategoryName());
         this.displayString = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.getSoundVolume(var5);
         this.field_146156_o = GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(var5);
      }

      @Override
      public int getHoverState(boolean var1) {
         return 0;
      }

      @Override
      public void mouseDragged(Minecraft var1, int var2, int var3) {
         if (this.visible) {
            if (this.field_146155_p) {
               this.field_146156_o = (float)(var2 - (this.xPosition + 4)) / (this.width - 8);
               this.field_146156_o = MathHelper.clamp_float(this.field_146156_o, 0.0F, 1.0F);
               var1.gameSettings.setSoundLevel(this.field_146153_r, this.field_146156_o);
               var1.gameSettings.saveOptions();
               this.displayString = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.getSoundVolume(this.field_146153_r);
            }

            var1.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.field_146156_o * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.field_146156_o * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
         }
      }

      @Override
      public boolean mousePressed(Minecraft var1, int var2, int var3) {
         if (super.mousePressed(var1, var2, var3)) {
            this.field_146156_o = (float)(var2 - (this.xPosition + 4)) / (this.width - 8);
            this.field_146156_o = MathHelper.clamp_float(this.field_146156_o, 0.0F, 1.0F);
            var1.gameSettings.setSoundLevel(this.field_146153_r, this.field_146156_o);
            var1.gameSettings.saveOptions();
            this.displayString = this.field_146152_s + ": " + GuiScreenOptionsSounds.this.getSoundVolume(this.field_146153_r);
            this.field_146155_p = true;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void playPressSound(SoundHandler var1) {
      }

      @Override
      public void mouseReleased(int var1, int var2) {
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
