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
   private static final long a = 120454699248962L;

   public GuiScreenRealmsProxy(RealmsScreen var1) {
      this.field_154330_a = var1;
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

   public void i(String var1, int var2, int var3, int var4) {
      super.d(this.fontRendererObj, var1, var2, var3, var4);
   }

   public void V(String var1, int var2, int var3, int var4) {
      super.I(this.fontRendererObj, var1, var2, var3, var4);
   }

   @Override
   public void drawTexturedModalRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_154330_a.u(var1, var2, var3, var4, var5, var6);
      super.drawTexturedModalRect(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void drawGradientRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      super.drawGradientRect(var1, var2, var3, var4, var5, var6);
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
   public void drawWorldBackground(int var1) {
      super.drawWorldBackground(var1);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.field_154330_a.render(var1, var2, var3);
   }

   @Override
   public void renderToolTip(ItemStack var1, int var2, int var3) {
      super.renderToolTip(var1, var2, var3);
   }

   @Override
   public void drawCreativeTabHoveringText(String var1, int var2, int var3) {
      super.drawCreativeTabHoveringText(var1, var2, var3);
   }

   @Override
   public void drawHoveringText(List<String> var1, int var2, int var3) {
      super.drawHoveringText(var1, var2, var3);
   }

   @Override
   public void updateScreen() {
      this.field_154330_a.Z();
      super.updateScreen();
   }

   public int func_154329_h() {
      return this.fontRendererObj.FONT_HEIGHT;
   }

   public int func_154326_c(String var1) {
      return this.fontRendererObj.getStringWidth(var1);
   }

   public void X(String var1, int var2, int var3, int var4) {
      this.fontRendererObj.drawStringWithShadow(var1, var2, var3, var4);
   }

   public List<String> func_154323_a(String var1, int var2) {
      return this.fontRendererObj.listFormattedStringToWidth(var1, var2);
   }

   @Override
   public final void actionPerformed(GuiButton var1) throws IOException {
      this.field_154330_a.buttonClicked(((GuiButtonRealmsProxy)var1).getRealmsButton());
   }

   public void func_154324_i() {
      super.buttonList.clear();
   }

   public void func_154327_a(RealmsButton var1) {
      super.buttonList.add(var1.getProxy());
   }

   public List<RealmsButton> func_154320_j() {
      ArrayList var4 = Lists.newArrayListWithExpectedSize(super.buttonList.size());

      for (GuiButton var6 : super.buttonList) {
         var4.add(((GuiButtonRealmsProxy)var6).getRealmsButton());
      }

      return var4;
   }

   public void func_154328_b(RealmsButton var1) {
      super.buttonList.remove(var1);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      this.field_154330_a.I(var1, var2, var3);
      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public void handleMouseInput() throws IOException {
      this.field_154330_a.E();
      super.handleMouseInput();
   }

   @Override
   public void I() throws IOException {
      this.field_154330_a.F();
      super.I();
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3) {
      this.field_154330_a.y(var1, var2, var3);
   }

   @Override
   public void mouseClickMove(int var1, int var2, int var3, long var4) {
      this.field_154330_a.n(var1, var2, var3, var4);
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      this.field_154330_a.keyPressed(var1, var2);
   }

   @Override
   public void confirmClicked(boolean var1, int var2) {
      this.field_154330_a.confirmResult(var1, var2);
   }

   @Override
   public void onGuiClosed() {
      this.field_154330_a.n();
      super.onGuiClosed();
   }
}
