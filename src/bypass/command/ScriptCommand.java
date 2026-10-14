package bypass.command;

import bypass.auth.DisconnectHandler;
import bypass.script.Script;
import bypass.script.ScriptManager;
import java.util.Arrays;
import net.minecraft.util.EnumChatFormatting;

public final class ScriptCommand extends Command {
   public static boolean z = true;
   private boolean p;
   private static final long d = 66829120883463L;

   public ScriptCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Arrays.asList("sc", "lua", "scripts"));
   }

   @Override
   public void runCommand(String[] strings) {
      long l = d ^ 75656006880922L;
      long m = l ^ 8985767458526L;
      if (DisconnectHandler.disconnected) {
         toasts.x("You must reconnect to Astolfo before changing scripts");
      } else {
         switch (strings.length) {
            case 2:
               if (strings[1].equals("reload")) {
                  toasts.B("Script manager", "Reloaded all scripts");
                  z = true;
                  client.getScriptManager().M();
                  return;
               }

               if (strings[1].equals("list")) {
                  for (Script script2 : client.getScriptManager().getList()) {
                     chat.D(false, false).i(".").i(script2.C).M(EnumChatFormatting.GRAY).N().b();
                  }

                  return;
               }

               if (strings[1].equals("unsafe")) {
                  if (!this.p && !ScriptManager.N.t()) {
                     chat.P("Enabling unsafe will allow Astolfo scripts to contact websites and load code from them");
                     chat.I("Execute this command again to enable/toggle it");
                     this.p = true;
                  } else {
                     ScriptManager.N.j(!ScriptManager.N.t());
                     chat.P("Unsafe functions: " + ScriptManager.N.t());
                  }
               }
               break;
            case 3:
               Script script3 = client.getScriptManager().T(strings[2]);
               if (strings[1].equals("enable") && script3 != null) {
                  if (!script3.n && !script3.o()) {
                     return;
                  }

                  if (script3.A()) {
                     ScriptManager.L.put(script3.C, true);
                     z = true;
                     toasts.B("Script manager", String.format("Script %s%s has been enabled", EnumChatFormatting.GRAY, script3.C + EnumChatFormatting.RESET));
                  } else {
                     toasts.k("Script manager", "This script is already enabled");
                  }
               }

               if (strings[1].equals("reload")) {
                  if (script3 != null) {
                     script3.x();
                     client.getScriptManager().remove(client.getScriptManager().T(script3.C));
                     Script script4 = new Script(script3.P, script3.C);
                     client.getScriptManager().add(script4);
                     z = true;
                     if (script3.t()) {
                        script4.A();
                     }
                  } else {
                     toasts.k("Script manager", "Script not found");
                  }
               }

               if (strings[1].equals("disable") && script3 != null) {
                  if (script3.n) {
                     if (script3.x()) {
                        ScriptManager.L.put(script3.C, false);
                        toasts.B("Script manager", String.format("Script %s%s has been disabled", EnumChatFormatting.GRAY, script3.C + EnumChatFormatting.RESET));
                        z = true;
                     } else {
                        toasts.k("Script manager", "This script is already disabled");
                     }
                  } else {
                     toasts.k("Script manager", "This script is already disabled");
                  }
               }
         }
      }
   }
}
