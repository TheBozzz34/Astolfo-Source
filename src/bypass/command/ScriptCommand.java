package bypass.command;

import bypass.auth.DisconnectHandler;
import bypass.script.Script;
import bypass.script.ScriptManager;
import java.util.Arrays;
import net.minecraft.util.EnumChatFormatting;

public final class ScriptCommand extends Command {
   public static boolean scriptsChanged = true;
   private boolean unsafeWarningShown;

   public ScriptCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Arrays.asList("sc", "lua", "scripts"));
   }

   @Override
   public void runCommand(String[] strings) {
      if (DisconnectHandler.disconnected) {
         toasts.danger("You must reconnect to Astolfo before changing scripts");
      } else {
         switch (strings.length) {
            case 2:
               if (strings[1].equals("reload")) {
                  toasts.infoMessage("Script manager", "Reloaded all scripts");
                  scriptsChanged = true;
                  client.getScriptManager().reload();
                  return;
               }

               if (strings[1].equals("list")) {
                  for (Script script2 : client.getScriptManager().getList()) {
                     chat.createBuilder(false, false).append(".").append(script2.name).color(EnumChatFormatting.GRAY).build().send();
                  }

                  return;
               }

               if (strings[1].equals("unsafe")) {
                  if (!this.unsafeWarningShown && !ScriptManager.unsafeFunctions.isEnabled()) {
                     chat.printWarning("Enabling unsafe will allow Astolfo scripts to contact websites and load code from them");
                     chat.print("Execute this command again to enable/toggle it");
                     this.unsafeWarningShown = true;
                  } else {
                     ScriptManager.unsafeFunctions.setEnabled(!ScriptManager.unsafeFunctions.isEnabled());
                     chat.printWarning("Unsafe functions: " + ScriptManager.unsafeFunctions.isEnabled());
                  }
               }
               break;
            case 3:
               Script script3 = client.getScriptManager().getScript(strings[2]);
               if (strings[1].equals("enable") && script3 != null) {
                  if (!script3.loaded && !script3.loadAndRun()) {
                     return;
                  }

                  if (script3.enable()) {
                     ScriptManager.savedStates.put(script3.name, true);
                     scriptsChanged = true;
                     toasts.infoMessage(
                        "Script manager", String.format("Script %s%s has been enabled", EnumChatFormatting.GRAY, script3.name + EnumChatFormatting.RESET)
                     );
                  } else {
                     toasts.dangerMessage("Script manager", "This script is already enabled");
                  }
               }

               if (strings[1].equals("reload")) {
                  if (script3 != null) {
                     script3.disable();
                     client.getScriptManager().remove(client.getScriptManager().getScript(script3.name));
                     Script script4 = new Script(script3.path, script3.name);
                     client.getScriptManager().add(script4);
                     scriptsChanged = true;
                     if (script3.isEnabled()) {
                        script4.enable();
                     }
                  } else {
                     toasts.dangerMessage("Script manager", "Script not found");
                  }
               }

               if (strings[1].equals("disable") && script3 != null) {
                  if (script3.loaded) {
                     if (script3.disable()) {
                        ScriptManager.savedStates.put(script3.name, false);
                        toasts.infoMessage(
                           "Script manager", String.format("Script %s%s has been disabled", EnumChatFormatting.GRAY, script3.name + EnumChatFormatting.RESET)
                        );
                        scriptsChanged = true;
                     } else {
                        toasts.dangerMessage("Script manager", "This script is already disabled");
                     }
                  } else {
                     toasts.dangerMessage("Script manager", "This script is already disabled");
                  }
               }
         }
      }
   }
}
