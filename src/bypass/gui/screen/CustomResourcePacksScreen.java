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
   private final GuiScreen V;
   private GuiTextField l;
   private GuiResourcePackAvailable f;
   private GuiResourcePackSelected n;
   private List<ResourcePackListEntry> m;
   private List<ResourcePackListEntry> w;
   private List<ResourcePackListEntry> c;
   private List<ResourcePackListEntry> B;
   private PackSearch y;
   private File O;
   private GuiButton N;
   private boolean i;
   private boolean a;
   private Comparator<ResourcePackListEntry> b;
   private static final int G = new Color(7, 7, 7).getRGB();
   private static final int q = new Color(17, 17, 17).getRGB();
   private static final long K = 87295963114827L;

   public CustomResourcePacksScreen(GuiScreen guiScreen) {
      super(guiScreen);
      this.V = guiScreen;
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 100 - 75, this.height - 26, I18n.format("gui.done")));
      this.buttonList.add(new GuiOptionButton(2, this.width / 2 + 100 - 75, this.height - 48, I18n.format("resourcePack.openFolder")));
      this.buttonList.add(new GuiOptionButton(10, this.width / 2 - 204, this.height - 26, 40, 20, "A-Z"));
      this.buttonList.add(new GuiOptionButton(11, this.width / 2 - 204 + 44, this.height - 26, 40, 20, "Z-A"));
      this.buttonList.add(new GuiOptionButton(20, this.width / 2 - 74, this.height - 26, 70, 20, "Refresh"));
      String string = this.l == null ? "" : this.l.getText();
      this.l = new GuiTextField(30, this.fontRendererObj, this.width / 2 - 203, this.height - 46, 198, 16);
      this.l.setText(string);
      if (!this.a) {
         this.m = Lists.newArrayListWithCapacity(8);
         this.w = Lists.newArrayListWithCapacity(8);
         this.c = Lists.newArrayListWithCapacity(1);
         this.B = Lists.newArrayListWithCapacity(8);
         ResourcePackRepository resourcePackRepository = this.mc.getResourcePackRepository();
         resourcePackRepository.updateRepositoryEntriesAll();
         this.O = resourcePackRepository.getDirResourcepacks();
         this.m.addAll(this.createAvailablePackList(resourcePackRepository));

         for (ResourcePackRepository.Entry entry : Lists.reverse(resourcePackRepository.getRepositoryEntries())) {
            this.B.add(new ResourcePackListEntryFound(this, entry));
         }

         this.B.add(new ResourcePackListEntryDefault(this));
      }

      this.f = new GuiResourcePackAvailable(this.mc, 200, this.height, this.w);
      this.f.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
      this.f.U(7, 8);
      this.n = new GuiResourcePackSelected(this.mc, 200, this.height, this.B);
      this.n.setSlotXBoundsFromLeft(this.width / 2 + 4);
      this.n.U(7, 8);
      this.y = new PackSearch(this.m, this.w);
      this.y.E(this.b == null ? (this.b = PackSearch.b) : this.b);
      this.y.R(this.l.getText().trim());
   }

   @Override
   public void actionPerformed(GuiButton button) {
      if (button.id == 20) {
         this.b();
      } else if (button.id == 11) {
         this.y.E(this.b = PackSearch.A);
      } else if (button.id == 10) {
         this.y.E(this.b = PackSearch.b);
      } else if (button.id == 2) {
         X(this.mc.getResourcePackRepository().getDirResourcepacks());
      } else if (button.id == 1) {
         if (this.a) {
            List list = this.t();
            this.mc.gameSettings.resourcePacks.clear();

            for (Object object : list) {
               ResourcePackRepository.Entry entry = (ResourcePackRepository.Entry)object;
               this.mc.gameSettings.resourcePacks.add(entry.getResourcePackName());
            }

            this.mc.gameSettings.saveOptions();
            this.mc.refreshResources();
            System.gc();
         }

         this.mc.displayGuiScreen(this.V);
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         for (GuiButton guiButton : this.buttonList) {
            if (guiButton.mousePressed(this.mc, mouseX, mouseY)) {
               this.N = guiButton;
               guiButton.playPressSound(this.mc.getSoundHandler());
               this.actionPerformed(guiButton);
            }
         }
      }

      this.f.mouseClicked(mouseX, mouseY, mouseButton);
      this.n.mouseClicked(mouseX, mouseY, mouseButton);
      this.l.mouseClicked(mouseX, mouseY, mouseButton);
      this.y.f();
   }

   @Override
   public void handleMouseInput() throws IOException {
      try {
         super.handleMouseInput();
      } catch (NullPointerException nullPointerException) {
      }

      this.f.handleMouseInput();
      this.n.handleMouseInput();
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      if (state == 0 && this.N != null) {
         this.N.mouseReleased(mouseX, mouseY);
         this.N = null;
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);
      if (this.l.isFocused()) {
         this.l.textboxKeyTyped(typedChar, keyCode);
         this.y.R(this.l.getText().trim());
      }
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      System.out.println("call");
      this.m.clear();
      this.w.clear();
      this.c.clear();
      this.B.clear();
      System.gc();
   }

   @Override
   public void updateScreen() {
      this.l.updateCursorCounter();
      if (this.i) {
         this.i = false;
         this.t();
         this.b();
      }
   }

   public void S(File file) {
      this.O = file;
      this.t();
      this.b();
   }

   public void b() {
      this.m.clear();
      this.m.addAll(this.createAvailablePackList(this.mc.getResourcePackRepository()));
      this.y.f();
   }

   public List<ResourcePackRepository.Entry> t() {
      ArrayList arrayList = Lists.newArrayListWithCapacity(this.B.size());

      for (ResourcePackListEntry resourcePackListEntry : this.B) {
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

   public static void X(File file) {
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
      this.f.drawScreen(mouseX, mouseY, partialTicks);
      this.n.drawScreen(mouseX, mouseY, partialTicks);
      this.v(0, 32, 255, 255);
      this.v(this.height - 52, this.height, 255, 255);
      this.l.drawTextBox();

      for (GuiButton guiButton : this.buttonList) {
         guiButton.drawButton(this.mc, mouseX, mouseY);
      }
   }

   public void v(int i, int j, int k, int l) {
      this.drawGradientRect(0, i, this.width, j, G, q);
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
      if (!resourcePackRepository.getDirResourcepacks().equals(this.O)) {
         arrayList.add(new FolderPackEntry(this, this.O.getParentFile(), true));
      }

      File[] files = this.O.listFiles();
      if (files != null) {
         for (File file : files) {
            if (file.isDirectory() && !new File(file, "pack.mcmeta").isFile()) {
               arrayList.add(new FolderPackEntry(this, file));
            } else {
               ResourcePackRepository.Entry entry = FolderResourcePackRepository.b(resourcePackRepository, file);
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
      return this.B.contains(resourcePackListEntry);
   }

   @Override
   public List getListContaining(ResourcePackListEntry resourcePackListEntry) {
      return this.hasResourcePackEntry(resourcePackListEntry) ? this.B : this.m;
   }

   @Override
   public List<ResourcePackListEntry> getAvailableResourcePacks() {
      this.i = true;
      this.c.clear();
      return this.c;
   }

   @Override
   public List<ResourcePackListEntry> getSelectedResourcePacks() {
      this.i = true;
      return this.B;
   }

   @Override
   public void markChanged() {
      this.a = true;
   }
}
