package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class S21PacketChunkData implements Packet<INetHandlerPlayClient> {
   private int chunkX;
   private int chunkZ;
   private S21PacketChunkData.Extracted extractedData;
   private boolean field_149279_g;

   public S21PacketChunkData() {
   }

   public S21PacketChunkData(Chunk chunkIn, boolean bl, int i) {
      this.chunkX = chunkIn.xPosition;
      this.chunkZ = chunkIn.zPosition;
      this.field_149279_g = bl;
      this.extractedData = getExtractedData(chunkIn, bl, !chunkIn.getWorld().provider.getHasNoSky(), i);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.chunkX = buf.readInt();
      this.chunkZ = buf.readInt();
      this.field_149279_g = buf.readBoolean();
      this.extractedData = new S21PacketChunkData.Extracted();
      this.extractedData.dataSize = buf.readShort();
      this.extractedData.data = buf.readByteArray();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.chunkX);
      buf.writeInt(this.chunkZ);
      buf.writeBoolean(this.field_149279_g);
      buf.writeShort((short)(this.extractedData.dataSize & 65535));
      buf.writeByteArray(this.extractedData.data);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleChunkData(this);
   }

   public byte[] getExtractedDataBytes() {
      return this.extractedData.data;
   }

   public static int func_180737_a(int i, boolean bl, boolean bl2) {
      int j = i * 2 * 16 * 16 * 16;
      int k = i * 16 * 16 * 16 / 2;
      int l = bl ? i * 16 * 16 * 16 / 2 : 0;
      int m = bl2 ? 256 : 0;
      return j + k + l + m;
   }

   public static S21PacketChunkData.Extracted getExtractedData(Chunk chunk, boolean bl, boolean bl2, int i) {
      ExtendedBlockStorage[] extendedBlockStorages = chunk.getBlockStorageArray();
      S21PacketChunkData.Extracted extracted = new S21PacketChunkData.Extracted();
      ArrayList arrayList = Lists.newArrayList();

      for (int j = 0; j < extendedBlockStorages.length; j++) {
         ExtendedBlockStorage extendedBlockStorage = extendedBlockStorages[j];
         if (extendedBlockStorage != null && (!bl || !extendedBlockStorage.isEmpty()) && (i & 1 << j) != 0) {
            extracted.dataSize |= 1 << j;
            arrayList.add(extendedBlockStorage);
         }
      }

      extracted.data = new byte[func_180737_a(Integer.bitCount(extracted.dataSize), bl2, bl)];
      int k = 0;

      for (Object object : arrayList) {
         ExtendedBlockStorage extendedBlockStorage2 = (ExtendedBlockStorage)object;
         char[] chars = extendedBlockStorage2.getData();

         for (char c : chars) {
            extracted.data[k++] = (byte)(c & 0xFF);
            extracted.data[k++] = (byte)(c >> '\b' & 0xFF);
         }
      }

      for (Object object2 : arrayList) {
         ExtendedBlockStorage extendedBlockStorage3 = (ExtendedBlockStorage)object2;
         k = func_179757_a(extendedBlockStorage3.getBlocklightArray().getData(), extracted.data, k);
      }

      if (bl2) {
         for (Object object3 : arrayList) {
            ExtendedBlockStorage extendedBlockStorage4 = (ExtendedBlockStorage)object3;
            k = func_179757_a(extendedBlockStorage4.getSkylightArray().getData(), extracted.data, k);
         }
      }

      if (bl) {
         func_179757_a(chunk.getBiomeArray(), extracted.data, k);
      }

      return extracted;
   }

   private static int func_179757_a(byte[] bytes, byte[] bytes2, int i) {
      System.arraycopy(bytes, 0, bytes2, i, bytes.length);
      return i + bytes.length;
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public int getExtractedSize() {
      return this.extractedData.dataSize;
   }

   public boolean func_149274_i() {
      return this.field_149279_g;
   }

   public static class Extracted {
      public byte[] data;
      public int dataSize;
   }
}
