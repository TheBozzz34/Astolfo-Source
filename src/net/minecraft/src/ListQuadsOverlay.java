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

   public void P(BakedQuad var1, IBlockState var2) {
      this.c.add(var1);
      this.o.add(var2);
   }

   public int N() {
      return this.c.size();
   }

   public BakedQuad k(int var1) {
      return this.c.get(var1);
   }

   public IBlockState a(int var1) {
      return var1 >= 0 && var1 < this.o.size() ? this.o.get(var1) : Blocks.air.getDefaultState();
   }

   public List<BakedQuad> y(BakedQuad var1) {
      this.Y.set(0, var1);
      return this.Y;
   }

   public void B() {
      this.c.clear();
      this.o.clear();
   }
}
