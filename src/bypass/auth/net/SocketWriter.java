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

   public SocketWriter(SocketChannel var1, Selector var2) {
      this.c = var1;
      this.t = var2;
      this.e = new ArrayDeque<>();
      this.Z = ByteBuffer.allocateDirect(5);
      this.s = ByteBuffer.allocateDirect(3);
      this.m = new ByteBuffer[2];
      this.m[0] = this.s;
   }

   public void j() throws IOException {
      while (!this.e.isEmpty()) {
         OutgoingPacket var4 = this.e.peek();
         Queue var5 = var4.G;
         if (!this.r) {
            if (!this.q) {
               this.M = RandomValues.S();
               this.Z.put(XorMasks.m(this.M));
               this.Z.putShort((short)(var5.size() ^ this.M));
               this.Z.putShort((short)(var4.Z ^ this.M));
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

         while (!var5.isEmpty()) {
            PacketChunk var6 = (PacketChunk)var5.peek();
            ByteBuffer var7 = var6.u;
            if (this.B) {
               this.c.write(var7);
            } else {
               if (!this.I) {
                  int var8 = 0;

                  for (int var9 = var7.limit(); var8 < var9; var8++) {
                     var7.put(var8, (byte)(var7.get(var8) ^ this.M));
                  }

                  ScramblerRange var10 = Z();
                  var10.m.B(var7);
                  ((Buffer)this.s).clear();
                  this.s.put(q(var10));
                  this.s.putShort((short)(var7.position() ^ this.M));
                  ((Buffer)this.s).flip();
                  ((Buffer)var7).flip();
                  this.m[1] = var7;
                  this.I = true;
               }

               this.c.write(this.m);
               if (this.s.hasRemaining()) {
                  return;
               }

               this.B = true;
            }

            if (var7.hasRemaining()) {
               return;
            }

            this.B = false;
            this.I = false;
            var5.remove();
         }

         this.r = false;
         this.q = false;
         this.e.remove();
      }

      this.X = false;
      this.c.keyFor(this.t).interestOps(1);
   }

   private static ScramblerRange Z() {
      List var0 = Scramblers.INSTANCE.Q();
      return (ScramblerRange)var0.get(ThreadLocalRandom.current().nextInt(var0.size()));
   }

   private static byte q(ScramblerRange var0) {
      return (byte)ThreadLocalRandom.current().nextInt(var0.r, var0.X);
   }

   public SocketChannel q() {
      return this.c;
   }

   public Selector G() {
      return this.t;
   }

   public void k(OutgoingPacket var1) {
      this.e.add(var1);
   }

   public void m() {
      if (!this.X) {
         this.X = true;
         this.c.keyFor(this.t).interestOps(4);
         this.t.wakeup();
      }
   }
}
