package bypass.auth.net;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileSender implements Connection {
   private final ByteBuffer O;
   private SocketChannel b;
   private static final long a = 31600913175106L;
   private static final long e = -5584179601946443768L;

   public FileSender() {
      this.O = ByteBuffer.allocateDirect((int)e);
   }

   @Override
   public void open(SocketChannel socketChannel) throws IOException {
      socketChannel.configureBlocking(true);
      this.b = socketChannel;
   }

   public void v(Path path, long l) throws IOException {
      this.y(path, path.getFileName().toString(), l);
   }

   public void y(Path path, String string, long l) throws IOException {
      if (Files.isDirectory(path)) {
         throw new NotAFileException(path.toString());
      }

      try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
         int i = string.length();
         long m = fileChannel.size();
         ((Buffer)this.O).clear();
         this.O.putInt(i);
         this.O.putInt((int)m);
         ((Buffer)this.O).flip();
         this.b.write(this.O);
         ByteBuffer byteBuffer = ByteBuffer.allocate(i);
         byteBuffer.put(string.getBytes(StandardCharsets.UTF_8));
         ((Buffer)byteBuffer).flip();
         this.b.write(byteBuffer);
         long n = 0L;

         while (n < m) {
            n += fileChannel.transferTo(n, l, this.b);
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.b.close();
   }
}
