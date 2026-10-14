package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public abstract class ChatComponentStyle implements IChatComponent {
   public List<IChatComponent> siblings = Lists.newArrayList();
   private ChatStyle style;

   @Override
   public IChatComponent appendSibling(IChatComponent component) {
      component.getChatStyle().setParentStyle(this.getChatStyle());
      this.siblings.add(component);
      return this;
   }

   @Override
   public List<IChatComponent> getSiblings() {
      return this.siblings;
   }

   @Override
   public IChatComponent appendText(String text) {
      return this.appendSibling(new ChatComponentText(text));
   }

   @Override
   public IChatComponent setChatStyle(ChatStyle style) {
      this.style = style;

      for (IChatComponent iChatComponent : this.siblings) {
         iChatComponent.getChatStyle().setParentStyle(this.getChatStyle());
      }

      return this;
   }

   @Override
   public ChatStyle getChatStyle() {
      if (this.style == null) {
         this.style = new ChatStyle();

         for (IChatComponent iChatComponent : this.siblings) {
            iChatComponent.getChatStyle().setParentStyle(this.style);
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
      StringBuilder stringBuilder = new StringBuilder();

      for (IChatComponent iChatComponent : this) {
         stringBuilder.append(iChatComponent.getUnformattedTextForChat());
      }

      return stringBuilder.toString();
   }

   @Override
   public final String getFormattedText() {
      StringBuilder stringBuilder = new StringBuilder();

      for (IChatComponent iChatComponent : this) {
         stringBuilder.append(iChatComponent.getChatStyle().getFormattingCode());
         stringBuilder.append(iChatComponent.getUnformattedTextForChat());
         stringBuilder.append(EnumChatFormatting.RESET);
      }

      return stringBuilder.toString();
   }

   public static Iterator<IChatComponent> createDeepCopyIterator(Iterable<IChatComponent> components) {
      Iterator iterator2 = Iterators.concat(Iterators.transform(components.iterator(), new Function<IChatComponent, Iterator<IChatComponent>>() {
         public Iterator<IChatComponent> apply(IChatComponent iChatComponent) {
            return iChatComponent.iterator();
         }
      }));
      return Iterators.transform(iterator2, new Function<IChatComponent, IChatComponent>() {
         public IChatComponent apply(IChatComponent iChatComponent) {
            IChatComponent iChatComponent2 = iChatComponent.createCopy();
            iChatComponent2.setChatStyle(iChatComponent2.getChatStyle().createDeepCopy());
            return iChatComponent2;
         }
      });
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof ChatComponentStyle)) {
         return false;
      }

      ChatComponentStyle chatComponentStyle = (ChatComponentStyle)object;
      return this.siblings.equals(chatComponentStyle.siblings) && this.getChatStyle().equals(chatComponentStyle.getChatStyle());
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
