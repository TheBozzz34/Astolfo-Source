package net.minecraft.world;

public class WorldProviderSurface extends WorldProvider {
   private static final long b = 98899493225946L;

   @Override
   public String getDimensionName() {
      return "Overworld";
   }

   @Override
   public String getInternalNameSuffix() {
      return "";
   }
}
