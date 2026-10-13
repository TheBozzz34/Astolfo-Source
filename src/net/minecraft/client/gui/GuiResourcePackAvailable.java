package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;

public class GuiResourcePackAvailable extends GuiResourcePackList {
   private static final long c = 21114068759901L;

   public GuiResourcePackAvailable(Minecraft var1, int var2, int var3, List<ResourcePackListEntry> var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public String getListHeader() {
      return I18n.format("resourcePack.available.title");
   }
}
