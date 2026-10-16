package bypass.lua;

import bypass.ClientAccess;
import bypass.lua.client.ChestName;
import bypass.lua.client.Config;
import bypass.lua.client.Fps;
import bypass.lua.client.GuiName;
import bypass.lua.client.Print;
import bypass.lua.client.Subtitle;
import bypass.lua.client.Time;
import bypass.lua.client.Title;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class ClientLibrary extends TwoArgFunction implements ClientAccess {
   public static final ZeroArgFunction guiNameFunction = new GuiName();
   public static final ZeroArgFunction chestNameFunction = new ChestName();
   public static final ZeroArgFunction timeFunction = new Time();
   public static final ZeroArgFunction configFunction = new Config();
   public static final ZeroArgFunction fpsFunction = new Fps();
   public static final ZeroArgFunction titleFunction = new Title();
   public static final ZeroArgFunction subtitleFunction = new Subtitle();
   public static final OneArgFunction printFunction = new Print();

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      LuaTable luaTable = LuaValue.tableOf();
      luaTable.set("gui_name", guiNameFunction);
      luaTable.set("chest_name", chestNameFunction);
      luaTable.set("time", timeFunction);
      luaTable.set("config", configFunction);
      luaTable.set("print", printFunction);
      luaTable.set("title", titleFunction);
      luaTable.set("subtitle", subtitleFunction);
      luaTable.set("fps", fpsFunction);
      luaValue2.set("client", luaTable);
      return luaTable;
   }
}
