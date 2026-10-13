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
   private final Listener<PacketEvent> onPacket = var1 -> {
      if (var1.getDirection() == PacketDirection.OUTGOING && var1.getPacket() instanceof C0EPacketClickWindow) {
         this.switchTimer.reset();
         this.dropTimer.reset();
      }
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1 -> {
      if (mc.thePlayer.ticksExisted < 30 && this.autoWhitelist.getValue()) {
         for (int var5 = 0; var5 < mc.thePlayer.inventoryContainer.inventorySlots.size(); var5++) {
            Slot var6 = mc.thePlayer.inventoryContainer.inventorySlots.get(var5);
            if (var6 != null && var6.getSlotTexture() == null && var6.getHasStack()) {
               ItemStack var7 = var6.getStack();
               if (var7 != null) {
                  whitelist.add(var7);
               }
            }
         }
      }
   };
   @EventHandler(MotionEvent.class)
   private final Listener<MotionEvent> onMotion = var1 -> {
      if (!(mc.currentScreen instanceof GuiContainer) || mc.currentScreen instanceof GuiInventory) {
         if (var1.getState() == EventState.PRE) {
            ItemStack[] var5 = getBestItems();
            List var6 = Arrays.asList(var5);
            int var7 = slots.getSetting("Pickaxe-Slot").asInt();
            int var8 = slots.getSetting("Axe-Slot").asInt();
            int var9 = slots.getSetting("Shovel-Slot").asInt();
            int var10 = slots.getSetting("Sword-Slot").asInt();
            int var11 = slots.getSetting("Gapple-Slot").asInt();
            int var12 = slots.getSetting("Head-Slot").asInt();
            int var13 = slots.getSetting("Bow-Slot").asInt();
            int var14 = slots.getSetting("Pearl-Slot").asInt();
            int var15 = slots.getSetting("Fishing-Rod-Slot").asInt();
            boolean var16 = this.slotsUnique = !hasDuplicateSlots(new int[]{var7, var8, var9, var10, var11, var12, var13, var14, var15});
            if (!(mc.currentScreen instanceof GuiInventory) && this.inventory.getValue()) {
               return;
            }

            for (int var17 = 0; var17 < mc.thePlayer.inventoryContainer.inventorySlots.size(); var17++) {
               Slot var18 = mc.thePlayer.inventoryContainer.inventorySlots.get(var17);
               if (var18 != null && var18.getSlotTexture() == null && var18.getHasStack()) {
                  ItemStack var19 = var18.getStack();
                  Item var20 = var19.getItem();
                  if (!whitelist.contains(var19)) {
                     if (uhc.getSetting("Enable").asBoolean() && !var6.contains(var19)) {
                        String var21 = getLore(var19);
                        boolean var22 = false;

                        for (String var24 : uhcItemLore) {
                           if (var21.contains(var24)) {
                              var22 = true;
                              break;
                           }
                        }

                        if (uhcItemNames.contains(StringUtils.stripControlCodes(var19.getDisplayName())) || var19.getDisplayName().contains("Andúril") || var22
                           )
                         {
                           continue;
                        }
                     }

                     if (ScoreboardTracker.r()) {
                        if (var19.getItem() instanceof ItemBlock) {
                           ItemBlock var25 = (ItemBlock)var19.getItem();
                           if (var25.getBlock() == Blocks.ender_chest) {
                              continue;
                           }
                        }

                        if (var19.getItem() == Items.compass || var19.getItem() == Items.bow || var19.getItem() == Items.milk_bucket) {
                           continue;
                        }
                     }

                     if ((var18.slotNumber != this.toContainerSlot(var7) || !(var20 instanceof ItemPickaxe))
                        && (var18.slotNumber != this.toContainerSlot(var8) || !(var20 instanceof ItemAxe))
                        && (var18.slotNumber != this.toContainerSlot(var9) || !(var20 instanceof ItemSpade))
                        && (var18.slotNumber != this.toContainerSlot(var10) || !(var20 instanceof ItemSword))
                        && (var18.slotNumber != this.toContainerSlot(var13) || !(var20 instanceof ItemBow))
                        && (var18.slotNumber != this.toContainerSlot(var14) || !(var20 instanceof ItemEnderPearl))
                        && (var18.slotNumber != this.toContainerSlot(var15) || !(var20 instanceof ItemFishingRod))
                        && (
                           !this.autoWhitelist.getValue()
                              || !(var19.getItem() instanceof ItemBook)
                              || !var19.getDisplayName().contains(EnumChatFormatting.GREEN.toString())
                              || var19.isItemEnchanted()
                        )
                        && (
                           !this.autoWhitelist.getValue()
                              || !var19.getDisplayName().contains("§7(")
                              || var19.getItem() instanceof ItemArmor
                              || var19.getItem() instanceof ItemSword
                        )) {
                        if (var19.getItem() instanceof ItemBlock && this.autoCraft.getValue()) {
                           ItemBlock var26 = (ItemBlock)var19.getItem();
                           if (var26.getBlock() == Blocks.log || var26.getBlock() == Blocks.log2) {
                              if (!AutoArmor.c() && this.dropTimer.hasElapsed(this.delay.getValue().intValue())) {
                                 mc.playerController.windowClick(0, var18.slotNumber, 0, 0, mc.thePlayer);
                                 mc.playerController.windowClick(0, 1, 0, 0, mc.thePlayer);
                                 mc.playerController.windowClick(0, 0, 0, 1, mc.thePlayer);
                              }
                              continue;
                           }
                        }

                        if (var20 instanceof ItemArmor) {
                           if (this.shouldDropArmor() && isWorseThanWorn(var19)) {
                              this.dropSlot(var18.slotNumber);
                           }
                        } else {
                           boolean var27 = var6.contains(var19);
                           if (var27) {
                              if (var16) {
                                 OldInventoryManagerSlot var28 = getSlotFor(var19);
                                 if (var28 != null && this.switchTimer.hasElapsed(this.switchDelay.getValue().intValue())) {
                                    this.moveToSlot(var28, var19, var18.slotNumber);
                                 }
                              }
                           } else if (var20 instanceof ItemTool) {
                              if (this.shouldDropTools()) {
                                 this.dropSlot(var18.slotNumber);
                              }
                           } else if (!this.shouldKeep(var19)) {
                              this.dropSlot(var18.slotNumber);
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
   private final Listener<RenderEvent> onRender = var1 -> {
      if (var1.getType() == RenderType.SCREEN && !this.slotsUnique) {
         mc.fontRendererObj
            .drawStringWithShadow(
               "Some InventoryManager slots are the same, make each slot unique for sorting to work",
               var1.getResolution().getScaledWidth() / 2.0F
                  - mc.fontRendererObj.getStringWidth("Some InventoryManager slots are the same, make each slot unique for sorting to work") / 2.0F,
               var1.getResolution().getScaledHeight() - 100,
               Color.RED.getRGB()
            );
      }
   };
   private static final long u = 94215075344745L;

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

   private boolean shouldKeep(ItemStack var1) {
      if (!this.isDropEnabled()) {
         return true;
      }

      Item var5 = var1.getItem();

      try {
         if (uhc.getSetting("Enable").asBoolean()) {
            if (var5 instanceof ItemBlock) {
               ItemBlock var6 = (ItemBlock)var5;
               if (isOreBlock(var6)) {
                  return true;
               }

               if (var6.getBlock() instanceof BlockWorkbench) {
                  int var17 = uhc.getSetting("Workbenches").asInt();
                  if (var17 == 0) {
                     return false;
                  }

                  return countWorkbenches() <= var17;
               }

               if (var6.getBlock() instanceof BlockFurnace && var1.hasEffect()) {
                  int var7 = uhc.getSetting("Forges").asInt();
                  if (var7 == 0) {
                     return false;
                  }

                  return countForges() <= var7;
               }
            }

            if (var5 == Items.nether_star) {
               return true;
            }

            if (var5 instanceof ItemAnvilBlock) {
               int var16 = uhc.getSetting("Anvils").asInt();
               if (var16 == 0) {
                  return false;
               }

               return countItemsOf(ItemAnvilBlock.class) <= var16;
            }

            if (var5 instanceof ItemBoat) {
               int var15 = uhc.getSetting("Boats").asInt();
               if (var15 == 0) {
                  return false;
               }

               return countItemsOf(ItemBoat.class) <= var15;
            }

            if (var5 instanceof ItemAppleGold) {
               return true;
            }

            if (var5 instanceof ItemFood) {
               ItemFood var9 = (ItemFood)var5;
               if (var9.getUnlocalizedName().equals("item.apple")) {
                  return true;
               }
            }

            if (var5 instanceof ItemBucket) {
               int var14 = uhc.getSetting("Buckets").asInt();
               if (var14 == 0) {
                  return false;
               }

               return countItemsOf(ItemBucket.class) <= var14;
            }

            if (var5 == Items.stick) {
               int var13 = uhc.getSetting("Sticks").asInt();
               if (var13 == 0) {
                  return false;
               }

               return countItems(null, Items.stick) <= var13;
            }

            if (this.uhcCrafts.getSetting("Seven-League-Boots").asBoolean()) {
               if (var5 instanceof ItemEnderPearl) {
                  return true;
               }

               if (var5 instanceof ItemArmor) {
                  ItemArmor var10 = (ItemArmor)var5;
                  if (var10.getArmorMaterial() == ItemArmor.ArmorMaterial.DIAMOND && var10.armorType == 3) {
                     return true;
                  }
               }

               if (var5 == Items.feather) {
                  return true;
               }
            }

            if (this.uhcCrafts.getSetting("Protection-Book").asBoolean()) {
               if (var5 == Items.reeds) {
                  return true;
               }

               if (var5 == Items.iron_ingot) {
                  return true;
               }

               if (var5 == Items.paper) {
                  return true;
               }
            }

            if (this.uhcCrafts.getSetting("Sharpness-Book").asBoolean()) {
               if (var5 == Items.paper) {
                  return true;
               }

               if (var5 == Items.reeds) {
                  return true;
               }

               if (var5 == Items.iron_sword) {
                  return true;
               }

               if (var5 == Items.flint) {
                  return true;
               }
            }

            if (this.uhcCrafts.getSetting("Eves-Temptation").asBoolean()) {
               if (var5 == Items.bone) {
                  return true;
               }

               if (var5 instanceof ItemDye && EnumDyeColor.WHITE == EnumDyeColor.byDyeDamage(var1.getMetadata())) {
                  return true;
               }
            }

            if (this.uhcCrafts.getSetting("Fusion-Armor").asBoolean() && var5 instanceof ItemArmor) {
               ItemArmor var11 = (ItemArmor)var5;
               if (var11.getArmorMaterial() == ItemArmor.ArmorMaterial.DIAMOND) {
                  return true;
               }
            }

            if (this.uhcCrafts.getSetting("Enlightening-Pack").asBoolean()) {
               if (var5 instanceof ItemGlassBottle) {
                  return true;
               }

               if (var5 instanceof ItemExpBottle) {
                  return true;
               }

               if (var5 instanceof ItemBlock) {
                  ItemBlock var12 = (ItemBlock)var5;
                  if (var12.getBlock() == Blocks.redstone_block) {
                     return true;
                  }
               }
            }
         }

         return var5 instanceof ItemBlock && keepBlocks() && isBuildingBlock((ItemBlock)var5)
            || var5 instanceof ItemFood && isSafeFood((ItemFood)var5) && keepFood()
            || var5 instanceof ItemEnderPearl && keepPearls()
            || var5 instanceof ItemEnchantedBook && keepEnchantedBooks()
            || var5 instanceof ItemSkull && keepHeads() && isHead(var1)
            || var5 instanceof ItemPotion && isGoodPotion(var1) && keepPotions()
            || var5 instanceof ItemSword && keepSwords()
            || isWantedOre(var1) && keepOres()
            || var5 == Items.arrow && keepBows() && countItems(null, Items.arrow) <= arrowLimit.getValue();
      } catch (Exception var8) {
         return false;
      }
   }

   private static boolean isOreBlock(ItemBlock var0) {
      return var0.getBlock() == Blocks.iron_ore || var0.getBlock() == Blocks.gold_block || var0.getBlock() == Blocks.gold_ore;
   }

   private static boolean isWantedOre(ItemStack var0) {
      return var0.getItem() == Items.emerald && ores.getSetting("Emerald").asBoolean()
         || var0.getItem() == Items.diamond && ores.getSetting("Diamond").asBoolean()
         || var0.getItem() == Items.iron_ingot && ores.getSetting("Iron").asBoolean()
         || var0.getItem() == Items.gold_ingot && ores.getSetting("Gold").asBoolean()
         || var0.getItem() == Items.gold_nugget && ores.getSetting("Gold").asBoolean()
         || var0.getItem() == Items.dye && EnumDyeColor.byDyeDamage(var0.getMetadata()) == EnumDyeColor.BLUE && ores.getSetting("Lapis").asBoolean()
         || var0.getItem() == Items.coal && ores.getSetting("Coal").asBoolean()
         || var0.getItem() == Items.redstone && ores.getSetting("Redstone").asBoolean();
   }

   public static boolean isUseful(ItemStack var0) {
      try {
         ItemStack[] var4 = getBestItems();
         float var5 = 0.0F;
         float var6 = 0.0F;
         float var7 = 0.0F;
         float var8 = 0.0F;
         float var9 = 0.0F;
         if (var4[0] != null) {
            var5 = InventoryUtil.getToolScore(var4[0]);
         }

         if (var4[1] != null) {
            var6 = InventoryUtil.getToolScore(var4[1]);
         }

         if (var4[2] != null) {
            var8 = InventoryUtil.getToolScore(var4[2]);
         }

         if (var4[4] != null) {
            var7 = InventoryUtil.getDamageScore(var4[4]);
         }

         if (var4[7] != null) {
            var9 = InventoryUtil.getBowScore(var4[7]);
         }

         Item var10 = var0.getItem();
         return var10 instanceof ItemBlock && keepBlocks() && isBuildingBlock((ItemBlock)var10)
            || var10 instanceof ItemFood && isSafeFood((ItemFood)var10) && keepFood()
            || var10 instanceof ItemEnderPearl && keepPearls()
            || var10 instanceof ItemFishingRod && keepFishingRods()
            || var10 instanceof ItemSkull && keepHeads() && isHead(var0)
            || var10 instanceof ItemPotion && isGoodPotion(var0) && keepPotions()
            || var10 instanceof ItemArmor && !isWorseThanWorn(var0)
            || var10 instanceof ItemPickaxe && InventoryUtil.getToolScore(var0) > var5
            || var10 instanceof ItemAxe && InventoryUtil.getToolScore(var0) > var6
            || var10 instanceof ItemSpade && InventoryUtil.getToolScore(var0) > var8
            || var10 instanceof ItemSword && InventoryUtil.getDamageScore(var0) > var7
            || var10 instanceof ItemBow && keepBows() && InventoryUtil.getBowScore(var0) > var9
            || var0.getItem() == Items.arrow && keepBows() && countItems(null, Items.arrow) <= arrowLimit.getValue();
      } catch (Exception var11) {
         return false;
      }
   }

   public static boolean isBuildingBlock(ItemBlock var0) {
      Block var4 = var0.getBlock();
      return var4.getMaterial().blocksMovement()
         && var4.isOpaqueCube()
         && !var4.getMaterial().isLiquid()
         && !(var4 instanceof BlockFalling)
         && !(var4 instanceof BlockWorkbench)
         && !(var4 instanceof BlockContainer);
   }

   private static boolean isWorseThanWorn(ItemStack var0) {
      ItemArmor var4 = (ItemArmor)var0.getItem();
      ArmorSlot var5 = ArmorSlots.byArmorType(var4.armorType);
      if (var5 == null) {
         return true;
      }

      ItemStack var6 = mc.thePlayer.inventoryContainer.getSlot(var5.slot).getStack();
      int var7 = InventoryUtil.getArmorScore(var0);
      int var8 = InventoryUtil.getArmorScore(var6);
      return var8 > var7 || var8 == var7 && (var6.hasEffect() || !var0.hasEffect());
   }

   private static ItemStack[] getBestItems() {
      ItemStack var3 = null;
      ItemStack var4 = null;
      ItemStack var5 = null;
      ItemStack var6 = null;
      ItemStack var7 = null;
      ItemStack var8 = null;
      ItemStack var9 = null;
      ItemStack var10 = null;
      ItemStack var11 = null;
      ItemStack var12 = null;
      float var13 = 0.0F;
      float var14 = 0.0F;
      float var15 = 0.0F;
      float var16 = 0.0F;
      float var17 = 0.0F;
      int var18 = 0;
      int var19 = 0;
      int var20 = 0;
      int var21 = 0;
      int var22 = 0;
      ArrayList var23 = null;

      for (Slot var26 : mc.thePlayer.inventoryContainer.inventorySlots) {
         if (var26 != null && var26.getHasStack()) {
            ItemStack var27 = var26.getStack();
            Item var28 = var27.getItem();
            if (var27.isItemDamaged() && minDurability.getValue() > 0) {
               int var29 = var27.getItemDamage() * 100 / var27.getMaxDamage();
               int var30 = 100 - var29;
               if (var30 < minDurability.getValue()) {
                  if (var23 == null) {
                     var23 = new ArrayList();
                  }

                  var23.add(var27);
                  continue;
               }
            }

            if (var28 instanceof ItemPickaxe) {
               int var48 = slots.getSetting("Pickaxe-Slot").asInt();
               float var57 = InventoryUtil.getToolScore(var27);
               if (var48 != 0) {
                  ItemStack var66 = mc.thePlayer.inventory.getStackInSlot(var48 - 1);
                  if (var66 != null) {
                     float var71 = InventoryUtil.getToolScore(var66);
                     if (var71 == var57 && var13 < var57 && var27 != var66 && var66.getItem() instanceof ItemPickaxe) {
                        var13 = var71;
                        var3 = var66;
                        continue;
                     }
                  }
               }

               if (var57 > var13) {
                  var13 = var57;
                  var3 = var27;
               }
            } else if (var28 instanceof ItemAxe) {
               int var47 = slots.getSetting("Axe-Slot").asInt();
               float var56 = InventoryUtil.getToolScore(var27);
               if (var47 != 0) {
                  ItemStack var65 = mc.thePlayer.inventory.getStackInSlot(var47 - 1);
                  if (var65 != null) {
                     float var70 = InventoryUtil.getToolScore(var65);
                     if (var70 == var56 && var14 < var56 && var27 != var65 && var65.getItem() instanceof ItemAxe) {
                        var14 = var70;
                        var4 = var65;
                        continue;
                     }
                  }
               }

               if (var56 > var14) {
                  var14 = var56;
                  var4 = var27;
               }
            } else if (var28 instanceof ItemSpade) {
               int var46 = slots.getSetting("Shovel-Slot").asInt();
               float var55 = InventoryUtil.getToolScore(var27);
               if (var46 != 0) {
                  ItemStack var64 = mc.thePlayer.inventory.getStackInSlot(var46 - 1);
                  if (var64 != null) {
                     float var69 = InventoryUtil.getToolScore(var64);
                     if (var69 == var55 && var15 < var55 && var27 != var64 && var64.getItem() instanceof ItemSpade) {
                        var15 = var69;
                        var5 = var64;
                        continue;
                     }
                  }
               }

               if (var55 > var15) {
                  var15 = var55;
                  var5 = var27;
               }
            } else if (var28 instanceof ItemShears) {
               int var45 = var27.getItemDamage() + (var27.hasEffect() ? 50 : 0);
               if (var45 >= var18) {
                  var18 = var45;
                  var6 = var27;
               }
            } else if (var28 instanceof ItemFishingRod && keepFishingRods()) {
               int var44 = var27.getItemDamage() + (var27.hasEffect() ? 50 : 0);
               if (var44 >= var22) {
                  var22 = var44;
                  var12 = var27;
               }
            } else if (!(var28 instanceof ItemSword)) {
               if (var28 instanceof ItemAppleGold) {
                  int var43 = slots.getSetting("Gapple-Slot").asInt();
                  if (var43 != 0) {
                     ItemStack var54 = mc.thePlayer.inventory.getStackInSlot(var43 - 1);
                     if (var54 != null) {
                        int var63 = var27.stackSize;
                        if (var63 == var54.stackSize && var19 < var63 && var27 != var54 && var54.getItem() instanceof ItemAppleGold) {
                           var19 = var63;
                           var8 = var54;
                           continue;
                        }
                     }
                  }

                  if (var27.stackSize > var19) {
                     var19 = var27.stackSize;
                     var8 = var27;
                  }
               } else if (var28 instanceof ItemEnderPearl && keepPearls()) {
                  int var42 = slots.getSetting("Pearl-Slot").asInt();
                  if (var42 != 0) {
                     ItemStack var53 = mc.thePlayer.inventory.getStackInSlot(var42 - 1);
                     if (var53 != null) {
                        int var62 = var27.stackSize;
                        if (var62 == var53.stackSize && var21 < var62 && var27 != var53 && var53.getItem() instanceof ItemEnderPearl) {
                           var21 = var62;
                           var11 = var53;
                           continue;
                        }
                     }
                  }

                  if (var27.stackSize > var21) {
                     var21 = var27.stackSize;
                     var11 = var27;
                  }
               } else if (var28 instanceof ItemSkull && isHead(var27) && keepHeads()) {
                  int var41 = slots.getSetting("Head-Slot").asInt();
                  if (var41 != 0) {
                     ItemStack var52 = mc.thePlayer.inventory.getStackInSlot(var41 - 1);
                     if (var52 != null) {
                        int var61 = var27.stackSize;
                        if (var61 == var52.stackSize && var20 < var61 && var27 != var52 && var52.getItem() instanceof ItemSkull && isHead(var52)) {
                           var20 = var61;
                           var9 = var52;
                           continue;
                        }
                     }
                  }

                  if (var27.stackSize > var20) {
                     var20 = var27.stackSize;
                     var9 = var27;
                  }
               } else if (var28 instanceof ItemBow && keepBows()) {
                  int var40 = slots.getSetting("Bow-Slot").asInt();
                  float var51 = InventoryUtil.getBowScore(var27);
                  if (var40 != 0) {
                     ItemStack var60 = mc.thePlayer.inventory.getStackInSlot(var40 - 1);
                     if (var60 != null) {
                        float var68 = InventoryUtil.getBowScore(var60);
                        if (var68 == var51 && var17 < var51 && var27 != var60 && var60.getItem() instanceof ItemBow) {
                           var17 = var68;
                           var10 = var60;
                           continue;
                        }
                     }
                  }

                  if (var51 > var17) {
                     var17 = var51;
                     var10 = var27;
                  }
               }
            } else {
               float var39 = InventoryUtil.getDamageScore(var27);
               OldInventoryManagerUHCSwordMode var50 = (OldInventoryManagerUHCSwordMode)uhc.getSetting("Sword").getValue();
               if (uhc.getSetting("Enable").asBoolean()) {
                  boolean var31 = var50 == OldInventoryManagerUHCSwordMode.DRAGON_SWORD && isDragonSword(var27)
                     || var50 == OldInventoryManagerUHCSwordMode.BLOODLUST && isBloodlust(var27);
                  if (var31) {
                     var39 = 999.0F;
                  }
               }

               int var59 = slots.getSetting("Sword-Slot").asInt();
               if (var59 != 0) {
                  ItemStack var32 = mc.thePlayer.inventory.getStackInSlot(var59 - 1);
                  if (var32 != null) {
                     boolean var33 = uhc.getSetting("Enable").asBoolean()
                        && (
                           var50 == OldInventoryManagerUHCSwordMode.DRAGON_SWORD && isDragonSword(var32)
                              || var50 == OldInventoryManagerUHCSwordMode.BLOODLUST && isBloodlust(var32)
                        );
                     float var34 = InventoryUtil.getDamageScore(var32);
                     if (var33) {
                        var34 = 999.0F;
                     }

                     if (var34 == var39 && var16 < var34 && var27 != var32 && var32.getItem() instanceof ItemSword) {
                        var16 = var34;
                        var7 = var32;
                        continue;
                     }
                  }
               }

               if (var39 > var16) {
                  var16 = var39;
                  var7 = var27;
               }
            }
         }
      }

      if (var23 != null) {
         int var35 = 9999;
         int var36 = 9999;
         int var37 = 9999;
         int var38 = 9999;
         ItemStack var49 = null;
         ItemStack var58 = null;
         ItemStack var67 = null;
         ItemStack var72 = null;

         for (Object var74$raw : var23) {
            ItemStack var74 = (ItemStack)var74$raw;
            if (var74.getItem() instanceof ItemPickaxe && var3 == null && var35 > var74.getItemDamage()) {
               var49 = var74;
               var35 = var74.getItemDamage();
            }

            if (var74.getItem() instanceof ItemAxe && var4 == null && var36 > var74.getItemDamage()) {
               var58 = var74;
               var36 = var74.getItemDamage();
            }

            if (var74.getItem() instanceof ItemSpade && var5 == null && var37 > var74.getItemDamage()) {
               var67 = var74;
               var37 = var74.getItemDamage();
            }

            if (var74.getItem() instanceof ItemSword && var7 == null && var38 > var74.getItemDamage()) {
               var72 = var74;
               var38 = var74.getItemDamage();
            }
         }

         if (var49 != null) {
            var3 = var49;
         }

         if (var58 != null) {
            var4 = var58;
         }

         if (var67 != null) {
            var5 = var67;
         }

         if (var72 != null) {
            var7 = var72;
         }
      }

      return new ItemStack[]{var3, var4, var5, var6, var7, var8, var9, var10, var11, var12};
   }

   private int toContainerSlot(int var1) {
      return var1 == 0 ? -999 : 35 + var1;
   }

   private static boolean isGoodPotion(ItemStack var0) {
      int var4 = 0;
      int var5 = 0;
      if (var0 == null) {
         return false;
      }

      for (PotionEffect var7 : Items.potionitem.getEffects(var0)) {
         if (var7 != null) {
            Potion var8 = Potion.potionTypes[var7.getPotionID()];
            if (var8 != null) {
               if (var8.isBadEffect()) {
                  var4++;
               } else {
                  var5++;
               }
            }
         }
      }

      return var4 < var5;
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

   private void dropSlot(int var1) {
      if (this.dropTimer.hasElapsed(this.delay.getValue().intValue())) {
         InventoryUtil.dropSlot(var1, 0);
      }
   }

   public static int countItems(Class<?> var0, Object var1) {
      int var5 = 0;

      for (int var6 = 0; var6 < mc.thePlayer.inventoryContainer.inventorySlots.size(); var6++) {
         Slot var7 = mc.thePlayer.inventoryContainer.inventorySlots.get(var6);
         if (var7 != null && var7.getSlotTexture() == null && var7.getHasStack()) {
            ItemStack var8 = var7.getStack();
            if ((!(var8.getItem() instanceof ItemBlock) || !isOreBlock((ItemBlock)var8.getItem()))
               && (var0 != null && var0.isInstance(var8.getItem()) || var1 == var8.getItem())) {
               var5 += var8.stackSize;
            }
         }
      }

      return var5;
   }

   private static int countWorkbenches() {
      int var3 = 0;

      for (int var4 = 0; var4 < mc.thePlayer.inventoryContainer.inventorySlots.size(); var4++) {
         Slot var5 = mc.thePlayer.inventoryContainer.inventorySlots.get(var4);
         if (var5 != null && var5.getSlotTexture() == null && var5.getHasStack()) {
            ItemStack var6 = var5.getStack();
            if (var6.getItem() instanceof ItemBlock && ((ItemBlock)var6.getItem()).getBlock() instanceof BlockWorkbench) {
               var3 += var6.stackSize;
            }
         }
      }

      return var3;
   }

   private static int countForges() {
      int var3 = 0;

      for (int var4 = 0; var4 < mc.thePlayer.inventoryContainer.inventorySlots.size(); var4++) {
         Slot var5 = mc.thePlayer.inventoryContainer.inventorySlots.get(var4);
         if (var5 != null && var5.getSlotTexture() == null && var5.getHasStack()) {
            ItemStack var6 = var5.getStack();
            if (var6.getItem() instanceof ItemBlock && ((ItemBlock)var6.getItem()).getBlock() instanceof BlockFurnace && var6.hasEffect()) {
               var3 += var6.stackSize;
            }
         }
      }

      return var3;
   }

   private static int countItemsOf(Class<?> var0) {
      return countItems(var0, null);
   }

   private static boolean isSafeFood(ItemFood var0) {
      return var0.J() != Potion.hunger.id && var0.J() != Potion.poison.id;
   }

   public static boolean isHead(ItemStack var0) {
      return var0.getMetadata() == 3 && (var0.getDisplayName() == null || !var0.getDisplayName().contains("Backpack"));
   }

   private void moveToSlot(OldInventoryManagerSlot var1, ItemStack var2, int var3) {
      int var7 = slots.getSetting(var1.getSettingName()).asInt();
      if (var7 > 0) {
         int var8 = var7 - 1;
         ItemStack var9 = mc.thePlayer.inventory.getStackInSlot(var8);
         if (var9 != var2) {
            this.swapToHotbar(var3, var8);
         }
      }
   }

   private void swapToHotbar(int var1, int var2) {
      mc.playerController.windowClick(0, var1, var2, 2, mc.thePlayer);
   }

   public static boolean hasDuplicateSlots(int[] var0) {
      BitSet var5 = new BitSet(11);
      var5.set(0, 10, false);

      for (int var9 : var0) {
         if (var9 != 0) {
            if (var5.get(var9)) {
               return true;
            }

            var5.set(var9, true);
         }
      }

      return false;
   }

   private static OldInventoryManagerSlot getSlotFor(ItemStack var0) {
      Item var4 = var0.getItem();
      if (var4 instanceof ItemSpade) {
         return OldInventoryManagerSlot.SHOVEL;
      } else if (var4 instanceof ItemPickaxe) {
         return OldInventoryManagerSlot.PICKAXE;
      } else if (var4 instanceof ItemAxe) {
         return OldInventoryManagerSlot.AXE;
      } else if (var4 instanceof ItemSword) {
         return OldInventoryManagerSlot.SWORD;
      } else if (var4 instanceof ItemAppleGold) {
         return OldInventoryManagerSlot.GAPPLE;
      } else if (var4 instanceof ItemSkull && isHead(var0)) {
         return OldInventoryManagerSlot.HEAD;
      } else if (var4 instanceof ItemBow) {
         return OldInventoryManagerSlot.BOW;
      } else if (var4 instanceof ItemEnderPearl) {
         return OldInventoryManagerSlot.PEARL;
      } else {
         return var4 instanceof ItemFishingRod ? OldInventoryManagerSlot.FISHING_ROD : null;
      }
   }

   public static String getLore(ItemStack var0) {
      StringBuilder var4 = new StringBuilder();
      if (var0.V() != null && var0.V().hasKey("display", 10)) {
         NBTTagCompound var5 = var0.V().getCompoundTag("display");
         NBTTagList var6 = var5.getTagList("Lore", 8);
         if (var6.tagCount() > 0) {
            for (int var7 = 0; var7 < var6.tagCount(); var7++) {
               var4.append(var6.getStringTagAt(var7));
            }
         }
      }

      return var4.toString();
   }

   public static boolean isDragonSword(ItemStack var0) {
      String var4 = getLore(var0);

      for (String var6 : uhcItemLore) {
         if (var4.contains(var6) && var6.equals("Weaponsmith Ultimate")) {
            return true;
         }
      }

      return false;
   }

   public static boolean isBloodlust(ItemStack var0) {
      String var4 = getLore(var0);

      for (String var6 : uhcItemLore) {
         if (var4.contains(var6) && var6.contains("Gains Sharpness II after 1 kill")) {
            return true;
         }
      }

      return false;
   }

   static {
      long var20 = u ^ 139327214074234L;
      long var10000 = 7594547158736891620L ^ var20;
   }
}
