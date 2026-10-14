package bypass.loader.check;

public final class MeasureRequest implements MeasureMarker {
   public static final int I = 1;
   public static final int v = 2;
   public static final int Q = 3;
   public static final int F = 4;
   private final AbstractMeasureTask o;
   private final int c;
   private boolean r;

   public MeasureRequest(AbstractMeasureTask abstractMeasureTask, int i) {
      this.o = abstractMeasureTask;
      this.c = i;
   }

   public AbstractMeasureTask h() {
      return this.o;
   }

   public int Q() {
      return this.c;
   }

   public boolean q() {
      return this.r;
   }

   public void L(boolean bl) {
      this.r = bl;
   }
}
