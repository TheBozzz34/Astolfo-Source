package net.minecraft.util;

public class ChatComponentSelector extends ChatComponentStyle {
   private final String selector;
   private static final long a = 23917152885201L;

   public ChatComponentSelector(String var1) {
      this.selector = var1;
   }

   public String getSelector() {
      return this.selector;
   }

   @Override
   public String getUnformattedTextForChat() {
      return this.selector;
   }

   public ChatComponentSelector createCopy() {
      ChatComponentSelector var4 = new ChatComponentSelector(this.selector);
      var4.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent var6 : this.getSiblings()) {
         var4.appendSibling(var6.createCopy());
      }

      return var4;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ChatComponentSelector)) {
         return false;
      }

      ChatComponentSelector var5 = (ChatComponentSelector)var1;
      return this.selector.equals(var5.selector) && super.equals(var1);
   }

   @Override
   public String toString() {
      return "SelectorComponent{pattern='" + this.selector + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
   }
}
