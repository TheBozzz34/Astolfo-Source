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
   public void open(SocketChannel var1) throws IOException {
      var1.configureBlocking(true);
      this.b = var1;
   }

   public void v(Path var1, long var2) throws IOException {
      this.y(var1, var1.getFileName().toString(), var2);
   }

   public void y(Path var1, String var2, long var3) throws IOException {
      if (Files.isDirectory(var1)) {
         throw new NotAFileException(var1.toString());
      }

      try (FileChannel var8 = FileChannel.open(var1, StandardOpenOption.READ)) {
         int var10 = var2.length();
         long var11 = var8.size();
         ((Buffer)this.O).clear();
         this.O.putInt(var10);
         this.O.putInt((int)var11);
         ((Buffer)this.O).flip();
         this.b.write(this.O);
         ByteBuffer var13 = ByteBuffer.allocate(var10);
         var13.put(var2.getBytes(StandardCharsets.UTF_8));
         ((Buffer)var13).flip();
         this.b.write(var13);
         long var14 = 0L;

         while (var14 < var11) {
            var14 += var8.transferTo(var14, var3, this.b);
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.b.close();
   }
}
