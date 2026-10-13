package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class GuiUtilRenderComponents {
   private static final long a = 38412556497011L;
   private static final long b = -978970188042993654L;

   public static String func_178909_a(String var0, boolean var1) {
      return !var1 && !Minecraft.getMinecraft().gameSettings.chatColours ? EnumChatFormatting.getTextWithoutFormattingCodes(var0) : var0;
   }

   public static List<IChatComponent> splitText(IChatComponent var0, int var1, FontRenderer var2, boolean var3, boolean var4) {
      int var7 = 0;
      ChatComponentText var8 = new ChatComponentText("");
      ArrayList var9 = Lists.newArrayList();
      ArrayList var10 = Lists.newArrayList(var0);

      for (int var11 = 0; var11 < var10.size(); var11++) {
         IChatComponent var12 = (IChatComponent)var10.get(var11);
         String var13 = var12.getUnformattedTextForChat();
         boolean var14 = false;
         if (var13.contains("\n")) {
            int var15 = var13.indexOf((int)b);
            String var16 = var13.substring(var15 + 1);
            var13 = var13.substring(0, var15 + 1);
            ChatComponentText var17 = new ChatComponentText(var16);
            var17.setChatStyle(var12.getChatStyle().createShallowCopy());
            var10.add(var11 + 1, var17);
            var14 = true;
         }

         String var23 = func_178909_a(var12.getChatStyle().getFormattingCode() + var13, var4);
         String var24 = var23.endsWith("\n") ? var23.substring(0, var23.length() - 1) : var23;
         int var25 = var2.getStringWidth(var24);
         ChatComponentText var18 = new ChatComponentText(var24);
         var18.setChatStyle(var12.getChatStyle().createShallowCopy());
         if (var7 + var25 > var1) {
            String var19 = var2.trimStringToWidth(var23, var1 - var7, false);
            String var20 = var19.length() < var23.length() ? var23.substring(var19.length()) : null;
            if (var20 != null && var20.length() > 0) {
               int var21 = var19.lastIndexOf(" ");
               if (var21 >= 0 && var2.getStringWidth(var23.substring(0, var21)) > 0) {
                  var19 = var23.substring(0, var21);
                  if (var3) {
                     var21++;
                  }

                  var20 = var23.substring(var21);
               } else if (var7 > 0 && !var23.contains(" ")) {
                  var19 = "";
                  var20 = var23;
               }

               ChatComponentText var22 = new ChatComponentText(var20);
               var22.setChatStyle(var12.getChatStyle().createShallowCopy());
               var10.add(var11 + 1, var22);
            }

            var25 = var2.getStringWidth(var19);
            var18 = new ChatComponentText(var19);
            var18.setChatStyle(var12.getChatStyle().createShallowCopy());
            var14 = true;
         }

         if (var7 + var25 <= var1) {
            var7 += var25;
            var8.appendSibling(var18);
         } else {
            var14 = true;
         }

         if (var14) {
            var9.add(var8);
            var7 = 0;
            var8 = new ChatComponentText("");
         }
      }

      var9.add(var8);
      return var9;
   }
}
