package net.minecraft.block.material;

public class MaterialLogic extends Material {
   public MaterialLogic(MapColor var1) {
      super(var1);
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
