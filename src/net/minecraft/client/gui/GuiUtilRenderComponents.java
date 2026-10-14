package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class GuiUtilRenderComponents {
   public static String func_178909_a(String string, boolean bl) {
      return !bl && !Minecraft.getMinecraft().gameSettings.chatColours ? EnumChatFormatting.getTextWithoutFormattingCodes(string) : string;
   }

   public static List<IChatComponent> splitText(IChatComponent iChatComponent, int i, FontRenderer fontRenderer, boolean bl, boolean bl2) {
      int j = 0;
      ChatComponentText chatComponentText = new ChatComponentText("");
      ArrayList arrayList = Lists.newArrayList();
      ArrayList arrayList2 = Lists.newArrayList(iChatComponent);

      for (int k = 0; k < arrayList2.size(); k++) {
         IChatComponent iChatComponent2 = (IChatComponent)arrayList2.get(k);
         String string = iChatComponent2.getUnformattedTextForChat();
         boolean bl3 = false;
         if (string.contains("\n")) {
            int l = string.indexOf(10);
            String string2 = string.substring(l + 1);
            string = string.substring(0, l + 1);
            ChatComponentText chatComponentText2 = new ChatComponentText(string2);
            chatComponentText2.setChatStyle(iChatComponent2.getChatStyle().createShallowCopy());
            arrayList2.add(k + 1, chatComponentText2);
            bl3 = true;
         }

         String string3 = func_178909_a(iChatComponent2.getChatStyle().getFormattingCode() + string, bl2);
         String string4 = string3.endsWith("\n") ? string3.substring(0, string3.length() - 1) : string3;
         int m = fontRenderer.getStringWidth(string4);
         ChatComponentText chatComponentText3 = new ChatComponentText(string4);
         chatComponentText3.setChatStyle(iChatComponent2.getChatStyle().createShallowCopy());
         if (j + m > i) {
            String string5 = fontRenderer.trimStringToWidth(string3, i - j, false);
            String string6 = string5.length() < string3.length() ? string3.substring(string5.length()) : null;
            if (string6 != null && string6.length() > 0) {
               int i2 = string5.lastIndexOf(" ");
               if (i2 >= 0 && fontRenderer.getStringWidth(string3.substring(0, i2)) > 0) {
                  string5 = string3.substring(0, i2);
                  if (bl) {
                     i2++;
                  }

                  string6 = string3.substring(i2);
               } else if (j > 0 && !string3.contains(" ")) {
                  string5 = "";
                  string6 = string3;
               }

               ChatComponentText chatComponentText4 = new ChatComponentText(string6);
               chatComponentText4.setChatStyle(iChatComponent2.getChatStyle().createShallowCopy());
               arrayList2.add(k + 1, chatComponentText4);
            }

            m = fontRenderer.getStringWidth(string5);
            chatComponentText3 = new ChatComponentText(string5);
            chatComponentText3.setChatStyle(iChatComponent2.getChatStyle().createShallowCopy());
            bl3 = true;
         }

         if (j + m <= i) {
            j += m;
            chatComponentText.appendSibling(chatComponentText3);
         } else {
            bl3 = true;
         }

         if (bl3) {
            arrayList.add(chatComponentText);
            j = 0;
            chatComponentText = new ChatComponentText("");
         }
      }

      arrayList.add(chatComponentText);
      return arrayList;
   }
}
