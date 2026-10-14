package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.world.World;

public class StructureMineshaftStart extends StructureStart {
   private static final long a = 92813651287719L;
   private static final long f = -751190909646798838L;

   public StructureMineshaftStart() {
   }

   public StructureMineshaftStart(World worldIn, Random rand, int chunkX, int chunkZ) {
      super(chunkX, chunkZ);
      StructureMineshaftPieces.Room room = new StructureMineshaftPieces.Room(0, rand, (chunkX << 4) + 2, (chunkZ << 4) + 2);
      this.components.add(room);
      room.buildComponent(room, this.components, rand);
      this.updateBoundingBox();
      this.markAvailableHeight(worldIn, rand, (int)f);
   }
}
