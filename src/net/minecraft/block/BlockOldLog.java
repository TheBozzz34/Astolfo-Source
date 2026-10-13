package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockOldLog extends BlockLog {
   public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create(
      "variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>() {
         private static final long a = 125603524136036L;

         public boolean apply(BlockPlanks.EnumType var1) {
            return var1.getMetadata() < 4;
         }
      }
   );
   private static final long l = 123791397855047L;

   public BlockOldLog() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
   }

   @Override
   public MapColor getMapColor(IBlockState var1) {
      BlockPlanks.EnumType var5 = var1.getValue(VARIANT);
      switch ((BlockLog.EnumAxis)var1.getValue(LOG_AXIS)) {
         case X:
         case Z:
         case NONE:
         default:
            switch (var5) {
               case OAK:
               default:
                  return BlockPlanks.EnumType.SPRUCE.getMapColor();
               case SPRUCE:
                  return BlockPlanks.EnumType.DARK_OAK.getMapColor();
               case BIRCH:
                  return MapColor.quartzColor;
               case JUNGLE:
                  return BlockPlanks.EnumType.SPRUCE.getMapColor();
            }
         case Y:
            return var5.getMapColor();
      }
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, BlockPlanks.EnumType.OAK.getMetadata()));
      var3.add(new ItemStack(var1, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()));
      var3.add(new ItemStack(var1, 1, BlockPlanks.EnumType.BIRCH.getMetadata()));
      var3.add(new ItemStack(var1, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()));
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      IBlockState var5 = this.getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata((var1 & 3) % 4));
      switch (var1 & 12) {
         case 0:
            var5 = var5.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
            break;
         case 4:
            var5 = var5.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
            break;
         case 8:
            var5 = var5.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
            break;
         default:
            var5 = var5.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
      }

      return var5;
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(VARIANT).getMetadata();
      switch ((BlockLog.EnumAxis)var1.getValue(LOG_AXIS)) {
         case X:
            var5 |= 4;
            break;
         case Z:
            var5 |= 8;
            break;
         case NONE:
            var5 |= 12;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, VARIANT, LOG_AXIS);
   }

   @Override
   public ItemStack createStackedBlock(IBlockState var1) {
      return new ItemStack(Item.getItemFromBlock(this), 1, var1.getValue(VARIANT).getMetadata());
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }
}
