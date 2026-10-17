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
   private static final int DIFFTIME = 2;
   private static final int EXECUTE = 3;
   private static final int EXIT = 4;
   private static final int GETENV = 5;
   private static final int REMOVE = 6;
   private static final int RENAME = 7;
   private static final int SETLOCALE = 8;
   private static final int TIME = 9;
   private static final int TMPNAME = 10;
   private static final String[] NAMES = new String[]{"clock", "date", "difftime", "execute", "exit", "getenv", "remove", "rename", "setlocale", "time"};
   private static final long START_TIME_MILLIS = System.currentTimeMillis();
   private static long tmpNameCounter = START_TIME_MILLIS;
   public Globals globals;
   private static final String[] WEEKDAY_NAMES_ABBREV = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
   private static final String[] WEEKDAY_NAMES = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
   private static final String[] MONTH_NAMES_ABBREV = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
   private static final String[] MONTH_NAMES = new String[]{
      "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
   };

   @Override
   public LuaValue call(LuaValue luaValue, LuaValue luaValue2) {
      this.globals = luaValue2.checkglobals();
      LuaTable luaTable = new LuaTable();

      for (int i = 0; i < NAMES.length; i++) {
         luaTable.set(NAMES[i], new OsLibrary.OsFunction(i, NAMES[i]));
      }

      luaValue2.set("os", luaTable);
      if (!luaValue2.get("package").isnil()) {
         luaValue2.get("package").get("loaded").set("os", luaTable);
      }

      return luaTable;
   }

   public double clock() {
      return (System.currentTimeMillis() - START_TIME_MILLIS) / 1000.0;
   }

   public double difftime(double d, double e) {
      return d - e;
   }

   public String date(String string, double d) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date((long)(d * 1000.0)));
      if (string.startsWith("!")) {
         d -= this.getTimeZoneOffset(calendar);
         calendar.setTime(new Date((long)(d * 1000.0)));
         string = string.substring(1);
      }

      byte[] bytes = string.getBytes();
      int i = bytes.length;
      Buffer buffer = new Buffer(i);
      int j = 0;

      while (j < i) {
         byte b2;
         switch (b2 = bytes[j++]) {
            case 10:
               buffer.append("\n");
               break;
            case 37:
               if (j < i) {
                  switch (b2 = bytes[j++]) {
                     case 37:
                        buffer.append((byte)37);
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
                        LuaValue.argerror(1, "invalid conversion specifier '%" + b2 + "'");
                        break;
                     case 65:
                        buffer.append(WEEKDAY_NAMES[calendar.get(7) - 1]);
                        break;
                     case 66:
                        buffer.append(MONTH_NAMES[calendar.get(2)]);
                        break;
                     case 72:
                        buffer.append(String.valueOf(100 + calendar.get(11)).substring(1));
                        break;
                     case 73:
                        buffer.append(String.valueOf(100 + calendar.get(11) % 12).substring(1));
                        break;
                     case 77:
                        buffer.append(String.valueOf(100 + calendar.get(12)).substring(1));
                        break;
                     case 83:
                        buffer.append(String.valueOf(100 + calendar.get(13)).substring(1));
                        break;
                     case 85:
                        buffer.append(String.valueOf(this.getWeekNumber(calendar, 0)));
                        break;
                     case 87:
                        buffer.append(String.valueOf(this.getWeekNumber(calendar, 1)));
                        break;
                     case 88:
                        buffer.append(this.date("%H:%M:%S", d));
                        break;
                     case 89:
                        buffer.append(String.valueOf(calendar.get(1)));
                        break;
                     case 97:
                        buffer.append(WEEKDAY_NAMES_ABBREV[calendar.get(7) - 1]);
                        break;
                     case 98:
                        buffer.append(MONTH_NAMES_ABBREV[calendar.get(2)]);
                        break;
                     case 99:
                        buffer.append(this.date("%a %b %d %H:%M:%S %Y", d));
                        break;
                     case 100:
                        buffer.append(String.valueOf(100 + calendar.get(5)).substring(1));
                        break;
                     case 106:
                        Calendar calendar2 = this.getStartOfYear(calendar);
                        int k = (int)((calendar.getTime().getTime() - calendar2.getTime().getTime()) / 86400000L);
                        buffer.append(String.valueOf(1001 + k).substring(1));
                        break;
                     case 109:
                        buffer.append(String.valueOf(101 + calendar.get(2)).substring(1));
                        break;
                     case 112:
                        buffer.append(calendar.get(11) < 12 ? "AM" : "PM");
                        break;
                     case 119:
                        buffer.append(String.valueOf((calendar.get(7) + 6) % 7));
                        break;
                     case 120:
                        buffer.append(this.date("%m/%d/%y", d));
                        break;
                     case 121:
                        buffer.append(String.valueOf(calendar.get(1)).substring(2));
                        break;
                     case 122:
                        int l = this.getTimeZoneOffset(calendar) / 60;
                        int i2 = Math.abs(l);
                        String string2 = String.valueOf(100 + i2 / 60).substring(1);
                        String string3 = String.valueOf(100 + i2 % 60).substring(1);
                        buffer.append((l >= 0 ? "+" : "-") + string2 + string3);
                  }
               }
               break;
            default:
               buffer.append(b2);
         }
      }

      return buffer.tojstring();
   }

   private Calendar getStartOfYear(Calendar calendar) {
      Calendar calendar2 = Calendar.getInstance();
      calendar2.setTime(calendar.getTime());
      calendar2.set(2, 0);
      calendar2.set(5, 1);
      calendar2.set(11, 0);
      calendar2.set(12, 0);
      calendar2.set(13, 0);
      calendar2.set(14, 0);
      return calendar2;
   }

   private int getWeekNumber(Calendar calendar, int i) {
      Calendar calendar2 = this.getStartOfYear(calendar);
      calendar2.set(5, 1 + (i + 8 - calendar2.get(7)) % 7);
      if (calendar2.after(calendar)) {
         calendar2.set(1, calendar2.get(1) - 1);
         calendar2.set(5, 1 + (i + 8 - calendar2.get(7)) % 7);
      }

      long l = calendar.getTime().getTime() - calendar2.getTime().getTime();
      return 1 + (int)(l / 604800000L);
   }

   private int getTimeZoneOffset(Calendar calendar) {
      int i = (calendar.get(11) * 3600 + calendar.get(12) * 60 + calendar.get(13)) * 1000;
      return calendar.getTimeZone().getOffset(1, calendar.get(1), calendar.get(2), calendar.get(5), calendar.get(7), i) / 1000;
   }

   private boolean isDaylightSavingTime(Calendar calendar) {
      return this.getTimeZoneOffset(calendar) != calendar.getTimeZone().getRawOffset() / 1000;
   }

   public Varargs execute(String string) {
      return varargsOf(new LuaValue[]{NIL});
   }

   public void exit(int i) {
   }

   public String getenv(String string) {
      return "";
   }

   public void remove(String string) throws IOException {
   }

   public void rename(String string, String string2) throws IOException {
   }

   public String setlocale(String string, String string2) {
      return "C";
   }

   public double time(LuaTable luaTable) {
      Date date;
      if (luaTable == null) {
         date = new Date();
      } else {
         Calendar calendar = Calendar.getInstance();
         calendar.set(1, luaTable.get("year").checkint());
         calendar.set(2, luaTable.get("month").checkint() - 1);
         calendar.set(5, luaTable.get("day").checkint());
         calendar.set(11, luaTable.get("hour").optint(12));
         calendar.set(12, luaTable.get("min").optint(0));
         calendar.set(13, luaTable.get("sec").optint(0));
         calendar.set(14, 0);
         date = calendar.getTime();
      }

      return date.getTime() / 1000.0;
   }

   public static boolean accessIsDaylightSavingTime(OsLibrary osLibrary, Calendar calendar) {
      return osLibrary.isDaylightSavingTime(calendar);
   }

   public class OsFunction extends VarArgFunction {
      public OsFunction(int i, String string) {
         this.opcode = i;
         this.name = string;
      }

      @Override
      public Varargs invoke(Varargs varargs) {
         switch (this.opcode) {
            case 0:
               return valueOf(OsLibrary.this.clock());
            case 1:
               String string = varargs.optjstring(1, "%c");
               double d = varargs.isnumber(2) ? varargs.todouble(2) : OsLibrary.this.time((LuaTable)null);
               if (string.equals("*t")) {
                  Calendar calendar = Calendar.getInstance();
                  calendar.setTime(new Date((long)(d * 1000.0)));
                  LuaTable luaTable = LuaValue.tableOf();
                  luaTable.set("year", LuaValue.valueOf(calendar.get(1)));
                  luaTable.set("month", LuaValue.valueOf(calendar.get(2) + 1));
                  luaTable.set("day", LuaValue.valueOf(calendar.get(5)));
                  luaTable.set("hour", LuaValue.valueOf(calendar.get(11)));
                  luaTable.set("min", LuaValue.valueOf(calendar.get(12)));
                  luaTable.set("sec", LuaValue.valueOf(calendar.get(13)));
                  luaTable.set("wday", LuaValue.valueOf(calendar.get(7)));
                  luaTable.set("yday", LuaValue.valueOf(calendar.get(6)));
                  luaTable.set("isdst", LuaValue.valueOf(OsLibrary.accessIsDaylightSavingTime(OsLibrary.this, calendar)));
                  return luaTable;
               }

               return valueOf(OsLibrary.this.date(string, d == -1.0 ? OsLibrary.this.time((LuaTable)null) : d));
            case 2:
               return valueOf(OsLibrary.this.difftime(varargs.checkdouble(1), varargs.checkdouble(2)));
            case 3:
            case 4:
               return NONE;
            case 5:
               String string2 = OsLibrary.this.getenv(varargs.checkjstring(1));
               return string2 != null ? valueOf(string2) : NIL;
            case 6:
               return LuaValue.TRUE;
            case 7:
               return LuaValue.TRUE;
            case 8:
               String string3 = OsLibrary.this.setlocale(varargs.optjstring(1, (String)null), varargs.optjstring(2, "all"));
               return string3 != null ? valueOf(string3) : NIL;
            case 9:
               return valueOf(OsLibrary.this.time(varargs.opttable(1, (LuaTable)null)));
            default:
               return NONE;
         }
      }
   }
}
