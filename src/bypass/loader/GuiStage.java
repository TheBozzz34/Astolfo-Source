package bypass.loader;

import net.minecraft.entity.monster.EntitySkeleton;

public final class GuiStage extends AbstractLoadStage {
   private final IntegrityCheckHolder integrityCheckHolder = new IntegrityCheckHolder();

   @Override
   public StageAction getAction() {
      return new GuiStage.a82(this);
   }

   @Override
   public StageValue getValue() {
      return new GuiStage.rs2(this);
   }

   public IntegrityCheckHolder getIntegrityCheckHolder() {
      return this.integrityCheckHolder;
   }

   @Override
   public void setState(int i) {
      this.state = i;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static IntegrityCheckHolder accessIntegrityCheckHolder(GuiStage guiStage) {
      return guiStage.integrityCheckHolder;
   }

   public class a82 extends StageAction {
      public a82(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
      }
   }

   public class rs2 extends StageValue {
      public rs2(LoadStage loadStage) {
         super(loadStage);
      }

      public EntitySkeleton get() {
         return (EntitySkeleton)GuiStage.accessIntegrityCheckHolder(GuiStage.this).getIntegrityCheck().performIntegrityCheck().value;
      }
   }
}
