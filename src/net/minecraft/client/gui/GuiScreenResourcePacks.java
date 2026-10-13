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
   private static final long x = 20885604809293L;

   public GuiScreenResourcePacks(GuiScreen var1) {
      this.parentScreen = var1;
   }

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("resourcePack.openFolder")));
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done")));
      if (!this.changed) {
         this.availableResourcePacks = Lists.newArrayList();
         this.selectedResourcePacks = Lists.newArrayList();
         ResourcePackRepository var4 = this.mc.getResourcePackRepository();
         var4.updateRepositoryEntriesAll();
         ArrayList var5 = Lists.newArrayList(var4.getRepositoryEntriesAll());
         var5.removeAll(var4.getRepositoryEntries());

         for (Object var7$raw : var5) {
            ResourcePackRepository.Entry var7 = (ResourcePackRepository.Entry)var7$raw;
            this.availableResourcePacks.add(new ResourcePackListEntryFound(this, var7));
         }

         for (ResourcePackRepository.Entry var9 : Lists.reverse(var4.getRepositoryEntries())) {
            this.selectedResourcePacks.add(new ResourcePackListEntryFound(this, var9));
         }

         this.selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
      }

      this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height, this.availableResourcePacks);
      this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
      this.availableResourcePacksList.U(7, 8);
      this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height, this.selectedResourcePacks);
      this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
      this.selectedResourcePacksList.U(7, 8);
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.selectedResourcePacksList.handleMouseInput();
      this.availableResourcePacksList.handleMouseInput();
   }

   public boolean hasResourcePackEntry(ResourcePackListEntry var1) {
      return this.selectedResourcePacks.contains(var1);
   }

   public List<ResourcePackListEntry> getListContaining(ResourcePackListEntry var1) {
      return this.hasResourcePackEntry(var1) ? this.selectedResourcePacks : this.availableResourcePacks;
   }

   public List<ResourcePackListEntry> getAvailableResourcePacks() {
      return this.availableResourcePacks;
   }

   public List<ResourcePackListEntry> getSelectedResourcePacks() {
      return this.selectedResourcePacks;
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled) {
         if (var1.id == 2) {
            File var5 = this.mc.getResourcePackRepository().getDirResourcepacks();
            String var6 = var5.getAbsolutePath();
            if (Util.getOSType() == Util.EnumOS.OSX) {
               try {
                  logger.info(var6);
                  Runtime.getRuntime().exec(new String[]{"/usr/bin/open", var6});
                  return;
               } catch (IOException var12) {
                  logger.error("Couldn't open file", var12);
               }
            } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
               String var7 = String.format("cmd.exe /C start \"Open file\" \"%s\"", var6);

               try {
                  Runtime.getRuntime().exec(var7);
                  return;
               } catch (IOException var11) {
                  logger.error("Couldn't open file", var11);
               }
            }

            boolean var16 = false;

            try {
               Class var8 = Class.forName("java.awt.Desktop");
               Object var9 = var8.getMethod("getDesktop").invoke(null);
               var8.getMethod("browse", URI.class).invoke(var9, var5.toURI());
            } catch (Throwable var10) {
               logger.error("Couldn't open link", var10);
               var16 = true;
            }

            if (var16) {
               logger.info("Opening via system class!");
               Sys.openURL("file://" + var6);
            }
         } else if (var1.id == 1) {
            if (this.changed) {
               ArrayList var13 = Lists.newArrayList();

               for (ResourcePackListEntry var17 : this.selectedResourcePacks) {
                  if (var17 instanceof ResourcePackListEntryFound) {
                     var13.add(((ResourcePackListEntryFound)var17).func_148318_i());
                  }
               }

               Collections.reverse(var13);
               this.mc.getResourcePackRepository().setRepositories(var13);
               this.mc.gameSettings.resourcePacks.clear();
               this.mc.gameSettings.incompatibleResourcePacks.clear();

               for (Object var18$raw : var13) {
                  ResourcePackRepository.Entry var18 = (ResourcePackRepository.Entry)var18$raw;
                  this.mc.gameSettings.resourcePacks.add(var18.getResourcePackName());
                  if (var18.func_183027_f() != 1) {
                     this.mc.gameSettings.incompatibleResourcePacks.add(var18.getResourcePackName());
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
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      super.mouseClicked(var1, var2, var3);
      this.availableResourcePacksList.mouseClicked(var1, var2, var3);
      this.selectedResourcePacksList.mouseClicked(var1, var2, var3);
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3) {
      super.mouseReleased(var1, var2, var3);
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawBackground(0);
      this.availableResourcePacksList.drawScreen(var1, var2, var3);
      this.selectedResourcePacksList.drawScreen(var1, var2, var3);
      this.d(this.fontRendererObj, I18n.format("resourcePack.title"), this.width / 2, 16, 16777215);
      this.d(this.fontRendererObj, I18n.format("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
      super.drawScreen(var1, var2, var3);
   }

   public void markChanged() {
      this.changed = true;
   }
}
