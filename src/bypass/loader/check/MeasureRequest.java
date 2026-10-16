package bypass.loader.check;

public final class MeasureRequest implements MeasureMarker {
   public static final int GROUP_ONE = 1;
   public static final int GROUP_TWO = 2;
   public static final int GROUP_THREE = 3;
   public static final int GROUP_FOUR = 4;
   private final AbstractMeasureTask task;
   private final int groupId;
   private boolean completed;

   public MeasureRequest(AbstractMeasureTask abstractMeasureTask, int i) {
      this.task = abstractMeasureTask;
      this.groupId = i;
   }

   public AbstractMeasureTask getTask() {
      return this.task;
   }

   public int getGroupId() {
      return this.groupId;
   }

   public boolean isCompleted() {
      return this.completed;
   }

   public void setCompleted(boolean bl) {
      this.completed = bl;
   }
}
