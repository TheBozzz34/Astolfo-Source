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
   public void setState(int i) {
      this.state = i;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static JvmArgumentsPropertyCheckSetup U(ManagersStage managersStage) {
      return managersStage.g;
   }

   public class az9 extends StageAction {
      private static final long c = 105110397576383L;

      public az9(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
         long l = c ^ 71123235168738L;
         long m = l ^ 134646927726923L;
         long n = l ^ 40764775396062L;
         long l2 = l ^ 125177937552439L;
         long l3 = l ^ 132776272997740L;
         if (ManagersStage.this.getState() == 1) {
            if (!Astolfo.INSTANCE.getDirectory().exists()) {
               Astolfo.INSTANCE.getDirectory().mkdir();
            }

            ClientAccess.client.register(l2, ClientAccess.client.getAltManager());
            ClientAccess.client.register(l2, ClientAccess.client.getAlteningManager());
            ClientAccess.client.register(l2, ClientAccess.client.getChatManager());
         }
      }
   }

   public class r99 extends StageValue {
      public r99(LoadStage loadStage) {
         super(loadStage);
      }

      public Pair get() {
         return (Pair)ManagersStage.U(ManagersStage.this).b().Q().value;
      }
   }
}
