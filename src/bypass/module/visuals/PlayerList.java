package bypass.module.visuals;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderType;
import bypass.gui.hud.PlayerListHud;
import bypass.hypixel.FinalKillTracker;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.exploits.Denicker;
import bypass.module.hidden.ScoreboardTracker;
import bypass.relation.Relation;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;

public final class PlayerList extends Module {
   private PlayerListHud W;
   private final NumberSetting<Float> posX = new NumberSetting<>("Pos-X", "Pos", 150.0F, 1.0F, 50000.0F);
   private final NumberSetting<Float> posY = new NumberSetting<>("Pos-Y", "Pos", 150.0F, 1.0F, 50000.0F);
   private final BooleanSetting killCounter = new BooleanSetting("Kill-Counter", "Counts kills", true);
   public static final Map<String, Integer> S = Collections.synchronizedMap(new LinkedHashMap<>());
   public static final String j = EnumChatFormatting.RED + "Red";
   public static final String X = EnumChatFormatting.GREEN + "Green";
   public static final String T = EnumChatFormatting.YELLOW + "Yellow";
   public static final String O = EnumChatFormatting.BLUE + "Blue";
   @EventHandler(RenderEvent.class)
   private final Listener<RenderEvent> onRender = renderEvent -> {
      long l = c ^ 29775334135652L;
      long m = l ^ 126807604784795L;
      int k = (int)((l ^ 126807604784795L) >>> 56);
      int i2 = (int)((l ^ 126807604784795L) << 8 >>> 32);
      int i3 = (int)(m << 40 >>> 40);
      if (renderEvent.getType() == RenderType.SCREEN && this.W != null) {
         if (Mouse.isButtonDown(0)) {
            this.posX.setValue(this.W.M());
            this.posY.setValue(this.W.E_float());
         } else if (this.posX.getValue() != this.W.M() || this.posY.getValue() != this.W.E_float()) {
            if (this.W.M() != this.posX.getValue()) {
               this.W.o(this.posX.getValue());
            }

            if (this.W.E_float() != this.posY.getValue()) {
               this.W.q(this.posY.getValue());
            }
         }

         int i4 = renderEvent.getResolution().getScaledHeight();
         int i5 = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
         int i6 = i4 - Mouse.getY() * i4 / mc.displayHeight - 1;
         this.W.j().clear();
         if (this.killCounter.getValue()) {
            if (ScoreboardTracker.r()) {
               if (!ScoreboardTracker.N.K() && !ScoreboardTracker.N.G()) {
                  this.W.j().put(j, String.valueOf(FinalKillTracker.getTeamFinals(0)));
                  this.W.j().put(X, String.valueOf(FinalKillTracker.getTeamFinals(1)));
                  this.W.j().put(T, String.valueOf(FinalKillTracker.getTeamFinals(2)));
                  this.W.j().put(O, String.valueOf(FinalKillTracker.getTeamFinals(3)));
               }
            } else {
               S.entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(entry -> {
                  String string = this.W.j().putIfAbsent(entry.getKey(), entry.getValue() + " " + (entry.getValue() > 1 ? "kills" : "kill"));
               });
            }
         }

         try {
            for (Relation relation : client.getRelationManager().n().values()) {
               if (!relation.z()) {
                  this.W.j().putIfAbsent(relation.i(), "enemy");
               }
            }
         } catch (Exception exception) {
         }

         for (String string2 : Denicker.nicks.keySet()) {
            this.W.j().putIfAbsent(string2, "nicked");
         }

         this.W.render(i5, i6);
         if (mc.currentScreen instanceof GuiChat && this.W.u_boolean() && Mouse.isButtonDown(0)) {
            this.W.T(i5, i6);
         }

         if (Mouse.isButtonDown(0) && this.W.G(i5, i6)) {
            this.W.F(true, i5, i6);
         } else {
            this.W.x(false);
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.RECEIVED && packetEvent.getPacket() instanceof S02PacketChat) {
         S02PacketChat s02PacketChat = (S02PacketChat)packetEvent.getPacket();
         String string = EnumChatFormatting.getTextWithoutFormattingCodes(s02PacketChat.getChatComponent().getUnformattedText());
         if (string == null) {
            return;
         }

         if (this.killCounter.getValue()
            && (s02PacketChat.getChatComponent().getFormattedText().startsWith("§r") || s02PacketChat.getChatComponent().getFormattedText().startsWith("§e"))
            && !string.startsWith(" ")
            && s02PacketChat.getChatComponent().getFormattedText().contains("by ")
            && s02PacketChat.getChatComponent().getFormattedText().endsWith("§r")) {
            String[] strings = string.split("by ");

            try {
               synchronized (S) {
                  String string2 = GuiIngame.V(strings[1].split(" ")[0]);
                  int i = S.containsKey(string2) ? S.get(string2) : 0;
                  S.put(string2, i + 1);
               }
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }
      }
   };
   private static final long c = 27553418976418L;

   public PlayerList(Category category) {
      super(category);
      this.addSettings(this.posX, this.posY, this.killCounter);
   }

   @Override
   public void onEnable() {
      this.W = new PlayerListHud(this.posX.asFloat(), this.posY.asFloat(), 150, 15);
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.W = null;
      super.onDisable();
   }
}
