package bypass.script;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class ReadOnlyLuaTable extends LuaTable {
   public ReadOnlyLuaTable(LuaValue luaValue) {
      this.presize(luaValue.length(), 0);

      for (Varargs varargs = luaValue.next(LuaValue.NIL); !varargs.arg1().isnil(); varargs = luaValue.next(varargs.arg1())) {
         LuaValue luaValue2 = varargs.arg1();
         LuaValue luaValue3 = varargs.arg(2);
         super.rawset(luaValue2, luaValue3.istable() ? new ReadOnlyLuaTable(luaValue3) : luaValue3);
      }
   }

   @Override
   public LuaValue setmetatable(LuaValue luaValue) {
      return error("table is read-only");
   }

   @Override
   public void set(int i, LuaValue luaValue) {
      error("table is read-only");
   }

   @Override
   public void rawset(int i, LuaValue luaValue) {
      error("table is read-only");
   }

   @Override
   public void rawset(LuaValue luaValue, LuaValue luaValue2) {
      error("table is read-only");
   }

   @Override
   public LuaValue remove(int i) {
      return error("table is read-only");
   }
}
