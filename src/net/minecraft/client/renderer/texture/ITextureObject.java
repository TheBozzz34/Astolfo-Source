package net.minecraft.client.renderer.texture;

import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;
import shadersmod.client.MultiTexID;

public interface ITextureObject {
   void setBlurMipmap(boolean bl, boolean bl2);

   void restoreLastBlurMipmap();

   void loadTexture(IResourceManager resourceManager) throws IOException;

   int getGlTextureId();

   MultiTexID getMultiTexID();
}
