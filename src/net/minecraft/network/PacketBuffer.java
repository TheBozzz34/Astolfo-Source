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

   public PacketBuffer(ByteBuf wrapped) {
      this.buf = wrapped;
   }

   public static int getVarIntSize(int input) {
      for (int i = 1; i < 5; i++) {
         if ((input & -1 << i * 7) == 0) {
            return i;
         }
      }

      return 5;
   }

   public void writeByteArray(byte[] array) {
      this.writeVarIntToBuffer(array.length);
      this.writeBytes(array);
   }

   public byte[] readByteArray() {
      byte[] bytes = new byte[this.readVarIntFromBuffer()];
      this.readBytes(bytes);
      return bytes;
   }

   public BlockPos readBlockPos() {
      return BlockPos.fromLong(this.readLong());
   }

   public void writeBlockPos(BlockPos pos) {
      this.writeLong(pos.toLong());
   }

   public IChatComponent readChatComponent() throws IOException {
      return IChatComponent.Serializer.jsonToComponent(this.readStringFromBuffer(32767));
   }

   public void writeChatComponent(IChatComponent component) throws IOException {
      this.writeString(IChatComponent.Serializer.componentToJson(component));
   }

   public <T extends Enum<T>> T readEnumValue(Class<T> enumClass) {
      return (T)enumClass.getEnumConstants()[this.readVarIntFromBuffer()];
   }

   public void writeEnumValue(Enum<?> value) {
      this.writeVarIntToBuffer(value.ordinal());
   }

   public int readVarIntFromBuffer() {
      int i = 0;
      int j = 0;

      byte b;
      do {
         b = this.readByte();
         i |= (b & 127) << j++ * 7;
         if (j > 5) {
            throw new RuntimeException("VarInt too big");
         }
      } while ((b & 128) == 128);

      return i;
   }

   public long readVarLong() {
      long l = 0L;
      int i = 0;

      byte b;
      do {
         b = this.readByte();
         l |= (long)(b & 127) << i++ * 7;
         if (i > 10) {
            throw new RuntimeException("VarLong too big");
         }
      } while ((b & 128) == 128);

      return l;
   }

   public void writeUuid(UUID uuid) {
      this.writeLong(uuid.getMostSignificantBits());
      this.writeLong(uuid.getLeastSignificantBits());
   }

   public UUID readUuid() {
      return new UUID(this.readLong(), this.readLong());
   }

   public void writeVarIntToBuffer(int input) {
      while ((input & -128) != 0) {
         this.writeByte(input & 127 | 128);
         input >>>= 7;
      }

      this.writeByte(input);
   }

   public void writeVarLong(long value) {
      while ((value & -128L) != 0L) {
         this.writeByte((int)(value & 127L) | 128);
         value >>>= 7;
      }

      this.writeByte((int)value);
   }

   public void writeNBTTagCompoundToBuffer(NBTTagCompound nbt) {
      if (nbt == null) {
         this.writeByte(0);
      } else {
         try {
            CompressedStreamTools.write(nbt, new ByteBufOutputStream(this));
         } catch (IOException ioException) {
            throw new EncoderException(ioException);
         }
      }
   }

   public NBTTagCompound readNBTTagCompoundFromBuffer() throws IOException {
      int i = this.readerIndex();
      byte b = this.readByte();
      if (b == 0) {
         return null;
      }

      this.readerIndex(i);
      return CompressedStreamTools.read(new ByteBufInputStream(this), new NBTSizeTracker(2097152L));
   }

   public void writeItemStackToBuffer(ItemStack stack) {
      if (stack == null) {
         this.writeShort(-1);
      } else {
         this.writeShort(Item.getIdFromItem(stack.getItem()));
         this.writeByte(stack.stackSize);
         this.writeShort(stack.getMetadata());
         NBTTagCompound nbtTagCompound = null;
         if (stack.getItem().isDamageable() || stack.getItem().getShareTag()) {
            nbtTagCompound = stack.getTagCompound();
         }

         this.writeNBTTagCompoundToBuffer(nbtTagCompound);
      }
   }

   public ItemStack readItemStackFromBuffer() throws IOException {
      ItemStack itemStack = null;
      short s = this.readShort();
      if (s >= 0) {
         byte b = this.readByte();
         short s2 = this.readShort();
         itemStack = new ItemStack(Item.getItemById(s), b, s2);
         itemStack.setTagCompound(this.readNBTTagCompoundFromBuffer());
      }

      return itemStack;
   }

   public String readStringFromBuffer(int maxLength) {
      int i = this.readVarIntFromBuffer();
      if (i > maxLength * 4) {
         throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")");
      } else if (i < 0) {
         throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         String string2 = new String(this.readBytes(i).array(), Charsets.UTF_8);
         if (string2.length() > maxLength) {
            throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")");
         } else {
            return string2;
         }
      }
   }

   public PacketBuffer writeString(String string) {
      byte[] bytes2 = string.getBytes(Charsets.UTF_8);
      if (bytes2.length > 32767) {
         throw new EncoderException("String too big (was " + string.length() + " bytes encoded, max " + 32767 + ")");
      }

      this.writeVarIntToBuffer(bytes2.length);
      this.writeBytes(bytes2);
      return this;
   }

   @Override
   public int capacity() {
      return this.buf.capacity();
   }

   @Override
   public ByteBuf capacity(int i) {
      return this.buf.capacity(i);
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
   public ByteBuf order(ByteOrder byteOrder) {
      return this.buf.order(byteOrder);
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
   public ByteBuf readerIndex(int i) {
      return this.buf.readerIndex(i);
   }

   @Override
   public int writerIndex() {
      return this.buf.writerIndex();
   }

   @Override
   public ByteBuf writerIndex(int i) {
      return this.buf.writerIndex(i);
   }

   @Override
   public ByteBuf setIndex(int i, int j) {
      return this.buf.setIndex(i, j);
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
   public boolean isReadable(int i) {
      return this.buf.isReadable(i);
   }

   @Override
   public boolean isWritable() {
      return this.buf.isWritable();
   }

   @Override
   public boolean isWritable(int i) {
      return this.buf.isWritable(i);
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
   public ByteBuf ensureWritable(int i) {
      return this.buf.ensureWritable(i);
   }

   @Override
   public int ensureWritable(int i, boolean bl) {
      return this.buf.ensureWritable(i, bl);
   }

   @Override
   public boolean getBoolean(int i) {
      return this.buf.getBoolean(i);
   }

   @Override
   public byte getByte(int i) {
      return this.buf.getByte(i);
   }

   @Override
   public short getUnsignedByte(int i) {
      return this.buf.getUnsignedByte(i);
   }

   @Override
   public short getShort(int i) {
      return this.buf.getShort(i);
   }

   @Override
   public int getUnsignedShort(int i) {
      return this.buf.getUnsignedShort(i);
   }

   @Override
   public int getMedium(int i) {
      return this.buf.getMedium(i);
   }

   @Override
   public int getUnsignedMedium(int i) {
      return this.buf.getUnsignedMedium(i);
   }

   @Override
   public int getInt(int i) {
      return this.buf.getInt(i);
   }

   @Override
   public long getUnsignedInt(int i) {
      return this.buf.getUnsignedInt(i);
   }

   @Override
   public long getLong(int i) {
      return this.buf.getLong(i);
   }

   @Override
   public char getChar(int i) {
      return this.buf.getChar(i);
   }

   @Override
   public float getFloat(int i) {
      return this.buf.getFloat(i);
   }

   @Override
   public double getDouble(int i) {
      return this.buf.getDouble(i);
   }

   @Override
   public ByteBuf getBytes(int i, ByteBuf byteBuf) {
      return this.buf.getBytes(i, byteBuf);
   }

   @Override
   public ByteBuf getBytes(int i, ByteBuf byteBuf, int j) {
      return this.buf.getBytes(i, byteBuf, j);
   }

   @Override
   public ByteBuf getBytes(int i, ByteBuf byteBuf, int j, int k) {
      return this.buf.getBytes(i, byteBuf, j, k);
   }

   @Override
   public ByteBuf getBytes(int i, byte[] bytes) {
      return this.buf.getBytes(i, bytes);
   }

   @Override
   public ByteBuf getBytes(int i, byte[] bytes, int j, int k) {
      return this.buf.getBytes(i, bytes, j, k);
   }

   @Override
   public ByteBuf getBytes(int i, ByteBuffer byteBuffer) {
      return this.buf.getBytes(i, byteBuffer);
   }

   @Override
   public ByteBuf getBytes(int i, OutputStream outputStream, int j) throws IOException {
      return this.buf.getBytes(i, outputStream, j);
   }

   @Override
   public int getBytes(int i, GatheringByteChannel gatheringByteChannel, int j) throws IOException {
      return this.buf.getBytes(i, gatheringByteChannel, j);
   }

   @Override
   public ByteBuf setBoolean(int i, boolean bl) {
      return this.buf.setBoolean(i, bl);
   }

   @Override
   public ByteBuf setByte(int i, int j) {
      return this.buf.setByte(i, j);
   }

   @Override
   public ByteBuf setShort(int i, int j) {
      return this.buf.setShort(i, j);
   }

   @Override
   public ByteBuf setMedium(int i, int j) {
      return this.buf.setMedium(i, j);
   }

   @Override
   public ByteBuf setInt(int i, int j) {
      return this.buf.setInt(i, j);
   }

   @Override
   public ByteBuf setLong(int i, long l) {
      return this.buf.setLong(i, l);
   }

   @Override
   public ByteBuf setChar(int i, int j) {
      return this.buf.setChar(i, j);
   }

   @Override
   public ByteBuf setFloat(int i, float f) {
      return this.buf.setFloat(i, f);
   }

   @Override
   public ByteBuf setDouble(int i, double d) {
      return this.buf.setDouble(i, d);
   }

   @Override
   public ByteBuf setBytes(int i, ByteBuf byteBuf) {
      return this.buf.setBytes(i, byteBuf);
   }

   @Override
   public ByteBuf setBytes(int i, ByteBuf byteBuf, int j) {
      return this.buf.setBytes(i, byteBuf, j);
   }

   @Override
   public ByteBuf setBytes(int i, ByteBuf byteBuf, int j, int k) {
      return this.buf.setBytes(i, byteBuf, j, k);
   }

   @Override
   public ByteBuf setBytes(int i, byte[] bytes) {
      return this.buf.setBytes(i, bytes);
   }

   @Override
   public ByteBuf setBytes(int i, byte[] bytes, int j, int k) {
      return this.buf.setBytes(i, bytes, j, k);
   }

   @Override
   public ByteBuf setBytes(int i, ByteBuffer byteBuffer) {
      return this.buf.setBytes(i, byteBuffer);
   }

   @Override
   public int setBytes(int i, InputStream inputStream, int j) throws IOException {
      return this.buf.setBytes(i, inputStream, j);
   }

   @Override
   public int setBytes(int i, ScatteringByteChannel scatteringByteChannel, int j) throws IOException {
      return this.buf.setBytes(i, scatteringByteChannel, j);
   }

   @Override
   public ByteBuf setZero(int i, int j) {
      return this.buf.setZero(i, j);
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
   public ByteBuf readBytes(int i) {
      return this.buf.readBytes(i);
   }

   @Override
   public ByteBuf readSlice(int i) {
      return this.buf.readSlice(i);
   }

   @Override
   public ByteBuf readBytes(ByteBuf byteBuf) {
      return this.buf.readBytes(byteBuf);
   }

   @Override
   public ByteBuf readBytes(ByteBuf byteBuf, int i) {
      return this.buf.readBytes(byteBuf, i);
   }

   @Override
   public ByteBuf readBytes(ByteBuf byteBuf, int i, int j) {
      return this.buf.readBytes(byteBuf, i, j);
   }

   @Override
   public ByteBuf readBytes(byte[] bytes) {
      return this.buf.readBytes(bytes);
   }

   @Override
   public ByteBuf readBytes(byte[] bytes, int i, int j) {
      return this.buf.readBytes(bytes, i, j);
   }

   @Override
   public ByteBuf readBytes(ByteBuffer byteBuffer) {
      return this.buf.readBytes(byteBuffer);
   }

   @Override
   public ByteBuf readBytes(OutputStream outputStream, int i) throws IOException {
      return this.buf.readBytes(outputStream, i);
   }

   @Override
   public int readBytes(GatheringByteChannel gatheringByteChannel, int i) throws IOException {
      return this.buf.readBytes(gatheringByteChannel, i);
   }

   @Override
   public ByteBuf skipBytes(int i) {
      return this.buf.skipBytes(i);
   }

   @Override
   public ByteBuf writeBoolean(boolean bl) {
      return this.buf.writeBoolean(bl);
   }

   @Override
   public ByteBuf writeByte(int i) {
      return this.buf.writeByte(i);
   }

   @Override
   public ByteBuf writeShort(int i) {
      return this.buf.writeShort(i);
   }

   @Override
   public ByteBuf writeMedium(int i) {
      return this.buf.writeMedium(i);
   }

   @Override
   public ByteBuf writeInt(int i) {
      return this.buf.writeInt(i);
   }

   @Override
   public ByteBuf writeLong(long l) {
      return this.buf.writeLong(l);
   }

   @Override
   public ByteBuf writeChar(int i) {
      return this.buf.writeChar(i);
   }

   @Override
   public ByteBuf writeFloat(float f) {
      return this.buf.writeFloat(f);
   }

   @Override
   public ByteBuf writeDouble(double d) {
      return this.buf.writeDouble(d);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf byteBuf) {
      return this.buf.writeBytes(byteBuf);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf byteBuf, int i) {
      return this.buf.writeBytes(byteBuf, i);
   }

   @Override
   public ByteBuf writeBytes(ByteBuf byteBuf, int i, int j) {
      return this.buf.writeBytes(byteBuf, i, j);
   }

   @Override
   public ByteBuf writeBytes(byte[] bytes) {
      return this.buf.writeBytes(bytes);
   }

   @Override
   public ByteBuf writeBytes(byte[] bytes, int i, int j) {
      return this.buf.writeBytes(bytes, i, j);
   }

   @Override
   public ByteBuf writeBytes(ByteBuffer byteBuffer) {
      return this.buf.writeBytes(byteBuffer);
   }

   @Override
   public int writeBytes(InputStream inputStream, int i) throws IOException {
      return this.buf.writeBytes(inputStream, i);
   }

   @Override
   public int writeBytes(ScatteringByteChannel scatteringByteChannel, int i) throws IOException {
      return this.buf.writeBytes(scatteringByteChannel, i);
   }

   @Override
   public ByteBuf writeZero(int i) {
      return this.buf.writeZero(i);
   }

   @Override
   public int indexOf(int i, int j, byte b) {
      return this.buf.indexOf(i, j, b);
   }

   @Override
   public int bytesBefore(byte b) {
      return this.buf.bytesBefore(b);
   }

   @Override
   public int bytesBefore(int i, byte b) {
      return this.buf.bytesBefore(i, b);
   }

   @Override
   public int bytesBefore(int i, int j, byte b) {
      return this.buf.bytesBefore(i, j, b);
   }

   @Override
   public int forEachByte(ByteBufProcessor byteBufProcessor) {
      return this.buf.forEachByte(byteBufProcessor);
   }

   @Override
   public int forEachByte(int i, int j, ByteBufProcessor byteBufProcessor) {
      return this.buf.forEachByte(i, j, byteBufProcessor);
   }

   @Override
   public int forEachByteDesc(ByteBufProcessor byteBufProcessor) {
      return this.buf.forEachByteDesc(byteBufProcessor);
   }

   @Override
   public int forEachByteDesc(int i, int j, ByteBufProcessor byteBufProcessor) {
      return this.buf.forEachByteDesc(i, j, byteBufProcessor);
   }

   @Override
   public ByteBuf copy() {
      return this.buf.copy();
   }

   @Override
   public ByteBuf copy(int i, int j) {
      return this.buf.copy(i, j);
   }

   @Override
   public ByteBuf slice() {
      return this.buf.slice();
   }

   @Override
   public ByteBuf slice(int i, int j) {
      return this.buf.slice(i, j);
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
   public ByteBuffer nioBuffer(int i, int j) {
      return this.buf.nioBuffer(i, j);
   }

   @Override
   public ByteBuffer internalNioBuffer(int i, int j) {
      return this.buf.internalNioBuffer(i, j);
   }

   @Override
   public ByteBuffer[] nioBuffers() {
      return this.buf.nioBuffers();
   }

   @Override
   public ByteBuffer[] nioBuffers(int i, int j) {
      return this.buf.nioBuffers(i, j);
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
   public String toString(Charset charset) {
      return this.buf.toString(charset);
   }

   @Override
   public String toString(int i, int j, Charset charset) {
      return this.buf.toString(i, j, charset);
   }

   @Override
   public int hashCode() {
      return this.buf.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      return this.buf.equals(object);
   }

   @Override
   public int compareTo(ByteBuf byteBuf) {
      return this.buf.compareTo(byteBuf);
   }

   @Override
   public String toString() {
      return this.buf.toString();
   }

   @Override
   public ByteBuf retain(int i) {
      return this.buf.retain(i);
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
   public boolean release(int i) {
      return this.buf.release(i);
   }
}
