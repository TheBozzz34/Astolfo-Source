package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EmptyChunk extends Chunk {
   private static final long d = 25027088410370L;

   public EmptyChunk(World var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean isAtLocation(int var1, int var2) {
      return var1 == this.xPosition && var2 == this.zPosition;
   }

   @Override
   public int getHeightValue(int var1, int var2) {
      return 0;
   }

   @Override
   public void generateHeightMap() {
   }

   @Override
   public void generateSkylightMap() {
   }

   @Override
   public Block getBlock(BlockPos var1) {
      return Blocks.air;
   }

   @Override
   public int getBlockLightOpacity(BlockPos var1) {
      return 255;
   }

   @Override
   public int getBlockMetadata(BlockPos var1) {
      return 0;
   }

   @Override
   public int getLightFor(EnumSkyBlock var1, BlockPos var2) {
      return var1.defaultLightValue;
   }

   @Override
   public void setLightFor(EnumSkyBlock var1, BlockPos var2, int var3) {
   }

   @Override
   public int getLightSubtracted(BlockPos var1, int var2) {
      return 0;
   }

   @Override
   public void addEntity(Entity var1) {
   }

   @Override
   public void removeEntity(Entity var1) {
   }

   @Override
   public void removeEntityAtIndex(Entity var1, int var2) {
   }

   @Override
   public boolean canSeeSky(BlockPos var1) {
      return false;
   }

   @Override
   public TileEntity getTileEntity(BlockPos var1, Chunk.EnumCreateEntityType var2) {
      return null;
   }

   @Override
   public void addTileEntity(TileEntity var1) {
   }

   @Override
   public void addTileEntity(BlockPos var1, TileEntity var2) {
   }

   @Override
   public void removeTileEntity(BlockPos var1) {
   }

   @Override
   public void onChunkLoad() {
   }

   @Override
   public void onChunkUnload() {
   }

   @Override
   public void setChunkModified() {
   }

   @Override
   public void getEntitiesWithinAABBForEntity(Entity var1, AxisAlignedBB var2, List<Entity> var3, Predicate<? super Entity> var4) {
   }

   @Override
   public <T extends Entity> void getEntitiesOfTypeWithinAAAB(Class<? extends T> var1, AxisAlignedBB var2, List<T> var3, Predicate<? super T> var4) {
   }

   @Override
   public boolean needsSaving(boolean var1) {
      return false;
   }

   @Override
   public Random getRandomWithSeed(long var1) {
      return new Random(
         this.getWorld().getSeed()
               + this.xPosition * this.xPosition * 4987142
               + this.xPosition * 5947611
               + this.zPosition * this.zPosition * 4392871L
               + this.zPosition * 389711
            ^ var1
      );
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public boolean getAreLevelsEmpty(int var1, int var2) {
      return true;
   }
}
