package bypass.event.player;

public enum StepState {
   PRE,
   BEFORE,
   POST;

   private static final StepState[] Z = new StepState[]{PRE, BEFORE, POST};
}
