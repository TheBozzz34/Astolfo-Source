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
   public void setState(int var1) {
      this.state = var1;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public class a8h extends StageAction {
      private static final long c = 111584894944810L;

      public a8h(LoadStage var2) {
         super(var2);
      }

      @Override
      public void run() {
         long var1 = c ^ 134463124439927L;
         long var10001 = var1 ^ 109048837705839L;
         int var3 = (int)((var1 ^ 109048837705839L) >>> 32);
         int var4 = (int)((var1 ^ 109048837705839L) << 32 >>> 48);
         int var5 = (int)(var10001 << 48 >>> 48);
         long var6 = var1 ^ 116415384644096L;
         long var8 = var1 ^ 125177937552439L;
         if (DirectoryStage.this.getState() == 1) {
            ClientAccess.client
               .setDirectory(new File(Minecraft.getMinecraft().mcDataDir.getAbsoluteFile().getParentFile(), ClientAccess.client.getName().toLowerCase()));
            ClientAccess.client.getDirectory().mkdir();
            ClientAccess.client.register(var8, ClientAccess.client.getFontManager());
            FontManager.A();
         }
      }
   }

   public class rsh extends StageValue {
      private static final long b = 58794049964221L;

      public rsh(LoadStage var2) {
         super(var2);
      }

      public EnvironmentKind get() {
         boolean var5 = Util.getOSType().equals(Util.EnumOS.WINDOWS);
         if (!var5) {
            return EnvironmentKind.M(() -> EnvironmentKinds.O(EnvironmentKind.O()));
         }

         var5 = false;
         if (Util.getOSType().equals(Util.EnumOS.WINDOWS)) {
            File var6 = new File("C:/Users");
            String[] var7 = var6.list((var0, var1) -> new File(var0, var1).isDirectory());
            if (var7 != null) {
               for (String var11 : var7) {
                  if (var11.contains(System.getProperty("user.home").replace("C:\\Users\\", ""))) {
                     var5 = true;
                     break;
                  }
               }
            }
         }

         boolean var13 = var5;
         return EnvironmentKind.M(() -> !var13);
      }
   }
}
