package bypass.command;

import bypass.module.exploits.Denicker;
import bypass.util.GameUtil;
import bypass.util.HttpRequest;
import bypass.util.HttpUtil;
import bypass.util.Stopwatch;
import java.util.Arrays;

public class FindCommand extends Command {
   private Stopwatch N = new Stopwatch();
   private static final long d = 97319406559614L;

   public FindCommand() {
      super("find", "<player name>");
      this.setAliases(Arrays.asList("nick"));
   }

   @Override
   public void runCommand(String[] strings) {
      if (!GameUtil.isInHypixelGame()) {
         chat.I("This command only runs in Hypixel");
      } else if (!this.N.hasElapsed(15000.0F)) {
         float h = (float)(this.N.getElapsed() / 1000.0);
         chat.I("You must wait " + (15.0F - h) + "s before doing this again");
      } else {
         switch (strings.length) {
            case 2:
               new Thread(
                     () -> {
                        String string = HttpUtil.get(
                           new HttpRequest().g(String.format("https://api.hypixel.net/player?key=%s&name=%s", Denicker.hypixelAPI.getValue(), strings[1]))
                        );
                        if (string == null) {
                           chat.f("Wrong API Key (Check Denicker module API key)");
                        } else if (string.contains("\"player\":null")) {
                           chat.f("This player is non existant");
                        } else if (string.contains("Key throttle")) {
                           chat.f("The API key is throttled");
                        } else if (string.contains("\"success\":false")) {
                           chat.f("Invalid API key");
                        } else {
                           HttpRequest httpRequest = new HttpRequest().g(String.format("http://144.172.70.174:8081/%s@debug_", strings[1]));
                           httpRequest.T("kpRNZWBTM6hut7NB");
                           String string2 = HttpUtil.get(httpRequest);
                           if (string2 != null) {
                              String string3 = string2.replace("\n", "");
                              if (strings[1].contains(string3)) {
                                 chat.I("This player has no nick");
                              } else {
                                 chat.I(String.format("%s nick is %s", strings[1], string3));
                              }

                              Denicker.nicks.put(string3, strings[1]);
                              this.N.reset();
                           }
                        }
                     }
                  )
                  .start();
         }
      }
   }
}
