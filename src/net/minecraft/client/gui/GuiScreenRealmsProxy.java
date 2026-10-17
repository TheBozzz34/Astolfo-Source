package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class GuiScreenRealmsProxy extends GuiScreen {
   private RealmsScreen field_154330_a;

   public GuiScreenRealmsProxy(RealmsScreen realmsScreen) {
      this.field_154330_a = realmsScreen;
      super.buttonList = Collections.synchronizedList(Lists.newArrayList());
   }

   public RealmsScreen func_154321_a() {
      return this.field_154330_a;
   }

   @Override
   public void initGui() {
      this.field_154330_a.init();
      super.initGui();
   }

   public void func_154325_a(String string, int i, int j, int k) {
      super.drawCenteredString(this.fontRendererObj, string, i, j, k);
   }

   public void drawStringNoShadow(String string, int i, int j, int k) {
      super.drawString(this.fontRendererObj, string, i, j, k);
   }

   @Override
   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      this.field_154330_a.blit(x, y, textureX, textureY, width, height);
      super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
   }

   @Override
   public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      super.drawGradientRect(left, top, right, bottom, startColor, endColor);
   }

   @Override
   public void drawDefaultBackground() {
      super.drawDefaultBackground();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return super.doesGuiPauseGame();
   }

   @Override
   public void drawWorldBackground(int tint) {
      super.drawWorldBackground(tint);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.field_154330_a.render(mouseX, mouseY, partialTicks);
   }

   @Override
   public void renderToolTip(ItemStack stack, int x, int y) {
      super.renderToolTip(stack, x, y);
   }

   @Override
   public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
      super.drawCreativeTabHoveringText(tabName, mouseX, mouseY);
   }

   @Override
   public void drawHoveringText(List<String> textLines, int x, int y) {
      super.drawHoveringText(textLines, x, y);
   }

   @Override
   public void updateScreen() {
      this.field_154330_a.tick();
      super.updateScreen();
   }

   public int func_154329_h() {
      return this.fontRendererObj.FONT_HEIGHT;
   }

   public int func_154326_c(String string) {
      return this.fontRendererObj.getStringWidth(string);
   }

   public void drawStringWithShadow(String string, int i, int j, int k) {
      this.fontRendererObj.drawStringWithShadow(string, i, j, k);
   }

   public List<String> func_154323_a(String string, int i) {
      return this.fontRendererObj.listFormattedStringToWidth(string, i);
   }

   @Override
   public final void actionPerformed(GuiButton button) throws IOException {
      this.field_154330_a.buttonClicked(((GuiButtonRealmsProxy)button).getRealmsButton());
   }

   public void func_154324_i() {
      super.buttonList.clear();
   }

   public void func_154327_a(RealmsButton realmsButton) {
      super.buttonList.add(realmsButton.getProxy());
   }

   public List<RealmsButton> func_154320_j() {
      ArrayList arrayList = Lists.newArrayListWithExpectedSize(super.buttonList.size());

      for (GuiButton guiButton : super.buttonList) {
         arrayList.add(((GuiButtonRealmsProxy)guiButton).getRealmsButton());
      }

      return arrayList;
   }

   public void func_154328_b(RealmsButton realmsButton) {
      super.buttonList.remove(realmsButton);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.field_154330_a.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void handleMouseInput() throws IOException {
      this.field_154330_a.mouseEvent();
      super.handleMouseInput();
   }

   @Override
   public void handleKeyboardEvent() throws IOException {
      this.field_154330_a.keyboardEvent();
      super.handleKeyboardEvent();
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      this.field_154330_a.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      this.field_154330_a.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      this.field_154330_a.keyPressed(typedChar, keyCode);
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      this.field_154330_a.confirmResult(result, id);
   }

   @Override
   public void onGuiClosed() {
      this.field_154330_a.removed();
      super.onGuiClosed();
   }
}
