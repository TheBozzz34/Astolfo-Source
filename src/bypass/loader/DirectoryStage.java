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
      private static final long c = 111584894944810L;

      public a8h(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
         long l = c ^ 134463124439927L;
         long m = l ^ 109048837705839L;
         int i = (int)((l ^ 109048837705839L) >>> 32);
         int j = (int)((l ^ 109048837705839L) << 32 >>> 48);
         int k = (int)(m << 48 >>> 48);
         long n = l ^ 116415384644096L;
         long l2 = l ^ 125177937552439L;
         if (DirectoryStage.this.getState() == 1) {
            ClientAccess.client
               .setDirectory(new File(Minecraft.getMinecraft().mcDataDir.getAbsoluteFile().getParentFile(), ClientAccess.client.getName().toLowerCase()));
            ClientAccess.client.getDirectory().mkdir();
            ClientAccess.client.register(l2, ClientAccess.client.getFontManager());
            FontManager.A();
         }
      }
   }

   public class rsh extends StageValue {
      private static final long b = 58794049964221L;

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
