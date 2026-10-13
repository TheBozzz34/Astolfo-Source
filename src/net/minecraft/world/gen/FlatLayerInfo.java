package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

public class FlatLayerInfo {
   private final int field_175902_a;
   private IBlockState layerMaterial;
   private int layerCount = 1;
   private int layerMinimumY;
   private static final long a = 77512581052068L;

   public FlatLayerInfo(int var1, Block var2) {
      this(3, var1, var2);
   }

   public FlatLayerInfo(int var1, int var2, Block var3) {
      this.field_175902_a = var1;
      this.layerCount = var2;
      this.layerMaterial = var3.getDefaultState();
   }

   public FlatLayerInfo(int var1, int var2, Block var3, int var4) {
      this(var1, var2, var3);
      this.layerMaterial = var3.getStateFromMeta(var4);
   }

   public int getLayerCount() {
      return this.layerCount;
   }

   public IBlockState getLayerMaterial() {
      return this.layerMaterial;
   }

   private Block getLayerMaterialBlock() {
      return this.layerMaterial.getBlock();
   }

   private int getFillBlockMeta() {
      return this.layerMaterial.getBlock().getMetaFromState(this.layerMaterial);
   }

   public int getMinY() {
      return this.layerMinimumY;
   }

   public void setMinY(int var1) {
      this.layerMinimumY = var1;
   }

   @Override
   public String toString() {
      String var3;
      if (this.field_175902_a >= 3) {
         ResourceLocation var4 = Block.blockRegistry.getNameForObject(this.getLayerMaterialBlock());
         var3 = var4 == null ? "null" : var4.toString();
         if (this.layerCount > 1) {
            var3 = this.layerCount + "*" + var3;
         }
      } else {
         var3 = Integer.toString(Block.getIdFromBlock(this.getLayerMaterialBlock()));
         if (this.layerCount > 1) {
            var3 = this.layerCount + "x" + var3;
         }
      }

      int var5 = this.getFillBlockMeta();
      if (var5 > 0) {
         var3 = var3 + ":" + var5;
      }

      return var3;
   }
}
