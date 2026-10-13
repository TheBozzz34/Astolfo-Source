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
   private Listener<PacketEvent> onPacket = var1x -> {
      switch (var1x.getDirection()) {
         case OUTGOING:
            int var5 = getOutgoingPacketId(var1x.getPacket());
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
                  C02PacketUseEntity var7 = (C02PacketUseEntity)var1x.getPacket();
                  Entity var14 = var7.getEntityFromWorld(mc.theWorld);
                  if (var14 != null) {
                     var6.set("entity_id", var14.getEntityId());
                  }

                  var6.set("action_name", var7.getAction().name());
                  var6.set("action", var7.getAction().ordinal() + 1);
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
            LuaValue var13 = this.callScript("on_send_packet", var6);
            if (var13 != null && !var13.isnil() && var13.istable()) {
               LuaTable var15 = (LuaTable)var13;
               LuaValue var17 = var15.get("cancel");
               if (!var17.isnil() && var17.isboolean() && var17.checkboolean()) {
                  var1x.setCancelled(true);
               }
            }
            break;
         case RECEIVED:
            int var8 = getIncomingPacketId(var1x.getPacket());
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

            LuaValue var18 = this.callScript("on_receive_packet", var9);
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
            this.callScript("on_pre_update");
            break;
         case POST:
            this.callScript("on_post_update");
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = var1x -> {
      if (var1x.getState() == EventState.PRE) {
         LuaTable var5 = LuaValue.tableOf();
         var5.set("x", var1x.getX());
         var5.set("y", var1x.getY());
         var5.set("z", var1x.getZ());
         LuaValue var6 = this.callScript("on_player_move", var5);
         if (var6 != null && !var6.isnil() && var6.istable()) {
            LuaTable var7 = (LuaTable)var6;
            LuaValue var8 = var7.get("x");
            if (var8 != null && !var8.isnil() && var8.isnumber()) {
               var1x.setX(var8.tofloat());
            }

            LuaValue var9 = var7.get("y");
            if (var9 != null && !var9.isnil() && var9.isnumber()) {
               var1x.setY(var9.tofloat());
            }

            LuaValue var10 = var7.get("z");
            if (var10 != null && !var10.isnil() && var10.isnumber()) {
               var1x.setZ(var10.tofloat());
            }
         }
      }
   };
   @EventHandler(MoveWithHeadingEvent.class)
   private Listener<MoveWithHeadingEvent> onMoveWithHeading = var1x -> {
      switch (var1x.getState()) {
         case PRE:
            LuaTable var5 = LuaValue.tableOf(0, 3);
            var5.set("strafe", var1x.getForward());
            var5.set("forward", var1x.getStrafe());
            LuaValue var6 = this.callScript("on_pre_player_input", var5);
            if (var6 != null && !var6.isnil() && var6.istable()) {
               var1x.setForward(var6.get("strafe").tofloat());
               var1x.setStrafe(var6.get("forward").tofloat());
            }
            break;
         case POST:
            this.callScript("on_post_player_input");
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = var1x -> this.callScript("on_player_join");
   @EventHandler(MouseClickEvent.class)
   private Listener<MouseClickEvent> onMouseClick = var1x -> this.callScript("on_click", LuaValue.valueOf(var1x.button));
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      switch (var1x.getState()) {
         case PRE:
            LuaTable var5 = LuaValue.tableOf();
            var5.set("x", var1x.getX());
            var5.set("y", var1x.getMinY());
            var5.set("z", var1x.getZ());
            var5.set("yaw", var1x.getYaw());
            var5.set("pitch", var1x.getPitch());
            var5.set("on_ground", LuaValue.valueOf(var1x.isOnGround()));
            LuaValue var6 = this.callScript("on_pre_motion", var5);
            if (var6 != null && !var6.isnil() && var6.istable()) {
               LuaTable var7 = (LuaTable)var6;
               LuaValue var8 = var7.get("x");
               LuaValue var9 = var7.get("y");
               LuaValue var10 = var7.get("z");
               LuaValue var11 = var7.get("yaw");
               LuaValue var12 = var7.get("pitch");
               LuaValue var13 = var7.get("on_ground");
               if (!var8.isnil() && var8.isnumber()) {
                  var1x.setX(var8.checkdouble());
               }

               if (!var9.isnil() && var9.isnumber()) {
                  var1x.setMinY(var9.checkdouble());
               }

               if (!var10.isnil() && var10.isnumber()) {
                  var1x.setZ(var10.checkdouble());
               }

               if (!var11.isnil() && var11.isnumber()) {
                  var1x.setYaw(var11.tofloat());
               }

               if (!var12.isnil() && var12.isnumber()) {
                  var1x.setPitch(var12.tofloat());
               }

               if (!var13.isnil() && var13.isboolean()) {
                  var1x.setOnGround(var13.checkboolean());
               }
            }
            break;
         case POST:
            this.callScript("on_post_motion");
      }
   };
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = var1x -> {
      switch (var1x.getType()) {
         case WORLD:
            LuaTable var5 = LuaValue.tableOf();
            var5.set("partial_ticks", var1x.getPartialTicks());
            this.callScript("on_render_world", var5);
            break;
         case SCREEN:
            int var6 = var1x.getResolution().getScaledHeight();
            int var7 = Mouse.getX() * var1x.getResolution().getScaledWidth() / mc.displayWidth;
            int var8 = var6 - Mouse.getY() * var6 / mc.displayHeight - 1;
            LuaTable var9 = LuaValue.tableOf();
            var9.set("partial_ticks", var1x.getPartialTicks());
            var9.set("width", var1x.getResolution().getScaledWidth());
            var9.set("height", var1x.getResolution().getScaledHeight());
            var9.set("scale_factor", var1x.getResolution().getScaleFactor());
            var9.set("mouse_x", var7);
            var9.set("mouse_y", var8);
            this.callScript("on_render_screen", var9);
      }
   };
   private static final long e = 3956703315578L;

   public ScriptModule(String var1, LuaTable var2) {
      super(var1, Category.SCRIPTS);
      this.callbacks = var2;
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

   public static int getOutgoingPacketId(Packet<?> var0) {
      if (var0 instanceof C00PacketKeepAlive) {
         return 0;
      } else if (var0 instanceof C01PacketChatMessage) {
         return 1;
      } else if (var0 instanceof C02PacketUseEntity) {
         return 2;
      } else if (var0 instanceof C03PacketPlayer.C04PacketPlayerPosition) {
         return 4;
      } else if (var0 instanceof C03PacketPlayer.C05PacketPlayerLook) {
         return 5;
      } else if (var0 instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
         return 6;
      } else if (var0 instanceof C03PacketPlayer) {
         return 3;
      } else if (var0 instanceof C07PacketPlayerDigging) {
         return 7;
      } else if (var0 instanceof C08PacketPlayerBlockPlacement) {
         return 8;
      } else if (var0 instanceof C09PacketHeldItemChange) {
         return 9;
      } else if (var0 instanceof C0APacketAnimation) {
         return 10;
      } else if (var0 instanceof C0BPacketEntityAction) {
         return 11;
      } else if (var0 instanceof C0CPacketInput) {
         return 12;
      } else if (var0 instanceof C0DPacketCloseWindow) {
         return 13;
      } else if (var0 instanceof C0EPacketClickWindow) {
         return 14;
      } else if (var0 instanceof C0FPacketConfirmTransaction) {
         return 15;
      } else if (var0 instanceof C10PacketCreativeInventoryAction) {
         return 16;
      } else if (var0 instanceof C11PacketEnchantItem) {
         return 17;
      } else if (var0 instanceof C12PacketUpdateSign) {
         return 18;
      } else if (var0 instanceof C13PacketPlayerAbilities) {
         return 19;
      } else if (var0 instanceof C14PacketTabComplete) {
         return 20;
      } else if (var0 instanceof C15PacketClientSettings) {
         return 21;
      } else if (var0 instanceof C16PacketClientStatus) {
         return 22;
      } else if (var0 instanceof C17PacketCustomPayload) {
         return 23;
      } else if (var0 instanceof C18PacketSpectate) {
         return 24;
      } else {
         return var0 instanceof C19PacketResourcePackStatus ? 25 : -1;
      }
   }

   public static int getIncomingPacketId(Packet<?> var0) {
      if (var0 instanceof S02PacketChat) {
         return 2;
      } else if (var0 instanceof S08PacketPlayerPosLook) {
         return 8;
      } else {
         return var0 instanceof S12PacketEntityVelocity ? 18 : -1;
      }
   }

   @Override
   public LuaValue callScript(String var1, LuaValue var2) {
      LuaValue var6 = this.callbacks.get(var1);
      if (!var6.isnil()) {
         try {
            return var6.call(var2);
         } catch (LuaError var8) {
            var8.printStackTrace();
            chat.f(var8.getMessage());
         }
      }

      return null;
   }

   @Override
   public LuaValue callScript(String var1) {
      LuaValue var5 = this.callbacks.get(var1);
      if (!var5.isnil()) {
         try {
            return var5.call();
         } catch (LuaError var7) {
            var7.printStackTrace();
            chat.f(var7.getMessage());
         }
      }

      return null;
   }
}
