package bypass.lua.world;

import bypass.ClientAccess;
import net.minecraft.util.BlockPos;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class Block extends ThreeArgFunction {
   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2, LuaValue luaValue3) {
      if (luaValue.isnumber() && luaValue2.isnumber() && luaValue3.isnumber()) {
         net.minecraft.block.Block block2 = ClientAccess.mc
            .theWorld
            .getBlockState(new BlockPos(luaValue.todouble(), luaValue2.todouble(), luaValue3.todouble()))
            .getBlock();
         return block2 != null ? valueOf(block2.getUnlocalizedName()) : valueOf("air");
      } else {
         return NIL;
      }
   }
}
