package bypass.event.player;

import bypass.event.CancellableEvent;

public class ChatEvent extends CancellableEvent {
   private ChatDirection direction;
   private String message;
   private boolean userTyped = true;

   public ChatEvent(ChatDirection chatDirection, String string) {
      this.direction = chatDirection;
      this.message = string;
   }

   public ChatDirection getDirection() {
      return this.direction;
   }

   public String getMessage() {
      return this.message;
   }

   public String[] getWords() {
      return this.message.split(" ");
   }

   public void setUserTyped(boolean bl) {
      this.userTyped = bl;
   }

   public boolean isUserTyped() {
      return this.userTyped;
   }

   public void setMessage(String string) {
      this.message = string;
   }
}
