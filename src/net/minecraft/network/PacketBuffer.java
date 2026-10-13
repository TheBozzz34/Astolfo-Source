package net.minecraft.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufProcessor;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

public class PacketBuffer extends ByteBuf {
   private final ByteBuf buf;
   private static final long a = 75668662882152L;

   public PacketBuffer(ByteBuf var1) {
      this.buf = var1;
   }

   public static int getVarIntSize(int var0) {
      for (int var4 = 1; var4 < 5; var4++) {
         if ((var0 & -1 << var4 * 7) == 0) {
            return var4;
         }
      }

      return 5;
   }

   public void writeByteArray(byte[] var1) {
      this.writeVarIntToBuffer(var1.length);
      this.writeBytes(var1);
   }

   public byte[] readByteArray() {
      byte[] var1 = new byte[this.readVarIntFromBuffer()];
      this.readBytes(var1);
      return var1;
   }

   public BlockPos readBlockPos() {
      return BlockPos.fromLong(this.readLong());
   }

   public void writeBlockPos(BlockPos var1) {
      this.writeLong(var1.toLong());
   }

   public IChatComponent readChatComponent() throws IOException {
      return IChatComponent.Serializer.jsonToComponent(this.readStringFromBuffer(32767));
   }

   public void writeChatComponent(IChatComponent var1) throws IOException {
      this.writeString(IChatComponent.Serializer.componentToJson(var1));
   }

   public <T extends Enum<T>> T readEnumValue(Class<T> var1) {
      return (T)var1.getEnumConstants()[this.readVarIntFromBuffer()];
   }

   public void writeEnumValue(Enum<?> var1) {
      this.writeVarIntToBuffer(var1.ordinal());
   }

   public int readVarIntFromBuffer() {
      int var4 = 0;
      int var5 = 0;

      byte var6;
      do {
         var6 = this.readByte();
         var4 |= (var6 & 127) << var5++ * 7;
         if (var5 > 5) {
            throw new RuntimeException("VarInt too big");
         }
      } while ((var6 & 128) == 128);

      return var4;
   }

   public long readVarLong() {
      long var4 = 0L;
      int var6 = 0;

      byte var7;
      do {
         var7 = this.readByte();
         var4 |= (long)(var7 & 127) << var6++ * 7;
         if (var6 > 10) {
            throw new RuntimeException("VarLong too big");
         }
      } while ((var7 & 128) == 128);

      return var4;
   }

   public void writeUuid(UUID var1) {
      this.writeLong(var1.getMostSignificantBits());
      this.writeLong(var1.getLeastSignificantBits());
   }

   public UUID readUuid() {
      return new UUID(this.readLong(), this.readLong());
   }

   public void writeVarIntToBuffer(int var1) {
      while ((var1 & -128) != 0) {
         this.writeByte(var1 & 127 | 128);
         var1 >>>= 7;
      }

      this.writeByte(var1);
   }

   public void writeVarLong(long var1) {
      while ((var1 & -128L) != 0L) {
         this.writeByte((int)(var1 & 127L) | 128);
         var1 >>>= 7;
      }

      this.writeByte((int)var1);
   }

   public void writeNBTTagCompoundToBuffer(NBTTagCompound var1) {
      if (var1 == null) {
         this.writeByte(0);
      } else {
         try {
            CompressedStreamTools.write(var1, new ByteBufOutputStream(this));
         } catch (IOException var6) {
            throw new EncoderException(var6);
         }
      }
   }

   public NBTTagCompound readNBTTagCompoundFromBuffer() throws IOException {
      int var4 = this.readerIndex();
      byte var5 = this.readByte();
      if (var5 == 0) {
         return null;
      }

      this.readerIndex(var4);
      return CompressedStreamTools.read(new ByteBufInputStream(this), new NBTSizeTracker(2097152L));
   }

   public void writeItemStackToBuffer(ItemStack var1) {
      if (var1 == null) {
         this.writeShort(-1);
      } else {
         this.writeShort(Item.getIdFromItem(var1.getItem()));
         this.writeByte(var1.stackSize);
         this.writeShort(var1.getMetadata());
         NBTTagCompound var5 = null;
         if (var1.getItem().isDamageable() || var1.getItem().getShareTag()) {
            var5 = var1.getTagCompound();
         }

         this.writeNBTTagCompoundToBuffer(var5);
      }
   }

   public ItemStack readItemStackFromBuffer() throws IOException {
      ItemStack var4 = null;
      short var5 = this.readShort();
      if (var5 >= 0) {
         byte var6 = this.readByte();
         short var7 = this.readShort();
         var4 = new ItemStack(Item.getItemById(var5), var6, var7);
         var4.setTagCompound(this.readNBTTagCompoundFromBuffer());
      }

      return var4;
   }

   public String readStringFromBuffer(int var1) {
      int var5 = this.readVarIntFromBuffer();
      if (var5 > var1 * 4) {
         throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + var5 + " > " + var1 * 4 + ")");
      } else if (var5 < 0) {
         throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         String var6 = new String(this.readBytes(var5).array(), Charsets.UTF_8);
         if (var6.length() > var1) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + var5 + " > " + var1 + ")");
         } else {
            return var6;
         }
      }
   }

   public PacketBuffer writeString(String var1) {
      byte[] var5 = var1.getBytes(Charsets.UTF_8);
      if (var5.length > 32767) {
         throw new EncoderException("String too big (was " + var1.length() + " bytes encoded, max " + 32767 + ")");
      }

      this.writeVarIntToBuffer(var5.length);
      this.writeBytes(var5);
      return this;
   }

   @Override
   public int capacity() {
      return this.buf.capacity();
   }

   @Override
   public ByteBuf capacity(int var1) {
      return this.buf.capacity(var1);
   }

   @Override
   public int maxCapacity() {
      return this.buf.maxCapacity();
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.buf.alloc();
   }

   @Override
   public ByteOrder order() {
      return this.buf.order();
   }

   @Override
   public ByteBuf order(ByteOrder var1) {
      return this.buf.order(var1);
   }

   @Override
   public ByteBuf unwrap() {
      return this.buf.unwrap();
   }

   @Override
   public boolean isDirect() {
      return this.buf.isDirect();
   }

   @Override
   public int readerIndex() {
      return this.buf.readerIndex();
   }

   @Override
   public ByteBuf readerIndex(int var1) {
      return this.buf.readerIndex(var1);
   }

   @Override
   public int writerIndex() {
      return this.buf.writerIndex();
   }

   @Override
   public ByteBuf writerIndex(int var1) {
      return this.buf.writerIndex(var1);
   }

   @Override
   public ByteBuf setIndex(int var1, int var2) {
      return this.buf.setIndex(var1, var2);
   }

   @Override
   public int readableBytes() {
      return this.buf.readableBytes();
   }

   @Override
   public int writableBytes() {
      return this.buf.writableBytes();
   }

   @Override
   public int maxWritableBytes() {
      return this.buf.maxWritableBytes();
   }

   @Override
   public boolean isReadable() {
      return this.buf.isReadable();
   }

   @Override
   public boolean isReadable(int var1) {
      return this.buf.isReadable(var1);
   }

   @Override
   public boolean isWritable() {
      return this.buf.isWritable();
   }

   @Override
   public boolean isWritable(int var1) {
      return this.buf.isWritable(var1);
   }

   @Override
   public ByteBuf clear() {
      return this.buf.clear();
   }

   @Override
   public ByteBuf markReaderIndex() {
      return this.buf.markReaderIndex();
   }

   @Override
   public ByteBuf resetReaderIndex() {
      return this.buf.resetReaderIndex();
   }

   @Override
   public ByteBuf markWriterIndex() {
      return this.buf.markWriterIndex();
   }

   @Override
   public ByteBuf resetWriterIndex() {
      return this.buf.resetWriterIndex();
   }

   @Override
   public ByteBuf discardReadBytes() {
      return this.buf.discardReadBytes();
   }

   @Override
   public ByteBuf discardSomeReadBytes() {
      return this.buf.discardSomeReadBytes();
   }

   @Override
   public ByteBuf ensureWritable(int var1) {
      return this.buf.ensureWritable(var1);
   }

   @Override
   public int ensureWritable(int var1, boolean var2) {
      return this.buf.ensureWritable(var1, var2);
   }

   @Override
   public boolean getBoolean(int var1) {
      return this.buf.getBoolean(var1);
   }

   @Override
   public byte getByte(int var1) {
      return this.buf.getByte(var1);
   }

   @Override
   public short getUnsignedByte(int var1) {
      return this.buf.getUnsignedByte(var1);
   }

   @Override
   public short getShort(int var1) {
      return this.buf.getShort(var1);
   }

   @Override
   public int getUnsignedShort(int var1) {
      return this.buf.getUnsignedShort(var1);
   }

   @Override
   public int getMedium(int var1) {
      return this.buf.getMedium(var1);
   }

   @Override
   public int getUnsignedMedium(int var1) {
      return this.buf.getUnsignedMedium(var1);
   }

   @Override
   public int getInt(int var1) {
      return this.buf.getInt(var1);
   }

   @Override
   public long getUnsignedInt(int var1) {
      return this.buf.getUnsignedInt(var1);
   }

   @Override
   public long getLong(int var1) {
      return this.buf.getLong(var1);
   }

   @Override
   public char getChar(int var1) {
      return this.buf.getChar(var1);
   }

   @Override
   public float getFloat(int var1) {
      return this.buf.getFloat(var1);
   }

   @Override
   public double getDouble(int var1) {
      return this.buf.getDouble(var1);
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2) {
      return this.buf.getBytes(var1, var2);
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3) {
      return this.buf.getBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuf var2, int var3, int var4) {
      return this.buf.getBytes(var1, var2, var3, var4);
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2) {
      return this.buf.getBytes(var1, var2);
   }

   @Override
   public ByteBuf getBytes(int var1, byte[] var2, int var3, int var4) {
      return this.buf.getBytes(var1, var2, var3, var4);
   }

   @Override
   public ByteBuf getBytes(int var1, ByteBuffer var2) {
      return this.buf.getBytes(var1, var2);
   }

   @Override
   public ByteBuf getBytes(int var1, OutputStream var2, int var3) throws IOException {
      return this.buf.getBytes(var1, var2, var3);
   }

   @Override
   public int getBytes(int var1, GatheringByteChannel var2, int var3) throws IOException {
      return this.buf.getBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf setBoolean(int var1, boolean var2) {
      return this.buf.setBoolean(var1, var2);
   }

   @Override
   public ByteBuf setByte(int var1, int var2) {
      return this.buf.setByte(var1, var2);
   }

   @Override
   public ByteBuf setShort(int var1, int var2) {
      return this.buf.setShort(var1, var2);
   }

   @Override
   public ByteBuf setMedium(int var1, int var2) {
      return this.buf.setMedium(var1, var2);
   }

   @Override
   public ByteBuf setInt(int var1, int var2) {
      return this.buf.setInt(var1, var2);
   }

   @Override
   public ByteBuf setLong(int var1, long var2) {
      return this.buf.setLong(var1, var2);
   }

   @Override
   public ByteBuf setChar(int var1, int var2) {
      return this.buf.setChar(var1, var2);
   }

   @Override
   public ByteBuf setFloat(int var1, float var2) {
      return this.buf.setFloat(var1, var2);
   }

   @Override
   public ByteBuf setDouble(int var1, double var2) {
      return this.buf.setDouble(var1, var2);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2) {
      return this.buf.setBytes(var1, var2);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3) {
      return this.buf.setBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuf var2, int var3, int var4) {
      return this.buf.setBytes(var1, var2, var3, var4);
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2) {
      return this.buf.setBytes(var1, var2);
   }

   @Override
   public ByteBuf setBytes(int var1, byte[] var2, int var3, int var4) {
      return this.buf.setBytes(var1, var2, var3, var4);
   }

   @Override
   public ByteBuf setBytes(int var1, ByteBuffer var2) {
      return this.buf.setBytes(var1, var2);
   }

   @Override
   public int setBytes(int var1, InputStream var2, int var3) throws IOException {
      return this.buf.setBytes(var1, var2, var3);
   }

   @Override
   public int setBytes(int var1, ScatteringByteChannel var2, int var3) throws IOException {
      return this.buf.setBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf setZero(int var1, int var2) {
      return this.buf.setZero(var1, var2);
   }

   @Override
   public boolean readBoolean() {
      return this.buf.readBoolean();
   }

   @Override
   public byte readByte() {
      return this.buf.readByte();
   }

   @Override
   public short readUnsignedByte() {
      return this.buf.readUnsignedByte();
   }

   @Override
   public short readShort() {
      return this.buf.readShort();
   }

   @Override
   public int readUnsignedShort() {
      return this.buf.readUnsignedShort();
   }

   @Override
   public int readMedium() {
      return this.buf.readMedium();
   }

   @Override
   public int readUnsignedMedium() {
      return this.buf.readUnsignedMedium();
   }

   @Override
   public int readInt() {
      return this.buf.readInt();
   }

   @Override
   public long readUnsignedInt() {
      return this.buf.readUnsignedInt();
   }

   @Override
   public long readLong() {
      return this.buf.readLong();
   }

   @Override
   public char readChar() {
      return this.buf.readChar();
   }

   @Override
   public float readFloat() {
      return this.buf.readFloat();
   }

   @Override
   public double readDouble() {
      return this.buf.readDouble();
   }

   @Override
   public ByteBuf readBytes(int var1) {
      return this.buf.readBytes(var1);
   }

   @Override
   public ByteBuf readSlice(int var1) {
      return this.buf.readSlice(var1);
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1) {
      return this.buf.readBytes(var1);
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1, int var2) {
      return this.buf.readBytes(var1, var2);
   }

   @Override
   public ByteBuf readBytes(ByteBuf var1, int var2, int var3) {
      return this.buf.readBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf readBytes(byte[] var1) {
      return this.buf.readBytes(var1);
   }

   @Override
   public ByteBuf readBytes(byte[] var1, int var2, int var3) {
      return this.buf.readBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf readBytes(ByteBuffer var1) {
      return this.buf.readBytes(var1);
   }

   @Override
   public ByteBuf readBytes(OutputStream var1, int var2) throws IOException {
      return this.buf.readBytes(var1, var2);
   }

   @Override
   public int readBytes(GatheringByteChannel var1, int var2) throws IOException {
      return this.buf.readBytes(var1, var2);
   }

   @Override
   public ByteBuf skipBytes(int var1) {
      return this.buf.skipBytes(var1);
   }

   @Override
   public ByteBuf writeBoolean(boolean var1) {
      return this.buf.writeBoolean(var1);
   }

   @Override
   public ByteBuf writeByte(int var1) {
      return this.buf.writeByte(var1);
   }

   @Override
   public ByteBuf writeShort(int var1) {
      return this.buf.writeShort(var1);
   }

   @Override
   public ByteBuf writeMedium(int var1) {
      return this.buf.writeMedium(var1);
   }

   @Override
   public ByteBuf writeInt(int var1) {
      return this.buf.writeInt(var1);
   }

   @Override
   public ByteBuf writeLong(long var1) {
      return this.buf.writeLong(var1);
   }

   @Override
   public ByteBuf writeChar(int var1) {
      return this.buf.writeChar(var1);
   }

   @Override
   public ByteBuf writeFloat(float var1) {
      return this.buf.writeFloat(var1);
   }

   @Override
   public ByteBuf writeDouble(double var1) {
      return this.buf.writeDouble(var1);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1) {
      return this.buf.writeBytes(var1);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1, int var2) {
      return this.buf.writeBytes(var1, var2);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf var1, int var2, int var3) {
      return this.buf.writeBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf writeBytes(byte[] var1) {
      return this.buf.writeBytes(var1);
   }

   @Override
   public ByteBuf writeBytes(byte[] var1, int var2, int var3) {
      return this.buf.writeBytes(var1, var2, var3);
   }

   @Override
   public ByteBuf writeBytes(ByteBuffer var1) {
      return this.buf.writeBytes(var1);
   }

   @Override
   public int writeBytes(InputStream var1, int var2) throws IOException {
      return this.buf.writeBytes(var1, var2);
   }

   @Override
   public int writeBytes(ScatteringByteChannel var1, int var2) throws IOException {
      return this.buf.writeBytes(var1, var2);
   }

   @Override
   public ByteBuf writeZero(int var1) {
      return this.buf.writeZero(var1);
   }

   @Override
   public int indexOf(int var1, int var2, byte var3) {
      return this.buf.indexOf(var1, var2, var3);
   }

   @Override
   public int bytesBefore(byte var1) {
      return this.buf.bytesBefore(var1);
   }

   @Override
   public int bytesBefore(int var1, byte var2) {
      return this.buf.bytesBefore(var1, var2);
   }

   @Override
   public int bytesBefore(int var1, int var2, byte var3) {
      return this.buf.bytesBefore(var1, var2, var3);
   }

   @Override
   public int forEachByte(ByteBufProcessor var1) {
      return this.buf.forEachByte(var1);
   }

   @Override
   public int forEachByte(int var1, int var2, ByteBufProcessor var3) {
      return this.buf.forEachByte(var1, var2, var3);
   }

   @Override
   public int forEachByteDesc(ByteBufProcessor var1) {
      return this.buf.forEachByteDesc(var1);
   }

   @Override
   public int forEachByteDesc(int var1, int var2, ByteBufProcessor var3) {
      return this.buf.forEachByteDesc(var1, var2, var3);
   }

   @Override
   public ByteBuf copy() {
      return this.buf.copy();
   }

   @Override
   public ByteBuf copy(int var1, int var2) {
      return this.buf.copy(var1, var2);
   }

   @Override
   public ByteBuf slice() {
      return this.buf.slice();
   }

   @Override
   public ByteBuf slice(int var1, int var2) {
      return this.buf.slice(var1, var2);
   }

   @Override
   public ByteBuf duplicate() {
      return this.buf.duplicate();
   }

   @Override
   public int nioBufferCount() {
      return this.buf.nioBufferCount();
   }

   @Override
   public ByteBuffer nioBuffer() {
      return this.buf.nioBuffer();
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      return this.buf.nioBuffer(var1, var2);
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      return this.buf.internalNioBuffer(var1, var2);
   }

   @Override
   public ByteBuffer[] nioBuffers() {
      return this.buf.nioBuffers();
   }

   @Override
   public ByteBuffer[] nioBuffers(int var1, int var2) {
      return this.buf.nioBuffers(var1, var2);
   }

   @Override
   public boolean hasArray() {
      return this.buf.hasArray();
   }

   @Override
   public byte[] array() {
      return this.buf.array();
   }

   @Override
   public int arrayOffset() {
      return this.buf.arrayOffset();
   }

   @Override
   public boolean hasMemoryAddress() {
      return this.buf.hasMemoryAddress();
   }

   @Override
   public long memoryAddress() {
      return this.buf.memoryAddress();
   }

   @Override
   public String toString(Charset var1) {
      return this.buf.toString(var1);
   }

   @Override
   public String toString(int var1, int var2, Charset var3) {
      return this.buf.toString(var1, var2, var3);
   }

   @Override
   public int hashCode() {
      return this.buf.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      return this.buf.equals(var1);
   }

   @Override
   public int compareTo(ByteBuf var1) {
      return this.buf.compareTo(var1);
   }

   @Override
   public String toString() {
      return this.buf.toString();
   }

   @Override
   public ByteBuf retain(int var1) {
      return this.buf.retain(var1);
   }

   @Override
   public ByteBuf retain() {
      return this.buf.retain();
   }

   @Override
   public int refCnt() {
      return this.buf.refCnt();
   }

   @Override
   public boolean release() {
      return this.buf.release();
   }

   @Override
   public boolean release(int var1) {
      return this.buf.release(var1);
   }
}
