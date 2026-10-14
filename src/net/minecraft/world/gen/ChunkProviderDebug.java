package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderDebug implements IChunkProvider {
   private static final List<IBlockState> field_177464_a = Lists.newArrayList();
   private static final int field_177462_b;
   private static final int field_181039_c;
   private final World world;
   private static final long a = 82529366371374L;

   public ChunkProviderDebug(World worldIn) {
      this.world = worldIn;
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      ChunkPrimer chunkPrimer = new ChunkPrimer();

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            int k = x * 16 + i;
            int l = z * 16 + j;
            chunkPrimer.setBlockState(i, 60, j, Blocks.barrier.getDefaultState());
            IBlockState iBlockState = func_177461_b(k, l);
            if (iBlockState != null) {
               chunkPrimer.setBlockState(i, 70, j, iBlockState);
            }
         }
      }

      Chunk chunk = new Chunk(this.world, chunkPrimer, x, z);
      chunk.generateSkylightMap();
      BiomeGenBase[] biomeGenBases = this.world.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
      byte[] bytes = chunk.getBiomeArray();

      for (int m = 0; m < bytes.length; m++) {
         bytes[m] = (byte)biomeGenBases[m].biomeID;
      }

      chunk.generateSkylightMap();
      return chunk;
   }

   public static IBlockState func_177461_b(int i, int j) {
      IBlockState iBlockState = null;
      if (i > 0 && j > 0 && i % 2 != 0 && j % 2 != 0) {
         i /= 2;
         j /= 2;
         if (i <= field_177462_b && j <= field_181039_c) {
            int k = MathHelper.abs_int(i * field_177462_b + j);
            if (k < field_177464_a.size()) {
               iBlockState = field_177464_a.get(k);
            }
         }
      }

      return iBlockState;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
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
      return "DebugLevelSource";
   }

   @Override
   public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      BiomeGenBase biomeGenBase = this.world.getBiomeGenForCoords(pos);
      return biomeGenBase.getSpawnableList(creatureType);
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return null;
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }

   static {
      for (Block block : Block.blockRegistry) {
         field_177464_a.addAll(block.getBlockState().getValidStates());
      }

      field_177462_b = MathHelper.ceiling_float_int(MathHelper.sqrt_float(field_177464_a.size()));
      field_181039_c = MathHelper.ceiling_float_int((float)field_177464_a.size() / field_177462_b);
   }
}
