package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class BlockAnvil extends BlockFalling {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
   public static final PropertyInteger DAMAGE = PropertyInteger.create("damage", 0, 2);
   private static final long e = 18537348588443L;

   public BlockAnvil() {
      super(Material.anvil);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DAMAGE, 0));
      this.setLightOpacity(0);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public IBlockState onBlockPlaced(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      EnumFacing var9 = var8.getHorizontalFacing().rotateY();
      return super.onBlockPlaced(var1, var2, var3, var4, var5, var6, var7, var8).withProperty(FACING, var9).withProperty(DAMAGE, var7 >> 2);
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var1.isRemote) {
         var4.displayGui(new BlockAnvil.Anvil(var1, var2));
      }

      return true;
   }

   @Override
   public int damageDropped(IBlockState var1) {
      return var1.getValue(DAMAGE);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess var1, BlockPos var2) {
      EnumFacing var6 = var1.getBlockState(var2).getValue(FACING);
      if (var6.getAxis() == EnumFacing.Axis.X) {
         this.setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
      } else {
         this.setBlockBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
      }
   }

   @Override
   public void getSubBlocks(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, 0));
      var3.add(new ItemStack(var1, 1, 1));
      var3.add(new ItemStack(var1, 1, 2));
   }

   @Override
   public void onStartFalling(EntityFallingBlock var1) {
      var1.setHurtEntities(true);
   }

   @Override
   public void onEndFalling(World var1, BlockPos var2) {
      var1.playAuxSFX(1022, var2, 0);
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   @Override
   public IBlockState getStateForEntityRender(IBlockState var1) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
   }

   @Override
   public IBlockState getStateFromMeta(int var1) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(var1 & 3)).withProperty(DAMAGE, (var1 & 15) >> 2);
   }

   @Override
   public int getMetaFromState(IBlockState var1) {
      int var2 = 0;
      var2 |= var1.getValue(FACING).getHorizontalIndex();
      return var2 | var1.getValue(DAMAGE) << 2;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, DAMAGE);
   }

   public static class Anvil implements IInteractionObject {
      private final World world;
      private final BlockPos position;
      private static final long a = 6342697609328L;

      public Anvil(World var1, BlockPos var2) {
         this.world = var1;
         this.position = var2;
      }

      @Override
      public String getName() {
         return "anvil";
      }

      @Override
      public boolean hasCustomName() {
         return false;
      }

      @Override
      public IChatComponent getDisplayName() {
         return new ChatComponentTranslation(Blocks.anvil.getUnlocalizedName() + ".name");
      }

      @Override
      public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
         return new ContainerRepair(var1, this.world, this.position, var2);
      }

      @Override
      public String getGuiID() {
         return "minecraft:anvil";
      }
   }
}
