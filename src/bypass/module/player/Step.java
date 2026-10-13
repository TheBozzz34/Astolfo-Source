package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.StepEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.exploits.RiderGod;
import bypass.module.movement.Flight;
import bypass.module.movement.NoFall;
import bypass.module.movement.Speed;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.Stopwatch;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.EnumChatFormatting;

public final class Step extends Module {
   private final EnumSetting<StepMode> mode = new EnumSetting<>("Mode", "Set your step mode", StepMode.NCP);
   private final NumberSetting<Float> height = new NumberSetting<>("Height", "Amount to step", 1.0F, 1.0F, 2.5F);
   private final NumberSetting<Integer> motionTick = new NumberSetting<>("Motion-Tick", "Motion-Tick", 2, 1, 5);
   private final BooleanSetting reverse = new BooleanSetting("Reverse", "Reverse step", false);
   private final Stopwatch Y = new Stopwatch();
   private int V;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> this.p();
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.PRE && this.reverse.getValue() && mc.thePlayer.onGround && !mc.thePlayer.isInWater() && NoFall.P_boolean()) {
         mc.thePlayer.motionY = -9.99;
      }
   };
   private boolean t;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      long var2 = d ^ 86242262513195L;
      long var4 = var2 ^ 18584933221786L;
      if (var1x.getDirection() == PacketDirection.OUTGOING && var1x.getPacket() instanceof C03PacketPlayer) {
         if (this.V >= 0) {
            this.V++;
         }

         if (this.V <= -1 && !client.getModuleManager().get(Flight.class).isEnabled() && this.t) {
            mc.timer.timerSpeed = 1.0F;
            this.t = false;
         }

         if (this.mode.getValue() == StepMode.MOTION && this.V >= this.motionTick.getValue()) {
            mc.timer.timerSpeed = 1.0F;
            this.V = -1;
         }

         if (R > 0) {
            R--;
         }
      }
   };
   public static int R;
   public double[] u = new double[]{
      0.4199935232546, 0.75323672435624, 1.0 + Math.random() / 10000.0, 1.0 + Math.random() / 100.0, 0.98 + Math.random() / 1000.0
   };
   @EventHandler(StepEvent.class)
   private Listener<StepEvent> onStep = var1x -> {
      long var2 = d ^ 45406839570829L;
      long var4 = var2 ^ 131687618392636L;
      if (mc.thePlayer != null) {
         if ((this.mode.getValue() == StepMode.NCP || this.mode.getValue() == StepMode.VANILLA || this.mode.getValue() == StepMode.MOTION)
            && var1x.getEntity() == mc.thePlayer) {
            switch (var1x.getState()) {
               case PRE:
                  if (var1x.getEntity().isInWater()
                     || var1x.getEntity().isInLava()
                     || client.getModuleManager().get(Speed.class).isEnabled()
                     || !mc.thePlayer.onGround
                     || !mc.thePlayer.isCollidedVertically
                     || mc.thePlayer.movementInput.jump) {
                     var1x.getEntity().stepHeight = 0.6F;
                     return;
                  }

                  if (var1x.getVec().yCoord <= this.height.getValue().floatValue()) {
                     var1x.getEntity().stepHeight = this.height.getValue();
                     if (this.mode.getValue() == StepMode.VANILLA) {
                        return;
                     }

                     if (var1x.getVec().yCoord >= 0.7 && var1x.getVec().yCoord <= 1.5
                        || var1x.getVec().yCoord > 1.5 && var1x.getVec().yCoord <= 2.0
                        || var1x.getVec().yCoord <= 2.6 && var1x.getVec().yCoord > 2.1) {
                        float var7 = 0.41900033F;
                        double var8 = 0.9800000190734864;
                        double var10 = 0.078400001525879;
                        byte var12 = 0;
                        double[] var13 = new double[0];
                        double var14 = var1x.getVec().yCoord;
                        if (var14 > 0.7 && var14 <= 1.1) {
                           var13 = new double[]{var14 / 2.380932455289242, var14 / 1.328668648249321};
                        } else if (var14 <= 1.4) {
                           var12 = 4;
                           var7 = 0.419998F;
                        } else if (var14 == 1.5) {
                           var12 = 6;
                           var7 = 0.4F;
                           var13 = this.u;
                        } else if (var14 > 1.5 && var14 <= 2.0) {
                           var13 = new double[]{
                              0.400000005960464,
                              0.713600017905236,
                              0.942528034066676,
                              0.875187243260925,
                              0.63048350884201,
                              0.5,
                              0.900000005960464,
                              1.213600017905236,
                              1.442528034066676,
                              1.58847749274547,
                              1.65310796350857,
                              1.63804582456326
                           };
                        } else if (var14 <= 2.6 && var14 > 2.1) {
                           var13 = new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
                        }

                        if (var13.length == 0) {
                           var13 = new double[var12];
                           double var16 = 0.0;

                           for (int var18 = 0; var18 < var13.length; var18++) {
                              var16 += var7;
                              var7 = (float)(var7 - var10);
                              var7 = (float)(var7 * var8);
                              var13[var18] = var16;
                           }
                        }

                        if (this.mode.getValue() == StepMode.MOTION) {
                           mc.timer.timerSpeed = 0.38F;
                        }

                        this.t = true;
                        if (this.V == -1) {
                           this.V = 0;
                        }

                        for (double var19 : var13) {
                           if (client.getModuleManager().get(RiderGod.class).isEnabled()) {
                              break;
                           }

                           if (GameUtil.isOnHypixel() && var14 > 0.7 && var14 <= 1.1) {
                              for (int var21 = 0; var21 < 4; var21++) {
                                 mc.thePlayer.sendQueue.u(new C03PacketPlayer(true));
                              }
                           }

                           mc.thePlayer
                              .sendQueue
                              .u(
                                 new C03PacketPlayer.C04PacketPlayerPosition(
                                    mc.thePlayer.posX, mc.thePlayer.posY + var19 + Math.random() / 100000.0, mc.thePlayer.posZ, false
                                 )
                              );
                        }

                        R = 0;
                        this.Y.reset();
                     }
                  }
                  break;
               case POST:
                  var1x.getEntity().stepHeight = 0.6F;
            }
         }
      }
   };
   private static final long d = 116544757911042L;

   public Step(Category var1) {
      super(var1);
      this.addSettings(this.mode, this.height, this.motionTick, this.reverse);
   }

   @Override
   public void onDisable() {
      if (mc.theWorld != null && mc.thePlayer != null) {
         mc.timer.timerSpeed = 1.0F;
         mc.thePlayer.stepHeight = 0.6F;
      }

      super.onDisable();
   }

   @Override
   public void p() {
      this.setDisplayName(this.getName() + " " + EnumChatFormatting.GRAY + StepMode.l(this.mode.getValue()));
      super.p();
   }
}
