package oshi.hardware.platform.mac;

import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@Immutable
public final class MacSoundCard extends AbstractSoundCard {
   private static final long c = 38171679500437L;

   public MacSoundCard(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public static List<MacSoundCard> getSoundCards() {
      ArrayList var3 = new ArrayList();
      String var4 = "Apple Inc.";
      String var5 = "AppleHDAController";
      String var6 = "AppleHDACodec";
      boolean var7 = false;
      String var8 = "<key>com.apple.driver.AppleHDAController</key>";

      for (String var10 : FileUtil.readFile("/System/Library/Extensions/AppleHDA.kext/Contents/Info.plist")) {
         if (var10.contains(var8)) {
            var7 = true;
         } else if (var7) {
            var5 = "AppleHDAController " + ParseUtil.getTextBetweenStrings(var10, "<string>", "</string>");
            var7 = false;
         }
      }

      var3.add(new MacSoundCard(var5, var4, var6));
      return var3;
   }
}
