package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

public class GuiRenameWorld extends GuiScreen {
   private GuiScreen parentScreen;
   private GuiTextField field_146583_f;
   private final String saveName;
   private static final long a = 27129258599505L;

   public GuiRenameWorld(GuiScreen var1, String var2) {
      this.parentScreen = var1;
      this.saveName = var2;
   }

   @Override
   public void updateScreen() {
      this.field_146583_f.updateCursorCounter();
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectWorld.renameButton")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
      ISaveFormat var3 = this.mc.getSaveLoader();
      WorldInfo var4 = var3.getWorldInfo(this.saveName);
      String var5 = var4.getWorldName();
      this.field_146583_f = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
      this.field_146583_f.setFocused(true);
      this.field_146583_f.setText(var5);
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled) {
         if (var1.id == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (var1.id == 0) {
            ISaveFormat var5 = this.mc.getSaveLoader();
            var5.renameWorld(this.saveName, this.field_146583_f.getText().trim());
            this.mc.displayGuiScreen(this.parentScreen);
         }
      }
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      this.field_146583_f.textboxKeyTyped(var1, var2);
      this.buttonList.get(0).enabled = this.field_146583_f.getText().trim().length() > 0;
      if (var2 == 28 || var2 == 156) {
         this.actionPerformed(this.buttonList.get(0));
      }
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      super.mouseClicked(var1, var2, var3);
      this.field_146583_f.mouseClicked(var1, var2, var3);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, I18n.format("selectWorld.renameTitle"), this.width / 2, 20, 16777215);
      this.I(this.fontRendererObj, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
      this.field_146583_f.drawTextBox();
      super.drawScreen(var1, var2, var3);
   }
}
