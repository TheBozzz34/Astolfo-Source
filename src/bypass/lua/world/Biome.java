package bypass.lua.world;

import bypass.ClientAccess;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Biome extends ZeroArgFunction {
   private static final long a = 2232696263595L;

   @Override
   public LuaValue call() {
      try {
         BlockPos var4 = new BlockPos(
            ClientAccess.mc.getRenderViewEntity().posX,
            ClientAccess.mc.getRenderViewEntity().getEntityBoundingBox().minY,
            ClientAccess.mc.getRenderViewEntity().posZ
         );
         if (ClientAccess.mc.theWorld != null && ClientAccess.mc.theWorld.isBlockLoaded(var4)) {
            Chunk var5 = ClientAccess.mc.theWorld.getChunkFromBlockCoords(var4);
            return valueOf(var5.getBiome(var4, ClientAccess.mc.theWorld.getWorldChunkManager()).biomeName);
         } else {
            return NIL;
         }
      } catch (Exception var6) {
         return NIL;
      }
   }
}
