package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.EnumChatFormatting;

public abstract class GuiResourcePackList extends GuiListExtended {
   public final Minecraft mc;
   public final List<ResourcePackListEntry> field_148204_l;

   public GuiResourcePackList(Minecraft mcIn, int i, int j, List<ResourcePackListEntry> list) {
      super(mcIn, i, j, 32, j - 55 + 4, 36);
      this.mc = mcIn;
      this.field_148204_l = list;
      this.field_148163_i = false;
      this.setHasListHeader(true, (int)(mcIn.fontRendererObj.FONT_HEIGHT * 1.5F));
   }

   @Override
   public void drawListHeader(int i, int j, Tessellator tessellator) {
      String string = EnumChatFormatting.UNDERLINE + "" + EnumChatFormatting.BOLD + this.getListHeader();
      this.mc
         .fontRendererObj
         .drawStringNoShadow(string, i + this.width / 2 - this.mc.fontRendererObj.getStringWidth(string) / 2, Math.min(this.top + 3, j), 16777215);
   }

   public abstract String getListHeader();

   public List<ResourcePackListEntry> getList() {
      return this.field_148204_l;
   }

   @Override
   public int getSize() {
      return this.getList().size();
   }

   public ResourcePackListEntry getListEntry(int index) {
      return this.getList().get(index);
   }

   @Override
   public int getListWidth() {
      return this.width;
   }

   @Override
   public int getScrollBarX() {
      return this.right - 6;
   }
}
