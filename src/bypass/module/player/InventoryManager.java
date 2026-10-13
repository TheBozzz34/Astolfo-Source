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
   private final Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.PRE) {
         this.update();
      }
   };
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = var1x -> {
      if (var1x.getType() == RenderType.SCREEN) {
         this.drawSlotWarning(var1x);
      }
   };
   private static final long cb = 139904808579327L;

   public InventoryManager(Category var1) {
      super(var1);
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

   public int remainingDurability(ItemStack var1) {
      int var5 = -1;
      if (var1 != null) {
         var5 = var1.getMaxDamage() - var1.getItemDamage();
      }

      return var5;
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
      float var4 = -1.0F;
      float var5 = -1.0F;
      float var6 = -1.0F;
      float var7 = -1.0F;
      float var8 = -1.0F;
      float var9 = -1.0F;
      float var10 = -1.0F;
      float var11 = -1.0F;
      float var12 = -1.0F;
      float var13 = -1.0F;
      int var14 = this.tools.getSetting("Min-Tool-Durability").asInt();
      boolean var15 = slots.getSetting("Golden-Apple-Refill").getValue() == InventoryManagerRefillMode.MOST;
      boolean var16 = slots.getSetting("Ender-Pearl-Refill").getValue() == InventoryManagerRefillMode.MOST;
      boolean var17 = slots.getSetting("Head-Refill").getValue() == InventoryManagerRefillMode.MOST;
      boolean var18 = slots.getSetting("Blocks-Refill").getValue() == InventoryManagerRefillMode.MOST;
      float var19 = var15 ? -1.0F : 42069.0F;
      float var20 = var16 ? -1.0F : 42069.0F;
      float var21 = var17 ? -1.0F : 42069.0F;
      float var22 = var18 ? -1.0F : 42069.0F;
      boolean var23 = general.getSetting("Auto-WhiteList").asBoolean();

      for (int var24 = 0; var24 < mc.thePlayer.inventoryContainer.inventorySlots.size(); var24++) {
         boolean var25 = true;
         Slot var26 = mc.thePlayer.inventoryContainer.inventorySlots.get(var24);
         if (var26.getHasStack() && var26.getHasStack()) {
            ItemStack var27 = var26.getStack();
            if ((
                  !var23
                     || !(var27.getItem() instanceof ItemBook)
                     || !var27.getDisplayName().contains(EnumChatFormatting.GREEN.toString())
                     || var27.isItemEnchanted()
               )
               && (!var23 || !var27.getDisplayName().contains("§7(") || var27.getItem() instanceof ItemArmor || var27.getItem() instanceof ItemSword)) {
               if (!this.lastStacks.containsKey(var24) || !var27.equals(this.lastStacks.get(var24))) {
                  this.changeTimes.put(var24, System.currentTimeMillis());
               }

               Item var28 = var27.getItem();
               String var29 = var27.getUnlocalizedName();
               String var30 = var27.getDisplayName();
               if (var28 instanceof ItemSword) {
                  var25 = false;
                  this.swords.add(new InventoryManager.RankedSlot(var24, this.swordRank(var27, var14)));
                  if (this.swordScore(var27) > var4) {
                     this.bestSwordSlot = var24;
                     var4 = this.swordScore(var27);
                  }
               }

               if (var28 instanceof ItemBow) {
                  var25 = false;
                  this.bows.add(new InventoryManager.RankedSlot(var24, this.bowRank(var27, var14)));
                  if (this.bowScore(var27) > var5) {
                     this.bestBowSlot = var24;
                     var5 = this.bowScore(var27);
                  }
               }

               if (var28 instanceof ItemPickaxe) {
                  var25 = false;
                  this.pickaxes.add(new InventoryManager.RankedSlot(var24, this.pickaxeRank(var27, var14)));
                  if (this.pickaxeScore(var27) > var6) {
                     this.bestPickaxeSlot = var24;
                     var6 = this.pickaxeScore(var27);
                  }
               }

               if (var28 instanceof ItemSpade) {
                  var25 = false;
                  this.shovels.add(new InventoryManager.RankedSlot(var24, this.shovelRank(var27, var14)));
                  if (this.shovelScore(var27) > var7) {
                     this.bestShovelSlot = var24;
                     var7 = this.shovelScore(var27);
                  }
               }

               if (var28 instanceof ItemAxe) {
                  var25 = false;
                  this.axes.add(new InventoryManager.RankedSlot(var24, this.axeRank(var27, var14)));
                  if (this.axeScore(var27) > var8) {
                     this.bestAxeSlot = var24;
                     var8 = this.axeScore(var27);
                  }
               }

               if (var28 instanceof ItemFishingRod) {
                  var25 = false;
                  this.fishingRods.add(new InventoryManager.RankedSlot(var24, this.fishingRodRank(var27, var14)));
                  if (this.fishingRodScore(var27) > var9) {
                     this.bestFishingRodSlot = var24;
                     var9 = this.fishingRodScore(var27);
                  }
               }

               if (var28 instanceof ItemShears) {
                  var25 = false;
                  this.shears.add(new InventoryManager.RankedSlot(var24, this.remainingDurability(var27)));
               }

               if (var28 instanceof ItemFlintAndSteel) {
                  var25 = false;
                  this.flintAndSteels.add(new InventoryManager.RankedSlot(var24, this.remainingDurability(var27)));
               }

               if (var28 instanceof ItemArmor && ((ItemArmor)var28).armorType == 0) {
                  var25 = false;
                  this.helmets.add(new InventoryManager.RankedSlot(var24, this.armorRank(var27, var14)));
                  if (this.armorScore(var27) > var10) {
                     this.bestHelmetSlot = var24;
                     var10 = this.armorScore(var27);
                  }
               }

               if (var28 instanceof ItemArmor && ((ItemArmor)var28).armorType == 1) {
                  var25 = false;
                  this.chestplates.add(new InventoryManager.RankedSlot(var24, this.armorRank(var27, var14)));
                  if (this.armorScore(var27) > var11) {
                     this.bestChestplateSlot = var24;
                     var11 = this.armorScore(var27);
                  }
               }

               if (var28 instanceof ItemArmor && ((ItemArmor)var28).armorType == 2) {
                  var25 = false;
                  this.leggings.add(new InventoryManager.RankedSlot(var24, this.armorRank(var27, var14)));
                  if (this.armorScore(var27) > var12) {
                     this.bestLeggingsSlot = var24;
                     var12 = this.armorScore(var27);
                  }
               }

               if (var28 instanceof ItemArmor && ((ItemArmor)var28).armorType == 3) {
                  var25 = false;
                  this.boots.add(new InventoryManager.RankedSlot(var24, this.armorRank(var27, var14)));
                  if (this.armorScore(var27) > var13) {
                     this.bestBootsSlot = var24;
                     var13 = this.armorScore(var27);
                  }
               }

               if (var28 instanceof ItemAppleGold) {
                  var25 = false;
                  this.goldenApples.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
                  if (var15 && var27.stackSize > var19
                     || slots.getSetting("Golden-Apple-Refill").getValue() == InventoryManagerRefillMode.LEAST && var27.stackSize < var19) {
                     this.goldenAppleRefillSlot = var24;
                     var19 = var27.stackSize;
                  }
               }

               if (var28 instanceof ItemEnderPearl) {
                  var25 = false;
                  this.enderPearls.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
                  if (var16 && var27.stackSize > var20
                     || slots.getSetting("Ender-Pearl-Refill").getValue() == InventoryManagerRefillMode.LEAST && var27.stackSize < var20) {
                     this.enderPearlRefillSlot = var24;
                     var20 = var27.stackSize;
                  }
               }

               if (var28 instanceof ItemBoat) {
                  var25 = false;
                  this.boats.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var28 instanceof ItemBlock && ((ItemBlock)var28).getBlock() instanceof BlockTNT) {
                  var25 = false;
                  this.tnt.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.arrow")) {
                  var25 = false;
                  this.arrows.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.egg") || var28.getUnlocalizedName().equals("item.snowball")) {
                  var25 = false;
                  this.eggsAndSnowballs.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.monsterPlacer")) {
                  var25 = false;
                  this.spawnEggs.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var28 instanceof ItemFood && !this.nonFoods.contains(var29)) {
                  var25 = false;
                  this.food.add(new InventoryManager.RankedSlot(var24, this.foodRank(var27)));
               }

               if (var29.equals("item.bucketWater")) {
                  var25 = false;
                  this.waterBuckets.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.bucketLava")) {
                  var25 = false;
                  this.lavaBuckets.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var28 instanceof ItemBlock && isBuildingBlock((ItemBlock)var28) && !this.nonBuildingBlocks.contains(var29)) {
                  var25 = false;
                  this.blocks.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
                  if (var18 && var27.stackSize > var22
                     || slots.getSetting("Blocks-Refill").getValue() == InventoryManagerRefillMode.LEAST && var27.stackSize < var22) {
                     this.blockRefillSlot = var24;
                     var22 = var27.stackSize;
                  }
               }

               if (var28 instanceof ItemBlock && (var29.equals("tile.sand.default") || var29.equals("tile.gravel"))) {
                  var25 = false;
                  this.sandAndGravel.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("tile.web")) {
                  var25 = false;
                  this.cobwebs.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.apple")) {
                  var25 = false;
                  this.apples.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("tile.workbench")) {
                  var25 = false;
                  this.craftingTables.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.stick")) {
                  var25 = false;
                  this.sticks.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.contains("tile.anvil")) {
                  var25 = false;
                  this.anvils.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("tile.enchantmentTable")) {
                  var25 = false;
                  this.enchantingTables.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("tile.furnace")) {
                  var25 = false;
                  this.furnaces.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.expBottle")) {
                  var25 = false;
                  this.expBottles.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.milk")) {
                  var25 = false;
                  this.milkBuckets.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("tile.chest")) {
                  var25 = false;
                  this.chests.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.bucket")) {
                  var25 = false;
                  this.emptyBuckets.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.string")) {
                  var25 = false;
                  this.strings.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.ingotIron")) {
                  var25 = false;
                  this.ironIngots.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.ingotGold")) {
                  var25 = false;
                  this.goldIngots.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("tile.oreIron")) {
                  var25 = false;
                  this.ironOres.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("tile.oreGold")) {
                  var25 = false;
                  this.goldOres.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.coal")) {
                  var25 = false;
                  this.coal.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.redstone")) {
                  var25 = false;
                  this.redstone.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.dyePowder.blue")) {
                  var25 = false;
                  this.lapis.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.diamond")) {
                  var25 = false;
                  this.diamonds.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var29.equals("item.emerald")) {
                  var25 = false;
                  this.emeralds.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               EnumSetting var31 = (EnumSetting)this.potions.getSetting("Potion-Sort");
               boolean var32 = var31.getValue() == InventoryManagerPotionSortMode.AMPLIFIER;
               if (var28 instanceof ItemPotion) {
                  var25 = false;
                  ItemPotion var33 = (ItemPotion)var28;
                  List var34 = var33.getEffects(var27);
                  if (var34 != null) {
                     for (Object var36$raw : var34) {
                        PotionEffect var36 = (PotionEffect)var36$raw;
                        if (var36.getPotionID() == 1) {
                           this.speedPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 5) {
                           this.strengthPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 10) {
                           this.regenerationPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 6) {
                           this.healingPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 8) {
                           this.jumpBoostPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 12) {
                           this.fireResistancePotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 11) {
                           this.resistancePotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 14) {
                           this.invisibilityPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 22) {
                           this.absorptionPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 18) {
                           this.weaknessPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 2) {
                           this.slownessPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 19) {
                           this.poisonPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }

                        if (var36.getPotionID() == 7) {
                           this.harmingPotions
                              .add(
                                 new InventoryManager.RankedSlot(
                                    var24,
                                    var27.stackSize
                                       * (var32 ? var36.getAmplifier() + var36.getDuration() / 10000000 : var36.getDuration() + var36.getAmplifier() / 10000000)
                                 )
                              );
                        }
                     }
                  }
               }

               if ((var27.getItem().isPotionIngredient(var27) || var27.getItem().getUnlocalizedName().equals("item.brewingStand"))
                  && (!var27.getItem().getUnlocalizedName().equals("item.redstone") || this.ores.getSetting("Redstone").asInt() <= 0)) {
                  var25 = false;
                  this.brewingItems.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var28 instanceof ItemSkull && isHead(var27)) {
                  var25 = false;
                  this.heads.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
                  if (var17 && var27.stackSize > var21
                     || slots.getSetting("Head-Refill").getValue() == InventoryManagerRefillMode.LEAST && var27.stackSize < var21) {
                     this.headRefillSlot = var24;
                     var21 = var27.stackSize;
                  }
               }

               if (this.isWhitelisted(var24)) {
                  var25 = false;
                  this.whitelisted.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               if (var25) {
                  this.garbage.add(new InventoryManager.RankedSlot(var24, var27.stackSize));
               }

               this.lastStacks.put(var24, var26.getStack());
            }
         } else {
            this.lastStacks.remove(var24);
         }
      }
   }

   public void generateSwapList(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      if (var1 != 0 && this.bestSwordSlot != -1) {
         int var14 = var1 - 1;
         Slot var15 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + var14);
         Slot var16 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestSwordSlot);
         if (!var15.getHasStack() || var15.getHasStack() && var16.getHasStack() && this.swordScore(var15.getStack()) < this.swordScore(var16.getStack())) {
            Integer[] var17 = new Integer[]{this.bestSwordSlot, var14};
            this.swaps.add(var17);
         }
      }

      if (var2 != 0 && this.bestBowSlot != -1) {
         int var18 = var2 - 1;
         Slot var28 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + var18);
         Slot var38 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestBowSlot);
         if (!var28.getHasStack() || var28.getHasStack() && var38.getHasStack() && this.bowScore(var28.getStack()) < this.bowScore(var38.getStack())) {
            Integer[] var48 = new Integer[]{this.bestBowSlot, var18};
            this.swaps.add(var48);
         }
      }

      if (var3 != 0 && this.bestPickaxeSlot != -1) {
         int var19 = var3 - 1;
         Slot var29 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + var19);
         Slot var39 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestPickaxeSlot);
         if (!var29.getHasStack() || var29.getHasStack() && var39.getHasStack() && this.pickaxeScore(var29.getStack()) < this.pickaxeScore(var39.getStack())) {
            Integer[] var49 = new Integer[]{this.bestPickaxeSlot, var19};
            this.swaps.add(var49);
         }
      }

      if (var4 != 0 && this.bestShovelSlot != -1) {
         int var20 = var4 - 1;
         Slot var30 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + var20);
         Slot var40 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestShovelSlot);
         if (!var30.getHasStack() || var30.getHasStack() && var40.getHasStack() && this.shovelScore(var30.getStack()) < this.shovelScore(var40.getStack())) {
            Integer[] var50 = new Integer[]{this.bestShovelSlot, var20};
            this.swaps.add(var50);
         }
      }

      if (var5 != 0 && this.bestAxeSlot != -1) {
         int var21 = var5 - 1;
         Slot var31 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + var21);
         Slot var41 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestAxeSlot);
         if (!var31.getHasStack() || var31.getHasStack() && var41.getHasStack() && this.axeScore(var31.getStack()) < this.axeScore(var41.getStack())) {
            Integer[] var51 = new Integer[]{this.bestAxeSlot, var21};
            this.swaps.add(var51);
         }
      }

      if (var6 != 0 && this.bestFishingRodSlot != -1) {
         int var22 = var6 - 1;
         Slot var32 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + var22);
         Slot var42 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bestFishingRodSlot);
         if (!var32.getHasStack()
            || var32.getHasStack() && var42.getHasStack() && this.fishingRodScore(var32.getStack()) < this.fishingRodScore(var42.getStack())) {
            Integer[] var52 = new Integer[]{this.bestFishingRodSlot, var22};
            this.swaps.add(var52);
         }
      }

      if (var7 != 0 && this.goldenAppleRefillSlot != -1) {
         int var23 = var7 - 1;
         Slot var33 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + var23);
         if (!var33.getHasStack() || !(var33.getStack().getItem() instanceof ItemAppleGold)) {
            Integer[] var43 = new Integer[]{this.goldenAppleRefillSlot, var23};
            this.swaps.add(var43);
         }
      }

      if (var8 != 0 && this.enderPearlRefillSlot != -1) {
         int var24 = var8 - 1;
         Slot var34 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + var24);
         if (!var34.getHasStack() || !(var34.getStack().getItem() instanceof ItemEnderPearl)) {
            Integer[] var44 = new Integer[]{this.enderPearlRefillSlot, var24};
            this.swaps.add(var44);
         }
      }

      if (var9 != 0 && this.headRefillSlot != -1) {
         int var25 = var9 - 1;
         Slot var35 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + var25);
         if (!var35.getHasStack() || !(var35.getStack().getItem() instanceof ItemSkull) || !isHead(var35.getStack())) {
            Integer[] var45 = new Integer[]{this.headRefillSlot, var25};
            this.swaps.add(var45);
         }
      }

      if (var10 != 0 && this.blockRefillSlot != -1) {
         int var26 = var10 - 1;
         Slot var36 = mc.thePlayer.inventoryContainer.inventorySlots.get(36 + var26);
         if (!var36.getHasStack()
            || !(var36.getStack().getItem() instanceof ItemBlock)
            || !isBuildingBlock((ItemBlock)var36.getStack().getItem())
            || this.nonBuildingBlocks.contains(var36.getStack().getUnlocalizedName())) {
            Integer[] var46 = new Integer[]{this.blockRefillSlot, var26};
            this.swaps.add(var46);
         }
      }

      EnumSetting var27 = (EnumSetting)general.getSetting("Swap-Pattern");
      if (((InventoryManagerPatternMode)var27.getValue()).equals(InventoryManagerPatternMode.RANDOM)) {
         Random var37 = new Random();
         ArrayList var47 = new ArrayList();

         while (this.swaps.size() > 0) {
            var47.add(this.swaps.remove(var37.nextInt(this.swaps.size())));
         }

         this.swaps = var47;
      } else if (((InventoryManagerPatternMode)var27.getValue()).equals(InventoryManagerPatternMode.SEQUENTIAL)) {
         this.swaps.sort(Comparator.comparing(var0 -> (Integer)var0[1]));
      } else if (((InventoryManagerPatternMode)var27.getValue()).equals(InventoryManagerPatternMode.REVERSE)) {
         this.swaps.sort(Comparator.comparing(var0 -> (Integer)var0[1]));
         Collections.reverse(this.swaps);
      }
   }

   public void generateDropList(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      this.processSwords(var1);
      this.processBows(var2);
      this.processTools(var3, var4, var5, var6);
      this.processArmor();
      this.processUtils(var7, var8, var9);
      this.processUhcItems();
      this.processOres();
      this.processPotions();
      this.processHeads(var10);
      this.processGarbage();
      InventoryManagerPatternMode var14 = (InventoryManagerPatternMode)general.getSetting("Drop-Pattern").getValue();
      if (var14.equals(InventoryManagerPatternMode.RANDOM)) {
         ArrayList var15 = new ArrayList();
         Random var16 = new Random();

         while (this.dropSlots.size() > 0) {
            var15.add(this.dropSlots.remove(var16.nextInt(this.dropSlots.size())));
         }

         this.dropSlots = var15;
      } else if (var14.equals(InventoryManagerPatternMode.SEQUENTIAL)) {
         Collections.sort(this.dropSlots);
      } else if (var14.equals(InventoryManagerPatternMode.REVERSE)) {
         Collections.sort(this.dropSlots);
         Collections.reverse(this.dropSlots);
      }
   }

   public static boolean isHead(ItemStack var0) {
      return var0.getMetadata() == 3 && (var0.getDisplayName() == null || !var0.getDisplayName().contains("Backpack"));
   }

   public void processHeads(int var1) {
      while (this.heads.size() > 0) {
         if (this.drop.getSetting("Util").asBoolean()) {
            InventoryManager.RankedSlot var5 = this.heads.peek();
            int var6 = var5.slot;
            if (this.heads.size() > this.hypixelUHCItems.getSetting("Heads").asInt() && !this.isWhitelisted(var6)) {
               int var7 = var1 - 1 + 36;
               if (var7 == var6 && this.heads.size() > 1) {
                  InventoryManager.RankedSlot var8 = this.heads.poll();
                  Slot var9 = mc.thePlayer.inventoryContainer.inventorySlots.get(var8.slot);
                  Slot var10 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.heads.peek().slot);
                  if (var9.getHasStack() && var10.getHasStack() && var9.getStack().stackSize == var10.getStack().stackSize) {
                     this.dropSlots.add(this.heads.poll().slot);
                     this.heads.add(var8);
                     continue;
                  }

                  this.heads.add(var8);
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
      boolean var4 = this.drop.getSetting("Potions").asBoolean();

      while (this.speedPotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var5 = this.speedPotions.peek();
            int var6 = var5.slot;
            if (this.speedPotions.size() > this.potions.getSetting("Speed-Potions").asInt() && !this.isWhitelisted(var6)) {
               this.dropSlots.add(this.speedPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.speedPotions.poll().slot);
      }

      while (this.strengthPotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var7 = this.strengthPotions.peek();
            int var19 = var7.slot;
            if (this.strengthPotions.size() > this.potions.getSetting("Strength-Potions").asInt() && !this.isWhitelisted(var19)) {
               this.dropSlots.add(this.strengthPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.strengthPotions.poll().slot);
      }

      while (this.regenerationPotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var8 = this.regenerationPotions.peek();
            int var20 = var8.slot;
            if (this.regenerationPotions.size() > this.potions.getSetting("Regeneration-Potions").asInt() && !this.isWhitelisted(var20)) {
               this.dropSlots.add(this.regenerationPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.regenerationPotions.poll().slot);
      }

      while (this.healingPotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var9 = this.healingPotions.peek();
            int var21 = var9.slot;
            if (this.healingPotions.size() > this.potions.getSetting("Healing-Potions").asInt() && !this.isWhitelisted(var21)) {
               this.dropSlots.add(this.healingPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.healingPotions.poll().slot);
      }

      while (this.jumpBoostPotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var10 = this.jumpBoostPotions.peek();
            int var22 = var10.slot;
            if (this.jumpBoostPotions.size() > this.potions.getSetting("Jump-Boost-Potions").asInt() && !this.isWhitelisted(var22)) {
               this.dropSlots.add(this.jumpBoostPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.jumpBoostPotions.poll().slot);
      }

      while (this.fireResistancePotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var11 = this.fireResistancePotions.peek();
            int var23 = var11.slot;
            if (this.fireResistancePotions.size() > this.potions.getSetting("Fire-Resistance-Potions").asInt() && !this.isWhitelisted(var23)) {
               this.dropSlots.add(this.fireResistancePotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.fireResistancePotions.poll().slot);
      }

      while (this.resistancePotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var12 = this.resistancePotions.peek();
            int var24 = var12.slot;
            if (this.resistancePotions.size() > this.potions.getSetting("Resistance-Potions").asInt() && !this.isWhitelisted(var24)) {
               this.dropSlots.add(this.resistancePotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.resistancePotions.poll().slot);
      }

      while (this.invisibilityPotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var13 = this.invisibilityPotions.peek();
            int var25 = var13.slot;
            if (this.invisibilityPotions.size() > this.potions.getSetting("Invisibility-Potions").asInt() && !this.isWhitelisted(var25)) {
               this.dropSlots.add(this.invisibilityPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.invisibilityPotions.poll().slot);
      }

      while (this.absorptionPotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var14 = this.absorptionPotions.peek();
            int var26 = var14.slot;
            if (this.absorptionPotions.size() > this.potions.getSetting("Absorption-Potions").asInt() && !this.isWhitelisted(var26)) {
               this.dropSlots.add(this.absorptionPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.absorptionPotions.poll().slot);
      }

      while (this.weaknessPotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var15 = this.weaknessPotions.peek();
            int var27 = var15.slot;
            if (this.weaknessPotions.size() > this.potions.getSetting("Weakness-Potions").asInt() && !this.isWhitelisted(var27)) {
               this.dropSlots.add(this.weaknessPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.weaknessPotions.poll().slot);
      }

      while (this.slownessPotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var16 = this.slownessPotions.peek();
            int var28 = var16.slot;
            if (this.slownessPotions.size() > this.potions.getSetting("Slowness-Potions").asInt() && !this.isWhitelisted(var28)) {
               this.dropSlots.add(this.slownessPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.slownessPotions.poll().slot);
      }

      while (this.poisonPotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var17 = this.poisonPotions.peek();
            int var29 = var17.slot;
            if (this.poisonPotions.size() > this.potions.getSetting("Poison-Potions").asInt() && !this.isWhitelisted(var29)) {
               this.dropSlots.add(this.poisonPotions.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.poisonPotions.poll().slot);
      }

      while (this.harmingPotions.size() > 0) {
         if (var4) {
            InventoryManager.RankedSlot var18 = this.harmingPotions.peek();
            int var30 = var18.slot;
            if (this.harmingPotions.size() > this.potions.getSetting("Harming-Potions").asInt() && !this.isWhitelisted(var30)) {
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
         int var4 = this.ores.getSetting("Iron-Ingots").asInt();

         while (this.ironIngots.size() > 0) {
            InventoryManager.RankedSlot var5 = this.ironIngots.peek();
            int var6 = var5.slot;
            if (this.ironIngots.size() > var4 && !this.isWhitelisted(var6)) {
               this.dropSlots.add(this.ironIngots.poll().slot);
            } else {
               this.keepSlots.add(this.ironIngots.poll().slot);
            }
         }

         int var16 = this.ores.getSetting("Gold-Ingots").asInt();

         while (this.goldIngots.size() > 0) {
            InventoryManager.RankedSlot var17 = this.goldIngots.peek();
            int var7 = var17.slot;
            if (this.goldIngots.size() > var16 && !this.isWhitelisted(var7)) {
               this.dropSlots.add(this.goldIngots.poll().slot);
            } else {
               this.keepSlots.add(this.goldIngots.poll().slot);
            }
         }

         int var18 = this.ores.getSetting("Iron-Ores").asInt();

         while (this.ironOres.size() > 0) {
            InventoryManager.RankedSlot var19 = this.ironOres.peek();
            int var8 = var19.slot;
            if (this.ironOres.size() > var18 && !this.isWhitelisted(var8)) {
               this.dropSlots.add(this.ironOres.poll().slot);
            } else {
               this.keepSlots.add(this.ironOres.poll().slot);
            }
         }

         int var20 = this.ores.getSetting("Gold-Ores").asInt();

         while (this.goldOres.size() > 0) {
            InventoryManager.RankedSlot var21 = this.goldOres.peek();
            int var9 = var21.slot;
            if (this.goldOres.size() > var20 && !this.isWhitelisted(var9)) {
               this.dropSlots.add(this.goldOres.poll().slot);
            } else {
               this.keepSlots.add(this.goldOres.poll().slot);
            }
         }

         int var22 = this.ores.getSetting("Coal").asInt();

         while (this.coal.size() > 0) {
            InventoryManager.RankedSlot var23 = this.coal.peek();
            int var10 = var23.slot;
            if (this.coal.size() > var22 && !this.isWhitelisted(var10)) {
               this.dropSlots.add(this.coal.poll().slot);
            } else {
               this.keepSlots.add(this.coal.poll().slot);
            }
         }

         int var24 = this.ores.getSetting("Redstone").asInt();
         boolean var25 = this.potions.getSetting("Brewing-Items").asBoolean();

         while (this.redstone.size() > 0) {
            InventoryManager.RankedSlot var11 = this.redstone.peek();
            int var12 = var11.slot;
            if (this.redstone.size() > Math.max(var24, var25 ? 1 : 0) && !this.isWhitelisted(var12)) {
               this.dropSlots.add(this.redstone.poll().slot);
            } else {
               this.keepSlots.add(this.redstone.poll().slot);
            }
         }

         int var26 = this.ores.getSetting("Lapis").asInt();

         while (this.lapis.size() > 0) {
            InventoryManager.RankedSlot var27 = this.lapis.peek();
            int var13 = var27.slot;
            if (this.lapis.size() > var26 && !this.isWhitelisted(var13)) {
               this.dropSlots.add(this.lapis.poll().slot);
            } else {
               this.keepSlots.add(this.lapis.poll().slot);
            }
         }

         int var28 = this.ores.getSetting("Diamonds").asInt();

         while (this.diamonds.size() > 0) {
            InventoryManager.RankedSlot var29 = this.diamonds.peek();
            int var14 = var29.slot;
            if (this.diamonds.size() > var28 && !this.isWhitelisted(var14)) {
               this.dropSlots.add(this.diamonds.poll().slot);
            } else {
               this.keepSlots.add(this.diamonds.poll().slot);
            }
         }

         int var30 = this.ores.getSetting("Emeralds").asInt();

         while (this.emeralds.size() > 0) {
            InventoryManager.RankedSlot var31 = this.emeralds.peek();
            int var15 = var31.slot;
            if (this.emeralds.size() > var30 && !this.isWhitelisted(var15)) {
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
            InventoryManager.RankedSlot var4 = this.apples.peek();
            int var5 = var4.slot;
            if (this.apples.size() > this.uhc.getSetting("Apples").asInt() && !this.isWhitelisted(var5)) {
               this.dropSlots.add(this.apples.poll().slot);
            } else {
               this.keepSlots.add(this.apples.poll().slot);
            }
         }

         while (this.craftingTables.size() > 0) {
            InventoryManager.RankedSlot var7 = this.craftingTables.peek();
            int var15 = var7.slot;
            if (this.craftingTables.size() > this.uhc.getSetting("Crafting-Tables").asInt() && !this.isWhitelisted(var15)) {
               this.dropSlots.add(this.craftingTables.poll().slot);
            } else {
               this.keepSlots.add(this.craftingTables.poll().slot);
            }
         }

         while (this.sticks.size() > 0) {
            InventoryManager.RankedSlot var8 = this.sticks.peek();
            int var16 = var8.slot;
            if (this.sticks.size() > this.uhc.getSetting("Sticks").asInt() && !this.isWhitelisted(var16)) {
               this.dropSlots.add(this.sticks.poll().slot);
            } else {
               this.keepSlots.add(this.sticks.poll().slot);
            }
         }

         while (this.anvils.size() > 0) {
            InventoryManager.RankedSlot var9 = this.anvils.peek();
            int var17 = var9.slot;
            if (this.anvils.size() > this.uhc.getSetting("Anvils").asInt() && !this.isWhitelisted(var17)) {
               this.dropSlots.add(this.anvils.poll().slot);
            } else {
               this.keepSlots.add(this.anvils.poll().slot);
            }
         }

         while (this.enchantingTables.size() > 0) {
            InventoryManager.RankedSlot var10 = this.enchantingTables.peek();
            int var18 = var10.slot;
            if (this.enchantingTables.size() > this.uhc.getSetting("Enchanting-Tables").asInt() && !this.isWhitelisted(var18)) {
               this.dropSlots.add(this.enchantingTables.poll().slot);
            } else {
               this.keepSlots.add(this.enchantingTables.poll().slot);
            }
         }

         while (this.furnaces.size() > 0) {
            InventoryManager.RankedSlot var11 = this.furnaces.peek();
            int var19 = var11.slot;
            if (this.furnaces.size() > this.uhc.getSetting("Furnaces").asInt() && !this.isWhitelisted(var19)) {
               this.dropSlots.add(this.furnaces.poll().slot);
            } else {
               this.keepSlots.add(this.furnaces.poll().slot);
            }
         }

         while (this.expBottles.size() > 0) {
            InventoryManager.RankedSlot var12 = this.expBottles.peek();
            int var20 = var12.slot;
            if (this.expBottles.size() > this.uhc.getSetting("EXP-Bottles").asInt() && !this.isWhitelisted(var20)) {
               this.dropSlots.add(this.expBottles.poll().slot);
            } else {
               this.keepSlots.add(this.expBottles.poll().slot);
            }
         }

         this.missingMilkBuckets = this.utils.getSetting("Milk-Buckets").asInt();

         while (this.milkBuckets.size() > 0) {
            InventoryManager.RankedSlot var13 = this.milkBuckets.peek();
            int var21 = var13.slot;
            if (this.milkBuckets.size() > this.utils.getSetting("Milk-Buckets").asInt() && !this.isWhitelisted(var21)) {
               this.dropSlots.add(this.milkBuckets.poll().slot);
            } else {
               this.keepSlots.add(this.milkBuckets.poll().slot);
               this.missingMilkBuckets--;
            }
         }

         int var14 = this.missingWaterBuckets + this.missingLavaBuckets + this.missingMilkBuckets;

         while (this.emptyBuckets.size() > 0) {
            if (var14 > 0) {
               var14 = (int)(var14 - this.emptyBuckets.peek().rank);
               this.keepSlots.add(this.emptyBuckets.poll().slot);
            } else {
               this.dropSlots.add(this.emptyBuckets.poll().slot);
            }
         }

         while (this.chests.size() > 0) {
            InventoryManager.RankedSlot var22 = this.chests.peek();
            int var6 = var22.slot;
            if (this.chests.size() > this.uhc.getSetting("Chests").asInt() && !this.isWhitelisted(var6)) {
               this.dropSlots.add(this.chests.poll().slot);
            } else {
               this.keepSlots.add(this.chests.poll().slot);
            }
         }

         while (this.strings.size() > 0) {
            InventoryManager.RankedSlot var23 = this.strings.peek();
            int var24 = var23.slot;
            if (this.strings.size() > this.uhc.getSetting("String").asInt() && !this.isWhitelisted(var24)) {
               this.dropSlots.add(this.strings.poll().slot);
            } else {
               this.keepSlots.add(this.strings.poll().slot);
            }
         }
      }
   }

   public void processUtils(int var1, int var2, int var3) {
      int var7 = this.utils.getSetting("Golden-Apples").asInt();
      int var8 = this.utils.getSetting("Ender-Pearls").asInt();
      if (this.drop.getSetting("Util").asBoolean()) {
         while (this.goldenApples.size() > 0) {
            InventoryManager.RankedSlot var9 = this.goldenApples.peek();
            int var10 = var9.slot;
            if (this.goldenApples.size() > var7 && !this.isWhitelisted(var10)) {
               int var11 = var2 - 1 + 36;
               if (var11 == var10 && this.goldenApples.size() > 1) {
                  InventoryManager.RankedSlot var12 = this.goldenApples.poll();
                  Slot var13 = mc.thePlayer.inventoryContainer.inventorySlots.get(var12.slot);
                  Slot var14 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.goldenApples.peek().slot);
                  if (var13.getHasStack() && var14.getHasStack() && var13.getStack().stackSize == var14.getStack().stackSize) {
                     this.dropSlots.add(this.goldenApples.poll().slot);
                     this.goldenApples.add(var12);
                     continue;
                  }

                  this.goldenApples.add(var12);
               }

               this.dropSlots.add(this.goldenApples.poll().slot);
            } else {
               this.keepSlots.add(this.goldenApples.poll().slot);
            }
         }

         while (this.enderPearls.size() > 0) {
            InventoryManager.RankedSlot var23 = this.enderPearls.peek();
            int var25 = var23.slot;
            if (this.enderPearls.size() > var8 && !this.isWhitelisted(var25)) {
               int var28 = var3 - 1 + 36;
               if (var28 == var25 && this.enderPearls.size() > 1) {
                  InventoryManager.RankedSlot var32 = this.enderPearls.poll();
                  Slot var36 = mc.thePlayer.inventoryContainer.inventorySlots.get(var32.slot);
                  Slot var40 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.enderPearls.peek().slot);
                  if (var36.getHasStack() && var40.getHasStack() && var36.getStack().stackSize == var40.getStack().stackSize) {
                     this.dropSlots.add(this.enderPearls.poll().slot);
                     this.enderPearls.add(var32);
                     continue;
                  }

                  this.enderPearls.add(var32);
               }

               this.dropSlots.add(this.enderPearls.poll().slot);
            } else {
               this.keepSlots.add(this.enderPearls.poll().slot);
            }
         }

         int var24 = this.utils.getSetting("Boats").asInt();

         while (this.boats.size() > 0) {
            InventoryManager.RankedSlot var26 = this.boats.peek();
            int var29 = var26.slot;
            if (this.boats.size() > var24 && !this.isWhitelisted(var29)) {
               this.dropSlots.add(this.boats.poll().slot);
            } else {
               this.keepSlots.add(this.boats.poll().slot);
            }
         }

         int var27 = this.utils.getSetting("TNT").asInt();

         while (this.tnt.size() > 0) {
            InventoryManager.RankedSlot var30 = this.tnt.peek();
            int var33 = var30.slot;
            if (this.tnt.size() > var27 && !this.isWhitelisted(var33)) {
               this.dropSlots.add(this.tnt.poll().slot);
            } else {
               this.keepSlots.add(this.tnt.poll().slot);
            }
         }

         int var31 = this.utils.getSetting("Arrows").asInt();

         while (this.arrows.size() > 0) {
            InventoryManager.RankedSlot var34 = this.arrows.peek();
            int var37 = var34.slot;
            if (this.arrows.size() > var31 && !this.isWhitelisted(var37)) {
               this.dropSlots.add(this.arrows.poll().slot);
            } else {
               this.keepSlots.add(this.arrows.poll().slot);
            }
         }

         int var35 = this.utils.getSetting("Eggs-Snowballs").asInt();

         while (this.eggsAndSnowballs.size() > 0) {
            InventoryManager.RankedSlot var38 = this.eggsAndSnowballs.peek();
            int var41 = var38.slot;
            if (this.eggsAndSnowballs.size() > var35 && !this.isWhitelisted(var41)) {
               this.dropSlots.add(this.eggsAndSnowballs.poll().slot);
            } else {
               this.keepSlots.add(this.eggsAndSnowballs.poll().slot);
            }
         }

         int var39 = this.utils.getSetting("Spawn-Eggs").asInt();

         while (this.spawnEggs.size() > 0) {
            InventoryManager.RankedSlot var42 = this.spawnEggs.peek();
            int var15 = var42.slot;
            if (this.spawnEggs.size() > var39 && !this.isWhitelisted(var15)) {
               this.dropSlots.add(this.spawnEggs.poll().slot);
            } else {
               this.keepSlots.add(this.spawnEggs.poll().slot);
            }
         }

         int var43 = this.utils.getSetting("Water-Buckets").asInt();

         while (this.waterBuckets.size() > 0) {
            InventoryManager.RankedSlot var44 = this.waterBuckets.peek();
            int var16 = var44.slot;
            if (this.waterBuckets.size() > var43 && !this.isWhitelisted(var16)) {
               this.dropSlots.add(this.waterBuckets.poll().slot);
            } else {
               this.keepSlots.add(this.waterBuckets.poll().slot);
               this.missingWaterBuckets--;
            }
         }

         int var45 = this.utils.getSetting("Lava-Buckets").asInt();

         while (this.lavaBuckets.size() > 0) {
            InventoryManager.RankedSlot var46 = this.lavaBuckets.peek();
            int var17 = var46.slot;
            if (this.lavaBuckets.size() > var45 && !this.isWhitelisted(var17)) {
               this.dropSlots.add(this.lavaBuckets.poll().slot);
            } else {
               this.keepSlots.add(this.lavaBuckets.poll().slot);
               this.missingLavaBuckets--;
            }
         }

         int var47 = this.utils.getSetting("Blocks").asInt();
         if (this.drop.getSetting("Blocks").asBoolean()) {
            while (this.blocks.size() > 0) {
               InventoryManager.RankedSlot var48 = this.blocks.peek();
               int var18 = var48.slot;
               if (this.blocks.size() > var47 && !this.isWhitelisted(var18)) {
                  int var19 = var1 - 1 + 36;
                  if (var19 == var18 && this.blocks.size() > 1) {
                     InventoryManager.RankedSlot var20 = this.blocks.poll();
                     Slot var21 = mc.thePlayer.inventoryContainer.inventorySlots.get(var20.slot);
                     Slot var22 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.blocks.peek().slot);
                     if (var21.getHasStack() && var22.getHasStack() && var21.getStack().stackSize == var22.getStack().stackSize) {
                        this.dropSlots.add(this.blocks.poll().slot);
                        this.blocks.add(var20);
                        continue;
                     }

                     this.blocks.add(var20);
                  }

                  this.dropSlots.add(this.blocks.poll().slot);
               } else {
                  this.keepSlots.add(this.blocks.poll().slot);
               }
            }
         }

         int var49 = this.utils.getSetting("Sand-Gravel").asInt();

         while (this.sandAndGravel.size() > 0) {
            InventoryManager.RankedSlot var50 = this.sandAndGravel.peek();
            int var52 = var50.slot;
            if (this.sandAndGravel.size() > var49 && !this.isWhitelisted(var52)) {
               this.dropSlots.add(this.sandAndGravel.poll().slot);
            } else {
               this.keepSlots.add(this.sandAndGravel.poll().slot);
            }
         }

         int var51 = this.utils.getSetting("Cobwebs").asInt();

         while (this.cobwebs.size() > 0) {
            InventoryManager.RankedSlot var53 = this.cobwebs.peek();
            int var55 = var53.slot;
            if (this.cobwebs.size() > var51 && !this.isWhitelisted(var55)) {
               this.dropSlots.add(this.cobwebs.poll().slot);
            } else {
               this.keepSlots.add(this.cobwebs.poll().slot);
            }
         }

         int var54 = this.utils.getSetting("Food").asInt();
         if (this.drop.getSetting("Food").asBoolean()) {
            while (this.food.size() > 0) {
               InventoryManager.RankedSlot var56 = this.food.peek();
               int var57 = var56.slot;
               if (this.food.size() > var54 && !this.isWhitelisted(var57)) {
                  this.dropSlots.add(this.food.poll().slot);
               } else {
                  this.keepSlots.add(this.food.poll().slot);
               }
            }
         }
      }
   }

   public float foodRank(ItemStack var1) {
      float var5 = -1.0F;
      if (var1 != null && var1.getItem() instanceof ItemFood && !this.nonFoods.contains(var1.getItem().getUnlocalizedName())) {
         ItemFood var6 = (ItemFood)var1.getItem();
         var5 = var1.stackSize * var6.getSaturationModifier(var1);
      }

      return var5;
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

   public void processArmor() {
      if (this.drop.getSetting("Armor").asBoolean()) {
         boolean var4 = this.enchants.getSetting("Combinable-Protection").asBoolean();
         int var5 = this.armor.getSetting("Helmets").asInt();
         int var6 = this.armor.getSetting("Chestplates").asInt();

         while (this.helmets.size() > 0) {
            InventoryManager.RankedSlot var7 = this.helmets.peek();
            int var8 = var7.slot;
            if (this.helmets.size() > var5 && (!var4 || !this.canCombineProtection(this.bestHelmetSlot, var8)) && !this.isWhitelisted(var8)) {
               byte var9 = 5;
               if (var9 == var8 && this.helmets.size() > 1) {
                  InventoryManager.RankedSlot var10 = this.helmets.poll();
                  Slot var11 = mc.thePlayer.inventoryContainer.inventorySlots.get(var10.slot);
                  Slot var12 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.helmets.peek().slot);
                  if (var11.getHasStack() && var12.getHasStack() && this.armorScore(var11.getStack()) == this.armorScore(var12.getStack())) {
                     this.dropSlots.add(this.helmets.poll().slot);
                     this.helmets.add(var10);
                     continue;
                  }

                  this.helmets.add(var10);
               }

               this.dropSlots.add(this.helmets.poll().slot);
            } else {
               this.keepSlots.add(this.helmets.poll().slot);
            }
         }

         while (this.chestplates.size() > 0) {
            InventoryManager.RankedSlot var15 = this.chestplates.peek();
            int var17 = var15.slot;
            if (this.chestplates.size() > var6 && (!var4 || !this.canCombineProtection(this.bestChestplateSlot, var17)) && !this.isWhitelisted(var17)) {
               byte var20 = 6;
               if (var20 == var17 && this.chestplates.size() > 1) {
                  InventoryManager.RankedSlot var23 = this.chestplates.poll();
                  Slot var26 = mc.thePlayer.inventoryContainer.inventorySlots.get(var23.slot);
                  Slot var29 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.chestplates.peek().slot);
                  if (var26.getHasStack() && var29.getHasStack() && this.armorScore(var26.getStack()) == this.armorScore(var29.getStack())) {
                     this.dropSlots.add(this.chestplates.poll().slot);
                     this.chestplates.add(var23);
                     continue;
                  }

                  this.chestplates.add(var23);
               }

               this.dropSlots.add(this.chestplates.poll().slot);
            } else {
               this.keepSlots.add(this.chestplates.poll().slot);
            }
         }

         int var16 = this.armor.getSetting("Leggings").asInt();

         while (this.leggings.size() > 0) {
            InventoryManager.RankedSlot var18 = this.leggings.peek();
            int var21 = var18.slot;
            if (this.leggings.size() > var16 && (!var4 || !this.canCombineProtection(this.bestLeggingsSlot, var21)) && !this.isWhitelisted(var21)) {
               byte var24 = 7;
               if (var24 == var21 && this.leggings.size() > 1) {
                  InventoryManager.RankedSlot var27 = this.leggings.poll();
                  Slot var30 = mc.thePlayer.inventoryContainer.inventorySlots.get(var27.slot);
                  Slot var13 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.leggings.peek().slot);
                  if (var30.getHasStack() && var13.getHasStack() && this.armorScore(var30.getStack()) == this.armorScore(var13.getStack())) {
                     this.dropSlots.add(this.leggings.poll().slot);
                     this.leggings.add(var27);
                     continue;
                  }

                  this.leggings.add(var27);
               }

               this.dropSlots.add(this.leggings.poll().slot);
            } else {
               this.keepSlots.add(this.leggings.poll().slot);
            }
         }

         int var19 = this.armor.getSetting("Boots").asInt();

         while (this.boots.size() > 0) {
            InventoryManager.RankedSlot var22 = this.boots.peek();
            int var25 = var22.slot;
            if (this.boots.size() > var19 && (!var4 || !this.canCombineProtection(this.bestBootsSlot, var25)) && !this.isWhitelisted(var25)) {
               byte var28 = 8;
               if (var28 == var25 && this.boots.size() > 1) {
                  InventoryManager.RankedSlot var31 = this.boots.poll();
                  Slot var32 = mc.thePlayer.inventoryContainer.inventorySlots.get(var31.slot);
                  Slot var14 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.boots.peek().slot);
                  if (var32.getHasStack() && var14.getHasStack() && this.armorScore(var32.getStack()) == this.armorScore(var14.getStack())) {
                     this.dropSlots.add(this.boots.poll().slot);
                     this.boots.add(var31);
                     continue;
                  }

                  this.boots.add(var31);
               }

               this.dropSlots.add(this.boots.poll().slot);
            } else {
               this.keepSlots.add(this.boots.poll().slot);
            }
         }
      }
   }

   public boolean canCombineProtection(int var1, int var2) {
      Slot var6 = mc.thePlayer.inventoryContainer.inventorySlots.get(var1);
      Slot var7 = mc.thePlayer.inventoryContainer.inventorySlots.get(var2);
      if (var6.getHasStack() && var7.getHasStack()) {
         ItemStack var8 = var6.getStack();
         ItemStack var9 = var7.getStack();
         return this.isCombinable(var8) && this.isCombinable(var9)
            ? EnchantmentHelper.getEnchantmentLevel(0, var8) > 0
               && EnchantmentHelper.getEnchantmentLevel(0, var8) < 4
               && var8.getUnlocalizedName().equals(var9.getUnlocalizedName())
               && EnchantmentHelper.getEnchantmentLevel(0, var8) == EnchantmentHelper.getEnchantmentLevel(0, var9)
            : false;
      } else {
         return false;
      }
   }

   public float armorScore(ItemStack var1) {
      return InventoryUtil.getArmorScore(var1);
   }

   public float armorRank(ItemStack var1, int var2) {
      float var6 = 0.0F;
      if (var1 != null && var1.getItem() instanceof ItemArmor) {
         ItemArmor var7 = (ItemArmor)var1.getItem();
         var6 = (float)(var6 + (var7.damageReduceAmount + (100 - var7.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(0, var1) * 0.0075));
         var6 += InventoryUtil.getArmorScore(var1);
         if (this.remainingDurability(var1) < var2) {
            var6 /= 1000.0F;
         }
      }

      return var6;
   }

   public void processTools(int var1, int var2, int var3, int var4) {
      if (this.drop.getSetting("Tools").asBoolean()) {
         int var8 = this.tools.getSetting("Pickaxes").asInt();

         while (this.pickaxes.size() > 0) {
            InventoryManager.RankedSlot var9 = this.pickaxes.peek();
            int var10 = var9.slot;
            if (this.pickaxes.size() > var8 && !this.isWhitelisted(var10)) {
               int var11 = var1 - 1 + 36;
               if (var11 == var10 && this.pickaxes.size() > 1) {
                  InventoryManager.RankedSlot var12 = this.pickaxes.poll();
                  Slot var13 = mc.thePlayer.inventoryContainer.inventorySlots.get(var12.slot);
                  Slot var14 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.pickaxes.peek().slot);
                  if (var13.getHasStack() && var14.getHasStack() && this.pickaxeScore(var13.getStack()) == this.pickaxeScore(var14.getStack())) {
                     this.dropSlots.add(this.pickaxes.poll().slot);
                     this.pickaxes.add(var12);
                     continue;
                  }

                  this.pickaxes.add(var12);
               }

               this.dropSlots.add(this.pickaxes.poll().slot);
            } else {
               this.keepSlots.add(this.pickaxes.poll().slot);
            }
         }

         int var18 = this.tools.getSetting("Shovels").asInt();

         while (this.shovels.size() > 0) {
            InventoryManager.RankedSlot var19 = this.shovels.peek();
            int var21 = var19.slot;
            if (this.shovels.size() > var18 && !this.isWhitelisted(var21)) {
               int var24 = var2 - 1 + 36;
               if (var24 == var21 && this.shovels.size() > 1) {
                  InventoryManager.RankedSlot var28 = this.shovels.poll();
                  Slot var33 = mc.thePlayer.inventoryContainer.inventorySlots.get(var28.slot);
                  Slot var15 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.shovels.peek().slot);
                  if (var33.getHasStack() && var15.getHasStack() && this.shovelScore(var33.getStack()) == this.shovelScore(var15.getStack())) {
                     this.dropSlots.add(this.shovels.poll().slot);
                     this.shovels.add(var28);
                     continue;
                  }

                  this.shovels.add(var28);
               }

               this.dropSlots.add(this.shovels.poll().slot);
            } else {
               this.keepSlots.add(this.shovels.poll().slot);
            }
         }

         int var20 = this.tools.getSetting("Axes").asInt();

         while (this.axes.size() > 0) {
            InventoryManager.RankedSlot var22 = this.axes.peek();
            int var25 = var22.slot;
            if (this.axes.size() > var20 && !this.isWhitelisted(var25)) {
               int var29 = var3 - 1 + 36;
               if (var29 == var25 && this.axes.size() > 1) {
                  InventoryManager.RankedSlot var34 = this.axes.poll();
                  Slot var38 = mc.thePlayer.inventoryContainer.inventorySlots.get(var34.slot);
                  Slot var16 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.axes.peek().slot);
                  if (var38.getHasStack() && var16.getHasStack() && this.axeScore(var38.getStack()) == this.axeScore(var16.getStack())) {
                     this.dropSlots.add(this.axes.poll().slot);
                     this.axes.add(var34);
                     continue;
                  }

                  this.axes.add(var34);
               }

               this.dropSlots.add(this.axes.poll().slot);
            } else {
               this.keepSlots.add(this.axes.poll().slot);
            }
         }

         int var23 = this.tools.getSetting("Fishing-Rods").asInt();

         while (this.fishingRods.size() > 0) {
            InventoryManager.RankedSlot var26 = this.fishingRods.peek();
            int var30 = var26.slot;
            if (this.fishingRods.size() > var23 && !this.isWhitelisted(var30)) {
               int var35 = var4 - 1 + 36;
               if (var35 == var30 && this.fishingRods.size() > 1) {
                  InventoryManager.RankedSlot var39 = this.fishingRods.poll();
                  Slot var41 = mc.thePlayer.inventoryContainer.inventorySlots.get(var39.slot);
                  Slot var17 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.fishingRods.peek().slot);
                  if (var41.getHasStack() && var17.getHasStack() && this.fishingRodScore(var41.getStack()) == this.fishingRodScore(var17.getStack())) {
                     this.dropSlots.add(this.fishingRods.poll().slot);
                     this.fishingRods.add(var39);
                     continue;
                  }

                  this.fishingRods.add(var39);
               }

               this.dropSlots.add(this.fishingRods.poll().slot);
            } else {
               this.keepSlots.add(this.fishingRods.poll().slot);
            }
         }

         int var27 = this.tools.getSetting("Shears").asInt();

         while (this.shears.size() > 0) {
            InventoryManager.RankedSlot var31 = this.shears.peek();
            int var36 = var31.slot;
            if (this.shears.size() > var27 && !this.isWhitelisted(var36)) {
               this.dropSlots.add(this.shears.poll().slot);
            } else {
               this.keepSlots.add(this.shears.poll().slot);
            }
         }

         int var32 = this.tools.getSetting("Flint-Steel").asInt();

         while (this.flintAndSteels.size() > 0) {
            InventoryManager.RankedSlot var37 = this.flintAndSteels.peek();
            int var40 = var37.slot;
            if (this.flintAndSteels.size() > var32 && !this.isWhitelisted(var40)) {
               this.dropSlots.add(this.flintAndSteels.poll().slot);
            } else {
               this.keepSlots.add(this.flintAndSteels.poll().slot);
            }
         }
      }
   }

   public float fishingRodScore(ItemStack var1) {
      float var5 = -1.0F;
      if (var1 != null && var1.getUnlocalizedName().equals("item.fishingRod")) {
         var5 = 1.0F;
         var5 += EnchantmentHelper.getEnchantmentLevel(62, var1);
         var5 += EnchantmentHelper.getEnchantmentLevel(19, var1) / 1000;
      }

      return var5;
   }

   public float fishingRodRank(ItemStack var1, int var2) {
      float var6 = -1.0F;
      if (var1 != null) {
         var6 = EnchantmentHelper.getEnchantmentLevel(62, var1);
         var6 += EnchantmentHelper.getEnchantmentLevel(19, var1) / 1000;
         if (this.remainingDurability(var1) < var2) {
            var6 /= 1000.0F;
         }
      }

      return var6;
   }

   public float pickaxeScore(ItemStack var1) {
      float var5 = -1.0F;
      if (var1 != null) {
         var5 = InventoryUtil.getToolScore(var1);
      }

      return var5;
   }

   public float pickaxeRank(ItemStack var1, int var2) {
      float var6 = -1.0F;
      if (var1 != null) {
         var6 = var1.getItem().getStrVsBlock(var1, Block.getBlockById(4)) * Math.max(1, (int)Math.pow(EnchantmentHelper.getEnchantmentLevel(32, var1), 2.0));
         if (this.remainingDurability(var1) < var2) {
            var6 /= 1000.0F;
         }
      }

      return var6;
   }

   public float shovelScore(ItemStack var1) {
      float var5 = -1.0F;
      if (var1 != null) {
         var5 = InventoryUtil.getToolScore(var1);
      }

      return var5;
   }

   public float shovelRank(ItemStack var1, int var2) {
      float var6 = -1.0F;
      if (var1 != null) {
         var6 = var1.getItem().getStrVsBlock(var1, Block.getBlockById(3)) * Math.max(1, (int)Math.pow(EnchantmentHelper.getEnchantmentLevel(32, var1), 2.0));
         if (this.remainingDurability(var1) < var2) {
            var6 /= 1000.0F;
         }
      }

      return var6;
   }

   public float axeScore(ItemStack var1) {
      float var5 = -1.0F;
      if (var1 != null) {
         var5 = InventoryUtil.getToolScore(var1);
      }

      return var5;
   }

   public float axeRank(ItemStack var1, int var2) {
      float var6 = -1.0F;
      if (var1 != null) {
         var6 = var1.getItem().getStrVsBlock(var1, Block.getBlockById(17)) * Math.max(1, (int)Math.pow(EnchantmentHelper.getEnchantmentLevel(32, var1), 2.0));
         if (this.remainingDurability(var1) < var2) {
            var6 /= 1000.0F;
         }
      }

      return var6;
   }

   public float bowScore(ItemStack var1) {
      float var5 = -1.0F;
      if (var1 != null && var1.getUnlocalizedName().equals("item.bow")) {
         var5 = 1.0F;
         var5 += EnchantmentHelper.getEnchantmentLevel(48, var1);
      }

      return var5;
   }

   public float bowRank(ItemStack var1, int var2) {
      float var6 = -1.0F;
      if (var1 != null && var1.getUnlocalizedName().equals("item.bow")) {
         var6 = 1.0F;
         var6 += EnchantmentHelper.getEnchantmentLevel(48, var1);
         if (this.remainingDurability(var1) < var2) {
            var6 /= 1000.0F;
         }
      }

      return var6;
   }

   public float damageScore(ItemStack var1) {
      float var5 = -1.0F;
      if (var1 != null) {
         var5 = InventoryUtil.getDamageScore(var1);
      }

      return var5;
   }

   public float swordScore(ItemStack var1) {
      float var5 = -1.0F;
      if (var1 != null) {
         var5 = InventoryUtil.getDamageScore(var1);
      }

      return var5;
   }

   public float swordRank(ItemStack var1, int var2) {
      float var6 = -1.0F;
      if (var1 != null) {
         var6 = InventoryUtil.getDamageScore(var1);
         if (this.remainingDurability(var1) < var2) {
            var6 /= 1000.0F;
         }
      }

      return var6;
   }

   public void processBows(int var1) {
      boolean var5 = this.enchants.getSetting("Combinable-Power").asBoolean();

      while (this.bows.size() > 0) {
         if (this.drop.getSetting("Bows").asBoolean()) {
            InventoryManager.RankedSlot var6 = this.bows.peek();
            int var7 = var6.slot;
            if (this.bows.size() > this.tools.getSetting("Bows").asInt()
               && (!var5 || !this.canCombinePower(this.bestBowSlot, var7))
               && !this.isWhitelisted(var7)) {
               int var8 = var1 - 1 + 36;
               if (var8 == var7 && this.bows.size() > 1) {
                  InventoryManager.RankedSlot var9 = this.bows.poll();
                  Slot var10 = mc.thePlayer.inventoryContainer.inventorySlots.get(var9.slot);
                  Slot var11 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.bows.peek().slot);
                  if (var10.getHasStack() && var11.getHasStack() && this.bowScore(var10.getStack()) == this.bowScore(var11.getStack())) {
                     this.dropSlots.add(this.bows.poll().slot);
                     this.bows.add(var9);
                     continue;
                  }

                  this.bows.add(var9);
               }

               this.dropSlots.add(this.bows.poll().slot);
               continue;
            }
         }

         this.keepSlots.add(this.bows.poll().slot);
      }
   }

   public boolean canCombinePower(int var1, int var2) {
      Slot var6 = mc.thePlayer.inventoryContainer.inventorySlots.get(var1);
      Slot var7 = mc.thePlayer.inventoryContainer.inventorySlots.get(var2);
      if (var6.getHasStack() && var7.getHasStack()) {
         ItemStack var8 = var6.getStack();
         ItemStack var9 = var7.getStack();
         return this.isCombinable(var8) && this.isCombinable(var9)
            ? EnchantmentHelper.getEnchantmentLevel(48, var8) > 0
               && EnchantmentHelper.getEnchantmentLevel(48, var8) < 5
               && var8.getUnlocalizedName().equals(var9.getUnlocalizedName())
               && EnchantmentHelper.getEnchantmentLevel(48, var8) == EnchantmentHelper.getEnchantmentLevel(48, var9)
            : false;
      } else {
         return false;
      }
   }

   public boolean shouldSwap() {
      if (this.swaps.size() > 0 && this.delay.hasPassed()) {
         EnumSetting var4 = (EnumSetting)general.getSetting("Swap-Mode");
         return this.isModeActive(var4);
      } else {
         return false;
      }
   }

   public void doSwaps() {
      for (int var4 = 0; var4 < this.swaps.size(); var4++) {
         Integer[] var5 = this.swaps.get(var4);
         if (this.canSwap(var5[0], var5[1])) {
            this.swap(var5[0], var5[1]);
            this.swaps.remove(var4);
         }
      }
   }

   public boolean canSwap(int var1, int var2) {
      return this.heldTime(var1) >= general.getSetting("Min-Hold-Time").asInt()
         && this.delay.hasPassed()
         && var2 >= 0
         && var2 <= 8
         && var1 >= 0
         && var1 <= mc.thePlayer.inventoryContainer.inventorySlots.size() - 1;
   }

   public long heldTime(int var1) {
      return System.currentTimeMillis() - this.changeTimes.get(var1);
   }

   public void swap(int var1, int var2) {
      this.replaceDropSlot(var1, 36 + var2);
      mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, var1, var2, 2, mc.thePlayer);
      this.delay = new InventoryManager.Delay(general.getSetting("Swap-Delay").asInt());
   }

   public void replaceDropSlot(int var1, int var2) {
      for (int var6 = 0; var6 < this.dropSlots.size(); var6++) {
         if (this.dropSlots.get(var6) == var1) {
            this.dropSlots.set(var6, var2);
         }
      }
   }

   public void processSwords(int var1) {
      int var5 = this.tools.getSetting("Swords").asInt();
      boolean var6 = this.enchants.getSetting("Combinable-Sharpness").asBoolean();
      if (this.drop.getSetting("Swords").asBoolean()) {
         while (this.swords.size() > 0) {
            InventoryManager.RankedSlot var7 = this.swords.peek();
            int var8 = var7.slot;
            if (this.swords.size() > var5 && (!var6 || !this.canCombineSharpness(this.bestSwordSlot, var8)) && !this.isWhitelisted(var8)) {
               int var9 = var1 - 1 + 36;
               if (var9 == var8 && this.swords.size() > 1) {
                  InventoryManager.RankedSlot var10 = this.swords.poll();
                  Slot var11 = mc.thePlayer.inventoryContainer.inventorySlots.get(var10.slot);
                  Slot var12 = mc.thePlayer.inventoryContainer.inventorySlots.get(this.swords.peek().slot);
                  if (var11.getHasStack() && var12.getHasStack() && this.swordScore(var11.getStack()) == this.swordScore(var12.getStack())) {
                     this.dropSlots.add(this.swords.poll().slot);
                     this.swords.add(var10);
                     continue;
                  }

                  this.swords.add(var10);
               }

               this.dropSlots.add(this.swords.poll().slot);
            } else {
               this.keepSlots.add(this.swords.poll().slot);
            }
         }
      }
   }

   public boolean isWhitelisted(int var1) {
      Slot var5 = mc.thePlayer.inventoryContainer.inventorySlots.get(var1);
      if (var5.getHasStack() && !this.dropSlots.contains(var1)) {
         ItemStack var6 = var5.getStack();
         if (var6 != null) {
            String var7 = var6.getUnlocalizedName();
            String var8 = var6.getDisplayName();
            if (this.hypixelUHCItems.getSetting("Forges").asBoolean() && var7.equals("tile.furnace") && var8.contains("Forge")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Dragon-Sword").asBoolean()
               && var7.equals("item.swordDiamond")
               && this.getLore(var6).contains("Weaponsmith Ultimate")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Bloodlust").asBoolean()
               && var7.equals("item.swordDiamond")
               && this.getLore(var6).contains("Gains Sharpness II after 1 kill")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Exodus").asBoolean() && var7.equals("item.helmetDiamond") && var8.contains("xodus")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Barbarian").asBoolean()
               && var7.equals("item.chestplateDiamond")
               && this.getLore(var6).contains("Resistance I (While Wearing)")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Vidar").asBoolean() && var7.equals("item.bootsDiamond") && var8.contains("Vidar")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Scythe").asBoolean() && var7.contains("item.hoe") && var8.contains("Scythe")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Perun").asBoolean() && var7.contains("item.hatchet") && var8.contains("Perun")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Excalibur").asBoolean() && var7.contains("item.sword") && var8.contains("Excalibur")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Anduril").asBoolean()
               && var7.equals("item.swordIron")
               && this.getLore(var6).contains("Resistance I (While Holding)")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Philo").asBoolean() && var7.equals("item.pickaxeDiamond") && var8.contains("Philo")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Expert-Seal").asBoolean() && var7.equals("item.netherStar")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Artemis-Bow").asBoolean() && var7.equals("item.bow") && var8.contains("Artemis")) {
               return true;
            }

            if (this.hypixelUHCItems.getSetting("Backpack").asBoolean() && var6.getItem() instanceof ItemSkull && var8.contains("Backpack")) {
               return true;
            }

            if (!this.hypixelUHCCrafts.getSetting("Backpack").asBoolean()
               || !var7.equals("tile.chest") && !var7.equals("item.stick") && !var7.equals("item.leather")) {
               if (this.hypixelUHCItems.getSetting("Modular-Bow").asBoolean() && var7.equals("item.bow") && var8.contains("Modular")) {
                  return true;
               }

               if (!this.hypixelUHCCrafts.getSetting("Eves-Temptation").asBoolean()
                  || !var7.equals("item.bone") && !var7.equals("item.dyePowder.white") && !var8.contains("Bone Meal")) {
                  if (!this.hypixelUHCCrafts.getSetting("Enlightening-Pack").asBoolean() || !var7.equals("item.redstone") && !var7.equals("tile.blockRedstone")
                     )
                   {
                     if (!this.hypixelUHCCrafts.getSetting("Sharpness-Books").asBoolean()
                        || !var7.equals("item.paper") && !var7.equals("item.swordIron") && !var7.equals("item.flint")) {
                        if (!this.hypixelUHCCrafts.getSetting("Protection-Books").asBoolean()
                           || !var7.equals("item.reeds") && !var7.equals("item.paper") && !var7.equals("item.ingotIron")) {
                           if (!this.hypixelUHCCrafts.getSetting("Seven-League").asBoolean() || !var7.equals("item.enderPearl") && !var7.equals("item.feather")
                              )
                            {
                              if (!this.hypixelUHCCrafts.getSetting("Philo").asBoolean()
                                 || !var7.equals("tile.oreIron") && !var7.equals("tile.oreGold") && !var7.equals("tile.blockLapis")) {
                                 if (!this.hypixelUHCCrafts.getSetting("Holy-Water").asBoolean()
                                    || !var7.equals("item.glassBottle") && !var7.equals("item.record") && !var7.equals("tile.blockRedstone")) {
                                    if (!this.hypixelUHCCrafts.getSetting("Arrow-Economy").asBoolean()
                                       || !var7.equals("item.feather") && !var7.equals("item.flint")) {
                                       if (this.enchants.getSetting("Fire-Aspect").asBoolean() && EnchantmentHelper.getEnchantmentLevel(20, var6) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Punch").asBoolean() && EnchantmentHelper.getEnchantmentLevel(49, var6) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Knockback").asBoolean() && EnchantmentHelper.getEnchantmentLevel(19, var6) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Thorns").asBoolean() && EnchantmentHelper.getEnchantmentLevel(7, var6) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Fortune").asBoolean() && EnchantmentHelper.getEnchantmentLevel(35, var6) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Flame").asBoolean() && EnchantmentHelper.getEnchantmentLevel(50, var6) > 0) {
                                          return true;
                                       }

                                       if (this.enchants.getSetting("Looting").asBoolean() && EnchantmentHelper.getEnchantmentLevel(21, var6) > 0) {
                                          return true;
                                       }

                                       boolean var9 = this.hypixelUHCItems.getSetting("Recipe-Books").asBoolean();
                                       if (var9 && var7.equals("item.enchantedBook")) {
                                          Matcher var11 = this.recipeBookPattern.matcher(var8);
                                          if (var11.find()) {
                                             return true;
                                          }
                                       } else if (!var9 && var7.equals("item.enchantedBook")) {
                                          Matcher var10 = this.recipeBookPattern.matcher(var8);
                                          if (var10.find()) {
                                             return false;
                                          }
                                       }

                                       return this.enchants.getSetting("Enchanted-Books").asBoolean() && var7.equals("item.enchantedBook");
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

   public boolean canCombineSharpness(int var1, int var2) {
      Slot var6 = mc.thePlayer.inventoryContainer.inventorySlots.get(var1);
      Slot var7 = mc.thePlayer.inventoryContainer.inventorySlots.get(var2);
      if (var6.getHasStack() && var7.getHasStack()) {
         ItemStack var8 = var6.getStack();
         ItemStack var9 = var7.getStack();
         return this.isCombinable(var8) && this.isCombinable(var9)
            ? EnchantmentHelper.getEnchantmentLevel(16, var8) > 0
               && EnchantmentHelper.getEnchantmentLevel(16, var8) < 5
               && var8.getUnlocalizedName().equals(var9.getUnlocalizedName())
               && EnchantmentHelper.getEnchantmentLevel(16, var8) == EnchantmentHelper.getEnchantmentLevel(16, var9)
            : false;
      } else {
         return false;
      }
   }

   public boolean isCombinable(ItemStack var1) {
      String var5 = var1.getUnlocalizedName();
      String var6 = var1.getDisplayName();
      if (var5.equals("item.chestplateDiamond") && this.getLore(var1).contains("Resistance I (While Wearing)")) {
         return false;
      } else if (var5.equals("item.swordIron") && this.getLore(var1).contains("Resistance I (While Holding)")) {
         return false;
      } else if (var5.contains("item.sword") && var6.contains("Excalibur")) {
         return false;
      } else if (var5.equals("item.swordIron") && var6.contains("Apprentice")) {
         return false;
      } else {
         return var5.equals("item.helmetIron") && var6.contains("Apprentice") ? false : !var5.equals("item.helmetDiamond") || !var6.contains("Tarnhelm");
      }
   }

   public String getLore(ItemStack var1) {
      StringBuilder var5 = new StringBuilder();
      if (var1.getTagCompound() != null && var1.getTagCompound().hasKey("display", 10)) {
         NBTTagCompound var6 = var1.getTagCompound().getCompoundTag("display");
         NBTTagList var7 = var6.getTagList("Lore", 8);
         if (var7.tagCount() > 0) {
            for (int var8 = 0; var8 < var7.tagCount(); var8++) {
               var5.append(var7.getStringTagAt(var8));
            }
         }
      }

      return var5.toString();
   }

   public boolean shouldDrop() {
      if (this.dropSlots.size() > 0 && this.delay.hasPassed()) {
         EnumSetting var4 = (EnumSetting)general.getSetting("Drop-Mode");
         return this.isModeActive(var4);
      } else {
         return false;
      }
   }

   private boolean isModeActive(EnumSetting<InventoryManagerMode> var1) {
      if (mc.currentScreen instanceof GuiChest) {
         return false;
      } else if (var1.getValue() == InventoryManagerMode.INVENTORY && mc.currentScreen instanceof GuiInventory) {
         return true;
      } else if (var1.getValue() == InventoryManagerMode.INVENTORY_LSHIFT && mc.currentScreen instanceof GuiInventory && Keyboard.isKeyDown(42)) {
         return true;
      } else {
         return var1.getValue() == InventoryManagerMode.NOT_MOVING && mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F
            ? true
            : var1.getValue() == InventoryManagerMode.ALWAYS;
      }
   }

   public void doDrops() {
      for (int var4 = 0; var4 < this.dropSlots.size(); var4++) {
         if (this.canDrop(this.dropSlots.get(var4))) {
            this.dropSlot(this.dropSlots.remove(var4));
         }
      }
   }

   public boolean canDrop(int var1) {
      return this.heldTime(var1) >= general.getSetting("Min-Hold-Time").asInt()
         && this.delay.hasPassed()
         && var1 >= 0
         && var1 <= mc.thePlayer.inventoryContainer.inventorySlots.size() - 1;
   }

   public void dropSlot(int var1) {
      if (var1 >= 0 && var1 < mc.thePlayer.inventoryContainer.inventorySlots.size()) {
         InventoryUtil.dropSlot(var1, 0);
         this.delay = new InventoryManager.Delay(general.getSetting("Drop-Delay").asInt());
      }
   }

   public boolean isHotbarSlot(int var1) {
      return var1 >= 36 && var1 <= 44;
   }

   public void drawSlotWarning(RenderEvent var1) {
      if (!this.slotsUnique) {
         mc.fontRendererObj
            .drawStringWithShadow(
               "Some InventoryManager slots are the same, make each slot unique for sorting to work",
               var1.getResolution().getScaledWidth() / 2.0F
                  - mc.fontRendererObj.getStringWidth("Some InventoryManager slots are the same, make each slot unique for sorting to work") / 2.0F,
               var1.getResolution().getScaledHeight() - 100,
               Color.RED.getRGB()
            );
         this.delay = new InventoryManager.Delay(500L);
      }
   }

   public void update() {
      int var4 = slots.getSetting("Sword-Slot").asInt();
      int var5 = slots.getSetting("Golden-Apple-Slot").asInt();
      int var6 = slots.getSetting("Head-Slot").asInt();
      int var7 = slots.getSetting("Pearl-Slot").asInt();
      int var8 = slots.getSetting("Bow-Slot").asInt();
      int var9 = slots.getSetting("Fishing-Rod-Slot").asInt();
      int var10 = slots.getSetting("Pickaxe-Slot").asInt();
      int var11 = slots.getSetting("Axe-Slot").asInt();
      int var12 = slots.getSetting("Shovel-Slot").asInt();
      int var13 = slots.getSetting("Blocks-Slot").asInt();
      boolean var14 = this.slotsUnique = !OldInventoryManager.hasDuplicateSlots(new int[]{var4, var5, var6, var7, var8, var9, var10, var11, var12, var13});
      if (var14) {
         this.inInventory = mc.currentScreen instanceof GuiInventory;
         if (this.inInventory != this.wasInInventory) {
            this.delay = new InventoryManager.Delay(general.getSetting("Open-Inv-Delay").asInt());
         }

         this.wasInInventory = this.inInventory;
         this.scanInventory();
         this.generateSwapList(var4, var8, var10, var12, var11, var9, var5, var7, var6, var13);
         if (this.shouldSwap()) {
            this.doSwaps();
         }

         this.generateDropList(var4, var8, var10, var12, var11, var9, var13, var5, var7, var6);
         if (this.shouldDrop()) {
            this.doDrops();
         }
      }
   }

   public boolean wouldKeep(ItemStack var1) {
      this.dropSlots.clear();
      int var5 = slots.getSetting("Sword-Slot").asInt();
      int var6 = slots.getSetting("Golden-Apple-Slot").asInt();
      int var7 = slots.getSetting("Head-Slot").asInt();
      int var8 = slots.getSetting("Pearl-Slot").asInt();
      int var9 = slots.getSetting("Bow-Slot").asInt();
      int var10 = slots.getSetting("Fishing-Rod-Slot").asInt();
      int var11 = slots.getSetting("Pickaxe-Slot").asInt();
      int var12 = slots.getSetting("Axe-Slot").asInt();
      int var13 = slots.getSetting("Shovel-Slot").asInt();
      int var14 = slots.getSetting("Blocks-Slot").asInt();
      Slot var15 = new Slot(mc.thePlayer.inventory, 28, 0, 0);
      ItemStack var16 = mc.thePlayer.inventory.mainInventory[28];
      mc.thePlayer.inventoryContainer.inventorySlots.add(28, var15);
      var15.putStack(var1);
      this.scanInventory();
      this.generateDropList(var5, var9, var11, var13, var12, var10, var14, var6, var8, var7);
      mc.thePlayer.inventoryContainer.inventorySlots.remove(var15);
      mc.thePlayer.inventory.mainInventory[28] = var16;
      return !this.dropSlots.contains(28);
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   public class Delay {
      public long start = System.currentTimeMillis();
      public long delay;
      private static final long a = 70079219502023L;

      public Delay() {
         this.delay = 0L;
      }

      public Delay(long var2) {
         this.delay = var2;
      }

      public boolean hasPassed() {
         return System.currentTimeMillis() - this.delay > this.start;
      }
   }

   public class RankedSlot implements Comparable<InventoryManager.RankedSlot> {
      public int slot;
      public float rank;
      private static final long a = 71847512793467L;

      public RankedSlot(int var2, float var3) {
         this.slot = var2;
         this.rank = var3;
      }

      public int compareTo(InventoryManager.RankedSlot var1) {
         if (this.rank != var1.rank) {
            return this.rank > var1.rank ? 1 : -1;
         } else {
            Slot var5 = ClientAccess.mc.thePlayer.inventoryContainer.inventorySlots.get(this.slot);
            Slot var6 = ClientAccess.mc.thePlayer.inventoryContainer.inventorySlots.get(var1.slot);
            if (var5.getHasStack() && var6.getHasStack()) {
               return InventoryManager.this.remainingDurability(var5.getStack()) > InventoryManager.this.remainingDurability(var6.getStack()) ? 1 : -1;
            } else {
               return 0;
            }
         }
      }
   }
}
