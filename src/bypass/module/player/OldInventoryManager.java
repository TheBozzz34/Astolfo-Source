package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.combat.AutoArmor;
import bypass.module.hidden.ScoreboardTracker;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.ArmorSlot;
import bypass.util.ArmorSlots;
import bypass.util.InventoryUtil;
import bypass.util.Stopwatch;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;

public final class OldInventoryManager extends Module {
   private static final MapSetting drop = new MapSettingBuilder("Drop")
      .ordered()
      .description("drop map")
      .add("Armor", new BooleanSetting("Armor", "Drops armor", true))
      .add("Blocks", new BooleanSetting("Blocks", "Drops blocks", false))
      .add("Swords", new BooleanSetting("Swords", "Drops swords", true))
      .add("Tools", new BooleanSetting("Tools", "Drops tools", false))
      .add("Potions", new BooleanSetting("Potions", "Drops potions", false))
      .add("Ores", new BooleanSetting("Ores", "Drops ores", false))
      .add("Heads", new BooleanSetting("Heads", "Drops heads", false))
      .add("Bows", new BooleanSetting("Bows", "Drops bows", false))
      .add("Pearls", new BooleanSetting("Pearls", "Drops pearls", false))
      .add("Food", new BooleanSetting("Food", "Drops food", false))
      .add("Fishing-Rod", new BooleanSetting("Fishing-Rod", "Drops fishing rod", false))
      .add("Enabled", new BooleanSetting("Enabled", "drop items or not", true))
      .add("Enchanted-Books", new BooleanSetting("Enchanted-Books", "Drops enchanted books", false))
      .build();
   private static final MapSetting slots = new MapSettingBuilder("Slots")
      .ordered()
      .description("sltoz")
      .add("Pickaxe-Slot", new NumberSetting<>("Pickaxe-Slot", "Pickaxe", 2, 0, 9))
      .add("Axe-Slot", new NumberSetting<>("Axe-Slot", "Axe", 4, 0, 9))
      .add("Shovel-Slot", new NumberSetting<>("Shovel-Slot", "Shovel", 3, 0, 9))
      .add("Sword-Slot", new NumberSetting<>("Sword-Slot", "Sword", 1, 0, 9))
      .add("Gapple-Slot", new NumberSetting<>("Gapple-Slot", "Gapple", 5, 0, 9))
      .add("Head-Slot", new NumberSetting<>("Head-Slot", "Head", 6, 0, 9))
      .add("Bow-Slot", new NumberSetting<>("Bow-Slot", "Bow", 7, 0, 9))
      .add("Pearl-Slot", new NumberSetting<>("Pearl-Slot", "Gapple", 0, 0, 9))
      .add("Fishing-Rod-Slot", new NumberSetting<>("Fishing-Rod-Slot", "Fishing Rod xD", 0, 0, 9))
      .build();
   public static final MapSetting uhc = new MapSettingBuilder("UHC")
      .ordered()
      .description("uhc")
      .add("Enable", new BooleanSetting("Enable", "UHC mode", false))
      .add("Buckets", new NumberSetting<>("Buckets", "buckets", 1, 0, 10))
      .add("Anvils", new NumberSetting<>("Anvils", "anvils", 1, 0, 10))
      .add("Sticks", new NumberSetting<>("Sticks", "sticks", 16, 0, 101))
      .add("Workbenches", new NumberSetting<>("Workbenches", "sticks", 32, 0, 101))
      .add("Boats", new NumberSetting<>("Boats", "boats", 1, 0, 10))
      .add("Forges", new NumberSetting<>("Forges", "boats", 16, 0, 101))
      .add("Sword", new EnumSetting<>("Sword", "Sword", OldInventoryManagerUHCSwordMode.NONE))
      .build();
   private final MapSetting uhcCrafts = new MapSettingBuilder("UHC-Crafts")
      .ordered()
      .description("uhc")
      .add("Seven-League-Boots", new BooleanSetting("Seven-League-Boots", "", false))
      .add("Eves-Temptation", new BooleanSetting("Eves-Temptation", "", false))
      .add("Fusion-Armor", new BooleanSetting("Fusion-Armor", "", false))
      .add("Enlightening-Pack", new BooleanSetting("Enlightening-Pack", "", false))
      .add("Protection-Book", new BooleanSetting("Protection-Pack", "", false))
      .add("Sharpness-Book", new BooleanSetting("Sharpness-Pack", "", false))
      .build();
   private static final MapSetting ores = new MapSettingBuilder("Ores")
      .ordered()
      .description("ore blocks")
      .add("Diamond", new BooleanSetting("Diamond", "Grabs diamond", true))
      .add("Iron", new BooleanSetting("Iron", "Grabs iron", true))
      .add("Coal", new BooleanSetting("Coal", "Grabs coal", true))
      .add("Emerald", new BooleanSetting("Emerald", "Grabs emerald", false))
      .add("Gold", new BooleanSetting("Gold", "Grabs gold", true))
      .add("Redstone", new BooleanSetting("Redstone", "Grabs redstone", false))
      .add("Lapis", new BooleanSetting("Lapis", "Grabs Lapis", false))
      .build();
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Delay in milliseconds between dropping each item", 250, 0, 1000, "ms");
   private final NumberSetting<Integer> switchDelay = new NumberSetting<>("Switch-Delay", "Delay in milliseconds between switching items", 500, 0, 1000);
   private static final NumberSetting<Integer> minDurability = new NumberSetting<>("Min-Durability", "Maximum of arrows", 25, 0, 100, "%");
   private static final NumberSetting<Integer> blockLimit = new NumberSetting<>("Block-Limit", "Maximum of blocks", 200, 1, 1000);
   private static final NumberSetting<Integer> foodLimit = new NumberSetting<>("Food-Limit", "Maximum of food", 200, 1, 1000);
   private static final NumberSetting<Integer> arrowLimit = new NumberSetting<>("Arrow-Limit", "Maximum of arrows", 200, 1, 1000);
   private final BooleanSetting autoWhitelist = new BooleanSetting("Auto-Whitelist", "Whitelists every item you get when you join", true);
   private final BooleanSetting autoCraft = new BooleanSetting("Auto-Craft", "Crafts logs into planks", false);
   private final BooleanSetting inventory = new BooleanSetting("Inventory", "Only if open inventory", false);
   private final Stopwatch dropTimer = new Stopwatch();
   private final Stopwatch switchTimer = new Stopwatch();
   private static final ArrayList<ItemStack> whitelist = new ArrayList<>();
   public static final List<String> uhcItemNames = Arrays.asList(
      "Andúril",
      "Death's Scythe",
      "aDragon Sword",
      "Dragon Sword",
      "Dragon Armor",
      "Seven-league Boot",
      "Hide of Leviathan",
      "Artemis' Bow",
      "Barbarian Chestplate",
      "Excalibur",
      "Bloodlust",
      "Modular Bow",
      "Exodus"
   );
   public static final List<String> uhcItemLore = Arrays.asList(
      "Resistance I (While Holding)",
      "The Holy Sword",
      "Weaponsmith Ultimate",
      "Oblivion",
      "Aeon",
      "Maelstrom",
      "Gains Sharpness II after 1 kill",
      "Resistance I (While Wearing)"
   );
   private boolean slotsUnique;
   private boolean unused;
   private boolean unused2;
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING && packetEvent.getPacket() instanceof C0EPacketClickWindow) {
         this.switchTimer.reset();
         this.dropTimer.reset();
      }
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (mc.thePlayer.ticksExisted < 30 && this.autoWhitelist.getValue()) {
         for (int i = 0; i < mc.thePlayer.inventoryContainer.inventorySlots.size(); i++) {
            Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(i);
            if (slot != null && slot.getSlotTexture() == null && slot.getHasStack()) {
               ItemStack itemStack = slot.getStack();
               if (itemStack != null) {
                  whitelist.add(itemStack);
               }
            }
         }
      }
   };
   @EventHandler(MotionEvent.class)
   private final Listener<MotionEvent> onMotion = motionEvent -> {
      if (!(mc.currentScreen instanceof GuiContainer) || mc.currentScreen instanceof GuiInventory) {
         if (motionEvent.getState() == EventState.PRE) {
            ItemStack[] itemStacks = getBestItems();
            List list = Arrays.asList(itemStacks);
            int i = slots.getSetting("Pickaxe-Slot").asInt();
            int j = slots.getSetting("Axe-Slot").asInt();
            int k = slots.getSetting("Shovel-Slot").asInt();
            int l = slots.getSetting("Sword-Slot").asInt();
            int m = slots.getSetting("Gapple-Slot").asInt();
            int n = slots.getSetting("Head-Slot").asInt();
            int i2 = slots.getSetting("Bow-Slot").asInt();
            int i3 = slots.getSetting("Pearl-Slot").asInt();
            int i4 = slots.getSetting("Fishing-Rod-Slot").asInt();
            boolean bl = this.slotsUnique = !hasDuplicateSlots(new int[]{i, j, k, l, m, n, i2, i3, i4});
            if (!(mc.currentScreen instanceof GuiInventory) && this.inventory.getValue()) {
               return;
            }

            for (int i5 = 0; i5 < mc.thePlayer.inventoryContainer.inventorySlots.size(); i5++) {
               Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(i5);
               if (slot != null && slot.getSlotTexture() == null && slot.getHasStack()) {
                  ItemStack itemStack = slot.getStack();
                  Item item = itemStack.getItem();
                  if (!whitelist.contains(itemStack)) {
                     if (uhc.getSetting("Enable").asBoolean() && !list.contains(itemStack)) {
                        String string = getLore(itemStack);
                        boolean bl2 = false;

                        for (String string2 : uhcItemLore) {
                           if (string.contains(string2)) {
                              bl2 = true;
                              break;
                           }
                        }

                        if (uhcItemNames.contains(StringUtils.stripControlCodes(itemStack.getDisplayName())) || itemStack.getDisplayName().contains("Andúril") || bl2
                           )
                         {
                           continue;
                        }
                     }

                     if (ScoreboardTracker.isInGame()) {
                        if (itemStack.getItem() instanceof ItemBlock) {
                           ItemBlock itemBlock = (ItemBlock)itemStack.getItem();
                           if (itemBlock.getBlock() == Blocks.ender_chest) {
                              continue;
                           }
                        }

                        if (itemStack.getItem() == Items.compass || itemStack.getItem() == Items.bow || itemStack.getItem() == Items.milk_bucket) {
                           continue;
                        }
                     }

                     if ((slot.slotNumber != this.toContainerSlot(i) || !(item instanceof ItemPickaxe))
                        && (slot.slotNumber != this.toContainerSlot(j) || !(item instanceof ItemAxe))
                        && (slot.slotNumber != this.toContainerSlot(k) || !(item instanceof ItemSpade))
                        && (slot.slotNumber != this.toContainerSlot(l) || !(item instanceof ItemSword))
                        && (slot.slotNumber != this.toContainerSlot(i2) || !(item instanceof ItemBow))
                        && (slot.slotNumber != this.toContainerSlot(i3) || !(item instanceof ItemEnderPearl))
                        && (slot.slotNumber != this.toContainerSlot(i4) || !(item instanceof ItemFishingRod))
                        && (
                           !this.autoWhitelist.getValue()
                              || !(itemStack.getItem() instanceof ItemBook)
                              || !itemStack.getDisplayName().contains(EnumChatFormatting.GREEN.toString())
                              || itemStack.isItemEnchanted()
                        )
                        && (
                           !this.autoWhitelist.getValue()
                              || !itemStack.getDisplayName().contains("§7(")
                              || itemStack.getItem() instanceof ItemArmor
                              || itemStack.getItem() instanceof ItemSword
                        )) {
                        if (itemStack.getItem() instanceof ItemBlock && this.autoCraft.getValue()) {
                           ItemBlock itemBlock2 = (ItemBlock)itemStack.getItem();
                           if (itemBlock2.getBlock() == Blocks.log || itemBlock2.getBlock() == Blocks.log2) {
                              if (!AutoArmor.isInventoryFull() && this.dropTimer.hasElapsed(this.delay.getValue().intValue())) {
                                 mc.playerController.windowClick(0, slot.slotNumber, 0, 0, mc.thePlayer);
                                 mc.playerController.windowClick(0, 1, 0, 0, mc.thePlayer);
                                 mc.playerController.windowClick(0, 0, 0, 1, mc.thePlayer);
                              }
                              continue;
                           }
                        }

                        if (item instanceof ItemArmor) {
                           if (this.shouldDropArmor() && isWorseThanWorn(itemStack)) {
                              this.dropSlot(slot.slotNumber);
                           }
                        } else {
                           boolean bl3 = list.contains(itemStack);
                           if (bl3) {
                              if (bl) {
                                 OldInventoryManagerSlot oldInventoryManagerSlot = getSlotFor(itemStack);
                                 if (oldInventoryManagerSlot != null && this.switchTimer.hasElapsed(this.switchDelay.getValue().intValue())) {
                                    this.moveToSlot(oldInventoryManagerSlot, itemStack, slot.slotNumber);
                                 }
                              }
                           } else if (item instanceof ItemTool) {
                              if (this.shouldDropTools()) {
                                 this.dropSlot(slot.slotNumber);
                              }
                           } else if (!this.shouldKeep(itemStack)) {
                              this.dropSlot(slot.slotNumber);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   };
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.SCREEN && !this.slotsUnique) {
         mc.fontRendererObj
            .drawStringWithShadow(
               "Some InventoryManager slots are the same, make each slot unique for sorting to work",
               renderEvent.getResolution().getScaledWidth() / 2.0F
                  - mc.fontRendererObj.getStringWidth("Some InventoryManager slots are the same, make each slot unique for sorting to work") / 2.0F,
               renderEvent.getResolution().getScaledHeight() - 100,
               Color.RED.getRGB()
            );
      }
   };

   public OldInventoryManager() {
      super(Category.PLAYER);
      this.addSettings(
         drop,
         slots,
         uhc,
         this.uhcCrafts,
         ores,
         minDurability,
         blockLimit,
         foodLimit,
         arrowLimit,
         this.switchDelay,
         this.delay,
         this.autoWhitelist,
         this.autoCraft,
         this.inventory
      );
   }

   @Override
   public void onDisable() {
      whitelist.clear();
      super.onDisable();
   }

   private boolean shouldKeep(ItemStack itemStack) {
      if (!this.isDropEnabled()) {
         return true;
      }

      Item item2 = itemStack.getItem();

      try {
         if (uhc.getSetting("Enable").asBoolean()) {
            if (item2 instanceof ItemBlock) {
               ItemBlock itemBlock = (ItemBlock)item2;
               if (isOreBlock(itemBlock)) {
                  return true;
               }

               if (itemBlock.getBlock() instanceof BlockWorkbench) {
                  int i = uhc.getSetting("Workbenches").asInt();
                  if (i == 0) {
                     return false;
                  }

                  return countWorkbenches() <= i;
               }

               if (itemBlock.getBlock() instanceof BlockFurnace && itemStack.hasEffect()) {
                  int j = uhc.getSetting("Forges").asInt();
                  if (j == 0) {
                     return false;
                  }

                  return countForges() <= j;
               }
            }

            if (item2 == Items.nether_star) {
               return true;
            }

            if (item2 instanceof ItemAnvilBlock) {
               int k = uhc.getSetting("Anvils").asInt();
               if (k == 0) {
                  return false;
               }

               return countItemsOf(ItemAnvilBlock.class) <= k;
            }

            if (item2 instanceof ItemBoat) {
               int l = uhc.getSetting("Boats").asInt();
               if (l == 0) {
                  return false;
               }

               return countItemsOf(ItemBoat.class) <= l;
            }

            if (item2 instanceof ItemAppleGold) {
               return true;
            }

            if (item2 instanceof ItemFood) {
               ItemFood itemFood = (ItemFood)item2;
               if (itemFood.getUnlocalizedName().equals("item.apple")) {
                  return true;
               }
            }

            if (item2 instanceof ItemBucket) {
               int m = uhc.getSetting("Buckets").asInt();
               if (m == 0) {
                  return false;
               }

               return countItemsOf(ItemBucket.class) <= m;
            }

            if (item2 == Items.stick) {
               int n = uhc.getSetting("Sticks").asInt();
               if (n == 0) {
                  return false;
               }

               return countItems(null, Items.stick) <= n;
            }

            if (this.uhcCrafts.getSetting("Seven-League-Boots").asBoolean()) {
               if (item2 instanceof ItemEnderPearl) {
                  return true;
               }

               if (item2 instanceof ItemArmor) {
                  ItemArmor itemArmor = (ItemArmor)item2;
                  if (itemArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.DIAMOND && itemArmor.armorType == 3) {
                     return true;
                  }
               }

               if (item2 == Items.feather) {
                  return true;
               }
            }

            if (this.uhcCrafts.getSetting("Protection-Book").asBoolean()) {
               if (item2 == Items.reeds) {
                  return true;
               }

               if (item2 == Items.iron_ingot) {
                  return true;
               }

               if (item2 == Items.paper) {
                  return true;
               }
            }

            if (this.uhcCrafts.getSetting("Sharpness-Book").asBoolean()) {
               if (item2 == Items.paper) {
                  return true;
               }

               if (item2 == Items.reeds) {
                  return true;
               }

               if (item2 == Items.iron_sword) {
                  return true;
               }

               if (item2 == Items.flint) {
                  return true;
               }
            }

            if (this.uhcCrafts.getSetting("Eves-Temptation").asBoolean()) {
               if (item2 == Items.bone) {
                  return true;
               }

               if (item2 instanceof ItemDye && EnumDyeColor.WHITE == EnumDyeColor.byDyeDamage(itemStack.getMetadata())) {
                  return true;
               }
            }

            if (this.uhcCrafts.getSetting("Fusion-Armor").asBoolean() && item2 instanceof ItemArmor) {
               ItemArmor itemArmor2 = (ItemArmor)item2;
               if (itemArmor2.getArmorMaterial() == ItemArmor.ArmorMaterial.DIAMOND) {
                  return true;
               }
            }

            if (this.uhcCrafts.getSetting("Enlightening-Pack").asBoolean()) {
               if (item2 instanceof ItemGlassBottle) {
                  return true;
               }

               if (item2 instanceof ItemExpBottle) {
                  return true;
               }

               if (item2 instanceof ItemBlock) {
                  ItemBlock itemBlock2 = (ItemBlock)item2;
                  if (itemBlock2.getBlock() == Blocks.redstone_block) {
                     return true;
                  }
               }
            }
         }

         return item2 instanceof ItemBlock && keepBlocks() && isBuildingBlock((ItemBlock)item2)
            || item2 instanceof ItemFood && isSafeFood((ItemFood)item2) && keepFood()
            || item2 instanceof ItemEnderPearl && keepPearls()
            || item2 instanceof ItemEnchantedBook && keepEnchantedBooks()
            || item2 instanceof ItemSkull && keepHeads() && isHead(itemStack)
            || item2 instanceof ItemPotion && isGoodPotion(itemStack) && keepPotions()
            || item2 instanceof ItemSword && keepSwords()
            || isWantedOre(itemStack) && keepOres()
            || item2 == Items.arrow && keepBows() && countItems(null, Items.arrow) <= arrowLimit.getValue();
      } catch (Exception exception) {
         return false;
      }
   }

   private static boolean isOreBlock(ItemBlock itemBlock) {
      return itemBlock.getBlock() == Blocks.iron_ore || itemBlock.getBlock() == Blocks.gold_block || itemBlock.getBlock() == Blocks.gold_ore;
   }

   private static boolean isWantedOre(ItemStack itemStack) {
      return itemStack.getItem() == Items.emerald && ores.getSetting("Emerald").asBoolean()
         || itemStack.getItem() == Items.diamond && ores.getSetting("Diamond").asBoolean()
         || itemStack.getItem() == Items.iron_ingot && ores.getSetting("Iron").asBoolean()
         || itemStack.getItem() == Items.gold_ingot && ores.getSetting("Gold").asBoolean()
         || itemStack.getItem() == Items.gold_nugget && ores.getSetting("Gold").asBoolean()
         || itemStack.getItem() == Items.dye && EnumDyeColor.byDyeDamage(itemStack.getMetadata()) == EnumDyeColor.BLUE && ores.getSetting("Lapis").asBoolean()
         || itemStack.getItem() == Items.coal && ores.getSetting("Coal").asBoolean()
         || itemStack.getItem() == Items.redstone && ores.getSetting("Redstone").asBoolean();
   }

   public static boolean isUseful(ItemStack itemStack) {
      try {
         ItemStack[] itemStacks = getBestItems();
         float f = 0.0F;
         float g = 0.0F;
         float h = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         if (itemStacks[0] != null) {
            f = InventoryUtil.getToolScore(itemStacks[0]);
         }

         if (itemStacks[1] != null) {
            g = InventoryUtil.getToolScore(itemStacks[1]);
         }

         if (itemStacks[2] != null) {
            f2 = InventoryUtil.getToolScore(itemStacks[2]);
         }

         if (itemStacks[4] != null) {
            h = InventoryUtil.getDamageScore(itemStacks[4]);
         }

         if (itemStacks[7] != null) {
            f3 = InventoryUtil.getBowScore(itemStacks[7]);
         }

         Item item = itemStack.getItem();
         return item instanceof ItemBlock && keepBlocks() && isBuildingBlock((ItemBlock)item)
            || item instanceof ItemFood && isSafeFood((ItemFood)item) && keepFood()
            || item instanceof ItemEnderPearl && keepPearls()
            || item instanceof ItemFishingRod && keepFishingRods()
            || item instanceof ItemSkull && keepHeads() && isHead(itemStack)
            || item instanceof ItemPotion && isGoodPotion(itemStack) && keepPotions()
            || item instanceof ItemArmor && !isWorseThanWorn(itemStack)
            || item instanceof ItemPickaxe && InventoryUtil.getToolScore(itemStack) > f
            || item instanceof ItemAxe && InventoryUtil.getToolScore(itemStack) > g
            || item instanceof ItemSpade && InventoryUtil.getToolScore(itemStack) > f2
            || item instanceof ItemSword && InventoryUtil.getDamageScore(itemStack) > h
            || item instanceof ItemBow && keepBows() && InventoryUtil.getBowScore(itemStack) > f3
            || itemStack.getItem() == Items.arrow && keepBows() && countItems(null, Items.arrow) <= arrowLimit.getValue();
      } catch (Exception exception) {
         return false;
      }
   }

   public static boolean isBuildingBlock(ItemBlock itemBlock) {
      Block block = itemBlock.getBlock();
      return block.getMaterial().blocksMovement()
         && block.isOpaqueCube()
         && !block.getMaterial().isLiquid()
         && !(block instanceof BlockFalling)
         && !(block instanceof BlockWorkbench)
         && !(block instanceof BlockContainer);
   }

   private static boolean isWorseThanWorn(ItemStack itemStack) {
      ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
      ArmorSlot armorSlot = ArmorSlots.byArmorType(itemArmor.armorType);
      if (armorSlot == null) {
         return true;
      }

      ItemStack itemStack2 = mc.thePlayer.inventoryContainer.getSlot(armorSlot.slot).getStack();
      int i = InventoryUtil.getArmorScore(itemStack);
      int j = InventoryUtil.getArmorScore(itemStack2);
      return j > i || j == i && (itemStack2.hasEffect() || !itemStack.hasEffect());
   }

   private static ItemStack[] getBestItems() {
      ItemStack itemStack = null;
      ItemStack itemStack2 = null;
      ItemStack itemStack3 = null;
      ItemStack itemStack4 = null;
      ItemStack itemStack5 = null;
      ItemStack itemStack6 = null;
      ItemStack itemStack7 = null;
      ItemStack itemStack8 = null;
      ItemStack itemStack9 = null;
      ItemStack itemStack10 = null;
      float f = 0.0F;
      float g = 0.0F;
      float h = 0.0F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      int i = 0;
      int j = 0;
      int k = 0;
      int l = 0;
      int m = 0;
      ArrayList arrayList = null;

      for (Slot slot : mc.thePlayer.inventoryContainer.inventorySlots) {
         if (slot != null && slot.getHasStack()) {
            ItemStack itemStack11 = slot.getStack();
            Item item = itemStack11.getItem();
            if (itemStack11.isItemDamaged() && minDurability.getValue() > 0) {
               int n = itemStack11.getItemDamage() * 100 / itemStack11.getMaxDamage();
               int i2 = 100 - n;
               if (i2 < minDurability.getValue()) {
                  if (arrayList == null) {
                     arrayList = new ArrayList();
                  }

                  arrayList.add(itemStack11);
                  continue;
               }
            }

            if (item instanceof ItemPickaxe) {
               int i3 = slots.getSetting("Pickaxe-Slot").asInt();
               float f4 = InventoryUtil.getToolScore(itemStack11);
               if (i3 != 0) {
                  ItemStack itemStack12 = mc.thePlayer.inventory.getStackInSlot(i3 - 1);
                  if (itemStack12 != null) {
                     float f5 = InventoryUtil.getToolScore(itemStack12);
                     if (f5 == f4 && f < f4 && itemStack11 != itemStack12 && itemStack12.getItem() instanceof ItemPickaxe) {
                        f = f5;
                        itemStack = itemStack12;
                        continue;
                     }
                  }
               }

               if (f4 > f) {
                  f = f4;
                  itemStack = itemStack11;
               }
            } else if (item instanceof ItemAxe) {
               int i4 = slots.getSetting("Axe-Slot").asInt();
               float f6 = InventoryUtil.getToolScore(itemStack11);
               if (i4 != 0) {
                  ItemStack itemStack13 = mc.thePlayer.inventory.getStackInSlot(i4 - 1);
                  if (itemStack13 != null) {
                     float f7 = InventoryUtil.getToolScore(itemStack13);
                     if (f7 == f6 && g < f6 && itemStack11 != itemStack13 && itemStack13.getItem() instanceof ItemAxe) {
                        g = f7;
                        itemStack2 = itemStack13;
                        continue;
                     }
                  }
               }

               if (f6 > g) {
                  g = f6;
                  itemStack2 = itemStack11;
               }
            } else if (item instanceof ItemSpade) {
               int i5 = slots.getSetting("Shovel-Slot").asInt();
               float f8 = InventoryUtil.getToolScore(itemStack11);
               if (i5 != 0) {
                  ItemStack itemStack14 = mc.thePlayer.inventory.getStackInSlot(i5 - 1);
                  if (itemStack14 != null) {
                     float f9 = InventoryUtil.getToolScore(itemStack14);
                     if (f9 == f8 && h < f8 && itemStack11 != itemStack14 && itemStack14.getItem() instanceof ItemSpade) {
                        h = f9;
                        itemStack3 = itemStack14;
                        continue;
                     }
                  }
               }

               if (f8 > h) {
                  h = f8;
                  itemStack3 = itemStack11;
               }
            } else if (item instanceof ItemShears) {
               int i6 = itemStack11.getItemDamage() + (itemStack11.hasEffect() ? 50 : 0);
               if (i6 >= i) {
                  i = i6;
                  itemStack4 = itemStack11;
               }
            } else if (item instanceof ItemFishingRod && keepFishingRods()) {
               int i7 = itemStack11.getItemDamage() + (itemStack11.hasEffect() ? 50 : 0);
               if (i7 >= m) {
                  m = i7;
                  itemStack10 = itemStack11;
               }
            } else if (!(item instanceof ItemSword)) {
               if (item instanceof ItemAppleGold) {
                  int i8 = slots.getSetting("Gapple-Slot").asInt();
                  if (i8 != 0) {
                     ItemStack itemStack15 = mc.thePlayer.inventory.getStackInSlot(i8 - 1);
                     if (itemStack15 != null) {
                        int i9 = itemStack11.stackSize;
                        if (i9 == itemStack15.stackSize && j < i9 && itemStack11 != itemStack15 && itemStack15.getItem() instanceof ItemAppleGold) {
                           j = i9;
                           itemStack6 = itemStack15;
                           continue;
                        }
                     }
                  }

                  if (itemStack11.stackSize > j) {
                     j = itemStack11.stackSize;
                     itemStack6 = itemStack11;
                  }
               } else if (item instanceof ItemEnderPearl && keepPearls()) {
                  int i10 = slots.getSetting("Pearl-Slot").asInt();
                  if (i10 != 0) {
                     ItemStack itemStack16 = mc.thePlayer.inventory.getStackInSlot(i10 - 1);
                     if (itemStack16 != null) {
                        int i11 = itemStack11.stackSize;
                        if (i11 == itemStack16.stackSize && l < i11 && itemStack11 != itemStack16 && itemStack16.getItem() instanceof ItemEnderPearl) {
                           l = i11;
                           itemStack9 = itemStack16;
                           continue;
                        }
                     }
                  }

                  if (itemStack11.stackSize > l) {
                     l = itemStack11.stackSize;
                     itemStack9 = itemStack11;
                  }
               } else if (item instanceof ItemSkull && isHead(itemStack11) && keepHeads()) {
                  int i12 = slots.getSetting("Head-Slot").asInt();
                  if (i12 != 0) {
                     ItemStack itemStack17 = mc.thePlayer.inventory.getStackInSlot(i12 - 1);
                     if (itemStack17 != null) {
                        int i13 = itemStack11.stackSize;
                        if (i13 == itemStack17.stackSize && k < i13 && itemStack11 != itemStack17 && itemStack17.getItem() instanceof ItemSkull && isHead(itemStack17)) {
                           k = i13;
                           itemStack7 = itemStack17;
                           continue;
                        }
                     }
                  }

                  if (itemStack11.stackSize > k) {
                     k = itemStack11.stackSize;
                     itemStack7 = itemStack11;
                  }
               } else if (item instanceof ItemBow && keepBows()) {
                  int i14 = slots.getSetting("Bow-Slot").asInt();
                  float f10 = InventoryUtil.getBowScore(itemStack11);
                  if (i14 != 0) {
                     ItemStack itemStack18 = mc.thePlayer.inventory.getStackInSlot(i14 - 1);
                     if (itemStack18 != null) {
                        float f11 = InventoryUtil.getBowScore(itemStack18);
                        if (f11 == f10 && f3 < f10 && itemStack11 != itemStack18 && itemStack18.getItem() instanceof ItemBow) {
                           f3 = f11;
                           itemStack8 = itemStack18;
                           continue;
                        }
                     }
                  }

                  if (f10 > f3) {
                     f3 = f10;
                     itemStack8 = itemStack11;
                  }
               }
            } else {
               float f12 = InventoryUtil.getDamageScore(itemStack11);
               OldInventoryManagerUHCSwordMode oldInventoryManagerUHCSwordMode = (OldInventoryManagerUHCSwordMode)uhc.getSetting("Sword").getValue();
               if (uhc.getSetting("Enable").asBoolean()) {
                  boolean bl = oldInventoryManagerUHCSwordMode == OldInventoryManagerUHCSwordMode.DRAGON_SWORD && isDragonSword(itemStack11)
                     || oldInventoryManagerUHCSwordMode == OldInventoryManagerUHCSwordMode.BLOODLUST && isBloodlust(itemStack11);
                  if (bl) {
                     f12 = 999.0F;
                  }
               }

               int i15 = slots.getSetting("Sword-Slot").asInt();
               if (i15 != 0) {
                  ItemStack itemStack19 = mc.thePlayer.inventory.getStackInSlot(i15 - 1);
                  if (itemStack19 != null) {
                     boolean bl2 = uhc.getSetting("Enable").asBoolean()
                        && (
                           oldInventoryManagerUHCSwordMode == OldInventoryManagerUHCSwordMode.DRAGON_SWORD && isDragonSword(itemStack19)
                              || oldInventoryManagerUHCSwordMode == OldInventoryManagerUHCSwordMode.BLOODLUST && isBloodlust(itemStack19)
                        );
                     float f13 = InventoryUtil.getDamageScore(itemStack19);
                     if (bl2) {
                        f13 = 999.0F;
                     }

                     if (f13 == f12 && f2 < f13 && itemStack11 != itemStack19 && itemStack19.getItem() instanceof ItemSword) {
                        f2 = f13;
                        itemStack5 = itemStack19;
                        continue;
                     }
                  }
               }

               if (f12 > f2) {
                  f2 = f12;
                  itemStack5 = itemStack11;
               }
            }
         }
      }

      if (arrayList != null) {
         int i16 = 9999;
         int i17 = 9999;
         int i18 = 9999;
         int i19 = 9999;
         ItemStack itemStack20 = null;
         ItemStack itemStack21 = null;
         ItemStack itemStack22 = null;
         ItemStack itemStack23 = null;

         for (Object object : arrayList) {
            ItemStack itemStack24 = (ItemStack)object;
            if (itemStack24.getItem() instanceof ItemPickaxe && itemStack == null && i16 > itemStack24.getItemDamage()) {
               itemStack20 = itemStack24;
               i16 = itemStack24.getItemDamage();
            }

            if (itemStack24.getItem() instanceof ItemAxe && itemStack2 == null && i17 > itemStack24.getItemDamage()) {
               itemStack21 = itemStack24;
               i17 = itemStack24.getItemDamage();
            }

            if (itemStack24.getItem() instanceof ItemSpade && itemStack3 == null && i18 > itemStack24.getItemDamage()) {
               itemStack22 = itemStack24;
               i18 = itemStack24.getItemDamage();
            }

            if (itemStack24.getItem() instanceof ItemSword && itemStack5 == null && i19 > itemStack24.getItemDamage()) {
               itemStack23 = itemStack24;
               i19 = itemStack24.getItemDamage();
            }
         }

         if (itemStack20 != null) {
            itemStack = itemStack20;
         }

         if (itemStack21 != null) {
            itemStack2 = itemStack21;
         }

         if (itemStack22 != null) {
            itemStack3 = itemStack22;
         }

         if (itemStack23 != null) {
            itemStack5 = itemStack23;
         }
      }

      return new ItemStack[]{itemStack, itemStack2, itemStack3, itemStack4, itemStack5, itemStack6, itemStack7, itemStack8, itemStack9, itemStack10};
   }

   private int toContainerSlot(int i) {
      return i == 0 ? -999 : 35 + i;
   }

   private static boolean isGoodPotion(ItemStack itemStack) {
      int i = 0;
      int j = 0;
      if (itemStack == null) {
         return false;
      }

      for (PotionEffect potionEffect : Items.potionitem.getEffects(itemStack)) {
         if (potionEffect != null) {
            Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
            if (potion != null) {
               if (potion.isBadEffect()) {
                  i++;
               } else {
                  j++;
               }
            }
         }
      }

      return i < j;
   }

   private boolean shouldDropArmor() {
      return this.isDropEnabled() && drop.getSetting("Armor").asBoolean();
   }

   private static boolean keepFood() {
      return !drop.getSetting("Food").asBoolean() && countItemsOf(ItemFood.class) <= foodLimit.getValue();
   }

   private static boolean keepPearls() {
      return !drop.getSetting("Pearls").asBoolean();
   }

   private static boolean keepBlocks() {
      return !drop.getSetting("Blocks").asBoolean() && countItemsOf(ItemBlock.class) <= blockLimit.getValue();
   }

   private static boolean keepHeads() {
      return !drop.getSetting("Heads").asBoolean();
   }

   private static boolean keepBows() {
      return !drop.getSetting("Bows").asBoolean();
   }

   private static boolean keepPotions() {
      return !drop.getSetting("Potions").asBoolean();
   }

   private static boolean keepSwords() {
      return !drop.getSetting("Swords").asBoolean();
   }

   private static boolean keepFishingRods() {
      return !drop.getSetting("Fishing-Rod").asBoolean();
   }

   private static boolean keepEnchantedBooks() {
      return !drop.getSetting("Enchanted-Books").asBoolean();
   }

   private static boolean keepOres() {
      return !drop.getSetting("Ores").asBoolean();
   }

   private boolean shouldDropTools() {
      return this.isDropEnabled() && drop.getSetting("Tools").asBoolean();
   }

   private boolean isDropEnabled() {
      return drop.getSetting("Enabled").asBoolean();
   }

   private void dropSlot(int i) {
      if (this.dropTimer.hasElapsed(this.delay.getValue().intValue())) {
         InventoryUtil.dropSlot(i, 0);
      }
   }

   public static int countItems(Class<?> class2, Object object) {
      int i = 0;

      for (int j = 0; j < mc.thePlayer.inventoryContainer.inventorySlots.size(); j++) {
         Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(j);
         if (slot != null && slot.getSlotTexture() == null && slot.getHasStack()) {
            ItemStack itemStack = slot.getStack();
            if ((!(itemStack.getItem() instanceof ItemBlock) || !isOreBlock((ItemBlock)itemStack.getItem()))
               && (class2 != null && class2.isInstance(itemStack.getItem()) || object == itemStack.getItem())) {
               i += itemStack.stackSize;
            }
         }
      }

      return i;
   }

   private static int countWorkbenches() {
      int i = 0;

      for (int j = 0; j < mc.thePlayer.inventoryContainer.inventorySlots.size(); j++) {
         Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(j);
         if (slot != null && slot.getSlotTexture() == null && slot.getHasStack()) {
            ItemStack itemStack = slot.getStack();
            if (itemStack.getItem() instanceof ItemBlock && ((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockWorkbench) {
               i += itemStack.stackSize;
            }
         }
      }

      return i;
   }

   private static int countForges() {
      int i = 0;

      for (int j = 0; j < mc.thePlayer.inventoryContainer.inventorySlots.size(); j++) {
         Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(j);
         if (slot != null && slot.getSlotTexture() == null && slot.getHasStack()) {
            ItemStack itemStack = slot.getStack();
            if (itemStack.getItem() instanceof ItemBlock && ((ItemBlock)itemStack.getItem()).getBlock() instanceof BlockFurnace && itemStack.hasEffect()) {
               i += itemStack.stackSize;
            }
         }
      }

      return i;
   }

   private static int countItemsOf(Class<?> class2) {
      return countItems(class2, null);
   }

   private static boolean isSafeFood(ItemFood itemFood) {
      return itemFood.getPotionId() != Potion.hunger.id && itemFood.getPotionId() != Potion.poison.id;
   }

   public static boolean isHead(ItemStack itemStack) {
      return itemStack.getMetadata() == 3 && (itemStack.getDisplayName() == null || !itemStack.getDisplayName().contains("Backpack"));
   }

   private void moveToSlot(OldInventoryManagerSlot oldInventoryManagerSlot, ItemStack itemStack, int i) {
      int j = slots.getSetting(oldInventoryManagerSlot.getSettingName()).asInt();
      if (j > 0) {
         int k = j - 1;
         ItemStack itemStack2 = mc.thePlayer.inventory.getStackInSlot(k);
         if (itemStack2 != itemStack) {
            this.swapToHotbar(i, k);
         }
      }
   }

   private void swapToHotbar(int i, int j) {
      mc.playerController.windowClick(0, i, j, 2, mc.thePlayer);
   }

   public static boolean hasDuplicateSlots(int[] ints) {
      BitSet bitSet = new BitSet(11);
      bitSet.set(0, 10, false);

      for (int i : ints) {
         if (i != 0) {
            if (bitSet.get(i)) {
               return true;
            }

            bitSet.set(i, true);
         }
      }

      return false;
   }

   private static OldInventoryManagerSlot getSlotFor(ItemStack itemStack) {
      Item item = itemStack.getItem();
      if (item instanceof ItemSpade) {
         return OldInventoryManagerSlot.SHOVEL;
      } else if (item instanceof ItemPickaxe) {
         return OldInventoryManagerSlot.PICKAXE;
      } else if (item instanceof ItemAxe) {
         return OldInventoryManagerSlot.AXE;
      } else if (item instanceof ItemSword) {
         return OldInventoryManagerSlot.SWORD;
      } else if (item instanceof ItemAppleGold) {
         return OldInventoryManagerSlot.GAPPLE;
      } else if (item instanceof ItemSkull && isHead(itemStack)) {
         return OldInventoryManagerSlot.HEAD;
      } else if (item instanceof ItemBow) {
         return OldInventoryManagerSlot.BOW;
      } else if (item instanceof ItemEnderPearl) {
         return OldInventoryManagerSlot.PEARL;
      } else {
         return item instanceof ItemFishingRod ? OldInventoryManagerSlot.FISHING_ROD : null;
      }
   }

   public static String getLore(ItemStack itemStack) {
      StringBuilder stringBuilder = new StringBuilder();
      if (itemStack.getRawTagCompound() != null && itemStack.getRawTagCompound().hasKey("display", 10)) {
         NBTTagCompound nbtTagCompound = itemStack.getRawTagCompound().getCompoundTag("display");
         NBTTagList nbtTagList = nbtTagCompound.getTagList("Lore", 8);
         if (nbtTagList.tagCount() > 0) {
            for (int i = 0; i < nbtTagList.tagCount(); i++) {
               stringBuilder.append(nbtTagList.getStringTagAt(i));
            }
         }
      }

      return stringBuilder.toString();
   }

   public static boolean isDragonSword(ItemStack itemStack) {
      String string = getLore(itemStack);

      for (String string2 : uhcItemLore) {
         if (string.contains(string2) && string2.equals("Weaponsmith Ultimate")) {
            return true;
         }
      }

      return false;
   }

   public static boolean isBloodlust(ItemStack itemStack) {
      String string = getLore(itemStack);

      for (String string2 : uhcItemLore) {
         if (string.contains(string2) && string2.contains("Gains Sharpness II after 1 kill")) {
            return true;
         }
      }

      return false;
   }
}
