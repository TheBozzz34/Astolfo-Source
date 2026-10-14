package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
public final class FreeBsdSoundCard extends AbstractSoundCard {
   public FreeBsdSoundCard(String kernelVersion, String name, String codec) {
      super(kernelVersion, name, codec);
   }

   public static List<SoundCard> getSoundCards() {
      HashMap hashMap = new HashMap();
      HashMap hashMap2 = new HashMap();
      hashMap.clear();
      hashMap2.clear();
      ArrayList arrayList = new ArrayList();
      String string = "";

      for (String string2 : ExecutingCommand.runNative("lshal")) {
         string2 = string2.trim();
         if (string2.startsWith("udi =")) {
            string = ParseUtil.getSingleQuoteStringValue(string2);
         } else if (!string.isEmpty() && !string2.isEmpty()) {
            if (string2.contains("freebsd.driver =") && "pcm".equals(ParseUtil.getSingleQuoteStringValue(string2))) {
               arrayList.add(string);
            } else if (string2.contains("info.product")) {
               hashMap2.put(string, ParseUtil.getStringBetween(string2, '\''));
            } else if (string2.contains("info.vendor")) {
               hashMap.put(string, ParseUtil.getStringBetween(string2, '\''));
            }
         }
      }

      ArrayList arrayList2 = new ArrayList();

      for (Object object : arrayList) {
         String string3 = (String)object;
         arrayList2.add(new FreeBsdSoundCard((String)hashMap2.get(string3), (String)hashMap.get(string3) + " " + (String)hashMap2.get(string3), (String)hashMap2.get(string3)));
      }

      return arrayList2;
   }
}
