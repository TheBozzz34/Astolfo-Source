package bypass.lua;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.luaj.vm2.Buffer;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class OsLibrary extends TwoArgFunction {
   private static final int R = 2;
   private static final int u = 3;
   private static final int v = 4;
   private static final int C = 5;
   private static final int E = 6;
   private static final int U = 7;
   private static final int l = 8;
   private static final int o = 9;
   private static final int O = 10;
   private static final String[] n = new String[]{"clock", "date", "difftime", "execute", "exit", "getenv", "remove", "rename", "setlocale", "time"};
   private static final long W = System.currentTimeMillis();
   private static long p = W;
   public Globals y;
   private static final String[] d = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
   private static final String[] g = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
   private static final String[] Q = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
   private static final String[] X = new String[]{
      "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
   };
   private static final long a = 34386368666718L;

   @Override
   public LuaValue call(LuaValue var1, LuaValue var2) {
      this.y = var2.checkglobals();
      LuaTable var6 = new LuaTable();

      for (int var7 = 0; var7 < n.length; var7++) {
         var6.set(n[var7], new OsLibrary.pp(var7, n[var7]));
      }

      var2.set("os", var6);
      if (!var2.get("package").isnil()) {
         var2.get("package").get("loaded").set("os", var6);
      }

      return var6;
   }

   public double n() {
      return (System.currentTimeMillis() - W) / 1000.0;
   }

   public double k(double var1, double var3) {
      return var1 - var3;
   }

   public String x(String var1, double var2) {
      long var4 = a ^ 96443817699932L;
      Calendar var8 = Calendar.getInstance();
      var8.setTime(new Date((long)(var2 * 1000.0)));
      if (var1.startsWith("!")) {
         var2 -= this.O(var8);
         var8.setTime(new Date((long)(var2 * 1000.0)));
         var1 = var1.substring(1);
      }

      byte[] var9 = var1.getBytes();
      int var10 = var9.length;
      Buffer var11 = new Buffer(var10);
      int var12 = 0;

      while (var12 < var10) {
         byte var13;
         switch (var13 = var9[var12++]) {
            case 10:
               var11.append("\n");
               break;
            case 37:
               if (var12 < var10) {
                  switch (var13 = var9[var12++]) {
                     case 37:
                        var11.append((byte)37);
                        break;
                     case 38:
                     case 39:
                     case 40:
                     case 41:
                     case 42:
                     case 43:
                     case 44:
                     case 45:
                     case 46:
                     case 47:
                     case 48:
                     case 49:
                     case 50:
                     case 51:
                     case 52:
                     case 53:
                     case 54:
                     case 55:
                     case 56:
                     case 57:
                     case 58:
                     case 59:
                     case 60:
                     case 61:
                     case 62:
                     case 63:
                     case 64:
                     case 67:
                     case 68:
                     case 69:
                     case 70:
                     case 71:
                     case 74:
                     case 75:
                     case 76:
                     case 78:
                     case 79:
                     case 80:
                     case 81:
                     case 82:
                     case 84:
                     case 86:
                     case 90:
                     case 91:
                     case 92:
                     case 93:
                     case 94:
                     case 95:
                     case 96:
                     case 101:
                     case 102:
                     case 103:
                     case 104:
                     case 105:
                     case 107:
                     case 108:
                     case 110:
                     case 111:
                     case 113:
                     case 114:
                     case 115:
                     case 116:
                     case 117:
                     case 118:
                     default:
                        LuaValue.argerror(1, "invalid conversion specifier '%" + var13 + "'");
                        break;
                     case 65:
                        var11.append(g[var8.get(7) - 1]);
                        break;
                     case 66:
                        var11.append(X[var8.get(2)]);
                        break;
                     case 72:
                        var11.append(String.valueOf(100 + var8.get(11)).substring(1));
                        break;
                     case 73:
                        var11.append(String.valueOf(100 + var8.get(11) % 12).substring(1));
                        break;
                     case 77:
                        var11.append(String.valueOf(100 + var8.get(12)).substring(1));
                        break;
                     case 83:
                        var11.append(String.valueOf(100 + var8.get(13)).substring(1));
                        break;
                     case 85:
                        var11.append(String.valueOf(this.c(var8, 0)));
                        break;
                     case 87:
                        var11.append(String.valueOf(this.c(var8, 1)));
                        break;
                     case 88:
                        var11.append(this.x("%H:%M:%S", var2));
                        break;
                     case 89:
                        var11.append(String.valueOf(var8.get(1)));
                        break;
                     case 97:
                        var11.append(d[var8.get(7) - 1]);
                        break;
                     case 98:
                        var11.append(Q[var8.get(2)]);
                        break;
                     case 99:
                        var11.append(this.x("%a %b %d %H:%M:%S %Y", var2));
                        break;
                     case 100:
                        var11.append(String.valueOf(100 + var8.get(5)).substring(1));
                        break;
                     case 106:
                        Calendar var15 = this.n(var8);
                        int var20 = (int)((var8.getTime().getTime() - var15.getTime().getTime()) / 86400000L);
                        var11.append(String.valueOf(1001 + var20).substring(1));
                        break;
                     case 109:
                        var11.append(String.valueOf(101 + var8.get(2)).substring(1));
                        break;
                     case 112:
                        var11.append(var8.get(11) < 12 ? "AM" : "PM");
                        break;
                     case 119:
                        var11.append(String.valueOf((var8.get(7) + 6) % 7));
                        break;
                     case 120:
                        var11.append(this.x("%m/%d/%y", var2));
                        break;
                     case 121:
                        var11.append(String.valueOf(var8.get(1)).substring(2));
                        break;
                     case 122:
                        int var16 = this.O(var8) / 60;
                        int var14 = Math.abs(var16);
                        long var10000 = 7237896015563187422L ^ var4;
                        String var17 = String.valueOf(100 + var14 / 60).substring(1);
                        String var18 = String.valueOf(100 + var14 % 60).substring(1);
                        var11.append((var16 >= 0 ? "+" : "-") + var17 + var18);
                  }
               }
               break;
            default:
               var11.append(var13);
         }
      }

      return var11.tojstring();
   }

   private Calendar n(Calendar var1) {
      Calendar var4 = Calendar.getInstance();
      var4.setTime(var1.getTime());
      var4.set(2, 0);
      var4.set(5, 1);
      var4.set(11, 0);
      var4.set(12, 0);
      var4.set(13, 0);
      var4.set(14, 0);
      return var4;
   }

   private int c(Calendar var1, int var2) {
      Calendar var6 = this.n(var1);
      var6.set(5, 1 + (var2 + 8 - var6.get(7)) % 7);
      if (var6.after(var1)) {
         var6.set(1, var6.get(1) - 1);
         var6.set(5, 1 + (var2 + 8 - var6.get(7)) % 7);
      }

      long var7 = var1.getTime().getTime() - var6.getTime().getTime();
      return 1 + (int)(var7 / 604800000L);
   }

   private int O(Calendar var1) {
      int var4 = (var1.get(11) * 3600 + var1.get(12) * 60 + var1.get(13)) * 1000;
      return var1.getTimeZone().getOffset(1, var1.get(1), var1.get(2), var1.get(5), var1.get(7), var4) / 1000;
   }

   private boolean S(Calendar var1) {
      return this.O(var1) != var1.getTimeZone().getRawOffset() / 1000;
   }

   public Varargs q(String var1) {
      return varargsOf(new LuaValue[]{NIL});
   }

   public void C(int var1) {
   }

   public String j(String var1) {
      return "";
   }

   public void k(String var1) throws IOException {
   }

   public void S(String var1, String var2) throws IOException {
   }

   public String G(String var1, String var2) {
      return "C";
   }

   public double D(LuaTable var1) {
      Date var5;
      if (var1 == null) {
         var5 = new Date();
      } else {
         Calendar var6 = Calendar.getInstance();
         var6.set(1, var1.get("year").checkint());
         var6.set(2, var1.get("month").checkint() - 1);
         var6.set(5, var1.get("day").checkint());
         var6.set(11, var1.get("hour").optint(12));
         var6.set(12, var1.get("min").optint(0));
         var6.set(13, var1.get("sec").optint(0));
         var6.set(14, 0);
         var5 = var6.getTime();
      }

      return var5.getTime() / 1000.0;
   }

   public static boolean k(OsLibrary var0, Calendar var1) {
      return var0.S(var1);
   }

   static {
      long var20 = a ^ 135563115540686L;
      long var10000 = var20 ^ 591866441202457622L;
      var10000 = var20 ^ 1838006599845597661L;
      var10000 = var20 ^ 4865360330267061601L;
      var10000 = var20 ^ 882013798488608339L;
      var10000 = var20 ^ 4653127324848495750L;
      var10000 = var20 ^ 4798389370670225603L;
      var10000 = var20 ^ 4448353121674533841L;
      var10000 = var20 ^ 7822790321494020732L;
      var10000 = var20 ^ 4898208734541847927L;
      var10000 = 5134378694953252986L ^ var20;
   }

   public class pp extends VarArgFunction {
      private static final long a = 80524074988789L;

      public pp(int var2, String var3) {
         this.opcode = var2;
         this.name = var3;
      }

      @Override
      public Varargs invoke(Varargs var1) {
         switch (this.opcode) {
            case 0:
               return valueOf(OsLibrary.this.n());
            case 1:
               String var11 = var1.optjstring(1, "%c");
               double var6 = var1.isnumber(2) ? var1.todouble(2) : OsLibrary.this.D((LuaTable)null);
               if (var11.equals("*t")) {
                  Calendar var8 = Calendar.getInstance();
                  var8.setTime(new Date((long)(var6 * 1000.0)));
                  LuaTable var9 = LuaValue.tableOf();
                  var9.set("year", LuaValue.valueOf(var8.get(1)));
                  var9.set("month", LuaValue.valueOf(var8.get(2) + 1));
                  var9.set("day", LuaValue.valueOf(var8.get(5)));
                  var9.set("hour", LuaValue.valueOf(var8.get(11)));
                  var9.set("min", LuaValue.valueOf(var8.get(12)));
                  var9.set("sec", LuaValue.valueOf(var8.get(13)));
                  var9.set("wday", LuaValue.valueOf(var8.get(7)));
                  var9.set("yday", LuaValue.valueOf(var8.get(6)));
                  var9.set("isdst", LuaValue.valueOf(OsLibrary.k(OsLibrary.this, var8)));
                  return var9;
               }

               return valueOf(OsLibrary.this.x(var11, var6 == -1.0 ? OsLibrary.this.D((LuaTable)null) : var6));
            case 2:
               return valueOf(OsLibrary.this.k(var1.checkdouble(1), var1.checkdouble(2)));
            case 3:
            case 4:
               return NONE;
            case 5:
               String var10 = OsLibrary.this.j(var1.checkjstring(1));
               return var10 != null ? valueOf(var10) : NIL;
            case 6:
               return LuaValue.TRUE;
            case 7:
               return LuaValue.TRUE;
            case 8:
               String var5 = OsLibrary.this.G(var1.optjstring(1, (String)null), var1.optjstring(2, "all"));
               return var5 != null ? valueOf(var5) : NIL;
            case 9:
               return valueOf(OsLibrary.this.D(var1.opttable(1, (LuaTable)null)));
            default:
               return NONE;
         }
      }
   }
}
