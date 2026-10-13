package bypass.event.player;

import bypass.event.CancellableEvent;

public class ChatEvent extends CancellableEvent {
   private ChatDirection direction;
   private String message;
   private boolean userTyped = true;

   public ChatEvent(ChatDirection var1, String var2) {
      this.direction = var1;
      this.message = var2;
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

   public void setUserTyped(boolean var1) {
      this.userTyped = var1;
   }

   public boolean isUserTyped() {
      return this.userTyped;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }
}
