package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public final class ItemStack {
   public static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.###");
   public int stackSize;
   public int animationsToGo;
   private Item item;
   private NBTTagCompound stackTagCompound;
   private int itemDamage;
   private EntityItemFrame itemFrame;
   private Block canDestroyCacheBlock = null;
   private boolean canDestroyCacheResult = false;
   private Block canPlaceOnCacheBlock = null;
   private boolean canPlaceOnCacheResult = false;

   public ItemStack(Block blockIn) {
      this(blockIn, 1);
   }

   public ItemStack(Block blockIn, int amount) {
      this(blockIn, amount, 0);
   }

   public ItemStack(Block blockIn, int amount, int meta) {
      this(Item.getItemFromBlock(blockIn), amount, meta);
   }

   public ItemStack(Item itemIn) {
      this(itemIn, 1);
   }

   public ItemStack(Item itemIn, int amount) {
      this(itemIn, amount, 0);
   }

   public ItemStack(Item itemIn, int amount, int meta) {
      this.item = itemIn;
      this.stackSize = amount;
      this.itemDamage = meta;
      if (this.itemDamage < 0) {
         this.itemDamage = 0;
      }
   }

   public static ItemStack loadItemStackFromNBT(NBTTagCompound nbt) {
      ItemStack itemStack = new ItemStack();
      itemStack.readFromNBT(nbt);
      return itemStack.getItem() != null ? itemStack : null;
   }

   private ItemStack() {
   }

   public ItemStack splitStack(int amount) {
      ItemStack itemStack = new ItemStack(this.item, amount, this.itemDamage);
      if (this.stackTagCompound != null) {
         itemStack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
      }

      this.stackSize -= amount;
      return itemStack;
   }

   public Item getItem() {
      return this.item;
   }

   public boolean onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      boolean bl = this.getItem().onItemUse(this, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
      if (bl) {
         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
      }

      return bl;
   }

   public float getStrVsBlock(Block blockIn) {
      return this.getItem().getStrVsBlock(this, blockIn);
   }

   public ItemStack useItemRightClick(World worldIn, EntityPlayer playerIn) {
      return this.getItem().onItemRightClick(this, worldIn, playerIn);
   }

   public ItemStack onItemUseFinish(World worldIn, EntityPlayer playerIn) {
      return this.getItem().onItemUseFinish(this, worldIn, playerIn);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      ResourceLocation resourceLocation = Item.itemRegistry.getNameForObject(this.item);
      nbt.setString("id", resourceLocation == null ? "minecraft:air" : resourceLocation.toString());
      nbt.setByte("Count", (byte)this.stackSize);
      nbt.setShort("Damage", (short)this.itemDamage);
      if (this.stackTagCompound != null) {
         nbt.setTag("tag", this.stackTagCompound);
      }

      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      if (nbt.hasKey("id", 8)) {
         this.item = Item.getByNameOrId(nbt.getString("id"));
      } else {
         this.item = Item.getItemById(nbt.getShort("id"));
      }

      this.stackSize = nbt.getByte("Count");
      this.itemDamage = nbt.getShort("Damage");
      if (this.itemDamage < 0) {
         this.itemDamage = 0;
      }

      if (nbt.hasKey("tag", 10)) {
         this.stackTagCompound = nbt.getCompoundTag("tag");
         if (this.item != null) {
            this.item.updateItemStackNBT(this.stackTagCompound);
         }
      }
   }

   public int getMaxStackSize() {
      return this.getItem().getItemStackLimit();
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
   }

   public boolean isItemStackDamageable() {
      return this.item == null ? false : (this.item.getMaxDamage() <= 0 ? false : !this.hasTagCompound() || !this.getTagCompound().getBoolean("Unbreakable"));
   }

   public boolean getHasSubtypes() {
      return this.item.getHasSubtypes();
   }

   public boolean isItemDamaged() {
      return this.isItemStackDamageable() && this.itemDamage > 0;
   }

   public int getItemDamage() {
      return this.itemDamage;
   }

   public int getMetadata() {
      return this.itemDamage;
   }

   public void setItemDamage(int meta) {
      this.itemDamage = meta;
      if (this.itemDamage < 0) {
         this.itemDamage = 0;
      }
   }

   public int getMaxDamage() {
      return this.item.getMaxDamage();
   }

   public boolean attemptDamageItem(int amount, Random rand) {
      if (!this.isItemStackDamageable()) {
         return false;
      }

      if (amount > 0) {
         int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, this);
         int j = 0;

         for (int k = 0; i > 0 && k < amount; k++) {
            if (EnchantmentDurability.negateDamage(this, i, rand)) {
               j++;
            }
         }

         amount -= j;
         if (amount <= 0) {
            return false;
         }
      }

      this.itemDamage += amount;
      return this.itemDamage > this.getMaxDamage();
   }

   public void damageItem(int amount, EntityLivingBase entityIn) {
      if ((!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).capabilities.isCreativeMode)
         && this.isItemStackDamageable()
         && this.attemptDamageItem(amount, entityIn.getRNG())) {
         entityIn.renderBrokenItemStack(this);
         this.stackSize--;
         if (entityIn instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)entityIn;
            entityPlayer.triggerAchievement(StatList.objectBreakStats[Item.getIdFromItem(this.item)]);
            if (this.stackSize == 0 && this.getItem() instanceof ItemBow) {
               entityPlayer.destroyCurrentEquippedItem();
            }
         }

         if (this.stackSize < 0) {
            this.stackSize = 0;
         }

         this.itemDamage = 0;
      }
   }

   public void hitEntity(EntityLivingBase entityIn, EntityPlayer playerIn) {
      boolean bl = this.item.hitEntity(this, entityIn, playerIn);
      if (bl) {
         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
      }
   }

   public void onBlockDestroyed(World worldIn, Block blockIn, BlockPos pos, EntityPlayer playerIn) {
      boolean bl = this.item.onBlockDestroyed(this, worldIn, blockIn, pos, playerIn);
      if (bl) {
         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
      }
   }

   public boolean canHarvestBlock(Block blockIn) {
      return this.item.canHarvestBlock(blockIn);
   }

   public boolean interactWithEntity(EntityPlayer playerIn, EntityLivingBase entityIn) {
      return this.item.itemInteractionForEntity(this, playerIn, entityIn);
   }

   public ItemStack copy() {
      ItemStack itemStack = new ItemStack(this.item, this.stackSize, this.itemDamage);
      if (this.stackTagCompound != null) {
         itemStack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
      }

      return itemStack;
   }

   public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
      return stackA == null && stackB == null
         ? true
         : (
            stackA == null || stackB == null
               ? false
               : (
                  stackA.stackTagCompound == null && stackB.stackTagCompound != null
                     ? false
                     : stackA.stackTagCompound == null || stackA.stackTagCompound.equals(stackB.stackTagCompound)
               )
         );
   }

   public static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB) {
      return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemStackEqual(stackB) : false);
   }

   private boolean isItemStackEqual(ItemStack other) {
      return this.stackSize != other.stackSize
         ? false
         : (
            this.item != other.item
               ? false
               : (
                  this.itemDamage != other.itemDamage
                     ? false
                     : (
                        this.stackTagCompound == null && other.stackTagCompound != null
                           ? false
                           : this.stackTagCompound == null || this.stackTagCompound.equals(other.stackTagCompound)
                     )
               )
         );
   }

   public static boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
      return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemEqual(stackB) : false);
   }

   public boolean isItemEqual(ItemStack other) {
      return other != null && this.item == other.item && this.itemDamage == other.itemDamage;
   }

   public String getUnlocalizedName() {
      return this.item.getUnlocalizedName(this);
   }

   public static ItemStack copyItemStack(ItemStack stack) {
      return stack == null ? null : stack.copy();
   }

   @Override
   public String toString() {
      return this.stackSize + "x" + this.item.getUnlocalizedName() + "@" + this.itemDamage;
   }

   public void updateAnimation(World worldIn, Entity entityIn, int inventorySlot, boolean isCurrentItem) {
      if (this.animationsToGo > 0) {
         this.animationsToGo--;
      }

      this.item.onUpdate(this, worldIn, entityIn, inventorySlot, isCurrentItem);
   }

   public void onCrafting(World worldIn, EntityPlayer playerIn, int amount) {
      playerIn.addStat(StatList.objectCraftStats[Item.getIdFromItem(this.item)], amount);
      this.item.onCreated(this, worldIn, playerIn);
   }

   public boolean getIsItemStackEqual(ItemStack itemStack) {
      return this.isItemStackEqual(itemStack);
   }

   public int getMaxItemUseDuration() {
      return this.getItem().getMaxItemUseDuration(this);
   }

   public EnumAction getItemUseAction() {
      return this.getItem().getItemUseAction(this);
   }

   public void onPlayerStoppedUsing(World worldIn, EntityPlayer playerIn, int timeLeft) {
      this.getItem().onPlayerStoppedUsing(this, worldIn, playerIn, timeLeft);
   }

   public boolean hasTagCompound() {
      return this.stackTagCompound != null;
   }

   public NBTTagCompound getTagCompound() {
      return this.stackTagCompound;
   }

   public NBTTagCompound getSubCompound(String key, boolean createIn) {
      if (this.stackTagCompound != null && this.stackTagCompound.hasKey(key, 10)) {
         return this.stackTagCompound.getCompoundTag(key);
      } else if (createIn) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         this.setTagInfo(key, nbtTagCompound);
         return nbtTagCompound;
      } else {
         return null;
      }
   }

   public NBTTagList getEnchantmentTagList() {
      return this.stackTagCompound == null ? null : this.stackTagCompound.getTagList("ench", 10);
   }

   public void setTagCompound(NBTTagCompound nbt) {
      this.stackTagCompound = nbt;
   }

   public String getDisplayName() {
      String string = this.getItem().getItemStackDisplayName(this);
      if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10)) {
         NBTTagCompound nbtTagCompound = this.stackTagCompound.getCompoundTag("display");
         if (nbtTagCompound.hasKey("Name", 8)) {
            string = nbtTagCompound.getString("Name");
         }
      }

      return string;
   }

   public ItemStack setStackDisplayName(String displayName) {
      if (this.stackTagCompound == null) {
         this.stackTagCompound = new NBTTagCompound();
      }

      if (!this.stackTagCompound.hasKey("display", 10)) {
         this.stackTagCompound.setTag("display", new NBTTagCompound());
      }

      this.stackTagCompound.getCompoundTag("display").setString("Name", displayName);
      return this;
   }

   public void clearCustomName() {
      if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10)) {
         NBTTagCompound nbtTagCompound = this.stackTagCompound.getCompoundTag("display");
         nbtTagCompound.removeTag("Name");
         if (nbtTagCompound.hasNoTags()) {
            this.stackTagCompound.removeTag("display");
            if (this.stackTagCompound.hasNoTags()) {
               this.setTagCompound((NBTTagCompound)null);
            }
         }
      }
   }

   public boolean hasDisplayName() {
      return this.stackTagCompound == null
         ? false
         : (!this.stackTagCompound.hasKey("display", 10) ? false : this.stackTagCompound.getCompoundTag("display").hasKey("Name", 8));
   }

   public List<String> getTooltip(EntityPlayer playerIn, boolean advanced) {
      ArrayList arrayList = Lists.newArrayList();
      String string = this.getDisplayName();
      if (this.hasDisplayName()) {
         string = EnumChatFormatting.ITALIC + string;
      }

      string = string + EnumChatFormatting.RESET;
      if (advanced) {
         String string2 = "";
         if (string.length() > 0) {
            string = string + " (";
            string2 = ")";
         }

         int i = Item.getIdFromItem(this.item);
         if (this.getHasSubtypes()) {
            string = string + String.format("#%04d/%d%s", i, this.itemDamage, string2);
         } else {
            string = string + String.format("#%04d%s", i, string2);
         }
      } else if (!this.hasDisplayName() && this.item == Items.filled_map) {
         string = string + " #" + this.itemDamage;
      }

      arrayList.add(string);
      int j = 0;
      if (this.hasTagCompound() && this.stackTagCompound.hasKey("HideFlags", 99)) {
         j = this.stackTagCompound.getInteger("HideFlags");
      }

      if ((j & 32) == 0) {
         this.item.addInformation(this, playerIn, arrayList, advanced);
      }

      if (this.hasTagCompound()) {
         if ((j & 1) == 0) {
            NBTTagList nbtTagList = this.getEnchantmentTagList();
            if (nbtTagList != null) {
               for (int k = 0; k < nbtTagList.tagCount(); k++) {
                  short s2 = nbtTagList.getCompoundTagAt(k).getShort("id");
                  short s3 = nbtTagList.getCompoundTagAt(k).getShort("lvl");
                  if (Enchantment.getEnchantmentById(s2) != null) {
                     arrayList.add(Enchantment.getEnchantmentById(s2).getTranslatedName(s3));
                  }
               }
            }
         }

         if (this.stackTagCompound.hasKey("display", 10)) {
            NBTTagCompound nbtTagCompound = this.stackTagCompound.getCompoundTag("display");
            if (nbtTagCompound.hasKey("color", 3)) {
               if (advanced) {
                  arrayList.add("Color: #" + Integer.toHexString(nbtTagCompound.getInteger("color")).toUpperCase());
               } else {
                  arrayList.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("item.dyed"));
               }
            }

            if (nbtTagCompound.getTagId("Lore") == 9) {
               NBTTagList nbtTagList2 = nbtTagCompound.getTagList("Lore", 8);
               if (nbtTagList2.tagCount() > 0) {
                  for (int l = 0; l < nbtTagList2.tagCount(); l++) {
                     arrayList.add(EnumChatFormatting.DARK_PURPLE + "" + EnumChatFormatting.ITALIC + nbtTagList2.getStringTagAt(l));
                  }
               }
            }
         }
      }

      Multimap multimap = this.getAttributeModifiers();
      if (!multimap.isEmpty() && (j & 2) == 0) {
         arrayList.add("");

         for (Object object : multimap.entries()) {
            Entry entry = (Entry)object;
            AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
            double e = attributeModifier.getAmount();
            if (attributeModifier.getID() == Item.itemModifierUUID) {
               e += EnchantmentHelper.getModifierForCreature(this, EnumCreatureAttribute.UNDEFINED);
            }

            double d2;
            if (attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2) {
               d2 = e;
            } else {
               d2 = e * 100.0;
            }

            if (e > 0.0) {
               arrayList.add(
                  EnumChatFormatting.BLUE
                     + StatCollector.translateToLocalFormatted(
                        "attribute.modifier.plus." + attributeModifier.getOperation(),
                        DECIMALFORMAT.format(d2),
                        StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())
                     )
               );
            } else if (e < 0.0) {
               d2 *= -1.0;
               arrayList.add(
                  EnumChatFormatting.RED
                     + StatCollector.translateToLocalFormatted(
                        "attribute.modifier.take." + attributeModifier.getOperation(),
                        DECIMALFORMAT.format(d2),
                        StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())
                     )
               );
            }
         }
      }

      if (this.hasTagCompound() && this.getTagCompound().getBoolean("Unbreakable") && (j & 4) == 0) {
         arrayList.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("item.unbreakable"));
      }

      if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9) && (j & 8) == 0) {
         NBTTagList nbtTagList3 = this.stackTagCompound.getTagList("CanDestroy", 8);
         if (nbtTagList3.tagCount() > 0) {
            arrayList.add("");
            arrayList.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.canBreak"));

            for (int m = 0; m < nbtTagList3.tagCount(); m++) {
               Block block = Block.getBlockFromName(nbtTagList3.getStringTagAt(m));
               if (block != null) {
                  arrayList.add(EnumChatFormatting.DARK_GRAY + block.getLocalizedName());
               } else {
                  arrayList.add(EnumChatFormatting.DARK_GRAY + "missingno");
               }
            }
         }
      }

      if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9) && (j & 16) == 0) {
         NBTTagList nbtTagList4 = this.stackTagCompound.getTagList("CanPlaceOn", 8);
         if (nbtTagList4.tagCount() > 0) {
            arrayList.add("");
            arrayList.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.canPlace"));

            for (int n = 0; n < nbtTagList4.tagCount(); n++) {
               Block block2 = Block.getBlockFromName(nbtTagList4.getStringTagAt(n));
               if (block2 != null) {
                  arrayList.add(EnumChatFormatting.DARK_GRAY + block2.getLocalizedName());
               } else {
                  arrayList.add(EnumChatFormatting.DARK_GRAY + "missingno");
               }
            }
         }
      }

      if (advanced) {
         if (this.isItemDamaged()) {
            arrayList.add("Durability: " + (this.getMaxDamage() - this.getItemDamage()) + " / " + this.getMaxDamage());
         }

         arrayList.add(EnumChatFormatting.DARK_GRAY + Item.itemRegistry.getNameForObject(this.item).toString());
         if (this.hasTagCompound()) {
            arrayList.add(EnumChatFormatting.DARK_GRAY + "NBT: " + this.getTagCompound().getKeySet().size() + " tag(s)");
         }
      }

      return arrayList;
   }

   public boolean hasEffect() {
      return this.getItem().hasEffect(this);
   }

   public EnumRarity getRarity() {
      return this.getItem().getRarity(this);
   }

   public boolean isItemEnchantable() {
      return !this.getItem().isItemTool(this) ? false : !this.isItemEnchanted();
   }

   public void addEnchantment(Enchantment ench, int level) {
      if (this.stackTagCompound == null) {
         this.setTagCompound(new NBTTagCompound());
      }

      if (!this.stackTagCompound.hasKey("ench", 9)) {
         this.stackTagCompound.setTag("ench", new NBTTagList());
      }

      NBTTagList nbtTagList = this.stackTagCompound.getTagList("ench", 10);
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      nbtTagCompound.setShort("id", (short)ench.effectId);
      nbtTagCompound.setShort("lvl", (byte)level);
      nbtTagList.appendTag(nbtTagCompound);
   }

   public boolean isItemEnchanted() {
      return this.stackTagCompound != null && this.stackTagCompound.hasKey("ench", 9);
   }

   public void setTagInfo(String key, NBTBase value) {
      if (this.stackTagCompound == null) {
         this.setTagCompound(new NBTTagCompound());
      }

      this.stackTagCompound.setTag(key, value);
   }

   public boolean canEditBlocks() {
      return this.getItem().canItemEditBlocks();
   }

   public boolean isOnItemFrame() {
      return this.itemFrame != null;
   }

   public void setItemFrame(EntityItemFrame frame) {
      this.itemFrame = frame;
   }

   public EntityItemFrame getItemFrame() {
      return this.itemFrame;
   }

   public int getRepairCost() {
      return this.hasTagCompound() && this.stackTagCompound.hasKey("RepairCost", 3) ? this.stackTagCompound.getInteger("RepairCost") : 0;
   }

   public void setRepairCost(int cost) {
      if (!this.hasTagCompound()) {
         this.stackTagCompound = new NBTTagCompound();
      }

      this.stackTagCompound.setInteger("RepairCost", cost);
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers() {
      Multimap multimap;
      if (this.hasTagCompound() && this.stackTagCompound.hasKey("AttributeModifiers", 9)) {
         multimap = HashMultimap.create();
         NBTTagList nbtTagList = this.stackTagCompound.getTagList("AttributeModifiers", 10);

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTTagCompound nbtTagCompound = nbtTagList.getCompoundTagAt(i);
            AttributeModifier attributeModifier = SharedMonsterAttributes.readAttributeModifierFromNBT(nbtTagCompound);
            if (attributeModifier != null && attributeModifier.getID().getLeastSignificantBits() != 0L && attributeModifier.getID().getMostSignificantBits() != 0L) {
               multimap.put(nbtTagCompound.getString("AttributeName"), attributeModifier);
            }
         }
      } else {
         multimap = this.getItem().getItemAttributeModifiers();
      }

      return multimap;
   }

   public void setItem(Item newItem) {
      this.item = newItem;
   }

   public IChatComponent getChatComponent() {
      ChatComponentText chatComponentText = new ChatComponentText(this.getDisplayName());
      if (this.hasDisplayName()) {
         chatComponentText.getChatStyle().setItalic(true);
      }

      IChatComponent iChatComponent = new ChatComponentText("[").appendSibling(chatComponentText).appendText("]");
      if (this.item != null) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         this.writeToNBT(nbtTagCompound);
         iChatComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(nbtTagCompound.toString())));
         iChatComponent.getChatStyle().setColor(this.getRarity().rarityColor);
      }

      return iChatComponent;
   }

   public boolean canDestroy(Block blockIn) {
      if (blockIn == this.canDestroyCacheBlock) {
         return this.canDestroyCacheResult;
      }

      this.canDestroyCacheBlock = blockIn;
      if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9)) {
         NBTTagList nbtTagList = this.stackTagCompound.getTagList("CanDestroy", 8);

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            Block block = Block.getBlockFromName(nbtTagList.getStringTagAt(i));
            if (block == blockIn) {
               this.canDestroyCacheResult = true;
               return true;
            }
         }
      }

      this.canDestroyCacheResult = false;
      return false;
   }

   public boolean canPlaceOn(Block blockIn) {
      if (blockIn == this.canPlaceOnCacheBlock) {
         return this.canPlaceOnCacheResult;
      }

      this.canPlaceOnCacheBlock = blockIn;
      if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9)) {
         NBTTagList nbtTagList = this.stackTagCompound.getTagList("CanPlaceOn", 8);

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            Block block = Block.getBlockFromName(nbtTagList.getStringTagAt(i));
            if (block == blockIn) {
               this.canPlaceOnCacheResult = true;
               return true;
            }
         }
      }

      this.canPlaceOnCacheResult = false;
      return false;
   }

   public NBTTagCompound getRawTagCompound() {
      return this.stackTagCompound;
   }
}
