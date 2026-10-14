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

   public ResourcePackListEntry(GuiScreenResourcePacks resourcePacksGUIIn) {
      this.resourcePacksGUI = resourcePacksGUIIn;
      this.mc = Minecraft.getMinecraft();
   }

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      int i = this.func_183019_a();
      if (i != 1) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         Gui.drawRect(x - 1, y - 1, x + listWidth - 9, y + slotHeight + 1, -8978432);
      }

      this.func_148313_c();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      String string = this.func_148312_b();
      String string2 = this.func_148311_a();
      if ((this.mc.gameSettings.touchscreen || isSelected) && this.func_148310_d()) {
         this.mc.getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
         Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int j = mouseX - x;
         int k = mouseY - y;
         if (i < 1) {
            string = field_183020_d.getFormattedText();
            string2 = field_183021_e.getFormattedText();
         } else if (i > 1) {
            string = field_183020_d.getFormattedText();
            string2 = field_183022_f.getFormattedText();
         }

         if (this.func_148309_e()) {
            if (j < 32) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         } else {
            if (this.func_148308_f()) {
               if (j < 16) {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_148314_g()) {
               if (j < 32 && j > 16 && k < 16) {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }

            if (this.func_148307_h()) {
               if (j < 32 && j > 16 && k > 16) {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
               } else {
                  Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
               }
            }
         }
      }

      int l = this.mc.fontRendererObj.getStringWidth(string);
      if (l > 157) {
         string = this.mc.fontRendererObj.trimStringToWidth(string, 157 - this.mc.fontRendererObj.getStringWidth("...")) + "...";
      }

      this.mc.fontRendererObj.drawStringWithShadow(string, x + 32 + 2, y + 1, 16777215);
      List list = this.mc.fontRendererObj.listFormattedStringToWidth(string2, 157);

      for (int m = 0; m < 2 && m < list.size(); m++) {
         this.mc.fontRendererObj.drawStringWithShadow((String)list.get(m), x + 32 + 2, y + 12 + 10 * m, 8421504);
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
      List list = this.resourcePacksGUI.getListContaining(this);
      int i = list.indexOf(this);
      return i > 0 && ((ResourcePackListEntry)list.get(i - 1)).func_148310_d();
   }

   public boolean func_148307_h() {
      List list = this.resourcePacksGUI.getListContaining(this);
      int i = list.indexOf(this);
      return i >= 0 && i < list.size() - 1 && ((ResourcePackListEntry)list.get(i + 1)).func_148310_d();
   }

   @Override
   public boolean mousePressed(int slotIndex, int i, int j, int k, int l, int m) {
      if (this.func_148310_d() && l <= 32) {
         if (this.func_148309_e()) {
            this.resourcePacksGUI.markChanged();
            int n = this.func_183019_a();
            if (n != 1) {
               String string = I18n.format("resourcePack.incompatible.confirm.title");
               String string2 = I18n.format("resourcePack.incompatible.confirm." + (n > 1 ? "new" : "old"));
               this.mc.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
                  @Override
                  public void confirmClicked(boolean result, int id) {
                     List list = ResourcePackListEntry.this.resourcePacksGUI.getListContaining(ResourcePackListEntry.this);
                     ResourcePackListEntry.this.mc.displayGuiScreen(ResourcePackListEntry.this.resourcePacksGUI);
                     if (result) {
                        list.remove(ResourcePackListEntry.this);
                        ResourcePackListEntry.this.resourcePacksGUI.getSelectedResourcePacks().add(0, ResourcePackListEntry.this);
                     }
                  }
               }, string, string2, 0));
            } else {
               this.resourcePacksGUI.getListContaining(this).remove(this);
               this.resourcePacksGUI.getSelectedResourcePacks().add(0, this);
            }

            return true;
         }

         if (l < 16 && this.func_148308_f()) {
            this.resourcePacksGUI.getListContaining(this).remove(this);
            this.resourcePacksGUI.getAvailableResourcePacks().add(0, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }

         if (l > 16 && m < 16 && this.func_148314_g()) {
            List list2 = this.resourcePacksGUI.getListContaining(this);
            int i2 = list2.indexOf(this);
            list2.remove(this);
            list2.add(i2 - 1, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }

         if (l > 16 && m > 16 && this.func_148307_h()) {
            List list3 = this.resourcePacksGUI.getListContaining(this);
            int i3 = list3.indexOf(this);
            list3.remove(this);
            list3.add(i3 + 1, this);
            this.resourcePacksGUI.markChanged();
            return true;
         }
      }

      return false;
   }

   @Override
   public void setSelected(int i, int j, int k) {
   }

   @Override
   public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
   }
}
