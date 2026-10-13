package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.world.ColorizerGrass;

public class ItemDoublePlant extends ItemMultiTexture {
   private static final long a = 82016496683558L;

   public ItemDoublePlant(Block var1, Block var2, Function<ItemStack, String> var3) {
      super(var1, var2, var3);
   }

   @Override
   public int getColorFromItemStack(ItemStack var1, int var2) {
      BlockDoublePlant.EnumPlantType var6 = BlockDoublePlant.EnumPlantType.byMetadata(var1.getMetadata());
      return var6 != BlockDoublePlant.EnumPlantType.GRASS && var6 != BlockDoublePlant.EnumPlantType.FERN
         ? super.getColorFromItemStack(var1, var2)
         : ColorizerGrass.getGrassColor(0.5, 1.0);
   }
}
