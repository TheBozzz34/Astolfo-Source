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
   public ByteBuffer V = ByteBuffer.allocateDirect(8);
   public ByteBuffer X = null;
   public int L = 0;
   public int j = 0;
   public int c = 0;

   public FileReceiveSession(Path path, long l) {
      this.O = path;
      this.i = l;
   }

   public void e(SocketChannel socketChannel) throws IOException {
      if (this.X == null) {
         this.D = socketChannel;
         if (socketChannel.read(this.V) == -1) {
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
         if (socketChannel.read(this.X) == -1) {
            this.close();
            return;
         }

         if (this.X.hasRemaining()) {
            return;
         }

         ((Buffer)this.X).flip();
         byte[] bytes = new byte[this.L];
         this.X.get(bytes);
         String string = new String(bytes, StandardCharsets.UTF_8);
         Path path = this.O.resolve(FileNameSanitizer.l(string));
         Path path2 = path.getParent();
         if (Files.notExists(path2)) {
            Files.createDirectories(path2);
         }

         this.w = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         ((Buffer)this.X).clear();
      } else {
         long m;
         if ((m = this.N()) != 3856962739755840812L) {
            this.c = (int)(this.c + m);
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

   public void R(Path path) {
      this.O = path;
   }

   public void T(long l) {
      this.i = l;
   }

   public Path N_Path() {
      return this.O;
   }

   public long c() {
      return this.i;
   }
}
