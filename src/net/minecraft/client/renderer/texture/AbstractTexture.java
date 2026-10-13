package net.minecraft.client.renderer.texture;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import shadersmod.client.MultiTexID;
import shadersmod.client.ShadersTex;

public abstract class AbstractTexture implements ITextureObject {
   public int glTextureId = -1;
   public boolean blur;
   public boolean mipmap;
   public boolean blurLast;
   public boolean mipmapLast;
   public MultiTexID multiTex;
   private static final long g = 61562381103383L;

   public void setBlurMipmapDirect(boolean var1, boolean var2) {
      this.blur = var1;
      this.mipmap = var2;
      int var8;
      short var9;
      if (var1) {
         var8 = var2 ? 9987 : 9729;
         var9 = 9729;
      } else {
         var8 = var2 ? 9986 : 9728;
         var9 = 9728;
      }

      GlStateManager.bindTexture(this.getGlTextureId());
      GL11.glTexParameteri(3553, 10241, var8);
      GL11.glTexParameteri(3553, 10240, var9);
   }

   @Override
   public void setBlurMipmap(boolean var1, boolean var2) {
      this.blurLast = this.blur;
      this.mipmapLast = this.mipmap;
      this.setBlurMipmapDirect(var1, var2);
   }

   @Override
   public void restoreLastBlurMipmap() {
      this.setBlurMipmapDirect(this.blurLast, this.mipmapLast);
   }

   @Override
   public int getGlTextureId() {
      if (this.glTextureId == -1) {
         this.glTextureId = TextureUtil.glGenTextures();
      }

      return this.glTextureId;
   }

   public void deleteGlTexture() {
      ShadersTex.deleteTextures(this, this.glTextureId);
      if (this.glTextureId != -1) {
         TextureUtil.deleteTexture(this.glTextureId);
         this.glTextureId = -1;
      }
   }

   @Override
   public MultiTexID getMultiTexID() {
      return ShadersTex.getMultiTexID(this);
   }
}
