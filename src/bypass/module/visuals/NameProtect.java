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
   private final Listener<PacketEvent> onPacket = var1x -> {
      long var2 = c ^ 106979336202516L;
      long var10001 = var2 ^ 58265579090293L;
      int var4 = (int)((var2 ^ 58265579090293L) >>> 56);
      int var5 = (int)((var2 ^ 58265579090293L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      if (mc.theWorld != null) {
         switch (var1x.getDirection()) {
            case OUTGOING:
               if (var1x.getPacket() instanceof C01PacketChatMessage && this.switchNames.getValue()) {
                  C01PacketChatMessage var13 = (C01PacketChatMessage)var1x.getPacket();
                  String var14 = var13.getMessage();
                  if (var14 == null) {
                     return;
                  }

                  if (var14.startsWith(".")) {
                     return;
                  }

                  if (var14.startsWith("/")) {
                     return;
                  }

                  for (Relation var16 : client.getRelationManager().n().values()) {
                     if (var14.contains(var16.m())) {
                        var14 = var14.replaceAll(var16.m(), var16.i());
                     }
                  }

                  var1x.cancel();
                  mc.thePlayer.sendQueue.u(new C01PacketChatMessage(var14));
               }
               break;
            case RECEIVED:
               if (var1x.getPacket() instanceof S02PacketChat) {
                  S02PacketChat var8 = (S02PacketChat)var1x.getPacket();
                  if (var8.getType() == 2) {
                     return;
                  }

                  Iterator var9 = var8.getChatComponent().iterator();
                  ChatComponentText var10 = new ChatComponentText("");

                  while (var9.hasNext()) {
                     IChatComponent var11 = (IChatComponent)var9.next();
                     ChatComponentText var12 = new ChatComponentText(this.v(var11.getUnformattedTextForChat(), var11.getFormattedText()));
                     var12.setChatStyle(var11.getChatStyle());
                     var10.appendSibling(var12);
                  }

                  var1x.cancel();
                  mc.ingameGUI.getChatGUI().printChatMessage(var10);
               }
         }
      }
   };
   private static final long c = 40671121243452L;

   public NameProtect(Category var1) {
      super(var1);
      this.setHidden(false);
      this.addSettings(this.switchNames);
   }

   public String v(String var1, String var2) {
      long var3 = c ^ 47323119762000L;
      long var10001 = var3 ^ 139330212323889L;
      int var5 = (int)((var3 ^ 139330212323889L) >>> 56);
      int var6 = (int)((var3 ^ 139330212323889L) << 8 >>> 32);
      int var7 = (int)(var10001 << 40 >>> 40);
      String var9 = var1;

      for (Relation var11 : client.getRelationManager().getList()) {
         if (var9.contains(var11.i())) {
            List var12 = this.w(var2.split(var11.i())[0]);
            StringBuilder var13 = new StringBuilder();

            for (Object var15$raw : var12) {
               Character var15 = (Character)var15$raw;
               var13.append("§").append(var15);
            }

            var9 = var9.replaceAll(var11.i(), (var11.z() ? "§b" : "§4") + var11.m() + var13);
         }
      }

      return var9;
   }

   public List<Character> w(String var1) {
      int var5 = var1.length();
      char var6 = 'f';
      ArrayList var7 = new ArrayList();

      for (int var8 = 0; var8 < var5; var8++) {
         char var9 = var1.charAt(var8);
         char var10 = var8 > 0 ? var1.charAt(var8 - 1) : '.';
         if (var10 != 167 && var9 == 167 && var8 < var5) {
            int var11 = "0123456789abcdefklmnor".indexOf(var1.toLowerCase(Locale.ENGLISH).charAt(var8 + 1));
            if (var11 < 16) {
               var7.clear();
               if (var11 < 0 || var11 > 15) {
                  var11 = 15;
               }

               var6 = "0123456789abcdef".charAt(var11);
            } else if (var11 == 16) {
               var7.add('k');
            } else if (var11 == 17) {
               var7.add('l');
            } else if (var11 == 18) {
               var7.add('m');
            } else if (var11 == 19) {
               var7.add('n');
            } else if (var11 == 20) {
               var7.add('o');
            } else if (var11 == 21) {
               var7.clear();
               var6 = 'f';
            }
         }
      }

      ArrayList var12 = new ArrayList();
      var12.add(var6);
      var12.addAll(var7);
      return var12;
   }
}
