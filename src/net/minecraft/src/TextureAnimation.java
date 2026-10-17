package net.minecraft.src;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Properties;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TextureAnimation {
   private String srcTex = null;
   private String dstTex = null;
   public ResourceLocation dstTexLoc = null;
   private int dstTextId = -1;
   private int dstX = 0;
   private int dstY = 0;
   private int frameWidth = 0;
   private int frameHeight = 0;
   private TextureAnimationFrame[] frames = null;
   private int activeFrame = 0;
   public byte[] srcData = null;
   private ByteBuffer imageData = null;

   public TextureAnimation(
      String texFrom,
      byte[] srcData,
      String texTo,
      ResourceLocation locTexTo,
      int dstX,
      int dstY,
      int frameWidth,
      int frameHeight,
      Properties props,
      int durDef
   ) {
      this.srcTex = texFrom;
      this.dstTex = texTo;
      this.dstTexLoc = locTexTo;
      this.dstX = dstX;
      this.dstY = dstY;
      this.frameWidth = frameWidth;
      this.frameHeight = frameHeight;
      int i = frameWidth * frameHeight * 4;
      if (srcData.length % i != 0) {
         Config.warn("Invalid animated texture length: " + srcData.length + ", frameWidth: " + frameWidth + ", frameHeight: " + frameHeight);
      }

      this.srcData = srcData;
      int j = srcData.length / i;
      if (props.get("tile.0") != null) {
         for (int k = 0; props.get("tile." + k) != null; k++) {
            j = k + 1;
         }
      }

      String string = (String)props.get("duration");
      int l = Config.parseInt(string, durDef);
      this.frames = new TextureAnimationFrame[j];

      for (int m = 0; m < this.frames.length; m++) {
         String string2 = (String)props.get("tile." + m);
         int n = Config.parseInt(string2, m);
         String string3 = (String)props.get("duration." + m);
         int i2 = Config.parseInt(string3, l);
         TextureAnimationFrame textureAnimationFrame = new TextureAnimationFrame(n, i2);
         this.frames[m] = textureAnimationFrame;
      }
   }

   public boolean nextFrame() {
      if (this.frames.length <= 0) {
         return false;
      }

      if (this.activeFrame >= this.frames.length) {
         this.activeFrame = 0;
      }

      TextureAnimationFrame textureAnimationFrame = this.frames[this.activeFrame];
      textureAnimationFrame.counter++;
      if (textureAnimationFrame.counter < textureAnimationFrame.duration) {
         return false;
      }

      textureAnimationFrame.counter = 0;
      this.activeFrame++;
      if (this.activeFrame >= this.frames.length) {
         this.activeFrame = 0;
      }

      return true;
   }

   public int getActiveFrameIndex() {
      if (this.frames.length <= 0) {
         return 0;
      }

      if (this.activeFrame >= this.frames.length) {
         this.activeFrame = 0;
      }

      TextureAnimationFrame textureAnimationFrame = this.frames[this.activeFrame];
      return textureAnimationFrame.index;
   }

   public int getFrameCount() {
      return this.frames.length;
   }

   public boolean updateTexture() {
      if (this.dstTextId < 0) {
         ITextureObject iTextureObject = TextureUtils.getTexture(this.dstTexLoc);
         if (iTextureObject == null) {
            return false;
         }

         this.dstTextId = iTextureObject.getGlTextureId();
      }

      if (this.imageData == null) {
         this.imageData = GLAllocation.createDirectByteBuffer(this.srcData.length);
         this.imageData.put(this.srcData);
         this.srcData = null;
      }

      if (!this.nextFrame()) {
         return false;
      }

      int i = this.frameWidth * this.frameHeight * 4;
      int j = this.getActiveFrameIndex();
      int k = i * j;
      if (k + i > this.imageData.capacity()) {
         return false;
      }

      ((Buffer)this.imageData).position(k);
      GlStateManager.bindTexture(this.dstTextId);
      GL11.glTexSubImage2D(3553, 0, this.dstX, this.dstY, this.frameWidth, this.frameHeight, 6408, 5121, this.imageData);
      return true;
   }

   public String getSrcTex() {
      return this.srcTex;
   }

   public String getDstTex() {
      return this.dstTex;
   }

   public ResourceLocation getDstTexLoc() {
      return this.dstTexLoc;
   }
}
