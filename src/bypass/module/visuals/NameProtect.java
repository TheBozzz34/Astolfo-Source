package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.relation.Relation;
import bypass.setting.BooleanSetting;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public final class NameProtect extends Module {
   private final BooleanSetting switchNames = new BooleanSetting("Switch-Names", "Switches names", false);
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (mc.theWorld != null) {
         switch (packetEvent.getDirection()) {
            case OUTGOING:
               if (packetEvent.getPacket() instanceof C01PacketChatMessage && this.switchNames.getValue()) {
                  C01PacketChatMessage c01PacketChatMessage = (C01PacketChatMessage)packetEvent.getPacket();
                  String string = c01PacketChatMessage.getMessage();
                  if (string == null) {
                     return;
                  }

                  if (string.startsWith(".")) {
                     return;
                  }

                  if (string.startsWith("/")) {
                     return;
                  }

                  for (Relation relation : client.getRelationManager().getRelations().values()) {
                     if (string.contains(relation.getNickname())) {
                        string = string.replaceAll(relation.getNickname(), relation.getName());
                     }
                  }

                  packetEvent.cancel();
                  mc.thePlayer.sendQueue.sendPacketNoEvent(new C01PacketChatMessage(string));
               }
               break;
            case RECEIVED:
               if (packetEvent.getPacket() instanceof S02PacketChat) {
                  S02PacketChat s02PacketChat = (S02PacketChat)packetEvent.getPacket();
                  if (s02PacketChat.getType() == 2) {
                     return;
                  }

                  Iterator iterator2 = s02PacketChat.getChatComponent().iterator();
                  ChatComponentText chatComponentText = new ChatComponentText("");

                  while (iterator2.hasNext()) {
                     IChatComponent iChatComponent = (IChatComponent)iterator2.next();
                     ChatComponentText chatComponentText2 = new ChatComponentText(this.replaceRelationNames(iChatComponent.getUnformattedTextForChat(), iChatComponent.getFormattedText()));
                     chatComponentText2.setChatStyle(iChatComponent.getChatStyle());
                     chatComponentText.appendSibling(chatComponentText2);
                  }

                  packetEvent.cancel();
                  mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
               }
         }
      }
   };

   public NameProtect(Category category) {
      super(category);
      this.setHidden(false);
      this.addSettings(this.switchNames);
   }

   public String replaceRelationNames(String string, String string2) {
      String string3 = string;

      for (Relation relation : client.getRelationManager().getList()) {
         if (string3.contains(relation.getName())) {
            List list = this.getActiveFormatCodes(string2.split(relation.getName())[0]);
            StringBuilder stringBuilder = new StringBuilder();

            for (Object object : list) {
               Character character = (Character)object;
               stringBuilder.append("§").append(character);
            }

            string3 = string3.replaceAll(relation.getName(), (relation.isFriend() ? "§b" : "§4") + relation.getNickname() + stringBuilder);
         }
      }

      return string3;
   }

   public List<Character> getActiveFormatCodes(String string) {
      int i = string.length();
      char c = 'f';
      ArrayList arrayList = new ArrayList();

      for (int j = 0; j < i; j++) {
         char c2 = string.charAt(j);
         char c3 = j > 0 ? string.charAt(j - 1) : '.';
         if (c3 != 167 && c2 == 167 && j < i) {
            int i2 = "0123456789abcdefklmnor".indexOf(string.toLowerCase(Locale.ENGLISH).charAt(j + 1));
            if (i2 < 16) {
               arrayList.clear();
               if (i2 < 0 || i2 > 15) {
                  i2 = 15;
               }

               c = "0123456789abcdef".charAt(i2);
            } else if (i2 == 16) {
               arrayList.add('k');
            } else if (i2 == 17) {
               arrayList.add('l');
            } else if (i2 == 18) {
               arrayList.add('m');
            } else if (i2 == 19) {
               arrayList.add('n');
            } else if (i2 == 20) {
               arrayList.add('o');
            } else if (i2 == 21) {
               arrayList.clear();
               c = 'f';
            }
         }
      }

      ArrayList arrayList2 = new ArrayList();
      arrayList2.add(c);
      arrayList2.addAll(arrayList);
      return arrayList2;
   }
}
