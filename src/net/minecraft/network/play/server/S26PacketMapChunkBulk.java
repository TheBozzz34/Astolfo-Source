package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;

public class S26PacketMapChunkBulk implements Packet<INetHandlerPlayClient> {
   private int[] xPositions;
   private int[] zPositions;
   private S21PacketChunkData.Extracted[] chunksData;
   private boolean isOverworld;
   private static final long a = 18237756360760L;

   public S26PacketMapChunkBulk() {
   }

   public S26PacketMapChunkBulk(List<Chunk> var1) {
      int var5 = var1.size();
      this.xPositions = new int[var5];
      this.zPositions = new int[var5];
      this.chunksData = new S21PacketChunkData.Extracted[var5];
      this.isOverworld = !((Chunk)var1.get(0)).getWorld().provider.getHasNoSky();

      for (int var6 = 0; var6 < var5; var6++) {
         Chunk var7 = (Chunk)var1.get(var6);
         S21PacketChunkData.Extracted var8 = S21PacketChunkData.getExtractedData(var7, true, this.isOverworld, 65535);
         this.xPositions[var6] = var7.xPosition;
         this.zPositions[var6] = var7.zPosition;
         this.chunksData[var6] = var8;
      }
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.isOverworld = var1.readBoolean();
      int var5 = var1.readVarIntFromBuffer();
      this.xPositions = new int[var5];
      this.zPositions = new int[var5];
      this.chunksData = new S21PacketChunkData.Extracted[var5];

      for (int var6 = 0; var6 < var5; var6++) {
         this.xPositions[var6] = var1.readInt();
         this.zPositions[var6] = var1.readInt();
         this.chunksData[var6] = new S21PacketChunkData.Extracted();
         this.chunksData[var6].dataSize = var1.readShort() & '\uffff';
         this.chunksData[var6].data = new byte[S21PacketChunkData.func_180737_a(Integer.bitCount(this.chunksData[var6].dataSize), this.isOverworld, true)];
      }

      for (int var7 = 0; var7 < var5; var7++) {
         var1.readBytes(this.chunksData[var7].data);
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeBoolean(this.isOverworld);
      var1.writeVarIntToBuffer(this.chunksData.length);

      for (int var5 = 0; var5 < this.xPositions.length; var5++) {
         var1.writeInt(this.xPositions[var5]);
         var1.writeInt(this.zPositions[var5]);
         var1.writeShort((short)(this.chunksData[var5].dataSize & 65535));
      }

      for (int var6 = 0; var6 < this.xPositions.length; var6++) {
         var1.writeBytes(this.chunksData[var6].data);
      }
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleMapChunkBulk(this);
   }

   public int getChunkX(int var1) {
      return this.xPositions[var1];
   }

   public int getChunkZ(int var1) {
      return this.zPositions[var1];
   }

   public int getChunkCount() {
      return this.xPositions.length;
   }

   public byte[] getChunkBytes(int var1) {
      return this.chunksData[var1].data;
   }

   public int getChunkSize(int var1) {
      return this.chunksData[var1].dataSize;
   }
}
