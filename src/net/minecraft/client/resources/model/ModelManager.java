package net.minecraft.client.resources.model;

import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.IRegistry;

public class ModelManager implements IResourceManagerReloadListener {
   private IRegistry<ModelResourceLocation, IBakedModel> modelRegistry;
   private final TextureMap texMap;
   private final BlockModelShapes modelProvider;
   private IBakedModel defaultModel;
   private static final long a = 115215768884007L;

   public ModelManager(TextureMap var1) {
      this.texMap = var1;
      this.modelProvider = new BlockModelShapes(this);
   }

   @Override
   public void onResourceManagerReload(IResourceManager var1) {
      ModelBakery var2 = new ModelBakery(var1, this.texMap, this.modelProvider);
      this.modelRegistry = var2.setupModelRegistry();
      this.defaultModel = this.modelRegistry.getObject(ModelBakery.MODEL_MISSING);
      this.modelProvider.reloadModels();
   }

   public IBakedModel getModel(ModelResourceLocation var1) {
      if (var1 == null) {
         return this.defaultModel;
      }

      IBakedModel var5 = this.modelRegistry.getObject(var1);
      return var5 == null ? this.defaultModel : var5;
   }

   public IBakedModel getMissingModel() {
      return this.defaultModel;
   }

   public TextureMap getTextureMap() {
      return this.texMap;
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.modelProvider;
   }
}
