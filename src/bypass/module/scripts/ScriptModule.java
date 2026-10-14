package bypass.module.scripts;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.game.MouseClickEvent;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.event.player.MoveWithHeadingEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.RenderEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.lwjgl.input.Mouse;

public final class ScriptModule extends Module {
   private final LuaTable callbacks;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      switch (packetEvent.getDirection()) {
         case OUTGOING:
            int i = getOutgoingPacketId(packetEvent.getPacket());
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
            LuaValue luaValue = this.callScript("on_send_packet", luaTable);
            if (luaValue != null && !luaValue.isnil() && luaValue.istable()) {
               LuaTable luaTable2 = (LuaTable)luaValue;
               LuaValue luaValue2 = luaTable2.get("cancel");
               if (!luaValue2.isnil() && luaValue2.isboolean() && luaValue2.checkboolean()) {
                  packetEvent.setCancelled(true);
               }
            }
            break;
         case RECEIVED:
            int j = getIncomingPacketId(packetEvent.getPacket());
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

            LuaValue luaValue3 = this.callScript("on_receive_packet", luaTable3);
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
            this.callScript("on_pre_update");
            break;
         case POST:
            this.callScript("on_post_update");
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = moveEvent -> {
      if (moveEvent.getState() == EventState.PRE) {
         LuaTable luaTable = LuaValue.tableOf();
         luaTable.set("x", moveEvent.getX());
         luaTable.set("y", moveEvent.getY());
         luaTable.set("z", moveEvent.getZ());
         LuaValue luaValue = this.callScript("on_player_move", luaTable);
         if (luaValue != null && !luaValue.isnil() && luaValue.istable()) {
            LuaTable luaTable2 = (LuaTable)luaValue;
            LuaValue luaValue2 = luaTable2.get("x");
            if (luaValue2 != null && !luaValue2.isnil() && luaValue2.isnumber()) {
               moveEvent.setX(luaValue2.tofloat());
            }

            LuaValue luaValue3 = luaTable2.get("y");
            if (luaValue3 != null && !luaValue3.isnil() && luaValue3.isnumber()) {
               moveEvent.setY(luaValue3.tofloat());
            }

            LuaValue luaValue4 = luaTable2.get("z");
            if (luaValue4 != null && !luaValue4.isnil() && luaValue4.isnumber()) {
               moveEvent.setZ(luaValue4.tofloat());
            }
         }
      }
   };
   @EventHandler(MoveWithHeadingEvent.class)
   private Listener<MoveWithHeadingEvent> onMoveWithHeading = moveWithHeadingEvent -> {
      switch (moveWithHeadingEvent.getState()) {
         case PRE:
            LuaTable luaTable = LuaValue.tableOf(0, 3);
            luaTable.set("strafe", moveWithHeadingEvent.getForward());
            luaTable.set("forward", moveWithHeadingEvent.getStrafe());
            LuaValue luaValue = this.callScript("on_pre_player_input", luaTable);
            if (luaValue != null && !luaValue.isnil() && luaValue.istable()) {
               moveWithHeadingEvent.setForward(luaValue.get("strafe").tofloat());
               moveWithHeadingEvent.setStrafe(luaValue.get("forward").tofloat());
            }
            break;
         case POST:
            this.callScript("on_post_player_input");
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> this.callScript("on_player_join");
   @EventHandler(MouseClickEvent.class)
   private Listener<MouseClickEvent> onMouseClick = mouseClickEvent -> this.callScript("on_click", LuaValue.valueOf(mouseClickEvent.button));
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      switch (motionEvent.getState()) {
         case PRE:
            LuaTable luaTable = LuaValue.tableOf();
            luaTable.set("x", motionEvent.getX());
            luaTable.set("y", motionEvent.getMinY());
            luaTable.set("z", motionEvent.getZ());
            luaTable.set("yaw", motionEvent.getYaw());
            luaTable.set("pitch", motionEvent.getPitch());
            luaTable.set("on_ground", LuaValue.valueOf(motionEvent.isOnGround()));
            LuaValue luaValue = this.callScript("on_pre_motion", luaTable);
            if (luaValue != null && !luaValue.isnil() && luaValue.istable()) {
               LuaTable luaTable2 = (LuaTable)luaValue;
               LuaValue luaValue2 = luaTable2.get("x");
               LuaValue luaValue3 = luaTable2.get("y");
               LuaValue luaValue4 = luaTable2.get("z");
               LuaValue luaValue5 = luaTable2.get("yaw");
               LuaValue luaValue6 = luaTable2.get("pitch");
               LuaValue luaValue7 = luaTable2.get("on_ground");
               if (!luaValue2.isnil() && luaValue2.isnumber()) {
                  motionEvent.setX(luaValue2.checkdouble());
               }

               if (!luaValue3.isnil() && luaValue3.isnumber()) {
                  motionEvent.setMinY(luaValue3.checkdouble());
               }

               if (!luaValue4.isnil() && luaValue4.isnumber()) {
                  motionEvent.setZ(luaValue4.checkdouble());
               }

               if (!luaValue5.isnil() && luaValue5.isnumber()) {
                  motionEvent.setYaw(luaValue5.tofloat());
               }

               if (!luaValue6.isnil() && luaValue6.isnumber()) {
                  motionEvent.setPitch(luaValue6.tofloat());
               }

               if (!luaValue7.isnil() && luaValue7.isboolean()) {
                  motionEvent.setOnGround(luaValue7.checkboolean());
               }
            }
            break;
         case POST:
            this.callScript("on_post_motion");
      }
   };
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = renderEvent -> {
      switch (renderEvent.getType()) {
         case WORLD:
            LuaTable luaTable = LuaValue.tableOf();
            luaTable.set("partial_ticks", renderEvent.getPartialTicks());
            this.callScript("on_render_world", luaTable);
            break;
         case SCREEN:
            int i = renderEvent.getResolution().getScaledHeight();
            int j = Mouse.getX() * renderEvent.getResolution().getScaledWidth() / mc.displayWidth;
            int k = i - Mouse.getY() * i / mc.displayHeight - 1;
            LuaTable luaTable2 = LuaValue.tableOf();
            luaTable2.set("partial_ticks", renderEvent.getPartialTicks());
            luaTable2.set("width", renderEvent.getResolution().getScaledWidth());
            luaTable2.set("height", renderEvent.getResolution().getScaledHeight());
            luaTable2.set("scale_factor", renderEvent.getResolution().getScaleFactor());
            luaTable2.set("mouse_x", j);
            luaTable2.set("mouse_y", k);
            this.callScript("on_render_screen", luaTable2);
      }
   };

   public ScriptModule(String string, LuaTable luaTable) {
      super(string, Category.SCRIPTS);
      this.callbacks = luaTable;
   }

   @Override
   public void onEnable() {
      this.callScript("on_enable");
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.callScript("on_disable");
      super.onDisable();
   }

   public static int getOutgoingPacketId(Packet<?> packet) {
      if (packet instanceof C00PacketKeepAlive) {
         return 0;
      } else if (packet instanceof C01PacketChatMessage) {
         return 1;
      } else if (packet instanceof C02PacketUseEntity) {
         return 2;
      } else if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition) {
         return 4;
      } else if (packet instanceof C03PacketPlayer.C05PacketPlayerLook) {
         return 5;
      } else if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
         return 6;
      } else if (packet instanceof C03PacketPlayer) {
         return 3;
      } else if (packet instanceof C07PacketPlayerDigging) {
         return 7;
      } else if (packet instanceof C08PacketPlayerBlockPlacement) {
         return 8;
      } else if (packet instanceof C09PacketHeldItemChange) {
         return 9;
      } else if (packet instanceof C0APacketAnimation) {
         return 10;
      } else if (packet instanceof C0BPacketEntityAction) {
         return 11;
      } else if (packet instanceof C0CPacketInput) {
         return 12;
      } else if (packet instanceof C0DPacketCloseWindow) {
         return 13;
      } else if (packet instanceof C0EPacketClickWindow) {
         return 14;
      } else if (packet instanceof C0FPacketConfirmTransaction) {
         return 15;
      } else if (packet instanceof C10PacketCreativeInventoryAction) {
         return 16;
      } else if (packet instanceof C11PacketEnchantItem) {
         return 17;
      } else if (packet instanceof C12PacketUpdateSign) {
         return 18;
      } else if (packet instanceof C13PacketPlayerAbilities) {
         return 19;
      } else if (packet instanceof C14PacketTabComplete) {
         return 20;
      } else if (packet instanceof C15PacketClientSettings) {
         return 21;
      } else if (packet instanceof C16PacketClientStatus) {
         return 22;
      } else if (packet instanceof C17PacketCustomPayload) {
         return 23;
      } else if (packet instanceof C18PacketSpectate) {
         return 24;
      } else {
         return packet instanceof C19PacketResourcePackStatus ? 25 : -1;
      }
   }

   public static int getIncomingPacketId(Packet<?> packet) {
      if (packet instanceof S02PacketChat) {
         return 2;
      } else if (packet instanceof S08PacketPlayerPosLook) {
         return 8;
      } else {
         return packet instanceof S12PacketEntityVelocity ? 18 : -1;
      }
   }

   @Override
   public LuaValue callScript(String string, LuaValue luaValue) {
      LuaValue luaValue2 = this.callbacks.get(string);
      if (!luaValue2.isnil()) {
         try {
            return luaValue2.call(luaValue);
         } catch (LuaError luaError) {
            luaError.printStackTrace();
            chat.f(luaError.getMessage());
         }
      }

      return null;
   }

   @Override
   public LuaValue callScript(String string) {
      LuaValue luaValue = this.callbacks.get(string);
      if (!luaValue.isnil()) {
         try {
            return luaValue.call();
         } catch (LuaError luaError) {
            luaError.printStackTrace();
            chat.f(luaError.getMessage());
         }
      }

      return null;
   }
}
