package bypass.lua.world;

import bypass.ClientAccess;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Biome extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      try {
         BlockPos blockPos = new BlockPos(
            ClientAccess.mc.getRenderViewEntity().posX,
            ClientAccess.mc.getRenderViewEntity().getEntityBoundingBox().minY,
            ClientAccess.mc.getRenderViewEntity().posZ
         );
         if (ClientAccess.mc.theWorld != null && ClientAccess.mc.theWorld.isBlockLoaded(blockPos)) {
            Chunk chunk = ClientAccess.mc.theWorld.getChunkFromBlockCoords(blockPos);
            return valueOf(chunk.getBiome(blockPos, ClientAccess.mc.theWorld.getWorldChunkManager()).biomeName);
         } else {
            return NIL;
         }
      } catch (Exception exception) {
         return NIL;
      }
   }
}
