package bypass.loader;

import net.minecraft.entity.monster.EntitySkeleton;

public final class GuiStage extends AbstractLoadStage {
   private final IntegrityCheckHolder integrityCheckHolder = new IntegrityCheckHolder();

   @Override
   public StageAction getAction() {
      return new GuiStage.GuiStageAction(this);
   }

   @Override
   public StageValue getValue() {
      return new GuiStage.IntegrityCheckValue(this);
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

   public class GuiStageAction extends StageAction {
      public GuiStageAction(LoadStage loadStage) {
         super(loadStage);
      }

      @Override
      public void run() {
      }
   }

   public class IntegrityCheckValue extends StageValue {
      public IntegrityCheckValue(LoadStage loadStage) {
         super(loadStage);
      }

      public EntitySkeleton get() {
         return (EntitySkeleton)GuiStage.accessIntegrityCheckHolder(GuiStage.this).getIntegrityCheck().performIntegrityCheck().value;
      }
   }
}
