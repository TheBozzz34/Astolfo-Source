package bypass.loader;

import net.minecraft.entity.monster.EntitySkeleton;

public final class GuiStage extends AbstractLoadStage {
   private final IntegrityCheckHolder L = new IntegrityCheckHolder();

   @Override
   public StageAction getAction() {
      return new GuiStage.a82(this);
   }

   @Override
   public StageValue getValue() {
      return new GuiStage.rs2(this);
   }

   public IntegrityCheckHolder C() {
      return this.L;
   }

   @Override
   public void setState(int var1) {
      this.state = var1;
   }

   @Override
   public int getState() {
      return this.state;
   }

   public static IntegrityCheckHolder t(GuiStage var0) {
      return var0.L;
   }

   public class a82 extends StageAction {
      public a82(LoadStage var2) {
         super(var2);
      }

      @Override
      public void run() {
      }
   }

   public class rs2 extends StageValue {
      public rs2(LoadStage var2) {
         super(var2);
      }

      public EntitySkeleton get() {
         return (EntitySkeleton)GuiStage.t(GuiStage.this).z().Y().value;
      }
   }
}
