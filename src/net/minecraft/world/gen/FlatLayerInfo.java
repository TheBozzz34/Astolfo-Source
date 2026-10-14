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

   public FlatLayerInfo(int i, Block block) {
      this(3, i, block);
   }

   public FlatLayerInfo(int i, int height, Block layerMaterialIn) {
      this.field_175902_a = i;
      this.layerCount = height;
      this.layerMaterial = layerMaterialIn.getDefaultState();
   }

   public FlatLayerInfo(int i, int j, Block block, int k) {
      this(i, j, block);
      this.layerMaterial = block.getStateFromMeta(k);
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

   public void setMinY(int minY) {
      this.layerMinimumY = minY;
   }

   @Override
   public String toString() {
      String string;
      if (this.field_175902_a >= 3) {
         ResourceLocation resourceLocation = Block.blockRegistry.getNameForObject(this.getLayerMaterialBlock());
         string = resourceLocation == null ? "null" : resourceLocation.toString();
         if (this.layerCount > 1) {
            string = this.layerCount + "*" + string;
         }
      } else {
         string = Integer.toString(Block.getIdFromBlock(this.getLayerMaterialBlock()));
         if (this.layerCount > 1) {
            string = this.layerCount + "x" + string;
         }
      }

      int i = this.getFillBlockMeta();
      if (i > 0) {
         string = string + ":" + i;
      }

      return string;
   }
}
