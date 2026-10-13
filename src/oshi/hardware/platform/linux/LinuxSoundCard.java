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
   private static final long c = 2526995184006L;
   private static final long j = 3439901047521280035L;

   public LinuxSoundCard(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   private static List<File> getCardFolders() {
      File var3 = new File(ProcPath.ASOUND);
      ArrayList var4 = new ArrayList();
      File[] var5 = var3.listFiles();
      if (var5 != null) {
         for (File var9 : var5) {
            if (var9.getName().startsWith("card") && var9.isDirectory()) {
               var4.add(var9);
            }
         }
      }

      return var4;
   }

   private static String getSoundCardVersion() {
      String var3 = FileUtil.getStringFromFile(ProcPath.ASOUND + "version");
      return var3.isEmpty() ? "not available" : var3;
   }

   private static String getCardCodec(File var0) {
      String var4 = "";
      File[] var5 = var0.listFiles();
      if (var5 != null) {
         for (File var9 : var5) {
            if (var9.getName().startsWith("codec")) {
               if (!var9.isDirectory()) {
                  var4 = FileUtil.getKeyValueMapFromFile(var9.getPath(), ":").get("Codec");
               } else {
                  File[] var10 = var9.listFiles();
                  if (var10 != null) {
                     for (File var14 : var10) {
                        if (!var14.isDirectory() && var14.getName().contains("#")) {
                           var4 = var14.getName().substring(0, var14.getName().indexOf((int)j));
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      return var4;
   }

   private static String getCardName(File var0) {
      String var4 = "Not Found..";
      Map var5 = FileUtil.getKeyValueMapFromFile(ProcPath.ASOUND + "/" + "cards", ":");
      String var6 = FileUtil.getStringFromFile(var0.getPath() + "/" + "id");

      for (Object var8$raw : var5.entrySet()) {
         Entry var8 = (Entry)var8$raw;
         if (((String)var8.getKey()).contains(var6)) {
            return (String)var8.getValue();
         }
      }

      return var4;
   }

   public static List<LinuxSoundCard> getSoundCards() {
      ArrayList var3 = new ArrayList();

      for (File var5 : getCardFolders()) {
         var3.add(new LinuxSoundCard(getSoundCardVersion(), getCardName(var5), getCardCodec(var5)));
      }

      return var3;
   }
}
