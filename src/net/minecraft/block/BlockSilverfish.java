package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockSilverfish extends Block {
   public static final PropertyEnum<BlockSilverfish.EnumType> VARIANT = PropertyEnum.create("variant", BlockSilverfish.EnumType.class);
   private static final long e = 32627866232432L;

   public BlockSilverfish() {
      super(Material.clay);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockSilverfish.EnumType.STONE));
      this.setHardness(0.0F);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public int quantityDropped(Random var1) {
      return 0;
   }

   public static boolean canContainSilverfish(IBlockState var0) {
      Block var4 = var0.getBlock();
      return var0 == Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE)
         || var4 == Blocks.cobblestone
         || var4 == Blocks.stonebrick;
   }

   @Override
   public ItemStack createStackedBlock(IBlockState var1) {
      switch ((BlockSilverfish.EnumType)var1.getValue(VARIANT)) {
         case COBBLESTONE:
            return new ItemStack(Blocks.cobblestone);
         case STONEBRICK:
            return new ItemStack(Blocks.stonebrick);
         case MOSSY_STONEBRICK:
            return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.MOSSY.getMetadata());
         case CRACKED_STONEBRICK:
            return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.CRACKED.getMetadata());
         case CHISELED_STONEBRICK:
            return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.CHISELED.getMetadata());
         default:
            return new ItemStack(Blocks.stone);
      }
   }

   @Override
   public void dropBlockAsItemWithChance(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (!var1.isRemote && var1.getGameRules().getBoolean("doTileDrops")) {
         EntitySilverfish var9 = new EntitySilverfish(var1);
         var9.setLocationAndAngles(var2.getX() + 0.5, var2.getY(), var2.getZ() + 0.5, 0.0F, 0.0F);
         var1.spawnEntityInWorld(var9);
         var9.spawnExplosionParticle();
      }
   }

   @Override
   public int getDamageValue(World var1, BlockPos var2) {
      IBlockState var3 = var1.getBlockState(var2);
      return var3.getBlock().getMetaFromState(var3);
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (BlockSilverfish.EnumType var10 : BlockSilverfish.EnumType.values()) {
         var3.add(new ItemStack(var1, 1, var10.getMetadata()));
      }
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(VARIANT, BlockSilverfish.EnumType.byMetadata(var1));
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, VARIANT);
   }

   public enum EnumType implements IStringSerializable {
      STONE(0, "stone") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE);
         }
      },
      COBBLESTONE(1, "cobblestone", "cobble") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.cobblestone.getDefaultState();
         }
      },
      STONEBRICK(2, "stone_brick", "brick") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT);
         }
      },
      MOSSY_STONEBRICK(3, "mossy_brick", "mossybrick") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
         }
      },
      CRACKED_STONEBRICK(4, "cracked_brick", "crackedbrick") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
         }
      },
      CHISELED_STONEBRICK(5, "chiseled_brick", "chiseledbrick") {
         @Override
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
         }
      };

      private static final BlockSilverfish.EnumType[] META_LOOKUP = new BlockSilverfish.EnumType[values().length];
      private final int meta;
      private final String name;
      private final String unlocalizedName;
      private static final long a = 137729343979879L;

      EnumType(int var3, String var4) {
         this(var3, var4, var4);
      }

      EnumType(int var3, String var4, String var5) {
         this.meta = var3;
         this.name = var4;
         this.unlocalizedName = var5;
      }

      public int getMetadata() {
         return this.meta;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockSilverfish.EnumType byMetadata(int var0) {
         if (var0 < 0 || var0 >= META_LOOKUP.length) {
            var0 = 0;
         }

         return META_LOOKUP[var0];
      }

      @Override
      public String getName() {
         return this.name;
      }

      public String getUnlocalizedName() {
         return this.unlocalizedName;
      }

      public abstract IBlockState getModelBlock();

      public static BlockSilverfish.EnumType forModelBlock(IBlockState var0) {
         for (BlockSilverfish.EnumType var7 : values()) {
            if (var0 == var7.getModelBlock()) {
               return var7;
            }
         }

         return STONE;
      }

      static {
         for (BlockSilverfish.EnumType var20 : values()) {
            META_LOOKUP[var20.getMetadata()] = var20;
         }
      }
   }
}
