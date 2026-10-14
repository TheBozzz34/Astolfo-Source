package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatComponentTranslation extends ChatComponentStyle {
   private final String key;
   private final Object[] formatArgs;
   private final Object syncLock = new Object();
   private long lastTranslationUpdateTimeInMilliseconds = -1L;
   public List<IChatComponent> children = Lists.newArrayList();
   public static final Pattern stringVariablePattern = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public ChatComponentTranslation(String translationKey, Object... args) {
      this.key = translationKey;
      this.formatArgs = args;

      for (Object object : args) {
         if (object instanceof IChatComponent) {
            ((IChatComponent)object).getChatStyle().setParentStyle(this.getChatStyle());
         }
      }
   }

   public synchronized void ensureInitialized() {
      synchronized (this.syncLock) {
         long l = StatCollector.getLastTranslationUpdateTimeInMilliseconds();
         if (l == this.lastTranslationUpdateTimeInMilliseconds) {
            return;
         }

         this.lastTranslationUpdateTimeInMilliseconds = l;
         this.children.clear();
      }

      try {
         this.initializeFromFormat(StatCollector.translateToLocal(this.key));
      } catch (ChatComponentTranslationFormatException chatComponentTranslationFormatException) {
         this.children.clear();

         try {
            this.initializeFromFormat(StatCollector.translateToFallback(this.key));
         } catch (ChatComponentTranslationFormatException chatComponentTranslationFormatException2) {
            throw chatComponentTranslationFormatException;
         }
      }
   }

   public void initializeFromFormat(String formatIn) {
      Matcher matcher2 = stringVariablePattern.matcher(formatIn);
      int i = 0;
      int j = 0;

      try {
         while (matcher2.find(j)) {
            int k = matcher2.start();
            int l = matcher2.end();
            if (k > j) {
               ChatComponentText chatComponentText = new ChatComponentText(String.format(formatIn.substring(j, k)));
               chatComponentText.getChatStyle().setParentStyle(this.getChatStyle());
               this.children.add(chatComponentText);
            }

            String string = matcher2.group(2);
            String string2 = formatIn.substring(k, l);
            if ("%".equals(string) && "%%".equals(string2)) {
               ChatComponentText chatComponentText2 = new ChatComponentText("%");
               chatComponentText2.getChatStyle().setParentStyle(this.getChatStyle());
               this.children.add(chatComponentText2);
            } else {
               if (!"s".equals(string)) {
                  throw new ChatComponentTranslationFormatException(this, "Unsupported format: '" + string2 + "'");
               }

               String string3 = matcher2.group(1);
               int m = string3 != null ? Integer.parseInt(string3) - 1 : i++;
               if (m < this.formatArgs.length) {
                  this.children.add(this.getFormatArgumentAsComponent(m));
               }
            }

            j = l;
         }

         if (j < formatIn.length()) {
            ChatComponentText chatComponentText3 = new ChatComponentText(String.format(formatIn.substring(j)));
            chatComponentText3.getChatStyle().setParentStyle(this.getChatStyle());
            this.children.add(chatComponentText3);
         }
      } catch (IllegalFormatException illegalFormatException) {
         throw new ChatComponentTranslationFormatException(this, illegalFormatException);
      }
   }

   private IChatComponent getFormatArgumentAsComponent(int index) {
      if (index >= this.formatArgs.length) {
         throw new ChatComponentTranslationFormatException(this, index);
      }

      Object object = this.formatArgs[index];
      IChatComponent iChatComponent;
      if (object instanceof IChatComponent) {
         iChatComponent = (IChatComponent)object;
      } else {
         iChatComponent = new ChatComponentText(object == null ? "null" : object.toString());
         iChatComponent.getChatStyle().setParentStyle(this.getChatStyle());
      }

      return iChatComponent;
   }

   @Override
   public IChatComponent setChatStyle(ChatStyle style) {
      super.setChatStyle(style);

      for (Object object : this.formatArgs) {
         if (object instanceof IChatComponent) {
            ((IChatComponent)object).getChatStyle().setParentStyle(this.getChatStyle());
         }
      }

      if (this.lastTranslationUpdateTimeInMilliseconds > -1L) {
         for (IChatComponent iChatComponent : this.children) {
            iChatComponent.getChatStyle().setParentStyle(style);
         }
      }

      return this;
   }

   @Override
   public Iterator<IChatComponent> iterator() {
      this.ensureInitialized();
      return Iterators.concat(createDeepCopyIterator(this.children), createDeepCopyIterator(this.siblings));
   }

   @Override
   public String getUnformattedTextForChat() {
      this.ensureInitialized();
      StringBuilder stringBuilder = new StringBuilder();

      for (IChatComponent iChatComponent : this.children) {
         stringBuilder.append(iChatComponent.getUnformattedTextForChat());
      }

      return stringBuilder.toString();
   }

   public ChatComponentTranslation createCopy() {
      Object[] objects = new Object[this.formatArgs.length];

      for (int i = 0; i < this.formatArgs.length; i++) {
         if (this.formatArgs[i] instanceof IChatComponent) {
            objects[i] = ((IChatComponent)this.formatArgs[i]).createCopy();
         } else {
            objects[i] = this.formatArgs[i];
         }
      }

      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(this.key, objects);
      chatComponentTranslation.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent iChatComponent : this.getSiblings()) {
         chatComponentTranslation.appendSibling(iChatComponent.createCopy());
      }

      return chatComponentTranslation;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof ChatComponentTranslation)) {
         return false;
      }

      ChatComponentTranslation chatComponentTranslation = (ChatComponentTranslation)object;
      return Arrays.equals(this.formatArgs, chatComponentTranslation.formatArgs) && this.key.equals(chatComponentTranslation.key) && super.equals(object);
   }

   @Override
   public int hashCode() {
      int i = super.hashCode();
      i = 31 * i + this.key.hashCode();
      return 31 * i + Arrays.hashCode(this.formatArgs);
   }

   @Override
   public String toString() {
      return "TranslatableComponent{key='"
         + this.key
         + '\''
         + ", args="
         + Arrays.toString(this.formatArgs)
         + ", siblings="
         + this.siblings
         + ", style="
         + this.getChatStyle()
         + '}';
   }

   public String getKey() {
      return this.key;
   }

   public Object[] getFormatArgs() {
      return this.formatArgs;
   }
}
