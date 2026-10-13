package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPotion extends Item {
   private Map<Integer, List<PotionEffect>> effectCache = Maps.newHashMap();
   private static final Map<List<PotionEffect>, Integer> SUB_ITEMS_CACHE = Maps.newLinkedHashMap();
   private static final long b = 83509567862196L;

   public ItemPotion() {
      this.setMaxStackSize(1);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
      this.setCreativeTab(CreativeTabs.tabBrewing);
   }

   public List<PotionEffect> getEffects(ItemStack var1) {
      if (var1.hasTagCompound() && var1.getTagCompound().hasKey("CustomPotionEffects", 9)) {
         ArrayList var10 = Lists.newArrayList();
         NBTTagList var6 = var1.getTagCompound().getTagList("CustomPotionEffects", 10);

         for (int var7 = 0; var7 < var6.tagCount(); var7++) {
            NBTTagCompound var8 = var6.getCompoundTagAt(var7);
            PotionEffect var9 = PotionEffect.readCustomPotionEffectFromNBT(var8);
            if (var9 != null) {
               var10.add(var9);
            }
         }

         return var10;
      } else {
         List var5 = this.effectCache.get(var1.getMetadata());
         if (var5 == null) {
            var5 = PotionHelper.getPotionEffects(var1.getMetadata(), false);
            this.effectCache.put(var1.getMetadata(), var5);
         }

         return var5;
      }
   }

   public List<PotionEffect> getEffects(int var1) {
      List var5 = this.effectCache.get(var1);
      if (var5 == null) {
         var5 = PotionHelper.getPotionEffects(var1, false);
         this.effectCache.put(var1, var5);
      }

      return var5;
   }

   @Override
   public ItemStack onItemUseFinish(ItemStack var1, World var2, EntityPlayer var3) {
      if (!var3.capabilities.isCreativeMode) {
         var1.stackSize--;
      }

      if (!var2.isRemote) {
         List var7 = this.getEffects(var1);
         if (var7 != null) {
            for (Object var9$raw : var7) {
               PotionEffect var9 = (PotionEffect)var9$raw;
               var3.addPotionEffect(new PotionEffect(var9));
            }
         }
      }

      var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      if (!var3.capabilities.isCreativeMode) {
         if (var1.stackSize <= 0) {
            return new ItemStack(Items.glass_bottle);
         }

         var3.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
      }

      return var1;
   }

   @Override
   public int getMaxItemUseDuration(ItemStack var1) {
      return 32;
   }

   @Override
   public EnumAction getItemUseAction(ItemStack var1) {
      return EnumAction.DRINK;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack var1, World var2, EntityPlayer var3) {
      if (isSplash(var1.getMetadata())) {
         if (!var3.capabilities.isCreativeMode) {
            var1.stackSize--;
         }

         var2.playSoundAtEntity(var3, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!var2.isRemote) {
            var2.spawnEntityInWorld(new EntityPotion(var2, var3, var1));
         }

         var3.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         return var1;
      } else {
         var3.setItemInUse(var1, this.getMaxItemUseDuration(var1));
         return var1;
      }
   }

   public static boolean isSplash(int var0) {
      return (var0 & 16384) != 0;
   }

   public int getColorFromDamage(int var1) {
      return PotionHelper.getLiquidColor(var1, false);
   }

   @Override
   public int getColorFromItemStack(ItemStack var1, int var2) {
      return var2 > 0 ? 16777215 : this.getColorFromDamage(var1.getMetadata());
   }

   public boolean isEffectInstant(int var1) {
      List var5 = this.getEffects(var1);
      if (var5 != null && !var5.isEmpty()) {
         for (Object var7$raw : var5) {
            PotionEffect var7 = (PotionEffect)var7$raw;
            if (Potion.potionTypes[var7.getPotionID()].isInstant()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public String getItemStackDisplayName(ItemStack var1) {
      if (var1.getMetadata() == 0) {
         return StatCollector.translateToLocal("item.emptyPotion.name").trim();
      }

      String var5 = "";
      if (isSplash(var1.getMetadata())) {
         var5 = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
      }

      List var6 = Items.potionitem.getEffects(var1);
      if (var6 != null && !var6.isEmpty()) {
         String var8 = ((PotionEffect)var6.get(0)).getEffectName();
         var8 = var8 + ".postfix";
         return var5 + StatCollector.translateToLocal(var8).trim();
      } else {
         String var7 = PotionHelper.getPotionPrefix(var1.getMetadata());
         return StatCollector.translateToLocal(var7).trim() + " " + super.getItemStackDisplayName(var1);
      }
   }

   @Override
   public void addInformation(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      if (var1.getMetadata() != 0) {
         List var8 = Items.potionitem.getEffects(var1);
         HashMultimap var9 = HashMultimap.create();
         if (var8 != null && !var8.isEmpty()) {
            for (Object var11$raw : var8) {
               PotionEffect var11 = (PotionEffect)var11$raw;
               String var12 = StatCollector.translateToLocal(var11.getEffectName()).trim();
               Potion var13 = Potion.potionTypes[var11.getPotionID()];
               Map var14 = var13.getAttributeModifierMap();
               if (var14 != null && var14.size() > 0) {
                  for (Object var16$raw : var14.entrySet()) {
                     Entry var16 = (Entry)var16$raw;
                     AttributeModifier var17 = (AttributeModifier)var16.getValue();
                     AttributeModifier var18 = new AttributeModifier(
                        var17.getName(), var13.getAttributeModifierAmount(var11.getAmplifier(), var17), var17.getOperation()
                     );
                     var9.put(((IAttribute)var16.getKey()).getAttributeUnlocalizedName(), var18);
                  }
               }

               if (var11.getAmplifier() > 0) {
                  var12 = var12 + " " + StatCollector.translateToLocal("potion.potency." + var11.getAmplifier()).trim();
               }

               if (var11.getDuration() > 20) {
                  var12 = var12 + " (" + Potion.getDurationString(var11) + ")";
               }

               if (var13.isBadEffect()) {
                  var3.add(EnumChatFormatting.RED + var12);
               } else {
                  var3.add(EnumChatFormatting.GRAY + var12);
               }
            }
         } else {
            String var10 = StatCollector.translateToLocal("potion.empty").trim();
            var3.add(EnumChatFormatting.GRAY + var10);
         }

         if (!var9.isEmpty()) {
            var3.add("");
            var3.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));

            for (Object var21$raw : var9.entries()) {
               Entry var21 = (Entry)var21$raw;
               AttributeModifier var22 = (AttributeModifier)var21.getValue();
               double var23 = var22.getAmount();
               double var24;
               if (var22.getOperation() != 1 && var22.getOperation() != 2) {
                  var24 = var22.getAmount();
               } else {
                  var24 = var22.getAmount() * 100.0;
               }

               if (var23 > 0.0) {
                  var3.add(
                     EnumChatFormatting.BLUE
                        + StatCollector.translateToLocalFormatted(
                           "attribute.modifier.plus." + var22.getOperation(),
                           ItemStack.DECIMALFORMAT.format(var24),
                           StatCollector.translateToLocal("attribute.name." + (String)var21.getKey())
                        )
                  );
               } else if (var23 < 0.0) {
                  var24 *= -1.0;
                  var3.add(
                     EnumChatFormatting.RED
                        + StatCollector.translateToLocalFormatted(
                           "attribute.modifier.take." + var22.getOperation(),
                           ItemStack.DECIMALFORMAT.format(var24),
                           StatCollector.translateToLocal("attribute.name." + (String)var21.getKey())
                        )
                  );
               }
            }
         }
      }
   }

   @Override
   public boolean hasEffect(ItemStack var1) {
      List var5 = this.getEffects(var1);
      return var5 != null && !var5.isEmpty();
   }

   @Override
   public void getSubItems(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      super.getSubItems(var1, var2, var3);
      if (SUB_ITEMS_CACHE.isEmpty()) {
         for (int var7 = 0; var7 <= 15; var7++) {
            for (int var8 = 0; var8 <= 1; var8++) {
               int var9;
               if (var8 == 0) {
                  var9 = var7 | 8192;
               } else {
                  var9 = var7 | 16384;
               }

               for (int var10 = 0; var10 <= 2; var10++) {
                  int var11 = var9;
                  if (var10 != 0) {
                     if (var10 == 1) {
                        var11 = var9 | 32;
                     } else if (var10 == 2) {
                        var11 = var9 | 64;
                     }
                  }

                  List var12 = PotionHelper.getPotionEffects(var11, false);
                  if (var12 != null && !var12.isEmpty()) {
                     SUB_ITEMS_CACHE.put(var12, var11);
                  }
               }
            }
         }
      }

      for (int var14 : SUB_ITEMS_CACHE.values()) {
         var3.add(new ItemStack(var1, 1, var14));
      }
   }
}
