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

   public void setBlurMipmapDirect(boolean bl, boolean bl2) {
      this.blur = bl;
      this.mipmap = bl2;
      int i;
      short s;
      if (bl) {
         i = bl2 ? 9987 : 9729;
         s = 9729;
      } else {
         i = bl2 ? 9986 : 9728;
         s = 9728;
      }

      GlStateManager.bindTexture(this.getGlTextureId());
      GL11.glTexParameteri(3553, 10241, i);
      GL11.glTexParameteri(3553, 10240, s);
   }

   @Override
   public void setBlurMipmap(boolean bl, boolean bl2) {
      this.blurLast = this.blur;
      this.mipmapLast = this.mipmap;
      this.setBlurMipmapDirect(bl, bl2);
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
