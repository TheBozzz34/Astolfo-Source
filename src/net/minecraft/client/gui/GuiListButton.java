package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiListButton extends GuiButton {
   private boolean field_175216_o;
   private String localizationStr;
   private final GuiPageButtonList.GuiResponder guiResponder;

   public GuiListButton(GuiPageButtonList.GuiResponder responder, int i, int j, int k, String string, boolean bl) {
      super(i, j, k, 150, 20, "");
      this.localizationStr = string;
      this.field_175216_o = bl;
      this.displayString = this.buildDisplayString();
      this.guiResponder = responder;
   }

   private String buildDisplayString() {
      return I18n.format(this.localizationStr) + ": " + (this.field_175216_o ? I18n.format("gui.yes") : I18n.format("gui.no"));
   }

   public void func_175212_b(boolean bl) {
      this.field_175216_o = bl;
      this.displayString = this.buildDisplayString();
      this.guiResponder.func_175321_a(this.id, bl);
   }

   @Override
   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY)) {
         this.field_175216_o = !this.field_175216_o;
         this.displayString = this.buildDisplayString();
         this.guiResponder.func_175321_a(this.id, this.field_175216_o);
         return true;
      } else {
         return false;
      }
   }
}
