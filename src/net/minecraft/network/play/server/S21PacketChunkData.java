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
   private static final long a = 109847491003025L;

   public S21PacketChunkData() {
   }

   public S21PacketChunkData(Chunk var1, boolean var2, int var3) {
      this.chunkX = var1.xPosition;
      this.chunkZ = var1.zPosition;
      this.field_149279_g = var2;
      this.extractedData = getExtractedData(var1, var2, !var1.getWorld().provider.getHasNoSky(), var3);
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.chunkX = var1.readInt();
      this.chunkZ = var1.readInt();
      this.field_149279_g = var1.readBoolean();
      this.extractedData = new S21PacketChunkData.Extracted();
      this.extractedData.dataSize = var1.readShort();
      this.extractedData.data = var1.readByteArray();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeInt(this.chunkX);
      var1.writeInt(this.chunkZ);
      var1.writeBoolean(this.field_149279_g);
      var1.writeShort((short)(this.extractedData.dataSize & 65535));
      var1.writeByteArray(this.extractedData.data);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleChunkData(this);
   }

   public byte[] getExtractedDataBytes() {
      return this.extractedData.data;
   }

   public static int func_180737_a(int var0, boolean var1, boolean var2) {
      int var6 = var0 * 2 * 16 * 16 * 16;
      int var7 = var0 * 16 * 16 * 16 / 2;
      int var8 = var1 ? var0 * 16 * 16 * 16 / 2 : 0;
      int var9 = var2 ? 256 : 0;
      return var6 + var7 + var8 + var9;
   }

   public static S21PacketChunkData.Extracted getExtractedData(Chunk var0, boolean var1, boolean var2, int var3) {
      ExtendedBlockStorage[] var7 = var0.getBlockStorageArray();
      S21PacketChunkData.Extracted var8 = new S21PacketChunkData.Extracted();
      ArrayList var9 = Lists.newArrayList();

      for (int var10 = 0; var10 < var7.length; var10++) {
         ExtendedBlockStorage var11 = var7[var10];
         if (var11 != null && (!var1 || !var11.isEmpty()) && (var3 & 1 << var10) != 0) {
            var8.dataSize |= 1 << var10;
            var9.add(var11);
         }
      }

      var8.data = new byte[func_180737_a(Integer.bitCount(var8.dataSize), var2, var1)];
      int var18 = 0;

      for (Object var12$raw : var9) {
         ExtendedBlockStorage var12 = (ExtendedBlockStorage)var12$raw;
         char[] var13 = var12.getData();

         for (char var17 : var13) {
            var8.data[var18++] = (byte)(var17 & 0xFF);
            var8.data[var18++] = (byte)(var17 >> '\b' & 0xFF);
         }
      }

      for (Object var23$raw : var9) {
         ExtendedBlockStorage var23 = (ExtendedBlockStorage)var23$raw;
         var18 = func_179757_a(var23.getBlocklightArray().getData(), var8.data, var18);
      }

      if (var2) {
         for (Object var24$raw : var9) {
            ExtendedBlockStorage var24 = (ExtendedBlockStorage)var24$raw;
            var18 = func_179757_a(var24.getSkylightArray().getData(), var8.data, var18);
         }
      }

      if (var1) {
         func_179757_a(var0.getBiomeArray(), var8.data, var18);
      }

      return var8;
   }

   private static int func_179757_a(byte[] var0, byte[] var1, int var2) {
      System.arraycopy(var0, 0, var1, var2, var0.length);
      return var2 + var0.length;
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
