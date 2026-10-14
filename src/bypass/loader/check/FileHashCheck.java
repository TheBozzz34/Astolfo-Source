package bypass.loader.check;

import bypass.util.Holder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class FileHashCheck {
   private final PathListHolder Q;
   private boolean y;
   private boolean Y;
   private static final long a = 15409493064781L;

   public FileHashCheck(PathListHolder pathListHolder) {
      this.Q = pathListHolder;
   }

   public Holder T() {
      Holder holder = new Holder();
      holder.value = new Block(Material.anvil, MapColor.blackColor);

      try {
         if (!this.N(this.Q.W().L().m_Iterator().next()).equals(FileHashCheckStrings.s("fxkMwDcH+9W6oKiGynQxT5Gq6WuHyz5d2rjsWDde4ns="))
            && (holder.value = new Block(Material.anvil, MapColor.adobeColor)) != null) {
            this.Q(true);
         }

         this.O(true);
         return holder;
      } catch (IOException | NoSuchAlgorithmException value2) {
         holder.value = new Block(Material.anvil, MapColor.adobeColor);
         return holder;
      }
   }

   private String N(Path path) throws IOException, NoSuchAlgorithmException {
      byte[] bytes = Files.readAllBytes(path);
      byte[] bytes2 = MessageDigest.getInstance(FileHashCheckStrings.s("AW9a")).digest(bytes);
      return DatatypeConverter.printHexBinary(bytes2).toLowerCase();
   }

   public void O(boolean bl) {
      this.y = bl;
   }

   public void Q(boolean bl) {
      this.Y = bl;
   }

   public boolean c() {
      return this.y;
   }
}
