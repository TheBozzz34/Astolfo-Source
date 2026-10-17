package net.minecraft.realms;

import com.mojang.util.UUIDTypeAdapter;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RealmsScreen {
   public static final int SKIN_HEAD_U = 8;
   public static final int SKIN_HEAD_V = 8;
   public static final int SKIN_HEAD_WIDTH = 8;
   public static final int SKIN_HEAD_HEIGHT = 8;
   public static final int SKIN_HAT_U = 40;
   public static final int SKIN_HAT_V = 8;
   public static final int SKIN_HAT_WIDTH = 8;
   public static final int SKIN_HAT_HEIGHT = 8;
   public static final int SKIN_TEX_WIDTH = 64;
   public static final int SKIN_TEX_HEIGHT = 64;
   public Minecraft minecraft;
   public int width;
   public int height;
   private GuiScreenRealmsProxy proxy = new GuiScreenRealmsProxy(this);

   public GuiScreenRealmsProxy getProxy() {
      return this.proxy;
   }

   public void init() {
   }

   public void init(Minecraft minecraft, int i, int j) {
   }

   public void drawCenteredString(String string, int i, int j, int k) {
      this.proxy.func_154325_a(string, i, j, k);
   }

   public void drawString(String string, int i, int j, int k) {
      this.proxy.drawStringNoShadow(string, i, j, k);
   }

   public void blit(int i, int j, int k, int l, int m, int n) {
      this.proxy.drawTexturedModalRect(i, j, k, l, m, n);
   }

   public static void blit(int i, int j, float f, float g, int k, int l, int m, int n, float h, float f2) {
      Gui.drawScaledCustomSizeModalRect(i, j, f, g, k, l, m, n, h, f2);
   }

   public static void blit(int i, int j, float f, float g, int k, int l, float h, float f2) {
      Gui.drawModalRectWithCustomSizedTexture(i, j, f, g, k, l, h, f2);
   }

   public void fillGradient(int i, int j, int k, int l, int m, int n) {
      this.proxy.drawGradientRect(i, j, k, l, m, n);
   }

   public void renderBackground() {
      this.proxy.drawDefaultBackground();
   }

   public boolean isPauseScreen() {
      return this.proxy.doesGuiPauseGame();
   }

   public void renderBackground(int i) {
      this.proxy.drawWorldBackground(i);
   }

   public void render(int i, int j, float f) {
      for (int k = 0; k < this.proxy.func_154320_j().size(); k++) {
         this.proxy.func_154320_j().get(k).render(i, j);
      }
   }

   public void renderTooltip(ItemStack itemStack, int i, int j) {
      this.proxy.renderToolTip(itemStack, i, j);
   }

   public void renderTooltip(String string, int i, int j) {
      this.proxy.drawCreativeTabHoveringText(string, i, j);
   }

   public void renderTooltip(List<String> list, int i, int j) {
      this.proxy.drawHoveringText(list, i, j);
   }

   public static void bindFace(String string, String string2) {
      ResourceLocation resourceLocation = AbstractClientPlayer.getLocationSkin(string2);
      if (resourceLocation == null) {
         resourceLocation = DefaultPlayerSkin.getDefaultSkin(UUIDTypeAdapter.fromString(string));
      }

      AbstractClientPlayer.getDownloadImageSkin(resourceLocation, string2);
      Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
   }

   public static void bind(String string) {
      ResourceLocation resourceLocation = new ResourceLocation(string);
      Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
   }

   public void tick() {
   }

   public int width() {
      return this.proxy.width;
   }

   public int height() {
      return this.proxy.height;
   }

   public int fontLineHeight() {
      return this.proxy.func_154329_h();
   }

   public int fontWidth(String string) {
      return this.proxy.func_154326_c(string);
   }

   public void fontDrawShadow(String string, int i, int j, int k) {
      this.proxy.drawStringWithShadow(string, i, j, k);
   }

   public List<String> fontSplit(String string, int i) {
      return this.proxy.func_154323_a(string, i);
   }

   public void buttonClicked(RealmsButton realmsButton) {
   }

   public static RealmsButton newButton(int i, int j, int k, String string) {
      return new RealmsButton(i, j, k, string);
   }

   public static RealmsButton newButton(int i, int j, int k, int l, int m, String string) {
      return new RealmsButton(i, j, k, l, m, string);
   }

   public void buttonsClear() {
      this.proxy.func_154324_i();
   }

   public void buttonsAdd(RealmsButton realmsButton) {
      this.proxy.func_154327_a(realmsButton);
   }

   public List<RealmsButton> buttons() {
      return this.proxy.func_154320_j();
   }

   public void buttonsRemove(RealmsButton realmsButton) {
      this.proxy.func_154328_b(realmsButton);
   }

   public RealmsEditBox newEditBox(int i, int j, int k, int l, int m) {
      return new RealmsEditBox(i, j, k, l, m);
   }

   public void mouseClicked(int i, int j, int k) {
   }

   public void mouseEvent() {
   }

   public void keyboardEvent() {
   }

   public void mouseReleased(int i, int j, int k) {
   }

   public void mouseDragged(int i, int j, int k, long l) {
   }

   public void keyPressed(char c, int i) {
   }

   public void confirmResult(boolean bl, int i) {
   }

   public static String getLocalizedString(String string) {
      return I18n.format(string);
   }

   public static String getLocalizedString(String string, Object... objects) {
      return I18n.format(string, objects);
   }

   public RealmsAnvilLevelStorageSource getLevelStorageSource() {
      return new RealmsAnvilLevelStorageSource(Minecraft.getMinecraft().getSaveLoader());
   }

   public void removed() {
   }
}
