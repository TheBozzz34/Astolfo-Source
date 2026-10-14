package net.minecraft.client.resources.model;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class SimpleBakedModel implements IBakedModel {
   public final List<BakedQuad> generalQuads;
   public final List<List<BakedQuad>> faceQuads;
   public final boolean ambientOcclusion;
   public final boolean gui3d;
   public final TextureAtlasSprite texture;
   public final ItemCameraTransforms cameraTransforms;

   public SimpleBakedModel(
      List<BakedQuad> generalQuadsIn,
      List<List<BakedQuad>> faceQuadsIn,
      boolean ambientOcclusionIn,
      boolean gui3dIn,
      TextureAtlasSprite textureIn,
      ItemCameraTransforms cameraTransformsIn
   ) {
      this.generalQuads = generalQuadsIn;
      this.faceQuads = faceQuadsIn;
      this.ambientOcclusion = ambientOcclusionIn;
      this.gui3d = gui3dIn;
      this.texture = textureIn;
      this.cameraTransforms = cameraTransformsIn;
   }

   @Override
   public List<BakedQuad> getFaceQuads(EnumFacing facing) {
      return this.faceQuads.get(facing.ordinal());
   }

   @Override
   public List<BakedQuad> getGeneralQuads() {
      return this.generalQuads;
   }

   @Override
   public boolean isAmbientOcclusion() {
      return this.ambientOcclusion;
   }

   @Override
   public boolean isGui3d() {
      return this.gui3d;
   }

   @Override
   public boolean isBuiltInRenderer() {
      return false;
   }

   @Override
   public TextureAtlasSprite getParticleTexture() {
      return this.texture;
   }

   @Override
   public ItemCameraTransforms getItemCameraTransforms() {
      return this.cameraTransforms;
   }

   public static class Builder {
      private final List<BakedQuad> builderGeneralQuads = Lists.newArrayList();
      private final List<List<BakedQuad>> builderFaceQuads = Lists.newArrayListWithCapacity(6);
      private final boolean builderAmbientOcclusion;
      private TextureAtlasSprite builderTexture;
      private boolean builderGui3d;
      private ItemCameraTransforms builderCameraTransforms;

      public Builder(ModelBlock model) {
         this(model.isAmbientOcclusion(), model.isGui3d(), model.getAllTransforms());
      }

      public Builder(IBakedModel bakedModel, TextureAtlasSprite textureIn) {
         this(bakedModel.isAmbientOcclusion(), bakedModel.isGui3d(), bakedModel.getItemCameraTransforms());
         this.builderTexture = bakedModel.getParticleTexture();

         for (EnumFacing enumFacing : EnumFacing.values()) {
            this.addFaceBreakingFours(bakedModel, textureIn, enumFacing);
         }

         this.addGeneralBreakingFours(bakedModel, textureIn);
      }

      private void addFaceBreakingFours(IBakedModel bakedModel, TextureAtlasSprite textureIn, EnumFacing facing) {
         for (BakedQuad bakedQuad : bakedModel.getFaceQuads(facing)) {
            this.addFaceQuad(facing, new BreakingFour(bakedQuad, textureIn));
         }
      }

      private void addGeneralBreakingFours(IBakedModel iBakedModel, TextureAtlasSprite textureIn) {
         for (BakedQuad bakedQuad : iBakedModel.getGeneralQuads()) {
            this.addGeneralQuad(new BreakingFour(bakedQuad, textureIn));
         }
      }

      private Builder(boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            this.builderFaceQuads.add(Lists.newArrayList());
         }

         this.builderAmbientOcclusion = ambientOcclusionIn;
         this.builderGui3d = gui3dIn;
         this.builderCameraTransforms = cameraTransformsIn;
      }

      public SimpleBakedModel.Builder addFaceQuad(EnumFacing facing, BakedQuad quad) {
         this.builderFaceQuads.get(facing.ordinal()).add(quad);
         return this;
      }

      public SimpleBakedModel.Builder addGeneralQuad(BakedQuad quad) {
         this.builderGeneralQuads.add(quad);
         return this;
      }

      public SimpleBakedModel.Builder setTexture(TextureAtlasSprite textureIn) {
         this.builderTexture = textureIn;
         return this;
      }

      public IBakedModel makeBakedModel() {
         if (this.builderTexture == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(
               this.builderGeneralQuads,
               this.builderFaceQuads,
               this.builderAmbientOcclusion,
               this.builderGui3d,
               this.builderTexture,
               this.builderCameraTransforms
            );
         }
      }
   }
}
