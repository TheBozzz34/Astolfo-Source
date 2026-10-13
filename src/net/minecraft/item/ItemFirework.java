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
   private static final long a = 119644669813052L;

   @Override
   public boolean onItemUse(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var3.isRemote) {
         EntityFireworkRocket var12 = new EntityFireworkRocket(var3, var4.getX() + var6, var4.getY() + var7, var4.getZ() + var8, var1);
         var3.spawnEntityInWorld(var12);
         if (!var2.capabilities.isCreativeMode) {
            var1.stackSize--;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void addInformation(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      if (var1.hasTagCompound()) {
         NBTTagCompound var8 = var1.getTagCompound().getCompoundTag("Fireworks");
         if (var8 != null) {
            if (var8.hasKey("Flight", 99)) {
               var3.add(StatCollector.translateToLocal("item.fireworks.flight") + " " + var8.getByte("Flight"));
            }

            NBTTagList var9 = var8.getTagList("Explosions", 10);
            if (var9 != null && var9.tagCount() > 0) {
               for (int var10 = 0; var10 < var9.tagCount(); var10++) {
                  NBTTagCompound var11 = var9.getCompoundTagAt(var10);
                  ArrayList var12 = Lists.newArrayList();
                  ItemFireworkCharge.addExplosionInfo(var11, var12);
                  if (var12.size() > 0) {
                     for (int var13 = 1; var13 < var12.size(); var13++) {
                        var12.set(var13, "  " + (String)var12.get(var13));
                     }

                     var3.addAll(var12);
                  }
               }
            }
         }
      }
   }
}
