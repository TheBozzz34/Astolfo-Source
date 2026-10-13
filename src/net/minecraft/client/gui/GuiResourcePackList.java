package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.EnumChatFormatting;

public abstract class GuiResourcePackList extends GuiListExtended {
   public final Minecraft mc;
   public final List<ResourcePackListEntry> field_148204_l;
   private static final long d = 42856277547411L;

   public GuiResourcePackList(Minecraft var1, int var2, int var3, List<ResourcePackListEntry> var4) {
      super(var1, var2, var3, 32, var3 - 55 + 4, 36);
      this.mc = var1;
      this.field_148204_l = var4;
      this.field_148163_i = false;
      this.setHasListHeader(true, (int)(var1.fontRendererObj.FONT_HEIGHT * 1.5F));
   }

   @Override
   public void drawListHeader(int var1, int var2, Tessellator var3) {
      String var6 = EnumChatFormatting.UNDERLINE + "" + EnumChatFormatting.BOLD + this.getListHeader();
      this.mc.fontRendererObj.D(var6, var1 + this.width / 2 - this.mc.fontRendererObj.getStringWidth(var6) / 2, Math.min(this.top + 3, var2), 16777215);
   }

   public abstract String getListHeader();

   public List<ResourcePackListEntry> getList() {
      return this.field_148204_l;
   }

   @Override
   public int getSize() {
      return this.getList().size();
   }

   public ResourcePackListEntry getListEntry(int var1) {
      return this.getList().get(var1);
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
