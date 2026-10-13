package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;

public class S22PacketMultiBlockChange implements Packet<INetHandlerPlayClient> {
   private ChunkCoordIntPair chunkPosCoord;
   private S22PacketMultiBlockChange.BlockUpdateData[] changedBlocks;
   private static final long a = 16376435705094L;

   public S22PacketMultiBlockChange() {
   }

   public S22PacketMultiBlockChange(int var1, short[] var2, Chunk var3) {
      this.chunkPosCoord = new ChunkCoordIntPair(var3.xPosition, var3.zPosition);
      this.changedBlocks = new S22PacketMultiBlockChange.BlockUpdateData[var1];

      for (int var7 = 0; var7 < this.changedBlocks.length; var7++) {
         this.changedBlocks[var7] = new S22PacketMultiBlockChange.BlockUpdateData(var2[var7], var3);
      }
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.chunkPosCoord = new ChunkCoordIntPair(var1.readInt(), var1.readInt());
      this.changedBlocks = new S22PacketMultiBlockChange.BlockUpdateData[var1.readVarIntFromBuffer()];

      for (int var5 = 0; var5 < this.changedBlocks.length; var5++) {
         this.changedBlocks[var5] = new S22PacketMultiBlockChange.BlockUpdateData(
            var1.readShort(), Block.BLOCK_STATE_IDS.getByValue(var1.readVarIntFromBuffer())
         );
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeInt(this.chunkPosCoord.chunkXPos);
      var1.writeInt(this.chunkPosCoord.chunkZPos);
      var1.writeVarIntToBuffer(this.changedBlocks.length);

      for (S22PacketMultiBlockChange.BlockUpdateData var8 : this.changedBlocks) {
         var1.writeShort(var8.func_180089_b());
         var1.writeVarIntToBuffer(Block.BLOCK_STATE_IDS.get(var8.getBlockState()));
      }
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleMultiBlockChange(this);
   }

   public S22PacketMultiBlockChange.BlockUpdateData[] getChangedBlocks() {
      return this.changedBlocks;
   }

   public class BlockUpdateData {
      private final short chunkPosCrammed;
      private final IBlockState blockState;
      private static final long a = 83522924034995L;

      public BlockUpdateData(short var2, IBlockState var3) {
         this.chunkPosCrammed = var2;
         this.blockState = var3;
      }

      public BlockUpdateData(short var2, Chunk var3) {
         this.chunkPosCrammed = var2;
         this.blockState = var3.getBlockState(this.getPos());
      }

      public BlockPos getPos() {
         return new BlockPos(
            S22PacketMultiBlockChange.this.chunkPosCoord.getBlock(this.chunkPosCrammed >> 12 & 15, this.chunkPosCrammed & 255, this.chunkPosCrammed >> 8 & 15)
         );
      }

      public short func_180089_b() {
         return this.chunkPosCrammed;
      }

      public IBlockState getBlockState() {
         return this.blockState;
      }
   }
}
