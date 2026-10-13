package net.minecraft.util;

public class ChatComponentText extends ChatComponentStyle {
   private final String text;
   private static final long a = 114867671507196L;

   public ChatComponentText(String var1) {
      this.text = var1;
   }

   public String getChatComponentText_TextValue() {
      return this.text;
   }

   @Override
   public String getUnformattedTextForChat() {
      return this.text;
   }

   public ChatComponentText createCopy() {
      ChatComponentText var4 = new ChatComponentText(this.text);
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

      if (!(var1 instanceof ChatComponentText)) {
         return false;
      }

      ChatComponentText var5 = (ChatComponentText)var1;
      return this.text.equals(var5.getChatComponentText_TextValue()) && super.equals(var1);
   }

   @Override
   public String toString() {
      return "TextComponent{text='" + this.text + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
   }
}
