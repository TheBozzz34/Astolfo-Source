package net.minecraft.world.chunk.storage;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.src.Reflector;
import net.minecraft.world.chunk.NibbleArray;

public class ExtendedBlockStorage {
   private int yBase;
   private int blockRefCount;
   private int tickRefCount;
   private char[] data;
   private NibbleArray blocklightArray;
   private NibbleArray skylightArray;

   public ExtendedBlockStorage(int y, boolean storeSkylight) {
      this.yBase = y;
      this.data = new char[4096];
      this.blocklightArray = new NibbleArray();
      if (storeSkylight) {
         this.skylightArray = new NibbleArray();
      }
   }

   public IBlockState get(int x, int y, int z) {
      IBlockState iBlockState = Block.BLOCK_STATE_IDS.getByValue(this.data[y << 8 | z << 4 | x]);
      return iBlockState != null ? iBlockState : Blocks.air.getDefaultState();
   }

   public void set(int x, int y, int z, IBlockState state) {
      if (Reflector.IExtendedBlockState.isInstance(state)) {
         state = (IBlockState)Reflector.call(state, Reflector.IExtendedBlockState_getClean);
      }

      IBlockState iBlockState = this.get(x, y, z);
      Block block = iBlockState.getBlock();
      Block block2 = state.getBlock();
      if (block != Blocks.air) {
         this.blockRefCount--;
         if (block.getTickRandomly()) {
            this.tickRefCount--;
         }
      }

      if (block2 != Blocks.air) {
         this.blockRefCount++;
         if (block2.getTickRandomly()) {
            this.tickRefCount++;
         }
      }

      this.data[y << 8 | z << 4 | x] = (char)Block.BLOCK_STATE_IDS.get(state);
   }

   public Block getBlockByExtId(int x, int y, int z) {
      return this.get(x, y, z).getBlock();
   }

   public int getExtBlockMetadata(int x, int y, int z) {
      IBlockState iBlockState = this.get(x, y, z);
      return iBlockState.getBlock().getMetaFromState(iBlockState);
   }

   public boolean isEmpty() {
      return this.blockRefCount == 0;
   }

   public boolean getNeedsRandomTick() {
      return this.tickRefCount > 0;
   }

   public int getYLocation() {
      return this.yBase;
   }

   public void setExtSkylightValue(int x, int y, int z, int value) {
      this.skylightArray.set(x, y, z, value);
   }

   public int getExtSkylightValue(int x, int y, int z) {
      return this.skylightArray.get(x, y, z);
   }

   public void setExtBlocklightValue(int x, int y, int z, int value) {
      this.blocklightArray.set(x, y, z, value);
   }

   public int getExtBlocklightValue(int x, int y, int z) {
      return this.blocklightArray.get(x, y, z);
   }

   public void removeInvalidBlocks() {
      List list = Block.BLOCK_STATE_IDS.getObjectList();
      int i = list.size();
      int j = 0;
      int k = 0;

      for (int l = 0; l < 16; l++) {
         int m = l << 8;

         for (int n = 0; n < 16; n++) {
            int i2 = m | n << 4;

            for (int i3 = 0; i3 < 16; i3++) {
               char c = this.data[i2 | i3];
               if (c > 0) {
                  j++;
                  if (c < i) {
                     IBlockState iBlockState = (IBlockState)list.get(c);
                     if (iBlockState != null) {
                        Block block = iBlockState.getBlock();
                        if (block.getTickRandomly()) {
                           k++;
                        }
                     }
                  }
               }
            }
         }
      }

      this.blockRefCount = j;
      this.tickRefCount = k;
   }

   public char[] getData() {
      return this.data;
   }

   public void setData(char[] dataArray) {
      this.data = dataArray;
   }

   public NibbleArray getBlocklightArray() {
      return this.blocklightArray;
   }

   public NibbleArray getSkylightArray() {
      return this.skylightArray;
   }

   public void setBlocklightArray(NibbleArray newBlocklightArray) {
      this.blocklightArray = newBlocklightArray;
   }

   public void setSkylightArray(NibbleArray newSkylightArray) {
      this.skylightArray = newSkylightArray;
   }
}
