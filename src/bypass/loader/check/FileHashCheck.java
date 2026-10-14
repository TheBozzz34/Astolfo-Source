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

   public FileHashCheck(PathListHolder pathListHolder) {
      this.Q = pathListHolder;
   }

   public Holder T() {
      Holder holder = new Holder();
      holder.value = new Block(Material.anvil, MapColor.blackColor);

      try {
         if (!this.N(this.Q.W().L().m_Iterator().next()).equals("32ce601ed8eb54b2a89cd30d3cc3157f")
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
      byte[] bytes2 = MessageDigest.getInstance("MD5").digest(bytes);
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
