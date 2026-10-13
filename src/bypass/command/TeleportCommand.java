package bypass.command;

import bypass.ClientChat;
import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.lua.ModuleManagerLibrary3;
import bypass.module.scripts.ScriptModule;
import bypass.script.LuaGlobals;
import bypass.script.Script;
import bypass.util.GameUtil;
import java.util.Collections;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

public class TeleportCommand extends Command {
   private final Globals m;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      switch (var1x.getDirection()) {
         case OUTGOING:
            int var5 = ScriptModule.getOutgoingPacketId(var1x.getPacket());
            if (var5 == -1) {
               return;
            }

            LuaTable var6 = LuaValue.tableOf();
            switch (var5) {
               case 0:
                  var6.set("keep_alive_key", ((C00PacketKeepAlive)var1x.getPacket()).getKey());
                  break;
               case 1:
                  var6.set("message", ((C01PacketChatMessage)var1x.getPacket()).getMessage());
                  break;
               case 2:
                  C02PacketUseEntity var7x = (C02PacketUseEntity)var1x.getPacket();
                  Entity var14 = var7x.getEntityFromWorld(mc.theWorld);
                  if (var14 != null) {
                     var6.set("entity_id", var14.getEntityId());
                  }

                  var6.set("action_name", var7x.getAction().name());
                  var6.set("action", var7x.getAction().ordinal() + 1);
               case 3:
               case 4:
               case 5:
               case 6:
               case 7:
               case 8:
               case 10:
               case 11:
               case 12:
               case 13:
               default:
                  break;
               case 9:
                  C09PacketHeldItemChange var19 = (C09PacketHeldItemChange)var1x.getPacket();
                  var6.set("slot", var19.getSlotId());
                  break;
               case 14:
                  C0EPacketClickWindow var21 = (C0EPacketClickWindow)var1x.getPacket();
                  var6.set("slot", var21.getSlotId());
                  var6.set("window_id", var21.getWindowId());
                  var6.set("mode", var21.getMode());
                  break;
               case 15:
                  C0FPacketConfirmTransaction var16 = (C0FPacketConfirmTransaction)var1x.getPacket();
                  var6.set("window_id", var16.getWindowId());
                  var6.set("uid", var16.getUid());
                  var6.set("accept", LuaValue.valueOf(var16.N()));
            }

            var6.set("packet_id", var5);
            var6.set("cancel", LuaValue.valueOf(var1x.isCancelled()));
            LuaValue var13 = this.b("on_send_packet", var6);
            if (var13 != null && !var13.isnil() && var13.istable()) {
               LuaTable var15 = (LuaTable)var13;
               LuaValue var17 = var15.get("cancel");
               if (!var17.isnil() && var17.isboolean() && var17.checkboolean()) {
                  var1x.setCancelled(true);
               }
            }
            break;
         case RECEIVED:
            int var8 = ScriptModule.getIncomingPacketId(var1x.getPacket());
            if (var8 == -1) {
               return;
            }

            LuaTable var9 = LuaValue.tableOf();
            var9.set("packet_id", var8);
            var9.set("cancel", LuaValue.valueOf(var1x.isCancelled()));
            switch (var8) {
               case 2:
                  S02PacketChat var10 = (S02PacketChat)var1x.getPacket();
                  var9.set("message", var10.getChatComponent().getFormattedText());
                  break;
               case 8:
                  S08PacketPlayerPosLook var12 = (S08PacketPlayerPosLook)var1x.getPacket();
                  var9.set("pos_x", var12.getX());
                  var9.set("pos_y", var12.getY());
                  var9.set("pos_z", var12.getZ());
                  var9.set("yaw", var12.getYaw());
                  var9.set("pitch", var12.getPitch());
                  break;
               case 18:
                  S12PacketEntityVelocity var11 = (S12PacketEntityVelocity)var1x.getPacket();
                  var9.set("entity_id", var11.getEntityID());
                  var9.set("motion_x", var11.getMotionX());
                  var9.set("motion_y", var11.getMotionY());
                  var9.set("motion_z", var11.getMotionZ());
            }

            LuaValue var18 = this.b("on_receive_packet", var9);
            if (var18 != null && !var18.isnil() && var18.istable()) {
               LuaValue var20 = var18.get("cancel");
               if (!var20.isnil() && var20.isboolean() && var20.checkboolean()) {
                  var1x.setCancelled(true);
               }
            }
      }
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      switch (var1x.getState()) {
         case PRE:
            this.S("on_pre_update");
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = var1x -> this.S("on_player_join");
   private static final long d = 67690303663614L;

   public TeleportCommand(String var1, String var2, String var3) {
      super(var1, var2);
      this.setAliases(Collections.singletonList("tp"));
      this.m = new Globals();
      Script.w(this.m);
      this.m.load(new ModuleManagerLibrary3(this));
      LuaValue var7 = LuaGlobals.globals.load(var3, "352675432675", this.m);
      var7.call();
      client.getEventBus().subscribe(this);
   }

   @Override
   public void runCommand(String[] var1) {
      if (GameUtil.isOnHypixel()) {
         switch (var1.length) {
            case 2:
               mc.thePlayer.sendChatMessage("!tp " + var1[1]);
               break;
            case 3:
               try {
                  double var13 = Double.parseDouble(var1[1]);
                  double var14 = Double.parseDouble(var1[2]);
                  mc.thePlayer.sendChatMessage(String.format("!tp %s %s", var13, var14));
               } catch (NumberFormatException var12) {
                  ClientChat.CHAT.f("put cords correctly noob");
               }
               break;
            case 4:
               try {
                  double var5 = Double.parseDouble(var1[1]);
                  double var7 = Double.parseDouble(var1[2]);
                  double var9 = Double.parseDouble(var1[3]);
                  mc.thePlayer.sendChatMessage(String.format("!tp %s %s %s", var5, var7, var9));
               } catch (NumberFormatException var11) {
                  ClientChat.CHAT.f("put cords correctly noob");
               }
         }
      }
   }

   public LuaValue b(String var1, LuaValue var2) {
      LuaValue var6 = this.w.get(var1);
      if (!var6.isnil()) {
         try {
            return var6.call(var2);
         } catch (LuaError var8) {
            var8.printStackTrace();
         }
      }

      return null;
   }

   public LuaValue S(String var1) {
      LuaValue var5 = this.w.get(var1);
      if (!var5.isnil()) {
         try {
            return var5.call();
         } catch (LuaError var7) {
            var7.printStackTrace();
         }
      }

      return null;
   }
}
