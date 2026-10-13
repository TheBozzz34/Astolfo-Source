package oshi.hardware.platform.unix.solaris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public final class SolarisSoundCard extends AbstractSoundCard {
   private static final long c = 62177792154519L;

   public SolarisSoundCard(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public static List<SolarisSoundCard> getSoundCards() {
      HashMap var3 = new HashMap();
      HashMap var4 = new HashMap();
      ArrayList var5 = new ArrayList();
      String var6 = "";

      for (String var8 : ExecutingCommand.runNative("lshal")) {
         var8 = var8.trim();
         if (var8.startsWith("udi =")) {
            var6 = ParseUtil.getSingleQuoteStringValue(var8);
         } else if (!var6.isEmpty() && !var8.isEmpty()) {
            if (var8.contains("info.solaris.driver =") && "audio810".equals(ParseUtil.getSingleQuoteStringValue(var8))) {
               var5.add(var6);
            } else if (var8.contains("info.product")) {
               var4.put(var6, ParseUtil.getStringBetween(var8, '\''));
            } else if (var8.contains("info.vendor")) {
               var3.put(var6, ParseUtil.getStringBetween(var8, '\''));
            }
         }
      }

      ArrayList var10 = new ArrayList();

      for (Object var9$raw : var5) {
         String var9 = (String)var9$raw;
         var10.add(
            new SolarisSoundCard((String)var4.get(var9) + " " + "audio810", (String)var3.get(var9) + " " + (String)var4.get(var9), (String)var4.get(var9))
         );
      }

      return var10;
   }
}
