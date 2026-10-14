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

   public ChunkProviderFlat(World worldIn, long seed, boolean generateStructures, String flatGeneratorSettings) {
      this.worldObj = worldIn;
      this.random = new Random(seed);
      this.flatWorldGenInfo = FlatGeneratorInfo.createFlatGeneratorFromString(flatGeneratorSettings);
      if (generateStructures) {
         Map map = this.flatWorldGenInfo.getWorldFeatures();
         if (map.containsKey("village")) {
            Map map2 = (Map)map.get("village");
            if (!map2.containsKey("size")) {
               map2.put("size", "1");
            }

            this.structureGenerators.add(new MapGenVillage(map2));
         }

         if (map.containsKey("biome_1")) {
            this.structureGenerators.add(new MapGenScatteredFeature((Map<String, String>)map.get("biome_1")));
         }

         if (map.containsKey("mineshaft")) {
            this.structureGenerators.add(new MapGenMineshaft((Map<String, String>)map.get("mineshaft")));
         }

         if (map.containsKey("stronghold")) {
            this.structureGenerators.add(new MapGenStronghold((Map<String, String>)map.get("stronghold")));
         }

         if (map.containsKey("oceanmonument")) {
            this.structureGenerators.add(new StructureOceanMonument((Map<String, String>)map.get("oceanmonument")));
         }
      }

      if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lake")) {
         this.waterLakeGenerator = new WorldGenLakes(Blocks.water);
      }

      if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lava_lake")) {
         this.lavaLakeGenerator = new WorldGenLakes(Blocks.lava);
      }

      this.hasDungeons = this.flatWorldGenInfo.getWorldFeatures().containsKey("dungeon");
      int i = 0;
      int j = 0;
      boolean bl = true;

      for (FlatLayerInfo flatLayerInfo : this.flatWorldGenInfo.getFlatLayers()) {
         for (int k = flatLayerInfo.getMinY(); k < flatLayerInfo.getMinY() + flatLayerInfo.getLayerCount(); k++) {
            IBlockState iBlockState = flatLayerInfo.getLayerMaterial();
            if (iBlockState.getBlock() != Blocks.air) {
               bl = false;
               this.cachedBlockIDs[k] = iBlockState;
            }
         }

         if (flatLayerInfo.getLayerMaterial().getBlock() == Blocks.air) {
            j += flatLayerInfo.getLayerCount();
         } else {
            i += flatLayerInfo.getLayerCount() + j;
            j = 0;
         }
      }

      worldIn.setSeaLevel(i);
      this.hasDecoration = bl ? false : this.flatWorldGenInfo.getWorldFeatures().containsKey("decoration");
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      ChunkPrimer chunkPrimer = new ChunkPrimer();

      for (int i = 0; i < this.cachedBlockIDs.length; i++) {
         IBlockState iBlockState = this.cachedBlockIDs[i];
         if (iBlockState != null) {
            for (int j = 0; j < 16; j++) {
               for (int k = 0; k < 16; k++) {
                  chunkPrimer.setBlockState(j, i, k, iBlockState);
               }
            }
         }
      }

      for (MapGenBase mapGenBase : this.structureGenerators) {
         mapGenBase.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      Chunk chunk = new Chunk(this.worldObj, chunkPrimer, x, z);
      BiomeGenBase[] biomeGenBases = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
      byte[] bytes = chunk.getBiomeArray();

      for (int l = 0; l < bytes.length; l++) {
         bytes[l] = (byte)biomeGenBases[l].biomeID;
      }

      chunk.generateSkylightMap();
      return chunk;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      int i = x * 16;
      int j = z * 16;
      BlockPos blockPos = new BlockPos(i, 0, j);
      BiomeGenBase biomeGenBase = this.worldObj.getBiomeGenForCoords(new BlockPos(i + 16, 0, j + 16));
      boolean bl = false;
      this.random.setSeed(this.worldObj.getSeed());
      long l = this.random.nextLong() / 2L * 2L + 1L;
      long m = this.random.nextLong() / 2L * 2L + 1L;
      this.random.setSeed(x * l + z * m ^ this.worldObj.getSeed());
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(x, z);

      for (MapGenStructure mapGenStructure : this.structureGenerators) {
         boolean bl2 = mapGenStructure.generateStructure(this.worldObj, this.random, chunkCoordIntPair);
         if (mapGenStructure instanceof MapGenVillage) {
            bl |= bl2;
         }
      }

      if (this.waterLakeGenerator != null && !bl && this.random.nextInt(4) == 0) {
         this.waterLakeGenerator
            .generate(this.worldObj, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
      }

      if (this.lavaLakeGenerator != null && !bl && this.random.nextInt(8) == 0) {
         BlockPos blockPos2 = blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(248) + 8), this.random.nextInt(16) + 8);
         if (blockPos2.getY() < this.worldObj.getSeaLevel() || this.random.nextInt(10) == 0) {
            this.lavaLakeGenerator.generate(this.worldObj, this.random, blockPos2);
         }
      }

      if (this.hasDungeons) {
         for (int k = 0; k < 8; k++) {
            new WorldGenDungeons()
               .generate(this.worldObj, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
         }
      }

      if (this.hasDecoration) {
         biomeGenBase.decorate(this.worldObj, this.random, blockPos);
      }
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      return false;
   }

   @Override
   public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback) {
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
   public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      BiomeGenBase biomeGenBase = this.worldObj.getBiomeGenForCoords(pos);
      return biomeGenBase.getSpawnableList(creatureType);
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      if ("Stronghold".equals(structureName)) {
         for (MapGenStructure mapGenStructure : this.structureGenerators) {
            if (mapGenStructure instanceof MapGenStronghold) {
               return mapGenStructure.getClosestStrongholdPos(worldIn, position);
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
   public void recreateStructures(Chunk chunkIn, int x, int z) {
      for (MapGenStructure mapGenStructure : this.structureGenerators) {
         mapGenStructure.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }
}
