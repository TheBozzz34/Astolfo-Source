package bypass.lua.client;

import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

public final class Fps extends ZeroArgFunction {
   @Override
   public LuaValue call() {
      return valueOf(Minecraft.B());
   }
}
