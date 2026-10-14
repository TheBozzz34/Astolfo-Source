package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;

public class GuiResourcePackSelected extends GuiResourcePackList {
   private static final long c = 46177529381888L;

   public GuiResourcePackSelected(Minecraft mcIn, int i, int j, List<ResourcePackListEntry> list) {
      super(mcIn, i, j, list);
   }

   @Override
   public String getListHeader() {
      return I18n.format("resourcePack.selected.title");
   }
}
