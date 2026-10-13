package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent {
   public List<IChatComponent> siblings = Lists.newArrayList();
   private ChatStyle style;
   private static final long d = 105157924575597L;

   @Override
   public IChatComponent appendSibling(IChatComponent var1) {
      var1.getChatStyle().setParentStyle(this.getChatStyle());
      this.siblings.add(var1);
      return this;
   }

   @Override
   public List<IChatComponent> getSiblings() {
      return this.siblings;
   }

   @Override
   public IChatComponent appendText(String var1) {
      return this.appendSibling(new ChatComponentText(var1));
   }

   @Override
   public IChatComponent setChatStyle(ChatStyle var1) {
      this.style = var1;

      for (IChatComponent var5 : this.siblings) {
         var5.getChatStyle().setParentStyle(this.getChatStyle());
      }

      return this;
   }

   @Override
   public ChatStyle getChatStyle() {
      if (this.style == null) {
         this.style = new ChatStyle();

         for (IChatComponent var4 : this.siblings) {
            var4.getChatStyle().setParentStyle(this.style);
         }
      }

      return this.style;
   }

   @Override
   public Iterator<IChatComponent> iterator() {
      return Iterators.concat(Iterators.forArray(this), createDeepCopyIterator(this.siblings));
   }

   @Override
   public final String getUnformattedText() {
      StringBuilder var4 = new StringBuilder();

      for (IChatComponent var6 : this) {
         var4.append(var6.getUnformattedTextForChat());
      }

      return var4.toString();
   }

   @Override
   public final String getFormattedText() {
      StringBuilder var4 = new StringBuilder();

      for (IChatComponent var6 : this) {
         var4.append(var6.getChatStyle().getFormattingCode());
         var4.append(var6.getUnformattedTextForChat());
         var4.append(EnumChatFormatting.RESET);
      }

      return var4.toString();
   }

   public static Iterator<IChatComponent> createDeepCopyIterator(Iterable<IChatComponent> var0) {
      Iterator var1 = Iterators.concat(Iterators.transform(var0.iterator(), new Function<IChatComponent, Iterator<IChatComponent>>() {
         public Iterator<IChatComponent> apply(IChatComponent var1) {
            return var1.iterator();
         }
      }));
      return Iterators.transform(var1, new Function<IChatComponent, IChatComponent>() {
         public IChatComponent apply(IChatComponent var1) {
            IChatComponent var2 = var1.createCopy();
            var2.setChatStyle(var2.getChatStyle().createDeepCopy());
            return var2;
         }
      });
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ChatComponentStyle)) {
         return false;
      }

      ChatComponentStyle var5 = (ChatComponentStyle)var1;
      return this.siblings.equals(var5.siblings) && this.getChatStyle().equals(var5.getChatStyle());
   }

   @Override
   public int hashCode() {
      return 31 * this.style.hashCode() + this.siblings.hashCode();
   }

   @Override
   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
   }
}
