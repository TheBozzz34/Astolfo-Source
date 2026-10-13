package bypass.command;

import bypass.font.FontManager;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public class FontCommand extends Command {
   private static final long d = 92021516688091L;

   public FontCommand(String var1, String var2) {
      super(var1, var2);
   }

   @Override
   public void runCommand(String[] var1) {
      if (var1.length >= 2) {
         String var5 = StringUtils.join(Arrays.copyOfRange(var1, 1, var1.length), " ");
         FontManager.U(var5);
         chat.I("Font has been set to " + var5);
      }
   }
}
