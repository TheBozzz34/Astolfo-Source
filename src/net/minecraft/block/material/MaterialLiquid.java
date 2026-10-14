package net.minecraft.block.material;

public class MaterialLiquid extends Material {
   public MaterialLiquid(MapColor color) {
      super(color);
      this.X();
      this.k();
   }

   @Override
   public boolean isLiquid() {
      return true;
   }

   @Override
   public boolean blocksMovement() {
      return false;
   }

   @Override
   public boolean isSolid() {
      return false;
   }
}
