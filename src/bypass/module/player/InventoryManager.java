package bypass.module.player;

import bypass.ClientAccess;
import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.player.MotionEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.InventoryUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class InventoryManager extends Module {
   public static final MapSetting general = new MapSettingBuilder("General")
      .ordered()
      .description("drop map")
      .add("Drop-Delay", new NumberSetting<>("Drop-Delay", "Drop-Delay", 130, 0, 1000, "ms"))
      .add("Swap-Delay", new NumberSetting<>("Swap-Delay", "Swap-Delay", 320, 0, 1000, "ms"))
      .add("Min-Hold-Time", new NumberSetting<>("Min-Hold-Time", "Min-Hold-Time", 130, 0, 1000, "ms"))
      .add("Open-Inv-Delay", new NumberSetting<>("Open-Inv-Delay", "Open-Inv-Delay", 130, 0, 1000, "ms"))
      .add("Drop-Pattern", new EnumSetting<>("Drop-Pattern", "drop", InventoryManagerPatternMode.NONE))
      .add("Swap-Pattern", new EnumSetting<>("Swap-Pattern", "Swap", InventoryManagerPatternMode.NONE))
      .add("Drop-Mode", new EnumSetting<>("Drop-Mode", "drop", InventoryManagerMode.ALWAYS))
      .add("Swap-Mode", new EnumSetting<>("Swap-Mode", "Swap", InventoryManagerMode.ALWAYS))
      .add("Auto-WhiteList", new BooleanSetting("Auto-WhiteList", "Auto-WhiteList", true))
      .add("Auto-Stop", new BooleanSetting("Auto-Drop-Stop", "Auto drops items automatically when not moving", false))
      .add(
         "Spoof-GUI",
         new BooleanSetting("Spoof-GUI", "Spoofs that you're in inventory GUI to the server (might ban in some servers but helps in others)", false)
      )
      .build();
   private final MapSetting drop = new MapSettingBuilder("Drop")
      .ordered()
      .description("drop map")
      .add("Swords", new BooleanSetting("Drop-Swords", "Drops Swords", false))
      .add("Bows", new BooleanSetting("Drop-Bows", "Drops Bows", false))
      .add("Tools", new BooleanSetting("Drop-Tools", "Drops Tools", true))
      .add("Armor", new BooleanSetting("Drop-Armor", "Drops Armor", false))
      .add("Util", new BooleanSetting("Drop-Util", "Drops Util", false))
      .add("Food", new BooleanSetting("Drop-Food", "Drops Food", false))
      .add("Blocks", new BooleanSetting("Drop-Blocks", "Drops Blocks", true))
      .add("UHC", new BooleanSetting("Drop-UHC", "Drops UHC", true))
      .add("Ores", new BooleanSetting("Drop-Ores", "Drops Ores", false))
      .add("Potions", new BooleanSetting("Drop-Potions", "Drops Potions", false))
      .add("Garbage", new BooleanSetting("Drop-Garbage", "Drops Garbage", true))
      .build();
   private final MapSetting tools = new MapSettingBuilder("Tools")
      .ordered()
      .description("sltoz")
      .add("Swords", new NumberSetting<>("Swords", "Sword", 1, 0, 5, "stacks"))
      .add("Bows", new NumberSetting<>("Bows", "Bows", 1, 0, 5, "stacks"))
      .add("Pickaxes", new NumberSetting<>("Pickaxes", "Pickaxes", 1, 0, 5, "stacks"))
      .add("Shovels", new NumberSetting<>("Shovels", "Shovels", 1, 0, 5, "stacks"))
      .add("Axes", new NumberSetting<>("Axes", "Axes", 1, 0, 5, "stacks"))
      .add("Fishing-Rods", new NumberSetting<>("Fishing-Rods", "Fishing-Rods", 1, 0, 5, "stacks"))
      .add("Shears", new NumberSetting<>("Shears", "Shears", 1, 0, 5, "stacks"))
      .add("Flint-Steel", new NumberSetting<>("Flint and Steel", "Flint and Steel", 1, 0, 5, "stacks"))
      .add("Min-Tool-Durability", new NumberSetting<>("Min-Tool-Durability", "Min-Tool-Durability", 200, 1, 500))
      .build();
   private final MapSetting armor = new MapSettingBuilder("Armor")
      .ordered()
      .description("sltoz")
      .add("Helmets", new NumberSetting<>("Helmets", "Helmets", 1, 0, 5, "stacks"))
      .add("Chestplates", new NumberSetting<>("Chestplates", "Chestplates", 1, 0, 5, "stacks"))
      .add("Leggings", new NumberSetting<>("Leggings", "Leggings", 1, 0, 5, "stacks"))
      .add("Boots", new NumberSetting<>("Boots", "Boots", 1, 0, 5, "stacks"))
      .add("Min-Armor-Durability", new NumberSetting<>("Min-Armor-Durability", "Min-Armor-Durability", 100, 1, 500))
      .build();
   private final MapSetting utils = new MapSettingBuilder("Utils")
      .ordered()
      .description("sltoz")
      .add("Golden-Apples", new NumberSetting<>("Golden-Apples", "Golden Apples", 3, 0, 5, "stacks"))
      .add("Ender-Pearls", new NumberSetting<>("Ender-Pearls", "Bows", 1, 0, 5, "stacks"))
      .add("Boats", new NumberSetting<>("Boats", "Boats", 0, 0, 5, "stacks"))
      .add("TNT", new NumberSetting<>("TNT", "TNT", 0, 0, 5, "stacks"))
      .add("Arrows", new NumberSetting<>("Arrows", "Arrows", 1, 0, 5, "stacks"))
      .add("Eggs-Snowballs", new NumberSetting<>("Eggs/Snowballs", "Eggs/Snowballs", 0, 0, 5, "stacks"))
      .add("Spawn-Eggs", new NumberSetting<>("Spawn Eggs", "Spawn Eggs", 0, 0, 5, "stacks"))
      .add("Food", new NumberSetting<>("Food", "Food", 1, 0, 5, "stacks"))
      .add("Water-Buckets", new NumberSetting<>("Water Buckets", "Water Buckets", 1, 0, 5, "stacks"))
      .add("Lava-Buckets", new NumberSetting<>("Lava Buckets", "Lava Buckets", 1, 0, 5, "stacks"))
      .add("Milk-Buckets", new NumberSetting<>("Milk Buckets", "Milk Buckets", 1, 0, 5, "stacks"))
      .add("Blocks", new NumberSetting<>("Blocks", "Blocks", 3, 0, 5, "stacks"))
      .add("Sand-Gravel", new NumberSetting<>("Sand/Gravel", "Sand/Gravel", 0, 0, 5, "stacks"))
      .add("Cobwebs", new NumberSetting<>("Cobwebs", "Cobwebs", 0, 0, 5, "stacks"))
      .build();
   private final MapSetting uhc = new MapSettingBuilder("UHC")
      .ordered()
      .description("sltoz")
      .add("Apples", new NumberSetting<>("Apples", "Apples", 1, 0, 5, "stacks"))
      .add("Crafting-Tables", new NumberSetting<>("Crafting Tables", "Crafting Tables", 1, 0, 5, "stacks"))
      .add("Sticks", new NumberSetting<>("Sticks", "Sticks", 1, 0, 5, "stacks"))
      .add("Anvils", new NumberSetting<>("Anvils", "Anvils", 1, 0, 5, "stacks"))
      .add("Enchanting-Tables", new NumberSetting<>("Enchanting Tables", "Enchanting Tables", 1, 0, 5, "stacks"))
      .add("Furnaces", new NumberSetting<>("Furnaces", "Furnaces", 1, 0, 5, "stacks"))
      .add("EXP-Bottles", new NumberSetting<>("EXP Bottles", "EXP Bottles", 1, 0, 5, "stacks"))
      .add("Gold-Blocks", new NumberSetting<>("Gold Blocks", "Gold Blocks", 1, 0, 5, "stacks"))
      .add("Chests", new NumberSetting<>("Chests", "Chests", 1, 0, 5, "stacks"))
      .add("String", new NumberSetting<>("String", "String", 1, 0, 5, "stacks"))
      .build();
   private final MapSetting ores = new MapSettingBuilder("Ores")
      .ordered()
      .description("sltoz")
      .add("Iron-Ingots", new NumberSetting<>("Iron Ingots", "Iron Ingots", 1, 0, 5, "stacks"))
      .add("Gold-Ingots", new NumberSetting<>("Gold Ingots", "Gold Ingots", 1, 0, 5, "stacks"))
      .add("Iron-Ores", new NumberSetting<>("Iron Ores", "Iron Ores", 1, 0, 5, "stacks"))
      .add("Gold-Ores", new NumberSetting<>("Gold Ores", "Gold Ores", 1, 0, 5, "stacks"))
      .add("Coal", new NumberSetting<>("Coal", "Coal", 1, 0, 5, "stacks"))
      .add("Redstone", new NumberSetting<>("Redstone", "Redstone", 1, 0, 5, "stacks"))
      .add("Lapis", new NumberSetting<>("Lapis", "Lapis", 1, 0, 5, "stacks"))
      .add("Diamonds", new NumberSetting<>("Diamonds", "Diamonds", 1, 0, 5, "stacks"))
      .add("Emeralds", new NumberSetting<>("Emeralds", "Emeralds", 1, 0, 5, "stacks"))
      .build();
   private final MapSetting potions = new MapSettingBuilder("Potions")
      .ordered()
      .description("sltoz")
      .add("Potion-Sort", new EnumSetting<>("Potion-Sort", "Sort", InventoryManagerPotionSortMode.AMPLIFIER))
      .add("Speed-Potions", new NumberSetting<>("Speed", "Speed potion", 3, 0, 5, "stacks"))
      .add("Strength-Potions", new NumberSetting<>("Strength", "Strength Potions", 3, 0, 5, "stacks"))
      .add("Regeneration-Potions", new NumberSetting<>("Regeneration", "Regeneration Potions", 3, 0, 5, "stacks"))
      .add("Healing-Potions", new NumberSetting<>("Healing", "Healing Potions", 3, 0, 5, "stacks"))
      .add("Jump-Boost-Potions", new NumberSetting<>("Jump-Boost", "Jump Boost Potions", 3, 0, 5, "stacks"))
      .add("Fire-Resistance-Potions", new NumberSetting<>("Fire-Resistance", "Fire Resistance Potions", 3, 0, 5, "stacks"))
      .add("Resistance-Potions", new NumberSetting<>("Resistance", "Resistance Potions", 3, 0, 5, "stacks"))
      .add("Invisibility-Potions", new NumberSetting<>("Invisibility", "Invisibility Potions", 3, 0, 5, "stacks"))
      .add("Absorption-Potions", new NumberSetting<>("Absorption", "Absorption Potions", 1, 0, 5, "stacks"))
      .add("Weakness-Potions", new NumberSetting<>("Weakness", "Weakness Potions", 3, 0, 5, "stacks"))
      .add("Slowness-Potions", new NumberSetting<>("Slowness", "Slowness Potions", 3, 0, 5, "stacks"))
      .add("Poison-Potions", new NumberSetting<>("Poison", "Poison Potions", 3, 0, 5, "stacks"))
      .add("Harming-Potions", new NumberSetting<>("Harming", "Harming Potions", 1, 0, 5, "stacks"))
      .add("Brewing-Items", new BooleanSetting("Brewing-Items", "Items for brewing potions", false))
      .build();
   private final MapSetting hypixelUHCItems = new MapSettingBuilder("Hypixel UHC Items")
      .ordered()
      .description("drop map")
      .add("Heads", new NumberSetting<>("Heads", "Heads", 10, 0, 20, "stacks"))
      .add("Forges", new BooleanSetting("Forges", "Forges", false))
      .add("Dragon-Sword", new BooleanSetting("Dragon-Sword", "Dragon Sword", true))
      .add("Bloodlust", new BooleanSetting("Bloodlust", "Bloodlust", false))
      .add("Exodus", new BooleanSetting("Exodus", "Exodus", false))
      .add("Barbarian", new BooleanSetting("Barbarian", "Barbarian", false))
      .add("Vidar", new BooleanSetting("Vidar", "Vidar", false))
      .add("Scythe", new BooleanSetting("Scythe", "Scythe", false))
      .add("Perun", new BooleanSetting("Perun", "Perun", false))
      .add("Backpack", new BooleanSetting("Backpack", "Backpacks", false))
      .add("Excalibur", new BooleanSetting("Excalibur", "Excalibur", false))
      .add("Anduril", new BooleanSetting("Anduril", "Anduril", false))
      .add("Philo", new BooleanSetting("Philo", "Philo", false))
      .add("Expert-Seal", new BooleanSetting("Expert-Seal", "Expert Seal", false))
      .add("Artemis-Bow", new BooleanSetting("Artemis-Bow", "Artemis' Bow", false))
      .add("Modular-Bow", new BooleanSetting("Modular-Bow", "Modular Bow", false))
      .add("Recipe-Books", new BooleanSetting("Recipe-Books", "Recipe Books", false))
      .build();
   private final MapSetting hypixelUHCCrafts = new MapSettingBuilder("Hypixel UHC Crafts")
      .ordered()
      .description("drop map")
      .add("Eves-Temptation", new BooleanSetting("Eves-Temptation", "Eve's Temptation", false))
      .add("Enlightening-Pack", new BooleanSetting("Enlightening-Pack", "Enlightening Pack", false))
      .add("Sharpness-Books", new BooleanSetting("Sharpness-Books", "Sharpness Books", false))
      .add("Protection-Books", new BooleanSetting("Protection-Books", "Protection Books", false))
      .add("Seven-League", new BooleanSetting("Seven-League", "Seven League", false))
      .add("Backpack", new BooleanSetting("Backpack", "Backpacks", false))
      .add("Philo", new BooleanSetting("Philo", "Philo", false))
      .add("Holy-Water", new BooleanSetting("Holy-Water", "Holy Water", false))
      .add("Arrow-Economy", new BooleanSetting("Arrow-Economy", "Arrow Economy", false))
      .build();
   private final MapSetting enchants = new MapSettingBuilder("Enchants")
      .ordered()
      .description("drop map")
      .add("Combinable-Sharpness", new BooleanSetting("Combinable-Sharpness", "Combinable Sharpness", false))
      .add("Combinable-Protection", new BooleanSetting("Combinable-Protection", "Combinable Protection", false))
      .add("Combinable-Power", new BooleanSetting("Combinable-Power", "Combinable Power", false))
      .add("Fire-Aspect", new BooleanSetting("Fire-Aspect", "Fire Aspect", false))
      .add("Punch", new BooleanSetting("Punch", "Punch", false))
      .add("Thorns", new BooleanSetting("Thorns", "Thorns", false))
      .add("Knockback", new BooleanSetting("Knockback", "Knockback", false))
      .add("Fortune", new BooleanSetting("Fortune", "Fortune", false))
      .add("Flame", new BooleanSetting("Flame", "Flame", false))
      .add("Looting", new BooleanSetting("Looting", "Looting", false))
      .add("Enchanted-Books", new BooleanSetting("Enchanted Books", "Enchanted Books", false))
      .build();
   private static final MapSetting slots = new MapSettingBuilder("Slots")
      .ordered()
      .description("sltoz")
      .add("Sword-Slot", new NumberSetting<>("Sword-Slot", "Sword Slot", 1, 0, 9))
      .add("Golden-Apple-Slot", new NumberSetting<>("Golden-Apple-Slot", "Golden Apple Slot", 2, 0, 9))
      .add("Golden-Apple-Refill", new EnumSetting<>("Golden-Apple-Refill", "Golden-Apple-Refill", InventoryManagerRefillMode.MOST))
      .add("Head-Slot", new NumberSetting<>("Head-Slot", "Head Slot", 0, 0, 9))
      .add("Head-Refill", new EnumSetting<>("Head-Refill", "Head-Refill", InventoryManagerRefillMode.MOST))
      .add("Pearl-Slot", new NumberSetting<>("Pearl-Slot", "Pearl Slot", 3, 0, 9))
      .add("Ender-Pearl-Refill", new EnumSetting<>("Ender-Pearl-Refill", "Ender-Pearl-Refill", InventoryManagerRefillMode.MOST))
      .add("Bow-Slot", new NumberSetting<>("Bow-Slot", "Bow Slot", 4, 0, 9))
      .add("Fishing-Rod-Slot", new NumberSetting<>("Fishing-Rod-Slot", "Fishing-Rods", 5, 0, 9))
      .add("Pickaxe-Slot", new NumberSetting<>("Pickaxe-Slot", "Pickaxe Slot", 6, 0, 9))
      .add("Axe-Slot", new NumberSetting<>("Axe-Slot", "Axe Slot", 7, 0, 9))
      .add("Blocks-Slot", new NumberSetting<>("Blocks-Slot", "Blocks Slot", 9, 0, 9))
      .add("Blocks-Refill", new EnumSetting<>("Blocks-Refill", "Blocks-Refill", InventoryManagerRefillMode.MOST))
      .add("Shovel-Slot", new NumberSetting<>("Shovel-Slot", "Shovel Slot", 8, 0, 9))
      .build();
   public HashMap<Integer, ItemStack> lastStacks = new HashMap<>();
   public HashMap<Integer, Long> changeTimes = new HashMap<>();
   public PriorityQueue<InventoryManager.RankedSlot> swords = new PriorityQueue<>();
   public int bestSwordSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> bows = new PriorityQueue<>();
   public int bestBowSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> pickaxes = new PriorityQueue<>();
   public int bestPickaxeSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> shovels = new PriorityQueue<>();
   public int bestShovelSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> axes = new PriorityQueue<>();
   public int bestAxeSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> fishingRods = new PriorityQueue<>();
   public int bestFishingRodSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> shears = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> flintAndSteels = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> helmets = new PriorityQueue<>();
   public int bestHelmetSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> chestplates = new PriorityQueue<>();
   public int bestChestplateSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> leggings = new PriorityQueue<>();
   public int bestLeggingsSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> boots = new PriorityQueue<>();
   public int bestBootsSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> goldenApples = new PriorityQueue<>();
   public int goldenAppleRefillSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> enderPearls = new PriorityQueue<>();
   public int enderPearlRefillSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> boats = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> tnt = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> arrows = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> eggsAndSnowballs = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> spawnEggs = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> food = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> waterBuckets = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> lavaBuckets = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> blocks = new PriorityQueue<>();
   public int blockRefillSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> sandAndGravel = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> cobwebs = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> apples = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> craftingTables = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> sticks = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> anvils = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> enchantingTables = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> furnaces = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> expBottles = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> milkBuckets = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> emptyBuckets = new PriorityQueue<>();
   public int missingWaterBuckets = -1;
   public int missingLavaBuckets = -1;
   public int missingMilkBuckets = -1;
   public PriorityQueue<InventoryManager.RankedSlot> chests = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> strings = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> ironIngots = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> goldIngots = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> ironOres = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> goldOres = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> coal = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> redstone = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> lapis = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> diamonds = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> emeralds = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> unused = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> speedPotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> strengthPotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> regenerationPotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> healingPotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> jumpBoostPotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> fireResistancePotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> resistancePotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> invisibilityPotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> absorptionPotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> weaknessPotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> slownessPotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> poisonPotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> harmingPotions = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> brewingItems = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> heads = new PriorityQueue<>();
   public int headRefillSlot = -1;
   public PriorityQueue<InventoryManager.RankedSlot> whitelisted = new PriorityQueue<>();
   public PriorityQueue<InventoryManager.RankedSlot> garbage = new PriorityQueue<>();
   private boolean inInventory;
   private boolean wasInInventory;
   public ArrayList<Integer[]> swaps = new ArrayList<>();
   public ArrayList<Integer> dropSlots = new ArrayList<>();
   public ArrayList<Integer> keepSlots = new ArrayList<>();
   public List<String> nonBuildingBlocks = Arrays.asList(
      "tile.workbench",
      "tile.tnt",
      "tile.anvil.intact",
      "tile.anvil.slightlyDamaged",
      "tile.anvil.veryDamaged",
      "tile.furnace",
      "tile.sand.default",
      "tile.gravel",
      "tile.web",
      "tile.oreIron",
      "tile.oreGold",
      "tile.blockGold",
      "tile.enchantmentTable"
   );
   public List<String> nonFoods = Arrays.asList("item.appleGold", "item.apple", "item.milk");
   public InventoryManager.Delay delay = new InventoryManager.Delay();
   private final Pattern recipeBookPattern = Pattern.compile("§a.*§7", 2);
   private boolean slotsUnique;
   @EventHandler(MotionEvent.class)
   private final Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.PRE) {
         this.update();
      }
   };
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.SCREEN) {
         this.drawSlotWarning(renderEvent);
      }
   };

   public InventoryManager(Category category) {
      super(category);
      this.addSettings(
         general,
         slots,
         this.drop,
         this.armor,
         this.tools,
         this.utils,
         this.uhc,
         this.ores,
         this.potions,
         this.hypixelUHCItems,
         this.hypixelUHCCrafts,
         this.enchants
      );
   }

   public int remainingDurability(ItemStack itemStack) {
      int i = -1;
      if (itemStack != null) {
         i = itemStack.getMaxDamage() - itemStack.getItemDamage();
      }

      return i;
   }

   public void resetScan() {
      this.swaps.clear();
      this.dropSlots.clear();
      this.keepSlots.clear();
      this.swords.clear();
      this.bestSwordSlot = -1;
      this.bows.clear();
      this.bestBowSlot = -1;
      this.pickaxes.clear();
      this.bestPickaxeSlot = -1;
      this.shovels.clear();
      this.bestShovelSlot = -1;
      this.axes.clear();
      this.bestAxeSlot = -1;
      this.fishingRods.clear();
      this.bestFishingRodSlot = -1;
      this.shears.clear();
      this.flintAndSteels.clear();
      this.helmets.clear();
      this.bestHelmetSlot = -1;
      this.chestplates.clear();
      this.bestChestplateSlot = -1;
      this.leggings.clear();
      this.bestLeggingsSlot = -1;
      this.boots.clear();
      this.bestBootsSlot = -1;
      this.goldenApples.clear();
      this.goldenAppleRefillSlot = -1;
      this.enderPearls.clear();
      this.enderPearlRefillSlot = -1;
      this.boats.clear();
      this.tnt.clear();
      this.arrows.clear();
      this.eggsAndSnowballs.clear();
      this.spawnEggs.clear();
      this.food.clear();
      this.waterBuckets.clear();
      this.lavaBuckets.clear();
      this.blocks.clear();
      this.blockRefillSlot = -1;
      this.sandAndGravel.clear();
      this.cobwebs.clear();
      this.apples.clear();
      this.craftingTables.clear();
      this.sticks.clear();
      this.anvils.clear();
      this.enchantingTables.clear();
      this.furnaces.clear();
      this.expBottles.clear();
      this.milkBuckets.clear();
      this.emptyBuckets.clear();
      this.missingWaterBuckets = -1;
      this.missingLavaBuckets = -1;
      this.missingMilkBuckets = -1;
      this.chests.clear();
      this.strings.clear();
      this.ironIngots.clear();
      this.goldIngots.clear();
      this.ironOres.clear();
      this.goldOres.clear();
      this.coal.clear();
      this.redstone.clear();
      this.lapis.clear();
      this.diamonds.clear();
      this.emeralds.clear();
      this.unused.clear();
      this.speedPotions.clear();
      this.strengthPotions.clear();
      this.regenerationPotions.clear();
      this.healingPotions.clear();
      this.jumpBoostPotions.clear();
      this.fireResistancePotions.clear();
      this.resistancePotions.clear();
      this.invisibilityPotions.clear();
      this.absorptionPotions.clear();
      this.weaknessPotions.clear();
      this.slownessPotions.clear();
      this.poisonPotions.clear();
      this.harmingPotions.clear();
      this.brewingItems.clear();
      this.heads.clear();
      this.headRefillSlot = -1;
      this.whitelisted.clear();
      this.garbage.clear();
   }

   public void scanInventory() {
      this.resetScan();
      float f = -1.0F;
      float g = -1.0F;
      float h = -1.0F;
      float f2 = -1.0F;
      float f3 = -1.0F;
      float f4 = -1.0F;
      float f5 = -1.0F;
      float f6 = -1.0F;
      float f7 = -1.0F;
      float f8 = -1.0F;
      int i = this.tools.getSetting("Min-Tool-Durability").asInt();
      boolean bl = slots.getSetting("Golden-Apple-Refill").getValue() == InventoryManagerRefillMode.MOST;
      boolean bl2 = slots.getSetting("Ender-Pearl-Refill").getValue() == InventoryManagerRefillMode.MOST;
      boolean bl3 = slots.getSetting("Head-Refill").getValue() == InventoryManagerRefillMode.MOST;
      boolean bl4 = slots.getSetting("Blocks-Refill").getValue() == InventoryManagerRefillMode.MOST;
      float f9 = bl ? -1.0F : 42069.0F;
      float f10 = bl2 ? -1.0F : 42069.0F;
      float f11 = bl3 ? -1.0F : 42069.0F;
      float f12 = bl4 ? -1.0F : 42069.0F;
      boolean bl5 = general.getSetting("Auto-WhiteList").asBoolean();

      for (int j = 0; j < mc.thePlayer.inventoryContainer.inventorySlots.size(); j++) {
         boolean bl6 = true;
         Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(j);
         if (slot.getHasStack() && slot.getHasStack()) {
            ItemStack itemStack = slot.getStack();
            if ((
                  !bl5
                     || !(itemStack.getItem() instanceof ItemBook)
                     || !itemStack.getDisplayName().contains(EnumChatFormatting.GREEN.toString())
                     || itemStack.isItemEnchanted()
               )
               && (!bl5 || !itemStack.getDisplayName().contains("§7(") || itemStack.getItem() instanceof ItemArmor || itemStack.getItem() instanceof ItemSword)) {
               if (!this.lastStacks.containsKey(j) || !itemStack.equals(this.lastStacks.get(j))) {
                  this.changeTimes.put(j, System.currentTimeMillis());
               }

               Item item2 = itemStack.getItem();
               String string2 = itemStack.getUnlocalizedName();
               String string3 = itemStack.getDisplayName();
               if (item2 instanceof ItemSword) {
                  bl6 = false;
                  this.swords.add(new InventoryManager.RankedSlot(j, this.swordRank(itemStack, i)));
                  if (this.swordScore(itemStack) > f) {
                     this.bestSwordSlot = j;
                     f = this.swordScore(itemStack);
                  }
               }

               if (item2 instanceof ItemBow) {
                  bl6 = false;
                  this.bows.add(new InventoryManager.RankedSlot(j, this.bowRank(itemStack, i)));
                  if (this.bowScore(itemStack) > g) {
                     this.bestBowSlot = j;
                     g = this.bowScore(itemStack);
                  }
               }

               if (item2 instanceof ItemPickaxe) {
                  bl6 = false;
                  this.pickaxes.add(new InventoryManager.RankedSlot(j, this.pickaxeRank(itemStack, i)));
                  if (this.pickaxeScore(itemStack) > h) {
                     this.bestPickaxeSlot = j;
                     h = this.pickaxeScore(itemStack);
                  }
               }

               if (item2 instanceof ItemSpade) {
                  bl6 = false;
                  this.shovels.add(new InventoryManager.RankedSlot(j, this.shovelRank(itemStack, i)));
                  if (this.shovelScore(itemStack) > f2) {
                     this.bestShovelSlot = j;
                     f2 = this.shovelScore(itemStack);
                  }
               }

               if (item2 instanceof ItemAxe) {
                  bl6 = false;
                  this.axes.add(new InventoryManager.RankedSlot(j, this.axeRank(itemStack, i)));
                  if (this.axeScore(itemStack) > f3) {
                     this.bestAxeSlot = j;
                     f3 = this.axeScore(itemStack);
                  }
               }

               if (item2 instanceof ItemFishingRod) {
                  bl6 = false;
                  this.fishingRods.add(new InventoryManager.RankedSlot(j, this.fishingRodRank(itemStack, i)));
                  if (this.fishingRodScore(itemStack) > f4) {
                     this.bestFishingRodSlot = j;
                     f4 = this.fishingRodScore(itemStack);
                  }
               }

               if (item2 instanceof ItemShears) {
                  bl6 = false;
                  this.shears.add(new InventoryManager.RankedSlot(j, this.remainingDurability(itemStack)));
               }

               if (item2 instanceof ItemFlintAndSteel) {
                  bl6 = false;
                  this.flintAndSteels.add(new InventoryManager.RankedSlot(j, this.remainingDurability(itemStack)));
               }

               if (item2 instanceof ItemArmor && ((ItemArmor)item2).armorType == 0) {
                  bl6 = false;
                  this.helmets.add(new InventoryManager.RankedSlot(j, this.armorRank(itemStack, i)));
                  if (this.armorScore(itemStack) > f5) {
                     this.bestHelmetSlot = j;
                     f5 = this.armorScore(itemStack);
                  }
               }

               if (item2 instanceof ItemArmor && ((ItemArmor)item2).armorType == 1) {
                  bl6 = false;
                  this.chestplates.add(new InventoryManager.RankedSlot(j, this.armorRank(itemStack, i)));
                  if (this.armorScore(itemStack) > f6) {
                     this.bestChestplateSlot = j;
                     f6 = this.armorScore(itemStack);
                  }
               }

               if (item2 instanceof ItemArmor && ((ItemArmor)item2).armorType == 2) {
                  bl6 = false;
                  this.leggings.add(new InventoryManager.RankedSlot(j, this.armorRank(itemStack, i)));
                  if (this.armorScore(itemStack) > f7) {
                     this.bestLeggingsSlot = j;
                     f7 = this.armorScore(itemStack);
                  }
               }

               if (item2 instanceof ItemArmor && ((ItemArmor)item2).armorType == 3) {
                  bl6 = false;
                  this.boots.add(new InventoryManager.RankedSlot(j, this.armorRank(itemStack, i)));
                  if (this.armorScore(itemStack) > f8) {
                     this.bestBootsSlot = j;
                     f8 = this.armorScore(itemStack);
                  }
               }

               if (item2 instanceof ItemAppleGold) {
                  bl6 = false;
                  this.goldenApples.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
                  if (bl && itemStack.stackSize > f9
                     || slots.getSetting("Golden-Apple-Refill").getValue() == InventoryManagerRefillMode.LEAST && itemStack.stackSize < f9) {
                     this.goldenAppleRefillSlot = j;
                     f9 = itemStack.stackSize;
                  }
               }

               if (item2 instanceof ItemEnderPearl) {
                  bl6 = false;
                  this.enderPearls.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
                  if (bl2 && itemStack.stackSize > f10
                     || slots.getSetting("Ender-Pearl-Refill").getValue() == InventoryManagerRefillMode.LEAST && itemStack.stackSize < f10) {
                     this.enderPearlRefillSlot = j;
                     f10 = itemStack.stackSize;
                  }
               }

               if (item2 instanceof ItemBoat) {
                  bl6 = false;
                  this.boats.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (item2 instanceof ItemBlock && ((ItemBlock)item2).getBlock() instanceof BlockTNT) {
                  bl6 = false;
                  this.tnt.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.arrow")) {
                  bl6 = false;
                  this.arrows.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.egg") || item2.getUnlocalizedName().equals("item.snowball")) {
                  bl6 = false;
                  this.eggsAndSnowballs.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.monsterPlacer")) {
                  bl6 = false;
                  this.spawnEggs.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (item2 instanceof ItemFood && !this.nonFoods.contains(string2)) {
                  bl6 = false;
                  this.food.add(new InventoryManager.RankedSlot(j, this.foodRank(itemStack)));
               }

               if (string2.equals("item.bucketWater")) {
                  bl6 = false;
                  this.waterBuckets.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.bucketLava")) {
                  bl6 = false;
                  this.lavaBuckets.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (item2 instanceof ItemBlock && isBuildingBlock((ItemBlock)item2) && !this.nonBuildingBlocks.contains(string2)) {
                  bl6 = false;
                  this.blocks.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
                  if (bl4 && itemStack.stackSize > f12
                     || slots.getSetting("Blocks-Refill").getValue() == InventoryManagerRefillMode.LEAST && itemStack.stackSize < f12) {
                     this.blockRefillSlot = j;
                     f12 = itemStack.stackSize;
                  }
               }

               if (item2 instanceof ItemBlock && (string2.equals("tile.sand.default") || string2.equals("tile.gravel"))) {
                  bl6 = false;
                  this.sandAndGravel.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("tile.web")) {
                  bl6 = false;
                  this.cobwebs.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.apple")) {
                  bl6 = false;
                  this.apples.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("tile.workbench")) {
                  bl6 = false;
                  this.craftingTables.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.stick")) {
                  bl6 = false;
                  this.sticks.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.contains("tile.anvil")) {
                  bl6 = false;
                  this.anvils.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("tile.enchantmentTable")) {
                  bl6 = false;
                  this.enchantingTables.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("tile.furnace")) {
                  bl6 = false;
                  this.furnaces.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.expBottle")) {
                  bl6 = false;
                  this.expBottles.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.milk")) {
                  bl6 = false;
                  this.milkBuckets.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("tile.chest")) {
                  bl6 = false;
                  this.chests.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.bucket")) {
                  bl6 = false;
                  this.emptyBuckets.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.string")) {
                  bl6 = false;
                  this.strings.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.ingotIron")) {
                  bl6 = false;
                  this.ironIngots.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.ingotGold")) {
                  bl6 = false;
                  this.goldIngots.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("tile.oreIron")) {
                  bl6 = false;
                  this.ironOres.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("tile.oreGold")) {
                  bl6 = false;
                  this.goldOres.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.coal")) {
                  bl6 = false;
                  this.coal.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.redstone")) {
                  bl6 = false;
                  this.redstone.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.dyePowder.blue")) {
                  bl6 = false;
                  this.lapis.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.diamond")) {
                  bl6 = false;
                  this.diamonds.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (string2.equals("item.emerald")) {
                  bl6 = false;
                  this.emeralds.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               EnumSetting enumSetting = (EnumSetting)this.potions.getSetting("Potion-Sort");
               boolean bl7 = enumSetting.getValue() == InventoryManagerPotionSortMode.AMPLIFIER;
               if (item2 instanceof ItemPotion) {
                  bl6 = false;
                  ItemPotion itemPotion = (ItemPotion)item2;
                  List list = itemPotion.getEffects(itemStack);
                  if (list != null) {
                     for (Object object : list) {
                        PotionEffect potionEffect = (PotionEffect)object;
                        if (potionEffect.getPotionID() == 1) {
                           this.speedPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 5) {
                           this.strengthPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 10) {
                           this.regenerationPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 6) {
                           this.healingPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 8) {
                           this.jumpBoostPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 12) {
                           this.fireResistancePotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 11) {
                           this.resistancePotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 14) {
                           this.invisibilityPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 22) {
                           this.absorptionPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 18) {
                           this.weaknessPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 2) {
                           this.slownessPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 19) {
                           this.poisonPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (potionEffect.getPotionID() == 7) {
                           this.harmingPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    j,
                                    itemStack.stackSize
                                       * (bl7 ? potionEffect.getAmplifier() + potionEffect.getDuration() / 10000000 : potionEffect.getDuration() + potionEffect.getAmplifier() / 10000000)
                                 )
                              );
                        }
                     }
                  }
               }

               if ((itemStack.getItem().isPotionIngredient(itemStack) || itemStack.getItem().getUnlocalizedName().equals("item.brewingStand"))
                  && (!itemStack.getItem().getUnlocalizedName().equals("item.redstone") || this.ores.getSetting("Redstone").asInt() <= 0)) {
                  bl6 = false;
                  this.brewingItems.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (item2 instanceof ItemSkull && isHead(itemStack)) {
                  bl6 = false;
                  this.heads.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
                  if (bl3 && itemStack.stackSize > f11
                     || slots.getSetting("Head-Refill").getValue() == InventoryManagerRefillMode.LEAST && itemStack.stackSize < f11) {
                     this.headRefillSlot = j;
                     f11 = itemStack.stackSize;
                  }
               }

               if (this.isWhitelisted(j)) {
                  bl6 = false;
                  this.whitelisted.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               if (bl6) {
                  this.garbage.add(new InventoryManager.RankedSlot(j, itemStack.stackSize));
               }

               this.lastStacks.put(j, slot.getStack());
            }
         } else {
            this.lastStacks.remove(j);
         }
      }
   }

   public void generateSwapList(int i, int j, int k, int l, int m, int n, int i2, int i3, int i4, int i5) {
      if (i != 0 && this.bestSwordSlot != -1) {
         int i6 = i - 1;
         Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + i6);
         Slot slot2 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestSwordSlot);
         if (!slot.getHasStack() || slot.getHasStack() && slot2.getHasStack() && this.swordScore(slot.getStack()) < this.swordScore(slot2.getStack())) {
            Integer[] integers = new Integer[]{this.bestSwordSlot, i6};
            this.swaps.add(integers);
         }
      }

      if (j != 0 && this.bestBowSlot != -1) {
         int i7 = j - 1;
         Slot slot3 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + i7);
         Slot slot4 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestBowSlot);
         if (!slot3.getHasStack() || slot3.getHasStack() && slot4.getHasStack() && this.bowScore(slot3.getStack()) < this.bowScore(slot4.getStack())) {
            Integer[] integers2 = new Integer[]{this.bestBowSlot, i7};
            this.swaps.add(integers2);
         }
      }

      if (k != 0 && this.bestPickaxeSlot != -1) {
         int i8 = k - 1;
         Slot slot5 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + i8);
         Slot slot6 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestPickaxeSlot);
         if (!slot5.getHasStack() || slot5.getHasStack() && slot6.getHasStack() && this.pickaxeScore(slot5.getStack()) < this.pickaxeScore(slot6.getStack())) {
            Integer[] integers3 = new Integer[]{this.bestPickaxeSlot, i8};
            this.swaps.add(integers3);
         }
      }

      if (l != 0 && this.bestShovelSlot != -1) {
         int i9 = l - 1;
         Slot slot7 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + i9);
         Slot slot8 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestShovelSlot);
         if (!slot7.getHasStack() || slot7.getHasStack() && slot8.getHasStack() && this.shovelScore(slot7.getStack()) < this.shovelScore(slot8.getStack())) {
            Integer[] integers4 = new Integer[]{this.bestShovelSlot, i9};
            this.swaps.add(integers4);
         }
      }

      if (m != 0 && this.bestAxeSlot != -1) {
         int i10 = m - 1;
         Slot slot9 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + i10);
         Slot slot10 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestAxeSlot);
         if (!slot9.getHasStack() || slot9.getHasStack() && slot10.getHasStack() && this.axeScore(slot9.getStack()) < this.axeScore(slot10.getStack())) {
            Integer[] integers5 = new Integer[]{this.bestAxeSlot, i10};
            this.swaps.add(integers5);
         }
      }

      if (n != 0 && this.bestFishingRodSlot != -1) {
         int i11 = n - 1;
         Slot slot11 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + i11);
         Slot slot12 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestFishingRodSlot);
         if (!slot11.getHasStack()
            || slot11.getHasStack() && slot12.getHasStack() && this.fishingRodScore(slot11.getStack()) < this.fishingRodScore(slot12.getStack())) {
            Integer[] integers6 = new Integer[]{this.bestFishingRodSlot, i11};
            this.swaps.add(integers6);
         }
      }

      if (i2 != 0 && this.goldenAppleRefillSlot != -1) {
         int i12 = i2 - 1;
         Slot slot13 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + i12);
         if (!slot13.getHasStack() || !(slot13.getStack().getItem() instanceof ItemAppleGold)) {
            Integer[] integers7 = new Integer[]{this.goldenAppleRefillSlot, i12};
            this.swaps.add(integers7);
         }
      }

      if (i3 != 0 && this.enderPearlRefillSlot != -1) {
         int i13 = i3 - 1;
         Slot slot14 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + i13);
         if (!slot14.getHasStack() || !(slot14.getStack().getItem() instanceof ItemEnderPearl)) {
            Integer[] integers8 = new Integer[]{this.enderPearlRefillSlot, i13};
            this.swaps.add(integers8);
         }
      }

      if (i4 != 0 && this.headRefillSlot != -1) {
         int i14 = i4 - 1;
         Slot slot15 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + i14);
         if (!slot15.getHasStack() || !(slot15.getStack().getItem() instanceof ItemSkull) || !isHead(slot15.getStack())) {
            Integer[] integers9 = new Integer[]{this.headRefillSlot, i14};
            this.swaps.add(integers9);
         }
      }

      if (i5 != 0 && this.blockRefillSlot != -1) {
         int i15 = i5 - 1;
         Slot slot16 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + i15);
         if (!slot16.getHasStack()
            || !(slot16.getStack().getItem() instanceof ItemBlock)
            || !isBuildingBlock((ItemBlock)slot16.getStack().getItem())
            || this.nonBuildingBlocks.contains(slot16.getStack().getUnlocalizedName())) {
            Integer[] integers10 = new Integer[]{this.blockRefillSlot, i15};
            this.swaps.add(integers10);
         }
      }

      EnumSetting enumSetting = (EnumSetting)general.getSetting("Swap-Pattern");
      if (((InventoryManagerPatternMode)enumSetting.getValue()).equals(InventoryManagerPatternMode.RANDOM)) {
         Random random = new Random();
         ArrayList arrayList = new ArrayList();

         while (this.swaps.size() > 0) {
            arrayList.add(this.swaps.remove(random.nextInt(this.swaps.size())));
         }

         this.swaps = arrayList;
      } else if (((InventoryManagerPatternMode)enumSetting.getValue()).equals(InventoryManagerPatternMode.SEQUENTIAL)) {
         this.swaps.sort(Comparator.comparing(integers11 -> (Integer)integers11[1]));
      } else if (((InventoryManagerPatternMode)enumSetting.getValue()).equals(InventoryManagerPatternMode.REVERSE)) {
         this.swaps.sort(Comparator.comparing(integers12 -> (Integer)integers12[1]));
         Collections.reverse(this.swaps);
      }
   }

   public void generateDropList(int i, int j, int k, int l, int m, int n, int i2, int i3, int i4, int i5) {
      this.processSwords(i);
      this.processBows(j);
      this.processTools(k, l, m, n);
      this.processArmor();
      this.processUtils(i2, i3, i4);
      this.processUhcItems();
      this.processOres();
      this.processPotions();
      this.processHeads(i5);
      this.processGarbage();
      InventoryManagerPatternMode inventoryManagerPatternMode = (InventoryManagerPatternMode)general.getSetting("Drop-Pattern").getValue();
      if (inventoryManagerPatternMode.equals(InventoryManagerPatternMode.RANDOM)) {
         ArrayList arrayList = new ArrayList();
         Random random = new Random();

         while (this.dropSlots.size() > 0) {
            arrayList.add(this.dropSlots.remove(random.nextInt(this.dropSlots.size())));
         }

         this.dropSlots = arrayList;
      } else if (inventoryManagerPatternMode.equals(InventoryManagerPatternMode.SEQUENTIAL)) {
         Collections.sort(this.dropSlots);
      } else if (inventoryManagerPatternMode.equals(InventoryManagerPatternMode.REVERSE)) {
         Collections.sort(this.dropSlots);
         Collections.reverse(this.dropSlots);
      }
   }

   public static boolean isHead(ItemStack itemStack) {
      return itemStack.getMetadata() == 3 && (itemStack.getDisplayName() == null || !itemStack.getDisplayName().contains("Backpack"));
   }

   public void processHeads(int i) {
      while (this.heads.size() > 0) {
         if (this.drop.getSetting("Util").asBoolean()) {
            InventoryManager.RankedSlot rankedSlot = this.heads.peek();
            int j = rankedSlot.slot;
            if (this.heads.size() > this.hypixelUHCItems.getSetting("Heads").asInt() && !this.isWhitelisted(j)) {
               int k = i - 1 + 36;
               if (k == j && this.heads.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot2 = this.heads.poll();
                  Slot slot2 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot2.slot);
                  Slot slot3 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.heads.peek().slot);
                  if (slot2.getHasStack() && slot3.getHasStack() && slot2.getStack().stackSize == slot3.getStack().stackSize) {
                     this.dropSlots.add(this.heads.poll().slot);
                     this.heads.add(rankedSlot2);
                     continue;
                  }

                  this.heads.add(rankedSlot2);
               }

               this.dropSlots.add(this.heads.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.heads.poll().slot);
      }

      while (this.whitelisted.size() > 0) {
         this.keepSlots.add(this.whitelisted.poll().slot);
      }
   }

   public void processGarbage() {
      while (this.garbage.size() > 0) {
         if (this.drop.getSetting("Garbage").asBoolean()) {
            this.dropSlots.add(this.garbage.poll().slot);
         } else {
            this.keepSlots.add(this.garbage.poll().slot);
         }
      }
   }

   public void processPotions() {
      boolean bl = this.drop.getSetting("Potions").asBoolean();

      while (this.speedPotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot = this.speedPotions.peek();
            int i = rankedSlot.slot;
            if (this.speedPotions.size() > this.potions.getSetting("Speed-Potions").asInt() && !this.isWhitelisted(i)) {
               this.dropSlots.add(this.speedPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.speedPotions.poll().slot);
      }

      while (this.strengthPotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot2 = this.strengthPotions.peek();
            int j = rankedSlot2.slot;
            if (this.strengthPotions.size() > this.potions.getSetting("Strength-Potions").asInt() && !this.isWhitelisted(j)) {
               this.dropSlots.add(this.strengthPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.strengthPotions.poll().slot);
      }

      while (this.regenerationPotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot3 = this.regenerationPotions.peek();
            int k = rankedSlot3.slot;
            if (this.regenerationPotions.size() > this.potions.getSetting("Regeneration-Potions").asInt() && !this.isWhitelisted(k)) {
               this.dropSlots.add(this.regenerationPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.regenerationPotions.poll().slot);
      }

      while (this.healingPotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot4 = this.healingPotions.peek();
            int l = rankedSlot4.slot;
            if (this.healingPotions.size() > this.potions.getSetting("Healing-Potions").asInt() && !this.isWhitelisted(l)) {
               this.dropSlots.add(this.healingPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.healingPotions.poll().slot);
      }

      while (this.jumpBoostPotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot5 = this.jumpBoostPotions.peek();
            int m = rankedSlot5.slot;
            if (this.jumpBoostPotions.size() > this.potions.getSetting("Jump-Boost-Potions").asInt() && !this.isWhitelisted(m)) {
               this.dropSlots.add(this.jumpBoostPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.jumpBoostPotions.poll().slot);
      }

      while (this.fireResistancePotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot6 = this.fireResistancePotions.peek();
            int n = rankedSlot6.slot;
            if (this.fireResistancePotions.size() > this.potions.getSetting("Fire-Resistance-Potions").asInt() && !this.isWhitelisted(n)) {
               this.dropSlots.add(this.fireResistancePotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.fireResistancePotions.poll().slot);
      }

      while (this.resistancePotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot7 = this.resistancePotions.peek();
            int i2 = rankedSlot7.slot;
            if (this.resistancePotions.size() > this.potions.getSetting("Resistance-Potions").asInt() && !this.isWhitelisted(i2)) {
               this.dropSlots.add(this.resistancePotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.resistancePotions.poll().slot);
      }

      while (this.invisibilityPotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot8 = this.invisibilityPotions.peek();
            int i3 = rankedSlot8.slot;
            if (this.invisibilityPotions.size() > this.potions.getSetting("Invisibility-Potions").asInt() && !this.isWhitelisted(i3)) {
               this.dropSlots.add(this.invisibilityPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.invisibilityPotions.poll().slot);
      }

      while (this.absorptionPotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot9 = this.absorptionPotions.peek();
            int i4 = rankedSlot9.slot;
            if (this.absorptionPotions.size() > this.potions.getSetting("Absorption-Potions").asInt() && !this.isWhitelisted(i4)) {
               this.dropSlots.add(this.absorptionPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.absorptionPotions.poll().slot);
      }

      while (this.weaknessPotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot10 = this.weaknessPotions.peek();
            int i5 = rankedSlot10.slot;
            if (this.weaknessPotions.size() > this.potions.getSetting("Weakness-Potions").asInt() && !this.isWhitelisted(i5)) {
               this.dropSlots.add(this.weaknessPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.weaknessPotions.poll().slot);
      }

      while (this.slownessPotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot11 = this.slownessPotions.peek();
            int i6 = rankedSlot11.slot;
            if (this.slownessPotions.size() > this.potions.getSetting("Slowness-Potions").asInt() && !this.isWhitelisted(i6)) {
               this.dropSlots.add(this.slownessPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.slownessPotions.poll().slot);
      }

      while (this.poisonPotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot12 = this.poisonPotions.peek();
            int i7 = rankedSlot12.slot;
            if (this.poisonPotions.size() > this.potions.getSetting("Poison-Potions").asInt() && !this.isWhitelisted(i7)) {
               this.dropSlots.add(this.poisonPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.poisonPotions.poll().slot);
      }

      while (this.harmingPotions.size() > 0) {
         if (bl) {
            InventoryManager.RankedSlot rankedSlot13 = this.harmingPotions.peek();
            int i8 = rankedSlot13.slot;
            if (this.harmingPotions.size() > this.potions.getSetting("Harming-Potions").asInt() && !this.isWhitelisted(i8)) {
               this.dropSlots.add(this.harmingPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.harmingPotions.poll().slot);
      }

      while (this.brewingItems.size() > 0) {
         if (this.potions.getSetting("Brewing-Items").asBoolean()) {
            this.keepSlots.add(this.brewingItems.poll().slot);
         } else {
            this.dropSlots.add(this.brewingItems.poll().slot);
         }
      }
   }

   public void processOres() {
      if (this.drop.getSetting("Ores").asBoolean()) {
         int i = this.ores.getSetting("Iron-Ingots").asInt();

         while (this.ironIngots.size() > 0) {
            InventoryManager.RankedSlot rankedSlot = this.ironIngots.peek();
            int j = rankedSlot.slot;
            if (this.ironIngots.size() > i && !this.isWhitelisted(j)) {
               this.dropSlots.add(this.ironIngots.poll().slot);
            } else {
               this.keepSlots.add(this.ironIngots.poll().slot);
            }
         }

         int k = this.ores.getSetting("Gold-Ingots").asInt();

         while (this.goldIngots.size() > 0) {
            InventoryManager.RankedSlot rankedSlot2 = this.goldIngots.peek();
            int l = rankedSlot2.slot;
            if (this.goldIngots.size() > k && !this.isWhitelisted(l)) {
               this.dropSlots.add(this.goldIngots.poll().slot);
            } else {
               this.keepSlots.add(this.goldIngots.poll().slot);
            }
         }

         int m = this.ores.getSetting("Iron-Ores").asInt();

         while (this.ironOres.size() > 0) {
            InventoryManager.RankedSlot rankedSlot3 = this.ironOres.peek();
            int n = rankedSlot3.slot;
            if (this.ironOres.size() > m && !this.isWhitelisted(n)) {
               this.dropSlots.add(this.ironOres.poll().slot);
            } else {
               this.keepSlots.add(this.ironOres.poll().slot);
            }
         }

         int i2 = this.ores.getSetting("Gold-Ores").asInt();

         while (this.goldOres.size() > 0) {
            InventoryManager.RankedSlot rankedSlot4 = this.goldOres.peek();
            int i3 = rankedSlot4.slot;
            if (this.goldOres.size() > i2 && !this.isWhitelisted(i3)) {
               this.dropSlots.add(this.goldOres.poll().slot);
            } else {
               this.keepSlots.add(this.goldOres.poll().slot);
            }
         }

         int i4 = this.ores.getSetting("Coal").asInt();

         while (this.coal.size() > 0) {
            InventoryManager.RankedSlot rankedSlot5 = this.coal.peek();
            int i5 = rankedSlot5.slot;
            if (this.coal.size() > i4 && !this.isWhitelisted(i5)) {
               this.dropSlots.add(this.coal.poll().slot);
            } else {
               this.keepSlots.add(this.coal.poll().slot);
            }
         }

         int i6 = this.ores.getSetting("Redstone").asInt();
         boolean bl = this.potions.getSetting("Brewing-Items").asBoolean();

         while (this.redstone.size() > 0) {
            InventoryManager.RankedSlot rankedSlot6 = this.redstone.peek();
            int i7 = rankedSlot6.slot;
            if (this.redstone.size() > Math.max(i6, bl ? 1 : 0) && !this.isWhitelisted(i7)) {
               this.dropSlots.add(this.redstone.poll().slot);
            } else {
               this.keepSlots.add(this.redstone.poll().slot);
            }
         }

         int i8 = this.ores.getSetting("Lapis").asInt();

         while (this.lapis.size() > 0) {
            InventoryManager.RankedSlot rankedSlot7 = this.lapis.peek();
            int i9 = rankedSlot7.slot;
            if (this.lapis.size() > i8 && !this.isWhitelisted(i9)) {
               this.dropSlots.add(this.lapis.poll().slot);
            } else {
               this.keepSlots.add(this.lapis.poll().slot);
            }
         }

         int i10 = this.ores.getSetting("Diamonds").asInt();

         while (this.diamonds.size() > 0) {
            InventoryManager.RankedSlot rankedSlot8 = this.diamonds.peek();
            int i11 = rankedSlot8.slot;
            if (this.diamonds.size() > i10 && !this.isWhitelisted(i11)) {
               this.dropSlots.add(this.diamonds.poll().slot);
            } else {
               this.keepSlots.add(this.diamonds.poll().slot);
            }
         }

         int i12 = this.ores.getSetting("Emeralds").asInt();

         while (this.emeralds.size() > 0) {
            InventoryManager.RankedSlot rankedSlot9 = this.emeralds.peek();
            int i13 = rankedSlot9.slot;
            if (this.emeralds.size() > i12 && !this.isWhitelisted(i13)) {
               this.dropSlots.add(this.emeralds.poll().slot);
            } else {
               this.keepSlots.add(this.emeralds.poll().slot);
            }
         }
      }
   }

   public void processUhcItems() {
      if (this.drop.getSetting("UHC").asBoolean()) {
         while (this.apples.size() > 0) {
            InventoryManager.RankedSlot rankedSlot = this.apples.peek();
            int i = rankedSlot.slot;
            if (this.apples.size() > this.uhc.getSetting("Apples").asInt() && !this.isWhitelisted(i)) {
               this.dropSlots.add(this.apples.poll().slot);
            } else {
               this.keepSlots.add(this.apples.poll().slot);
            }
         }

         while (this.craftingTables.size() > 0) {
            InventoryManager.RankedSlot rankedSlot2 = this.craftingTables.peek();
            int j = rankedSlot2.slot;
            if (this.craftingTables.size() > this.uhc.getSetting("Crafting-Tables").asInt() && !this.isWhitelisted(j)) {
               this.dropSlots.add(this.craftingTables.poll().slot);
            } else {
               this.keepSlots.add(this.craftingTables.poll().slot);
            }
         }

         while (this.sticks.size() > 0) {
            InventoryManager.RankedSlot rankedSlot3 = this.sticks.peek();
            int k = rankedSlot3.slot;
            if (this.sticks.size() > this.uhc.getSetting("Sticks").asInt() && !this.isWhitelisted(k)) {
               this.dropSlots.add(this.sticks.poll().slot);
            } else {
               this.keepSlots.add(this.sticks.poll().slot);
            }
         }

         while (this.anvils.size() > 0) {
            InventoryManager.RankedSlot rankedSlot4 = this.anvils.peek();
            int l = rankedSlot4.slot;
            if (this.anvils.size() > this.uhc.getSetting("Anvils").asInt() && !this.isWhitelisted(l)) {
               this.dropSlots.add(this.anvils.poll().slot);
            } else {
               this.keepSlots.add(this.anvils.poll().slot);
            }
         }

         while (this.enchantingTables.size() > 0) {
            InventoryManager.RankedSlot rankedSlot5 = this.enchantingTables.peek();
            int m = rankedSlot5.slot;
            if (this.enchantingTables.size() > this.uhc.getSetting("Enchanting-Tables").asInt() && !this.isWhitelisted(m)) {
               this.dropSlots.add(this.enchantingTables.poll().slot);
            } else {
               this.keepSlots.add(this.enchantingTables.poll().slot);
            }
         }

         while (this.furnaces.size() > 0) {
            InventoryManager.RankedSlot rankedSlot6 = this.furnaces.peek();
            int n = rankedSlot6.slot;
            if (this.furnaces.size() > this.uhc.getSetting("Furnaces").asInt() && !this.isWhitelisted(n)) {
               this.dropSlots.add(this.furnaces.poll().slot);
            } else {
               this.keepSlots.add(this.furnaces.poll().slot);
            }
         }

         while (this.expBottles.size() > 0) {
            InventoryManager.RankedSlot rankedSlot7 = this.expBottles.peek();
            int i2 = rankedSlot7.slot;
            if (this.expBottles.size() > this.uhc.getSetting("EXP-Bottles").asInt() && !this.isWhitelisted(i2)) {
               this.dropSlots.add(this.expBottles.poll().slot);
            } else {
               this.keepSlots.add(this.expBottles.poll().slot);
            }
         }

         this.missingMilkBuckets = this.utils.getSetting("Milk-Buckets").asInt();

         while (this.milkBuckets.size() > 0) {
            InventoryManager.RankedSlot rankedSlot8 = this.milkBuckets.peek();
            int i3 = rankedSlot8.slot;
            if (this.milkBuckets.size() > this.utils.getSetting("Milk-Buckets").asInt() && !this.isWhitelisted(i3)) {
               this.dropSlots.add(this.milkBuckets.poll().slot);
            } else {
               this.keepSlots.add(this.milkBuckets.poll().slot);
               this.missingMilkBuckets--;
            }
         }

         int i4 = this.missingWaterBuckets + this.missingLavaBuckets + this.missingMilkBuckets;

         while (this.emptyBuckets.size() > 0) {
            if (i4 > 0) {
               i4 = (int)(i4 - this.emptyBuckets.peek().rank);
               this.keepSlots.add(this.emptyBuckets.poll().slot);
            } else {
               this.dropSlots.add(this.emptyBuckets.poll().slot);
            }
         }

         while (this.chests.size() > 0) {
            InventoryManager.RankedSlot rankedSlot9 = this.chests.peek();
            int i5 = rankedSlot9.slot;
            if (this.chests.size() > this.uhc.getSetting("Chests").asInt() && !this.isWhitelisted(i5)) {
               this.dropSlots.add(this.chests.poll().slot);
            } else {
               this.keepSlots.add(this.chests.poll().slot);
            }
         }

         while (this.strings.size() > 0) {
            InventoryManager.RankedSlot rankedSlot10 = this.strings.peek();
            int i6 = rankedSlot10.slot;
            if (this.strings.size() > this.uhc.getSetting("String").asInt() && !this.isWhitelisted(i6)) {
               this.dropSlots.add(this.strings.poll().slot);
            } else {
               this.keepSlots.add(this.strings.poll().slot);
            }
         }
      }
   }

   public void processUtils(int i, int j, int k) {
      int l = this.utils.getSetting("Golden-Apples").asInt();
      int m = this.utils.getSetting("Ender-Pearls").asInt();
      if (this.drop.getSetting("Util").asBoolean()) {
         while (this.goldenApples.size() > 0) {
            InventoryManager.RankedSlot rankedSlot = this.goldenApples.peek();
            int n = rankedSlot.slot;
            if (this.goldenApples.size() > l && !this.isWhitelisted(n)) {
               int i2 = j - 1 + 36;
               if (i2 == n && this.goldenApples.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot2 = this.goldenApples.poll();
                  Slot slot2 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot2.slot);
                  Slot slot3 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.goldenApples.peek().slot);
                  if (slot2.getHasStack() && slot3.getHasStack() && slot2.getStack().stackSize == slot3.getStack().stackSize) {
                     this.dropSlots.add(this.goldenApples.poll().slot);
                     this.goldenApples.add(rankedSlot2);
                     continue;
                  }

                  this.goldenApples.add(rankedSlot2);
               }

               this.dropSlots.add(this.goldenApples.poll().slot);
            } else {
               this.keepSlots.add(this.goldenApples.poll().slot);
            }
         }

         while (this.enderPearls.size() > 0) {
            InventoryManager.RankedSlot rankedSlot3 = this.enderPearls.peek();
            int i3 = rankedSlot3.slot;
            if (this.enderPearls.size() > m && !this.isWhitelisted(i3)) {
               int i4 = k - 1 + 36;
               if (i4 == i3 && this.enderPearls.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot4 = this.enderPearls.poll();
                  Slot slot4 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot4.slot);
                  Slot slot5 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.enderPearls.peek().slot);
                  if (slot4.getHasStack() && slot5.getHasStack() && slot4.getStack().stackSize == slot5.getStack().stackSize) {
                     this.dropSlots.add(this.enderPearls.poll().slot);
                     this.enderPearls.add(rankedSlot4);
                     continue;
                  }

                  this.enderPearls.add(rankedSlot4);
               }

               this.dropSlots.add(this.enderPearls.poll().slot);
            } else {
               this.keepSlots.add(this.enderPearls.poll().slot);
            }
         }

         int i5 = this.utils.getSetting("Boats").asInt();

         while (this.boats.size() > 0) {
            InventoryManager.RankedSlot rankedSlot5 = this.boats.peek();
            int i6 = rankedSlot5.slot;
            if (this.boats.size() > i5 && !this.isWhitelisted(i6)) {
               this.dropSlots.add(this.boats.poll().slot);
            } else {
               this.keepSlots.add(this.boats.poll().slot);
            }
         }

         int i7 = this.utils.getSetting("TNT").asInt();

         while (this.tnt.size() > 0) {
            InventoryManager.RankedSlot rankedSlot6 = this.tnt.peek();
            int i8 = rankedSlot6.slot;
            if (this.tnt.size() > i7 && !this.isWhitelisted(i8)) {
               this.dropSlots.add(this.tnt.poll().slot);
            } else {
               this.keepSlots.add(this.tnt.poll().slot);
            }
         }

         int i9 = this.utils.getSetting("Arrows").asInt();

         while (this.arrows.size() > 0) {
            InventoryManager.RankedSlot rankedSlot7 = this.arrows.peek();
            int i10 = rankedSlot7.slot;
            if (this.arrows.size() > i9 && !this.isWhitelisted(i10)) {
               this.dropSlots.add(this.arrows.poll().slot);
            } else {
               this.keepSlots.add(this.arrows.poll().slot);
            }
         }

         int i11 = this.utils.getSetting("Eggs-Snowballs").asInt();

         while (this.eggsAndSnowballs.size() > 0) {
            InventoryManager.RankedSlot rankedSlot8 = this.eggsAndSnowballs.peek();
            int i12 = rankedSlot8.slot;
            if (this.eggsAndSnowballs.size() > i11 && !this.isWhitelisted(i12)) {
               this.dropSlots.add(this.eggsAndSnowballs.poll().slot);
            } else {
               this.keepSlots.add(this.eggsAndSnowballs.poll().slot);
            }
         }

         int i13 = this.utils.getSetting("Spawn-Eggs").asInt();

         while (this.spawnEggs.size() > 0) {
            InventoryManager.RankedSlot rankedSlot9 = this.spawnEggs.peek();
            int i14 = rankedSlot9.slot;
            if (this.spawnEggs.size() > i13 && !this.isWhitelisted(i14)) {
               this.dropSlots.add(this.spawnEggs.poll().slot);
            } else {
               this.keepSlots.add(this.spawnEggs.poll().slot);
            }
         }

         int i15 = this.utils.getSetting("Water-Buckets").asInt();

         while (this.waterBuckets.size() > 0) {
            InventoryManager.RankedSlot rankedSlot10 = this.waterBuckets.peek();
            int i16 = rankedSlot10.slot;
            if (this.waterBuckets.size() > i15 && !this.isWhitelisted(i16)) {
               this.dropSlots.add(this.waterBuckets.poll().slot);
            } else {
               this.keepSlots.add(this.waterBuckets.poll().slot);
               this.missingWaterBuckets--;
            }
         }

         int i17 = this.utils.getSetting("Lava-Buckets").asInt();

         while (this.lavaBuckets.size() > 0) {
            InventoryManager.RankedSlot rankedSlot11 = this.lavaBuckets.peek();
            int i18 = rankedSlot11.slot;
            if (this.lavaBuckets.size() > i17 && !this.isWhitelisted(i18)) {
               this.dropSlots.add(this.lavaBuckets.poll().slot);
            } else {
               this.keepSlots.add(this.lavaBuckets.poll().slot);
               this.missingLavaBuckets--;
            }
         }

         int i19 = this.utils.getSetting("Blocks").asInt();
         if (this.drop.getSetting("Blocks").asBoolean()) {
            while (this.blocks.size() > 0) {
               InventoryManager.RankedSlot rankedSlot12 = this.blocks.peek();
               int i20 = rankedSlot12.slot;
               if (this.blocks.size() > i19 && !this.isWhitelisted(i20)) {
                  int i21 = i - 1 + 36;
                  if (i21 == i20 && this.blocks.size() > 1) {
                     InventoryManager.RankedSlot rankedSlot13 = this.blocks.poll();
                     Slot slot6 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot13.slot);
                     Slot slot7 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.blocks.peek().slot);
                     if (slot6.getHasStack() && slot7.getHasStack() && slot6.getStack().stackSize == slot7.getStack().stackSize) {
                        this.dropSlots.add(this.blocks.poll().slot);
                        this.blocks.add(rankedSlot13);
                        continue;
                     }

                     this.blocks.add(rankedSlot13);
                  }

                  this.dropSlots.add(this.blocks.poll().slot);
               } else {
                  this.keepSlots.add(this.blocks.poll().slot);
               }
            }
         }

         int i22 = this.utils.getSetting("Sand-Gravel").asInt();

         while (this.sandAndGravel.size() > 0) {
            InventoryManager.RankedSlot rankedSlot14 = this.sandAndGravel.peek();
            int i23 = rankedSlot14.slot;
            if (this.sandAndGravel.size() > i22 && !this.isWhitelisted(i23)) {
               this.dropSlots.add(this.sandAndGravel.poll().slot);
            } else {
               this.keepSlots.add(this.sandAndGravel.poll().slot);
            }
         }

         int i24 = this.utils.getSetting("Cobwebs").asInt();

         while (this.cobwebs.size() > 0) {
            InventoryManager.RankedSlot rankedSlot15 = this.cobwebs.peek();
            int i25 = rankedSlot15.slot;
            if (this.cobwebs.size() > i24 && !this.isWhitelisted(i25)) {
               this.dropSlots.add(this.cobwebs.poll().slot);
            } else {
               this.keepSlots.add(this.cobwebs.poll().slot);
            }
         }

         int i26 = this.utils.getSetting("Food").asInt();
         if (this.drop.getSetting("Food").asBoolean()) {
            while (this.food.size() > 0) {
               InventoryManager.RankedSlot rankedSlot16 = this.food.peek();
               int i27 = rankedSlot16.slot;
               if (this.food.size() > i26 && !this.isWhitelisted(i27)) {
                  this.dropSlots.add(this.food.poll().slot);
               } else {
                  this.keepSlots.add(this.food.poll().slot);
               }
            }
         }
      }
   }

   public float foodRank(ItemStack itemStack) {
      float f = -1.0F;
      if (itemStack != null && itemStack.getItem() instanceof ItemFood && !this.nonFoods.contains(itemStack.getItem().getUnlocalizedName())) {
         ItemFood itemFood = (ItemFood)itemStack.getItem();
         f = itemStack.stackSize * itemFood.getSaturationModifier(itemStack);
      }

      return f;
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

   public void processArmor() {
      if (this.drop.getSetting("Armor").asBoolean()) {
         boolean bl = this.enchants.getSetting("Combinable-Protection").asBoolean();
         int i = this.armor.getSetting("Helmets").asInt();
         int j = this.armor.getSetting("Chestplates").asInt();

         while (this.helmets.size() > 0) {
            InventoryManager.RankedSlot rankedSlot = this.helmets.peek();
            int k = rankedSlot.slot;
            if (this.helmets.size() > i && (!bl || !this.canCombineProtection(this.bestHelmetSlot, k)) && !this.isWhitelisted(k)) {
               byte b = 5;
               if (b == k && this.helmets.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot2 = this.helmets.poll();
                  Slot slot2 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot2.slot);
                  Slot slot3 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.helmets.peek().slot);
                  if (slot2.getHasStack() && slot3.getHasStack() && this.armorScore(slot2.getStack()) == this.armorScore(slot3.getStack())) {
                     this.dropSlots.add(this.helmets.poll().slot);
                     this.helmets.add(rankedSlot2);
                     continue;
                  }

                  this.helmets.add(rankedSlot2);
               }

               this.dropSlots.add(this.helmets.poll().slot);
            } else {
               this.keepSlots.add(this.helmets.poll().slot);
            }
         }

         while (this.chestplates.size() > 0) {
            InventoryManager.RankedSlot rankedSlot3 = this.chestplates.peek();
            int l = rankedSlot3.slot;
            if (this.chestplates.size() > j && (!bl || !this.canCombineProtection(this.bestChestplateSlot, l)) && !this.isWhitelisted(l)) {
               byte b2 = 6;
               if (b2 == l && this.chestplates.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot4 = this.chestplates.poll();
                  Slot slot4 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot4.slot);
                  Slot slot5 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.chestplates.peek().slot);
                  if (slot4.getHasStack() && slot5.getHasStack() && this.armorScore(slot4.getStack()) == this.armorScore(slot5.getStack())) {
                     this.dropSlots.add(this.chestplates.poll().slot);
                     this.chestplates.add(rankedSlot4);
                     continue;
                  }

                  this.chestplates.add(rankedSlot4);
               }

               this.dropSlots.add(this.chestplates.poll().slot);
            } else {
               this.keepSlots.add(this.chestplates.poll().slot);
            }
         }

         int m = this.armor.getSetting("Leggings").asInt();

         while (this.leggings.size() > 0) {
            InventoryManager.RankedSlot rankedSlot5 = this.leggings.peek();
            int n = rankedSlot5.slot;
            if (this.leggings.size() > m && (!bl || !this.canCombineProtection(this.bestLeggingsSlot, n)) && !this.isWhitelisted(n)) {
               byte b3 = 7;
               if (b3 == n && this.leggings.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot6 = this.leggings.poll();
                  Slot slot6 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot6.slot);
                  Slot slot7 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.leggings.peek().slot);
                  if (slot6.getHasStack() && slot7.getHasStack() && this.armorScore(slot6.getStack()) == this.armorScore(slot7.getStack())) {
                     this.dropSlots.add(this.leggings.poll().slot);
                     this.leggings.add(rankedSlot6);
                     continue;
                  }

                  this.leggings.add(rankedSlot6);
               }

               this.dropSlots.add(this.leggings.poll().slot);
            } else {
               this.keepSlots.add(this.leggings.poll().slot);
            }
         }

         int i2 = this.armor.getSetting("Boots").asInt();

         while (this.boots.size() > 0) {
            InventoryManager.RankedSlot rankedSlot7 = this.boots.peek();
            int i3 = rankedSlot7.slot;
            if (this.boots.size() > i2 && (!bl || !this.canCombineProtection(this.bestBootsSlot, i3)) && !this.isWhitelisted(i3)) {
               byte b4 = 8;
               if (b4 == i3 && this.boots.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot8 = this.boots.poll();
                  Slot slot8 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot8.slot);
                  Slot slot9 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.boots.peek().slot);
                  if (slot8.getHasStack() && slot9.getHasStack() && this.armorScore(slot8.getStack()) == this.armorScore(slot9.getStack())) {
                     this.dropSlots.add(this.boots.poll().slot);
                     this.boots.add(rankedSlot8);
                     continue;
                  }

                  this.boots.add(rankedSlot8);
               }

               this.dropSlots.add(this.boots.poll().slot);
            } else {
               this.keepSlots.add(this.boots.poll().slot);
            }
         }
      }
   }

   public boolean canCombineProtection(int i, int j) {
      Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(i);
      Slot slot2 = mc.thePlayer.inventoryContainer.inventorySlots.get(j);
      if (slot.getHasStack() && slot2.getHasStack()) {
         ItemStack itemStack = slot.getStack();
         ItemStack itemStack2 = slot2.getStack();
         return this.isCombinable(itemStack) && this.isCombinable(itemStack2)
            ? EnchantmentHelper.getEnchantmentLevel(0, itemStack) > 0
               && EnchantmentHelper.getEnchantmentLevel(0, itemStack) < 4
               && itemStack.getUnlocalizedName().equals(itemStack2.getUnlocalizedName())
               && EnchantmentHelper.getEnchantmentLevel(0, itemStack) == EnchantmentHelper.getEnchantmentLevel(0, itemStack2)
            : false;
      } else {
         return false;
      }
   }

   public float armorScore(ItemStack itemStack) {
      return InventoryUtil.getArmorScore(itemStack);
   }

   public float armorRank(ItemStack itemStack, int i) {
      float f = 0.0F;
      if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
         ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
         f = (float)(f + (itemArmor.damageReduceAmount + (100 - itemArmor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(0, itemStack) * 0.0075));
         f += InventoryUtil.getArmorScore(itemStack);
         if (this.remainingDurability(itemStack) < i) {
            f /= 1000.0F;
         }
      }

      return f;
   }

   public void processTools(int i, int j, int k, int l) {
      if (this.drop.getSetting("Tools").asBoolean()) {
         int m = this.tools.getSetting("Pickaxes").asInt();

         while (this.pickaxes.size() > 0) {
            InventoryManager.RankedSlot rankedSlot = this.pickaxes.peek();
            int n = rankedSlot.slot;
            if (this.pickaxes.size() > m && !this.isWhitelisted(n)) {
               int i2 = i - 1 + 36;
               if (i2 == n && this.pickaxes.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot2 = this.pickaxes.poll();
                  Slot slot2 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot2.slot);
                  Slot slot3 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.pickaxes.peek().slot);
                  if (slot2.getHasStack() && slot3.getHasStack() && this.pickaxeScore(slot2.getStack()) == this.pickaxeScore(slot3.getStack())) {
                     this.dropSlots.add(this.pickaxes.poll().slot);
                     this.pickaxes.add(rankedSlot2);
                     continue;
                  }

                  this.pickaxes.add(rankedSlot2);
               }

               this.dropSlots.add(this.pickaxes.poll().slot);
            } else {
               this.keepSlots.add(this.pickaxes.poll().slot);
            }
         }

         int i3 = this.tools.getSetting("Shovels").asInt();

         while (this.shovels.size() > 0) {
            InventoryManager.RankedSlot rankedSlot3 = this.shovels.peek();
            int i4 = rankedSlot3.slot;
            if (this.shovels.size() > i3 && !this.isWhitelisted(i4)) {
               int i5 = j - 1 + 36;
               if (i5 == i4 && this.shovels.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot4 = this.shovels.poll();
                  Slot slot4 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot4.slot);
                  Slot slot5 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.shovels.peek().slot);
                  if (slot4.getHasStack() && slot5.getHasStack() && this.shovelScore(slot4.getStack()) == this.shovelScore(slot5.getStack())) {
                     this.dropSlots.add(this.shovels.poll().slot);
                     this.shovels.add(rankedSlot4);
                     continue;
                  }

                  this.shovels.add(rankedSlot4);
               }

               this.dropSlots.add(this.shovels.poll().slot);
            } else {
               this.keepSlots.add(this.shovels.poll().slot);
            }
         }

         int i6 = this.tools.getSetting("Axes").asInt();

         while (this.axes.size() > 0) {
            InventoryManager.RankedSlot rankedSlot5 = this.axes.peek();
            int i7 = rankedSlot5.slot;
            if (this.axes.size() > i6 && !this.isWhitelisted(i7)) {
               int i8 = k - 1 + 36;
               if (i8 == i7 && this.axes.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot6 = this.axes.poll();
                  Slot slot6 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot6.slot);
                  Slot slot7 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.axes.peek().slot);
                  if (slot6.getHasStack() && slot7.getHasStack() && this.axeScore(slot6.getStack()) == this.axeScore(slot7.getStack())) {
                     this.dropSlots.add(this.axes.poll().slot);
                     this.axes.add(rankedSlot6);
                     continue;
                  }

                  this.axes.add(rankedSlot6);
               }

               this.dropSlots.add(this.axes.poll().slot);
            } else {
               this.keepSlots.add(this.axes.poll().slot);
            }
         }

         int i9 = this.tools.getSetting("Fishing-Rods").asInt();

         while (this.fishingRods.size() > 0) {
            InventoryManager.RankedSlot rankedSlot7 = this.fishingRods.peek();
            int i10 = rankedSlot7.slot;
            if (this.fishingRods.size() > i9 && !this.isWhitelisted(i10)) {
               int i11 = l - 1 + 36;
               if (i11 == i10 && this.fishingRods.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot8 = this.fishingRods.poll();
                  Slot slot8 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot8.slot);
                  Slot slot9 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.fishingRods.peek().slot);
                  if (slot8.getHasStack() && slot9.getHasStack() && this.fishingRodScore(slot8.getStack()) == this.fishingRodScore(slot9.getStack())) {
                     this.dropSlots.add(this.fishingRods.poll().slot);
                     this.fishingRods.add(rankedSlot8);
                     continue;
                  }

                  this.fishingRods.add(rankedSlot8);
               }

               this.dropSlots.add(this.fishingRods.poll().slot);
            } else {
               this.keepSlots.add(this.fishingRods.poll().slot);
            }
         }

         int i12 = this.tools.getSetting("Shears").asInt();

         while (this.shears.size() > 0) {
            InventoryManager.RankedSlot rankedSlot9 = this.shears.peek();
            int i13 = rankedSlot9.slot;
            if (this.shears.size() > i12 && !this.isWhitelisted(i13)) {
               this.dropSlots.add(this.shears.poll().slot);
            } else {
               this.keepSlots.add(this.shears.poll().slot);
            }
         }

         int i14 = this.tools.getSetting("Flint-Steel").asInt();

         while (this.flintAndSteels.size() > 0) {
            InventoryManager.RankedSlot rankedSlot10 = this.flintAndSteels.peek();
            int i15 = rankedSlot10.slot;
            if (this.flintAndSteels.size() > i14 && !this.isWhitelisted(i15)) {
               this.dropSlots.add(this.flintAndSteels.poll().slot);
            } else {
               this.keepSlots.add(this.flintAndSteels.poll().slot);
            }
         }
      }
   }

   public float fishingRodScore(ItemStack itemStack) {
      float f = -1.0F;
      if (itemStack != null && itemStack.getUnlocalizedName().equals("item.fishingRod")) {
         f = 1.0F;
         f += EnchantmentHelper.getEnchantmentLevel(62, itemStack);
         f += EnchantmentHelper.getEnchantmentLevel(19, itemStack) / 1000;
      }

      return f;
   }

   public float fishingRodRank(ItemStack itemStack, int i) {
      float f = -1.0F;
      if (itemStack != null) {
         f = EnchantmentHelper.getEnchantmentLevel(62, itemStack);
         f += EnchantmentHelper.getEnchantmentLevel(19, itemStack) / 1000;
         if (this.remainingDurability(itemStack) < i) {
            f /= 1000.0F;
         }
      }

      return f;
   }

   public float pickaxeScore(ItemStack itemStack) {
      float f = -1.0F;
      if (itemStack != null) {
         f = InventoryUtil.getToolScore(itemStack);
      }

      return f;
   }

   public float pickaxeRank(ItemStack itemStack, int i) {
      float f = -1.0F;
      if (itemStack != null) {
         f = itemStack.getItem().getStrVsBlock(itemStack, Block.getBlockById(4))
            * Math.max(1, (int)Math.pow(EnchantmentHelper.getEnchantmentLevel(32, itemStack), 2.0));
         if (this.remainingDurability(itemStack) < i) {
            f /= 1000.0F;
         }
      }

      return f;
   }

   public float shovelScore(ItemStack itemStack) {
      float f = -1.0F;
      if (itemStack != null) {
         f = InventoryUtil.getToolScore(itemStack);
      }

      return f;
   }

   public float shovelRank(ItemStack itemStack, int i) {
      float f = -1.0F;
      if (itemStack != null) {
         f = itemStack.getItem().getStrVsBlock(itemStack, Block.getBlockById(3))
            * Math.max(1, (int)Math.pow(EnchantmentHelper.getEnchantmentLevel(32, itemStack), 2.0));
         if (this.remainingDurability(itemStack) < i) {
            f /= 1000.0F;
         }
      }

      return f;
   }

   public float axeScore(ItemStack itemStack) {
      float f = -1.0F;
      if (itemStack != null) {
         f = InventoryUtil.getToolScore(itemStack);
      }

      return f;
   }

   public float axeRank(ItemStack itemStack, int i) {
      float f = -1.0F;
      if (itemStack != null) {
         f = itemStack.getItem().getStrVsBlock(itemStack, Block.getBlockById(17))
            * Math.max(1, (int)Math.pow(EnchantmentHelper.getEnchantmentLevel(32, itemStack), 2.0));
         if (this.remainingDurability(itemStack) < i) {
            f /= 1000.0F;
         }
      }

      return f;
   }

   public float bowScore(ItemStack itemStack) {
      float f = -1.0F;
      if (itemStack != null && itemStack.getUnlocalizedName().equals("item.bow")) {
         f = 1.0F;
         f += EnchantmentHelper.getEnchantmentLevel(48, itemStack);
      }

      return f;
   }

   public float bowRank(ItemStack itemStack, int i) {
      float f = -1.0F;
      if (itemStack != null && itemStack.getUnlocalizedName().equals("item.bow")) {
         f = 1.0F;
         f += EnchantmentHelper.getEnchantmentLevel(48, itemStack);
         if (this.remainingDurability(itemStack) < i) {
            f /= 1000.0F;
         }
      }

      return f;
   }

   public float damageScore(ItemStack itemStack) {
      float f = -1.0F;
      if (itemStack != null) {
         f = InventoryUtil.getDamageScore(itemStack);
      }

      return f;
   }

   public float swordScore(ItemStack itemStack) {
      float f = -1.0F;
      if (itemStack != null) {
         f = InventoryUtil.getDamageScore(itemStack);
      }

      return f;
   }

   public float swordRank(ItemStack itemStack, int i) {
      float f = -1.0F;
      if (itemStack != null) {
         f = InventoryUtil.getDamageScore(itemStack);
         if (this.remainingDurability(itemStack) < i) {
            f /= 1000.0F;
         }
      }

      return f;
   }

   public void processBows(int i) {
      boolean bl = this.enchants.getSetting("Combinable-Power").asBoolean();

      while (this.bows.size() > 0) {
         if (this.drop.getSetting("Bows").asBoolean()) {
            InventoryManager.RankedSlot rankedSlot = this.bows.peek();
            int j = rankedSlot.slot;
            if (this.bows.size() > this.tools.getSetting("Bows").asInt()
               && (!bl || !this.canCombinePower(this.bestBowSlot, j))
               && !this.isWhitelisted(j)) {
               int k = i - 1 + 36;
               if (k == j && this.bows.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot2 = this.bows.poll();
                  Slot slot2 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot2.slot);
                  Slot slot3 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bows.peek().slot);
                  if (slot2.getHasStack() && slot3.getHasStack() && this.bowScore(slot2.getStack()) == this.bowScore(slot3.getStack())) {
                     this.dropSlots.add(this.bows.poll().slot);
                     this.bows.add(rankedSlot2);
                     continue;
                  }

                  this.bows.add(rankedSlot2);
               }

               this.dropSlots.add(this.bows.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.bows.poll().slot);
      }
   }

   public boolean canCombinePower(int i, int j) {
      Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(i);
      Slot slot2 = mc.thePlayer.inventoryContainer.inventorySlots.get(j);
      if (slot.getHasStack() && slot2.getHasStack()) {
         ItemStack itemStack = slot.getStack();
         ItemStack itemStack2 = slot2.getStack();
         return this.isCombinable(itemStack) && this.isCombinable(itemStack2)
            ? EnchantmentHelper.getEnchantmentLevel(48, itemStack) > 0
               && EnchantmentHelper.getEnchantmentLevel(48, itemStack) < 5
               && itemStack.getUnlocalizedName().equals(itemStack2.getUnlocalizedName())
               && EnchantmentHelper.getEnchantmentLevel(48, itemStack) == EnchantmentHelper.getEnchantmentLevel(48, itemStack2)
            : false;
      } else {
         return false;
      }
   }

   public boolean shouldSwap() {
      if (this.swaps.size() > 0 && this.delay.hasPassed()) {
         EnumSetting enumSetting = (EnumSetting)general.getSetting("Swap-Mode");
         return this.isModeActive(enumSetting);
      } else {
         return false;
      }
   }

   public void doSwaps() {
      for (int i = 0; i < this.swaps.size(); i++) {
         Integer[] integers = this.swaps.get(i);
         if (this.canSwap(integers[0], integers[1])) {
            this.swap(integers[0], integers[1]);
            this.swaps.remove(i);
         }
      }
   }

   public boolean canSwap(int i, int j) {
      return this.heldTime(i) >= general.getSetting("Min-Hold-Time").asInt()
         && this.delay.hasPassed()
         && j >= 0
         && j <= 8
         && i >= 0
         && i <= mc.thePlayer.inventoryContainer.inventorySlots.size() - 1;
   }

   public long heldTime(int i) {
      return System.currentTimeMillis() - this.changeTimes.get(i);
   }

   public void swap(int i, int j) {
      this.replaceDropSlot(i, 36 + j);
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, j, 2, mc.thePlayer);
      this.delay = new InventoryManager.Delay(general.getSetting("Swap-Delay").asInt());
   }

   public void replaceDropSlot(int i, int j) {
      for (int k = 0; k < this.dropSlots.size(); k++) {
         if (this.dropSlots.get(k) == i) {
            this.dropSlots.set(k, j);
         }
      }
   }

   public void processSwords(int i) {
      int j = this.tools.getSetting("Swords").asInt();
      boolean bl = this.enchants.getSetting("Combinable-Sharpness").asBoolean();
      if (this.drop.getSetting("Swords").asBoolean()) {
         while (this.swords.size() > 0) {
            InventoryManager.RankedSlot rankedSlot = this.swords.peek();
            int k = rankedSlot.slot;
            if (this.swords.size() > j && (!bl || !this.canCombineSharpness(this.bestSwordSlot, k)) && !this.isWhitelisted(k)) {
               int l = i - 1 + 36;
               if (l == k && this.swords.size() > 1) {
                  InventoryManager.RankedSlot rankedSlot2 = this.swords.poll();
                  Slot slot2 = mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot2.slot);
                  Slot slot3 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.swords.peek().slot);
                  if (slot2.getHasStack() && slot3.getHasStack() && this.swordScore(slot2.getStack()) == this.swordScore(slot3.getStack())) {
                     this.dropSlots.add(this.swords.poll().slot);
                     this.swords.add(rankedSlot2);
                     continue;
                  }

                  this.swords.add(rankedSlot2);
               }

               this.dropSlots.add(this.swords.poll().slot);
            } else {
               this.keepSlots.add(this.swords.poll().slot);
            }
         }
      }
   }

   public boolean isWhitelisted(int i) {
      Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(i);
      if (slot.getHasStack() && !this.dropSlots.contains(i)) {
         ItemStack itemStack = slot.getStack();
         if (itemStack != null) {
            String string = itemStack.getUnlocalizedName();
            String string2 = itemStack.getDisplayName();
            if (this.hypixelUHCItems.getSetting("Forges").asBoolean() && string.equals("tile.furnace") && string2.contains("Forge")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Dragon-Sword").asBoolean()
               && string.equals("item.swordDiamond")
               && this.getLore(itemStack).contains("Weaponsmith Ultimate")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Bloodlust").asBoolean()
               && string.equals("item.swordDiamond")
               && this.getLore(itemStack).contains("Gains Sharpness II after 1 kill")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Exodus").asBoolean() && string.equals("item.helmetDiamond") && string2.contains("xodus")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Barbarian").asBoolean()
               && string.equals("item.chestplateDiamond")
               && this.getLore(itemStack).contains("Resistance I (While Wearing)")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Vidar").asBoolean() && string.equals("item.bootsDiamond") && string2.contains("Vidar")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Scythe").asBoolean() && string.contains("item.hoe") && string2.contains("Scythe")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Perun").asBoolean() && string.contains("item.hatchet") && string2.contains("Perun")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Excalibur").asBoolean() && string.contains("item.sword") && string2.contains("Excalibur")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Anduril").asBoolean()
               && string.equals("item.swordIron")
               && this.getLore(itemStack).contains("Resistance I (While Holding)")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Philo").asBoolean() && string.equals("item.pickaxeDiamond") && string2.contains("Philo")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Expert-Seal").asBoolean() && string.equals("item.netherStar")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Artemis-Bow").asBoolean() && string.equals("item.bow") && string2.contains("Artemis")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Backpack").asBoolean() && itemStack.getItem() instanceof ItemSkull && string2.contains("Backpack")) {
               return true;
            }

            if (!this.hypixelUHCCrafts.getSetting("Backpack").asBoolean()
               || !string.equals("tile.chest") && !string.equals("item.stick") && !string.equals("item.leather")) {
               if (this.hypixelUHCItems.getSetting("Modular-Bow").asBoolean() && string.equals("item.bow") && string2.contains("Modular")) {
                  return true;
               }

               if (!this.hypixelUHCCrafts.getSetting("Eves-Temptation").asBoolean()
                  || !string.equals("item.bone") && !string.equals("item.dyePowder.white") && !string2.contains("Bone Meal")) {
                  if (!this.hypixelUHCCrafts.getSetting("Enlightening-Pack").asBoolean() || !string.equals("item.redstone") && !string.equals("tile.blockRedstone")
                     )
                   {
                     if (!this.hypixelUHCCrafts.getSetting("Sharpness-Books").asBoolean()
                        || !string.equals("item.paper") && !string.equals("item.swordIron") && !string.equals("item.flint")) {
                        if (!this.hypixelUHCCrafts.getSetting("Protection-Books").asBoolean()
                           || !string.equals("item.reeds") && !string.equals("item.paper") && !string.equals("item.ingotIron")) {
                           if (!this.hypixelUHCCrafts.getSetting("Seven-League").asBoolean() || !string.equals("item.enderPearl") && !string.equals("item.feather")
                              )
                            {
                              if (!this.hypixelUHCCrafts.getSetting("Philo").asBoolean()
                                 || !string.equals("tile.oreIron") && !string.equals("tile.oreGold") && !string.equals("tile.blockLapis")) {
                                 if (!this.hypixelUHCCrafts.getSetting("Holy-Water").asBoolean()
                                    || !string.equals("item.glassBottle") && !string.equals("item.record") && !string.equals("tile.blockRedstone")) {
                                    if (!this.hypixelUHCCrafts.getSetting("Arrow-Economy").asBoolean()
                                       || !string.equals("item.feather") && !string.equals("item.flint")) {
                                       if (this.enchants.getSetting("Fire-Aspect").asBoolean() && EnchantmentHelper.getEnchantmentLevel(20, itemStack) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Punch").asBoolean() && EnchantmentHelper.getEnchantmentLevel(49, itemStack) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Knockback").asBoolean() && EnchantmentHelper.getEnchantmentLevel(19, itemStack) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Thorns").asBoolean() && EnchantmentHelper.getEnchantmentLevel(7, itemStack) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Fortune").asBoolean() && EnchantmentHelper.getEnchantmentLevel(35, itemStack) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Flame").asBoolean() && EnchantmentHelper.getEnchantmentLevel(50, itemStack) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Looting").asBoolean() && EnchantmentHelper.getEnchantmentLevel(21, itemStack) > 0) {
                                          return true;
                                       }

                                       boolean bl = this.hypixelUHCItems.getSetting("Recipe-Books").asBoolean();
                                       if (bl && string.equals("item.enchantedBook")) {
                                          Matcher matcher2 = this.recipeBookPattern.matcher(string2);
                                          if (matcher2.find()) {
                                             return true;
                                          }
                                       } else if (!bl && string.equals("item.enchantedBook")) {
                                          Matcher matcher3 = this.recipeBookPattern.matcher(string2);
                                          if (matcher3.find()) {
                                             return false;
                                          }
                                       }

                                       return this.enchants.getSetting("Enchanted-Books").asBoolean() && string.equals("item.enchantedBook");
                                    }

                                    return true;
                                 }

                                 return true;
                              }

                              return true;
                           }

                           return true;
                        }

                        return true;
                     }

                     return true;
                  }

                  return true;
               }

               return true;
            }

            return true;
         }
      }

      return false;
   }

   public boolean canCombineSharpness(int i, int j) {
      Slot slot = mc.thePlayer.inventoryContainer.inventorySlots.get(i);
      Slot slot2 = mc.thePlayer.inventoryContainer.inventorySlots.get(j);
      if (slot.getHasStack() && slot2.getHasStack()) {
         ItemStack itemStack = slot.getStack();
         ItemStack itemStack2 = slot2.getStack();
         return this.isCombinable(itemStack) && this.isCombinable(itemStack2)
            ? EnchantmentHelper.getEnchantmentLevel(16, itemStack) > 0
               && EnchantmentHelper.getEnchantmentLevel(16, itemStack) < 5
               && itemStack.getUnlocalizedName().equals(itemStack2.getUnlocalizedName())
               && EnchantmentHelper.getEnchantmentLevel(16, itemStack) == EnchantmentHelper.getEnchantmentLevel(16, itemStack2)
            : false;
      } else {
         return false;
      }
   }

   public boolean isCombinable(ItemStack itemStack) {
      String string = itemStack.getUnlocalizedName();
      String string2 = itemStack.getDisplayName();
      if (string.equals("item.chestplateDiamond") && this.getLore(itemStack).contains("Resistance I (While Wearing)")) {
         return false;
      } else if (string.equals("item.swordIron") && this.getLore(itemStack).contains("Resistance I (While Holding)")) {
         return false;
      } else if (string.contains("item.sword") && string2.contains("Excalibur")) {
         return false;
      } else if (string.equals("item.swordIron") && string2.contains("Apprentice")) {
         return false;
      } else {
         return string.equals("item.helmetIron") && string2.contains("Apprentice") ? false : !string.equals("item.helmetDiamond") || !string2.contains("Tarnhelm");
      }
   }

   public String getLore(ItemStack itemStack) {
      StringBuilder stringBuilder = new StringBuilder();
      if (itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("display", 10)) {
         NBTTagCompound nbtTagCompound = itemStack.getTagCompound().getCompoundTag("display");
         NBTTagList nbtTagList = nbtTagCompound.getTagList("Lore", 8);
         if (nbtTagList.tagCount() > 0) {
            for (int i = 0; i < nbtTagList.tagCount(); i++) {
               stringBuilder.append(nbtTagList.getStringTagAt(i));
            }
         }
      }

      return stringBuilder.toString();
   }

   public boolean shouldDrop() {
      if (this.dropSlots.size() > 0 && this.delay.hasPassed()) {
         EnumSetting enumSetting = (EnumSetting)general.getSetting("Drop-Mode");
         return this.isModeActive(enumSetting);
      } else {
         return false;
      }
   }

   private boolean isModeActive(EnumSetting<InventoryManagerMode> enumSetting) {
      if (mc.currentScreen instanceof GuiChest) {
         return false;
      } else if (enumSetting.getValue() == InventoryManagerMode.INVENTORY && mc.currentScreen instanceof GuiInventory) {
         return true;
      } else if (enumSetting.getValue() == InventoryManagerMode.INVENTORY_LSHIFT && mc.currentScreen instanceof GuiInventory && Keyboard.isKeyDown(42)) {
         return true;
      } else {
         return enumSetting.getValue() == InventoryManagerMode.NOT_MOVING && mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F
            ? true
            : enumSetting.getValue() == InventoryManagerMode.ALWAYS;
      }
   }

   public void doDrops() {
      for (int i = 0; i < this.dropSlots.size(); i++) {
         if (this.canDrop(this.dropSlots.get(i))) {
            this.dropSlot(this.dropSlots.remove(i));
         }
      }
   }

   public boolean canDrop(int i) {
      return this.heldTime(i) >= general.getSetting("Min-Hold-Time").asInt()
         && this.delay.hasPassed()
         && i >= 0
         && i <= mc.thePlayer.inventoryContainer.inventorySlots.size() - 1;
   }

   public void dropSlot(int i) {
      if (i >= 0 && i < mc.thePlayer.inventoryContainer.inventorySlots.size()) {
         InventoryUtil.dropSlot(i, 0);
         this.delay = new InventoryManager.Delay(general.getSetting("Drop-Delay").asInt());
      }
   }

   public boolean isHotbarSlot(int i) {
      return i >= 36 && i <= 44;
   }

   public void drawSlotWarning(RenderEvent renderEvent) {
      if (!this.slotsUnique) {
         mc.fontRendererObj
            .drawStringWithShadow(
               "Some InventoryManager slots are the same, make each slot unique for sorting to work",
               renderEvent.getResolution().getScaledWidth() / 2.0F
                  - mc.fontRendererObj.getStringWidth("Some InventoryManager slots are the same, make each slot unique for sorting to work") / 2.0F,
               renderEvent.getResolution().getScaledHeight() - 100,
               Color.RED.getRGB()
            );
         this.delay = new InventoryManager.Delay(500L);
      }
   }

   public void update() {
      int i = slots.getSetting("Sword-Slot").asInt();
      int j = slots.getSetting("Golden-Apple-Slot").asInt();
      int k = slots.getSetting("Head-Slot").asInt();
      int l = slots.getSetting("Pearl-Slot").asInt();
      int m = slots.getSetting("Bow-Slot").asInt();
      int n = slots.getSetting("Fishing-Rod-Slot").asInt();
      int i2 = slots.getSetting("Pickaxe-Slot").asInt();
      int i3 = slots.getSetting("Axe-Slot").asInt();
      int i4 = slots.getSetting("Shovel-Slot").asInt();
      int i5 = slots.getSetting("Blocks-Slot").asInt();
      boolean bl = this.slotsUnique = !OldInventoryManager.hasDuplicateSlots(new int[]{i, j, k, l, m, n, i2, i3, i4, i5});
      if (bl) {
         this.inInventory = mc.currentScreen instanceof GuiInventory;
         if (this.inInventory != this.wasInInventory) {
            this.delay = new InventoryManager.Delay(general.getSetting("Open-Inv-Delay").asInt());
         }

         this.wasInInventory = this.inInventory;
         this.scanInventory();
         this.generateSwapList(i, m, i2, i4, i3, n, j, l, k, i5);
         if (this.shouldSwap()) {
            this.doSwaps();
         }

         this.generateDropList(i, m, i2, i4, i3, n, i5, j, l, k);
         if (this.shouldDrop()) {
            this.doDrops();
         }
      }
   }

   public boolean wouldKeep(ItemStack itemStack) {
      this.dropSlots.clear();
      int i = slots.getSetting("Sword-Slot").asInt();
      int j = slots.getSetting("Golden-Apple-Slot").asInt();
      int k = slots.getSetting("Head-Slot").asInt();
      int l = slots.getSetting("Pearl-Slot").asInt();
      int m = slots.getSetting("Bow-Slot").asInt();
      int n = slots.getSetting("Fishing-Rod-Slot").asInt();
      int i2 = slots.getSetting("Pickaxe-Slot").asInt();
      int i3 = slots.getSetting("Axe-Slot").asInt();
      int i4 = slots.getSetting("Shovel-Slot").asInt();
      int i5 = slots.getSetting("Blocks-Slot").asInt();
      Slot slot = new Slot(mc.thePlayer.inventory, 28, 0, 0);
      ItemStack itemStack2 = mc.thePlayer.inventory.mainInventory[28];
      mc.thePlayer.inventoryContainer.inventorySlots.add(28, slot);
      slot.putStack(itemStack);
      this.scanInventory();
      this.generateDropList(i, m, i2, i4, i3, n, i5, j, l, k);
      mc.thePlayer.inventoryContainer.inventorySlots.remove(slot);
      mc.thePlayer.inventory.mainInventory[28] = itemStack2;
      return !this.dropSlots.contains(28);
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   public class Delay {
      public long start = System.currentTimeMillis();
      public long delay;

      public Delay() {
         this.delay = 0L;
      }

      public Delay(long l) {
         this.delay = l;
      }

      public boolean hasPassed() {
         return System.currentTimeMillis() - this.delay > this.start;
      }
   }

   public class RankedSlot implements Comparable<InventoryManager.RankedSlot> {
      public int slot;
      public float rank;

      public RankedSlot(int i, float f) {
         this.slot = i;
         this.rank = f;
      }

      public int compareTo(InventoryManager.RankedSlot rankedSlot) {
         if (this.rank != rankedSlot.rank) {
            return this.rank > rankedSlot.rank ? 1 : -1;
         } else {
            Slot slot2 = ClientAccess.mc.thePlayer.inventoryContainer.inventorySlots.get(this.slot);
            Slot slot3 = ClientAccess.mc.thePlayer.inventoryContainer.inventorySlots.get(rankedSlot.slot);
            if (slot2.getHasStack() && slot3.getHasStack()) {
               return InventoryManager.this.remainingDurability(slot2.getStack()) > InventoryManager.this.remainingDurability(slot3.getStack()) ? 1 : -1;
            } else {
               return 0;
            }
         }
      }
   }
}
