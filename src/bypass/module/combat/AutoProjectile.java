package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.render.RenderEvent;
import bypass.lua.ModuleManagerLibrary4;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.scripts.ScriptModule;
import bypass.script.LuaGlobals;
import bypass.script.Script;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import java.awt.Color;
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
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.lwjgl.input.Mouse;

public class AutoProjectile extends Module {
   private final MapSetting entities = new MapSettingBuilder("Entities")
      .ordered()
      .description("killaura entiti")
      .add("Player-Only", new BooleanSetting("Player-Only", "Add players to Entities", true))
      .add("Teams", new BooleanSetting("Teams", "Add teams to Entities", false))
      .add("Hurt-Time", new NumberSetting<>("Hurt-Time", "Hurt time", 10, 0, 10))
      .build();
   private final MapSetting rotations = new MapSettingBuilder("Rotations")
      .ordered()
      .description("rotations stuff")
      .add("FOV", new NumberSetting<>("FOV", "field of view", 180, 0, 360, "º"))
      .add("Aim-Height", new NumberSetting<>("Aim-Height", "Aim-Height", 0.75F, 0.1F, 1.0F))
      .add("Speed", new NumberSetting<>("Speed", "Speed of aim", 0.25F, 0.1F, 1.0F))
      .add("Randomization", new NumberSetting<>("Randomization", "Randomization of aim", 10.0F, 0.0F, 50.0F))
      .add("Lock-View", new BooleanSetting("Lock-View", "Locks your view", false))
      .build();
   private final MapSetting prediction = new MapSettingBuilder("Prediction")
      .ordered()
      .description("rotations stuff")
      .add("Add", new NumberSetting<>("Add", "field of view", 5.0F, 0.0F, 20.0F))
      .add("Multiplier", new NumberSetting<>("Multiplier", "Aim-Height", 1.0F, 1.0F, 2.0F))
      .build();
   private final MapSetting render = new MapSettingBuilder("Render")
      .ordered()
      .description("Render stuff")
      .add("Target-ESP", new BooleanSetting("Target-ESP", "Target-ESP", true))
      .add("Target-Color", new ColorSetting("Target-Color", "color", Color.RED))
      .add("Target-Tracer", new BooleanSetting("Target-Tracer", "Target-Tracer", false))
      .add("Prediction-ESP", new BooleanSetting("Prediction-ESP", "Prediction-ESP", false))
      .add("Prediction-Color", new ColorSetting("Prediction-Color", "color", Color.YELLOW))
      .add("Prediction-Tracer", new BooleanSetting("Prediction-Tracer", "Prediction-Tracer", false))
      .add("Thrown-ESP", new BooleanSetting("Thrown-ESP", "Thrown-ESP", false))
      .add("Thrown-Color", new ColorSetting("Thrown-Color", "color", Color.WHITE))
      .build();
   private final EnumSetting<AutoProjectileSortMode> sortMode = new EnumSetting<>("Sort-Mode", "Sorting mode", AutoProjectileSortMode.ANGLE);
   private final NumberSetting<Integer> minDistance = new NumberSetting<>("Min-Distance", "Min Distance in blocks", 5, 1, 100, " blocks");
   private final NumberSetting<Integer> maxDistance = new NumberSetting<>("Max-Distance", "Max Distance in blocks", 20, 1, 100);
   private final NumberSetting<Integer> minDelay = new NumberSetting<>("Min-Delay", "Min delay", 5, 1, 20);
   private final NumberSetting<Integer> maxDelay = new NumberSetting<>("Max-Delay", "Max delay", 5, 1, 20);
   private final NumberSetting<Integer> slot = new NumberSetting<>("Slot", "Slot", 5, 1, 9);
   private final BooleanSetting hotbarOnly = new BooleanSetting("Hotbar-Only", "Hotbar-Only", true);
   private final Globals globals;
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

            LuaValue luaValue3 = this.callScript("on_receive_packet", luaTable3);
            if (luaValue3 != null && !luaValue3.isnil() && luaValue3.istable()) {
               LuaValue luaValue4 = luaValue3.get("cancel");
               if (!luaValue4.isnil() && luaValue4.isboolean() && luaValue4.checkboolean()) {
                  packetEvent.setCancelled(true);
               }
            }
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

   public AutoProjectile(Category category, String string) {
      super(category);
      this.addSettings(
         this.entities,
         this.rotations,
         this.prediction,
         this.render,
         this.sortMode,
         this.hotbarOnly,
         this.minDistance,
         this.maxDistance,
         this.minDelay,
         this.maxDelay,
         this.slot
      );
      this.globals = new Globals();
      Script.installLibraries(this.globals);
      this.globals.load(new ModuleManagerLibrary4(this));
      LuaValue luaValue = LuaGlobals.globals.load(string, "754362327", this.globals);
      luaValue.call();
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
}
