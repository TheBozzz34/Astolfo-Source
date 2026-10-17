package bypass.loader;

import bypass.Astolfo;
import bypass.ClientAccess;
import bypass.loader.check.JvmArgumentsPropertyCheckSetup;
import oshi.util.tuples.Pair;

public final class ManagersStage extends AbstractLoadStage {
   private final JvmArgumentsPropertyCheckSetup jvmArgumentsPropertyCheckSetup = new JvmArgumentsPropertyCheckSetup();

   @Override
   public StageAction getAction() {
      return new ManagersStage.RegisterManagersAction(this);
   }

   @Override
   public StageValue getValue() {
      return new ManagersStage.JvmPropertyCheckValue(this);
   }

   @Override
   public void setState(int i) {
      this.state = i;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static JvmArgumentsPropertyCheckSetup accessJvmArgumentsPropertyCheckSetup(ManagersStage managersStage) {
      return managersStage.jvmArgumentsPropertyCheckSetup;
   }

   public class JvmPropertyCheckValue extends StageValue {
      public JvmPropertyCheckValue(LoadStage loadStage) {
         super(loadStage);
      }

      public Pair get() {
         return (Pair)ManagersStage.accessJvmArgumentsPropertyCheckSetup(ManagersStage.this).getJvmArgumentsPropertyCheck().checkJvmArgumentProperties().value;
      }
   }

   public class RegisterManagersAction extends StageAction {
      public RegisterManagersAction(LoadStage loadStage) {
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
}
