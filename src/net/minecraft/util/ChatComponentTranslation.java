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
   private static final long a = 82059539470389L;

   public ChatComponentTranslation(String var1, Object... var2) {
      this.key = var1;
      this.formatArgs = var2;

      for (Object var8 : var2) {
         if (var8 instanceof IChatComponent) {
            ((IChatComponent)var8).getChatStyle().setParentStyle(this.getChatStyle());
         }
      }
   }

   public synchronized void ensureInitialized() {
      synchronized (this.syncLock) {
         long var5 = StatCollector.getLastTranslationUpdateTimeInMilliseconds();
         if (var5 == this.lastTranslationUpdateTimeInMilliseconds) {
            return;
         }

         this.lastTranslationUpdateTimeInMilliseconds = var5;
         this.children.clear();
      }

      try {
         this.initializeFromFormat(StatCollector.translateToLocal(this.key));
      } catch (ChatComponentTranslationFormatException var9) {
         this.children.clear();

         try {
            this.initializeFromFormat(StatCollector.translateToFallback(this.key));
         } catch (ChatComponentTranslationFormatException var8) {
            throw var9;
         }
      }
   }

   public void initializeFromFormat(String var1) {
      Matcher var6 = stringVariablePattern.matcher(var1);
      int var7 = 0;
      int var8 = 0;

      try {
         while (var6.find(var8)) {
            int var10 = var6.start();
            int var9 = var6.end();
            if (var10 > var8) {
               ChatComponentText var11 = new ChatComponentText(String.format(var1.substring(var8, var10)));
               var11.getChatStyle().setParentStyle(this.getChatStyle());
               this.children.add(var11);
            }

            String var17 = var6.group(2);
            String var12 = var1.substring(var10, var9);
            if ("%".equals(var17) && "%%".equals(var12)) {
               ChatComponentText var18 = new ChatComponentText("%");
               var18.getChatStyle().setParentStyle(this.getChatStyle());
               this.children.add(var18);
            } else {
               if (!"s".equals(var17)) {
                  throw new ChatComponentTranslationFormatException(this, "Unsupported format: '" + var12 + "'");
               }

               String var13 = var6.group(1);
               int var14 = var13 != null ? Integer.parseInt(var13) - 1 : var7++;
               if (var14 < this.formatArgs.length) {
                  this.children.add(this.getFormatArgumentAsComponent(var14));
               }
            }

            var8 = var9;
         }

         if (var8 < var1.length()) {
            ChatComponentText var16 = new ChatComponentText(String.format(var1.substring(var8)));
            var16.getChatStyle().setParentStyle(this.getChatStyle());
            this.children.add(var16);
         }
      } catch (IllegalFormatException var15) {
         throw new ChatComponentTranslationFormatException(this, var15);
      }
   }

   private IChatComponent getFormatArgumentAsComponent(int var1) {
      if (var1 >= this.formatArgs.length) {
         throw new ChatComponentTranslationFormatException(this, var1);
      }

      Object var5 = this.formatArgs[var1];
      IChatComponent var6;
      if (var5 instanceof IChatComponent) {
         var6 = (IChatComponent)var5;
      } else {
         var6 = new ChatComponentText(var5 == null ? "null" : var5.toString());
         var6.getChatStyle().setParentStyle(this.getChatStyle());
      }

      return var6;
   }

   @Override
   public IChatComponent setChatStyle(ChatStyle var1) {
      super.setChatStyle(var1);

      for (Object var8 : this.formatArgs) {
         if (var8 instanceof IChatComponent) {
            ((IChatComponent)var8).getChatStyle().setParentStyle(this.getChatStyle());
         }
      }

      if (this.lastTranslationUpdateTimeInMilliseconds > -1L) {
         for (IChatComponent var10 : this.children) {
            var10.getChatStyle().setParentStyle(var1);
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
      StringBuilder var4 = new StringBuilder();

      for (IChatComponent var6 : this.children) {
         var4.append(var6.getUnformattedTextForChat());
      }

      return var4.toString();
   }

   public ChatComponentTranslation createCopy() {
      Object[] var4 = new Object[this.formatArgs.length];

      for (int var5 = 0; var5 < this.formatArgs.length; var5++) {
         if (this.formatArgs[var5] instanceof IChatComponent) {
            var4[var5] = ((IChatComponent)this.formatArgs[var5]).createCopy();
         } else {
            var4[var5] = this.formatArgs[var5];
         }
      }

      ChatComponentTranslation var8 = new ChatComponentTranslation(this.key, var4);
      var8.setChatStyle(this.getChatStyle().createShallowCopy());

      for (IChatComponent var7 : this.getSiblings()) {
         var8.appendSibling(var7.createCopy());
      }

      return var8;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ChatComponentTranslation)) {
         return false;
      }

      ChatComponentTranslation var5 = (ChatComponentTranslation)var1;
      return Arrays.equals(this.formatArgs, var5.formatArgs) && this.key.equals(var5.key) && super.equals(var1);
   }

   @Override
   public int hashCode() {
      int var3 = super.hashCode();
      var3 = 31 * var3 + this.key.hashCode();
      return 31 * var3 + Arrays.hashCode(this.formatArgs);
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
