package oshi.hardware.platform.linux;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.FileUtil;
import oshi.util.platform.linux.ProcPath;

@Immutable
public final class LinuxSoundCard extends AbstractSoundCard {
   public LinuxSoundCard(String kernelVersion, String name, String codec) {
      super(kernelVersion, name, codec);
   }

   private static List<File> getCardFolders() {
      File file = new File(ProcPath.ASOUND);
      ArrayList arrayList = new ArrayList();
      File[] files = file.listFiles();
      if (files != null) {
         for (File file2 : files) {
            if (file2.getName().startsWith("card") && file2.isDirectory()) {
               arrayList.add(file2);
            }
         }
      }

      return arrayList;
   }

   private static String getSoundCardVersion() {
      String string = FileUtil.getStringFromFile(ProcPath.ASOUND + "version");
      return string.isEmpty() ? "not available" : string;
   }

   private static String getCardCodec(File cardDir) {
      String string = "";
      File[] files = cardDir.listFiles();
      if (files != null) {
         for (File file : files) {
            if (file.getName().startsWith("codec")) {
               if (!file.isDirectory()) {
                  string = FileUtil.getKeyValueMapFromFile(file.getPath(), ":").get("Codec");
               } else {
                  File[] files2 = file.listFiles();
                  if (files2 != null) {
                     for (File file2 : files2) {
                        if (!file2.isDirectory() && file2.getName().contains("#")) {
                           string = file2.getName().substring(0, file2.getName().indexOf(35));
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      return string;
   }

   private static String getCardName(File file) {
      String string = "Not Found..";
      Map map = FileUtil.getKeyValueMapFromFile(ProcPath.ASOUND + "/" + "cards", ":");
      String string2 = FileUtil.getStringFromFile(file.getPath() + "/" + "id");

      for (Object object : map.entrySet()) {
         Entry entry = (Entry)object;
         if (((String)entry.getKey()).contains(string2)) {
            return (String)entry.getValue();
         }
      }

      return string;
   }

   public static List<LinuxSoundCard> getSoundCards() {
      ArrayList arrayList = new ArrayList();

      for (File file : getCardFolders()) {
         arrayList.add(new LinuxSoundCard(getSoundCardVersion(), getCardName(file), getCardCodec(file)));
      }

      return arrayList;
   }
}
