package net.minecraft.client.resources.data;

import net.minecraft.util.IChatComponent;

public class PackMetadataSection implements IMetadataSection {
   private final IChatComponent packDescription;
   private final int packFormat;

   public PackMetadataSection(IChatComponent iChatComponent, int i) {
      this.packDescription = iChatComponent;
      this.packFormat = i;
   }

   public IChatComponent getPackDescription() {
      return this.packDescription;
   }

   public int getPackFormat() {
      return this.packFormat;
   }
}
