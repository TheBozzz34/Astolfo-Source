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
   private final SocketChannel c;
   private final Selector t;
   private final Queue<OutgoingPacket> e;
   private final ByteBuffer Z;
   private final ByteBuffer s;
   private final ByteBuffer[] m;
   private boolean X;
   private boolean r;
   private boolean q;
   private boolean B;
   private boolean I;
   private byte M;
   private static final long a = 66296606086539L;

   public SocketWriter(SocketChannel socketChannel, Selector selector) {
      this.c = socketChannel;
      this.t = selector;
      this.e = new ArrayDeque<>();
      this.Z = ByteBuffer.allocateDirect(5);
      this.s = ByteBuffer.allocateDirect(3);
      this.m = new ByteBuffer[2];
      this.m[0] = this.s;
   }

   public void j() throws IOException {
      while (!this.e.isEmpty()) {
         OutgoingPacket outgoingPacket = this.e.peek();
         Queue queue = outgoingPacket.G;
         if (!this.r) {
            if (!this.q) {
               this.M = RandomValues.S();
               this.Z.put(XorMasks.m(this.M));
               this.Z.putShort((short)(queue.size() ^ this.M));
               this.Z.putShort((short)(outgoingPacket.Z ^ this.M));
               ((Buffer)this.Z).flip();
               this.q = true;
            }

            this.c.write(this.Z);
            if (this.Z.hasRemaining()) {
               return;
            }

            ((Buffer)this.Z).clear();
            this.r = true;
         }

         while (!queue.isEmpty()) {
            PacketChunk packetChunk = (PacketChunk)queue.peek();
            ByteBuffer byteBuffer = packetChunk.u;
            if (this.B) {
               this.c.write(byteBuffer);
            } else {
               if (!this.I) {
                  int i = 0;

                  for (int k = byteBuffer.limit(); i < k; i++) {
                     byteBuffer.put(i, (byte)(byteBuffer.get(i) ^ this.M));
                  }

                  ScramblerRange scramblerRange = Z();
                  scramblerRange.m.B(byteBuffer);
                  ((Buffer)this.s).clear();
                  this.s.put(q(scramblerRange));
                  this.s.putShort((short)(byteBuffer.position() ^ this.M));
                  ((Buffer)this.s).flip();
                  ((Buffer)byteBuffer).flip();
                  this.m[1] = byteBuffer;
                  this.I = true;
               }

               this.c.write(this.m);
               if (this.s.hasRemaining()) {
                  return;
               }

               this.B = true;
            }

            if (byteBuffer.hasRemaining()) {
               return;
            }

            this.B = false;
            this.I = false;
            queue.remove();
         }

         this.r = false;
         this.q = false;
         this.e.remove();
      }

      this.X = false;
      this.c.keyFor(this.t).interestOps(1);
   }

   private static ScramblerRange Z() {
      List list = Scramblers.INSTANCE.Q();
      return (ScramblerRange)list.get(ThreadLocalRandom.current().nextInt(list.size()));
   }

   private static byte q(ScramblerRange scramblerRange) {
      return (byte)ThreadLocalRandom.current().nextInt(scramblerRange.r, scramblerRange.X);
   }

   public SocketChannel q() {
      return this.c;
   }

   public Selector G() {
      return this.t;
   }

   public void k(OutgoingPacket outgoingPacket) {
      this.e.add(outgoingPacket);
   }

   public void m() {
      if (!this.X) {
         this.X = true;
         this.c.keyFor(this.t).interestOps(4);
         this.t.wakeup();
      }
   }
}
