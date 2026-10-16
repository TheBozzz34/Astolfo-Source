package bypass;

import bypass.command.UnusedTeleportCommand;
import bypass.module.visuals.HUD;
import java.util.regex.Pattern;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.EnumChatFormatting;

public enum ClientChat {
   CHAT;

   private static final Pattern URL_PATTERN = Pattern.compile(
      "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)", 42
   );
   private final EnumChatFormatting accentColor = EnumChatFormatting.AQUA;
   private final ChatComponentStyle prefix = this.createBuilder(false, false).append("§8> §f").build().getComponent();
   private static final ClientChat[] VALUES = new ClientChat[]{CHAT};

   public void print(String string) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.printPowerMessage(string);
      } else {
         this.createPrefixedMessage(string).build().send();
      }
   }

   public void printSuccess(String string) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.printPowerMessage(string);
      } else {
         this.createPrefixedMessage(string).color(EnumChatFormatting.GREEN).build().send();
      }
   }

   public void printWarning(String string) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.printPowerMessage(string);
      } else {
         this.createPrefixedMessage(string).color(EnumChatFormatting.YELLOW).build().send();
      }
   }

   public void printError(String string) {
      if (HUD.isPowerBranding()) {
         UnusedTeleportCommand.printPowerMessage(string);
      } else {
         this.createPrefixedMessage(string).color(EnumChatFormatting.RED).build().send();
      }
   }

   public String capitalize(String string) {
      return Character.toUpperCase(string.charAt(0)) + string.substring(1);
   }

   public ChatMessageBuilder createPrefixedBuilder() {
      ChatMessageBuilder chatMessageBuilder = new ChatMessageBuilder(this);
      ChatMessageBuilder.getRoot(chatMessageBuilder).appendSibling(this.prefix);
      return chatMessageBuilder;
   }

   public ChatMessageBuilder createBuilder(boolean bl, boolean bl2) {
      ChatMessageBuilder chatMessageBuilder = bl ? this.createPrefixedBuilder() : new ChatMessageBuilder(this);
      ChatMessageBuilder.setAutoColor(chatMessageBuilder, bl2);
      return chatMessageBuilder;
   }

   public ChatMessageBuilder createPrefixedMessage(String string) {
      return this.createPrefixedBuilder().append(string);
   }

   public static Pattern getUrlPattern() {
      return URL_PATTERN;
   }

   public static EnumChatFormatting getAccentColor(ClientChat clientChat) {
      return clientChat.accentColor;
   }
}
