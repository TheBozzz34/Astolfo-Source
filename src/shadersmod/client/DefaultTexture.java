package shadersmod.client;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResourceManager;

public class DefaultTexture extends AbstractTexture {
   public DefaultTexture() {
      this.loadTexture((IResourceManager)null);
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) {
      int[] ints = ShadersTex.createAIntImage(1, -1);
      ShadersTex.setupTexture(this.getMultiTexID(), ints, 1, 1, false, false);
   }
}
