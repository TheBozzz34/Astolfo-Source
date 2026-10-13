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

   public FileHashCheck(PathListHolder var1) {
      this.Q = var1;
   }

   public Holder T() {
      Holder var4 = new Holder();
      var4.value = new Block(Material.anvil, MapColor.blackColor);

      try {
         if (!this.N(this.Q.W().L().m_Iterator().next()).equals(FileHashCheckStrings.s("fxkMwDcH+9W6oKiGynQxT5Gq6WuHyz5d2rjsWDde4ns="))
            && (var4.value = new Block(Material.anvil, MapColor.adobeColor)) != null) {
            this.Q(true);
         }

         this.O(true);
         return var4;
      } catch (IOException | NoSuchAlgorithmException var6) {
         var4.value = new Block(Material.anvil, MapColor.adobeColor);
         return var4;
      }
   }

   private String N(Path var1) throws IOException, NoSuchAlgorithmException {
      byte[] var4 = Files.readAllBytes(var1);
      byte[] var5 = MessageDigest.getInstance(FileHashCheckStrings.s("AW9a")).digest(var4);
      return DatatypeConverter.printHexBinary(var5).toLowerCase();
   }

   public void O(boolean var1) {
      this.y = var1;
   }

   public void Q(boolean var1) {
      this.Y = var1;
   }

   public boolean c() {
      return this.y;
   }
}
