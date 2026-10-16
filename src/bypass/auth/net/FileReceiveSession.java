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
   public Path directory;
   public long chunkSize;
   public SocketChannel socketChannel;
   public FileChannel fileChannel;
   public ByteBuffer headerBuffer = ByteBuffer.allocateDirect(8);
   public ByteBuffer nameBuffer = null;
   public int nameLength = 0;
   public int fileSize = 0;
   public int bytesReceived = 0;

   public FileReceiveSession(Path path, long l) {
      this.directory = path;
      this.chunkSize = l;
   }

   public void handleRead(SocketChannel socketChannel) throws IOException {
      if (this.nameBuffer == null) {
         this.socketChannel = socketChannel;
         if (socketChannel.read(this.headerBuffer) == -1) {
            this.close();
            return;
         }

         if (this.headerBuffer.hasRemaining()) {
            return;
         }

         ((Buffer)this.headerBuffer).flip();
         this.nameLength = this.headerBuffer.getInt();
         this.fileSize = this.headerBuffer.getInt();
         this.nameBuffer = ByteBuffer.allocate(this.nameLength);
         ((Buffer)this.headerBuffer).clear();
      } else if (this.fileChannel == null) {
         if (socketChannel.read(this.nameBuffer) == -1) {
            this.close();
            return;
         }

         if (this.nameBuffer.hasRemaining()) {
            return;
         }

         ((Buffer)this.nameBuffer).flip();
         byte[] bytes = new byte[this.nameLength];
         this.nameBuffer.get(bytes);
         String string = new String(bytes, StandardCharsets.UTF_8);
         Path path = this.directory.resolve(FileNameSanitizer.sanitize(string));
         Path path2 = path.getParent();
         if (Files.notExists(path2)) {
            Files.createDirectories(path2);
         }

         this.fileChannel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         ((Buffer)this.nameBuffer).clear();
      } else {
         long l;
         if ((l = this.transferChunk()) != 3856962739755840812L) {
            this.bytesReceived = (int)(this.bytesReceived + l);
            if (this.bytesReceived != this.fileSize) {
               return;
            }
         }

         this.fileChannel.force(true);
         this.fileChannel.close();
         this.fileChannel = null;
         this.nameBuffer = null;
         this.bytesReceived = 0;
      }
   }

   public long transferChunk() throws IOException {
      return this.fileChannel.transferFrom(this.socketChannel, this.bytesReceived, Math.min(this.chunkSize, this.fileSize - this.bytesReceived));
   }

   @Override
   public void close() throws IOException {
      try {
         this.socketChannel.close();
      } finally {
         if (this.fileChannel != null) {
            this.fileChannel.force(true);
            this.fileChannel.close();
         }
      }
   }

   public void setDirectory(Path path) {
      this.directory = path;
   }

   public void setChunkSize(long l) {
      this.chunkSize = l;
   }

   public Path getDirectory() {
      return this.directory;
   }

   public long getChunkSize() {
      return this.chunkSize;
   }
}
