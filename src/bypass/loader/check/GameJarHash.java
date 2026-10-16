package bypass.loader.check;

import bypass.config.ConfigFiles;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

public class GameJarHash {
   private List<String> hashes;

   public GameJarHash() {
      try {
         this.hashes = new ArrayList<>(Collections.singleton(this.computeHash()));
      } catch (IOException | URISyntaxException | NoSuchAlgorithmException value) {
      }
   }

   public List<String> getHashes() {
      return this.hashes;
   }

   private String computeHash() throws IOException, NoSuchAlgorithmException, URISyntaxException {
      byte[] bytes = Files.readAllBytes(Paths.get(ConfigFiles.getGameJarPath()));
      byte[] bytes2 = MessageDigest.getInstance("MD5").digest(bytes);
      return DatatypeConverter.printHexBinary(bytes2).toLowerCase();
   }
}
