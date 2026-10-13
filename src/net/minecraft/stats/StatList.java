package net.minecraft.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;

public class StatList {
   public static Map<String, StatBase> oneShotStats = Maps.newHashMap();
   public static List<StatBase> allStats = Lists.newArrayList();
   public static List<StatBase> generalStats = Lists.newArrayList();
   public static List<StatCrafting> itemStats = Lists.newArrayList();
   public static List<StatCrafting> objectMineStats = Lists.newArrayList();
   public static StatBase leaveGameStat = new StatBasic("stat.leaveGame", new ChatComponentTranslation("stat.leaveGame")).initIndependentStat().registerStat();
   public static StatBase minutesPlayedStat = new StatBasic("stat.playOneMinute", new ChatComponentTranslation("stat.playOneMinute"), StatBase.timeStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase timeSinceDeathStat = new StatBasic("stat.timeSinceDeath", new ChatComponentTranslation("stat.timeSinceDeath"), StatBase.timeStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceWalkedStat = new StatBasic("stat.walkOneCm", new ChatComponentTranslation("stat.walkOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceCrouchedStat = new StatBasic("stat.crouchOneCm", new ChatComponentTranslation("stat.crouchOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceSprintedStat = new StatBasic("stat.sprintOneCm", new ChatComponentTranslation("stat.sprintOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceSwumStat = new StatBasic("stat.swimOneCm", new ChatComponentTranslation("stat.swimOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceFallenStat = new StatBasic("stat.fallOneCm", new ChatComponentTranslation("stat.fallOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceClimbedStat = new StatBasic("stat.climbOneCm", new ChatComponentTranslation("stat.climbOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceFlownStat = new StatBasic("stat.flyOneCm", new ChatComponentTranslation("stat.flyOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceDoveStat = new StatBasic("stat.diveOneCm", new ChatComponentTranslation("stat.diveOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceByMinecartStat = new StatBasic(
         "stat.minecartOneCm", new ChatComponentTranslation("stat.minecartOneCm"), StatBase.distanceStatType
      )
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceByBoatStat = new StatBasic("stat.boatOneCm", new ChatComponentTranslation("stat.boatOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceByPigStat = new StatBasic("stat.pigOneCm", new ChatComponentTranslation("stat.pigOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceByHorseStat = new StatBasic("stat.horseOneCm", new ChatComponentTranslation("stat.horseOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase jumpStat = new StatBasic("stat.jump", new ChatComponentTranslation("stat.jump")).initIndependentStat().registerStat();
   public static StatBase dropStat = new StatBasic("stat.drop", new ChatComponentTranslation("stat.drop")).initIndependentStat().registerStat();
   public static StatBase damageDealtStat = new StatBasic("stat.damageDealt", new ChatComponentTranslation("stat.damageDealt"), StatBase.field_111202_k)
      .registerStat();
   public static StatBase damageTakenStat = new StatBasic("stat.damageTaken", new ChatComponentTranslation("stat.damageTaken"), StatBase.field_111202_k)
      .registerStat();
   public static StatBase deathsStat = new StatBasic("stat.deaths", new ChatComponentTranslation("stat.deaths")).registerStat();
   public static StatBase mobKillsStat = new StatBasic("stat.mobKills", new ChatComponentTranslation("stat.mobKills")).registerStat();
   public static StatBase animalsBredStat = new StatBasic("stat.animalsBred", new ChatComponentTranslation("stat.animalsBred")).registerStat();
   public static StatBase playerKillsStat = new StatBasic("stat.playerKills", new ChatComponentTranslation("stat.playerKills")).registerStat();
   public static StatBase fishCaughtStat = new StatBasic("stat.fishCaught", new ChatComponentTranslation("stat.fishCaught")).registerStat();
   public static StatBase junkFishedStat = new StatBasic("stat.junkFished", new ChatComponentTranslation("stat.junkFished")).registerStat();
   public static StatBase treasureFishedStat = new StatBasic("stat.treasureFished", new ChatComponentTranslation("stat.treasureFished")).registerStat();
   public static StatBase timesTalkedToVillagerStat = new StatBasic("stat.talkedToVillager", new ChatComponentTranslation("stat.talkedToVillager"))
      .registerStat();
   public static StatBase timesTradedWithVillagerStat = new StatBasic("stat.tradedWithVillager", new ChatComponentTranslation("stat.tradedWithVillager"))
      .registerStat();
   public static StatBase field_181724_H = new StatBasic("stat.cakeSlicesEaten", new ChatComponentTranslation("stat.cakeSlicesEaten")).registerStat();
   public static StatBase field_181725_I = new StatBasic("stat.cauldronFilled", new ChatComponentTranslation("stat.cauldronFilled")).registerStat();
   public static StatBase field_181726_J = new StatBasic("stat.cauldronUsed", new ChatComponentTranslation("stat.cauldronUsed")).registerStat();
   public static StatBase field_181727_K = new StatBasic("stat.armorCleaned", new ChatComponentTranslation("stat.armorCleaned")).registerStat();
   public static StatBase field_181728_L = new StatBasic("stat.bannerCleaned", new ChatComponentTranslation("stat.bannerCleaned")).registerStat();
   public static StatBase field_181729_M = new StatBasic("stat.brewingstandInteraction", new ChatComponentTranslation("stat.brewingstandInteraction"))
      .registerStat();
   public static StatBase field_181730_N = new StatBasic("stat.beaconInteraction", new ChatComponentTranslation("stat.beaconInteraction")).registerStat();
   public static StatBase field_181731_O = new StatBasic("stat.dropperInspected", new ChatComponentTranslation("stat.dropperInspected")).registerStat();
   public static StatBase field_181732_P = new StatBasic("stat.hopperInspected", new ChatComponentTranslation("stat.hopperInspected")).registerStat();
   public static StatBase field_181733_Q = new StatBasic("stat.dispenserInspected", new ChatComponentTranslation("stat.dispenserInspected")).registerStat();
   public static StatBase field_181734_R = new StatBasic("stat.noteblockPlayed", new ChatComponentTranslation("stat.noteblockPlayed")).registerStat();
   public static StatBase field_181735_S = new StatBasic("stat.noteblockTuned", new ChatComponentTranslation("stat.noteblockTuned")).registerStat();
   public static StatBase field_181736_T = new StatBasic("stat.flowerPotted", new ChatComponentTranslation("stat.flowerPotted")).registerStat();
   public static StatBase field_181737_U = new StatBasic("stat.trappedChestTriggered", new ChatComponentTranslation("stat.trappedChestTriggered"))
      .registerStat();
   public static StatBase field_181738_V = new StatBasic("stat.enderchestOpened", new ChatComponentTranslation("stat.enderchestOpened")).registerStat();
   public static StatBase field_181739_W = new StatBasic("stat.itemEnchanted", new ChatComponentTranslation("stat.itemEnchanted")).registerStat();
   public static StatBase field_181740_X = new StatBasic("stat.recordPlayed", new ChatComponentTranslation("stat.recordPlayed")).registerStat();
   public static StatBase field_181741_Y = new StatBasic("stat.furnaceInteraction", new ChatComponentTranslation("stat.furnaceInteraction")).registerStat();
   public static StatBase field_181742_Z = new StatBasic("stat.craftingTableInteraction", new ChatComponentTranslation("stat.workbenchInteraction"))
      .registerStat();
   public static StatBase field_181723_aa = new StatBasic("stat.chestOpened", new ChatComponentTranslation("stat.chestOpened")).registerStat();
   public static final StatBase[] mineBlockStatArray = new StatBase[4096];
   public static final StatBase[] objectCraftStats = new StatBase[32000];
   public static final StatBase[] objectUseStats = new StatBase[32000];
   public static final StatBase[] objectBreakStats = new StatBase[32000];
   private static final long ab = 87223946156603L;

   public static void init() {
      initMiningStats();
      initStats();
      initItemDepleteStats();
      initCraftableStats();
      AchievementList.init();
      EntityList.func_151514_a();
   }

   private static void initCraftableStats() {
      HashSet var3 = Sets.newHashSet();

      for (IRecipe var5 : CraftingManager.getInstance().getRecipeList()) {
         if (var5.getRecipeOutput() != null) {
            var3.add(var5.getRecipeOutput().getItem());
         }
      }

      for (ItemStack var10 : FurnaceRecipes.instance().getSmeltingList().values()) {
         var3.add(var10.getItem());
      }

      for (Object var11$raw : var3) {
         Item var11 = (Item)var11$raw;
         if (var11 != null) {
            int var6 = Item.getIdFromItem(var11);
            String var7 = func_180204_a(var11);
            if (var7 != null) {
               objectCraftStats[var6] = new StatCrafting(
                     "stat.craftItem.", var7, new ChatComponentTranslation("stat.craftItem", new ItemStack(var11).getChatComponent()), var11
                  )
                  .registerStat();
            }
         }
      }

      replaceAllSimilarBlocks(objectCraftStats);
   }

   private static void initMiningStats() {
      for (Block var4 : Block.blockRegistry) {
         Item var5 = Item.getItemFromBlock(var4);
         if (var5 != null) {
            int var6 = Block.getIdFromBlock(var4);
            String var7 = func_180204_a(var5);
            if (var7 != null && var4.getEnableStats()) {
               mineBlockStatArray[var6] = new StatCrafting(
                     "stat.mineBlock.", var7, new ChatComponentTranslation("stat.mineBlock", new ItemStack(var4).getChatComponent()), var5
                  )
                  .registerStat();
               objectMineStats.add((StatCrafting)mineBlockStatArray[var6]);
            }
         }
      }

      replaceAllSimilarBlocks(mineBlockStatArray);
   }

   private static void initStats() {
      for (Item var4 : Item.itemRegistry) {
         if (var4 != null) {
            int var5 = Item.getIdFromItem(var4);
            String var6 = func_180204_a(var4);
            if (var6 != null) {
               objectUseStats[var5] = new StatCrafting(
                     "stat.useItem.", var6, new ChatComponentTranslation("stat.useItem", new ItemStack(var4).getChatComponent()), var4
                  )
                  .registerStat();
               if (!(var4 instanceof ItemBlock)) {
                  itemStats.add((StatCrafting)objectUseStats[var5]);
               }
            }
         }
      }

      replaceAllSimilarBlocks(objectUseStats);
   }

   private static void initItemDepleteStats() {
      for (Item var4 : Item.itemRegistry) {
         if (var4 != null) {
            int var5 = Item.getIdFromItem(var4);
            String var6 = func_180204_a(var4);
            if (var6 != null && var4.isDamageable()) {
               objectBreakStats[var5] = new StatCrafting(
                     "stat.breakItem.", var6, new ChatComponentTranslation("stat.breakItem", new ItemStack(var4).getChatComponent()), var4
                  )
                  .registerStat();
            }
         }
      }

      replaceAllSimilarBlocks(objectBreakStats);
   }

   private static String func_180204_a(Item var0) {
      ResourceLocation var4 = Item.itemRegistry.getNameForObject(var0);
      return var4 != null ? var4.toString().replace(':', '.') : null;
   }

   private static void replaceAllSimilarBlocks(StatBase[] var0) {
      mergeStatBases(var0, Blocks.water, Blocks.flowing_water);
      mergeStatBases(var0, Blocks.lava, Blocks.flowing_lava);
      mergeStatBases(var0, Blocks.lit_pumpkin, Blocks.pumpkin);
      mergeStatBases(var0, Blocks.lit_furnace, Blocks.furnace);
      mergeStatBases(var0, Blocks.lit_redstone_ore, Blocks.redstone_ore);
      mergeStatBases(var0, Blocks.powered_repeater, Blocks.unpowered_repeater);
      mergeStatBases(var0, Blocks.powered_comparator, Blocks.unpowered_comparator);
      mergeStatBases(var0, Blocks.redstone_torch, Blocks.unlit_redstone_torch);
      mergeStatBases(var0, Blocks.lit_redstone_lamp, Blocks.redstone_lamp);
      mergeStatBases(var0, Blocks.double_stone_slab, Blocks.stone_slab);
      mergeStatBases(var0, Blocks.double_wooden_slab, Blocks.wooden_slab);
      mergeStatBases(var0, Blocks.double_stone_slab2, Blocks.stone_slab2);
      mergeStatBases(var0, Blocks.grass, Blocks.dirt);
      mergeStatBases(var0, Blocks.farmland, Blocks.dirt);
   }

   private static void mergeStatBases(StatBase[] var0, Block var1, Block var2) {
      int var6 = Block.getIdFromBlock(var1);
      int var7 = Block.getIdFromBlock(var2);
      if (var0[var6] != null && var0[var7] == null) {
         var0[var7] = var0[var6];
      } else {
         allStats.remove(var0[var6]);
         objectMineStats.remove(var0[var6]);
         generalStats.remove(var0[var6]);
         var0[var6] = var0[var7];
      }
   }

   public static StatBase getStatKillEntity(EntityList.EntityEggInfo var0) {
      String var3 = EntityList.getStringFromID(var0.spawnedID);
      return var3 == null
         ? null
         : new StatBase("stat.killEntity." + var3, new ChatComponentTranslation("stat.entityKill", new ChatComponentTranslation("entity." + var3 + ".name")))
            .registerStat();
   }

   public static StatBase getStatEntityKilledBy(EntityList.EntityEggInfo var0) {
      String var3 = EntityList.getStringFromID(var0.spawnedID);
      return var3 == null
         ? null
         : new StatBase(
               "stat.entityKilledBy." + var3, new ChatComponentTranslation("stat.entityKilledBy", new ChatComponentTranslation("entity." + var3 + ".name"))
            )
            .registerStat();
   }

   public static StatBase getOneShotStat(String var0) {
      return oneShotStats.get(var0);
   }

   static {
      long var20 = ab ^ 35351384066293L;
      long var10000 = 1375895848788757002L ^ var20;
   }
}
