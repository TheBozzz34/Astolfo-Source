package shadersmod.client;

import net.minecraft.src.MatchBlock;

public class BlockAlias {
   private int blockId;
   private MatchBlock[] matchBlocks;
   private static final long a = 38170275249193L;

   public BlockAlias(int blockId, MatchBlock[] matchBlocks) {
      this.blockId = blockId;
      this.matchBlocks = matchBlocks;
   }

   public int getBlockId() {
      return this.blockId;
   }

   public boolean matches(int id, int metadata) {
      for (int i = 0; i < this.matchBlocks.length; i++) {
         MatchBlock matchBlock = this.matchBlocks[i];
         if (matchBlock.matches(id, metadata)) {
            return true;
         }
      }

      return false;
   }

   public int[] getMatchBlockIds() {
      int[] ints = new int[this.matchBlocks.length];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = this.matchBlocks[i].getBlockId();
      }

      return ints;
   }
}
