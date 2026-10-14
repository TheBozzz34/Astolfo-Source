package net.minecraft.util;

public class ChatComponentSelector extends ChatComponentStyle {
   private final String selector;
   private static final long a = 23917152885201L;

   public ChatComponentSelector(String selectorIn) {
      this.selector = selectorIn;
   }

   public String getSelector() {
      return this.selector;
   }

   @Override
   public String getUnformattedTextForChat() {
      return this.selector;
   }

   public ChatComponentSelector createCopy() {
      ChatComponentSelector chatComponentSelector = new ChatComponentSelector(this.selector);
      chatComponentSelector.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent iChatComponent : this.getSiblings()) {
         chatComponentSelector.appendSibling(iChatComponent.createCopy());
      }

      return chatComponentSelector;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof ChatComponentSelector)) {
         return false;
      }

      ChatComponentSelector chatComponentSelector = (ChatComponentSelector)object;
      return this.selector.equals(chatComponentSelector.selector) && super.equals(object);
   }

   @Override
   public String toString() {
      return "SelectorComponent{pattern='" + this.selector + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
   }
}
