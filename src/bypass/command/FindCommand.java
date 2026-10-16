package bypass.command;

import bypass.module.exploits.Denicker;
import bypass.util.GameUtil;
import bypass.util.HttpRequest;
import bypass.util.HttpUtil;
import bypass.util.Stopwatch;
import java.util.Arrays;

public class FindCommand extends Command {
   private Stopwatch cooldown = new Stopwatch();

   public FindCommand() {
      super("find", "<player name>");
      this.setAliases(Arrays.asList("nick"));
   }

   @Override
   public void runCommand(String[] strings) {
      if (!GameUtil.isInHypixelGame()) {
         chat.print("This command only runs in Hypixel");
      } else if (!this.cooldown.hasElapsed(15000.0F)) {
         float f = (float)(this.cooldown.getElapsed() / 1000.0);
         chat.print("You must wait " + (15.0F - f) + "s before doing this again");
      } else {
         switch (strings.length) {
            case 2:
               new Thread(
                     () -> {
                        String string = HttpUtil.get(
                           new HttpRequest().setUrl(String.format("https://api.hypixel.net/player?key=%s&name=%s", Denicker.hypixelAPI.getValue(), strings[1]))
                        );
                        if (string == null) {
                           chat.printError("Wrong API Key (Check Denicker module API key)");
                        } else if (string.contains("\"player\":null")) {
                           chat.printError("This player is non existant");
                        } else if (string.contains("Key throttle")) {
                           chat.printError("The API key is throttled");
                        } else if (string.contains("\"success\":false")) {
                           chat.printError("Invalid API key");
                        } else {
                           HttpRequest httpRequest = new HttpRequest().setUrl(String.format("http://144.172.70.174:8081/%s@debug_", strings[1]));
                           httpRequest.setUserAgent("kpRNZWBTM6hut7NB");
                           String string2 = HttpUtil.get(httpRequest);
                           if (string2 != null) {
                              String string3 = string2.replace("\n", "");
                              if (strings[1].contains(string3)) {
                                 chat.print("This player has no nick");
                              } else {
                                 chat.print(String.format("%s nick is %s", strings[1], string3));
                              }

                              Denicker.nicks.put(string3, strings[1]);
                              this.cooldown.reset();
                           }
                        }
                     }
                  )
                  .start();
         }
      }
   }
}
