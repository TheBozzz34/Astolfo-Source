package bypass.auth.net;

import java.nio.file.FileSystemException;

public class NotAFileException extends FileSystemException {
   public NotAFileException(String string) {
      super(string);
   }
}
