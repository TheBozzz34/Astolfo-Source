package net.minecraft.block;

import net.minecraft.util.BlockPos;

public class BlockEventData {
   private BlockPos position;
   private Block blockType;
   private int eventID;
   private int eventParameter;

   public BlockEventData(BlockPos pos, Block blockType, int eventId, int i) {
      this.position = pos;
      this.eventID = eventId;
      this.eventParameter = i;
      this.blockType = blockType;
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
   public boolean equals(Object object) {
      if (!(object instanceof BlockEventData)) {
         return false;
      }

      BlockEventData blockEventData = (BlockEventData)object;
      return this.position.equals(blockEventData.position)
         && this.eventID == blockEventData.eventID
         && this.eventParameter == blockEventData.eventParameter
         && this.blockType == blockEventData.blockType;
   }

   @Override
   public String toString() {
      return "TE(" + this.position + ")," + this.eventID + "," + this.eventParameter + "," + this.blockType;
   }
}
