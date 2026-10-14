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
      public az9(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
         if (ManagersStage.this.getState() == 1) {
            if (!Astolfo.INSTANCE.getDirectory().exists()) {
               Astolfo.INSTANCE.getDirectory().mkdir();
            }

            ClientAccess.client.register(121968519176554L, ClientAccess.client.getAltManager());
            ClientAccess.client.register(121968519176554L, ClientAccess.client.getAlteningManager());
            ClientAccess.client.register(121968519176554L, ClientAccess.client.getChatManager());
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
