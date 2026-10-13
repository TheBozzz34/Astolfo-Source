package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ChunkProviderFlat implements IChunkProvider {
   private World worldObj;
   private Random random;
   private final IBlockState[] cachedBlockIDs = new IBlockState[256];
   private final FlatGeneratorInfo flatWorldGenInfo;
   private final List<MapGenStructure> structureGenerators = Lists.newArrayList();
   private final boolean hasDecoration;
   private final boolean hasDungeons;
   private WorldGenLakes waterLakeGenerator;
   private WorldGenLakes lavaLakeGenerator;
   private static final long a = 41802532596386L;

   public ChunkProviderFlat(World var1, long var2, boolean var4, String var5) {
      this.worldObj = var1;
      this.random = new Random(var2);
      this.flatWorldGenInfo = FlatGeneratorInfo.createFlatGeneratorFromString(var5);
      if (var4) {
         Map var9 = this.flatWorldGenInfo.getWorldFeatures();
         if (var9.containsKey("village")) {
            Map var10 = (Map)var9.get("village");
            if (!var10.containsKey("size")) {
               var10.put("size", "1");
            }

            this.structureGenerators.add(new MapGenVillage(var10));
         }

         if (var9.containsKey("biome_1")) {
            this.structureGenerators.add(new MapGenScatteredFeature((Map<String, String>)var9.get("biome_1")));
         }

         if (var9.containsKey("mineshaft")) {
            this.structureGenerators.add(new MapGenMineshaft((Map<String, String>)var9.get("mineshaft")));
         }

         if (var9.containsKey("stronghold")) {
            this.structureGenerators.add(new MapGenStronghold((Map<String, String>)var9.get("stronghold")));
         }

         if (var9.containsKey("oceanmonument")) {
            this.structureGenerators.add(new StructureOceanMonument((Map<String, String>)var9.get("oceanmonument")));
         }
      }

      if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lake")) {
         this.waterLakeGenerator = new WorldGenLakes(Blocks.water);
      }

      if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lava_lake")) {
         this.lavaLakeGenerator = new WorldGenLakes(Blocks.lava);
      }

      this.hasDungeons = this.flatWorldGenInfo.getWorldFeatures().containsKey("dungeon");
      int var16 = 0;
      int var17 = 0;
      boolean var11 = true;

      for (FlatLayerInfo var13 : this.flatWorldGenInfo.getFlatLayers()) {
         for (int var14 = var13.getMinY(); var14 < var13.getMinY() + var13.getLayerCount(); var14++) {
            IBlockState var15 = var13.getLayerMaterial();
            if (var15.getBlock() != Blocks.air) {
               var11 = false;
               this.cachedBlockIDs[var14] = var15;
            }
         }

         if (var13.getLayerMaterial().getBlock() == Blocks.air) {
            var17 += var13.getLayerCount();
         } else {
            var16 += var13.getLayerCount() + var17;
            var17 = 0;
         }
      }

      var1.setSeaLevel(var16);
      this.hasDecoration = var11 ? false : this.flatWorldGenInfo.getWorldFeatures().containsKey("decoration");
   }

   @Override
   public Chunk provideChunk(int var1, int var2) {
      ChunkPrimer var6 = new ChunkPrimer();

      for (int var7 = 0; var7 < this.cachedBlockIDs.length; var7++) {
         IBlockState var8 = this.cachedBlockIDs[var7];
         if (var8 != null) {
            for (int var9 = 0; var9 < 16; var9++) {
               for (int var10 = 0; var10 < 16; var10++) {
                  var6.setBlockState(var9, var7, var10, var8);
               }
            }
         }
      }

      for (MapGenBase var13 : this.structureGenerators) {
         var13.generate(this, this.worldObj, var1, var2, var6);
      }

      Chunk var12 = new Chunk(this.worldObj, var6, var1, var2);
      BiomeGenBase[] var14 = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, var1 * 16, var2 * 16, 16, 16);
      byte[] var15 = var12.getBiomeArray();

      for (int var16 = 0; var16 < var15.length; var16++) {
         var15[var16] = (byte)var14[var16].biomeID;
      }

      var12.generateSkylightMap();
      return var12;
   }

   @Override
   public boolean chunkExists(int var1, int var2) {
      return true;
   }

   @Override
   public void populate(IChunkProvider var1, int var2, int var3) {
      int var7 = var2 * 16;
      int var8 = var3 * 16;
      BlockPos var9 = new BlockPos(var7, 0, var8);
      BiomeGenBase var10 = this.worldObj.getBiomeGenForCoords(new BlockPos(var7 + 16, 0, var8 + 16));
      boolean var11 = false;
      this.random.setSeed(this.worldObj.getSeed());
      long var12 = this.random.nextLong() / 2L * 2L + 1L;
      long var14 = this.random.nextLong() / 2L * 2L + 1L;
      this.random.setSeed(var2 * var12 + var3 * var14 ^ this.worldObj.getSeed());
      ChunkCoordIntPair var16 = new ChunkCoordIntPair(var2, var3);

      for (MapGenStructure var18 : this.structureGenerators) {
         boolean var19 = var18.generateStructure(this.worldObj, this.random, var16);
         if (var18 instanceof MapGenVillage) {
            var11 |= var19;
         }
      }

      if (this.waterLakeGenerator != null && !var11 && this.random.nextInt(4) == 0) {
         this.waterLakeGenerator
            .generate(this.worldObj, this.random, var9.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
      }

      if (this.lavaLakeGenerator != null && !var11 && this.random.nextInt(8) == 0) {
         BlockPos var20 = var9.add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(248) + 8), this.random.nextInt(16) + 8);
         if (var20.getY() < this.worldObj.getSeaLevel() || this.random.nextInt(10) == 0) {
            this.lavaLakeGenerator.generate(this.worldObj, this.random, var20);
         }
      }

      if (this.hasDungeons) {
         for (int var21 = 0; var21 < 8; var21++) {
            new WorldGenDungeons()
               .generate(this.worldObj, this.random, var9.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
         }
      }

      if (this.hasDecoration) {
         var10.decorate(this.worldObj, this.random, var9);
      }
   }

   @Override
   public boolean populateChunk(IChunkProvider var1, Chunk var2, int var3, int var4) {
      return false;
   }

   @Override
   public boolean saveChunks(boolean var1, IProgressUpdate var2) {
      return true;
   }

   @Override
   public void saveExtraData() {
   }

   @Override
   public boolean unloadQueuedChunks() {
      return false;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public String makeString() {
      return "FlatLevelSource";
   }

   @Override
   public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType var1, BlockPos var2) {
      BiomeGenBase var3 = this.worldObj.getBiomeGenForCoords(var2);
      return var3.getSpawnableList(var1);
   }

   @Override
   public BlockPos getStrongholdGen(World var1, String var2, BlockPos var3) {
      if ("Stronghold".equals(var2)) {
         for (MapGenStructure var8 : this.structureGenerators) {
            if (var8 instanceof MapGenStronghold) {
               return var8.getClosestStrongholdPos(var1, var3);
            }
         }
      }

      return null;
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public void recreateStructures(Chunk var1, int var2, int var3) {
      for (MapGenStructure var8 : this.structureGenerators) {
         var8.generate(this, this.worldObj, var2, var3, (ChunkPrimer)null);
      }
   }

   @Override
   public Chunk provideChunk(BlockPos var1) {
      return this.provideChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }
}
