package bypass.module.movement;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.event.player.TeleportEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.player.Scaffold;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.Stopwatch;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public final class AntiFall extends Module {
   private final EnumSetting<AntiFallMode> mode = new EnumSetting<>("Mode", "Set your AntiFall mode", AntiFallMode.SET_BACK);
   private final NumberSetting<Integer> height = new NumberSetting<>("Height", "Height", 2, 1, 10);
   private final BooleanSetting voidOnly = new BooleanSetting("Void-Only", "Prevents you from void only", true);
   private Vec3 C;
   private final Stopwatch y = new Stopwatch();
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = var1x -> this.C = null;
   private Stopwatch P = new Stopwatch();
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      this.p();
      if (var1x.getState() == EventState.PRE && mc.thePlayer != null) {
         boolean var5 = NoFall.u_boolean();
         if (mc.thePlayer.onGround && !var5 && mc.thePlayer.isCollidedVertically && !Scaffold.isOnEdge()) {
            this.C = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            this.P.reset();
         }

         if (this.mode.getValue() == AntiFallMode.LAG
            && this.h()
            && this.C != null
            && mc.thePlayer.posY - this.C.yCoord < -this.height.getValue()
            && mc.thePlayer.ticksExisted % 5 == 0) {
            mc.thePlayer.setPosition(this.C.xCoord, this.C.yCoord, this.C.zCoord);
            this.y.reset();
         }
      }
   };
   private boolean N;
   private int A;
   private boolean Q;
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = var1x -> {
      if (mc.thePlayer != null) {
         if (var1x.getDirection() == PacketDirection.OUTGOING) {
            if (var1x.getPacket() instanceof C03PacketPlayer && this.h() && this.mode.getValue() == AntiFallMode.LAG) {
               var1x.cancel();
            }
         } else if (var1x.getPacket() instanceof S08PacketPlayerPosLook && this.Q) {
            mc.thePlayer.fallDistance = 0.0F;
         }
      }
   };
   @EventHandler(TeleportEvent.class)
   private Listener<TeleportEvent> onTeleport = var1x -> {
      if (var1x.y()) {
         this.A++;
         if (this.A > 10) {
            this.N = true;
         }
      }
   };
   @EventHandler(value = MoveEvent.class, priority = EventPriority.HIGHEST)
   private Listener<MoveEvent> onMove = var1x -> {
      if (var1x.getState() == EventState.PRE && this.Q) {
         GameUtil.setMoveSpeed(var1x, 0.0, 90.0F);
         var1x.setY(mc.thePlayer.motionY = 0.0);
      }
   };
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      long var2 = d ^ 2368323770163L;
      long var4 = var2 ^ 121686549348941L;
      this.Q = false;
      if (mc.thePlayer.fallDistance > (this.voidOnly.getValue() ? 3.0 : this.height.getValue().intValue())
         && !client.getModuleManager().get(Flight.class).isEnabled()
         && !client.getModuleManager().get(LongJump.class).isEnabled()
         && (!this.voidOnly.getValue() || !NoFall.P_boolean())
         && !mc.thePlayer.onGround
         && !mc.thePlayer.isCollidedVertically) {
         if (this.mode.getValue() == AntiFallMode.SET_BACK) {
            if (this.C != null) {
               mc.thePlayer.setPosition(this.C.xCoord, this.C.yCoord, this.C.zCoord);
            }
         } else if (this.mode.getValue() != AntiFallMode.LAG) {
            if (this.C != null) {
               var1x.setMinY(var1x.getMinY() + 3.0);
            } else if (this.y.hasElapsed(1000.0F)) {
               var1x.setZ(var1x.getZ() + 25.0);
               var1x.setOnGround(false);
               mc.thePlayer.fallDistance = 0.0F;
               this.y.reset();
            }
         }
      }
   };
   private static final long d = 101141971855565L;

   public AntiFall(Category var1) {
      super(var1);
      this.addSettings(this.mode, this.height, this.voidOnly);
   }

   private boolean h() {
      long var1 = d ^ 96169894116253L;
      long var3 = var1 ^ 65955614224099L;
      double var6 = mc.thePlayer.posX - mc.thePlayer.prevPosX;
      double var8 = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
      double var10 = MathHelper.sqrt_double(var6 * var6 + var8 * var8);
      double var12 = var10 / 0.05F;

      for (double var14 = 0.0; var14 < var12 - 1.0; var14 += 0.2) {
         double var16 = -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw));
         double var18 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw));
         long var10000 = 7776788986887341479L ^ var1;
         double var20 = mc.thePlayer.movementInput.moveForward * var14 * var16 + mc.thePlayer.movementInput.moveStrafe * var14 * var18;
         double var22 = mc.thePlayer.movementInput.moveForward * var14 * var18 - mc.thePlayer.movementInput.moveStrafe * var14 * var16;

         for (int var24 = -7; var24 < 0; var24++) {
            Block var25 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + var20, mc.thePlayer.posY + var24, mc.thePlayer.posZ + var22)).getBlock();
            Block var26 = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + var20, mc.thePlayer.posY + var24, mc.thePlayer.posZ + var22)).getBlock();
            if (var25 != Blocks.air || var26 != Blocks.air) {
               return false;
            }
         }
      }

      return NoFall.u_boolean()
         && !mc.thePlayer.isCollidedVertically
         && !mc.thePlayer.onGround
         && mc.thePlayer.fallDistance > 0.0F
         && GameUtil.isOnHypixel()
         && this.C != null
         && !client.getModuleManager().get(Flight.class).isEnabled()
         && !client.getModuleManager().get(LongJump.class).isEnabled();
   }

   @Override
   public void onEnable() {
      this.C = null;
      super.onEnable();
   }

   @Override
   public void p() {
      this.setDisplayName("Anti Fall " + EnumChatFormatting.GRAY + AntiFallMode.M(this.mode.getValue()));
      super.p();
   }
}
