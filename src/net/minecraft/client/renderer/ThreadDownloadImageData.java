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
   private boolean textureUploaded;
   public Boolean imageFound = null;
   public boolean pipeline = false;

   public ThreadDownloadImageData(File cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation, IImageBuffer imageBufferIn) {
      super(textureResourceLocation);
      this.cacheFile = cacheFileIn;
      this.imageUrl = imageUrlIn;
      this.imageBuffer = imageBufferIn;
   }

   private void checkTextureUploaded() {
      if (!this.textureUploaded && this.bufferedImage != null) {
         this.textureUploaded = true;
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

   public void setBufferedImage(BufferedImage bufferedImageIn) {
      this.bufferedImage = bufferedImageIn;
      if (this.imageBuffer != null) {
         this.imageBuffer.skinAvailable();
      }

      this.imageFound = this.bufferedImage != null;
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      if (this.bufferedImage == null && this.textureLocation != null) {
         super.loadTexture(resourceManager);
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
            } catch (IOException ioException) {
               logger.error("Couldn't load skin " + this.cacheFile, ioException);
               this.loadTextureFromServer();
            }
         } else {
            this.loadTextureFromServer();
         }
      }
   }

   public void loadTextureFromServer() {
      this.imageThread = new Thread("Texture Downloader #" + threadDownloadCounter.incrementAndGet()) {
         @Override
         public void run() {
            HttpURLConnection httpURLConnection = null;
            ThreadDownloadImageData.logger
               .debug("Downloading http texture from {} to {}", ThreadDownloadImageData.this.imageUrl, ThreadDownloadImageData.this.cacheFile);
            if (ThreadDownloadImageData.accessShouldPipeline(ThreadDownloadImageData.this)) {
               ThreadDownloadImageData.accessLoadPipelined(ThreadDownloadImageData.this);
            } else {
               try {
                  httpURLConnection = (HttpURLConnection)new URL(ThreadDownloadImageData.this.imageUrl).openConnection(Minecraft.getMinecraft().getProxy());
                  httpURLConnection.setDoInput(true);
                  httpURLConnection.setDoOutput(false);
                  httpURLConnection.setRequestMethod("GET");
                  httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:95.0) Gecko/20100101 Firefox/95.0");
                  httpURLConnection.setRequestProperty("Accept", "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
                  httpURLConnection.connect();
                  if (httpURLConnection.getResponseCode() / 100 == 2) {
                     BufferedImage bufferedImage;
                     if (ThreadDownloadImageData.this.cacheFile != null) {
                        FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), ThreadDownloadImageData.this.cacheFile);
                        bufferedImage = ImageIO.read(ThreadDownloadImageData.this.cacheFile);
                     } else {
                        bufferedImage = TextureUtil.readBufferedImage(httpURLConnection.getInputStream());
                     }

                     if (ThreadDownloadImageData.this.imageBuffer != null) {
                        bufferedImage = ThreadDownloadImageData.this.imageBuffer.parseUserSkin(bufferedImage);
                     }

                     ThreadDownloadImageData.this.setBufferedImage(bufferedImage);
                     return;
                  }

                  if (httpURLConnection.getErrorStream() != null) {
                     Config.readAll(httpURLConnection.getErrorStream());
                  }

                  return;
               } catch (Exception exception) {
                  ThreadDownloadImageData.logger.error("Couldn't download http texture: " + exception.getClass().getName() + ": " + exception.getMessage());
               } finally {
                  if (httpURLConnection != null) {
                     httpURLConnection.disconnect();
                  }

                  ThreadDownloadImageData.this.imageFound = ThreadDownloadImageData.accessBufferedImage(ThreadDownloadImageData.this) != null;
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

      Proxy proxy = Minecraft.getMinecraft().getProxy();
      return proxy.type() != Type.DIRECT && proxy.type() != Type.SOCKS ? false : this.imageUrl.startsWith("http://");
   }

   private void loadPipelined() {
      try {
         HttpRequest httpRequest = HttpPipeline.makeRequest(this.imageUrl, Minecraft.getMinecraft().getProxy());
         HttpResponse httpResponse = HttpPipeline.executeRequest(httpRequest);
         if (httpResponse.getStatus() / 100 == 2) {
            byte[] bytes = httpResponse.getBody();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            BufferedImage bufferedImage2;
            if (this.cacheFile != null) {
               FileUtils.copyInputStreamToFile(byteArrayInputStream, this.cacheFile);
               bufferedImage2 = ImageIO.read(this.cacheFile);
            } else {
               bufferedImage2 = TextureUtil.readBufferedImage(byteArrayInputStream);
            }

            if (this.imageBuffer != null) {
               bufferedImage2 = this.imageBuffer.parseUserSkin(bufferedImage2);
            }

            this.setBufferedImage(bufferedImage2);
            return;
         }
      } catch (Exception exception) {
         logger.error("Couldn't download http texture: " + exception.getClass().getName() + ": " + exception.getMessage());
         return;
      } finally {
         this.imageFound = this.bufferedImage != null;
      }
   }

   public BufferedImage getBufferedImage() {
      return this.bufferedImage;
   }

   public static boolean accessShouldPipeline(ThreadDownloadImageData threadDownloadImageData) {
      return threadDownloadImageData.shouldPipeline();
   }

   public static void accessLoadPipelined(ThreadDownloadImageData threadDownloadImageData) {
      threadDownloadImageData.loadPipelined();
   }

   public static BufferedImage accessBufferedImage(ThreadDownloadImageData threadDownloadImageData) {
      return threadDownloadImageData.bufferedImage;
   }
}
