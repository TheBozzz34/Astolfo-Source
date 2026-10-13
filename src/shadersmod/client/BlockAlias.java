package shadersmod.client;

import net.minecraft.src.MatchBlock;

public class BlockAlias {
   private int blockId;
   private MatchBlock[] matchBlocks;
   private static final long a = 38170275249193L;

   public BlockAlias(int var1, MatchBlock[] var2) {
      this.blockId = var1;
      this.matchBlocks = var2;
   }

   public int getBlockId() {
      return this.blockId;
   }

   public boolean matches(int var1, int var2) {
      for (int var6 = 0; var6 < this.matchBlocks.length; var6++) {
         MatchBlock var7 = this.matchBlocks[var6];
         if (var7.matches(var1, var2)) {
            return true;
         }
      }

      return false;
   }

   public int[] getMatchBlockIds() {
      int[] var4 = new int[this.matchBlocks.length];

      for (int var5 = 0; var5 < var4.length; var5++) {
         var4[var5] = this.matchBlocks[var5].getBlockId();
      }

      return var4;
   }
}
