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

   public ChunkProviderDebug(World var1) {
      this.world = var1;
   }

   @Override
   public Chunk provideChunk(int var1, int var2) {
      ChunkPrimer var6 = new ChunkPrimer();

      for (int var7 = 0; var7 < 16; var7++) {
         for (int var8 = 0; var8 < 16; var8++) {
            int var9 = var1 * 16 + var7;
            int var10 = var2 * 16 + var8;
            var6.setBlockState(var7, 60, var8, Blocks.barrier.getDefaultState());
            IBlockState var11 = func_177461_b(var9, var10);
            if (var11 != null) {
               var6.setBlockState(var7, 70, var8, var11);
            }
         }
      }

      Chunk var12 = new Chunk(this.world, var6, var1, var2);
      var12.generateSkylightMap();
      BiomeGenBase[] var13 = this.world.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, var1 * 16, var2 * 16, 16, 16);
      byte[] var14 = var12.getBiomeArray();

      for (int var15 = 0; var15 < var14.length; var15++) {
         var14[var15] = (byte)var13[var15].biomeID;
      }

      var12.generateSkylightMap();
      return var12;
   }

   public static IBlockState func_177461_b(int var0, int var1) {
      IBlockState var5 = null;
      if (var0 > 0 && var1 > 0 && var0 % 2 != 0 && var1 % 2 != 0) {
         var0 /= 2;
         var1 /= 2;
         if (var0 <= field_177462_b && var1 <= field_181039_c) {
            int var6 = MathHelper.abs_int(var0 * field_177462_b + var1);
            if (var6 < field_177464_a.size()) {
               var5 = field_177464_a.get(var6);
            }
         }
      }

      return var5;
   }

   @Override
   public boolean chunkExists(int var1, int var2) {
      return true;
   }

   @Override
   public void populate(IChunkProvider var1, int var2, int var3) {
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
      return "DebugLevelSource";
   }

   @Override
   public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType var1, BlockPos var2) {
      BiomeGenBase var3 = this.world.getBiomeGenForCoords(var2);
      return var3.getSpawnableList(var1);
   }

   @Override
   public BlockPos getStrongholdGen(World var1, String var2, BlockPos var3) {
      return null;
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public void recreateStructures(Chunk var1, int var2, int var3) {
   }

   @Override
   public Chunk provideChunk(BlockPos var1) {
      return this.provideChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }

   static {
      for (Block var17 : Block.blockRegistry) {
         field_177464_a.addAll(var17.getBlockState().getValidStates());
      }

      field_177462_b = MathHelper.ceiling_float_int(MathHelper.sqrt_float(field_177464_a.size()));
      field_181039_c = MathHelper.ceiling_float_int((float)field_177464_a.size() / field_177462_b);
   }
}
