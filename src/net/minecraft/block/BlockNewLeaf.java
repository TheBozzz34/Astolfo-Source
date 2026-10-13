package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockNewLeaf extends BlockLeaves {
   public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.create(
      "variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>() {
         private static final long a = 29744617169832L;

         public boolean apply(BlockPlanks.EnumType var1) {
            return var1.getMetadata() >= 4;
         }
      }
   );
   private static final long e = 108993192577492L;

   public BlockNewLeaf() {
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.ACACIA).withProperty(CHECK_DECAY, true).withProperty(DECAYABLE, true)
      );
   }

   @Override
   public void dropApple(World var1, BlockPos var2, IBlockState var3, int var4) {
      if (var3.getValue(VARIANT) == BlockPlanks.EnumType.DARK_OAK && var1.rand.nextInt(var4) == 0) {
         spawnAsEntity(var1, var2, new ItemStack(Items.apple, 1, 0));
      }
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   @Override
   public int getDamageValue(World var1, BlockPos var2) {
      IBlockState var3 = var1.getBlockState(var2);
      return var3.getBlock().getMetaFromState(var3) & 3;
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
   }

   @Override
   public ItemStack createStackedBlock(IBlockState var1) {
      return new ItemStack(Item.getItemFromBlock(this), 1, var1.getValue(VARIANT).getMetadata() - 4);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState()
         .withProperty(VARIANT, this.getWoodType(var1))
         .withProperty(DECAYABLE, (var1 & 4) == 0)
         .withProperty(CHECK_DECAY, (var1 & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(VARIANT).getMetadata() - 4;
      if (!var1.getValue(DECAYABLE)) {
         var5 |= 4;
      }

      if (var1.getValue(CHECK_DECAY)) {
         var5 |= 8;
      }

      return var5;
   }

   @Override
   public BlockPlanks.EnumType getWoodType(int var1) {
      return BlockPlanks.EnumType.byMetadata((var1 & 3) + 4);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, VARIANT, CHECK_DECAY, DECAYABLE);
   }

   @Override
   public void harvestBlock(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      if (!var1.isRemote && var2.getCurrentEquippedItem() != null && var2.getCurrentEquippedItem().getItem() == Items.shears) {
         var2.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
         spawnAsEntity(var1, var3, new ItemStack(Item.getItemFromBlock(this), 1, var4.getValue(VARIANT).getMetadata() - 4));
      } else {
         super.harvestBlock(var1, var2, var3, var4, var5);
      }
   }
}
