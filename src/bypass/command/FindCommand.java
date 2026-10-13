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
   public void runCommand(String[] var1) {
      if (!GameUtil.isInHypixelGame()) {
         chat.I("This command only runs in Hypixel");
      } else if (!this.N.hasElapsed(15000.0F)) {
         float var5 = (float)(this.N.getElapsed() / 1000.0);
         chat.I("You must wait " + (15.0F - var5) + "s before doing this again");
      } else {
         switch (var1.length) {
            case 2:
               new Thread(
                     () -> {
                        String var5x = HttpUtil.get(
                           new HttpRequest().g(String.format("https://api.hypixel.net/player?key=%s&name=%s", Denicker.hypixelAPI.getValue(), var1[1]))
                        );
                        if (var5x == null) {
                           chat.f("Wrong API Key (Check Denicker module API key)");
                        } else if (var5x.contains("\"player\":null")) {
                           chat.f("This player is non existant");
                        } else if (var5x.contains("Key throttle")) {
                           chat.f("The API key is throttled");
                        } else if (var5x.contains("\"success\":false")) {
                           chat.f("Invalid API key");
                        } else {
                           HttpRequest var6 = new HttpRequest().g(String.format("http://144.172.70.174:8081/%s@debug_", var1[1]));
                           var6.T("kpRNZWBTM6hut7NB");
                           String var7 = HttpUtil.get(var6);
                           if (var7 != null) {
                              String var8 = var7.replace("\n", "");
                              if (var1[1].contains(var8)) {
                                 chat.I("This player has no nick");
                              } else {
                                 chat.I(String.format("%s nick is %s", var1[1], var8));
                              }

                              Denicker.nicks.put(var8, var1[1]);
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
