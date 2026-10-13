package bypass.lua;

import bypass.ClientAccess;
import bypass.lua.modulemanager.IsModuleOn;
import bypass.lua.modulemanager.Option4;
import bypass.lua.modulemanager.RegisterBoolean3;
import bypass.lua.modulemanager.RegisterNumber;
import bypass.lua.modulemanager.SetOption;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import bypass.setting.SettingType;
import bypass.setting.StringSetting;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class ModuleManagerLibrary4 extends TwoArgFunction implements ClientAccess {
   private Module u;
   public final TwoArgFunction w = new ModuleManagerLibrary4.r4e();
   public static final OneArgFunction X = new GetSettingFunction4();
   public static final VarArgFunction H = new Option4();
   public static final VarArgFunction q = new SetOption();
   public static final OneArgFunction Q = new IsModuleOn();
   public static final ThreeArgFunction b = new RegisterBoolean3();
   public static final VarArgFunction T = new RegisterNumber();
   private static final long c = 99828213274077L;

   public ModuleManagerLibrary4(Module var1) {
      this.u = var1;
   }

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      LuaTable var5 = LuaValue.tableOf();
      var5.set("register", this.w);
      var5.set("register_boolean", b);
      var5.set("register_number", T);
      var5.set("option", H);
      var5.set("set_option", q);
      var5.set("is_module_on", Q);
      var2.set("module_manager", var5);
      return var5;
   }

   private static void c(LuaValue var0, Setting<?> var1) {
      if (var0.isnumber()) {
         if (var1.getType() == SettingType.NUMBER) {
            NumberSetting var5 = (NumberSetting)var1;
            if (var5.valueClass == Integer.class) {
               var5.setValue(var0.toint());
            } else {
               var5.setValue(var0.tofloat());
            }
         } else if (var1.getType() == SettingType.ENUM) {
            EnumSetting var9 = (EnumSetting)var1;
            Enum[] var6 = (Enum[])var9.getValue().getClass().getEnumConstants();

            try {
               var9.setValue(var6[var0.toint() + 1]);
            } catch (Exception var8) {
               var9.setValue(var6[0]);
            }
         }
      } else if (var0.isboolean()) {
         if (var1.getType() == SettingType.BOOLEAN) {
            BooleanSetting var10 = (BooleanSetting)var1;
            var10.set(var0.checkboolean());
         }
      } else if (var0.isstring() && var1.getType() == SettingType.STRING) {
         StringSetting var11 = (StringSetting)var1;
         var11.setValue(var0.checkjstring());
      }
   }

   public static Module d(ModuleManagerLibrary4 var0) {
      return var0.u;
   }

   public static void h(LuaValue var0, Setting var1) {
      c(var0, var1);
   }

   public class r4e extends TwoArgFunction {
      private static final long a = 130125136064414L;

      @Override
      public LuaValue call(LuaValue var1, LuaValue var2) {
         String var6 = null;
         LuaTable var7 = null;
         if (var1.isstring()) {
            LuaString var8 = (LuaString)var1;
            var6 = var8.checkjstring();
         }

         if (var2.isfunction()) {
            LuaValue var9 = var2.call();
            if (var9.istable()) {
               var7 = (LuaTable)var9.call();
            }
         } else if (var2.istable()) {
            var7 = (LuaTable)var2;
         }

         if (var6 != null && var7 != null) {
            ModuleManagerLibrary4.d(ModuleManagerLibrary4.this).script = var7;
            return valueOf(true);
         } else {
            return valueOf(false);
         }
      }
   }
}
