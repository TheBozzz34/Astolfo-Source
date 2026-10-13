package net.minecraft.util;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.Entity;

public class ChatComponentProcessor {
   private static final long a = 69941451953249L;

   public static IChatComponent processComponent(ICommandSender var0, IChatComponent var1, Entity var2) throws CommandException {
      net.minecraft.util.IChatComponent var6 = null;
      if (var1 instanceof ChatComponentScore) {
         ChatComponentScore var7 = (ChatComponentScore)var1;
         String var8 = var7.getName();
         if (PlayerSelector.hasArguments(var8)) {
            List var9 = PlayerSelector.matchEntities(var0, var8, Entity.class);
            if (var9.size() != 1) {
               throw new EntityNotFoundException();
            }

            var8 = ((Entity)var9.get(0)).getName();
         }

         var6 = var2 != null && var8.equals("*")
            ? new ChatComponentScore(var2.getName(), var7.getObjective())
            : new ChatComponentScore(var8, var7.getObjective());
         ((net.minecraft.util.ChatComponentScore)var6).setValue(var7.getUnformattedTextForChat());
      } else if (var1 instanceof ChatComponentSelector) {
         String var11 = ((ChatComponentSelector)var1).getSelector();
         var6 = PlayerSelector.matchEntitiesToChatComponent(var0, var11);
         if (var6 == null) {
            var6 = new ChatComponentText("");
         }
      } else if (var1 instanceof ChatComponentText) {
         var6 = new ChatComponentText(((ChatComponentText)var1).getChatComponentText_TextValue());
      } else {
         if (!(var1 instanceof ChatComponentTranslation)) {
            return var1;
         }

         Object[] var12 = ((ChatComponentTranslation)var1).getFormatArgs();

         for (int var14 = 0; var14 < var12.length; var14++) {
            Object var16 = var12[var14];
            if (var16 instanceof IChatComponent) {
               var12[var14] = processComponent(var0, (IChatComponent)var16, var2);
            }
         }

         var6 = new ChatComponentTranslation(((ChatComponentTranslation)var1).getKey(), var12);
      }

      ChatStyle var13 = var1.getChatStyle();
      if (var13 != null) {
         var6.setChatStyle(var13.createShallowCopy());
      }

      for (IChatComponent var17 : var1.getSiblings()) {
         var6.appendSibling(processComponent(var0, var17, var2));
      }

      return var6;
   }
}
