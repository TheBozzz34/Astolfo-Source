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

   public S26PacketMapChunkBulk(List<Chunk> chunks) {
      int i = chunks.size();
      this.xPositions = new int[i];
      this.zPositions = new int[i];
      this.chunksData = new S21PacketChunkData.Extracted[i];
      this.isOverworld = !((Chunk)chunks.get(0)).getWorld().provider.getHasNoSky();

      for (int j = 0; j < i; j++) {
         Chunk chunk = (Chunk)chunks.get(j);
         S21PacketChunkData.Extracted extracted = S21PacketChunkData.getExtractedData(chunk, true, this.isOverworld, 65535);
         this.xPositions[j] = chunk.xPosition;
         this.zPositions[j] = chunk.zPosition;
         this.chunksData[j] = extracted;
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.isOverworld = buf.readBoolean();
      int i = buf.readVarIntFromBuffer();
      this.xPositions = new int[i];
      this.zPositions = new int[i];
      this.chunksData = new S21PacketChunkData.Extracted[i];

      for (int j = 0; j < i; j++) {
         this.xPositions[j] = buf.readInt();
         this.zPositions[j] = buf.readInt();
         this.chunksData[j] = new S21PacketChunkData.Extracted();
         this.chunksData[j].dataSize = buf.readShort() & '\uffff';
         this.chunksData[j].data = new byte[S21PacketChunkData.func_180737_a(Integer.bitCount(this.chunksData[j].dataSize), this.isOverworld, true)];
      }

      for (int k = 0; k < i; k++) {
         buf.readBytes(this.chunksData[k].data);
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBoolean(this.isOverworld);
      buf.writeVarIntToBuffer(this.chunksData.length);

      for (int i = 0; i < this.xPositions.length; i++) {
         buf.writeInt(this.xPositions[i]);
         buf.writeInt(this.zPositions[i]);
         buf.writeShort((short)(this.chunksData[i].dataSize & 65535));
      }

      for (int j = 0; j < this.xPositions.length; j++) {
         buf.writeBytes(this.chunksData[j].data);
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleMapChunkBulk(this);
   }

   public int getChunkX(int i) {
      return this.xPositions[i];
   }

   public int getChunkZ(int i) {
      return this.zPositions[i];
   }

   public int getChunkCount() {
      return this.xPositions.length;
   }

   public byte[] getChunkBytes(int i) {
      return this.chunksData[i].data;
   }

   public int getChunkSize(int i) {
      return this.chunksData[i].dataSize;
   }
}
