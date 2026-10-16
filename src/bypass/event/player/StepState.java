package bypass.event.player;

public enum StepState {
   PRE,
   BEFORE,
   POST;

   private static final StepState[] VALUES = new StepState[]{PRE, BEFORE, POST};
}
