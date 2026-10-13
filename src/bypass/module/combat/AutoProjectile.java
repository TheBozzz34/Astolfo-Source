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
   private Listener<PacketEvent> onPacket = var1x -> {
      switch (var1x.getDirection()) {
         case OUTGOING:
            int var5 = ScriptModule.getOutgoingPacketId(var1x.getPacket());
            if (var5 == -1) {
               return;
            }

            LuaTable var6x = LuaValue.tableOf();
            switch (var5) {
               case 0:
                  var6x.set("keep_alive_key", ((C00PacketKeepAlive)var1x.getPacket()).getKey());
                  break;
               case 1:
                  var6x.set("message", ((C01PacketChatMessage)var1x.getPacket()).getMessage());
                  break;
               case 2:
                  C02PacketUseEntity var7 = (C02PacketUseEntity)var1x.getPacket();
                  Entity var14 = var7.getEntityFromWorld(mc.theWorld);
                  if (var14 != null) {
                     var6x.set("entity_id", var14.getEntityId());
                  }

                  var6x.set("action_name", var7.getAction().name());
                  var6x.set("action", var7.getAction().ordinal() + 1);
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
                  var6x.set("slot", var19.getSlotId());
                  break;
               case 14:
                  C0EPacketClickWindow var21 = (C0EPacketClickWindow)var1x.getPacket();
                  var6x.set("slot", var21.getSlotId());
                  var6x.set("window_id", var21.getWindowId());
                  var6x.set("mode", var21.getMode());
                  break;
               case 15:
                  C0FPacketConfirmTransaction var16 = (C0FPacketConfirmTransaction)var1x.getPacket();
                  var6x.set("window_id", var16.getWindowId());
                  var6x.set("uid", var16.getUid());
                  var6x.set("accept", LuaValue.valueOf(var16.N()));
            }

            var6x.set("packet_id", var5);
            var6x.set("cancel", LuaValue.valueOf(var1x.isCancelled()));
            LuaValue var13 = this.callScript("on_send_packet", var6x);
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

            LuaValue var18 = this.callScript("on_receive_packet", var9);
            if (var18 != null && !var18.isnil() && var18.istable()) {
               LuaValue var20 = var18.get("cancel");
               if (!var20.isnil() && var20.isboolean() && var20.checkboolean()) {
                  var1x.setCancelled(true);
               }
            }
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
            int var6x = var1x.getResolution().getScaledHeight();
            int var7 = Mouse.getX() * var1x.getResolution().getScaledWidth() / mc.displayWidth;
            int var8 = var6x - Mouse.getY() * var6x / mc.displayHeight - 1;
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
            LuaValue var6x = this.callScript("on_pre_motion", var5);
            if (var6x != null && !var6x.isnil() && var6x.istable()) {
               LuaTable var7 = (LuaTable)var6x;
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
   private static final long c = 90934864379768L;

   public AutoProjectile(Category var1, String var2) {
      super(var1);
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
      Script.w(this.globals);
      this.globals.load(new ModuleManagerLibrary4(this));
      LuaValue var6 = LuaGlobals.globals.load(var2, "754362327", this.globals);
      var6.call();
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
