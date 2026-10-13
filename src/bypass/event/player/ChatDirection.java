package bypass.event.player;

public enum ChatDirection {
   TO_SEND,
   RECEIVED;

   private static final ChatDirection[] Q = new ChatDirection[]{TO_SEND, RECEIVED};
}
