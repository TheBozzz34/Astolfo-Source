package net.minecraft.block;

import net.minecraft.util.BlockPos;

public class BlockEventData {
   private BlockPos position;
   private Block blockType;
   private int eventID;
   private int eventParameter;
   private static final long a = 67859383971692L;

   public BlockEventData(BlockPos var1, Block var2, int var3, int var4) {
      this.position = var1;
      this.eventID = var3;
      this.eventParameter = var4;
      this.blockType = var2;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public int getEventID() {
      return this.eventID;
   }

   public int getEventParameter() {
      return this.eventParameter;
   }

   public Block getBlock() {
      return this.blockType;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof BlockEventData)) {
         return false;
      }

      BlockEventData var5 = (BlockEventData)var1;
      return this.position.equals(var5.position)
         && this.eventID == var5.eventID
         && this.eventParameter == var5.eventParameter
         && this.blockType == var5.blockType;
   }

   @Override
   public String toString() {
      return "TE(" + this.position + ")," + this.eventID + "," + this.eventParameter + "," + this.blockType;
   }
}
