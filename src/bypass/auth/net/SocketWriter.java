package bypass.auth.net;

import bypass.auth.net.scramble.ScramblerRange;
import bypass.auth.net.scramble.Scramblers;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

public final class SocketWriter {
   private final SocketChannel socketChannel;
   private final Selector selector;
   private final Queue<OutgoingPacket> pendingPackets;
   private final ByteBuffer headerBuffer;
   private final ByteBuffer chunkHeaderBuffer;
   private final ByteBuffer[] chunkBuffers;
   private boolean writePending;
   private boolean headerWritten;
   private boolean headerPrepared;
   private boolean chunkHeaderWritten;
   private boolean chunkPrepared;
   private byte xorKey;

   public SocketWriter(SocketChannel socketChannel, Selector selector) {
      this.socketChannel = socketChannel;
      this.selector = selector;
      this.pendingPackets = new ArrayDeque<>();
      this.headerBuffer = ByteBuffer.allocateDirect(5);
      this.chunkHeaderBuffer = ByteBuffer.allocateDirect(3);
      this.chunkBuffers = new ByteBuffer[2];
      this.chunkBuffers[0] = this.chunkHeaderBuffer;
   }

   public void flush() throws IOException {
      while (!this.pendingPackets.isEmpty()) {
         OutgoingPacket outgoingPacket = this.pendingPackets.peek();
         Queue queue = outgoingPacket.chunks;
         if (!this.headerWritten) {
            if (!this.headerPrepared) {
               this.xorKey = RandomValues.randomByte();
               this.headerBuffer.put(XorMasks.xor72(this.xorKey));
               this.headerBuffer.putShort((short)(queue.size() ^ this.xorKey));
               this.headerBuffer.putShort((short)(outgoingPacket.id ^ this.xorKey));
               ((Buffer)this.headerBuffer).flip();
               this.headerPrepared = true;
            }

            this.socketChannel.write(this.headerBuffer);
            if (this.headerBuffer.hasRemaining()) {
               return;
            }

            ((Buffer)this.headerBuffer).clear();
            this.headerWritten = true;
         }

         while (!queue.isEmpty()) {
            PacketChunk packetChunk = (PacketChunk)queue.peek();
            ByteBuffer byteBuffer = packetChunk.buffer;
            if (this.chunkHeaderWritten) {
               this.socketChannel.write(byteBuffer);
            } else {
               if (!this.chunkPrepared) {
                  int i = 0;

                  for (int j = byteBuffer.limit(); i < j; i++) {
                     byteBuffer.put(i, (byte)(byteBuffer.get(i) ^ this.xorKey));
                  }

                  ScramblerRange scramblerRange = pickRandomScrambler();
                  scramblerRange.scrambler.scramble(byteBuffer);
                  ((Buffer)this.chunkHeaderBuffer).clear();
                  this.chunkHeaderBuffer.put(randomScramblerId(scramblerRange));
                  this.chunkHeaderBuffer.putShort((short)(byteBuffer.position() ^ this.xorKey));
                  ((Buffer)this.chunkHeaderBuffer).flip();
                  ((Buffer)byteBuffer).flip();
                  this.chunkBuffers[1] = byteBuffer;
                  this.chunkPrepared = true;
               }

               this.socketChannel.write(this.chunkBuffers);
               if (this.chunkHeaderBuffer.hasRemaining()) {
                  return;
               }

               this.chunkHeaderWritten = true;
            }

            if (byteBuffer.hasRemaining()) {
               return;
            }

            this.chunkHeaderWritten = false;
            this.chunkPrepared = false;
            queue.remove();
         }

         this.headerWritten = false;
         this.headerPrepared = false;
         this.pendingPackets.remove();
      }

      this.writePending = false;
      this.socketChannel.keyFor(this.selector).interestOps(1);
   }

   private static ScramblerRange pickRandomScrambler() {
      List list = Scramblers.INSTANCE.getRanges();
      return (ScramblerRange)list.get(ThreadLocalRandom.current().nextInt(list.size()));
   }

   private static byte randomScramblerId(ScramblerRange scramblerRange) {
      return (byte)ThreadLocalRandom.current().nextInt(scramblerRange.minId, scramblerRange.maxId);
   }

   public SocketChannel getSocketChannel() {
      return this.socketChannel;
   }

   public Selector getSelector() {
      return this.selector;
   }

   public void enqueue(OutgoingPacket outgoingPacket) {
      this.pendingPackets.add(outgoingPacket);
   }

   public void requestWrite() {
      if (!this.writePending) {
         this.writePending = true;
         this.socketChannel.keyFor(this.selector).interestOps(4);
         this.selector.wakeup();
      }
   }
}
