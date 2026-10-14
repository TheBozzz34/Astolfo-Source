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

   public ItemPotion() {
      this.setMaxStackSize(1);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
      this.setCreativeTab(CreativeTabs.tabBrewing);
   }

   public List<PotionEffect> getEffects(ItemStack stack) {
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
         ArrayList arrayList = Lists.newArrayList();
         NBTTagList nbtTagList = stack.getTagCompound().getTagList("CustomPotionEffects", 10);

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
            PotionEffect potionEffect = PotionEffect.readCustomPotionEffectFromNBT(nbtTagCompound);
            if (potionEffect != null) {
               arrayList.add(potionEffect);
            }
         }

         return arrayList;
      } else {
         List list = this.effectCache.get(stack.getMetadata());
         if (list == null) {
            list = PotionHelper.getPotionEffects(stack.getMetadata(), false);
            this.effectCache.put(stack.getMetadata(), list);
         }

         return list;
      }
   }

   public List<PotionEffect> getEffects(int meta) {
      List list = this.effectCache.get(meta);
      if (list == null) {
         list = PotionHelper.getPotionEffects(meta, false);
         this.effectCache.put(meta, list);
      }

      return list;
   }

   @Override
   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      if (!playerIn.capabilities.isCreativeMode) {
         stack.stackSize--;
      }

      if (!worldIn.isRemote) {
         List list = this.getEffects(stack);
         if (list != null) {
            for (Object object : list) {
               PotionEffect potionEffect = (PotionEffect)object;
               playerIn.addPotionEffect(new PotionEffect(potionEffect));
            }
         }
      }

      playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      if (!playerIn.capabilities.isCreativeMode) {
         if (stack.stackSize <= 0) {
            return new ItemStack(Items.glass_bottle);
         }

         playerIn.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
      }

      return stack;
   }

   @Override
   public int getMaxItemUseDuration(ItemStack stack) {
      return 32;
   }

   @Override
   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.DRINK;
   }

   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      if (isSplash(itemStackIn.getMetadata())) {
         if (!playerIn.capabilities.isCreativeMode) {
            itemStackIn.stackSize--;
         }

         worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(new EntityPotion(worldIn, playerIn, itemStackIn));
         }

         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
         return itemStackIn;
      } else {
         playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
         return itemStackIn;
      }
   }

   public static boolean isSplash(int meta) {
      return (meta & 16384) != 0;
   }

   public int getColorFromDamage(int meta) {
      return PotionHelper.getLiquidColor(meta, false);
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      return renderPass > 0 ? 16777215 : this.getColorFromDamage(stack.getMetadata());
   }

   public boolean isEffectInstant(int meta) {
      List list = this.getEffects(meta);
      if (list != null && !list.isEmpty()) {
         for (Object object : list) {
            PotionEffect potionEffect = (PotionEffect)object;
            if (Potion.potionTypes[potionEffect.getPotionID()].isInstant()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      if (stack.getMetadata() == 0) {
         return StatCollector.translateToLocal("item.emptyPotion.name").trim();
      }

      String string = "";
      if (isSplash(stack.getMetadata())) {
         string = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
      }

      List list = Items.potionitem.getEffects(stack);
      if (list != null && !list.isEmpty()) {
         String string2 = ((PotionEffect)list.get(0)).getEffectName();
         string2 = string2 + ".postfix";
         return string + StatCollector.translateToLocal(string2).trim();
      } else {
         String string3 = PotionHelper.getPotionPrefix(stack.getMetadata());
         return StatCollector.translateToLocal(string3).trim() + " " + super.getItemStackDisplayName(stack);
      }
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      if (stack.getMetadata() != 0) {
         List list = Items.potionitem.getEffects(stack);
         HashMultimap hashMultimap = HashMultimap.create();
         if (list != null && !list.isEmpty()) {
            for (Object object : list) {
               PotionEffect potionEffect = (PotionEffect)object;
               String string = StatCollector.translateToLocal(potionEffect.getEffectName()).trim();
               Potion potion2 = Potion.potionTypes[potionEffect.getPotionID()];
               Map map = potion2.getAttributeModifierMap();
               if (map != null && map.size() > 0) {
                  for (Object object2 : map.entrySet()) {
                     Entry entry = (Entry)object2;
                     AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
                     AttributeModifier attributeModifier2 = new AttributeModifier(
                        attributeModifier.getName(), potion2.getAttributeModifierAmount(potionEffect.getAmplifier(), attributeModifier), attributeModifier.getOperation()
                     );
                     hashMultimap.put(((IAttribute)entry.getKey()).getAttributeUnlocalizedName(), attributeModifier2);
                  }
               }

               if (potionEffect.getAmplifier() > 0) {
                  string = string + " " + StatCollector.translateToLocal("potion.potency." + potionEffect.getAmplifier()).trim();
               }

               if (potionEffect.getDuration() > 20) {
                  string = string + " (" + Potion.getDurationString(potionEffect) + ")";
               }

               if (potion2.isBadEffect()) {
                  tooltip.add(EnumChatFormatting.RED + string);
               } else {
                  tooltip.add(EnumChatFormatting.GRAY + string);
               }
            }
         } else {
            String string2 = StatCollector.translateToLocal("potion.empty").trim();
            tooltip.add(EnumChatFormatting.GRAY + string2);
         }

         if (!hashMultimap.isEmpty()) {
            tooltip.add("");
            tooltip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));

            for (Object object3 : hashMultimap.entries()) {
               Entry entry2 = (Entry)object3;
               AttributeModifier attributeModifier3 = (AttributeModifier)entry2.getValue();
               double d = attributeModifier3.getAmount();
               double e;
               if (attributeModifier3.getOperation() != 1 && attributeModifier3.getOperation() != 2) {
                  e = attributeModifier3.getAmount();
               } else {
                  e = attributeModifier3.getAmount() * 100.0;
               }

               if (d > 0.0) {
                  tooltip.add(
                     EnumChatFormatting.BLUE
                        + StatCollector.translateToLocalFormatted(
                           "attribute.modifier.plus." + attributeModifier3.getOperation(),
                           ItemStack.DECIMALFORMAT.format(e),
                           StatCollector.translateToLocal("attribute.name." + (String)entry2.getKey())
                        )
                  );
               } else if (d < 0.0) {
                  e *= -1.0;
                  tooltip.add(
                     EnumChatFormatting.RED
                        + StatCollector.translateToLocalFormatted(
                           "attribute.modifier.take." + attributeModifier3.getOperation(),
                           ItemStack.DECIMALFORMAT.format(e),
                           StatCollector.translateToLocal("attribute.name." + (String)entry2.getKey())
                        )
                  );
               }
            }
         }
      }
   }

   @Override
   public boolean hasEffect(ItemStack stack) {
      List list = this.getEffects(stack);
      return list != null && !list.isEmpty();
   }

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      super.getSubItems(itemIn, tab, subItems);
      if (SUB_ITEMS_CACHE.isEmpty()) {
         for (int i = 0; i <= 15; i++) {
            for (int j = 0; j <= 1; j++) {
               int k;
               if (j == 0) {
                  k = i | 8192;
               } else {
                  k = i | 16384;
               }

               for (int l = 0; l <= 2; l++) {
                  int m = k;
                  if (l != 0) {
                     if (l == 1) {
                        m = k | 32;
                     } else if (l == 2) {
                        m = k | 64;
                     }
                  }

                  List list = PotionHelper.getPotionEffects(m, false);
                  if (list != null && !list.isEmpty()) {
                     SUB_ITEMS_CACHE.put(list, m);
                  }
               }
            }
         }
      }

      for (int n : SUB_ITEMS_CACHE.values()) {
         subItems.add(new ItemStack(itemIn, 1, n));
      }
   }
}
