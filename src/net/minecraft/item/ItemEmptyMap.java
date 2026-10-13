package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class ItemEmptyMap extends ItemMapBase {
   private static final long a = 62461965309321L;

   public ItemEmptyMap() {
      this.setCreativeTab(CreativeTabs.tabMisc);
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      ItemStack var7 = new ItemStack(Items.filled_map, 1, var2.getUniqueDataId("map"));
      String var8 = "map_" + var7.getMetadata();
      MapData var9 = new MapData(var8);
      var2.setItemData(var8, var9);
      var9.scale = 0;
      var9.calculateMapCenter(var3.posX, var3.posZ, var9.scale);
      var9.dimension = (byte)var2.provider.getDimensionId();
      var9.markDirty();
      var1.stackSize--;
      if (var1.stackSize <= 0) {
         return var7;
      }

      if (!var3.inventory.addItemStackToInventory(var7.copy())) {
         var3.dropPlayerItemWithRandomChoice(var7, false);
      }

      var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      return var1;
   }
}
