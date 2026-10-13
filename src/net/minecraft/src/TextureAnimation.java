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
   private static final long a = 91556960218487L;

   public TextureAnimation(String var1, byte[] var2, String var3, ResourceLocation var4, int var5, int var6, int var7, int var8, Properties var9, int var10) {
      this.srcTex = var1;
      this.dstTex = var3;
      this.dstTexLoc = var4;
      this.dstX = var5;
      this.dstY = var6;
      this.frameWidth = var7;
      this.frameHeight = var8;
      int var14 = var7 * var8 * 4;
      if (var2.length % var14 != 0) {
         Config.warn("Invalid animated texture length: " + var2.length + ", frameWidth: " + var7 + ", frameHeight: " + var8);
      }

      this.srcData = var2;
      int var15 = var2.length / var14;
      if (var9.get("tile.0") != null) {
         for (int var16 = 0; var9.get("tile." + var16) != null; var16++) {
            var15 = var16 + 1;
         }
      }

      String var24 = (String)var9.get("duration");
      int var17 = Config.parseInt(var24, var10);
      this.frames = new TextureAnimationFrame[var15];

      for (int var18 = 0; var18 < this.frames.length; var18++) {
         String var19 = (String)var9.get("tile." + var18);
         int var20 = Config.parseInt(var19, var18);
         String var21 = (String)var9.get("duration." + var18);
         int var22 = Config.parseInt(var21, var17);
         TextureAnimationFrame var23 = new TextureAnimationFrame(var20, var22);
         this.frames[var18] = var23;
      }
   }

   public boolean nextFrame() {
      if (this.frames.length <= 0) {
         return false;
      }

      if (this.activeFrame >= this.frames.length) {
         this.activeFrame = 0;
      }

      TextureAnimationFrame var4 = this.frames[this.activeFrame];
      var4.counter++;
      if (var4.counter < var4.duration) {
         return false;
      }

      var4.counter = 0;
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

      TextureAnimationFrame var4 = this.frames[this.activeFrame];
      return var4.index;
   }

   public int f() {
      return this.frames.length;
   }

   public boolean updateTexture() {
      long var1 = a ^ 5986741842575L;
      if (this.dstTextId < 0) {
         ITextureObject var4 = TextureUtils.getTexture(this.dstTexLoc);
         if (var4 == null) {
            return false;
         }

         this.dstTextId = var4.getGlTextureId();
      }

      if (this.imageData == null) {
         this.imageData = GLAllocation.createDirectByteBuffer(this.srcData.length);
         this.imageData.put(this.srcData);
         this.srcData = null;
      }

      if (!this.nextFrame()) {
         return false;
      }

      int var7 = this.frameWidth * this.frameHeight * 4;
      int var5 = this.getActiveFrameIndex();
      int var6 = var7 * var5;
      if (var6 + var7 > this.imageData.capacity()) {
         return false;
      }

      ((Buffer)this.imageData).position(var6);
      GlStateManager.bindTexture(this.dstTextId);
      long var10000 = 1455253693910378400L ^ var1;
      GL11.glTexSubImage2D(3553, 0, this.dstX, this.dstY, this.frameWidth, this.frameHeight, 6408, 5121, this.imageData);
      return true;
   }

   public String K() {
      return this.srcTex;
   }

   public String getDstTex() {
      return this.dstTex;
   }

   public ResourceLocation getDstTexLoc() {
      return this.dstTexLoc;
   }
}
