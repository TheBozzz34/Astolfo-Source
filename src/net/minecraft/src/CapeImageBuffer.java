package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer implements IImageBuffer {
   public ImageBufferDownload r;
   public final WeakReference<AbstractClientPlayer> m;
   public final ResourceLocation E;
   private static final long a = 76521857163960L;

   public CapeImageBuffer(AbstractClientPlayer player, ResourceLocation resourceLocation) {
      this.m = new WeakReference<>(player);
      this.E = resourceLocation;
      this.r = new ImageBufferDownload();
   }

   @Override
   public BufferedImage parseUserSkin(BufferedImage image) {
      return CapeUtils.parseCape(image);
   }

   @Override
   public void skinAvailable() {
      AbstractClientPlayer abstractClientPlayer = this.m.get();
      if (abstractClientPlayer != null) {
         abstractClientPlayer.setLocationOfCape(this.E);
      }
   }
}
