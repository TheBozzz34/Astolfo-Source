package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveWithHeadingEvent;
import bypass.event.render.RenderEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.lua.ModuleManagerLibrary4;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.scripts.ScriptModule;
import bypass.script.LuaGlobals;
import bypass.script.Script;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
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

public class LegitScaffold extends Module {
   private final MapSetting rotations = new MapSettingBuilder("Rotations")
      .ordered()
      .description("rotations stuff")
      .add("Speed", new NumberSetting<>("Speed", "Speed of aim", 0.3F, 0.0F, 1.0F))
      .add("Randomization", new NumberSetting<>("Randomization", "Randomization of bezier curve", 10.0F, 0.0F, 50.0F))
      .add("Yaw-Random", new NumberSetting<>("Yaw-Random", "Randomization of yaw", 0.3F, 0.0F, 1.0F))
      .add("Pitch-Random", new NumberSetting<>("Pitch-Random", "Randomization of pitch", 0.3F, 0.0F, 1.0F))
      .build();
   private final NumberSetting<Integer> minClickDelay = new NumberSetting<>("Min-Click-Delay", "Min click delay", 1, 1, 5);
   private final NumberSetting<Integer> maxClickDelay = new NumberSetting<>("Max-Click-Delay", "Max Distance in blocks", 2, 1, 5);
   private final NumberSetting<Integer> minSneakDelay = new NumberSetting<>("Min-Sneak-Delay", "Min sneak delay", 1, 1, 5);
   private final NumberSetting<Integer> maxSneakDelay = new NumberSetting<>("Max-Sneak-Delay", "Max Distance in blocks", 2, 1, 5);
   private final NumberSetting<Integer> sneakAdjust = new NumberSetting<>("Sneak-Adjust", "Adjusts sneak delay  ", 2, 1, 5);
   private final NumberSetting<Float> movementTolerance = new NumberSetting<>("Movement-Tolerance", "", 0.25F, 0.0F, 0.5F);
   private final EnumSetting<LegitScaffoldFlatMode> flatMode = new EnumSetting<>("Flat-Mode", "Straight-Flat-Mode", LegitScaffoldFlatMode.BACK_NINJA);
   private final EnumSetting<LegitScaffoldStairsMode> stairsMode = new EnumSetting<>(
      "Stairs-Mode", "Straight-Stairs-Mode", LegitScaffoldStairsMode.BACK_1_STACK
   );
   private final Globals globals;
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = var1x -> this.callScript("on_player_join");
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
               LuaTable var7x = (LuaTable)var6;
               LuaValue var8 = var7x.get("x");
               LuaValue var9 = var7x.get("y");
               LuaValue var10 = var7x.get("z");
               LuaValue var11 = var7x.get("yaw");
               LuaValue var12 = var7x.get("pitch");
               LuaValue var13 = var7x.get("on_ground");
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
            int var7x = Mouse.getX() * var1x.getResolution().getScaledWidth() / mc.displayWidth;
            int var8 = var6 - Mouse.getY() * var6 / mc.displayHeight - 1;
            LuaTable var9 = LuaValue.tableOf();
            var9.set("partial_ticks", var1x.getPartialTicks());
            var9.set("width", var1x.getResolution().getScaledWidth());
            var9.set("height", var1x.getResolution().getScaledHeight());
            var9.set("scale_factor", var1x.getResolution().getScaleFactor());
            var9.set("mouse_x", var7x);
            var9.set("mouse_y", var8);
            this.callScript("on_render_screen", var9);
      }
   };
   private static final long c = 138694887050059L;

   public LegitScaffold(Category var1, String var2) {
      super(var1);
      this.addSettings(
         this.rotations,
         this.flatMode,
         this.stairsMode,
         this.movementTolerance,
         this.minClickDelay,
         this.maxClickDelay,
         this.minSneakDelay,
         this.maxSneakDelay,
         this.sneakAdjust
      );
      this.globals = new Globals();
      Script.w(this.globals);
      this.globals.load(new ModuleManagerLibrary4(this));
      LuaValue var7 = LuaGlobals.globals.load(var2, "563726532", this.globals);
      var7.call();
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
