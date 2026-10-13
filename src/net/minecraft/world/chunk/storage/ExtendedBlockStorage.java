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
   private static final long a = 102914880937644L;

   public ExtendedBlockStorage(int var1, boolean var2) {
      this.yBase = var1;
      this.data = new char[4096];
      this.blocklightArray = new NibbleArray();
      if (var2) {
         this.skylightArray = new NibbleArray();
      }
   }

   public IBlockState get(int var1, int var2, int var3) {
      IBlockState var7 = Block.BLOCK_STATE_IDS.getByValue(this.data[var2 << 8 | var3 << 4 | var1]);
      return var7 != null ? var7 : Blocks.air.getDefaultState();
   }

   public void set(int var1, int var2, int var3, IBlockState var4) {
      if (Reflector.IExtendedBlockState.isInstance(var4)) {
         var4 = (IBlockState)Reflector.call(var4, Reflector.IExtendedBlockState_getClean);
      }

      IBlockState var8 = this.get(var1, var2, var3);
      Block var9 = var8.getBlock();
      Block var10 = var4.getBlock();
      if (var9 != Blocks.air) {
         this.blockRefCount--;
         if (var9.getTickRandomly()) {
            this.tickRefCount--;
         }
      }

      if (var10 != Blocks.air) {
         this.blockRefCount++;
         if (var10.getTickRandomly()) {
            this.tickRefCount++;
         }
      }

      this.data[var2 << 8 | var3 << 4 | var1] = (char)Block.BLOCK_STATE_IDS.get(var4);
   }

   public Block getBlockByExtId(int var1, int var2, int var3) {
      return this.get(var1, var2, var3).getBlock();
   }

   public int getExtBlockMetadata(int var1, int var2, int var3) {
      IBlockState var4 = this.get(var1, var2, var3);
      return var4.getBlock().getMetaFromState(var4);
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

   public void setExtSkylightValue(int var1, int var2, int var3, int var4) {
      this.skylightArray.set(var1, var2, var3, var4);
   }

   public int getExtSkylightValue(int var1, int var2, int var3) {
      return this.skylightArray.get(var1, var2, var3);
   }

   public void setExtBlocklightValue(int var1, int var2, int var3, int var4) {
      this.blocklightArray.set(var1, var2, var3, var4);
   }

   public int getExtBlocklightValue(int var1, int var2, int var3) {
      return this.blocklightArray.get(var1, var2, var3);
   }

   public void removeInvalidBlocks() {
      List var4 = Block.BLOCK_STATE_IDS.getObjectList();
      int var5 = var4.size();
      int var6 = 0;
      int var7 = 0;

      for (int var8 = 0; var8 < 16; var8++) {
         int var9 = var8 << 8;

         for (int var10 = 0; var10 < 16; var10++) {
            int var11 = var9 | var10 << 4;

            for (int var12 = 0; var12 < 16; var12++) {
               char var13 = this.data[var11 | var12];
               if (var13 > 0) {
                  var6++;
                  if (var13 < var5) {
                     IBlockState var14 = (IBlockState)var4.get(var13);
                     if (var14 != null) {
                        Block var15 = var14.getBlock();
                        if (var15.getTickRandomly()) {
                           var7++;
                        }
                     }
                  }
               }
            }
         }
      }

      this.blockRefCount = var6;
      this.tickRefCount = var7;
   }

   public char[] getData() {
      return this.data;
   }

   public void setData(char[] var1) {
      this.data = var1;
   }

   public NibbleArray getBlocklightArray() {
      return this.blocklightArray;
   }

   public NibbleArray getSkylightArray() {
      return this.skylightArray;
   }

   public void setBlocklightArray(NibbleArray var1) {
      this.blocklightArray = var1;
   }

   public void setSkylightArray(NibbleArray var1) {
      this.skylightArray = var1;
   }
}
