package bypass.script;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class ReadOnlyLuaTable extends LuaTable {
   private static final long a = 123943596769236L;

   public ReadOnlyLuaTable(LuaValue var1) {
      this.presize(var1.length(), 0);

      for (Varargs var5 = var1.next(LuaValue.NIL); !var5.arg1().isnil(); var5 = var1.next(var5.arg1())) {
         LuaValue var6 = var5.arg1();
         LuaValue var7 = var5.arg(2);
         super.rawset(var6, var7.istable() ? new ReadOnlyLuaTable(var7) : var7);
      }
   }

   @Override
   public LuaValue setmetatable(LuaValue var1) {
      return error("table is read-only");
   }

   @Override
   public void set(int var1, LuaValue var2) {
      error("table is read-only");
   }

   @Override
   public void rawset(int var1, LuaValue var2) {
      error("table is read-only");
   }

   @Override
   public void rawset(LuaValue var1, LuaValue var2) {
      error("table is read-only");
   }

   @Override
   public LuaValue remove(int var1) {
      return error("table is read-only");
   }
}
