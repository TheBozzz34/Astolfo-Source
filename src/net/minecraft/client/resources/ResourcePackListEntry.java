package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public abstract class ResourcePackListEntry implements GuiListExtended.IGuiListEntry {
   private static final ResourceLocation RESOURCE_PACKS_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");
   private static final IChatComponent field_183020_d = new ChatComponentTranslation("resourcePack.incompatible");
   private static final IChatComponent field_183021_e = new ChatComponentTranslation("resourcePack.incompatible.old");
   private static final IChatComponent field_183022_f = new ChatComponentTranslation("resourcePack.incompatible.new");
   public final Minecraft mc;
   public final GuiScreenResourcePacks resourcePacksGUI;
   private static final long b = 78267819550543L;

   public ResourcePackListEntry(GuiScreenResourcePacks var1) {
      this.resourcePacksGUI = var1;
      this.mc = Minecraft.getMinecraft();
   }

   @Override
   public void drawEntry(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      int var13 = this.func_183019_a();
      if (var13 != 1) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         Gui.drawRect(var2 - 1, var3 - 1, var2 + var4 - 9, var3 + var5 + 1, -8978432);
      }

      this.func_148313_c();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(var2, var3, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      String var14 = this.func_148312_b();
      String var15 = this.func_148311_a();
      if ((this.mc.gameSettings.touchscreen || var8) && this.func_148310_d()) {
         this.mc.getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
         Gui.drawRect(var2, var3, var2 + 32, var3 + 32, -1601138544);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int var16 = var6 - var2;
         int var17 = var7 - var3;
         if (var13 < 1) {
            var14 = field_183020_d.getFormattedText();
            var15 = field_183021_e.getFormattedText();
         } else if (var13 > 1) {
            var14 = field_183020_d.getFormattedText();
            var15 = field_183022_f.getFormattedText();
         }

         if (this.func_148309_e()) {
            if (var16 < 32) {
               Gui.drawModalRectWithCustomSizedTexture(var2, var3, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(var2, var3, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         } else {
            if (this.func_148308_f()) {
               if (var16 < 16) {
                  Gui.drawModalRectWithCustomSizedTexture(var2, var3, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(var2, var3, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_148314_g()) {
               if (var16 < 32 && var16 > 16 && var17 < 16) {
                  Gui.drawModalRectWithCustomSizedTexture(var2, var3, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(var2, var3, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_148307_h()) {
               if (var16 < 32 && var16 > 16 && var17 > 16) {
                  Gui.drawModalRectWithCustomSizedTexture(var2, var3, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(var2, var3, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }
         }
      }

      int var19 = this.mc.fontRendererObj.getStringWidth(var14);
      if (var19 > 157) {
         var14 = this.mc.fontRendererObj.trimStringToWidth(var14, 157 - this.mc.fontRendererObj.getStringWidth("...")) + "...";
      }

      this.mc.fontRendererObj.drawStringWithShadow(var14, var2 + 32 + 2, var3 + 1, 16777215);
      List var20 = this.mc.fontRendererObj.listFormattedStringToWidth(var15, 157);

      for (int var18 = 0; var18 < 2 && var18 < var20.size(); var18++) {
         this.mc.fontRendererObj.drawStringWithShadow((String)var20.get(var18), var2 + 32 + 2, var3 + 12 + 10 * var18, 8421504);
      }
   }

   public abstract int func_183019_a();

   public abstract String func_148311_a();

   public abstract String func_148312_b();

   public abstract void func_148313_c();

   public boolean func_148310_d() {
      return true;
   }

   public boolean func_148309_e() {
      return !this.resourcePacksGUI.hasResourcePackEntry(this);
   }

   public boolean func_148308_f() {
      return this.resourcePacksGUI.hasResourcePackEntry(this);
   }

   public boolean func_148314_g() {
      List var4 = this.resourcePacksGUI.getListContaining(this);
      int var5 = var4.indexOf(this);
      return var5 > 0 && ((ResourcePackListEntry)var4.get(var5 - 1)).func_148310_d();
   }

   public boolean func_148307_h() {
      List var4 = this.resourcePacksGUI.getListContaining(this);
      int var5 = var4.indexOf(this);
      return var5 >= 0 && var5 < var4.size() - 1 && ((ResourcePackListEntry)var4.get(var5 + 1)).func_148310_d();
   }

   @Override
   public boolean mousePressed(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.func_148310_d() && var5 <= 32) {
         if (this.func_148309_e()) {
            this.resourcePacksGUI.markChanged();
            int var14 = this.func_183019_a();
            if (var14 != 1) {
               String var16 = I18n.format("resourcePack.incompatible.confirm.title");
               String var12 = I18n.format("resourcePack.incompatible.confirm." + (var14 > 1 ? "new" : "old"));
               this.mc.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
                  private static final long a = 97880945572452L;

                  @Override
                  public void confirmClicked(boolean var1, int var2x) {
                     List var6x = ResourcePackListEntry.this.resourcePacksGUI.getListContaining(ResourcePackListEntry.this);
                     ResourcePackListEntry.this.mc.displayGuiScreen(ResourcePackListEntry.this.resourcePacksGUI);
                     if (var1) {
                        var6x.remove(ResourcePackListEntry.this);
                        ResourcePackListEntry.this.resourcePacksGUI.getSelectedResourcePacks().add(0, ResourcePackListEntry.this);
                     }
                  }
               }, var16, var12, 0));
            } else {
               this.resourcePacksGUI.getListContaining(this).remove(this);
               this.resourcePacksGUI.getSelectedResourcePacks().add(0, this);
            }

            return true;
         }

         if (var5 < 16 && this.func_148308_f()) {
            this.resourcePacksGUI.getListContaining(this).remove(this);
            this.resourcePacksGUI.getAvailableResourcePacks().add(0, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }

         if (var5 > 16 && var6 < 16 && this.func_148314_g()) {
            List var13 = this.resourcePacksGUI.getListContaining(this);
            int var15 = var13.indexOf(this);
            var13.remove(this);
            var13.add(var15 - 1, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }

         if (var5 > 16 && var6 > 16 && this.func_148307_h()) {
            List var10 = this.resourcePacksGUI.getListContaining(this);
            int var11 = var10.indexOf(this);
            var10.remove(this);
            var10.add(var11 + 1, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }
      }

      return false;
   }

   @Override
   public void setSelected(int var1, int var2, int var3) {
   }

   @Override
   public void mouseReleased(int var1, int var2, int var3, int var4, int var5, int var6) {
   }
}
