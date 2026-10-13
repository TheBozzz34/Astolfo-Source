package net.minecraft.tileentity;

public class TileEntityDropper extends TileEntityDispenser {
   private static final long c = 10050644843137L;

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.dropper";
   }

   @Override
   public String getGuiID() {
      return "minecraft:dropper";
   }
}
