package net.minecraft.client.resources.model;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;

public class WeightedBakedModel implements IBakedModel {
   private final int totalWeight;
   private final List<WeightedBakedModel.MyWeighedRandomItem> models;
   private final IBakedModel baseModel;
   private static final long a = 4419971028597L;
   private static final long b = 2305898435766648848L;

   public WeightedBakedModel(List<WeightedBakedModel.MyWeighedRandomItem> list) {
      this.models = list;
      this.totalWeight = WeightedRandom.getTotalWeight(list);
      this.baseModel = ((WeightedBakedModel.MyWeighedRandomItem)list.get(0)).model;
   }

   @Override
   public List<BakedQuad> getFaceQuads(EnumFacing facing) {
      return this.baseModel.getFaceQuads(facing);
   }

   @Override
   public List<BakedQuad> getGeneralQuads() {
      return this.baseModel.getGeneralQuads();
   }

   @Override
   public boolean isAmbientOcclusion() {
      return this.baseModel.isAmbientOcclusion();
   }

   @Override
   public boolean isGui3d() {
      return this.baseModel.isGui3d();
   }

   @Override
   public boolean isBuiltInRenderer() {
      return this.baseModel.isBuiltInRenderer();
   }

   @Override
   public TextureAtlasSprite getParticleTexture() {
      return this.baseModel.getParticleTexture();
   }

   @Override
   public ItemCameraTransforms getItemCameraTransforms() {
      return this.baseModel.getItemCameraTransforms();
   }

   public IBakedModel getAlternativeModel(long l) {
      return WeightedRandom.getRandomItem(this.models, Math.abs((int)l >> (int)b) % this.totalWeight).model;
   }

   public static class Builder {
      private List<WeightedBakedModel.MyWeighedRandomItem> listItems = Lists.newArrayList();

      public WeightedBakedModel.Builder add(IBakedModel iBakedModel, int i) {
         this.listItems.add(new WeightedBakedModel.MyWeighedRandomItem(iBakedModel, i));
         return this;
      }

      public WeightedBakedModel build() {
         Collections.sort(this.listItems);
         return new WeightedBakedModel(this.listItems);
      }

      public IBakedModel first() {
         return this.listItems.get(0).model;
      }
   }

   public static class MyWeighedRandomItem extends WeightedRandom.Item implements Comparable<WeightedBakedModel.MyWeighedRandomItem> {
      public final IBakedModel model;
      private static final long a = 102096866608081L;
      private static final long e = -2711945962485448579L;

      public MyWeighedRandomItem(IBakedModel iBakedModel, int i) {
         super(i);
         this.model = iBakedModel;
      }

      public int compareTo(WeightedBakedModel.MyWeighedRandomItem myWeighedRandomItem) {
         return ComparisonChain.start()
            .compare(myWeighedRandomItem.itemWeight, this.itemWeight)
            .compare(this.getCountQuads(), myWeighedRandomItem.getCountQuads())
            .result();
      }

      public int getCountQuads() {
         int i = this.model.getGeneralQuads().size();

         for (EnumFacing enumFacing : EnumFacing.values()) {
            i += this.model.getFaceQuads(enumFacing).size();
         }

         return i;
      }

      @Override
      public String toString() {
         return "MyWeighedRandomItem{weight=" + this.itemWeight + ", model=" + this.model + (int)e;
      }
   }
}
