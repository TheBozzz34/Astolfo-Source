package net.minecraft.world.gen.layer;

public class GenLayerFuzzyZoom extends GenLayerZoom {
   public GenLayerFuzzyZoom(long l, GenLayer genLayer) {
      super(l, genLayer);
   }

   @Override
   public int selectModeOrRandom(int i, int j, int k, int l) {
      return this.selectRandom(i, j, k, l);
   }
}
