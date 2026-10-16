package bypass.render;

import java.awt.image.BufferedImage;
import net.minecraft.util.ResourceLocation;

public class ImageTexture {
   public ResourceLocation location;
   public BufferedImage image;
   public int width;
   public int height;

   public ImageTexture() {
   }

   public ImageTexture(BufferedImage bufferedImage) {
      this.image = bufferedImage;
   }
}
