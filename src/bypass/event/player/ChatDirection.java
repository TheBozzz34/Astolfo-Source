package bypass.event.player;

public enum ChatDirection {
   TO_SEND,
   RECEIVED;

   private static final ChatDirection[] VALUES = new ChatDirection[]{TO_SEND, RECEIVED};
}
