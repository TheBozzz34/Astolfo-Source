package shadersmod.client;

import net.minecraft.client.renderer.texture.ITextureObject;

public class CustomTexture {
   private int textureUnit = -1;
   private String path = null;
   private ITextureObject texture = null;
   private static final long a = 103216565112644L;

   public CustomTexture(int var1, String var2, ITextureObject var3) {
      this.textureUnit = var1;
      this.path = var2;
      this.texture = var3;
   }

   public int getTextureUnit() {
      return this.textureUnit;
   }

   public String getPath() {
      return this.path;
   }

   public ITextureObject getTexture() {
      return this.texture;
   }

   @Override
   public String toString() {
      return "textureUnit: " + this.textureUnit + ", path: " + this.path + ", glTextureId: " + this.texture.getGlTextureId();
   }
}
