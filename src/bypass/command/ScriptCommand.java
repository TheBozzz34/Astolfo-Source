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

   public ScriptCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Arrays.asList("sc", "lua", "scripts"));
   }

   @Override
   public void runCommand(String[] var1) {
      long var2 = d ^ 75656006880922L;
      long var4 = var2 ^ 8985767458526L;
      if (DisconnectHandler.disconnected) {
         toasts.x("You must reconnect to Astolfo before changing scripts");
      } else {
         switch (var1.length) {
            case 2:
               if (var1[1].equals("reload")) {
                  toasts.B("Script manager", "Reloaded all scripts");
                  z = true;
                  client.getScriptManager().M();
                  return;
               }

               if (var1[1].equals("list")) {
                  for (Script var10 : client.getScriptManager().getList()) {
                     chat.D(false, false).i(".").i(var10.C).M(EnumChatFormatting.GRAY).N().b();
                  }

                  return;
               }

               if (var1[1].equals("unsafe")) {
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
               Script var7 = client.getScriptManager().T(var1[2]);
               if (var1[1].equals("enable") && var7 != null) {
                  if (!var7.n && !var7.o()) {
                     return;
                  }

                  if (var7.A()) {
                     ScriptManager.L.put(var7.C, true);
                     z = true;
                     toasts.B("Script manager", String.format("Script %s%s has been enabled", EnumChatFormatting.GRAY, var7.C + EnumChatFormatting.RESET));
                  } else {
                     toasts.k("Script manager", "This script is already enabled");
                  }
               }

               if (var1[1].equals("reload")) {
                  if (var7 != null) {
                     var7.x();
                     client.getScriptManager().remove(client.getScriptManager().T(var7.C));
                     Script var8 = new Script(var7.P, var7.C);
                     client.getScriptManager().add(var8);
                     z = true;
                     if (var7.t()) {
                        var8.A();
                     }
                  } else {
                     toasts.k("Script manager", "Script not found");
                  }
               }

               if (var1[1].equals("disable") && var7 != null) {
                  if (var7.n) {
                     if (var7.x()) {
                        ScriptManager.L.put(var7.C, false);
                        toasts.B("Script manager", String.format("Script %s%s has been disabled", EnumChatFormatting.GRAY, var7.C + EnumChatFormatting.RESET));
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
