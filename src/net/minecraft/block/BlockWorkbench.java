package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class BlockWorkbench extends Block {
   private static final long e = 113903629716228L;

   public BlockWorkbench() {
      super(Material.wood);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean onBlockActivated(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.isRemote) {
         return true;
      }

      var4.displayGui(new BlockWorkbench.InterfaceCraftingTable(var1, var2));
      var4.triggerAchievement(StatList.field_181742_Z);
      return true;
   }

   public static class InterfaceCraftingTable implements IInteractionObject {
      private final World world;
      private final BlockPos position;
      private static final long a = 112533289309207L;

      public InterfaceCraftingTable(World var1, BlockPos var2) {
         this.world = var1;
         this.position = var2;
      }

      @Override
      public String getName() {
         return null;
      }

      @Override
      public boolean hasCustomName() {
         return false;
      }

      @Override
      public IChatComponent getDisplayName() {
         return new ChatComponentTranslation(Blocks.crafting_table.getUnlocalizedName() + ".name");
      }

      @Override
      public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
         return new ContainerWorkbench(var1, this.world, this.position);
      }

      @Override
      public String getGuiID() {
         return "minecraft:crafting_table";
      }
   }
}
