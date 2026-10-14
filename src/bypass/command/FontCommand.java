package bypass.command;

import bypass.font.FontManager;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public class FontCommand extends Command {
   private static final long d = 92021516688091L;

   public FontCommand(String string, String string2) {
      super(string, string2);
   }

   @Override
   public void runCommand(String[] strings) {
      if (strings.length >= 2) {
         String string = StringUtils.join(Arrays.copyOfRange(strings, 1, strings.length), " ");
         FontManager.U(string);
         chat.I("Font has been set to " + string);
      }
   }
}
