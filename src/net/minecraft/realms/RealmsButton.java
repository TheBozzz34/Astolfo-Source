package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonRealmsProxy;
import net.minecraft.util.ResourceLocation;

public class RealmsButton {
   public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   private GuiButtonRealmsProxy proxy;

   public RealmsButton(int buttonId, int x, int y, String text) {
      this.proxy = new GuiButtonRealmsProxy(this, buttonId, x, y, text);
   }

   public RealmsButton(int buttonId, int x, int y, int widthIn, int heightIn, String text) {
      this.proxy = new GuiButtonRealmsProxy(this, buttonId, x, y, text, widthIn, heightIn);
   }

   public GuiButton getProxy() {
      return this.proxy;
   }

   public int id() {
      return this.proxy.getId();
   }

   public boolean active() {
      return this.proxy.getEnabled();
   }

   public void active(boolean bl) {
      this.proxy.setEnabled(bl);
   }

   public void msg(String string) {
      this.proxy.setText(string);
   }

   public int getWidth() {
      return this.proxy.getButtonWidth();
   }

   public int getHeight() {
      return this.proxy.getHeight();
   }

   public int y() {
      return this.proxy.getPositionY();
   }

   public void render(int i, int j) {
      this.proxy.drawButton(Minecraft.getMinecraft(), i, j);
   }

   public void clicked(int i, int j) {
   }

   public void released(int i, int j) {
   }

   public void blit(int i, int j, int k, int l, int m, int n) {
      this.proxy.drawTexturedModalRect(i, j, k, l, m, n);
   }

   public void renderBg(int i, int j) {
   }

   public int getYImage(boolean bl) {
      return this.proxy.func_154312_c(bl);
   }
}
