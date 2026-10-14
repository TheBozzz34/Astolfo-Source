package bypass.loader;

import bypass.ClientAccess;
import bypass.font.FontManager;
import bypass.loader.check.EnvironmentKind;
import bypass.loader.check.EnvironmentKinds;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;

public final class DirectoryStage extends AbstractLoadStage {
   @Override
   public StageAction getAction() {
      return new DirectoryStage.a8h(this);
   }

   @Override
   public StageValue getValue() {
      return new DirectoryStage.rsh(this);
   }

   @Override
   public void setState(int i) {
      this.state = i;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public class a8h extends StageAction {
      public a8h(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
         if (DirectoryStage.this.getState() == 1) {
            ClientAccess.client
               .setDirectory(new File(Minecraft.getMinecraft().mcDataDir.getAbsoluteFile().getParentFile(), ClientAccess.client.getName().toLowerCase()));
            ClientAccess.client.getDirectory().mkdir();
            ClientAccess.client.register(121968519176554L, ClientAccess.client.getFontManager());
            FontManager.A();
         }
      }
   }

   public class rsh extends StageValue {
      public rsh(LoadStage loadStage) {
         super(loadStage);
      }

      public EnvironmentKind get() {
         boolean bl = Util.getOSType().equals(Util.EnumOS.WINDOWS);
         if (!bl) {
            return EnvironmentKind.M(() -> EnvironmentKinds.O(EnvironmentKind.O()));
         }

         bl = false;
         if (Util.getOSType().equals(Util.EnumOS.WINDOWS)) {
            File file = new File("C:/Users");
            String[] strings = file.list((file2, string) -> new File(file2, string).isDirectory());
            if (strings != null) {
               for (String string2 : strings) {
                  if (string2.contains(System.getProperty("user.home").replace("C:\\Users\\", ""))) {
                     bl = true;
                     break;
                  }
               }
            }
         }

         boolean bl2 = bl;
         return EnvironmentKind.M(() -> !bl2);
      }
   }
}
