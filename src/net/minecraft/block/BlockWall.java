package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWall extends Block {
   public static final PropertyBool UP = PropertyBool.create("up");
   public static final PropertyBool NORTH = PropertyBool.create("north");
   public static final PropertyBool EAST = PropertyBool.create("east");
   public static final PropertyBool SOUTH = PropertyBool.create("south");
   public static final PropertyBool WEST = PropertyBool.create("west");
   public static final PropertyEnum<BlockWall.EnumType> VARIANT = PropertyEnum.create("variant", BlockWall.EnumType.class);
   private static final long l = 36750201935449L;
   private static final long bb = -1589980554923606010L;

   public BlockWall(Block var1) {
      super(var1.blockMaterial);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(UP, false)
            .withProperty(NORTH, false)
            .withProperty(EAST, false)
            .withProperty(SOUTH, false)
            .withProperty(WEST, false)
            .withProperty(VARIANT, BlockWall.EnumType.NORMAL)
      );
      this.setHardness(var1.blockHardness);
      this.setResistance(var1.blockResistance / 3.0F);
      this.setStepSound(var1.stepSound);
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockWall.EnumType.NORMAL.getUnlocalizedName() + ".name");
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isPassable(IBlockAccess var1, BlockPos var2) {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      boolean var6 = this.canConnectTo(var1, var2.north());
      boolean var7 = this.canConnectTo(var1, var2.south());
      boolean var8 = this.canConnectTo(var1, var2.west());
      boolean var9 = this.canConnectTo(var1, var2.east());
      float var10 = 0.25F;
      float var11 = 0.75F;
      float var12 = 0.25F;
      float var13 = 0.75F;
      float var14 = 1.0F;
      if (var6) {
         var12 = 0.0F;
      }

      if (var7) {
         var13 = 1.0F;
      }

      if (var8) {
         var10 = 0.0F;
      }

      if (var9) {
         var11 = 1.0F;
      }

      if (var6 && var7 && !var8 && !var9) {
         var14 = 0.8125F;
         var10 = 0.3125F;
         var11 = 0.6875F;
      } else if (!var6 && !var7 && var8 && var9) {
         var14 = 0.8125F;
         var12 = 0.3125F;
         var13 = 0.6875F;
      }

      this.setBlockBounds(var10, 0.0F, var12, var11, var14, var13);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World var1, BlockPos var2, IBlockState var3) {
      this.setBlockBoundsBasedOnState(var1, var2);
      this.maxY = 1.5;
      return super.getCollisionBoundingBox(var1, var2, var3);
   }

   public boolean canConnectTo(IBlockAccess var1, BlockPos var2) {
      Block var6 = var1.getBlockState(var2).getBlock();
      return var6 == Blocks.barrier
         ? false
         : (
            var6 != this && !(var6 instanceof BlockFenceGate)
               ? (var6.blockMaterial.isOpaque() && var6.isFullCube() ? var6.blockMaterial != Material.gourd : false)
               : true
         );
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for (BlockWall.EnumType var10 : BlockWall.EnumType.values()) {
         var3.add(new ItemStack(var1, 1, var10.getMetadata()));
      }
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var3 == EnumFacing.DOWN ? super.shouldSideBeRendered(var1, var2, var3) : true;
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(VARIANT, BlockWall.EnumType.byMetadata(var1));
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      return var1.getValue(VARIANT).getMetadata();
   }

   @Override
   public IBlockState getActualState(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.withProperty(UP, !var2.isAirBlock(var3.up()))
         .withProperty(NORTH, this.canConnectTo(var2, var3.north()))
         .withProperty(EAST, this.canConnectTo(var2, var3.east()))
         .withProperty(SOUTH, this.canConnectTo(var2, var3.south()))
         .withProperty(WEST, this.canConnectTo(var2, var3.west()));
   }

   @Override
   public BlockState createBlockState() {
      IProperty[] var10003 = new IProperty[(int)bb];
      var10003[0] = UP;
      var10003[1] = NORTH;
      var10003[2] = EAST;
      var10003[3] = WEST;
      var10003[4] = SOUTH;
      var10003[5] = VARIANT;
      return new BlockState(this, var10003);
   }

   public enum EnumType implements IStringSerializable {
      NORMAL(0, "cobblestone", "normal"),
      MOSSY(1, "mossy_cobblestone", "mossy");

      private static final BlockWall.EnumType[] META_LOOKUP = new BlockWall.EnumType[values().length];
      private final int meta;
      private final String name;
      private String unlocalizedName;
      private static final long a = 137487768257945L;

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

      public static BlockWall.EnumType byMetadata(int var0) {
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

      static {
         for (BlockWall.EnumType var14 : values()) {
            META_LOOKUP[var14.getMetadata()] = var14;
         }
      }
   }
}
