package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemColored extends ItemBlock {
   private final Block coloredBlock;
   private String[] subtypeNames;
   private static final long a = 11959513315217L;

   public ItemColored(Block var1, boolean var2) {
      super(var1);
      this.coloredBlock = var1;
      if (var2) {
         this.setMaxDamage(0);
         this.setHasSubtypes(true);
      }
   }

   @Override
   public int getColorFromItemStack(ItemStack var1, int var2) {
      return this.coloredBlock.getRenderColor(this.coloredBlock.getStateFromMeta(var1.getMetadata()));
   }

   @Override
   public int getMetadata(int var1) {
      return var1;
   }

   public ItemColored setSubtypeNames(String[] var1) {
      this.subtypeNames = var1;
      return this;
   }

   @Override
   public String getUnlocalizedName(ItemStack var1) {
      if (this.subtypeNames == null) {
         return super.getUnlocalizedName(var1);
      }

      int var5 = var1.getMetadata();
      return var5 >= 0 && var5 < this.subtypeNames.length ? super.getUnlocalizedName(var1) + "." + this.subtypeNames[var5] : super.getUnlocalizedName(var1);
   }
}
