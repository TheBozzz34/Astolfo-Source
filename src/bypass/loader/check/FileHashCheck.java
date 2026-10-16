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
   private final PathListHolder pathListHolder;
   private boolean flag1;
   private boolean flag2;

   public FileHashCheck(PathListHolder pathListHolder) {
      this.pathListHolder = pathListHolder;
   }

   public Holder checkFileHash() {
      Holder holder = new Holder();
      holder.value = new Block(Material.anvil, MapColor.blackColor);

      try {
         if (!this.hashFile(this.pathListHolder.getPathContainer().getOwnedContainer().m_Iterator().next()).equals("32ce601ed8eb54b2a89cd30d3cc3157f")
            && (holder.value = new Block(Material.anvil, MapColor.adobeColor)) != null) {
            this.setFlag2(true);
         }

         this.setFlag1(true);
         return holder;
      } catch (IOException | NoSuchAlgorithmException value2) {
         holder.value = new Block(Material.anvil, MapColor.adobeColor);
         return holder;
      }
   }

   private String hashFile(Path path) throws IOException, NoSuchAlgorithmException {
      byte[] bytes = Files.readAllBytes(path);
      byte[] bytes2 = MessageDigest.getInstance("MD5").digest(bytes);
      return DatatypeConverter.printHexBinary(bytes2).toLowerCase();
   }

   public void setFlag1(boolean bl) {
      this.flag1 = bl;
   }

   public void setFlag2(boolean bl) {
      this.flag2 = bl;
   }

   public boolean isFlag1() {
      return this.flag1;
   }
}
