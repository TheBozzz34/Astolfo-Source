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
      return new DirectoryStage.CreateDirectoryAction(this);
   }

   @Override
   public StageValue getValue() {
      return new DirectoryStage.UserHomeCheckValue(this);
   }

   @Override
   public void setState(int i) {
      this.state = i;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public class CreateDirectoryAction extends StageAction {
      public CreateDirectoryAction(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
         if (DirectoryStage.this.getState() == 1) {
            ClientAccess.client
               .setDirectory(new File(Minecraft.getMinecraft().mcDataDir.getAbsoluteFile().getParentFile(), ClientAccess.client.getName().toLowerCase()));
            ClientAccess.client.getDirectory().mkdir();
            ClientAccess.client.register(121968519176554L, ClientAccess.client.getFontManager());
            FontManager.reloadFont();
         }
      }
   }

   public class UserHomeCheckValue extends StageValue {
      public UserHomeCheckValue(LoadStage loadStage) {
         super(loadStage);
      }

      public EnvironmentKind get() {
         boolean bl = Util.getOSType().equals(Util.EnumOS.WINDOWS);
         if (!bl) {
            return EnvironmentKind.fromCondition(() -> EnvironmentKinds.isNotKindD(EnvironmentKind.createKindD()));
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
         return EnvironmentKind.fromCondition(() -> !bl2);
      }
   }
}
