package net.minecraft.block.material;

public class MaterialLogic extends Material {
   public MaterialLogic(MapColor color) {
      super(color);
      this.h();
   }

   @Override
   public boolean isSolid() {
      return false;
   }

   @Override
   public boolean blocksLight() {
      return false;
   }

   @Override
   public boolean blocksMovement() {
      return false;
   }
}
