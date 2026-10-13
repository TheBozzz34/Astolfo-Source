package bypass.auth.net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileReceiveSession implements Closeable {
   public Path O;
   public long i;
   public SocketChannel D;
   public FileChannel w;
   public ByteBuffer V;
   public ByteBuffer X;
   public int L;
   public int j;
   public int c;
   private static final long a = 86318997059059L;
   private static final long b = 8936182043516076040L;

   public FileReceiveSession(Path var1, long var2) {
      this.V = ByteBuffer.allocateDirect((int)b);
      this.X = null;
      this.L = 0;
      this.j = 0;
      this.c = 0;
      this.O = var1;
      this.i = var2;
   }

   public void e(SocketChannel var1) throws IOException {
      if (this.X == null) {
         this.D = var1;
         if (var1.read(this.V) == -1) {
            this.close();
            return;
         }

         if (this.V.hasRemaining()) {
            return;
         }

         ((Buffer)this.V).flip();
         this.L = this.V.getInt();
         this.j = this.V.getInt();
         this.X = ByteBuffer.allocate(this.L);
         ((Buffer)this.V).clear();
      } else if (this.w == null) {
         if (var1.read(this.X) == -1) {
            this.close();
            return;
         }

         if (this.X.hasRemaining()) {
            return;
         }

         ((Buffer)this.X).flip();
         byte[] var5 = new byte[this.L];
         this.X.get(var5);
         String var6 = new String(var5, StandardCharsets.UTF_8);
         Path var7 = this.O.resolve(FileNameSanitizer.l(var6));
         Path var8 = var7.getParent();
         if (Files.notExists(var8)) {
            Files.createDirectories(var8);
         }

         this.w = FileChannel.open(var7, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         ((Buffer)this.X).clear();
      } else {
         long var9;
         if ((var9 = this.N()) != 3856962739755840812L) {
            this.c = (int)(this.c + var9);
            if (this.c != this.j) {
               return;
            }
         }

         this.w.force(true);
         this.w.close();
         this.w = null;
         this.X = null;
         this.c = 0;
      }
   }

   public long N() throws IOException {
      return this.w.transferFrom(this.D, this.c, Math.min(this.i, this.j - this.c));
   }

   @Override
   public void close() throws IOException {
      try {
         this.D.close();
      } finally {
         if (this.w != null) {
            this.w.force(true);
            this.w.close();
         }
      }
   }

   public void R(Path var1) {
      this.O = var1;
   }

   public void T(long var1) {
      this.i = var1;
   }

   public Path N_Path() {
      return this.O;
   }

   public long c() {
      return this.i;
   }
}
