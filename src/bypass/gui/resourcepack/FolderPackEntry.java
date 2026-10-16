package bypass.gui.resourcepack;

import bypass.gui.screen.CustomResourcePacksScreen;
import java.io.File;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class FolderPackEntry extends CustomPackEntry {
   private static final ResourceLocation FOLDER_ICON = new ResourceLocation("textures/gui/folder.png");
   private final CustomResourcePacksScreen packsScreen;
   public final File directory;
   public final String displayName;
   public final boolean isBackEntry;

   public FolderPackEntry(CustomResourcePacksScreen customResourcePacksScreen, File file) {
      super(customResourcePacksScreen);
      this.packsScreen = customResourcePacksScreen;
      this.directory = file;
      this.displayName = file.getName();
      this.isBackEntry = false;
   }

   public FolderPackEntry(CustomResourcePacksScreen customResourcePacksScreen, File file, boolean bl) {
      super(customResourcePacksScreen);
      this.packsScreen = customResourcePacksScreen;
      this.directory = file;
      this.displayName = "..";
      this.isBackEntry = bl;
   }

   @Override
   public void func_148313_c() {
      this.mc.getTextureManager().bindTexture(FOLDER_ICON);
   }

   @Override
   public String func_148312_b() {
      return this.displayName;
   }

   @Override
   public String func_148311_a() {
      return this.isBackEntry ? "(Back)" : "(Folder)";
   }

   @Override
   public boolean mousePressed(int slotIndex, int i, int j, int k, int l, int m) {
      this.packsScreen.openDirectory(this.directory);
      return true;
   }

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      this.drawFolderEntry(this, x, y, isSelected);
   }

   public void drawFolderEntry(FolderPackEntry folderPackEntry, int i, int j, boolean bl) {
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
