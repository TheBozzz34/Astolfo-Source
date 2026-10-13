package bypass.lua.world;

import bypass.ClientAccess;
import net.minecraft.util.BlockPos;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

public final class Block extends ThreeArgFunction {
   private static final long a = 124443886286116L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2, LuaValue var3) {
      if (var1.isnumber() && var2.isnumber() && var3.isnumber()) {
         net.minecraft.block.Block var7 = ClientAccess.mc.theWorld.getBlockState(new BlockPos(var1.todouble(), var2.todouble(), var3.todouble())).getBlock();
         return var7 != null ? valueOf(var7.getUnlocalizedName()) : valueOf("air");
      } else {
         return NIL;
      }
   }
}
