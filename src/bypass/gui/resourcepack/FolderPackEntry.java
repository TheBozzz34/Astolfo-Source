package bypass.gui.resourcepack;

import bypass.gui.screen.CustomResourcePacksScreen;
import java.io.File;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class FolderPackEntry extends CustomPackEntry {
   private static final ResourceLocation I = new ResourceLocation("textures/gui/folder.png");
   private final CustomResourcePacksScreen T;
   public final File w;
   public final String B;
   public final boolean Y;

   public FolderPackEntry(CustomResourcePacksScreen customResourcePacksScreen, File file) {
      super(customResourcePacksScreen);
      this.T = customResourcePacksScreen;
      this.w = file;
      this.B = file.getName();
      this.Y = false;
   }

   public FolderPackEntry(CustomResourcePacksScreen customResourcePacksScreen, File file, boolean bl) {
      super(customResourcePacksScreen);
      this.T = customResourcePacksScreen;
      this.w = file;
      this.B = "..";
      this.Y = bl;
   }

   @Override
   public void func_148313_c() {
      this.mc.getTextureManager().bindTexture(I);
   }

   @Override
   public String func_148312_b() {
      return this.B;
   }

   @Override
   public String func_148311_a() {
      return this.Y ? "(Back)" : "(Folder)";
   }

   @Override
   public boolean mousePressed(int slotIndex, int i, int j, int k, int l, int m) {
      this.T.S(this.w);
      return true;
   }

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      this.E(this, x, y, isSelected);
   }

   public void E(FolderPackEntry folderPackEntry, int i, int j, boolean bl) {
      folderPackEntry.func_148313_c();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      Gui.drawModalRectWithCustomSizedTexture(i, j, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      GlStateManager.disableBlend();
      if ((this.mc.gameSettings.touchscreen || bl) && folderPackEntry.func_148310_d()) {
         Gui.drawRect(i, j, i + 32, j + 32, -1601138544);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }

      String string = folderPackEntry.func_148312_b();
      int k = this.mc.fontRendererObj.getStringWidth(string);
      if (k > 157) {
         string = this.mc.fontRendererObj.trimStringToWidth(string, 157 - this.mc.fontRendererObj.getStringWidth("...")) + "...";
      }

      this.mc.fontRendererObj.drawStringWithShadow(string, i + 32 + 2, j + 1, 16777215);
      List list = this.mc.fontRendererObj.listFormattedStringToWidth(folderPackEntry.func_148311_a(), 157);

      for (int l = 0; l < 2 && l < list.size(); l++) {
         this.mc.fontRendererObj.drawStringWithShadow((String)list.get(l), i + 32 + 2, j + 12 + 10 * l, 8421504);
      }
   }
}
