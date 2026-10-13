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
   private final Listener<RenderEvent> onRender = var1x -> {
      long var2 = c ^ 29775334135652L;
      long var10001 = var2 ^ 126807604784795L;
      int var4 = (int)((var2 ^ 126807604784795L) >>> 56);
      int var5 = (int)((var2 ^ 126807604784795L) << 8 >>> 32);
      int var6 = (int)(var10001 << 40 >>> 40);
      if (var1x.getType() == RenderType.SCREEN && this.W != null) {
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

         int var8 = var1x.getResolution().getScaledHeight();
         int var9 = Mouse.getX() * var1x.getResolution().getScaledWidth() / mc.displayWidth;
         int var10 = var8 - Mouse.getY() * var8 / mc.displayHeight - 1;
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
               S.entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(var1xx -> {
                  String var10000 = this.W.j().putIfAbsent(var1xx.getKey(), var1xx.getValue() + " " + (var1xx.getValue() > 1 ? "kills" : "kill"));
               });
            }
         }

         try {
            for (Relation var12 : client.getRelationManager().n().values()) {
               if (!var12.z()) {
                  this.W.j().putIfAbsent(var12.i(), "enemy");
               }
            }
         } catch (Exception var13) {
         }

         for (String var15 : Denicker.nicks.keySet()) {
            this.W.j().putIfAbsent(var15, "nicked");
         }

         this.W.render(var9, var10);
         if (mc.currentScreen instanceof GuiChat && this.W.u_boolean() && Mouse.isButtonDown(0)) {
            this.W.T(var9, var10);
         }

         if (Mouse.isButtonDown(0) && this.W.G(var9, var10)) {
            this.W.F(true, var9, var10);
         } else {
            this.W.x(false);
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.RECEIVED && var1x.getPacket() instanceof S02PacketChat) {
         S02PacketChat var5 = (S02PacketChat)var1x.getPacket();
         String var6 = EnumChatFormatting.getTextWithoutFormattingCodes(var5.getChatComponent().getUnformattedText());
         if (var6 == null) {
            return;
         }

         if (this.killCounter.getValue()
            && (var5.getChatComponent().getFormattedText().startsWith("§r") || var5.getChatComponent().getFormattedText().startsWith("§e"))
            && !var6.startsWith(" ")
            && var5.getChatComponent().getFormattedText().contains("by ")
            && var5.getChatComponent().getFormattedText().endsWith("§r")) {
            String[] var7 = var6.split("by ");

            try {
               synchronized (S) {
                  String var9 = GuiIngame.V(var7[1].split(" ")[0]);
                  int var10 = S.containsKey(var9) ? S.get(var9) : 0;
                  S.put(var9, var10 + 1);
               }
            } catch (Exception var13) {
               var13.printStackTrace();
            }
         }
      }
   };
   private static final long c = 27553418976418L;

   public PlayerList(Category var1) {
      super(var1);
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
