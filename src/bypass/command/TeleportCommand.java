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
   private final Globals luaGlobals;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      switch (packetEvent.getDirection()) {
         case OUTGOING:
            int i = ScriptModule.getOutgoingPacketId(packetEvent.getPacket());
            if (i == -1) {
               return;
            }

            LuaTable luaTable = LuaValue.tableOf();
            switch (i) {
               case 0:
                  luaTable.set("keep_alive_key", ((C00PacketKeepAlive)packetEvent.getPacket()).getKey());
                  break;
               case 1:
                  luaTable.set("message", ((C01PacketChatMessage)packetEvent.getPacket()).getMessage());
                  break;
               case 2:
                  C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity)packetEvent.getPacket();
                  Entity entity = c02PacketUseEntity.getEntityFromWorld(mc.theWorld);
                  if (entity != null) {
                     luaTable.set("entity_id", entity.getEntityId());
                  }

                  luaTable.set("action_name", c02PacketUseEntity.getAction().name());
                  luaTable.set("action", c02PacketUseEntity.getAction().ordinal() + 1);
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
                  C09PacketHeldItemChange c09PacketHeldItemChange = (C09PacketHeldItemChange)packetEvent.getPacket();
                  luaTable.set("slot", c09PacketHeldItemChange.getSlotId());
                  break;
               case 14:
                  C0EPacketClickWindow c0EPacketClickWindow = (C0EPacketClickWindow)packetEvent.getPacket();
                  luaTable.set("slot", c0EPacketClickWindow.getSlotId());
                  luaTable.set("window_id", c0EPacketClickWindow.getWindowId());
                  luaTable.set("mode", c0EPacketClickWindow.getMode());
                  break;
               case 15:
                  C0FPacketConfirmTransaction c0FPacketConfirmTransaction = (C0FPacketConfirmTransaction)packetEvent.getPacket();
                  luaTable.set("window_id", c0FPacketConfirmTransaction.getWindowId());
                  luaTable.set("uid", c0FPacketConfirmTransaction.getUid());
                  luaTable.set("accept", LuaValue.valueOf(c0FPacketConfirmTransaction.N()));
            }

            luaTable.set("packet_id", i);
            luaTable.set("cancel", LuaValue.valueOf(packetEvent.isCancelled()));
            LuaValue luaValue = this.callLuaFunctionWithArg("on_send_packet", luaTable);
            if (luaValue != null && !luaValue.isnil() && luaValue.istable()) {
               LuaTable luaTable2 = (LuaTable)luaValue;
               LuaValue luaValue2 = luaTable2.get("cancel");
               if (!luaValue2.isnil() && luaValue2.isboolean() && luaValue2.checkboolean()) {
                  packetEvent.setCancelled(true);
               }
            }
            break;
         case RECEIVED:
            int j = ScriptModule.getIncomingPacketId(packetEvent.getPacket());
            if (j == -1) {
               return;
            }

            LuaTable luaTable3 = LuaValue.tableOf();
            luaTable3.set("packet_id", j);
            luaTable3.set("cancel", LuaValue.valueOf(packetEvent.isCancelled()));
            switch (j) {
               case 2:
                  S02PacketChat s02PacketChat = (S02PacketChat)packetEvent.getPacket();
                  luaTable3.set("message", s02PacketChat.getChatComponent().getFormattedText());
                  break;
               case 8:
                  S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook)packetEvent.getPacket();
                  luaTable3.set("pos_x", s08PacketPlayerPosLook.getX());
                  luaTable3.set("pos_y", s08PacketPlayerPosLook.getY());
                  luaTable3.set("pos_z", s08PacketPlayerPosLook.getZ());
                  luaTable3.set("yaw", s08PacketPlayerPosLook.getYaw());
                  luaTable3.set("pitch", s08PacketPlayerPosLook.getPitch());
                  break;
               case 18:
                  S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity)packetEvent.getPacket();
                  luaTable3.set("entity_id", s12PacketEntityVelocity.getEntityID());
                  luaTable3.set("motion_x", s12PacketEntityVelocity.getMotionX());
                  luaTable3.set("motion_y", s12PacketEntityVelocity.getMotionY());
                  luaTable3.set("motion_z", s12PacketEntityVelocity.getMotionZ());
            }

            LuaValue luaValue3 = this.callLuaFunctionWithArg("on_receive_packet", luaTable3);
            if (luaValue3 != null && !luaValue3.isnil() && luaValue3.istable()) {
               LuaValue luaValue4 = luaValue3.get("cancel");
               if (!luaValue4.isnil() && luaValue4.isboolean() && luaValue4.checkboolean()) {
                  packetEvent.setCancelled(true);
               }
            }
      }
   };
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      switch (updateEvent.getState()) {
         case PRE:
            this.callLuaFunction("on_pre_update");
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> this.callLuaFunction("on_player_join");

   public TeleportCommand(String string, String string2, String string3) {
      super(string, string2);
      this.setAliases(Collections.singletonList("tp"));
      this.luaGlobals = new Globals();
      Script.installLibraries(this.luaGlobals);
      this.luaGlobals.load(new ModuleManagerLibrary3(this));
      LuaValue luaValue = LuaGlobals.globals.load(string3, "352675432675", this.luaGlobals);
      luaValue.call();
      client.getEventBus().subscribe(this);
   }

   @Override
   public void runCommand(String[] strings) {
      if (GameUtil.isOnHypixel()) {
         switch (strings.length) {
            case 2:
               mc.thePlayer.sendChatMessage("!tp " + strings[1]);
               break;
            case 3:
               try {
                  double d = Double.parseDouble(strings[1]);
                  double e = Double.parseDouble(strings[2]);
                  mc.thePlayer.sendChatMessage(String.format("!tp %s %s", d, e));
               } catch (NumberFormatException numberFormatException) {
                  ClientChat.CHAT.printError("put cords correctly noob");
               }
               break;
            case 4:
               try {
                  double d2 = Double.parseDouble(strings[1]);
                  double d3 = Double.parseDouble(strings[2]);
                  double d4 = Double.parseDouble(strings[3]);
                  mc.thePlayer.sendChatMessage(String.format("!tp %s %s %s", d2, d3, d4));
               } catch (NumberFormatException numberFormatException2) {
                  ClientChat.CHAT.printError("put cords correctly noob");
               }
         }
      }
   }

   public LuaValue callLuaFunctionWithArg(String string, LuaValue luaValue) {
      LuaValue luaValue2 = this.luaTable.get(string);
      if (!luaValue2.isnil()) {
         try {
            return luaValue2.call(luaValue);
         } catch (LuaError luaError) {
            luaError.printStackTrace();
         }
      }

      return null;
   }

   public LuaValue callLuaFunction(String string) {
      LuaValue luaValue = this.luaTable.get(string);
      if (!luaValue.isnil()) {
         try {
            return luaValue.call();
         } catch (LuaError luaError) {
            luaError.printStackTrace();
         }
      }

      return null;
   }
}
