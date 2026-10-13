package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import net.minecraft.src.HttpPipeline;
import net.minecraft.src.HttpRequest;
import net.minecraft.src.HttpResponse;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadDownloadImageData extends SimpleTexture {
   private static final Logger logger = LogManager.getLogger();
   private static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);
   private final File cacheFile;
   private final String imageUrl;
   private final IImageBuffer imageBuffer;
   private BufferedImage bufferedImage;
   private Thread imageThread;
   private boolean d;
   public Boolean imageFound = null;
   public boolean pipeline = false;
   private static final long a = 7070048517755L;
   private static final long n = 7231164942802485348L;

   public ThreadDownloadImageData(File var1, String var2, ResourceLocation var3, IImageBuffer var4) {
      super(var3);
      this.cacheFile = var1;
      this.imageUrl = var2;
      this.imageBuffer = var4;
   }

   private void checkTextureUploaded() {
      if (!this.d && this.bufferedImage != null) {
         this.d = true;
         if (this.textureLocation != null) {
            this.deleteGlTexture();
         }

         TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
      }
   }

   @Override
   public int getGlTextureId() {
      this.checkTextureUploaded();
      return super.getGlTextureId();
   }

   public void setBufferedImage(BufferedImage var1) {
      this.bufferedImage = var1;
      if (this.imageBuffer != null) {
         this.imageBuffer.skinAvailable();
      }

      this.imageFound = this.bufferedImage != null;
   }

   @Override
   public void loadTexture(IResourceManager var1) throws IOException {
      if (this.bufferedImage == null && this.textureLocation != null) {
         super.loadTexture(var1);
      }

      if (this.imageThread == null) {
         if (this.cacheFile != null && this.cacheFile.isFile()) {
            logger.debug("Loading http texture from local cache ({})", new Object[]{this.cacheFile});

            try {
               this.bufferedImage = ImageIO.read(this.cacheFile);
               if (this.imageBuffer != null) {
                  this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage));
               }

               this.imageFound = this.bufferedImage != null;
            } catch (IOException var6) {
               logger.error("Couldn't load skin " + this.cacheFile, var6);
               this.loadTextureFromServer();
            }
         } else {
            this.loadTextureFromServer();
         }
      }
   }

   public void loadTextureFromServer() {
      this.imageThread = new Thread("Texture Downloader #" + threadDownloadCounter.incrementAndGet()) {
         private static final long a = 101465193557687L;
         private static final long e = -3634219743247859612L;

         @Override
         public void run() {
            HttpURLConnection var4 = null;
            ThreadDownloadImageData.logger
               .debug("Downloading http texture from {} to {}", ThreadDownloadImageData.this.imageUrl, ThreadDownloadImageData.this.cacheFile);
            if (ThreadDownloadImageData.k(ThreadDownloadImageData.this)) {
               ThreadDownloadImageData.O(ThreadDownloadImageData.this);
            } else {
               try {
                  var4 = (HttpURLConnection)new URL(ThreadDownloadImageData.this.imageUrl).openConnection(Minecraft.getMinecraft().getProxy());
                  var4.setDoInput(true);
                  var4.setDoOutput(false);
                  var4.setRequestMethod("GET");
                  var4.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:95.0) Gecko/20100101 Firefox/95.0");
                  var4.setRequestProperty("Accept", "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
                  var4.connect();
                  if (var4.getResponseCode() / (int)e == 2) {
                     BufferedImage var5;
                     if (ThreadDownloadImageData.this.cacheFile != null) {
                        FileUtils.copyInputStreamToFile(var4.getInputStream(), ThreadDownloadImageData.this.cacheFile);
                        var5 = ImageIO.read(ThreadDownloadImageData.this.cacheFile);
                     } else {
                        var5 = TextureUtil.readBufferedImage(var4.getInputStream());
                     }

                     if (ThreadDownloadImageData.this.imageBuffer != null) {
                        var5 = ThreadDownloadImageData.this.imageBuffer.parseUserSkin(var5);
                     }

                     ThreadDownloadImageData.this.setBufferedImage(var5);
                     return;
                  }

                  if (var4.getErrorStream() != null) {
                     Config.readAll(var4.getErrorStream());
                  }

                  return;
               } catch (Exception var9) {
                  ThreadDownloadImageData.logger.error("Couldn't download http texture: " + var9.getClass().getName() + ": " + var9.getMessage());
               } finally {
                  if (var4 != null) {
                     var4.disconnect();
                  }

                  ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.r(ThreadDownloadImageData.this) != null;
               }
            }
         }
      };
      this.imageThread.setDaemon(true);
      this.imageThread.start();
   }

   private boolean shouldPipeline() {
      if (!this.pipeline) {
         return false;
      }

      Proxy var4 = Minecraft.getMinecraft().getProxy();
      return var4.type() != Type.DIRECT && var4.type() != Type.SOCKS ? false : this.imageUrl.startsWith("http://");
   }

   private void loadPipelined() {
      try {
         HttpRequest var4 = HttpPipeline.makeRequest(this.imageUrl, Minecraft.getMinecraft().getProxy());
         HttpResponse var5 = HttpPipeline.executeRequest(var4);
         if (var5.getStatus() / (int)n == 2) {
            byte[] var6 = var5.getBody();
            ByteArrayInputStream var7 = new ByteArrayInputStream(var6);
            BufferedImage var8;
            if (this.cacheFile != null) {
               FileUtils.copyInputStreamToFile(var7, this.cacheFile);
               var8 = ImageIO.read(this.cacheFile);
            } else {
               var8 = TextureUtil.readBufferedImage(var7);
            }

            if (this.imageBuffer != null) {
               var8 = this.imageBuffer.parseUserSkin(var8);
            }

            this.setBufferedImage(var8);
            return;
         }
      } catch (Exception var12) {
         logger.error("Couldn't download http texture: " + var12.getClass().getName() + ": " + var12.getMessage());
         return;
      } finally {
         this.imageFound = this.bufferedImage != null;
      }
   }

   public BufferedImage l() {
      return this.bufferedImage;
   }

   public static boolean k(ThreadDownloadImageData var0) {
      return var0.shouldPipeline();
   }

   public static void O(ThreadDownloadImageData var0) {
      var0.loadPipelined();
   }

   public static BufferedImage r(ThreadDownloadImageData var0) {
      return var0.bufferedImage;
   }
}
