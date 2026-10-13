package bypass.auth.net;

import java.nio.file.FileSystemException;

public class NotAFileException extends FileSystemException {
   public NotAFileException(String var1) {
      super(var1);
   }
}
