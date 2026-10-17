package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.util.ResourceLocation;

public class CapeImageBuffer implements IImageBuffer {
   public ImageBufferDownload imageBufferDownload;
   public final WeakReference<AbstractClientPlayer> playerRef;
   public final ResourceLocation resourceLocation;

   public CapeImageBuffer(AbstractClientPlayer player, ResourceLocation resourceLocation) {
      this.playerRef = new WeakReference<>(player);
      this.resourceLocation = resourceLocation;
      this.imageBufferDownload = new ImageBufferDownload();
   }

   @Override
   public BufferedImage parseUserSkin(BufferedImage image) {
      return CapeUtils.parseCape(image);
   }

   @Override
   public void skinAvailable() {
      AbstractClientPlayer abstractClientPlayer = this.playerRef.get();
      if (abstractClientPlayer != null) {
         abstractClientPlayer.setLocationOfCape(this.resourceLocation);
      }
   }
}
