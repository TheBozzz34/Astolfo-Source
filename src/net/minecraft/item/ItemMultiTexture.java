package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;

public class ItemMultiTexture extends ItemBlock {
   public final Block theBlock;
   public final Function<ItemStack, String> nameFunction;

   public ItemMultiTexture(Block var1, Block var2, Function<ItemStack, String> var3) {
      super(var1);
      this.theBlock = var2;
      this.nameFunction = var3;
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public ItemMultiTexture(Block var1, Block var2, final String[] var3) {
      this(var1, var2, new Function<ItemStack, String>() {
         private static final long a = 2504113483611L;

         public String apply(ItemStack var1) {
            int var5 = var1.getMetadata();
            if (var5 < 0 || var5 >= var3.length) {
               var5 = 0;
            }

            return var3[var5];
         }
      });
   }

   @Override
   public int getMetadata(int var1) {
      return var1;
   }

   @Override
   public String getUnlocalizedName(ItemStack var1) {
      return super.getUnlocalizedName() + "." + this.nameFunction.apply(var1);
   }
}
