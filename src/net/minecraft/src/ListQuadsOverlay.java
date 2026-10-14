package net.minecraft.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.init.Blocks;

public class ListQuadsOverlay {
   private List<BakedQuad> c = new ArrayList<>();
   private List<IBlockState> o = new ArrayList<>();
   private List<BakedQuad> Y = Arrays.asList();
   private static final long a = 85620408113011L;

   public void P(BakedQuad bakedQuad, IBlockState iBlockState) {
      this.c.add(bakedQuad);
      this.o.add(iBlockState);
   }

   public int N() {
      return this.c.size();
   }

   public BakedQuad k(int i) {
      return this.c.get(i);
   }

   public IBlockState a(int i) {
      return i >= 0 && i < this.o.size() ? this.o.get(i) : Blocks.air.getDefaultState();
   }

   public List<BakedQuad> y(BakedQuad bakedQuad) {
      this.Y.set(0, bakedQuad);
      return this.Y;
   }

   public void B() {
      this.c.clear();
      this.o.clear();
   }
}
