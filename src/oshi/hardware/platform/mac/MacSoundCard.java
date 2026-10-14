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

   public MacSoundCard(String kernelVersion, String name, String codec) {
      super(kernelVersion, name, codec);
   }

   public static List<MacSoundCard> getSoundCards() {
      ArrayList arrayList = new ArrayList();
      String string2 = "Apple Inc.";
      String string3 = "AppleHDAController";
      String string4 = "AppleHDACodec";
      boolean bl = false;
      String string5 = "<key>com.apple.driver.AppleHDAController</key>";

      for (String string6 : FileUtil.readFile("/System/Library/Extensions/AppleHDA.kext/Contents/Info.plist")) {
         if (string6.contains(string5)) {
            bl = true;
         } else if (bl) {
            string3 = "AppleHDAController " + ParseUtil.getTextBetweenStrings(string6, "<string>", "</string>");
            bl = false;
         }
      }

      arrayList.add(new MacSoundCard(string3, string2, string4));
      return arrayList;
   }
}
