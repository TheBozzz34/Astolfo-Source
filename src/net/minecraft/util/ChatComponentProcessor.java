package net.minecraft.util;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.Entity;

public class ChatComponentProcessor {
   public static IChatComponent processComponent(ICommandSender commandSender, IChatComponent component, Entity entityIn) throws CommandException {
      net.minecraft.util.IChatComponent iChatComponent = null;
      if (component instanceof ChatComponentScore) {
         ChatComponentScore chatComponentScore = (ChatComponentScore)component;
         String string = chatComponentScore.getName();
         if (PlayerSelector.hasArguments(string)) {
            List list = PlayerSelector.matchEntities(commandSender, string, Entity.class);
            if (list.size() != 1) {
               throw new EntityNotFoundException();
            }

            string = ((Entity)list.get(0)).getName();
         }

         iChatComponent = entityIn != null && string.equals("*")
            ? new ChatComponentScore(entityIn.getName(), chatComponentScore.getObjective())
            : new ChatComponentScore(string, chatComponentScore.getObjective());
         ((net.minecraft.util.ChatComponentScore)iChatComponent).setValue(chatComponentScore.getUnformattedTextForChat());
      } else if (component instanceof ChatComponentSelector) {
         String string2 = ((ChatComponentSelector)component).getSelector();
         iChatComponent = PlayerSelector.matchEntitiesToChatComponent(commandSender, string2);
         if (iChatComponent == null) {
            iChatComponent = new ChatComponentText("");
         }
      } else if (component instanceof ChatComponentText) {
         iChatComponent = new ChatComponentText(((ChatComponentText)component).getChatComponentText_TextValue());
      } else {
         if (!(component instanceof ChatComponentTranslation)) {
            return component;
         }

         Object[] objects = ((ChatComponentTranslation)component).getFormatArgs();

         for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];
            if (object instanceof IChatComponent) {
               objects[i] = processComponent(commandSender, (IChatComponent)object, entityIn);
            }
         }

         iChatComponent = new ChatComponentTranslation(((ChatComponentTranslation)component).getKey(), objects);
      }

      ChatStyle chatStyle = component.getChatStyle();
      if (chatStyle != null) {
         iChatComponent.setChatStyle(chatStyle.createShallowCopy());
      }

      for (IChatComponent iChatComponent2 : component.getSiblings()) {
         iChatComponent.appendSibling(processComponent(commandSender, iChatComponent2, entityIn));
      }

      return iChatComponent;
   }
}
