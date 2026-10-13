package bypass.loader;

import bypass.Astolfo;
import bypass.ClientAccess;
import bypass.loader.check.JvmArgumentsPropertyCheckSetup;
import oshi.util.tuples.Pair;

public final class ManagersStage extends AbstractLoadStage {
   private final JvmArgumentsPropertyCheckSetup g = new JvmArgumentsPropertyCheckSetup();

   @Override
   public StageAction getAction() {
      return new ManagersStage.az9(this);
   }

   @Override
   public StageValue getValue() {
      return new ManagersStage.r99(this);
   }

   @Override
   public void setState(int var1) {
      this.state = var1;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static JvmArgumentsPropertyCheckSetup U(ManagersStage var0) {
      return var0.g;
   }

   public class az9 extends StageAction {
      private static final long c = 105110397576383L;

      public az9(LoadStage var2) {
         super(var2);
      }

      @Override
      public void run() {
         long var1 = c ^ 71123235168738L;
         long var3 = var1 ^ 134646927726923L;
         long var5 = var1 ^ 40764775396062L;
         long var7 = var1 ^ 125177937552439L;
         long var9 = var1 ^ 132776272997740L;
         if (ManagersStage.this.getState() == 1) {
            if (!Astolfo.INSTANCE.getDirectory().exists()) {
               Astolfo.INSTANCE.getDirectory().mkdir();
            }

            ClientAccess.client.register(var7, ClientAccess.client.getAltManager());
            ClientAccess.client.register(var7, ClientAccess.client.getAlteningManager());
            ClientAccess.client.register(var7, ClientAccess.client.getChatManager());
         }
      }
   }

   public class r99 extends StageValue {
      public r99(LoadStage var2) {
         super(var2);
      }

      public Pair get() {
         return (Pair)ManagersStage.U(ManagersStage.this).b().Q().value;
      }
   }
}
