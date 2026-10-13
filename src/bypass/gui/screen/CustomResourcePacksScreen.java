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

   public CustomResourcePacksScreen(GuiScreen var1) {
      super(var1);
      this.V = var1;
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 100 - 75, this.height - 26, I18n.format("gui.done")));
      this.buttonList.add(new GuiOptionButton(2, this.width / 2 + 100 - 75, this.height - 48, I18n.format("resourcePack.openFolder")));
      this.buttonList.add(new GuiOptionButton(10, this.width / 2 - 204, this.height - 26, 40, 20, "A-Z"));
      this.buttonList.add(new GuiOptionButton(11, this.width / 2 - 204 + 44, this.height - 26, 40, 20, "Z-A"));
      this.buttonList.add(new GuiOptionButton(20, this.width / 2 - 74, this.height - 26, 70, 20, "Refresh"));
      String var5 = this.l == null ? "" : this.l.getText();
      this.l = new GuiTextField(30, this.fontRendererObj, this.width / 2 - 203, this.height - 46, 198, 16);
      this.l.setText(var5);
      if (!this.a) {
         this.m = Lists.newArrayListWithCapacity(8);
         this.w = Lists.newArrayListWithCapacity(8);
         this.c = Lists.newArrayListWithCapacity(1);
         this.B = Lists.newArrayListWithCapacity(8);
         ResourcePackRepository var6 = this.mc.getResourcePackRepository();
         var6.updateRepositoryEntriesAll();
         this.O = var6.getDirResourcepacks();
         this.m.addAll(this.createAvailablePackList(var6));

         for (ResourcePackRepository.Entry var8 : Lists.reverse(var6.getRepositoryEntries())) {
            this.B.add(new ResourcePackListEntryFound(this, var8));
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
   public void actionPerformed(GuiButton var1) {
      if (var1.id == 20) {
         this.b();
      } else if (var1.id == 11) {
         this.y.E(this.b = PackSearch.A);
      } else if (var1.id == 10) {
         this.y.E(this.b = PackSearch.b);
      } else if (var1.id == 2) {
         X(this.mc.getResourcePackRepository().getDirResourcepacks());
      } else if (var1.id == 1) {
         if (this.a) {
            List var5 = this.t();
            this.mc.gameSettings.resourcePacks.clear();

            for (Object var7$raw : var5) {
               ResourcePackRepository.Entry var7 = (ResourcePackRepository.Entry)var7$raw;
               this.mc.gameSettings.resourcePacks.add(var7.getResourcePackName());
            }

            this.mc.gameSettings.saveOptions();
            this.mc.refreshResources();
            System.gc();
         }

         this.mc.displayGuiScreen(this.V);
      }
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) {
      if (var3 == 0) {
         for (GuiButton var8 : this.buttonList) {
            if (var8.mousePressed(this.mc, var1, var2)) {
               this.N = var8;
               var8.playPressSound(this.mc.getSoundHandler());
               this.actionPerformed(var8);
            }
         }
      }

      this.f.mouseClicked(var1, var2, var3);
      this.n.mouseClicked(var1, var2, var3);
      this.l.mouseClicked(var1, var2, var3);
      this.y.f();
   }

   @Override
   public void handleMouseInput() throws IOException {
      try {
         super.handleMouseInput();
      } catch (NullPointerException var2) {
      }

      this.f.handleMouseInput();
      this.n.handleMouseInput();
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3) {
      if (var3 == 0 && this.N != null) {
         this.N.mouseReleased(var1, var2);
         this.N = null;
      }
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      super.keyTyped(var1, var2);
      if (this.l.isFocused()) {
         this.l.textboxKeyTyped(var1, var2);
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

   public void S(File var1) {
      this.O = var1;
      this.t();
      this.b();
   }

   public void b() {
      this.m.clear();
      this.m.addAll(this.createAvailablePackList(this.mc.getResourcePackRepository()));
      this.y.f();
   }

   public List<ResourcePackRepository.Entry> t() {
      ArrayList var4 = Lists.newArrayListWithCapacity(this.B.size());

      for (ResourcePackListEntry var6 : this.B) {
         if (var6 instanceof ResourcePackListEntryFound) {
            ResourcePackListEntryFound var7 = (ResourcePackListEntryFound)var6;
            if (var7.func_148318_i() != null) {
               var4.add(var7.func_148318_i());
            }
         }
      }

      Collections.reverse(var4);
      this.mc.getResourcePackRepository().setRepositories(var4);
      return var4;
   }

   public static void X(File var0) {
      String var4 = var0.getAbsolutePath();
      if (Util.getOSType() == Util.EnumOS.OSX) {
         try {
            Runtime.getRuntime().exec(new String[]{"/usr/bin/open", var4});
            return;
         } catch (Exception var9) {
         }
      } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
         String var5 = String.format("cmd.exe /C start \"Open file\" \"%s\"", var4);

         try {
            Runtime.getRuntime().exec(var5);
            return;
         } catch (Exception var8) {
         }
      }

      try {
         Class var10 = Class.forName("java.awt.Desktop");
         Object var6 = var10.getMethod("getDesktop").invoke(null);
         var10.getMethod("browse", URI.class).invoke(var6, var0.toURI());
      } catch (Throwable var7) {
         Sys.openURL("file://" + var4);
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawBackground(0);
      this.f.drawScreen(var1, var2, var3);
      this.n.drawScreen(var1, var2, var3);
      this.v(0, 32, 255, 255);
      this.v(this.height - 52, this.height, 255, 255);
      this.l.drawTextBox();

      for (GuiButton var8 : this.buttonList) {
         var8.drawButton(this.mc, var1, var2);
      }
   }

   public void v(int var1, int var2, int var3, int var4) {
      this.drawGradientRect(0, var1, this.width, var2, G, q);
   }

   @Override
   public void drawGradientRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      float var10 = (var5 >> 24 & 0xFF) / 255.0F;
      float var11 = (var5 >> 16 & 0xFF) / 255.0F;
      float var12 = (var5 >> 8 & 0xFF) / 255.0F;
      float var13 = (var5 & 0xFF) / 255.0F;
      float var14 = (var6 >> 24 & 0xFF) / 255.0F;
      float var15 = (var6 >> 16 & 0xFF) / 255.0F;
      float var16 = (var6 >> 8 & 0xFF) / 255.0F;
      float var17 = (var6 & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator var18 = Tessellator.getInstance();
      WorldRenderer var19 = var18.getWorldRenderer();
      var19.begin(7, DefaultVertexFormats.POSITION_COLOR);
      var19.pos(var3, var2, 0.0).color(var11, var12, var13, var10).endVertex();
      var19.pos(var1, var2, 0.0).color(var11, var12, var13, var10).endVertex();
      var19.pos(var1, var4, 0.0).color(var15, var16, var17, var14).endVertex();
      var19.pos(var3, var4, 0.0).color(var15, var16, var17, var14).endVertex();
      var18.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   private List<ResourcePackListEntryFound> createAvailablePackList(ResourcePackRepository var1) {
      ArrayList var5 = Lists.newArrayList();
      if (!var1.getDirResourcepacks().equals(this.O)) {
         var5.add(new FolderPackEntry(this, this.O.getParentFile(), true));
      }

      File[] var6 = this.O.listFiles();
      if (var6 != null) {
         for (File var10 : var6) {
            if (var10.isDirectory() && !new File(var10, "pack.mcmeta").isFile()) {
               var5.add(new FolderPackEntry(this, var10));
            } else {
               ResourcePackRepository.Entry var11 = FolderResourcePackRepository.b(var1, var10);
               if (var11 != null) {
                  try {
                     var11.updateResourcePack();
                     var5.add(new ResourcePackListEntryFound(this, var11));
                  } catch (Exception var13) {
                  }
               }
            }
         }
      }

      List var14 = var1.getRepositoryEntries();
      var5.removeIf(var1x -> ((net.minecraft.client.resources.ResourcePackListEntryFound)var1x).func_148318_i() != null && var14.contains(((net.minecraft.client.resources.ResourcePackListEntryFound)var1x).func_148318_i()));
      return var5;
   }

   @Override
   public boolean hasResourcePackEntry(ResourcePackListEntry var1) {
      return this.B.contains(var1);
   }

   @Override
   public List getListContaining(ResourcePackListEntry var1) {
      return this.hasResourcePackEntry(var1) ? this.B : this.m;
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
