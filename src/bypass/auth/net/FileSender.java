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
   private final ByteBuffer headerBuffer = ByteBuffer.allocateDirect(8);
   private SocketChannel socketChannel;

   @Override
   public void open(SocketChannel socketChannel) throws IOException {
      socketChannel.configureBlocking(true);
      this.socketChannel = socketChannel;
   }

   public void sendFile(Path path, long l) throws IOException {
      this.sendNamedFile(path, path.getFileName().toString(), l);
   }

   public void sendNamedFile(Path path, String string, long l) throws IOException {
      if (Files.isDirectory(path)) {
         throw new NotAFileException(path.toString());
      }

      try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
         int i = string.length();
         long m = fileChannel.size();
         ((Buffer)this.headerBuffer).clear();
         this.headerBuffer.putInt(i);
         this.headerBuffer.putInt((int)m);
         ((Buffer)this.headerBuffer).flip();
         this.socketChannel.write(this.headerBuffer);
         ByteBuffer byteBuffer = ByteBuffer.allocate(i);
         byteBuffer.put(string.getBytes(StandardCharsets.UTF_8));
         ((Buffer)byteBuffer).flip();
         this.socketChannel.write(byteBuffer);
         long n = 0L;

         while (n < m) {
            n += fileChannel.transferTo(n, l, this.socketChannel);
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.socketChannel.close();
   }
}
