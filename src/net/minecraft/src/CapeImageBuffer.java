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

   public CapeImageBuffer(AbstractClientPlayer var1, ResourceLocation var2) {
      this.m = new WeakReference<>(var1);
      this.E = var2;
      this.r = new ImageBufferDownload();
   }

   @Override
   public BufferedImage parseUserSkin(BufferedImage var1) {
      return CapeUtils.parseCape(var1);
   }

   @Override
   public void skinAvailable() {
      AbstractClientPlayer var4 = this.m.get();
      if (var4 != null) {
         var4.setLocationOfCape(this.E);
      }
   }
}
