package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BiomeGenBase {
   private static final Logger logger = LogManager.getLogger();
   public static final BiomeGenBase.Height height_Default = new BiomeGenBase.Height(0.1F, 0.2F);
   public static final BiomeGenBase.Height height_ShallowWaters = new BiomeGenBase.Height(-0.5F, 0.0F);
   public static final BiomeGenBase.Height height_Oceans = new BiomeGenBase.Height(-1.0F, 0.1F);
   public static final BiomeGenBase.Height height_DeepOceans = new BiomeGenBase.Height(-1.8F, 0.1F);
   public static final BiomeGenBase.Height height_LowPlains = new BiomeGenBase.Height(0.125F, 0.05F);
   public static final BiomeGenBase.Height height_MidPlains = new BiomeGenBase.Height(0.2F, 0.2F);
   public static final BiomeGenBase.Height height_LowHills = new BiomeGenBase.Height(0.45F, 0.3F);
   public static final BiomeGenBase.Height height_HighPlateaus = new BiomeGenBase.Height(1.5F, 0.025F);
   public static final BiomeGenBase.Height height_MidHills = new BiomeGenBase.Height(1.0F, 0.5F);
   public static final BiomeGenBase.Height height_Shores = new BiomeGenBase.Height(0.0F, 0.025F);
   public static final BiomeGenBase.Height height_RockyWaters = new BiomeGenBase.Height(0.1F, 0.8F);
   public static final BiomeGenBase.Height height_LowIslands = new BiomeGenBase.Height(0.2F, 0.3F);
   public static final BiomeGenBase.Height height_PartiallySubmerged = new BiomeGenBase.Height(-0.2F, 0.1F);
   private static final BiomeGenBase[] biomeList = new BiomeGenBase[256];
   public static final Set<BiomeGenBase> explorationBiomesList = Sets.newHashSet();
   public static final Map<String, BiomeGenBase> BIOME_ID_MAP = Maps.newHashMap();
   public static final BiomeGenBase ocean = new BiomeGenOcean(0).e(112).setBiomeName("Ocean").setHeight(height_Oceans);
   public static final BiomeGenBase plains = new BiomeGenPlains(1).e(9286496).setBiomeName("Plains");
   public static final BiomeGenBase desert = new BiomeGenDesert(2)
      .e(16421912)
      .setBiomeName("Desert")
      .setDisableRain()
      .setTemperatureRainfall(2.0F, 0.0F)
      .setHeight(height_LowPlains);
   public static final BiomeGenBase extremeHills = new BiomeGenHills(3, false)
      .e(6316128)
      .setBiomeName("Extreme Hills")
      .setHeight(height_MidHills)
      .setTemperatureRainfall(0.2F, 0.3F);
   public static final BiomeGenBase forest = new BiomeGenForest(4, 0).e(353825).setBiomeName("Forest");
   public static final BiomeGenBase taiga = new BiomeGenTaiga(5, 0)
      .e(747097)
      .setBiomeName("Taiga")
      .setFillerBlockMetadata(5159473)
      .setTemperatureRainfall(0.25F, 0.8F)
      .setHeight(height_MidPlains);
   public static final BiomeGenBase swampland = new BiomeGenSwamp(6)
      .e(522674)
      .setBiomeName("Swampland")
      .setFillerBlockMetadata(9154376)
      .setHeight(height_PartiallySubmerged)
      .setTemperatureRainfall(0.8F, 0.9F);
   public static final BiomeGenBase river = new BiomeGenRiver(7).e(255).setBiomeName("River").setHeight(height_ShallowWaters);
   public static final BiomeGenBase hell = new BiomeGenHell(8).e(16711680).setBiomeName("Hell").setDisableRain().setTemperatureRainfall(2.0F, 0.0F);
   public static final BiomeGenBase sky = new BiomeGenEnd(9).e(8421631).setBiomeName("The End").setDisableRain();
   public static final BiomeGenBase frozenOcean = new BiomeGenOcean(10)
      .e(9474208)
      .setBiomeName("FrozenOcean")
      .setEnableSnow()
      .setHeight(height_Oceans)
      .setTemperatureRainfall(0.0F, 0.5F);
   public static final BiomeGenBase frozenRiver = new BiomeGenRiver(11)
      .e(10526975)
      .setBiomeName("FrozenRiver")
      .setEnableSnow()
      .setHeight(height_ShallowWaters)
      .setTemperatureRainfall(0.0F, 0.5F);
   public static final BiomeGenBase icePlains = new BiomeGenSnow(12, false)
      .e(16777215)
      .setBiomeName("Ice Plains")
      .setEnableSnow()
      .setTemperatureRainfall(0.0F, 0.5F)
      .setHeight(height_LowPlains);
   public static final BiomeGenBase iceMountains = new BiomeGenSnow(13, false)
      .e(10526880)
      .setBiomeName("Ice Mountains")
      .setEnableSnow()
      .setHeight(height_LowHills)
      .setTemperatureRainfall(0.0F, 0.5F);
   public static final BiomeGenBase mushroomIsland = new BiomeGenMushroomIsland(14)
      .e(16711935)
      .setBiomeName("MushroomIsland")
      .setTemperatureRainfall(0.9F, 1.0F)
      .setHeight(height_LowIslands);
   public static final BiomeGenBase mushroomIslandShore = new BiomeGenMushroomIsland(15)
      .e(10486015)
      .setBiomeName("MushroomIslandShore")
      .setTemperatureRainfall(0.9F, 1.0F)
      .setHeight(height_Shores);
   public static final BiomeGenBase beach = new BiomeGenBeach(16).e(16440917).setBiomeName("Beach").setTemperatureRainfall(0.8F, 0.4F).setHeight(height_Shores);
   public static final BiomeGenBase desertHills = new BiomeGenDesert(17)
      .e(13786898)
      .setBiomeName("DesertHills")
      .setDisableRain()
      .setTemperatureRainfall(2.0F, 0.0F)
      .setHeight(height_LowHills);
   public static final BiomeGenBase forestHills = new BiomeGenForest(18, 0).e(2250012).setBiomeName("ForestHills").setHeight(height_LowHills);
   public static final BiomeGenBase taigaHills = new BiomeGenTaiga(19, 0)
      .e(1456435)
      .setBiomeName("TaigaHills")
      .setFillerBlockMetadata(5159473)
      .setTemperatureRainfall(0.25F, 0.8F)
      .setHeight(height_LowHills);
   public static final BiomeGenBase extremeHillsEdge = new BiomeGenHills(20, true)
      .e(7501978)
      .setBiomeName("Extreme Hills Edge")
      .setHeight(height_MidHills.attenuate())
      .setTemperatureRainfall(0.2F, 0.3F);
   public static final BiomeGenBase jungle = new BiomeGenJungle(21, false)
      .e(5470985)
      .setBiomeName("Jungle")
      .setFillerBlockMetadata(5470985)
      .setTemperatureRainfall(0.95F, 0.9F);
   public static final BiomeGenBase jungleHills = new BiomeGenJungle(22, false)
      .e(2900485)
      .setBiomeName("JungleHills")
      .setFillerBlockMetadata(5470985)
      .setTemperatureRainfall(0.95F, 0.9F)
      .setHeight(height_LowHills);
   public static final BiomeGenBase jungleEdge = new BiomeGenJungle(23, true)
      .e(6458135)
      .setBiomeName("JungleEdge")
      .setFillerBlockMetadata(5470985)
      .setTemperatureRainfall(0.95F, 0.8F);
   public static final BiomeGenBase deepOcean = new BiomeGenOcean(24).e(48).setBiomeName("Deep Ocean").setHeight(height_DeepOceans);
   public static final BiomeGenBase stoneBeach = new BiomeGenStoneBeach(25)
      .e(10658436)
      .setBiomeName("Stone Beach")
      .setTemperatureRainfall(0.2F, 0.3F)
      .setHeight(height_RockyWaters);
   public static final BiomeGenBase coldBeach = new BiomeGenBeach(26)
      .e(16445632)
      .setBiomeName("Cold Beach")
      .setTemperatureRainfall(0.05F, 0.3F)
      .setHeight(height_Shores)
      .setEnableSnow();
   public static final BiomeGenBase birchForest = new BiomeGenForest(27, 2).setBiomeName("Birch Forest").e(3175492);
   public static final BiomeGenBase birchForestHills = new BiomeGenForest(28, 2).setBiomeName("Birch Forest Hills").e(2055986).setHeight(height_LowHills);
   public static final BiomeGenBase roofedForest = new BiomeGenForest(29, 3).e(4215066).setBiomeName("Roofed Forest");
   public static final BiomeGenBase coldTaiga = new BiomeGenTaiga(30, 0)
      .e(3233098)
      .setBiomeName("Cold Taiga")
      .setFillerBlockMetadata(5159473)
      .setEnableSnow()
      .setTemperatureRainfall(-0.5F, 0.4F)
      .setHeight(height_MidPlains)
      .F(16777215);
   public static final BiomeGenBase coldTaigaHills = new BiomeGenTaiga(31, 0)
      .e(2375478)
      .setBiomeName("Cold Taiga Hills")
      .setFillerBlockMetadata(5159473)
      .setEnableSnow()
      .setTemperatureRainfall(-0.5F, 0.4F)
      .setHeight(height_LowHills)
      .F(16777215);
   public static final BiomeGenBase megaTaiga = new BiomeGenTaiga(32, 1)
      .e(5858897)
      .setBiomeName("Mega Taiga")
      .setFillerBlockMetadata(5159473)
      .setTemperatureRainfall(0.3F, 0.8F)
      .setHeight(height_MidPlains);
   public static final BiomeGenBase megaTaigaHills = new BiomeGenTaiga(33, 1)
      .e(4542270)
      .setBiomeName("Mega Taiga Hills")
      .setFillerBlockMetadata(5159473)
      .setTemperatureRainfall(0.3F, 0.8F)
      .setHeight(height_LowHills);
   public static final BiomeGenBase extremeHillsPlus = new BiomeGenHills(34, true)
      .e(5271632)
      .setBiomeName("Extreme Hills+")
      .setHeight(height_MidHills)
      .setTemperatureRainfall(0.2F, 0.3F);
   public static final BiomeGenBase savanna = new BiomeGenSavanna(35)
      .e(12431967)
      .setBiomeName("Savanna")
      .setTemperatureRainfall(1.2F, 0.0F)
      .setDisableRain()
      .setHeight(height_LowPlains);
   public static final BiomeGenBase savannaPlateau = new BiomeGenSavanna(36)
      .e(10984804)
      .setBiomeName("Savanna Plateau")
      .setTemperatureRainfall(1.0F, 0.0F)
      .setDisableRain()
      .setHeight(height_HighPlateaus);
   public static final BiomeGenBase mesa = new BiomeGenMesa(37, false, false).e(14238997).setBiomeName("Mesa");
   public static final BiomeGenBase mesaPlateau_F = new BiomeGenMesa(38, false, true).e(11573093).setBiomeName("Mesa Plateau F").setHeight(height_HighPlateaus);
   public static final BiomeGenBase mesaPlateau = new BiomeGenMesa(39, false, false).e(13274213).setBiomeName("Mesa Plateau").setHeight(height_HighPlateaus);
   public static final BiomeGenBase field_180279_ad = ocean;
   public static final NoiseGeneratorPerlin temperatureNoise;
   public static final NoiseGeneratorPerlin GRASS_COLOR_NOISE;
   public static final WorldGenDoublePlant DOUBLE_PLANT_GENERATOR;
   public String biomeName;
   public int color;
   public int field_150609_ah;
   public IBlockState topBlock = Blocks.grass.getDefaultState();
   public IBlockState fillerBlock = Blocks.dirt.getDefaultState();
   public int fillerBlockMetadata = 5169201;
   public float minHeight = height_Default.rootHeight;
   public float maxHeight = height_Default.variation;
   public float temperature = 0.5F;
   public float rainfall = 0.5F;
   public int waterColorMultiplier = 16777215;
   public BiomeDecorator theBiomeDecorator;
   public List<BiomeGenBase.SpawnListEntry> spawnableMonsterList = Lists.newArrayList();
   public List<BiomeGenBase.SpawnListEntry> spawnableCreatureList = Lists.newArrayList();
   public List<BiomeGenBase.SpawnListEntry> spawnableWaterCreatureList = Lists.newArrayList();
   public List<BiomeGenBase.SpawnListEntry> spawnableCaveCreatureList = Lists.newArrayList();
   public boolean enableSnow;
   public boolean enableRain = true;
   public final int biomeID;
   public WorldGenTrees worldGeneratorTrees = new WorldGenTrees(false);
   public WorldGenBigTree worldGeneratorBigTree = new WorldGenBigTree(false);
   public WorldGenSwamp worldGeneratorSwamp = new WorldGenSwamp();
   private static final long ab = 131356532790921L;

   public BiomeGenBase(int var1) {
      this.biomeID = var1;
      biomeList[var1] = this;
      this.theBiomeDecorator = this.createBiomeDecorator();
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntitySheep.class, 12, 4, 4));
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityRabbit.class, 10, 3, 3));
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityPig.class, 10, 4, 4));
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityChicken.class, 10, 4, 4));
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityCow.class, 8, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySpider.class, 100, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityZombie.class, 100, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySkeleton.class, 100, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityCreeper.class, 100, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 100, 4, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityEnderman.class, 10, 1, 4));
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityWitch.class, 5, 1, 1));
      this.spawnableWaterCreatureList.add(new BiomeGenBase.SpawnListEntry(EntitySquid.class, 10, 4, 4));
      this.spawnableCaveCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityBat.class, 10, 8, 8));
   }

   public BiomeDecorator createBiomeDecorator() {
      return new BiomeDecorator();
   }

   public BiomeGenBase setTemperatureRainfall(float var1, float var2) {
      if (var1 > 0.1F && var1 < 0.2F) {
         throw new IllegalArgumentException("Please avoid temperatures in the range 0.1 - 0.2 because of snow");
      }

      this.temperature = var1;
      this.rainfall = var2;
      return this;
   }

   public final BiomeGenBase setHeight(BiomeGenBase.Height var1) {
      this.minHeight = var1.rootHeight;
      this.maxHeight = var1.variation;
      return this;
   }

   public BiomeGenBase setDisableRain() {
      this.enableRain = false;
      return this;
   }

   public WorldGenAbstractTree genBigTreeChance(Random var1) {
      return var1.nextInt(10) == 0 ? this.worldGeneratorBigTree : this.worldGeneratorTrees;
   }

   public WorldGenerator getRandomWorldGenForGrass(Random var1) {
      return new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
   }

   public BlockFlower.EnumFlowerType pickRandomFlower(Random var1, BlockPos var2) {
      return var1.nextInt(3) > 0 ? BlockFlower.EnumFlowerType.DANDELION : BlockFlower.EnumFlowerType.POPPY;
   }

   public BiomeGenBase setEnableSnow() {
      this.enableSnow = true;
      return this;
   }

   public BiomeGenBase setBiomeName(String var1) {
      this.biomeName = var1;
      return this;
   }

   public BiomeGenBase setFillerBlockMetadata(int var1) {
      this.fillerBlockMetadata = var1;
      return this;
   }

   public BiomeGenBase e(int var1) {
      this.func_150557_a(var1, false);
      return this;
   }

   public BiomeGenBase F(int var1) {
      this.field_150609_ah = var1;
      return this;
   }

   public BiomeGenBase func_150557_a(int var1, boolean var2) {
      this.color = var1;
      if (var2) {
         this.field_150609_ah = (var1 & 16711422) >> 1;
      } else {
         this.field_150609_ah = var1;
      }

      return this;
   }

   public int getSkyColorByTemp(float var1) {
      var1 /= 3.0F;
      var1 = MathHelper.clamp_float(var1, -1.0F, 1.0F);
      return MathHelper.hsvToRGB(0.62222224F - var1 * 0.05F, 0.5F + var1 * 0.1F, 1.0F);
   }

   public List<BiomeGenBase.SpawnListEntry> getSpawnableList(EnumCreatureType var1) {
      switch (var1) {
         case MONSTER:
            return this.spawnableMonsterList;
         case CREATURE:
            return this.spawnableCreatureList;
         case WATER_CREATURE:
            return this.spawnableWaterCreatureList;
         case AMBIENT:
            return this.spawnableCaveCreatureList;
         default:
            return Collections.emptyList();
      }
   }

   public boolean getEnableSnow() {
      return this.isSnowyBiome();
   }

   public boolean canRain() {
      return this.isSnowyBiome() ? false : this.enableRain;
   }

   public boolean isHighHumidity() {
      return this.rainfall > 0.85F;
   }

   public float getSpawningChance() {
      return 0.1F;
   }

   public final int getIntRainfall() {
      return (int)(this.rainfall * 65536.0F);
   }

   public final float getFloatRainfall() {
      return this.rainfall;
   }

   public final float getFloatTemperature(BlockPos var1) {
      if (var1.getY() > 64) {
         float var4 = (float)(temperatureNoise.func_151601_a(var1.getX() * 1.0 / 8.0, var1.getZ() * 1.0 / 8.0) * 4.0);
         return this.temperature - (var4 + var1.getY() - 64.0F) * 0.05F / 30.0F;
      } else {
         return this.temperature;
      }
   }

   public void decorate(World var1, Random var2, BlockPos var3) {
      this.theBiomeDecorator.decorate(var1, var2, this, var3);
   }

   public int getGrassColorAtPos(BlockPos var1) {
      double var2 = MathHelper.clamp_float(this.getFloatTemperature(var1), 0.0F, 1.0F);
      double var4 = MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
      return ColorizerGrass.getGrassColor(var2, var4);
   }

   public int getFoliageColorAtPos(BlockPos var1) {
      double var2 = MathHelper.clamp_float(this.getFloatTemperature(var1), 0.0F, 1.0F);
      double var4 = MathHelper.clamp_float(this.getFloatRainfall(), 0.0F, 1.0F);
      return ColorizerFoliage.getFoliageColor(var2, var4);
   }

   public boolean isSnowyBiome() {
      return this.enableSnow;
   }

   public void genTerrainBlocks(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
      this.generateBiomeTerrain(var1, var2, var3, var4, var5, var6);
   }

   public final void generateBiomeTerrain(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
      int var11 = var1.getSeaLevel();
      IBlockState var12 = this.topBlock;
      IBlockState var13 = this.fillerBlock;
      int var14 = -1;
      int var15 = (int)(var6 / 3.0 + 3.0 + var2.nextDouble() * 0.25);
      int var16 = var4 & 15;
      int var17 = var5 & 15;
      BlockPos.MutableBlockPos var18 = new BlockPos.MutableBlockPos();

      for (int var19 = 255; var19 >= 0; var19--) {
         if (var19 <= var2.nextInt(5)) {
            var3.setBlockState(var17, var19, var16, Blocks.bedrock.getDefaultState());
         } else {
            IBlockState var20 = var3.getBlockState(var17, var19, var16);
            if (var20.getBlock().getMaterial() == Material.air) {
               var14 = -1;
            } else if (var20.getBlock() == Blocks.stone) {
               if (var14 == -1) {
                  if (var15 <= 0) {
                     var12 = null;
                     var13 = Blocks.stone.getDefaultState();
                  } else if (var19 >= var11 - 4 && var19 <= var11 + 1) {
                     var12 = this.topBlock;
                     var13 = this.fillerBlock;
                  }

                  if (var19 < var11 && (var12 == null || var12.getBlock().getMaterial() == Material.air)) {
                     if (this.getFloatTemperature(var18.set(var4, var19, var5)) < 0.15F) {
                        var12 = Blocks.ice.getDefaultState();
                     } else {
                        var12 = Blocks.water.getDefaultState();
                     }
                  }

                  var14 = var15;
                  if (var19 >= var11 - 1) {
                     var3.setBlockState(var17, var19, var16, var12);
                  } else if (var19 < var11 - 7 - var15) {
                     var12 = null;
                     var13 = Blocks.stone.getDefaultState();
                     var3.setBlockState(var17, var19, var16, Blocks.gravel.getDefaultState());
                  } else {
                     var3.setBlockState(var17, var19, var16, var13);
                  }
               } else if (var14 > 0) {
                  var14--;
                  var3.setBlockState(var17, var19, var16, var13);
                  if (var14 == 0 && var13.getBlock() == Blocks.sand) {
                     var14 = var2.nextInt(4) + Math.max(0, var19 - 63);
                     var13 = var13.getValue(BlockSand.VARIANT) == BlockSand.EnumType.RED_SAND
                        ? Blocks.red_sandstone.getDefaultState()
                        : Blocks.sandstone.getDefaultState();
                  }
               }
            }
         }
      }
   }

   public BiomeGenBase createMutation() {
      return this.createMutatedBiome(this.biomeID + 128);
   }

   public BiomeGenBase createMutatedBiome(int var1) {
      return new BiomeGenMutated(var1, this);
   }

   public Class<? extends BiomeGenBase> getBiomeClass() {
      return (Class<? extends BiomeGenBase>)this.getClass();
   }

   public boolean isEqualTo(BiomeGenBase var1) {
      return var1 == this ? true : (var1 == null ? false : this.getBiomeClass() == var1.getBiomeClass());
   }

   public BiomeGenBase.TempCategory getTempCategory() {
      return this.temperature < 0.2
         ? BiomeGenBase.TempCategory.COLD
         : (this.temperature < 1.0 ? BiomeGenBase.TempCategory.MEDIUM : BiomeGenBase.TempCategory.WARM);
   }

   public static BiomeGenBase[] getBiomeGenArray() {
      return biomeList;
   }

   public static BiomeGenBase getBiome(int var0) {
      return getBiomeFromBiomeList(var0, (BiomeGenBase)null);
   }

   public static BiomeGenBase getBiomeFromBiomeList(int var0, BiomeGenBase var1) {
      if (var0 >= 0 && var0 <= biomeList.length) {
         BiomeGenBase var5 = biomeList[var0];
         return var5 == null ? var1 : var5;
      } else {
         logger.warn("Biome ID is out of bounds: " + var0 + ", defaulting to 0 (Ocean)");
         return ocean;
      }
   }

   static {
      long var20 = ab ^ 23982143559762L;
      long var10000 = 3474986416736007602L ^ var20;
      plains.createMutation();
      desert.createMutation();
      forest.createMutation();
      taiga.createMutation();
      swampland.createMutation();
      icePlains.createMutation();
      jungle.createMutation();
      jungleEdge.createMutation();
      coldTaiga.createMutation();
      savanna.createMutation();
      savannaPlateau.createMutation();
      mesa.createMutation();
      mesaPlateau_F.createMutation();
      mesaPlateau.createMutation();
      birchForest.createMutation();
      birchForestHills.createMutation();
      roofedForest.createMutation();
      megaTaiga.createMutation();
      extremeHills.createMutation();
      extremeHillsPlus.createMutation();
      megaTaiga.createMutatedBiome(megaTaigaHills.biomeID + 128).setBiomeName("Redwood Taiga Hills M");

      for (BiomeGenBase var25 : biomeList) {
         if (var25 != null) {
            if (BIOME_ID_MAP.containsKey(var25.biomeName)) {
               throw new Error("Biome \"" + var25.biomeName + "\" is defined as both ID " + BIOME_ID_MAP.get(var25.biomeName).biomeID + " and " + var25.biomeID);
            }

            BIOME_ID_MAP.put(var25.biomeName, var25);
            if (var25.biomeID < 128) {
               explorationBiomesList.add(var25);
            }
         }
      }

      explorationBiomesList.remove(hell);
      explorationBiomesList.remove(sky);
      explorationBiomesList.remove(frozenOcean);
      explorationBiomesList.remove(extremeHillsEdge);
      temperatureNoise = new NoiseGeneratorPerlin(new Random(1234L), 1);
      GRASS_COLOR_NOISE = new NoiseGeneratorPerlin(new Random(2345L), 1);
      DOUBLE_PLANT_GENERATOR = new WorldGenDoublePlant();
   }

   public static class Height {
      public float rootHeight;
      public float variation;

      public Height(float var1, float var2) {
         this.rootHeight = var1;
         this.variation = var2;
      }

      public BiomeGenBase.Height attenuate() {
         return new BiomeGenBase.Height(this.rootHeight * 0.8F, this.variation * 0.6F);
      }
   }

   public static class SpawnListEntry extends WeightedRandom.Item {
      public Class<? extends EntityLiving> entityClass;
      public int minGroupCount;
      public int maxGroupCount;
      private static final long a = 122887977560301L;

      public SpawnListEntry(Class<? extends EntityLiving> var1, int var2, int var3, int var4) {
         super(var2);
         this.entityClass = var1;
         this.minGroupCount = var3;
         this.maxGroupCount = var4;
      }

      @Override
      public String toString() {
         return this.entityClass.getSimpleName() + "*(" + this.minGroupCount + "-" + this.maxGroupCount + "):" + this.itemWeight;
      }
   }

   public enum TempCategory {
      OCEAN,
      COLD,
      MEDIUM,
      WARM;
   }
}
