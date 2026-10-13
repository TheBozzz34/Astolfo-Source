package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   public static final PropertyEnum<BlockRedstoneComparator.Mode> MODE = PropertyEnum.create("mode", BlockRedstoneComparator.Mode.class);
   private static final long e = 87014277362740L;

   public BlockRedstoneComparator(boolean var1) {
      super(var1);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(POWERED, false)
            .withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE)
      );
      this.isBlockContainer = true;
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal("item.comparator.name");
   }

   @Override
   public Item getItemDropped(IBlockState var1, Random var2, int var3) {
      return Items.comparator;
   }

   @Override
   public Item getItem(World var1, BlockPos var2) {
      return Items.comparator;
   }

   @Override
   public int getDelay(IBlockState var1) {
      return 2;
   }

   @Override
   public IBlockState getPoweredState(IBlockState var1) {
      Boolean var2 = var1.getValue(POWERED);
      BlockRedstoneComparator.Mode var3 = var1.getValue(MODE);
      EnumFacing var4 = var1.getValue(FACING);
      return Blocks.powered_comparator.getDefaultState().withProperty(FACING, var4).withProperty(POWERED, var2).withProperty(MODE, var3);
   }

   @Override
   public IBlockState getUnpoweredState(IBlockState var1) {
      Boolean var2 = var1.getValue(POWERED);
      BlockRedstoneComparator.Mode var3 = var1.getValue(MODE);
      EnumFacing var4 = var1.getValue(FACING);
      return Blocks.unpowered_comparator.getDefaultState().withProperty(FACING, var4).withProperty(POWERED, var2).withProperty(MODE, var3);
   }

   @Override
   public boolean isPowered(IBlockState var1) {
      return this.isRepeaterPowered || var1.getValue(POWERED);
   }

   @Override
   public int getActiveSignal(IBlockAccess var1, BlockPos var2, IBlockState var3) {
      TileEntity var7 = var1.getTileEntity(var2);
      return var7 instanceof TileEntityComparator ? ((TileEntityComparator)var7).getOutputSignal() : 0;
   }

   private int calculateOutput(World var1, BlockPos var2, IBlockState var3) {
      return var3.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT
         ? Math.max(this.calculateInputStrength(var1, var2, var3) - this.getPowerOnSides(var1, var2, var3), 0)
         : this.calculateInputStrength(var1, var2, var3);
   }

   @Override
   public boolean shouldBePowered(World var1, BlockPos var2, IBlockState var3) {
      int var7 = this.calculateInputStrength(var1, var2, var3);
      if (var7 >= 15) {
         return true;
      }

      if (var7 == 0) {
         return false;
      }

      int var8 = this.getPowerOnSides(var1, var2, var3);
      return var8 == 0 ? true : var7 >= var8;
   }

   @Override
   public int calculateInputStrength(World var1, BlockPos var2, IBlockState var3) {
      int var7 = super.calculateInputStrength(var1, var2, var3);
      EnumFacing var8 = var3.getValue(FACING);
      BlockPos var9 = var2.offset(var8);
      Block var10 = var1.getBlockState(var9).getBlock();
      if (var10.hasComparatorInputOverride()) {
         var7 = var10.getComparatorInputOverride(var1, var9);
      } else if (var7 < 15 && var10.isNormalCube()) {
         var9 = var9.offset(var8);
         var10 = var1.getBlockState(var9).getBlock();
         if (var10.hasComparatorInputOverride()) {
            var7 = var10.getComparatorInputOverride(var1, var9);
         } else if (var10.getMaterial() == Material.air) {
            EntityItemFrame var11 = this.findItemFrame(var1, var8, var9);
            if (var11 != null) {
               var7 = var11.func_174866_q();
            }
         }
      }

      return var7;
   }

   private EntityItemFrame findItemFrame(World var1, final EnumFacing var2, BlockPos var3) {
      List var7 = var1.getEntitiesWithinAABB(
         EntityItemFrame.class,
         new AxisAlignedBB(var3.getX(), var3.getY(), var3.getZ(), var3.getX() + 1, var3.getY() + 1, var3.getZ() + 1),
         new Predicate<Entity>() {
            private static final long a = 69076170298314L;

            public boolean apply(Entity var1) {
               return var1 != null && var1.getHorizontalFacing() == var2;
            }
         }
      );
      return var7.size() == 1 ? (EntityItemFrame)var7.get(0) : null;
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var4.capabilities.allowEdit) {
         return false;
      }

      var3 = var3.cycleProperty(MODE);
      var1.playSoundEffect(
         var2.getX() + 0.5,
         var2.getY() + 0.5,
         var2.getZ() + 0.5,
         "random.click",
         0.3F,
         var3.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT ? 0.55F : 0.5F
      );
      var1.setBlockState(var2, var3, 2);
      this.onStateChange(var1, var2, var3);
      return true;
   }

   @Override
   public void updateState(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.isBlockTickPending(var2, this)) {
         int var7 = this.calculateOutput(var1, var2, var3);
         TileEntity var8 = var1.getTileEntity(var2);
         int var9 = var8 instanceof TileEntityComparator ? ((TileEntityComparator)var8).getOutputSignal() : 0;
         if (var7 != var9 || this.isPowered(var3) != this.shouldBePowered(var1, var2, var3)) {
            if (this.isFacingTowardsRepeater(var1, var2, var3)) {
               var1.updateBlockTick(var2, this, 2, -1);
            } else {
               var1.updateBlockTick(var2, this, 2, 0);
            }
         }
      }
   }

   private void onStateChange(World var1, BlockPos var2, IBlockState var3) {
      int var7 = this.calculateOutput(var1, var2, var3);
      TileEntity var8 = var1.getTileEntity(var2);
      int var9 = 0;
      if (var8 instanceof TileEntityComparator) {
         TileEntityComparator var10 = (TileEntityComparator)var8;
         var9 = var10.getOutputSignal();
         var10.setOutputSignal(var7);
      }

      if (var9 != var7 || var3.getValue(MODE) == BlockRedstoneComparator.Mode.COMPARE) {
         boolean var12 = this.shouldBePowered(var1, var2, var3);
         boolean var11 = this.isPowered(var3);
         if (var11 && !var12) {
            var1.setBlockState(var2, var3.withProperty(POWERED, false), 2);
         } else if (!var11 && var12) {
            var1.setBlockState(var2, var3.withProperty(POWERED, true), 2);
         }

         this.n(var1, var2, var3);
      }
   }

   @Override
   public void updateTick(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this.isRepeaterPowered) {
         var1.setBlockState(var2, this.getUnpoweredState(var3).withProperty(POWERED, true), 4);
      }

      this.onStateChange(var1, var2, var3);
   }

   @Override
   public void onBlockAdded(World var1, BlockPos var2, IBlockState var3) {
      super.onBlockAdded(var1, var2, var3);
      var1.setTileEntity(var2, this.createNewTileEntity(var1, 0));
   }

   @Override
   public void breakBlock(World var1, BlockPos var2, IBlockState var3) {
      super.breakBlock(var1, var2, var3);
      var1.removeTileEntity(var2);
      this.n(var1, var2, var3);
   }

   @Override
   public boolean onBlockEventReceived(World var1, BlockPos var2, IBlockState var3, int var4, int var5) {
      super.onBlockEventReceived(var1, var2, var3, var4, var5);
      TileEntity var9 = var1.getTileEntity(var2);
      return var9 == null ? false : var9.receiveClientEvent(var4, var5);
   }

   @Override
   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileEntityComparator();
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState()
         .withProperty(FACING, EnumFacing.getHorizontal(var1))
         .withProperty(POWERED, (var1 & 8) > 0)
         .withProperty(MODE, (var1 & 4) > 0 ? BlockRedstoneComparator.Mode.SUBTRACT : BlockRedstoneComparator.Mode.COMPARE);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var5 = 0;
      var5 |= var1.getValue(FACING).getHorizontalIndex();
      if (var1.getValue(POWERED)) {
         var5 |= 8;
      }

      if (var1.getValue(MODE) == BlockRedstoneComparator.Mode.SUBTRACT) {
         var5 |= 4;
      }

      return var5;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, MODE, POWERED);
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.getDefaultState()
         .withProperty(FACING, var8.getHorizontalFacing().getOpposite())
         .withProperty(POWERED, false)
         .withProperty(MODE, BlockRedstoneComparator.Mode.COMPARE);
   }

   public enum Mode implements IStringSerializable {
      COMPARE("compare"),
      SUBTRACT("subtract");

      private final String name;

      Mode(String var3) {
         this.name = var3;
      }

      @Override
      public String toString() {
         return this.name;
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
