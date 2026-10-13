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
   private static final long a = 64260344234225L;

   public FolderPackEntry(CustomResourcePacksScreen var1, File var2) {
      super(var1);
      this.T = var1;
      this.w = var2;
      this.B = var2.getName();
      this.Y = false;
   }

   public FolderPackEntry(CustomResourcePacksScreen var1, File var2, boolean var3) {
      super(var1);
      this.T = var1;
      this.w = var2;
      this.B = "..";
      this.Y = var3;
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
   public boolean mousePressed(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.T.S(this.w);
      return true;
   }

   @Override
   public void drawEntry(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      this.E(this, var2, var3, var8);
   }

   public void E(FolderPackEntry var1, int var2, int var3, boolean var4) {
      var1.func_148313_c();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      Gui.drawModalRectWithCustomSizedTexture(var2, var3, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      GlStateManager.disableBlend();
      if ((this.mc.gameSettings.touchscreen || var4) && var1.func_148310_d()) {
         Gui.drawRect(var2, var3, var2 + 32, var3 + 32, -1601138544);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }

      String var9 = var1.func_148312_b();
      int var8 = this.mc.fontRendererObj.getStringWidth(var9);
      if (var8 > 157) {
         var9 = this.mc.fontRendererObj.trimStringToWidth(var9, 157 - this.mc.fontRendererObj.getStringWidth("...")) + "...";
      }

      this.mc.fontRendererObj.drawStringWithShadow(var9, var2 + 32 + 2, var3 + 1, 16777215);
      List var10 = this.mc.fontRendererObj.listFormattedStringToWidth(var1.func_148311_a(), 157);

      for (int var11 = 0; var11 < 2 && var11 < var10.size(); var11++) {
         this.mc.fontRendererObj.drawStringWithShadow((String)var10.get(var11), var2 + 32 + 2, var3 + 12 + 10 * var11, 8421504);
      }
   }
}
