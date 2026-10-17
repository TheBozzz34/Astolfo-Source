package bypass.gui.screen;

import bypass.gui.resourcepack.FolderPackEntry;
import bypass.gui.resourcepack.FolderResourcePackRepository;
import bypass.gui.resourcepack.PackSearch;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiResourcePackAvailable;
import net.minecraft.client.gui.GuiResourcePackSelected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.Util;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

public class CustomResourcePacksScreen extends GuiScreenResourcePacks {
   private final GuiScreen parentGui;
   private GuiTextField searchField;
   private GuiResourcePackAvailable availablePackList;
   private GuiResourcePackSelected selectedPackList;
   private List<ResourcePackListEntry> allAvailableEntries;
   private List<ResourcePackListEntry> filteredAvailableEntries;
   private List<ResourcePackListEntry> availableEntriesSink;
   private List<ResourcePackListEntry> selectedEntries;
   private PackSearch packSearch;
   private File currentDirectory;
   private GuiButton pressedButton;
   private boolean needsRefresh;
   private boolean packsChanged;
   private Comparator<ResourcePackListEntry> sortComparator;
   private static final int GRADIENT_TOP_COLOR = new Color(7, 7, 7).getRGB();
   private static final int GRADIENT_BOTTOM_COLOR = new Color(17, 17, 17).getRGB();

   public CustomResourcePacksScreen(GuiScreen guiScreen) {
      super(guiScreen);
      this.parentGui = guiScreen;
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 100 - 75, this.height - 26, I18n.format("gui.done")));
      this.buttonList.add(new GuiOptionButton(2, this.width / 2 + 100 - 75, this.height - 48, I18n.format("resourcePack.openFolder")));
      this.buttonList.add(new GuiOptionButton(10, this.width / 2 - 204, this.height - 26, 40, 20, "A-Z"));
      this.buttonList.add(new GuiOptionButton(11, this.width / 2 - 204 + 44, this.height - 26, 40, 20, "Z-A"));
      this.buttonList.add(new GuiOptionButton(20, this.width / 2 - 74, this.height - 26, 70, 20, "Refresh"));
      String string = this.searchField == null ? "" : this.searchField.getText();
      this.searchField = new GuiTextField(30, this.fontRendererObj, this.width / 2 - 203, this.height - 46, 198, 16);
      this.searchField.setText(string);
      if (!this.packsChanged) {
         this.allAvailableEntries = Lists.newArrayListWithCapacity(8);
         this.filteredAvailableEntries = Lists.newArrayListWithCapacity(8);
         this.availableEntriesSink = Lists.newArrayListWithCapacity(1);
         this.selectedEntries = Lists.newArrayListWithCapacity(8);
         ResourcePackRepository resourcePackRepository = this.mc.getResourcePackRepository();
         resourcePackRepository.updateRepositoryEntriesAll();
         this.currentDirectory = resourcePackRepository.getDirResourcepacks();
         this.allAvailableEntries.addAll(this.createAvailablePackList(resourcePackRepository));

         for (ResourcePackRepository.Entry entry : Lists.reverse(resourcePackRepository.getRepositoryEntries())) {
            this.selectedEntries.add(new ResourcePackListEntryFound(this, entry));
         }

         this.selectedEntries.add(new ResourcePackListEntryDefault(this));
      }

      this.availablePackList = new GuiResourcePackAvailable(this.mc, 200, this.height, this.filteredAvailableEntries);
      this.availablePackList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
      this.availablePackList.registerScrollButtons(7, 8);
      this.selectedPackList = new GuiResourcePackSelected(this.mc, 200, this.height, this.selectedEntries);
      this.selectedPackList.setSlotXBoundsFromLeft(this.width / 2 + 4);
      this.selectedPackList.registerScrollButtons(7, 8);
      this.packSearch = new PackSearch(this.allAvailableEntries, this.filteredAvailableEntries);
      this.packSearch.setComparator(this.sortComparator == null ? (this.sortComparator = PackSearch.NAME_ASCENDING) : this.sortComparator);
      this.packSearch.setSearchQuery(this.searchField.getText().trim());
   }

   @Override
   public void actionPerformed(GuiButton button) {
      if (button.id == 20) {
         this.refreshAvailablePacks();
      } else if (button.id == 11) {
         this.packSearch.setComparator(this.sortComparator = PackSearch.NAME_DESCENDING);
      } else if (button.id == 10) {
         this.packSearch.setComparator(this.sortComparator = PackSearch.NAME_ASCENDING);
      } else if (button.id == 2) {
         openInFileExplorer(this.mc.getResourcePackRepository().getDirResourcepacks());
      } else if (button.id == 1) {
         if (this.packsChanged) {
            List list = this.applySelectedPacks();
            this.mc.gameSettings.resourcePacks.clear();

            for (Object object : list) {
               ResourcePackRepository.Entry entry = (ResourcePackRepository.Entry)object;
               this.mc.gameSettings.resourcePacks.add(entry.getResourcePackName());
            }

            this.mc.gameSettings.saveOptions();
            this.mc.refreshResources();
            System.gc();
         }

         this.mc.displayGuiScreen(this.parentGui);
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         for (GuiButton guiButton : this.buttonList) {
            if (guiButton.mousePressed(this.mc, mouseX, mouseY)) {
               this.pressedButton = guiButton;
               guiButton.playPressSound(this.mc.getSoundHandler());
               this.actionPerformed(guiButton);
            }
         }
      }

      this.availablePackList.mouseClicked(mouseX, mouseY, mouseButton);
      this.selectedPackList.mouseClicked(mouseX, mouseY, mouseButton);
      this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
      this.packSearch.updateFilter();
   }

   @Override
   public void handleMouseInput() throws IOException {
      try {
         super.handleMouseInput();
      } catch (NullPointerException nullPointerException) {
      }

      this.availablePackList.handleMouseInput();
      this.selectedPackList.handleMouseInput();
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      if (state == 0 && this.pressedButton != null) {
         this.pressedButton.mouseReleased(mouseX, mouseY);
         this.pressedButton = null;
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);
      if (this.searchField.isFocused()) {
         this.searchField.textboxKeyTyped(typedChar, keyCode);
         this.packSearch.setSearchQuery(this.searchField.getText().trim());
      }
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      System.out.println("call");
      this.allAvailableEntries.clear();
      this.filteredAvailableEntries.clear();
      this.availableEntriesSink.clear();
      this.selectedEntries.clear();
      System.gc();
   }

   @Override
   public void updateScreen() {
      this.searchField.updateCursorCounter();
      if (this.needsRefresh) {
         this.needsRefresh = false;
         this.applySelectedPacks();
         this.refreshAvailablePacks();
      }
   }

   public void openDirectory(File file) {
      this.currentDirectory = file;
      this.applySelectedPacks();
      this.refreshAvailablePacks();
   }

   public void refreshAvailablePacks() {
      this.allAvailableEntries.clear();
      this.allAvailableEntries.addAll(this.createAvailablePackList(this.mc.getResourcePackRepository()));
      this.packSearch.updateFilter();
   }

   public List<ResourcePackRepository.Entry> applySelectedPacks() {
      ArrayList arrayList = Lists.newArrayListWithCapacity(this.selectedEntries.size());

      for (ResourcePackListEntry resourcePackListEntry : this.selectedEntries) {
         if (resourcePackListEntry instanceof ResourcePackListEntryFound) {
            ResourcePackListEntryFound resourcePackListEntryFound = (ResourcePackListEntryFound)resourcePackListEntry;
            if (resourcePackListEntryFound.func_148318_i() != null) {
               arrayList.add(resourcePackListEntryFound.func_148318_i());
            }
         }
      }

      Collections.reverse(arrayList);
      this.mc.getResourcePackRepository().setRepositories(arrayList);
      return arrayList;
   }

   public static void openInFileExplorer(File file) {
      String string = file.getAbsolutePath();
      if (Util.getOSType() == Util.EnumOS.OSX) {
         try {
            Runtime.getRuntime().exec(new String[]{"/usr/bin/open", string});
            return;
         } catch (Exception exception) {
         }
      } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
         String string2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", string);

         try {
            Runtime.getRuntime().exec(string2);
            return;
         } catch (Exception exception2) {
         }
      }

      try {
         Class class2 = Class.forName("java.awt.Desktop");
         Object object = class2.getMethod("getDesktop").invoke(null);
         class2.getMethod("browse", URI.class).invoke(object, file.toURI());
      } catch (Throwable throwable) {
         Sys.openURL("file://" + string);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(0);
      this.availablePackList.drawScreen(mouseX, mouseY, partialTicks);
      this.selectedPackList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawOverlayGradient(0, 32, 255, 255);
      this.drawOverlayGradient(this.height - 52, this.height, 255, 255);
      this.searchField.drawTextBox();

      for (GuiButton guiButton : this.buttonList) {
         guiButton.drawButton(this.mc, mouseX, mouseY);
      }
   }

   public void drawOverlayGradient(int i, int j, int k, int l) {
      this.drawGradientRect(0, i, this.width, j, GRADIENT_TOP_COLOR, GRADIENT_BOTTOM_COLOR);
   }

   @Override
   public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      float f = (startColor >> 24 & 0xFF) / 255.0F;
      float g = (startColor >> 16 & 0xFF) / 255.0F;
      float h = (startColor >> 8 & 0xFF) / 255.0F;
      float f2 = (startColor & 0xFF) / 255.0F;
      float f3 = (endColor >> 24 & 0xFF) / 255.0F;
      float f4 = (endColor >> 16 & 0xFF) / 255.0F;
      float f5 = (endColor >> 8 & 0xFF) / 255.0F;
      float f6 = (endColor & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(right, top, 0.0).color(g, h, f2, f).endVertex();
      worldRenderer.pos(left, top, 0.0).color(g, h, f2, f).endVertex();
      worldRenderer.pos(left, bottom, 0.0).color(f4, f5, f6, f3).endVertex();
      worldRenderer.pos(right, bottom, 0.0).color(f4, f5, f6, f3).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   private List<ResourcePackListEntryFound> createAvailablePackList(ResourcePackRepository resourcePackRepository) {
      ArrayList arrayList = Lists.newArrayList();
      if (!resourcePackRepository.getDirResourcepacks().equals(this.currentDirectory)) {
         arrayList.add(new FolderPackEntry(this, this.currentDirectory.getParentFile(), true));
      }

      File[] files = this.currentDirectory.listFiles();
      if (files != null) {
         for (File file : files) {
            if (file.isDirectory() && !new File(file, "pack.mcmeta").isFile()) {
               arrayList.add(new FolderPackEntry(this, file));
            } else {
               ResourcePackRepository.Entry entry = FolderResourcePackRepository.createEntry(resourcePackRepository, file);
               if (entry != null) {
                  try {
                     entry.updateResourcePack();
                     arrayList.add(new ResourcePackListEntryFound(this, entry));
                  } catch (Exception exception) {
                  }
               }
            }
         }
      }

      List list = resourcePackRepository.getRepositoryEntries();
      arrayList.removeIf(object -> ((net.minecraft.client.resources.ResourcePackListEntryFound)object).func_148318_i() != null && list.contains(((net.minecraft.client.resources.ResourcePackListEntryFound)object).func_148318_i()));
      return arrayList;
   }

   @Override
   public boolean hasResourcePackEntry(ResourcePackListEntry resourcePackListEntry) {
      return this.selectedEntries.contains(resourcePackListEntry);
   }

   @Override
   public List getListContaining(ResourcePackListEntry resourcePackListEntry) {
      return this.hasResourcePackEntry(resourcePackListEntry) ? this.selectedEntries : this.allAvailableEntries;
   }

   @Override
   public List<ResourcePackListEntry> getAvailableResourcePacks() {
      this.needsRefresh = true;
      this.availableEntriesSink.clear();
      return this.availableEntriesSink;
   }

   @Override
   public List<ResourcePackListEntry> getSelectedResourcePacks() {
      this.needsRefresh = true;
      return this.selectedEntries;
   }

   @Override
   public void markChanged() {
      this.packsChanged = true;
   }
}
