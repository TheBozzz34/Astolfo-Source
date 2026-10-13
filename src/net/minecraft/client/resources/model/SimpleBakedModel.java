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

   public SimpleBakedModel(List<BakedQuad> var1, List<List<BakedQuad>> var2, boolean var3, boolean var4, TextureAtlasSprite var5, ItemCameraTransforms var6) {
      this.generalQuads = var1;
      this.faceQuads = var2;
      this.ambientOcclusion = var3;
      this.gui3d = var4;
      this.texture = var5;
      this.cameraTransforms = var6;
   }

   @Override
   public List<BakedQuad> getFaceQuads(EnumFacing var1) {
      return this.faceQuads.get(var1.ordinal());
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
      private final List<List<BakedQuad>> builderFaceQuads;
      private final boolean builderAmbientOcclusion;
      private TextureAtlasSprite builderTexture;
      private boolean builderGui3d;
      private ItemCameraTransforms builderCameraTransforms;
      private static final long a = 57892787859419L;
      private static final long c = -1890038850101706746L;

      public Builder(ModelBlock var1) {
         this(var1.isAmbientOcclusion(), var1.isGui3d(), var1.getAllTransforms());
      }

      public Builder(IBakedModel var1, TextureAtlasSprite var2) {
         this(var1.isAmbientOcclusion(), var1.isGui3d(), var1.getItemCameraTransforms());
         this.builderTexture = var1.getParticleTexture();

         for (EnumFacing var9 : EnumFacing.values()) {
            this.addFaceBreakingFours(var1, var2, var9);
         }

         this.addGeneralBreakingFours(var1, var2);
      }

      private void addFaceBreakingFours(IBakedModel var1, TextureAtlasSprite var2, EnumFacing var3) {
         for (BakedQuad var8 : var1.getFaceQuads(var3)) {
            this.addFaceQuad(var3, new BreakingFour(var8, var2));
         }
      }

      private void addGeneralBreakingFours(IBakedModel var1, TextureAtlasSprite var2) {
         for (BakedQuad var7 : var1.getGeneralQuads()) {
            this.addGeneralQuad(new BreakingFour(var7, var2));
         }
      }

      private Builder(boolean var1, boolean var2, ItemCameraTransforms var3) {
         this.builderFaceQuads = Lists.newArrayListWithCapacity((int)c);

         for (EnumFacing var10 : EnumFacing.values()) {
            this.builderFaceQuads.add(Lists.newArrayList());
         }

         this.builderAmbientOcclusion = var1;
         this.builderGui3d = var2;
         this.builderCameraTransforms = var3;
      }

      public SimpleBakedModel.Builder addFaceQuad(EnumFacing var1, BakedQuad var2) {
         this.builderFaceQuads.get(var1.ordinal()).add(var2);
         return this;
      }

      public SimpleBakedModel.Builder addGeneralQuad(BakedQuad var1) {
         this.builderGeneralQuads.add(var1);
         return this;
      }

      public SimpleBakedModel.Builder setTexture(TextureAtlasSprite var1) {
         this.builderTexture = var1;
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
