package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFirework extends Item {
   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         EntityFireworkRocket entityFireworkRocket = new EntityFireworkRocket(worldIn, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack);
         worldIn.spawnEntityInWorld(entityFireworkRocket);
         if (!playerIn.capabilities.isCreativeMode) {
            stack.stackSize--;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.hasTagCompound()) {
         NBTTagCompound nbtTagCompound = stack.getTagCompound().getCompoundTag("Fireworks");
         if (nbtTagCompound != null) {
            if (nbtTagCompound.hasKey("Flight", 99)) {
               tooltip.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + nbtTagCompound.getByte("Flight"));
            }

            NBTTagList nbtTagList = nbtTagCompound.getTagList("Explosions", 10);
            if (nbtTagList != null && nbtTagList.tagCount() > 0) {
               for (int i = 0; i < nbtTagList.tagCount(); i++) {
                  NBTTagCompound nbtTagCompound2 = nbtTagList.getCompoundTagAt(i);
                  ArrayList arrayList = Lists.newArrayList();
                  ItemFireworkCharge.addExplosionInfo(nbtTagCompound2, arrayList);
                  if (arrayList.size() > 0) {
                     for (int j = 1; j < arrayList.size(); j++) {
                        arrayList.set(j, "  " + (String)arrayList.get(j));
                     }

                     tooltip.addAll(arrayList);
                  }
               }
            }
         }
      }
   }
}
