package net.minecraft.entity.item;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer {
   private static final long ab = 70260913384049L;

   public EntityMinecartChest(World var1) {
      super(var1);
   }

   public EntityMinecartChest(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public void killMinecart(DamageSource var1) {
      super.killMinecart(var1);
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         this.dropItemWithOffset(Item.getItemFromBlock(Blocks.chest), 1, 0.0F);
      }
   }

   @Override
   public int getSizeInventory() {
      return 27;
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.CHEST;
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return Blocks.chest.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
   }

   @Override
   public int getDefaultDisplayTileOffset() {
      return 8;
   }

   @Override
   public String getGuiID() {
      return "minecraft:chest";
   }

   @Override
   public Container createContainer(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerChest(var1, this, var2);
   }
}
