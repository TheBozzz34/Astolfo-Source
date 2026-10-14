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

   public ModelManager(TextureMap textures) {
      this.texMap = textures;
      this.modelProvider = new BlockModelShapes(this);
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      ModelBakery modelBakery = new ModelBakery(resourceManager, this.texMap, this.modelProvider);
      this.modelRegistry = modelBakery.setupModelRegistry();
      this.defaultModel = this.modelRegistry.getObject(ModelBakery.MODEL_MISSING);
      this.modelProvider.reloadModels();
   }

   public IBakedModel getModel(ModelResourceLocation modelLocation) {
      if (modelLocation == null) {
         return this.defaultModel;
      }

      IBakedModel iBakedModel = this.modelRegistry.getObject(modelLocation);
      return iBakedModel == null ? this.defaultModel : iBakedModel;
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
