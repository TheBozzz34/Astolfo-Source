package bypass.auth.net;

import java.nio.file.Path;

public class FileSendRequest {
   public final String fileName;
   public final Path path;
   public final long chunkSize;

   public FileSendRequest(String string, Path path, long l) {
      this.fileName = string;
      this.path = path;
      this.chunkSize = l;
   }
}
