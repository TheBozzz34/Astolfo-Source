package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

public class GuiScreenResourcePacks extends GuiScreen {
   private static final Logger logger = LogManager.getLogger();
   private final GuiScreen parentScreen;
   private List<ResourcePackListEntry> availableResourcePacks;
   private List<ResourcePackListEntry> selectedResourcePacks;
   private GuiResourcePackAvailable availableResourcePacksList;
   private GuiResourcePackSelected selectedResourcePacksList;
   private boolean changed = false;

   public GuiScreenResourcePacks(GuiScreen parentScreenIn) {
      this.parentScreen = parentScreenIn;
   }

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("resourcePack.openFolder")));
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done")));
      if (!this.changed) {
         this.availableResourcePacks = Lists.newArrayList();
         this.selectedResourcePacks = Lists.newArrayList();
         ResourcePackRepository resourcePackRepository = this.mc.getResourcePackRepository();
         resourcePackRepository.updateRepositoryEntriesAll();
         ArrayList arrayList = Lists.newArrayList(resourcePackRepository.getRepositoryEntriesAll());
         arrayList.removeAll(resourcePackRepository.getRepositoryEntries());

         for (Object object : arrayList) {
            ResourcePackRepository.Entry entry = (ResourcePackRepository.Entry)object;
            this.availableResourcePacks.add(new ResourcePackListEntryFound(this, entry));
         }

         for (ResourcePackRepository.Entry entry2 : Lists.reverse(resourcePackRepository.getRepositoryEntries())) {
            this.selectedResourcePacks.add(new ResourcePackListEntryFound(this, entry2));
         }

         this.selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
      }

      this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height, this.availableResourcePacks);
      this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
      this.availableResourcePacksList.registerScrollButtons(7, 8);
      this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height, this.selectedResourcePacks);
      this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
      this.selectedResourcePacksList.registerScrollButtons(7, 8);
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.selectedResourcePacksList.handleMouseInput();
      this.availableResourcePacksList.handleMouseInput();
   }

   public boolean hasResourcePackEntry(ResourcePackListEntry resourcePackListEntry) {
      return this.selectedResourcePacks.contains(resourcePackListEntry);
   }

   public List<ResourcePackListEntry> getListContaining(ResourcePackListEntry resourcePackListEntry) {
      return this.hasResourcePackEntry(resourcePackListEntry) ? this.selectedResourcePacks : this.availableResourcePacks;
   }

   public List<ResourcePackListEntry> getAvailableResourcePacks() {
      return this.availableResourcePacks;
   }

   public List<ResourcePackListEntry> getSelectedResourcePacks() {
      return this.selectedResourcePacks;
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 2) {
            File file2 = this.mc.getResourcePackRepository().getDirResourcepacks();
            String string = file2.getAbsolutePath();
            if (Util.getOSType() == Util.EnumOS.OSX) {
               try {
                  logger.info(string);
                  Runtime.getRuntime().exec(new String[]{"/usr/bin/open", string});
                  return;
               } catch (IOException ioException) {
                  logger.error("Couldn't open file", ioException);
               }
            } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
               String string2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", string);

               try {
                  Runtime.getRuntime().exec(string2);
                  return;
               } catch (IOException ioException2) {
                  logger.error("Couldn't open file", ioException2);
               }
            }

            boolean bl = false;

            try {
               Class class2 = Class.forName("java.awt.Desktop");
               Object object = class2.getMethod("getDesktop").invoke(null);
               class2.getMethod("browse", URI.class).invoke(object, file2.toURI());
            } catch (Throwable throwable) {
               logger.error("Couldn't open link", throwable);
               bl = true;
            }

            if (bl) {
               logger.info("Opening via system class!");
               Sys.openURL("file://" + string);
            }
         } else if (button.id == 1) {
            if (this.changed) {
               ArrayList arrayList = Lists.newArrayList();

               for (ResourcePackListEntry resourcePackListEntry : this.selectedResourcePacks) {
                  if (resourcePackListEntry instanceof ResourcePackListEntryFound) {
                     arrayList.add(((ResourcePackListEntryFound)resourcePackListEntry).func_148318_i());
                  }
               }

               Collections.reverse(arrayList);
               this.mc.getResourcePackRepository().setRepositories(arrayList);
               this.mc.gameSettings.resourcePacks.clear();
               this.mc.gameSettings.incompatibleResourcePacks.clear();

               for (Object object2 : arrayList) {
                  ResourcePackRepository.Entry entry = (ResourcePackRepository.Entry)object2;
                  this.mc.gameSettings.resourcePacks.add(entry.getResourcePackName());
                  if (entry.func_183027_f() != 1) {
                     this.mc.gameSettings.incompatibleResourcePacks.add(entry.getResourcePackName());
                  }
               }

               this.mc.gameSettings.saveOptions();
               this.mc.refreshResources();
            }

            this.mc.displayGuiScreen(this.parentScreen);
         }
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
      this.selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(0);
      this.availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
      this.selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.title"), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void markChanged() {
      this.changed = true;
   }
}
